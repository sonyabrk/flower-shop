package flowershop.service;

import flowershop.exception.BusinessException;
import flowershop.exception.EntityNotFoundException;
import flowershop.model.Bouquet;
import flowershop.model.BouquetOrder;
import flowershop.model.OrderStatus;
import flowershop.repository.BouquetRepository;
import flowershop.repository.CustomerRepository;
import flowershop.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final BouquetRepository bouquetRepository;

    // Бизнес-правило: разрешённые переходы статусов
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.NEW, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, Set.of(OrderStatus.IN_DELIVERY, OrderStatus.CANCELLED),
            OrderStatus.IN_DELIVERY, Set.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED),
            OrderStatus.DELIVERED, Set.of(),
            OrderStatus.CANCELLED, Set.of()
    );

    public OrderService(OrderRepository orderRepository,
                         CustomerRepository customerRepository,
                         BouquetRepository bouquetRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.bouquetRepository = bouquetRepository;
    }

    public BouquetOrder create(BouquetOrder order) {
        // Бизнес-правило: клиент и букет должны существовать
        customerRepository.findById(order.getCustomerId())
                .orElseThrow(() -> new BusinessException("Клиент с ID " + order.getCustomerId() + " не найден"));

        Bouquet bouquet = bouquetRepository.findById(order.getBouquetId())
                .orElseThrow(() -> new BusinessException("Букет с ID " + order.getBouquetId() + " не найден"));

        validateBase(order);

        order.setTotalPrice(bouquet.getPrice().multiply(BigDecimal.valueOf(order.getQuantity())));
        order.setStatus(OrderStatus.NEW);
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDate.now());
        }

        return orderRepository.save(order);
    }

    public BouquetOrder getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Заказ с ID " + id + " не найден"));
    }

    public List<BouquetOrder> getAll() {
        return orderRepository.findAll();
    }

    public BouquetOrder update(Long id, BouquetOrder updated) {
        BouquetOrder existing = getById(id);
        validateBase(updated);

        Bouquet bouquet = bouquetRepository.findById(updated.getBouquetId())
                .orElseThrow(() -> new BusinessException("Букет с ID " + updated.getBouquetId() + " не найден"));

        existing.setQuantity(updated.getQuantity());
        existing.setDeliveryDate(updated.getDeliveryDate());
        existing.setTotalPrice(bouquet.getPrice().multiply(BigDecimal.valueOf(updated.getQuantity())));

        return orderRepository.update(existing);
    }

    // Бизнес-правило: переход статуса только по разрешённой схеме
    public BouquetOrder changeStatus(Long id, OrderStatus newStatus) {
        BouquetOrder order = getById(id);

        Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.get(order.getStatus());
        if (!allowed.contains(newStatus)) {
            throw new BusinessException(
                    "Недопустимый переход статуса: " + order.getStatus() + " -> " + newStatus);
        }

        order.setStatus(newStatus);
        return orderRepository.update(order);
    }

    public void delete(Long id) {
        getById(id);
        orderRepository.deleteById(id);
    }

    // Поиск

    public List<BouquetOrder> searchByCustomerName(String query) {
        String lower = query.toLowerCase().trim();
        return orderRepository.findAll().stream()
                .filter(o -> {
                    var customer = customerRepository.findById(o.getCustomerId()).orElse(null);
                    return customer != null && customer.getFullName().toLowerCase().contains(lower);
                })
                .toList();
    }

    public List<BouquetOrder> searchByBouquetName(String query) {
        String lower = query.toLowerCase().trim();
        return orderRepository.findAll().stream()
                .filter(o -> {
                    var bouquet = bouquetRepository.findById(o.getBouquetId()).orElse(null);
                    return bouquet != null && bouquet.getName().toLowerCase().contains(lower);
                })
                .toList();
    }

    // Фильтрация

    public List<BouquetOrder> filterByStatus(OrderStatus status) {
        return orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == status)
                .toList();
    }

    public List<BouquetOrder> filterByDateRange(LocalDate from, LocalDate to) {
        return orderRepository.findAll().stream()
                .filter(o -> !o.getOrderDate().isBefore(from) && !o.getOrderDate().isAfter(to))
                .toList();
    }

    // Сортировка 

    public List<BouquetOrder> sortByDate(boolean ascending) {
        Comparator<BouquetOrder> cmp = Comparator.comparing(BouquetOrder::getOrderDate);
        return orderRepository.findAll().stream()
                .sorted(ascending ? cmp : cmp.reversed())
                .toList();
    }

    public List<BouquetOrder> sortByTotalPrice(boolean ascending) {
        Comparator<BouquetOrder> cmp = Comparator.comparing(BouquetOrder::getTotalPrice);
        return orderRepository.findAll().stream()
                .sorted(ascending ? cmp : cmp.reversed())
                .toList();
    }

    // Статистика 

    public Map<OrderStatus, Long> countByStatus() {
        Map<OrderStatus, Long> result = new EnumMap<>(OrderStatus.class);
        for (OrderStatus status : OrderStatus.values()) {
            result.put(status, orderRepository.findAll().stream()
                    .filter(o -> o.getStatus() == status)
                    .count());
        }
        return result;
    }

    public BigDecimal totalRevenue() {
        return orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(BouquetOrder::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Валидация
     
    private void validateBase(BouquetOrder order) {
        if (order.getQuantity() <= 0) {
            throw new BusinessException("Количество букетов должно быть положительным");
        }
        if (order.getDeliveryDate() != null && order.getOrderDate() != null
                && order.getDeliveryDate().isBefore(order.getOrderDate())) {
            throw new BusinessException("Дата доставки не может быть раньше даты заказа");
        }
    }
}
