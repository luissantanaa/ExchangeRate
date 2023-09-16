package com.ls.ExchangeRate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ls.ExchangeRate.dto.RateInfoDto;

public interface RateInfoRepository extends JpaRepository<RateInfoDto, String> {

}
