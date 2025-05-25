package md.bumbac.api.model;

/**
 * Все возможные статусы заказа.
 */
public enum OrderStatus {
    CREATED,   // заказ создан, ожидает оплаты
    PAID,      // оплачен
    PACKING,   // собирается на складе
    SHIPPED,   // передан курьеру
    DONE       // доставлен
}

