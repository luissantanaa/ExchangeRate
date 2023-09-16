package com.ls.ExchangeRate.dto;

import java.time.LocalDate;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table
public class RateInfoDto {

    @Id
    String code;

    @ElementCollection
    @CollectionTable(name = "base_rate_to_final_rate_mapping", joinColumns = {
            @JoinColumn(name = "rate_code", referencedColumnName = "code") })
    @MapKeyColumn(name = "final_rate")
    @Column(name = "exhange_rate")
    Map<String, Double> exchangeRates;

    LocalDate last_updated;

    private boolean isValid() {
        boolean valid = false;

        if (last_updated.equals(LocalDate.now())) {
            valid = true;
        }

        return valid;
    }
}
