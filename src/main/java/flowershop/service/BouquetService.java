package flowershop.service;

import flowershop.exception.BusinessException;
import flowershop.exception.EntityNotFoundException;
import flowershop.model.Bouquet;
import flowershop.model.OrderStatus;
import flowershop.repository.BouquetRepository;
import flowershop.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

public class BouquetService {

    private final BouquetRepository bouquetRepository;
    private final OrderRepository orderRepository;

    public BouquetService(BouquetRepository bouquetRepository, OrderRepository orderRepository) {
        this.bouquetRepository = bouquetRepository;
        this.orderRepository = orderRepository;
    }

    public Bouquet create(Bouquet bouquet) {
        validate(bouquet);
        return bouquetRepository.save(bouquet);
    }

    public Bouquet getById(Long id) {
        return bouquetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Букет с ID " + id + " не найден"));
    }

    public List<Bouquet> getAll() {
        return bouquetRepository.findAll();
    }

    public Bouquet update(Long id, Bouquet updated) {
        Bouquet existing = getById(id);
        validate(updated);

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());

        return bouquetRepository.update(existing);
    }

    // Бизнес-правило: нельзя удалить букет, если по нему есть активные заказы
    public void delete(Long id) {
        getById(id);

        boolean hasActiveOrders = orderRepository.findByBouquetId(id).stream()
                .anyMatch(o -> o.getStatus() != OrderStatus.DELIVERED
                        && o.getStatus() != OrderStatus.CANCELLED);

        if (hasActiveOrders) {
            throw new BusinessException(
                    "Нельзя удалить букет: по нему есть незавершённые заказы");
        }

        bouquetRepository.deleteById(id);
    }

    public List<Bouquet> search(String query) {
        String lower = query.toLowerCase().trim();
        return bouquetRepository.findAll().stream()
                .filter(b -> b.getName().toLowerCase().contains(lower))
                .toList();
    }

    public List<Bouquet> filterByPriceRange(BigDecimal min, BigDecimal max) {
        return bouquetRepository.findAll().stream()
                .filter(b -> b.getPrice().compareTo(min) >= 0 && b.getPrice().compareTo(max) <= 0)
                .toList();
    }

    // Бизнес-правило: обязательное название и корректная цена
    private void validate(Bouquet bouquet) {
        if (bouquet.getName() == null || bouquet.getName().isBlank()) {
            throw new BusinessException("Название букета обязательно для заполнения");
        }
        if (bouquet.getPrice() == null || bouquet.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Цена букета должна быть положительной");
        }
    }
}