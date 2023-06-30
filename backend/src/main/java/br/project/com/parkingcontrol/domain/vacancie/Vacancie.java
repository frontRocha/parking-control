package br.project.com.parkingcontrol.domain.vacancie;

import br.project.com.parkingcontrol.domain.allocation.Allocation;
import br.project.com.parkingcontrol.domain.block.Block;
import br.project.com.parkingcontrol.domain.history.History;
import br.project.com.parkingcontrol.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "VACANCIES")
public class Vacancie {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private Integer vacancieNumber;
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

    @JsonIgnore
    @OneToMany(mappedBy = "vacancie")
    private List<History> historyList;

    public UUID getId() {
        return id;
    }

    public void setVacancieNumber(Integer vacancieNumber) {
        this.vacancieNumber = vacancieNumber;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public void setAllocation(Allocation allocation) {
        this.allocation = allocation;
    }

    public Integer getVacancieNumber() {
        return vacancieNumber;
    }

    public User getUser() {
        return user;
    }

    public Block getBlock() {
        return block;
    }

    public boolean getStatus() {
        return status;
    }

    public Allocation getAllocation() {
        return allocation;
    }

    public List<History> getHistoryList() {
        return historyList;
    }

    public static class Builder {
        private UUID id;
        private Integer vacancieNumber;
        private boolean status;
        private User user;
        private Block block;
        private Allocation allocation;
        private List<History> historyList;

        public Builder() {
            this.id = null;
            this.vacancieNumber = null;
            this.status = false;
            this.user = null;
            this.block = null;
            this.allocation = null;
            this.historyList = null;
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

        public Builder setHistory(List<History> historyList) {
            this.historyList = historyList;
            return this;
        }

        public Vacancie build() {
            return new Vacancie(id, vacancieNumber, status, user, block, allocation, historyList);
        }
    }
}