package br.project.com.parkingcontrol.domain.user;

import br.project.com.parkingcontrol.domain.allocation.Allocation;
import br.project.com.parkingcontrol.domain.block.Block;
import br.project.com.parkingcontrol.domain.customer.Customer;
import br.project.com.parkingcontrol.domain.history.History;
import br.project.com.parkingcontrol.domain.vacancie.Vacancie;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String login;
    @Column(unique = false, nullable = false)
    private String password;
    @Column(nullable = false)
    private LocalDateTime registrationDate;

    @OneToMany(mappedBy = "user")
    private List<Block> blockList;

    @OneToMany(mappedBy = "user")
    private List<Allocation> allocationList;

    @OneToMany(mappedBy = "user")
    private List<Customer> customerList;

    @OneToMany(mappedBy = "user")
    private List<History> historyList;

    @OneToMany(mappedBy = "user")
    private List<Vacancie> vacancieList;

    public Integer getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public List<Block> getBlockList() {
        return blockList;
    }

    public List<Allocation> getAllocationList() {
        return allocationList;
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }

    public List<History> getHistoryList() {
        return historyList;
    }

    public List<Vacancie> getVacancieList() {
        return vacancieList;
    }

    public static class Builder {
        private Integer id;
        private String login;
        private String password;
        private LocalDateTime registrationDate;
        private List<Block> blockList;
        private List<Allocation> allocationList;
        private List<Customer> customerList;
        private List<History> historyList;
        private List<Vacancie> vacancieList;

        public Builder() {
            this.id = null;
            this.registrationDate = null;
            this.login = null;
            this.password = null;
            this.blockList = null;
            this.allocationList = null;
            this.customerList = null;
            this.historyList = null;
            this.vacancieList = null;
        }

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder setLogin(String login) {
            this.login = login;
            return this;
        }

        public Builder setPassoword(String password) {
            this.password = password;
            return this;
        }

        public Builder setResgistrationDate(LocalDateTime registrationDate) {
            this.registrationDate = registrationDate;
            return this;
        }

        public Builder setBlockList(List<Block> blockList)  {
            this.blockList = blockList;
            return this;
        }

        public Builder setRegistrationDate(LocalDateTime registrationDate) {
            this.registrationDate = registrationDate;
            return this;
        }

        public Builder setAllocationList(List<Allocation> allocationList) {
            this.allocationList = allocationList;
            return this;
        }

        public Builder setCustomerList(List<Customer> customerList) {
            this.customerList = customerList;
            return this;
        }

        public Builder setHistoryList(List<History> historyList) {
            this.historyList = historyList;
            return this;
        }

        public Builder setVacancieList(List<Vacancie> vacancieList) {
            this.vacancieList = vacancieList;
            return this;
        }

        public Integer getId() {
            return id;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public LocalDateTime getRegistrationDate() {
            return registrationDate;
        }

        public List<Block> getBlockList() {
            return blockList;
        }

        public User build() {
            return new User(id, login, password, registrationDate, blockList, allocationList, customerList, historyList, vacancieList);
        }
    }
}
