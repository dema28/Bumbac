package md.bumbac.api.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.bumbac.api.model.CartItem;
import md.bumbac.api.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEmailService {

    private final JavaMailSender mailSender;

    @Value("${warehouse.email:orders@bumbac.md}")
    private String warehouseEmail;

    /** отправить письмо с деталями заказа на склад */
    public void sendOrderToWarehouse(Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(warehouseEmail);
            // helper.setBcc("archive@bumbac.md");   // ← раскомментируйте при необходимости
            helper.setSubject("📦 Новый заказ #" + order.getId());

            /* ---------------- HTML-тело ---------------- */
            StringBuilder body = new StringBuilder();
            body.append("<h2>Новый заказ #").append(order.getId()).append("</h2>");
            body.append("<p><strong>Получатель:</strong> ").append(order.getRecipientName()).append("<br>");
            body.append("<strong>Контактное лицо:</strong> ").append(order.getContactPerson()).append("<br>");
            body.append("<strong>Телефон:</strong> ").append(order.getPhoneNumber()).append("<br>");
            body.append("<strong>Email:</strong> ").append(order.getEmail()).append("<br>");
            body.append("<strong>Адрес:</strong> ")
                    .append(order.getCountry()).append(", ")
                    .append(order.getRegion()).append(", ")
                    .append(order.getCity()).append(", ")
                    .append(order.getStreet()).append(", ")
                    .append(order.getBuildingNumber()).append(", ")
                    .append(order.getPostalCode()).append("</p>");

            body.append("<h3>Товары:</h3><ul>");
            for (CartItem item : order.getItems()) {
                body.append("<li>")
                        .append(item.getProduct().getName())
                        .append(" × ").append(item.getQuantity())
                        .append("</li>");
            }
            body.append("</ul>");

            body.append("<p><strong>Общая сумма:</strong> ")
                    .append(order.getTotal()).append(" MDL</p>");

            helper.setText(body.toString(), true);   // true → HTML

            mailSender.send(message);
            log.info("Письмо о заказе #{} отправлено на склад {}", order.getId(), warehouseEmail);

        } catch (MessagingException e) {
            log.error("Не удалось отправить письмо о заказе #{}", order.getId(), e);
        }
    }
}
