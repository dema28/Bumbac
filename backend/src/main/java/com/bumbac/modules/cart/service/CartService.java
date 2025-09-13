package com.bumbac.modules.cart.service;

import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.auth.repository.UserRepository;
import com.bumbac.modules.auth.security.JwtService;
import com.bumbac.modules.cart.dto.AddToCartRequest;
import com.bumbac.modules.cart.dto.CartItemResponse;
import com.bumbac.modules.cart.dto.CartResponse;
import com.bumbac.modules.cart.dto.UpdateCartRequest;
import com.bumbac.modules.cart.entity.Cart;
import com.bumbac.modules.cart.entity.CartItem;
import com.bumbac.modules.cart.entity.Color;
import com.bumbac.modules.cart.repository.CartItemRepository;
import com.bumbac.modules.cart.repository.CartRepository;
import com.bumbac.modules.cart.repository.ColorRepository;
import com.bumbac.modules.cart.entity.CartItemId;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ColorRepository colorRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    // Метрики
    private final Counter itemsAddedCounter;
    private final Counter itemsUpdatedCounter;
    private final Counter itemsRemovedCounter;
    private final Counter cartsViewedCounter;
    private final Counter cartsClearedCounter;
    private final Counter cartErrorsCounter;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       ColorRepository colorRepository, UserRepository userRepository,
                       JwtService jwtService, MeterRegistry meterRegistry) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.colorRepository = colorRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;

        // Инициализация метрик
        this.itemsAddedCounter = Counter.builder("cart.items.added")
                .description("Количество добавленных товаров в корзину")
                .register(meterRegistry);

        this.itemsUpdatedCounter = Counter.builder("cart.items.updated")
                .description("Количество обновленных товаров в корзине")
                .register(meterRegistry);

        this.itemsRemovedCounter = Counter.builder("cart.items.removed")
                .description("Количество удаленных товаров из корзины")
                .register(meterRegistry);

        this.cartsViewedCounter = Counter.builder("cart.views")
                .description("Количество просмотров корзины")
                .register(meterRegistry);

        this.cartsClearedCounter = Counter.builder("cart.cleared")
                .description("Количество очисток корзины")
                .register(meterRegistry);

        this.cartErrorsCounter = Counter.builder("cart.errors")
                .description("Количество ошибок в операциях с корзиной")
                .register(meterRegistry);

        log.info("CartService метрики инициализированы");
    }

    @Transactional
    public CartResponse addItem(HttpServletRequest request, AddToCartRequest dto) {
        log.info("Начало добавления товара в корзину: colorId={}, quantity={}", dto.getColorId(), dto.getQuantity());

        try {
            User user = getCurrentUser(request);
            Color color = getColorAndValidateStock(dto.getColorId(), dto.getQuantity());
            Cart cart = getOrCreateCart(user);
            CartItem item = createOrUpdateCartItem(cart, color, dto.getQuantity());

            cartItemRepository.save(item);
            itemsAddedCounter.increment();

            log.info("Товар успешно добавлен в корзину: userId={}, colorId={}, quantity={}",
                    user.getId(), dto.getColorId(), dto.getQuantity());

            return buildCartResponse(cart);

        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Неожиданная ошибка при добавлении товара в корзину: colorId={}, quantity={}, error={}",
                    dto.getColorId(), dto.getQuantity(), ex.getMessage(), ex);
            cartErrorsCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add item to cart");
        }
    }


    @Transactional
    public CartResponse updateItem(HttpServletRequest request, UpdateCartRequest dto) {
        log.info("Начало обновления товара в корзине: colorId={}, quantity={}", dto.getColorId(), dto.getQuantity());

        try {
            User user = getCurrentUser(request);
            Cart cart = getUserCart(user);
            CartItem item = getCartItem(cart, dto.getColorId());

            if (dto.getQuantity() == 0) {
                removeItemFromCart(item, user.getId(), dto.getColorId());
            } else {
                updateItemQuantity(item, dto.getQuantity(), dto.getColorId());
            }

            updateCartTimestamp(cart);

            return buildCartResponse(cart);

        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Неожиданная ошибка при обновлении товара в корзине: colorId={}, quantity={}, error={}",
                    dto.getColorId(), dto.getQuantity(), ex.getMessage(), ex);
            cartErrorsCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update cart item");
        }
    }


    @Transactional(readOnly = true)
    public CartResponse getItems(HttpServletRequest request) {
        log.debug("Получение содержимого корзины");

        try {
            User user = getCurrentUser(request);
            Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);

            if (cart == null) {
                log.info("Корзина не найдена для пользователя {}, возвращаем пустой CartResponse", user.getEmail());
                cartsViewedCounter.increment();
                return new CartResponse(user.getId(), List.of());
            }

            List<CartItemResponse> items = cartItemRepository.findByCart(cart)
                    .stream()
                    .map(item -> {
                        Color color = item.getColor();

                        CartItemResponse dto = new CartItemResponse();
                        dto.setColorId(color.getId());
                        dto.setColorCode(color.getColorCode());
                        dto.setColorName(color.getColorName());
                        dto.setSku(color.getSku());
                        dto.setHexValue(color.getHexValue());
                        dto.setQuantity(item.getQuantity());
                        dto.setUnitPrice(color.getPrice());
                        dto.setTotalPrice(color.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                        dto.setStockQuantity(color.getStockQuantity());
                        dto.setAddedAt(item.getAddedAt());

                        if (color.getYarn() != null) {
                            dto.setYarnId(color.getYarn().getId());
                            dto.setYarnName(color.getYarn().getName());
                        }

                        return dto;
                    })
                    .toList();

            cartsViewedCounter.increment();
            log.info("Получено содержимое корзины: userId={}, itemsCount={}", user.getId(), items.size());

            return new CartResponse(user.getId(), items);

        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Неожиданная ошибка при получении корзины: error={}", ex.getMessage(), ex);
            cartErrorsCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get cart items");
        }
    }

    @Transactional
    public void clearCart(HttpServletRequest request) {
        log.info("Начало очистки корзины");

        try {
            User user = getCurrentUser(request);
            Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);

            if (cart == null) {
                log.info("Корзина не найдена для пользователя {}, нечего очищать", user.getEmail());
                return;
            }

            List<CartItem> items = cartItemRepository.findByCart(cart);
            int itemCount = items.size();

            cartItemRepository.deleteByCart(cart);
            updateCartTimestamp(cart);

            cartsClearedCounter.increment();
            log.info("Корзина успешно очищена: userId={}, removedItems={}", user.getId(), itemCount);

        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Неожиданная ошибка при очистке корзины: error={}", ex.getMessage(), ex);
            cartErrorsCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to clear cart");
        }
    }

    // Приватные методы-помощники

    private User getCurrentUser(HttpServletRequest request) {
        String email = jwtService.extractUsernameFromHeader(request);
        if (email == null) {
            log.warn("Попытка работы с корзиной без валидного JWT токена");
            cartErrorsCounter.increment();
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing authentication token");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Пользователь с email {} не найден", email);
                    cartErrorsCounter.increment();
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
                });
    }

    private Color getColorAndValidateStock(Long colorId, int requestedQuantity) {
        Color color = colorRepository.findById(colorId)
                .orElseThrow(() -> {
                    log.warn("Попытка добавить несуществующий товар: colorId={}", colorId);
                    cartErrorsCounter.increment();
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product color not found");
                });

        if (color.getStockQuantity() == null || color.getStockQuantity() < requestedQuantity) {
            log.warn("Недостаточно товара на складе: colorId={}, requested={}, available={}",
                    colorId, requestedQuantity, color.getStockQuantity());
            cartErrorsCounter.increment();
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Insufficient stock. Available: " + (color.getStockQuantity() != null ? color.getStockQuantity() : 0));
        }

        return color;
    }

    private Cart getOrCreateCart(User user) {
        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);

        if (cart == null) {
            cart = new Cart();
            cart.setUserId(user.getId());
            cart.setCreatedAt(LocalDateTime.now());
            cart.setUpdatedAt(LocalDateTime.now());
            cart = cartRepository.save(cart);
            log.info("Создана новая корзина для пользователя {} с ID: {}", user.getEmail(), cart.getId());
        } else {
            cart.setUpdatedAt(LocalDateTime.now());
            cart = cartRepository.save(cart);
            log.debug("Обновлена существующая корзина ID: {}", cart.getId());
        }

        return cart;
    }

    private Cart getUserCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    log.warn("Корзина не найдена для пользователя: {}", user.getEmail());
                    cartErrorsCounter.increment();
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
                });
    }

    private CartItem getCartItem(Cart cart, Long colorId) {
        CartItemId id = new CartItemId(cart.getId(), colorId);
        return cartItemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Товар не найден в корзине: cartId={}, colorId={}", cart.getId(), colorId);
                    cartErrorsCounter.increment();
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found in cart");
                });
    }

    private CartItem createOrUpdateCartItem(Cart cart, Color color, int quantity) {
        CartItemId id = new CartItemId(cart.getId(), color.getId());

        return cartItemRepository.findById(id)
                .map(existing -> updateExistingItem(existing, quantity, color))
                .orElseGet(() -> createNewItem(id, cart, color, quantity));
    }

    private CartItem updateExistingItem(CartItem existing, int additionalQuantity, Color color) {
        int newQuantity = existing.getQuantity() + additionalQuantity;

        if (color.getStockQuantity() < newQuantity) {
            log.warn("Недостаточно товара для увеличения количества: colorId={}, current={}, adding={}, available={}",
                    color.getId(), existing.getQuantity(), additionalQuantity, color.getStockQuantity());
            cartErrorsCounter.increment();
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Insufficient stock for total quantity. Available: " + color.getStockQuantity());
        }

        existing.setQuantity(newQuantity);
        log.debug("Обновлено количество существующего товара в корзине: colorId={}, oldQuantity={}, newQuantity={}",
                color.getId(), existing.getQuantity() - additionalQuantity, newQuantity);

        return existing;
    }

    private CartItem createNewItem(CartItemId id, Cart cart, Color color, Integer quantity) {
        CartItem newItem = new CartItem(cart, color, quantity, LocalDateTime.now());
        newItem.setId(id); // используем переданный id, а не создаем новый
        log.debug("Создан новый товар в корзине: colorId={}, quantity={}", color.getId(), quantity);
        return newItem;
    }

    private void removeItemFromCart(CartItem item, Long userId, Long colorId) {
        cartItemRepository.delete(item);
        itemsRemovedCounter.increment();
        log.info("Товар удален из корзины: userId={}, colorId={}", userId, colorId);
    }

    private void updateItemQuantity(CartItem item, int newQuantity, Long colorId) {
        Color color = colorRepository.findById(colorId)
                .orElseThrow(() -> {
                    log.error("Цвет товара не найден при обновлении: colorId={}", colorId);
                    cartErrorsCounter.increment();
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product color not found");
                });

        if (color.getStockQuantity() == null || color.getStockQuantity() < newQuantity) {
            log.warn("Недостаточно товара для обновления количества: colorId={}, requested={}, available={}",
                    colorId, newQuantity, color.getStockQuantity());
            cartErrorsCounter.increment();
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Insufficient stock. Available: " + (color.getStockQuantity() != null ? color.getStockQuantity() : 0));
        }

        int oldQuantity = item.getQuantity();
        item.setQuantity(newQuantity);
        cartItemRepository.save(item);
        itemsUpdatedCounter.increment();

        log.info("Количество товара в корзине обновлено: colorId={}, oldQuantity={}, newQuantity={}",
                colorId, oldQuantity, newQuantity);
    }

    private void updateCartTimestamp(Cart cart) {
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItemResponse> items = cartItemRepository.findByCart(cart)
                .stream()
                .map(item -> {
                    Color color = item.getColor();

                    CartItemResponse dto = new CartItemResponse();
                    dto.setColorId(color.getId());
                    dto.setColorCode(color.getColorCode());
                    dto.setColorName(color.getColorName());
                    dto.setSku(color.getSku());
                    dto.setHexValue(color.getHexValue());
                    dto.setQuantity(item.getQuantity());
                    dto.setUnitPrice(color.getPrice());
                    dto.setTotalPrice(color.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    dto.setStockQuantity(color.getStockQuantity());
                    dto.setAddedAt(item.getAddedAt());

                    if (color.getYarn() != null) {
                        dto.setYarnId(color.getYarn().getId());
                        dto.setYarnName(color.getYarn().getName());
                    }

                    return dto;
                })
                .toList();

        return new CartResponse(cart.getUserId(), items);
    }

}