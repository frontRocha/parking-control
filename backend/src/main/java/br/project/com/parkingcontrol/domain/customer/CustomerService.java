package br.project.com.parkingcontrol.domain.customer;

import br.project.com.parkingcontrol.util.BusinessException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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

    public void existsByCustomerNameAndIdNot(String customerName, String customerLastName, UUID id, Integer userId) throws BusinessException {
        if (customerRepository.existsByNameAndLastNameAndIdNotAndUserId(customerName, customerLastName, id, userId)) {
            throw new BusinessException("The customer: " + customerName + " " + customerLastName + " is already registered for the user");
        }
    }

    public void existsByPlateCarAndUserId(String plateCar, UUID id, Integer userId) throws BusinessException {
        if (customerRepository.existsByPlateCarAndIdNotAndUserId(plateCar, id, userId)) {
            throw new BusinessException("The plate: " + plateCar + " is already registered for the user");
        }
    }
}
