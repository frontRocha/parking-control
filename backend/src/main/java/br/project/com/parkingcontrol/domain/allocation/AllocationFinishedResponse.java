package br.project.com.parkingcontrol.domain.allocation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllocationFinishedResponse {
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
    private double total;
    private String name;
    private Integer vacancieNumber;
    private String blockName;

    public static class Builder {
        private LocalDateTime arrivalTime;
        private LocalDateTime departureTime;
        private double total;
        private String name;
        private Integer vacancieNumber;
        private String blockName;

        public Builder() {
            this.arrivalTime = null;
            this.departureTime = null;
            this.total = 0;
            this.name = null;
            this.vacancieNumber = null;
            this.blockName = null;
        }

        public LocalDateTime getArrivalTime() {
            return arrivalTime;
        }

        public LocalDateTime getDepartureTime() {
            return departureTime;
        }

        public double getTotal() {
            return total;
        }

        public String getName() {
            return name;
        }

        public Integer getVacancieNumber() {
            return vacancieNumber;
        }

        public String getBlockName() {
            return blockName;
        }

        public Builder setArrivalTime(LocalDateTime arrivalTime) {
            this.arrivalTime = arrivalTime;
            return this;
        }

        public Builder setDepartureTime(LocalDateTime departureTime) {
            this.departureTime = departureTime;
            return this;
        }

        public Builder setTotal(double total) {
            this.total = total;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setVacancieNumber(Integer vacancieNumber) {
            this.vacancieNumber = vacancieNumber;
            return this;
        }

        public Builder setBlockName(String blockName) {
            this.blockName = blockName;
            return this;
        }

        public AllocationFinishedResponse build() {
            return new AllocationFinishedResponse(arrivalTime, departureTime, total, name, vacancieNumber, blockName);
        }
    }
}
