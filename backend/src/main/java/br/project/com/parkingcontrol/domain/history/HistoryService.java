package br.project.com.parkingcontrol.domain.history;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {
    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Transactional
    public void saveDataInHistory(History history) {
        historyRepository.save(history);
    }
}
