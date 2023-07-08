package br.project.com.parkingcontrol.domain.allocation;

import br.project.com.parkingcontrol.businessException.BusinessException;
import br.project.com.parkingcontrol.domain.vacancie.Vacancie;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AllocationService {
    private final AllocationRepository allocationRepository;

    public AllocationService(AllocationRepository allocationRepository) {
        this.allocationRepository = allocationRepository;
    }

    public List<Allocation> findAll(Integer userId) {
        return allocationRepository.findAllByUserId(userId);
    }

    public Optional<Allocation> findById(UUID id) {
        return allocationRepository.findById(id);
    }

    @Transactional
    public Allocation createAllocation(Allocation allocation) {
        return allocationRepository.save(allocation);
    }

    @Transactional
    public void deleteAllocation(UUID id) {
        allocationRepository.deleteById(id);
    }

    public void existsAllocation(Optional<Allocation> allocationOptional) throws BusinessException {
        if(!allocationOptional.isPresent()) {
            throw new BusinessException("This allocation does not exists");
        }
    }

    public void verifyRelationUserWithAllocation(Optional<Allocation> allocation, Integer userId) throws BusinessException {
        if(allocation.orElse(null).getUser().getId() != userId) {
            throw new BusinessException("This allocation does not exist");
        }
    }
}
