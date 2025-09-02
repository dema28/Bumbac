package com.bumbac.modules.catalog.service;

import com.bumbac.modules.catalog.dto.YarnRequest;
import com.bumbac.modules.catalog.dto.YarnResponse;
import com.bumbac.modules.catalog.entity.Brand;
import com.bumbac.modules.catalog.entity.Category;
import com.bumbac.modules.catalog.entity.Collection;
import com.bumbac.modules.catalog.entity.Yarn;
import com.bumbac.modules.catalog.mapper.YarnMapper;
import com.bumbac.modules.catalog.repository.BrandRepository;
import com.bumbac.modules.catalog.repository.CategoryRepository;
import com.bumbac.modules.catalog.repository.CollectionRepository;
import com.bumbac.modules.catalog.repository.YarnRepository;
import com.bumbac.modules.catalog.specification.YarnSpecification;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
public class YarnService {

    private final YarnRepository yarnRepository;
    private final YarnMapper yarnMapper;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final CollectionRepository collectionRepository;
    private final MeterRegistry meterRegistry;

    // Метрики для отслеживания операций
    private final Counter yarnCreatedCounter;
    private final Counter yarnUpdatedCounter;
    private final Counter yarnDeletedCounter;
    private final Counter yarnViewedCounter;
    private final Counter yarnFilteredCounter;
    private final Counter yarnErrorCounter;

    public YarnService(YarnRepository yarnRepository,
                       YarnMapper yarnMapper,
                       BrandRepository brandRepository,
                       CategoryRepository categoryRepository,
                       CollectionRepository collectionRepository,
                       MeterRegistry meterRegistry) {
        this.yarnRepository = yarnRepository;
        this.yarnMapper = yarnMapper;
        this.brandRepository = brandRepository;
        this.collectionRepository = collectionRepository;
        this.categoryRepository = categoryRepository;
        this.meterRegistry = meterRegistry;

        // Инициализация метрик
        this.yarnCreatedCounter = Counter.builder("yarn.operations.created")
                .description("Количество созданных пряж")
                .register(meterRegistry);
        this.yarnUpdatedCounter = Counter.builder("yarn.operations.updated")
                .description("Количество обновленных пряж")
                .register(meterRegistry);
        this.yarnDeletedCounter = Counter.builder("yarn.operations.deleted")
                .description("Количество удаленных пряж")
                .register(meterRegistry);
        this.yarnViewedCounter = Counter.builder("yarn.operations.viewed")
                .description("Количество просмотров пряж")
                .register(meterRegistry);
        this.yarnFilteredCounter = Counter.builder("yarn.operations.filtered")
                .description("Количество фильтраций пряж")
                .register(meterRegistry);
        this.yarnErrorCounter = Counter.builder("yarn.operations.errors")
                .description("Количество ошибок при операциях с пряжей")
                .register(meterRegistry);
    }

    @Transactional
    public YarnResponse create(YarnRequest request) {
        log.info("Создание новой пряжи: {}", request.getName());

        try {
            // Проверка существования связанных сущностей
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> {
                        log.warn("Бренд не найден при создании пряжи: brandId={}", request.getBrandId());
                        yarnErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Бренд не найден");
                    });

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> {
                        log.warn("Категория не найдена при создании пряжи: categoryId={}", request.getCategoryId());
                        yarnErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Категория не найдена");
                    });

            Collection collection = collectionRepository.findById(request.getCollectionId())
                    .orElseThrow(() -> {
                        log.warn("Коллекция не найдена при создании пряжи: collectionId={}", request.getCollectionId());
                        yarnErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Коллекция не найдена");
                    });

            // Проверка уникальности имени в рамках коллекции (если метод существует)
            // if (yarnRepository.existsByNameAndCollection(request.getName(), collection)) {
            //   log.warn("Пряжа с таким именем уже существует в коллекции: name={}, collectionId={}",
            //       request.getName(), request.getCollectionId());
            //   yarnErrorCounter.increment();
            //   throw new ResponseStatusException(HttpStatus.CONFLICT,
            //       "Пряжа с таким именем уже существует в данной коллекции");
            // }

            Yarn yarn = yarnMapper.toEntity(request);
            yarn.setBrand(brand);
            yarn.setCategory(category);
            yarn.setCollection(collection);
            yarn.setCreatedAt(LocalDateTime.now());

            Yarn savedYarn = yarnRepository.save(yarn);
            YarnResponse response = yarnMapper.toResponse(savedYarn);

            log.info("Пряжа успешно создана: id={}, name={}", savedYarn.getId(), savedYarn.getName());
            yarnCreatedCounter.increment();

            return response;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при создании пряжи: {}", e.getMessage(), e);
            yarnErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при создании пряжи");
        }
    }

    @Transactional(readOnly = true)
    public List<YarnResponse> getAll() {
        log.debug("Получение всех пряж");

        try {
            List<Yarn> yarns = yarnRepository.findAll();
            List<YarnResponse> responses = yarns.stream()
                    .map(yarnMapper::toResponse)
                    .toList();

            log.debug("Получено {} пряж", responses.size());
            yarnViewedCounter.increment();

            return responses;

        } catch (Exception e) {
            log.error("Ошибка при получении всех пряж: {}", e.getMessage(), e);
            yarnErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при получении пряж");
        }
    }

    @Transactional(readOnly = true)
    public YarnResponse getById(Long id) {
        log.debug("Получение пряжи по ID: {}", id);

        try {
            Yarn yarn = yarnRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Пряжа не найдена: id={}", id);
                        yarnErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Пряжа не найдена");
                    });

            YarnResponse response = yarnMapper.toResponse(yarn);

            log.debug("Пряжа найдена: id={}, name={}", id, yarn.getName());
            yarnViewedCounter.increment();

            return response;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при получении пряжи по ID {}: {}", id, e.getMessage(), e);
            yarnErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при получении пряжи");
        }
    }

    @Transactional
    public YarnResponse update(Long id, YarnRequest request) {
        log.info("Обновление пряжи: id={}, name={}", id, request.getName());

        try {
            Yarn existingYarn = yarnRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Пряжа не найдена для обновления: id={}", id);
                        yarnErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Пряжа не найдена");
                    });

            // Проверка существования связанных сущностей
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> {
                        log.warn("Бренд не найден при обновлении пряжи: brandId={}", request.getBrandId());
                        yarnErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Бренд не найден");
                    });

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> {
                        log.warn("Категория не найдена при обновлении пряжи: categoryId={}", request.getCategoryId());
                        yarnErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Категория не найдена");
                    });

            Collection collection = collectionRepository.findById(request.getCollectionId())
                    .orElseThrow(() -> {
                        log.warn("Коллекция не найдена при обновлении пряжи: collectionId={}", request.getCollectionId());
                        yarnErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Коллекция не найдена");
                    });

            // Обновление полей
            existingYarn.setName(request.getName());
            existingYarn.setMaterial(request.getMaterial());
            existingYarn.setWeight(request.getWeight());
            existingYarn.setLength(request.getLength());
            existingYarn.setPriceMDL(request.getPriceMDL());
            existingYarn.setPriceUSD(request.getPriceUSD());
            existingYarn.setBrand(brand);
            existingYarn.setCategory(category);
            existingYarn.setCollection(collection);

            Yarn updatedYarn = yarnRepository.save(existingYarn);
            YarnResponse response = yarnMapper.toResponse(updatedYarn);

            log.info("Пряжа успешно обновлена: id={}, name={}", id, updatedYarn.getName());
            yarnUpdatedCounter.increment();

            return response;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при обновлении пряжи {}: {}", id, e.getMessage(), e);
            yarnErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при обновлении пряжи");
        }
    }

    @Transactional
    public void delete(Long id) {
        log.info("Удаление пряжи: id={}", id);

        try {
            if (!yarnRepository.existsById(id)) {
                log.warn("Пряжа не найдена для удаления: id={}", id);
                yarnErrorCounter.increment();
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пряжа не найдена");
            }

            yarnRepository.deleteById(id);

            log.info("Пряжа успешно удалена: id={}", id);
            yarnDeletedCounter.increment();

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при удалении пряжи {}: {}", id, e.getMessage(), e);
            yarnErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при удалении пряжи");
        }
    }

    @Transactional(readOnly = true)
    public List<YarnResponse> filter(String category, String brand, String material) {
        log.debug("Фильтрация пряж: category={}, brand={}, material={}", category, brand, material);

        try {
            Specification<Yarn> spec = YarnSpecification.filterBy(category, brand, material);
            List<Yarn> yarns = yarnRepository.findAll(spec);

            List<YarnResponse> responses = yarns.stream()
                    .map(yarnMapper::toResponse)
                    .toList();

            log.debug("Найдено {} пряж по фильтру", responses.size());
            yarnFilteredCounter.increment();

            return responses;

        } catch (Exception e) {
            log.error("Ошибка при фильтрации пряж: {}", e.getMessage(), e);
            yarnErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при фильтрации пряж");
        }
    }
}