package br.project.com.parkingcontrol.domain.vacancie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VacancieRepository extends JpaRepository<Vacancie, UUID> {
    int countByUserId(Integer userId);

    void deleteAllByBlock_Id(UUID blockId);

    Optional<Vacancie> findById(UUID id);

    List<Vacancie> findAllByUserIdOrderByVacancieNumberAsc(Integer userId);
}
