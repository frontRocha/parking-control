package br.project.com.parkingcontrol.domain.customer;

import br.project.com.parkingcontrol.domain.allocation.Allocation;
import br.project.com.parkingcontrol.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false, unique = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String lastName;
    @Column(nullable = false, unique = true, length = 8)
    private String plateCar;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "customer")
    private Allocation allocation;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPlateCar() {
        return plateCar;
    }

    public User getUser() {
        return user;
    }

    public Allocation getAllocation() {
        return allocation;
    }

    public static void validarCampos(String name, String lastName, String plateCar) {
        Preconditions.checkArgument(name.length() > 1, "customer name cannot be null");
        Preconditions.checkArgument(lastName.length() > 1, "customer last name cannot be null");
        Preconditions.checkArgument(plateCar.length() == 8, "The car plate must have exactly 8 characters.");
        Preconditions.checkArgument(plateCar.length() < 9, "The car plate cannot have more than 8 characters.");
    }

    public static class Builder {
        private UUID id;
        private String name;
        private String lastName;
        private String plateCar;
        private Allocation allocation;
        private User user;

        public Builder() {
            this.id = null;
            this.name = null;
            this.lastName = null;
            this.plateCar = null;
            this.allocation = null;
            this.user = null;
        }

        public Builder setId(UUID id) {
            this.id = id;
            return this;
        }

        public UUID getId() {
            return id;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public String getName() {
            return name;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public String getLastName() {
            return lastName;
        }

        public Builder setPlateCar(String plateCar) {
            this.plateCar = plateCar;
            return this;
        }

        public String getPlateCar() {
            return plateCar;
        }

        public Allocation getAllocation() {
            return allocation;
        }

        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public Builder setAllocation(Allocation allocation) {
            this.allocation = allocation;
            return this;
        }

        public Customer build() {
            validarCampos(name, lastName, plateCar);
            return new Customer(id, name, lastName, plateCar, user, allocation);
        }
    }
}
