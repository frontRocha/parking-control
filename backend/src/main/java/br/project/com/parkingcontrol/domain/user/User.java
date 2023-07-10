package br.project.com.parkingcontrol.domain.user;

import br.project.com.parkingcontrol.domain.allocation.Allocation;
import br.project.com.parkingcontrol.domain.block.Block;
import br.project.com.parkingcontrol.domain.customer.Customer;
import br.project.com.parkingcontrol.domain.history.History;
import br.project.com.parkingcontrol.domain.vacancie.Vacancie;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="TB_USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String login;
    @Column(unique = false, nullable = false)
    private String password;
    @Column(unique = false, nullable = true)
    private double pricePerHour;
    @Column(nullable = false)
    private LocalDateTime registrationDate;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Block> blockList;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Allocation> allocationList;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Customer> customerList;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<History> historyList;
    @JsonIgnore
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

    public double getPricePerHour() {
        return pricePerHour;
    }

    private static void fieldValidation(String login, String password) {
        Preconditions.checkArgument(login.length() > 1, "email cannot be null");
        Preconditions.checkArgument(password.length() > 1, "password cannot be null");
    }

    public static class Builder {
        private Integer id;
        private String login;
        private String password;
        private double pricePerHour;
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
            this.pricePerHour = 0;
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

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setPricePerHour(double pricePerHour) {
            this.pricePerHour = pricePerHour;
            return this;
        }

        public Builder setRegistrationDate(LocalDateTime registrationDate) {
            this.registrationDate = registrationDate;
            return this;
        }

        public Builder setBlockList(List<Block> blockList)  {
            this.blockList = blockList;
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

        public double getPricePerHour() {
            return pricePerHour;
        }

        public LocalDateTime getRegistrationDate() {
            return registrationDate;
        }

        public List<Block> getBlockList() {
            return blockList;
        }

        public User build() {
            fieldValidation(login, password);
            return new User(id, login, password, pricePerHour, registrationDate, blockList, allocationList, customerList, historyList, vacancieList);
        }
    }
}
