package br.project.com.parkingcontrol.web.allocation;

import br.project.com.parkingcontrol.businessException.BusinessException;
import br.project.com.parkingcontrol.domain.allocation.Allocation;
import br.project.com.parkingcontrol.domain.allocation.AllocationFinishedResponse;
import br.project.com.parkingcontrol.domain.allocation.AllocationService;
import br.project.com.parkingcontrol.domain.block.BlockService;
import br.project.com.parkingcontrol.domain.customer.Customer;
import br.project.com.parkingcontrol.domain.customer.CustomerRepository;
import br.project.com.parkingcontrol.domain.customer.CustomerService;
import br.project.com.parkingcontrol.domain.history.History;
import br.project.com.parkingcontrol.domain.history.HistoryService;
import br.project.com.parkingcontrol.domain.user.User;
import br.project.com.parkingcontrol.domain.user.UserRepository;
import br.project.com.parkingcontrol.domain.vacancie.Vacancie;
import br.project.com.parkingcontrol.domain.vacancie.VacancieService;
import br.project.com.parkingcontrol.util.TokenGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private BusinessException businessException;

    AllocationController(VacancieService vacancieService,
                         TokenGenerator tokenGenerator,
                         BlockService blockService,
                         UserRepository userRepository,
                         AllocationService allocationService,
                         CustomerRepository customerRepository, CustomerService customerService, HistoryService historyService) {
        this.vacancieService = vacancieService;
        this.tokenGenerator = tokenGenerator;
        this.blockService = blockService;
        this.userRepository = userRepository;
        this.allocationService = allocationService;
        this.customerService = customerService;
        this.historyService = historyService;
    }

    @GetMapping
    public ResponseEntity<List<Allocation>> getAllAllocation(@RequestHeader("Authorization") String authorizationHeader) {
        String token = tokenGenerator.extractTokenFromAuthorizationHeader(authorizationHeader);
        Integer userId = tokenGenerator.extractUserIdFromToken(token);

        return ResponseEntity.status(HttpStatus.OK).body(allocationService.findAll(userId));
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<Object> createAllocation(@RequestHeader("Authorization") String authorizationHeader,
                                                   @RequestBody Customer customer,
                                                   @PathVariable(value = "id") UUID id) {
        try {
            String token = tokenGenerator.extractTokenFromAuthorizationHeader(authorizationHeader);
            Integer userId = tokenGenerator.extractUserIdFromToken(token);

            Optional<Vacancie> vacancie = vacancieService.findyById(id);
            vacancieService.validateStatusVacancie(vacancie.get());

            User user = getUserModel(userId);
            validateUserCredential(userId, vacancie.get());

            Customer customerModel = createCustomerModel(customer, user);

            Customer savedCustomer = customerService.createCustomer(customerModel);
            Vacancie vacancieModel = createUpdatedVacancie(vacancie.get(), user);

            vacancieService.createVacancy(vacancieModel);
            Allocation allocationModel = createAllocationModel(savedCustomer, user, vacancie.get());

            return ResponseEntity.status(HttpStatus.CREATED).body(allocationService.createAllocation(allocationModel));
        } catch(BusinessException err) {
            return BusinessException.handleBusinessException(err, HttpStatus.CONFLICT.value());
        }
    }

    @DeleteMapping("/finish/{id}")
    public ResponseEntity<Object> deleteAllocation(@RequestHeader("Authorization") String authorizationHeader,
                                                                       @PathVariable(value = "id") UUID id) {
        try {
            String token = tokenGenerator.extractTokenFromAuthorizationHeader(authorizationHeader);
            Integer userId = tokenGenerator.extractUserIdFromToken(token);

            Optional<Allocation> allocation = allocationService.findById(id);
            allocationService.existsAllocation(allocation);

            User user = getUserModel(userId);

            validateUserTeste(userId, allocation.get());

            var status = createUpdatedVacancieStatus(allocation.get().getVacancie(), user);

            var response = createFinishResponse(allocation.get());
            var historyModel = createHistoryModel(allocation.get(), user, response);

            vacancieService.createVacancy(status);
            allocationService.deleteAllocation(id);
            customerService.deleteCustomer(allocation.get().getCustomer().getId());
            createUpdatedVacancieStatus(allocation.get().getVacancie(), user);

            historyService.saveDataInHistory(historyModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch(BusinessException err) {
            return BusinessException.handleBusinessException(err, HttpStatus.CONFLICT.value());
        }
    }

    private AllocationFinishedResponse createFinishResponse(Allocation allocation) {
        return new AllocationFinishedResponse.Builder()
                .setArrivalTime(allocation.getArrivalTime())
                .setDepartureTime(LocalDateTime.now(ZoneId.of("UTC")))
                .setTotal(0)
                .setName(allocation.getCustomerName())
                .setVacancieNumber(allocation.getVacancieName())
                .setBlockName(allocation.getBlockName())
                .build();
    }

    private History createHistoryModel(Allocation allocation, User user, AllocationFinishedResponse allocationFinishedResponse) {
        return new History.Builder()
                .setArrivalTime(allocation.getArrivalTime())
                .setDepartureTime(LocalDateTime.now(ZoneId.of("UTC")))
                .setCustomerName(allocation.getCustomer().getName())
                .setCustomerLastName(allocation.getCustomer().getLastName())
                .setPlateCar(allocation.getCustomer().getPlateCar())
                .setVacancieName(allocation.getVacancie().getVacancieNumber())
                .setBlockName(allocation.getVacancie().getBlock().getBlockName())
                .setTotal(allocationFinishedResponse.getTotal())
                .setVacancie(allocation.getVacancie())
                .setUser(user)
                .build();
    }

    private Customer createCustomerModel(Customer customer, User user) {
        return new Customer.Builder()
                .setName(customer.getName())
                .setLastName(customer.getLastName())
                .setPlateCar(customer.getPlateCar())
                .setUser(user)
                .build();
    }

    private Allocation createAllocationModel(Customer customer, User user, Vacancie vacancie) {
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

    private Vacancie createUpdatedVacancie(Vacancie vacancie, User user) {
        return new Vacancie.Builder()
                .setId(vacancie.getId())
                .setStatus(true)
                .setVacancieNumber(vacancie.getVacancieNumber())
                .setAllocation(vacancie.getAllocation())
                .setUser(user)
                .setBlock(vacancie.getBlock())
                .build();
    }

    private Vacancie createUpdatedVacancieStatus(Vacancie vacancie, User user) {
        return new Vacancie.Builder()
                .setId(vacancie.getId())
                .setStatus(false)
                .setVacancieNumber(vacancie.getVacancieNumber())
                .setAllocation(null)
                .setUser(user)
                .setBlock(vacancie.getBlock())
                .build();
    }

    private User getUserModel(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private void validateUserCredential(Integer userId, Vacancie vacancie) throws BusinessException {
        if(userId != vacancie.getUser().getId()) {
            throw new BusinessException("the vacancy is not exists");
        }
    }

    private void validateUserTeste(Integer userId, Allocation allocation) {
        if(userId != allocation.getUser().getId()) {
            throw new BusinessException("the vacancy is not exists");
        }
    }
}