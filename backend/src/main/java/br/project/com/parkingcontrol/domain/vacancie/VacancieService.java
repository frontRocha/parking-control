package br.project.com.parkingcontrol.domain.vacancie;

import br.project.com.parkingcontrol.businessException.BusinessException;
import br.project.com.parkingcontrol.domain.block.Block;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VacancieService {
    private final VacancieRepository vacancieRepository;

    public VacancieService(VacancieRepository vacancieRepository) {
        this.vacancieRepository = vacancieRepository;
    }

    public List<Vacancie> getVacancies(Integer userId) {
        return vacancieRepository.findAllByUserIdOrderByVacancieNumberAsc(userId);
    }

    public Optional<Vacancie> findById(UUID id) {
        return vacancieRepository.findById(id);
    }

    public Vacancie createVacancy(Vacancie vacancie) {
        return vacancieRepository.save(vacancie);
    }

    public void deleteVacancy(UUID vacancyId) {
        Optional<Vacancie> vacancyOptional = vacancieRepository.findById(vacancyId);
        Vacancie vacancy = vacancyOptional.get();

        Block block = vacancy.getBlock();
        if (block != null) {
            block.getVacancieList().remove(vacancy);
        }

        vacancieRepository.deleteById(vacancy.getId());
    }

    @Transactional
    public void deleteAllVacancies(UUID blockId) {
        vacancieRepository.deleteAllByBlock_Id(blockId);
    }

    public Vacancie updateVacancy(Vacancie vacancy) throws BusinessException {
        Vacancie existingVacancy = vacancieRepository.findById(vacancy.getId())
                .orElseThrow(() -> new BusinessException("Vacancy not found"));

        existingVacancy.setVacancieNumber(vacancy.getVacancieNumber());
        existingVacancy.setBlock(vacancy.getBlock());
        existingVacancy.setUser(vacancy.getUser());

        Vacancie updatedVacancy = vacancieRepository.save(existingVacancy);

        return updatedVacancy;
    }

    public void validateStatusVacancie(Vacancie vacancie) throws BusinessException {
        if(vacancie.getStatus()) {
            throw new BusinessException("This vacancie is already in use");
        }
    }

    public void validationExistsVacancie(Optional<Vacancie> bookModelOptional) throws BusinessException {
        if(!bookModelOptional.isPresent()) {
            throw new BusinessException("This vacancy does not exist");
        }
    }

    public void verifyRelationUserWithVacancy(Integer userId, Vacancie vacancie) throws BusinessException {
        if(userId != vacancie.getUser().getId()) {
            throw new BusinessException("the vacancy is not exists");
        }
    }
}