package br.project.com.parkingcontrol.domain.history;

import br.project.com.parkingcontrol.domain.user.User;
import br.project.com.parkingcontrol.domain.vacancie.Vacancie;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Entity(name="HISTORY")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false, unique = false)
    private LocalDateTime arrivalTime;
    @Column(nullable = false, unique = false)
    private LocalDateTime departureTime;
    @Column(nullable = false, unique = false)
    private String customerName;
    @Column(nullable = false, unique = false)
    private String customerLastName;
    @Column(nullable = false, unique = false)
    private String plateCar;
    @Column(nullable = false, unique = false)
    private Integer vacancieName;
    @Column(nullable = false, unique = false)
    private String blockName;
    @Column(nullable = false, unique = false)
    private double total;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

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

    public double getTotal() {
        return total;
    }

    public User getUser() {
        return user;
    }

    public static class Builder {
        private UUID id;
        private LocalDateTime arrivalTime;
        private LocalDateTime departureTime;
        private String customerName;
        private String customerLastName;
        private String plateCar;
        private Integer vacancieName;
        private String blockName;
        private double total;
        private User user;

        public Builder() {
            this.id = null;
            this.arrivalTime = null;
            this.departureTime = null;
            this.customerName = null;
            this.customerLastName = null;
            this.plateCar = null;
            this.vacancieName = null;
            this.blockName = null;
            this.total = 0;
            this.user = null;
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

        public Builder setTotal(double total) {
            this.total = total;
            return this;
        }

        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public History build() {
            return new History(id, arrivalTime, departureTime, customerName, customerLastName, plateCar, vacancieName, blockName, total, user);
        }
    }
}
