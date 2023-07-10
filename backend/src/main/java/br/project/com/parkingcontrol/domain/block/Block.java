package br.project.com.parkingcontrol.domain.block;

import br.project.com.parkingcontrol.domain.user.User;
import br.project.com.parkingcontrol.domain.vacancie.Vacancie;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TB_BLOCK")
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false, unique = false, length = 1)
    private String blockName;
    @Column(nullable = false, unique = false)
    private Integer totalVacancies;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "block")
    private List<Vacancie> vacancieList;

    public UUID getId() {
        return id;
    }

    public String getBlockName() {
        return blockName;
    }

    public Integer getTotalVacancies() {
        return totalVacancies;
    }

    public User getUser() {
        return user;
    }

    public List<Vacancie> getVacancieList() {
        return vacancieList;
    }

    private static void fieldValidation(String blockName, Integer totalVacancies, User user) {
        Preconditions.checkNotNull(Strings.isNullOrEmpty(blockName),"block name cannot be null");
        Preconditions.checkArgument(blockName.length() == 1,"block name must be just one character");
        Preconditions.checkArgument(totalVacancies > 0,"the total number of vacancies must be greater than 0");
        Preconditions.checkNotNull(user,"user cannot be null");
    }

    public static class Builder {
        private UUID id;
        private String blockName;
        private Integer totalVacancies;
        private User user;
        private List<Vacancie> vacancieList;

        public Builder() {
            this.id = null;
            this.blockName = null;
            this.totalVacancies = null;
            this.user = null;
            this.vacancieList = null;
        }

        public Builder setId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder setBlockName(String blockName) {
            this.blockName = blockName;
            return this;
        }

        public Builder setTotalVacancies(Integer totalVacancies) {
            this.totalVacancies = totalVacancies;
            return this;
        }

        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public Builder setVacancieList(List<Vacancie> vacancieList) {
            this.vacancieList = vacancieList;
            return this;
        }

        public UUID getId() {
            return id;
        }

        public String getBlockName() {
            return blockName;
        }

        public User getUser() {
            return user;
        }

        public Integer getTotalVacancies() {
            return totalVacancies;
        }

        public Block build() {
            fieldValidation(blockName, totalVacancies, user);
            return new Block(id, blockName, totalVacancies, user, vacancieList);
        }
    }
}
