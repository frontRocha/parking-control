package br.project.com.parkingcontrol.domain.history;

import br.project.com.parkingcontrol.businessException.BusinessException;
import br.project.com.parkingcontrol.domain.allocation.Allocation;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HistoryService {
    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public List<History> findAll(Integer userId) {
        return historyRepository.findAllByUserId(userId);
    }

    public Optional<History> findById(UUID id) {
        return historyRepository.findById(id);
    }

    @Transactional
    public void saveDataInHistory(History history) {
        historyRepository.save(history);
    }

    @Transactional
    public void deleteHistory(UUID id) {
        historyRepository.deleteById(id);
    }

    public void existsHistory(Optional<History> historyOptional) throws BusinessException {
        if(!historyOptional.isPresent()) {
            throw new BusinessException("This history does not exists");
        }
    }

    public void verifyRelationUserWithHistory(Optional<History> history, Integer userId) throws BusinessException {
        if(history.orElse(null).getUser().getId() != userId) {
            throw new BusinessException("This history does not exist");
        }
    }
}
