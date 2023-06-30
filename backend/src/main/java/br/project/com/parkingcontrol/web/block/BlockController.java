package br.project.com.parkingcontrol.web.block;

import br.project.com.parkingcontrol.businessException.BusinessException;
import br.project.com.parkingcontrol.domain.block.Block;
import br.project.com.parkingcontrol.domain.block.BlockService;
import br.project.com.parkingcontrol.domain.user.User;
import br.project.com.parkingcontrol.domain.vacancie.Vacancie;
import br.project.com.parkingcontrol.util.TokenGenerator;
import br.project.com.parkingcontrol.domain.user.UserRepository;
import br.project.com.parkingcontrol.domain.vacancie.VacancieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/block")
public class BlockController {
    private final TokenGenerator tokenGenerator;
    private final UserRepository userRepository;
    private final BlockService blockService;
    private final VacancieService vacancieService;

    BlockController(TokenGenerator tokenGenerator,
                    UserRepository userRepository,
                    BlockService blockService, VacancieService vacancieService) {
        this.tokenGenerator = tokenGenerator;
        this.userRepository = userRepository;
        this.blockService = blockService;
        this.vacancieService = vacancieService;
    }

    @GetMapping
    public ResponseEntity<List<Block>> getAllBlocks(@RequestHeader("Authorization") String authorizationHeader) {
        String token = tokenGenerator.extractTokenFromAuthorizationHeader(authorizationHeader);
        Integer userId = tokenGenerator.extractUserIdFromToken(token);

        return ResponseEntity.status(HttpStatus.OK).body(blockService.findAll(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneBlock(@PathVariable(value = "id") UUID id,
                                              @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Optional<Block> blockOptional = blockService.findById(id);
            String token = tokenGenerator.extractTokenFromAuthorizationHeader(authorizationHeader);
            Integer userId = tokenGenerator.extractUserIdFromToken(token);

            blockService.validationExistsbook(blockOptional);
            verifyUser(blockOptional, userId);

            return ResponseEntity.status(HttpStatus.OK).body(blockOptional.get());
        } catch(BusinessException err) {
            return BusinessException.handleBusinessException(err, HttpStatus.CONFLICT.value());
        }
    }

    @PostMapping
    public ResponseEntity<Object> createBlock(@RequestBody @Valid Block block,
                                              @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = tokenGenerator.extractTokenFromAuthorizationHeader(authorizationHeader);
            Integer userId = tokenGenerator.extractUserIdFromToken(token);

            blockService.existsByBlockName(block.getBlockName(), userId);

            User user = getUserModel(userId);

            Block.Builder blockBuilder = createBlockModel(block, block.getTotalVacancies(), user);
            Block.Builder setVacancieListInBlockBuilder = setVacanceListInBlockBuilder(blockBuilder, userId);
            Block initializeInstanceBlock = initializeInstanceBlock(setVacancieListInBlockBuilder);
            Block blockBuilded = blockService.save(initializeInstanceBlock);

            createVacanciesForBlock(blockBuilded, block.getTotalVacancies(), user);

            return ResponseEntity.status(HttpStatus.CREATED).body(blockBuilded);
        } catch(BusinessException err) {
            return BusinessException.handleBusinessException(err, HttpStatus.UNAUTHORIZED.value());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> editBlock(@PathVariable(value = "id") UUID id,
                                            @RequestBody @Valid Block block,
                                            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Optional<Block> blockOptional = blockService.findById(id);

            String token = tokenGenerator.extractTokenFromAuthorizationHeader(authorizationHeader);
            Integer userId = tokenGenerator.extractUserIdFromToken(token);

            blockService.validationExistsbook(blockOptional);
            verifyUser(blockOptional, userId);
            blockService.existsByBlockNameAndIdNot(block.getBlockName(), blockOptional.get().getId(), userId);

            Block.Builder blockBuilder = updateBlockModel(blockOptional, block);

            updateVacancyNumbers(blockBuilder, block.getTotalVacancies());

            blockBuilder.setVacancieList(vacancieService.getVacancies(userId));
            var blockBuild = blockService.save(blockBuilder.build());

            return ResponseEntity.status(HttpStatus.OK).body(blockBuild);
        } catch(BusinessException err) {
            return BusinessException.handleBusinessException(err, HttpStatus.UNAUTHORIZED.value());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBlock(@PathVariable(value = "id") UUID id,
                                              @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Optional<Block> blockOptional = blockService.findById(id);

            String token = tokenGenerator.extractTokenFromAuthorizationHeader(authorizationHeader);
            Integer userId = tokenGenerator.extractUserIdFromToken(token);

            blockService.validationExistsbook(blockOptional);
            verifyUser(blockOptional, userId);

            vacancieService.deleteAllVacancies(blockOptional.get().getId());
            blockService.deleteBlock(blockOptional.get().getId());

            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch(BusinessException err) {
            return BusinessException.handleBusinessException(err, HttpStatus.UNAUTHORIZED.value());
        }
    }

    private Block.Builder createBlockModel(Block block, Integer totalVacancies, User user) throws BusinessException {
        try {
            return new Block.Builder()
                    .setBlockName(block.getBlockName())
                    .setTotalVacancies(totalVacancies)
                    .setUser(user);
        } catch(BusinessException e) {
            System.out.println("tem problema aqui " + e);
            throw new BusinessException(e.getMessage());
        }
    }

    private void createVacanciesForBlock(Block blockBuilder, int totalVacancies, User user) {
        Block savedBlock = blockService.save(blockBuilder);

        for (int i = 0; i < totalVacancies; i++) {
            Vacancie vacancyBuilder = new Vacancie.Builder()
                    .setVacancieNumber(i + 1)
                    .setBlock(savedBlock)
                    .setUser(user)
                    .build();

            vacancieService.createVacancy(vacancyBuilder);
        }
    }

    private Block.Builder updateBlockModel(Optional<Block> blockOptional, Block block) {
        Block existingBlock = blockOptional.get();

        return new Block.Builder()
                .setId(existingBlock.getId())
                .setBlockName(block.getBlockName())
                .setTotalVacancies(block.getTotalVacancies())
                .setUser(existingBlock.getUser())
                .setVacancieList(existingBlock.getVacancieList());
    }

    private void updateVacancyNumbers(Block.Builder blockBuilder, int totalVacancies) throws BusinessException {
        List<Vacancie> vacancies = blockBuilder.build().getVacancieList();

        updateExistingVacancies(vacancies, totalVacancies);
        createNewVacancies(blockBuilder, vacancies, totalVacancies);
        deleteExcessVacancies(vacancies, totalVacancies);
    }

    private void updateExistingVacancies(List<Vacancie> vacancies, int totalVacancies) throws BusinessException {
        for (int i = 0; i < Math.min(vacancies.size(), totalVacancies); i++) {
            Vacancie vacancy = vacancies.get(i);
            int newVacancyNumber = i + 1;

            if (vacancy.getVacancieNumber() != newVacancyNumber) {
                vacancy.setVacancieNumber(newVacancyNumber);
                vacancieService.updateVacancy(vacancy);
            }
        }
    }

    private void createNewVacancies(Block.Builder blockBuilder, List<Vacancie> vacancies, int totalVacancies) throws BusinessException {
        for (int i = vacancies.size(); i < totalVacancies; i++) {
            Vacancie vacancy = new Vacancie.Builder()
                    .setVacancieNumber(i + 1)
                    .setBlock(blockBuilder.build())
                    .setUser(blockBuilder.getUser())
                    .build();

            System.out.println("o erro ta aqui");

            vacancieService.createVacancy(vacancy);
            System.out.println("mas eu passei aqui");
        }
    }

    private void deleteExcessVacancies(List<Vacancie> vacancies, int totalVacancies) throws BusinessException {
        if (vacancies.size() > totalVacancies) {
            for (int i = vacancies.size() - 1; i >= totalVacancies; i--) {
                Vacancie vacancy = vacancies.get(i);
                vacancieService.deleteVacancy(vacancy.getId());
            }
        }
    }

    private Block.Builder setVacanceListInBlockBuilder(Block.Builder blockBuilder, Integer userId) {
        return blockBuilder.setVacancieList(vacancieService.getVacancies(userId));
    }

    private Block initializeInstanceBlock(Block.Builder blockBuilder) {
        return blockBuilder.build();
    }

    private void verifyUser(Optional<Block> block, Integer userId) throws BusinessException {
        if(block.orElse(null).getUser().getId() != userId) {
            throw new BusinessException("This block does not exist");
        }
    }

    private User getUserModel(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
