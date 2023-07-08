package br.project.com.parkingcontrol.domain.allocation;

import br.project.com.parkingcontrol.domain.customer.Customer;
import br.project.com.parkingcontrol.domain.user.User;
import br.project.com.parkingcontrol.domain.vacancie.Vacancie;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
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
    @Column(nullable = false, unique = false)
    private LocalDateTime arrivalTime;
    @Column(nullable = true, unique = false)
    private LocalDateTime departureTime;
    @Column(nullable = false, unique = false)
    private String customerName;
    @Column(nullable = false, unique = true)
    private String customerLastName;
    @Column(nullable = false, unique = true)
    private String plateCar;
    @Column(nullable = false, unique = false)
    private Integer vacancieName;
    @Column(nullable = false, unique = false)
    private String blockName;

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

    public String getBlockName() {
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

    private static void fieldValidation(String customerName, String customerLastName, String plateCar) {
        Preconditions.checkArgument(customerName.length() > 1, "customer name cannot be null");
        Preconditions.checkArgument(customerLastName.length() > 1, "customer last name cannot be null");
        Preconditions.checkArgument(plateCar.length() == 8, "The car plate must have exactly 8 characters.");
        Preconditions.checkArgument(plateCar.length() < 9, "The car plate cannot have more than 8 characters.");
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
        private String blockName;

        public Builder() {
            this.id = null;
            this.arrivalTime = null;
            this.departureTime = null;
            this.customerName = null;
            this.customerLastName = null;
            this.plateCar = null;
            this.vacancieName = null;
            this.blockName = null;
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

        public Builder setBlockName(String blockName) {
            this.blockName = blockName;
            return this;
        }

        public Allocation build() {
            fieldValidation(customerName, customerLastName, plateCar);
            return new Allocation(id, arrivalTime, departureTime, customerName, customerLastName, plateCar, vacancieName, blockName, user, vacancie, customer);
        }
    }
}

