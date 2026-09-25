package flowershop.service;

import flowershop.exception.BusinessException;
import flowershop.exception.EntityNotFoundException;
import flowershop.model.Customer;
import flowershop.repository.CustomerRepository;

import java.util.List;

public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer create(Customer customer) {
        validate(customer);
        return customerRepository.save(customer);
    }

    public Customer getById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Клиент с ID " + id + " не найден"));
    }

    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    public Customer update(Long id, Customer updated) {
        Customer existing = getById(id); // бросит EntityNotFoundException, если не найден
        validate(updated);

        existing.setFullName(updated.getFullName());
        existing.setPhone(updated.getPhone());
        existing.setEmail(updated.getEmail());

        return customerRepository.update(existing);
    }

    public void delete(Long id) {
        getById(id); // проверка существования перед удалением
        customerRepository.deleteById(id);
    }

    public List<Customer> search(String query) {
        String lower = query.toLowerCase().trim();
        return customerRepository.findAll().stream()
                .filter(c -> c.getFullName().toLowerCase().contains(lower)
                        || c.getEmail().toLowerCase().contains(lower))
                .toList();
    }

    // Бизнес-правило: обязательные поля и корректный email
    private void validate(Customer customer) {
        if (customer.getFullName() == null || customer.getFullName().isBlank()) {
            throw new BusinessException("Имя клиента обязательно для заполнения");
        }
        if (customer.getEmail() == null || !customer.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new BusinessException("Некорректный email");
        }
        if (customer.getPhone() == null || customer.getPhone().isBlank()) {
            throw new BusinessException("Телефон клиента обязателен для заполнения");
        }
    }
}