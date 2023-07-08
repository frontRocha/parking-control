package br.project.com.parkingcontrol.web.history;

import br.project.com.parkingcontrol.businessException.BusinessException;
import br.project.com.parkingcontrol.domain.allocation.Allocation;
import br.project.com.parkingcontrol.domain.block.Block;
import br.project.com.parkingcontrol.domain.history.History;
import br.project.com.parkingcontrol.domain.history.HistoryService;
import br.project.com.parkingcontrol.util.TokenGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/history")
public class HistoryController {

    private final HistoryService historyService;
    private final TokenGenerator tokenGenerator;

    public HistoryController(HistoryService historyService, TokenGenerator tokenGenerator) {
        this.historyService = historyService;
        this.tokenGenerator = tokenGenerator;
    }

    @GetMapping
    public ResponseEntity<List<History>> getAllHistory(@RequestHeader("Authorization") String authorizationHeader) {
        String token = extractTokenFromAuthorizationHeader(authorizationHeader);
        Integer userId = extractUserIdFromToken(token);

        return ResponseEntity.status(HttpStatus.OK).body(historyService.findAll(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneHistory(@RequestHeader("Authorization") String authorizationHeader,
                                                @PathVariable(value = "id") UUID id) {
        try {
            String token = extractTokenFromAuthorizationHeader(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            Optional<History> historyOptional = getHistoryModel(id);
            validateExistsHistory(historyOptional);
            verifyRelationUserWithHistory(historyOptional, userId);

            return ResponseEntity.status(HttpStatus.OK).body(historyOptional.get());
        } catch(Exception err) {
            BusinessException businessException = new BusinessException(err.getMessage());
            return BusinessException.handleBusinessException(businessException, HttpStatus.CONFLICT.value());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteHistory(@RequestHeader("Authorization") String authorizationHeader,
                                                @PathVariable(value = "id") UUID id) {
        try {
            String token = extractTokenFromAuthorizationHeader(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            Optional<History> historyOptional = getHistoryModel(id);
            validateExistsHistory(historyOptional);
            verifyRelationUserWithHistory(historyOptional, userId);

            deleteHistoryData(id);

            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch(Exception err) {
            BusinessException businessException = new BusinessException(err.getMessage());
            return BusinessException.handleBusinessException(businessException, HttpStatus.CONFLICT.value());
        }
    }

    private String extractTokenFromAuthorizationHeader(String authorizationHeader) {
        return tokenGenerator.extractTokenFromAuthorizationHeader(authorizationHeader);
    }

    private Integer extractUserIdFromToken(String token) {
        return tokenGenerator.extractUserIdFromToken(token);
    }

    private void validateExistsHistory(Optional<History> history) throws BusinessException {
        historyService.existsHistory(history);
    }

    private void verifyRelationUserWithHistory(Optional<History> history,
                                               Integer userId) throws BusinessException {
        historyService.verifyRelationUserWithHistory(history, userId);
    }

    private Optional<History> getHistoryModel(UUID id) {
        return historyService.findById(id);
    }

    private void deleteHistoryData(UUID id) {
        historyService.deleteHistory(id);
    }
}
