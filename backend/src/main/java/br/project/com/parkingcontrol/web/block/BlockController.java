package br.project.com.parkingcontrol.web.block;

import br.project.com.parkingcontrol.util.BusinessException;
import br.project.com.parkingcontrol.domain.block.Block;
import br.project.com.parkingcontrol.domain.block.BlockService;
import br.project.com.parkingcontrol.domain.user.User;
import br.project.com.parkingcontrol.domain.user.UserServiceImpl;
import br.project.com.parkingcontrol.domain.vacancie.Vacancie;
import br.project.com.parkingcontrol.util.ResponseData;
import br.project.com.parkingcontrol.util.TokenGenerator;
import br.project.com.parkingcontrol.domain.user.UserRepository;
import br.project.com.parkingcontrol.domain.vacancie.VacancieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
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
    private final UserServiceImpl userService;

    BlockController(TokenGenerator tokenGenerator,
                    UserRepository userRepository,
                    BlockService blockService, VacancieService vacancieService,
                    UserServiceImpl userService) {
        this.tokenGenerator = tokenGenerator;
        this.userRepository = userRepository;
        this.blockService = blockService;
        this.vacancieService = vacancieService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ResponseData> getAllBlocks(@RequestHeader("Authorization") String authorizationHeader) {
        String token = extractTokenFromAuthorizationHeader(authorizationHeader);
        Integer userId = extractUserIdFromToken(token);

        List<Block> blocks = getBlocksModels(userId);
        rearrangeVacanciesInBlocks(blocks);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseData.generateSuccessfulResponse(blocks));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getOneBlock(@PathVariable(value = "id") UUID id,
                                              @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = extractTokenFromAuthorizationHeader(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            Optional<Block> blockOptional = getBlockModel(id);
            validateExistsBook(blockOptional);

            verifyRelationUserWithAllocation(blockOptional, userId);

            Optional<Block> block = getBlockModel(id);
            rearrangeVacancies(block.get());

            return ResponseEntity.status(HttpStatus.OK).body(ResponseData.generateSuccessfulResponse(block.get()));
        } catch(BusinessException err) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseData.generateUnsuccessfulResponse(err.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseData> createBlock(@RequestBody @Valid Block block,
                                              @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = extractTokenFromAuthorizationHeader(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            existsByBlockName(block, userId);
            User user = userService.getUserModel(userId);

            Block.Builder blockBuilder = createBlockModel(block, block.getTotalVacancies(), user);
            Block.Builder blockBuilderFinish = setVacanceListInBlockBuilder(blockBuilder, userId);
            Block blockBuild = saveDataBlock(blockBuilderFinish.build());

            createVacanciesForBlock(blockBuild, block.getTotalVacancies(), user);

            return ResponseEntity.status(HttpStatus.OK).body(ResponseData.generateSuccessfulResponse(blockBuild));
        } catch (Exception err) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseData.generateUnsuccessfulResponse(err.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> editBlock(@PathVariable(value = "id") UUID id,
                                            @RequestBody @Valid Block block,
                                            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = extractTokenFromAuthorizationHeader(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            Optional<Block> blockOptional = getBlockModel(id);

            validateExistsBook(blockOptional);
            verifyRelationUserWithAllocation(blockOptional, userId);
            existsByBlockNameAndIdNot(block, blockOptional, userId);

            Block.Builder blockBuilder = updateBlockModel(blockOptional, block);
            updateVacancyNumbers(blockBuilder, block.getTotalVacancies());
            setVacanceListInBlockBuilder(blockBuilder, userId);

            return ResponseEntity.status(HttpStatus.OK).body(ResponseData.generateSuccessfulResponse(blockService.save(blockBuilder.build())));
        } catch(Exception err) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseData.generateUnsuccessfulResponse(err.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBlock(@PathVariable(value = "id") UUID id,
                                              @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = extractTokenFromAuthorizationHeader(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            Optional<Block> blockOptional = getBlockModel(id);

            validateExistsBook(blockOptional);
            verifyRelationUserWithAllocation(blockOptional, userId);

            deleteAllVacancies(blockOptional.get().getId());
            deleteBlock(blockOptional.get().getId());

            return ResponseEntity.status(HttpStatus.OK).body(ResponseData.generateSuccessfulResponse(blockOptional));
        } catch(BusinessException err) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseData.generateUnsuccessfulResponse(err.getMessage()));
        }
    }

    private String extractTokenFromAuthorizationHeader(String authorizationHeader) {
        return tokenGenerator.extractTokenFromAuthorizationHeader(authorizationHeader);
    }

    private Integer extractUserIdFromToken(String token) {
        return tokenGenerator.extractUserIdFromToken(token);
    }

    private void validateExistsBook(Optional<Block> blockOptional) throws BusinessException {
        blockService.validationExistsblock(blockOptional);
    }

    private void existsByBlockNameAndIdNot(Block block,
                                           Optional<Block> blockOptional,
                                           Integer userId) throws BusinessException {
        blockService.existsByBlockNameAndIdNot(block.getBlockName(), blockOptional.get().getId(), userId);
    }

    private void existsByBlockName(Block block,
                                   Integer userId) throws BusinessException {
        blockService.existsByBlockName(block.getBlockName(), userId);
    }

    private void verifyRelationUserWithAllocation(Optional<Block> block,
                                                  Integer userId) {
        blockService.verifyRelationUserWithBlock(block, userId);
    }

    private Optional<Block> getBlockModel(UUID id) {
        return blockService.findById(id);
    }

    private List<Block> getBlocksModels(Integer userId) {
        return blockService.findAll(userId);
    }

    private void rearrangeVacanciesInBlocks(List<Block> blocks) {
        for (Block block : blocks) {
            rearrangeVacancies(block);
        }
    }

    private void rearrangeVacancies(Block block) {
        List<Vacancie> vacancies = block.getVacancieList();
        vacancies.sort(Comparator.comparingInt(Vacancie::getVacancieNumber));
    }

    private Block.Builder setVacanceListInBlockBuilder(Block.Builder blockBuilder,
                                                       Integer userId) {
        return blockBuilder.setVacancieList(vacancieService.getVacancies(userId));
    }

    private Block.Builder createBlockModel(Block block,
                                           Integer totalVacancies,
                                           User user) throws BusinessException {
        try {
            return new Block.Builder()
                    .setBlockName(block.getBlockName())
                    .setTotalVacancies(totalVacancies)
                    .setUser(user);
        } catch(BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    private void createVacanciesForBlock(Block blockBuilder,
                                         int totalVacancies,
                                         User user) {
        for (int i = 0; i < totalVacancies; i++) {
            Vacancie vacancyBuilder = new Vacancie.Builder()
                    .setVacancieNumber(i + 1)
                    .setBlock(blockBuilder)
                    .setUser(user)
                    .build();

            vacancieService.createVacancy(vacancyBuilder);
        }
    }

    private Block.Builder updateBlockModel(Optional<Block> blockOptional,
                                           Block block) {
        Block existingBlock = blockOptional.get();

        return new Block.Builder()
                .setId(existingBlock.getId())
                .setBlockName(block.getBlockName())
                .setTotalVacancies(block.getTotalVacancies())
                .setUser(existingBlock.getUser())
                .setVacancieList(existingBlock.getVacancieList());
    }

    private void updateVacancyNumbers(Block.Builder blockBuilder,
                                      int totalVacancies) throws BusinessException {
        List<Vacancie> vacancies = blockBuilder.build().getVacancieList();

        updateExistingVacancies(vacancies, totalVacancies);
        createNewVacancies(blockBuilder, vacancies, totalVacancies);
        deleteExcessVacancies(vacancies, totalVacancies);
    }

    private void updateExistingVacancies(List<Vacancie> vacancies,
                                         int totalVacancies) throws BusinessException {
        for (int i = 0; i < Math.min(vacancies.size(), totalVacancies); i++) {
            Vacancie vacancy = vacancies.get(i);
            int newVacancyNumber = i + 1;

            if (vacancy.getVacancieNumber() != newVacancyNumber) {
                vacancy.setVacancieNumber(newVacancyNumber);
                vacancieService.updateVacancy(vacancy);
            }
        }
    }

    private void createNewVacancies(Block.Builder blockBuilder,
                                    List<Vacancie> vacancies,
                                    int totalVacancies) throws BusinessException {
        for (int i = vacancies.size(); i < totalVacancies; i++) {
            Vacancie vacancy = new Vacancie.Builder()
                    .setVacancieNumber(i + 1)
                    .setBlock(blockBuilder.build())
                    .setUser(blockBuilder.getUser())
                    .build();

            vacancieService.createVacancy(vacancy);
        }
    }

    private void deleteExcessVacancies(List<Vacancie> vacancies,
                                       int totalVacancies) throws BusinessException {
        if (vacancies.size() > totalVacancies) {
            for (int i = vacancies.size() - 1; i >= totalVacancies; i--) {
                Vacancie vacancy = vacancies.get(i);
                vacancieService.deleteVacancy(vacancy.getId());
            }
        }
    }

    private Block saveDataBlock(Block initializeInstanceBlock) {
        return blockService.save(initializeInstanceBlock);
    }

    private void deleteAllVacancies(UUID id) {
        vacancieService.deleteAllVacancies(id);
    }

    private void deleteBlock(UUID id) {
        blockService.deleteBlock(id);
    }
}
