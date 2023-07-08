package br.project.com.parkingcontrol.domain.history;

import br.project.com.parkingcontrol.domain.allocation.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HistoryRepository extends JpaRepository<History, UUID> {
    List<History> findAllByUserId(Integer userId);
}
