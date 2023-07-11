package br.project.com.parkingcontrol.web.allocation;

import br.project.com.parkingcontrol.util.BusinessException;
import br.project.com.parkingcontrol.domain.allocation.Allocation;
import br.project.com.parkingcontrol.domain.allocation.AllocationFinishedResponse;
import br.project.com.parkingcontrol.domain.allocation.AllocationService;
import br.project.com.parkingcontrol.domain.block.BlockService;
import br.project.com.parkingcontrol.domain.customer.Customer;
import br.project.com.parkingcontrol.domain.customer.CustomerService;
import br.project.com.parkingcontrol.domain.history.History;
import br.project.com.parkingcontrol.domain.history.HistoryService;
import br.project.com.parkingcontrol.domain.user.User;
import br.project.com.parkingcontrol.domain.user.UserRepository;
import br.project.com.parkingcontrol.domain.user.UserServiceImpl;
import br.project.com.parkingcontrol.domain.vacancie.Vacancie;
import br.project.com.parkingcontrol.domain.vacancie.VacancieService;
import br.project.com.parkingcontrol.util.ResponseData;
import br.project.com.parkingcontrol.util.TokenGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/allocation")
public class AllocationController {
    private final VacancieService vacancieService;
    private final TokenGenerator tokenGenerator;
    private final BlockService blockService;
    private final UserRepository userRepository;
    private final AllocationService allocationService;
    private final CustomerService customerService;
    private final HistoryService historyService;
    private final UserServiceImpl userService;

    AllocationController(VacancieService vacancieService,
                         TokenGenerator tokenGenerator,
                         BlockService blockService,
                         UserRepository userRepository,
                         AllocationService allocationService,
                         CustomerService customerService,
                         HistoryService historyService,
                         UserServiceImpl userService) {
        this.vacancieService = vacancieService;
        this.tokenGenerator = tokenGenerator;
        this.blockService = blockService;
        this.userRepository = userRepository;
        this.allocationService = allocationService;
        this.customerService = customerService;
        this.historyService = historyService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ResponseData> getAllAllocation(@RequestHeader("Authorization") String authorizationHeader) {
        String token = extractTokenFromAuthorizationHeader(authorizationHeader);
        Integer userId = extractUserIdFromToken(token);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseData.generateSuccessfulResponse(allocationService.findAll(userId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getOneAllocation(@RequestHeader("Authorization") String authorizationHeader,
                                                   @PathVariable(value = "id") UUID id) {
        try {
            String token = extractTokenFromAuthorizationHeader(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            Optional<Allocation> allocationOptional = getAllocationModel(id);
            validateExistsAllocation(allocationOptional);
            verifyRelationUserWithAllocation(allocationOptional, userId);

            return ResponseEntity.status(HttpStatus.OK).body(ResponseData.generateSuccessfulResponse(allocationOptional.get()));
        } catch(Exception err) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseData.generateUnsuccessfulResponse(err.getMessage()));
        }
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<ResponseData> createAllocation(@RequestHeader("Authorization") String authorizationHeader,
                                                   @RequestBody Customer customer,
                                                   @PathVariable(value = "id") UUID id) {
        try {
            String token = extractTokenFromAuthorizationHeader(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            Optional<Vacancie> vacancie = getVacancyModel(id);
            validationExistsVacancie(vacancie);
            validationStatusVacancie(vacancie.get());

            User user = getUserModel(userId);
            verifyRelationUserWithVacancie(vacancie.get(), userId);

            Customer customerModel = createCustomerModel(customer, user);

            Customer savedCustomer = createCustomer(customerModel);
            Vacancie vacancieModel = createUpdatedVacancie(vacancie.get(), user);
            createVacancy(vacancieModel);

            Allocation allocationModel = createAllocationModel(savedCustomer, user, vacancie.get());

            return ResponseEntity.status(HttpStatus.OK).body(ResponseData.generateSuccessfulResponse(allocationService.createAllocation(allocationModel)));
        } catch(Exception err) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseData.generateUnsuccessfulResponse(err.getMessage()));
        }
    }

    @DeleteMapping("/finish/{id}")
    public ResponseEntity<ResponseData> deleteAllocation(@RequestHeader("Authorization") String authorizationHeader,
                                                   @PathVariable(value = "id") UUID id) {
        try {
            String token = extractTokenFromAuthorizationHeader(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            Optional<Allocation> allocation = getAllocationModel(id);
            validateExistsAllocation(allocation);

            User userModel = getUserModel(userId);
            verifyRelationUserWithAllocation(allocation, userId);

            Vacancie status = createUpdatedVacancieStatus(allocation.get().getVacancie(), userModel);

            AllocationFinishedResponse response = createFinishResponse(allocation.get());
            History historyModel = createHistoryModel(allocation.get(), userModel, response);

            createVacancy(status);
            deleteAllocation(id);
            deleteCustomer(allocation.get().getCustomer().getId());
            saveDataInHistory(historyModel);

            return ResponseEntity.status(HttpStatus.OK).body(ResponseData.generateSuccessfulResponse(response));
        } catch(BusinessException err) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseData.generateUnsuccessfulResponse(err.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData> editAllocation(@RequestHeader("Authorization") String authorizationHeader,
                                                 @RequestBody Allocation newAllocation,
                                                 @PathVariable(value = "id") UUID id) {
        try {
            String token = extractTokenFromAuthorizationHeader(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            Optional<Allocation> allocationOptional = getAllocationModel(id);
            validateExistsAllocation(allocationOptional);
            verifyRelationUserWithAllocation(allocationOptional, userId);

            existsByCustomerNameAndIdNot(newAllocation, allocationOptional, userId);
            existsByPlateCarAndUserId(newAllocation, allocationOptional, userId);

            Customer customerModel = createUpdateCustomerModel(newAllocation, allocationOptional);
            Customer updatedCustomerModel = saveUpdatedCustomer(customerModel);

            Allocation allocationModel = createUpdateAllocation(allocationOptional, updatedCustomerModel);
            Allocation updatedAllocationModel = saveUpdateAllocation(allocationModel);

            return ResponseEntity.status(HttpStatus.OK).body(ResponseData.generateSuccessfulResponse(updatedAllocationModel));
        } catch(Exception err) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseData.generateUnsuccessfulResponse(err.getMessage()));
        }
    }

    private String extractTokenFromAuthorizationHeader(String authorizationHeader) {
        return tokenGenerator.extractTokenFromAuthorizationHeader(authorizationHeader);
    }

    private Integer extractUserIdFromToken(String token) {
        return tokenGenerator.extractUserIdFromToken(token);
    }

    private Optional<Allocation> getAllocationModel(UUID id) {
        return allocationService.findById(id);
    }

    private Optional<Vacancie> getVacancyModel(UUID id) {
        return vacancieService.findById(id);
    }

    private User getUserModel(Integer userId) {
        return userService.getUserModel(userId);
    }

    private void validateExistsAllocation(Optional<Allocation> allocation) throws BusinessException {
        allocationService.existsAllocation(allocation);
    }

    private void validationExistsVacancie(Optional<Vacancie> vacancie) throws BusinessException {
        vacancieService.validationExistsVacancie(vacancie);
    }

    private void validationStatusVacancie(Vacancie vacancie) throws BusinessException {
        vacancieService.validateStatusVacancie(vacancie);
    }

    private void verifyRelationUserWithAllocation(Optional<Allocation> allocation,
                                                  Integer userId) {
        allocationService.verifyRelationUserWithAllocation(allocation, userId);
    }

    private void verifyRelationUserWithVacancie(Vacancie vacancie,
                                                Integer userId) {
        vacancieService.verifyRelationUserWithVacancy(userId, vacancie);
    }

    private void existsByCustomerNameAndIdNot(Allocation newAllocation,
                                              Optional<Allocation> allocationOptional,
                                              Integer userId) throws BusinessException {
        customerService.existsByCustomerNameAndIdNot(newAllocation.getCustomerName(), newAllocation.getCustomerLastName(), allocationOptional.get().getCustomer().getId(), userId);
    }

    private void existsByPlateCarAndUserId(Allocation newAllocation,
                                           Optional<Allocation> allocationOptional,
                                           Integer userId) throws BusinessException {
        customerService.existsByPlateCarAndUserId(newAllocation.getPlateCar(), allocationOptional.get().getCustomer().getId(), userId);
    }

    public void createVacancy(Vacancie status) {
        vacancieService.createVacancy(status);
    }

    public void deleteAllocation(UUID id) {
        allocationService.deleteAllocation(id);
    }

    public void deleteCustomer(UUID customerId) {
        customerService.deleteCustomer(customerId);
    }

    private void saveDataInHistory(History historyModel) {
        historyService.saveDataInHistory(historyModel);
    }

    private Customer saveUpdatedCustomer(Customer customerModel) {
        return customerService.createCustomer(customerModel);
    }

    private Allocation saveUpdateAllocation(Allocation allocationModel) {
        return allocationService.createAllocation(allocationModel);
    }

    private Customer createCustomer(Customer customerModel) {
        return customerService.createCustomer(customerModel);
    }

    private AllocationFinishedResponse createFinishResponse(Allocation allocation) {
        double hoursWithDecimals = calculateHoursWithDecimals(allocation.getArrivalTime());

        return new AllocationFinishedResponse.Builder()
                .setArrivalTime(allocation.getArrivalTime())
                .setDepartureTime(LocalDateTime.now(ZoneId.of("UTC")))
                .setTotal(calculateTotalValue(allocation.getUser().getPricePerHour(), hoursWithDecimals))
                .setName(allocation.getCustomerName())
                .setVacancieNumber(allocation.getVacancieName())
                .setBlockName(allocation.getBlockName())
                .build();
    }

    private double calculateHoursWithDecimals(LocalDateTime arrivalTime) {
        Duration duration = Duration.between(arrivalTime, LocalDateTime.now(ZoneId.of("UTC")));
        double seconds = duration.getSeconds();
        return seconds / 3600.0;
    }

    private double calculateTotalValue(double pricePerHour, double hoursWithDecimals) {
        return pricePerHour * hoursWithDecimals;
    }

    private History createHistoryModel(Allocation allocation,
                                       User user,
                                       AllocationFinishedResponse allocationFinishedResponse) {
        return new History.Builder()
                .setArrivalTime(allocation.getArrivalTime())
                .setDepartureTime(LocalDateTime.now(ZoneId.of("UTC")))
                .setCustomerName(allocation.getCustomer().getName())
                .setCustomerLastName(allocation.getCustomer().getLastName())
                .setPlateCar(allocation.getCustomer().getPlateCar())
                .setVacancieName(allocation.getVacancie().getVacancieNumber())
                .setBlockName(allocation.getVacancie().getBlock().getBlockName())
                .setTotal(allocationFinishedResponse.getTotal())
                .setUser(user)
                .build();
    }

    private Customer createCustomerModel(Customer customer,
                                         User user) throws BusinessException {
        try {
            return new Customer.Builder()
                    .setName(customer.getName())
                    .setLastName(customer.getLastName())
                    .setPlateCar(customer.getPlateCar())
                    .setUser(user)
                    .build();
        } catch(BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    private Allocation createAllocationModel(Customer customer,
                                             User user,
                                             Vacancie vacancie) {
        return new Allocation.Builder()
                .setArrivalTime(LocalDateTime.now(ZoneId.of("UTC")))
                .setCustomer(customer)
                .setUser(user)
                .setPlateCar(customer.getPlateCar())
                .setCustomerName(customer.getName())
                .setCustomerLastName(customer.getLastName())
                .setVacancieName(vacancie.getVacancieNumber())
                .setVacancie(vacancie)
                .setBlockName(vacancie.getBlock().getBlockName())
                .build();
    }

    private Customer createUpdateCustomerModel(Allocation newAllocation,
                                               Optional<Allocation> allocationOptional) {
        return new Customer.Builder()
                .setId(allocationOptional.get().getCustomer().getId())
                .setName(newAllocation.getCustomerName())
                .setLastName(newAllocation.getCustomerLastName())
                .setPlateCar(newAllocation.getPlateCar())
                .setUser(allocationOptional.get().getUser())
                .setAllocation(allocationOptional.get())
                .build();
    }

    private Allocation createUpdateAllocation(Optional<Allocation> allocationOptional,
                                              Customer updatedCustomerModel) {
        return new Allocation.Builder()
                .setId(allocationOptional.get().getId())
                .setBlockName(allocationOptional.get().getBlockName())
                .setVacancieName(allocationOptional.get().getVacancie().getVacancieNumber())
                .setCustomerName(updatedCustomerModel.getName())
                .setCustomerLastName(updatedCustomerModel.getLastName())
                .setPlateCar(updatedCustomerModel.getPlateCar())
                .setArrivalTime(allocationOptional.get().getArrivalTime())
                .setCustomer(updatedCustomerModel)
                .setUser(allocationOptional.get().getUser())
                .setVacancie(allocationOptional.get().getVacancie())
                .build();
    }

    private Vacancie createUpdatedVacancie(Vacancie vacancie,
                                           User user) {
        return new Vacancie.Builder()
                .setId(vacancie.getId())
                .setStatus(true)
                .setVacancieNumber(vacancie.getVacancieNumber())
                .setAllocation(vacancie.getAllocation())
                .setUser(user)
                .setBlock(vacancie.getBlock())
                .build();
    }

    private Vacancie createUpdatedVacancieStatus(Vacancie vacancie,
                                                 User user) {
        return new Vacancie.Builder()
                .setId(vacancie.getId())
                .setStatus(false)
                .setVacancieNumber(vacancie.getVacancieNumber())
                .setAllocation(null)
                .setUser(user)
                .setBlock(vacancie.getBlock())
                .build();
    }
}