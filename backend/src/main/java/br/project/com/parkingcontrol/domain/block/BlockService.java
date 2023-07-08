package br.project.com.parkingcontrol.domain.block;

import br.project.com.parkingcontrol.businessException.BusinessException;
import br.project.com.parkingcontrol.domain.allocation.Allocation;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BlockService {
    private final BlockRepository blockRepository;

    public BlockService(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    public List<Block> findAll(Integer user_id) {
        return blockRepository.findAllByUserId(user_id);
    }

    public Optional<Block> findById(UUID id) {
        return blockRepository.findById(id);
    }

    @Transactional
    public Block save(Block block) {
        return blockRepository.save(block);
    }

    @Transactional
    public void deleteBlock(UUID blockId) {
        blockRepository.deleteById(blockId);
    }

    public void existsByBlockName(String blockName, Integer userId) throws BusinessException {
        validationBlockName(blockName, userId);
    }

    public void existsByBlockNameAndIdNot(String blockName, UUID id, Integer userId) throws BusinessException {
        validationBlockNameAndIdNot(blockName, id, userId);
    }

    private void validationBlockName(String blockName, Integer userId) throws BusinessException {
        int count = blockRepository.countByBlockNameAndUserId(blockName, userId);

        if (count > 0) {
            throw new BusinessException("The block: " + blockName + " is already registered for the user");
        }
    }

    private void validationBlockNameAndIdNot(String blockName, UUID id, Integer userId) throws BusinessException {
        int count = blockRepository.countByBlockNameAndIdNotAndUserId(blockName, id, userId);

        if (count > 0) {
            throw new BusinessException("The block: " + blockName + " is already registered for the user");
        }
    }

    public void validationExistsblock(Optional<Block> bookModelOptional) throws BusinessException {
        if(!bookModelOptional.isPresent()) {
            throw new BusinessException("This block does not exist");
        }
    }

    public void verifyRelationUserWithBlock(Optional<Block> allocation, Integer userId) throws BusinessException {
        if(allocation.orElse(null).getUser().getId() != userId) {
            throw new BusinessException("This allocation does not exist");
        }
    }
}
