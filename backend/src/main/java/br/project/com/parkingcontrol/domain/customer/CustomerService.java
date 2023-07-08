package br.project.com.parkingcontrol.domain.customer;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(Customer customerModel) {
        return customerRepository.save(customerModel);
    }

    @Transactional
    public void deleteCustomer(UUID id) {
        customerRepository.deleteById(id);
    }

    public Optional<Customer> findById(UUID id) {
        return customerRepository.findById(id);
    }
}
