package br.project.com.parkingcontrol.domain.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByNameAndLastNameAndIdNotAndUserId(String name, String lastName, UUID id, Integer userId);

    boolean existsByPlateCarAndIdNotAndUserId(String plateCar, UUID id, Integer uid);
}
