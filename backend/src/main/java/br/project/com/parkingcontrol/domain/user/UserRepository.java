package br.project.com.parkingcontrol.domain.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByLogin(String email);

    boolean existsByLogin(String email);
}