package br.project.com.parkingcontrol.domain.block;

import br.project.com.parkingcontrol.domain.user.User;
import br.project.com.parkingcontrol.domain.vacancie.Vacancie;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "BLOCK")
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false, unique = false)
    private char blockName;
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

    public char getBlockName() {
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

    private static void validarCampos(char campo1, int campo2) {
        Preconditions.checkNotNull(campo1, "Campo1 não pode ser nulo");
        Preconditions.checkArgument(campo2 > 0,"Campo2 deve ser maior que zero");
    }

    public static class Builder {
        private UUID id;
        private char blockName;
        private Integer totalVacancies;
        private User user;
        private List<Vacancie> vacancieList;

        public Builder() {
            this.id = null;
            this.blockName = 'A';
            this.totalVacancies = null;
            this.user = null;
            this.vacancieList = null;
        }

        public Builder setId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder setBlockName(char blockName) {
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

        public char getBlockName() {
            return blockName;
        }

        public User getUser() {
            return user;
        }

        public Integer getTotalVacancies() {
            return totalVacancies;
        }

        public Block build() {
            validarCampos(blockName, totalVacancies);
            return new Block(id, blockName, totalVacancies, user, vacancieList);
        }
    }
}
