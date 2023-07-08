package br.project.com.parkingcontrol.domain.block;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BlockRepository extends JpaRepository<Block, UUID> {
    int countByBlockNameAndUserId(String blockName, Integer userId);

    int countByBlockNameAndIdNotAndUserId(String blockName, UUID id, Integer userId);

    void deleteById(UUID blockId);

    List<Block> findAllByUserId(Integer userId);
}
