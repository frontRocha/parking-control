package br.project.com.parkingcontrol.domain.allocation;

import br.project.com.parkingcontrol.domain.customer.Customer;
import br.project.com.parkingcontrol.domain.user.User;
import br.project.com.parkingcontrol.domain.vacancie.Vacancie;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "ALLOCATION")
public class Allocation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
    private String customerName;
    private String customerLastName;
    private String plateCar;
    private Integer vacancieName;
    private char blockName;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "vacancie_id")
    private Vacancie vacancie;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public UUID getId() {
        return id;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public String getPlateCar() {
        return plateCar;
    }

    public Integer getVacancieName() {
        return vacancieName;
    }

    public char getBlockName() {
        return blockName;
    }

    public User getUser() {
        return user;
    }

    public Vacancie getVacancie() {
        return vacancie;
    }

    public Customer getCustomer() {
        return customer;
    }

    public static class Builder {
        private UUID id;
        private LocalDateTime arrivalTime;
        private LocalDateTime departureTime;
        private User user;
        private Vacancie vacancie;
        private Customer customer;
        private String customerName;
        private String customerLastName;
        private String plateCar;
        private Integer vacancieName;
        private char blockName;

        public Builder() {
            this.id = null;
            this.arrivalTime = null;
            this.departureTime = null;
            this.customerName = null;
            this.customerLastName = null;
            this.plateCar = null;
            this.vacancieName = null;
            this.blockName = 'A';
            this.user = null;
            this.vacancie = null;
            this.customer = null;
        }

        public Builder setId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder setArrivalTime(LocalDateTime arrivalTime) {
            this.arrivalTime = arrivalTime;
            return this;
        }

        public Builder setDepartureTime(LocalDateTime departureTime) {
            this.departureTime = departureTime;
            return this;
        }

        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public Builder setVacancie(Vacancie vacancie) {
            this.vacancie = vacancie;
            return this;
        }

        public Builder setCustomer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public Builder setCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public Builder setCustomerLastName(String customerLastName) {
            this.customerLastName = customerLastName;
            return this;
        }

        public Builder setPlateCar(String plateCar) {
            this.plateCar = plateCar;
            return this;
        }

        public Builder setVacancieName(Integer vacancieName) {
            this.vacancieName = vacancieName;
            return this;
        }

        public Builder setBlockName(char blockName) {
            this.blockName = blockName;
            return this;
        }

        public Allocation build() {
            return new Allocation(id, arrivalTime, departureTime, customerName, customerLastName, plateCar, vacancieName, blockName, user, vacancie, customer);
        }
    }
}

