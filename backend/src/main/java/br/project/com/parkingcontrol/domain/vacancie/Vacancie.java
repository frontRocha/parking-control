package br.project.com.parkingcontrol.domain.vacancie;

import br.project.com.parkingcontrol.domain.allocation.Allocation;
import br.project.com.parkingcontrol.domain.block.Block;
import br.project.com.parkingcontrol.domain.history.History;
import br.project.com.parkingcontrol.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TB_VACANCIES")
public class Vacancie {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false, unique = false)
    private Integer vacancieNumber;
    @Column(nullable = false, unique = false)
    private boolean status;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "block_id")
    private Block block;

    @OneToOne(mappedBy = "vacancie")
    private Allocation allocation;

    public static class Builder {
        private UUID id;
        private Integer vacancieNumber;
        private boolean status;
        private User user;
        private Block block;
        private Allocation allocation;

        public Builder() {
            this.id = null;
            this.vacancieNumber = null;
            this.status = false;
            this.user = null;
            this.block = null;
            this.allocation = null;
        }

        public Builder setId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder setVacancieNumber(Integer vacancieNumber) {
            this.vacancieNumber = vacancieNumber;
            return this;
        }

        public Builder setStatus(boolean status) {
            this.status = status;
            return this;
        }

        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public Builder setBlock(Block block) {
            this.block = block;
            return this;
        }

        public Builder setAllocation(Allocation allocation) {
            this.allocation = allocation;
            return this;
        }

        public Vacancie build() {
            return new Vacancie(id, vacancieNumber, status, user, block, allocation);
        }
    }
}