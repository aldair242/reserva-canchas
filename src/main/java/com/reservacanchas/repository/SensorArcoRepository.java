package com.reservacanchas.repository;

import com.reservacanchas.model.SensorArco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorArcoRepository extends JpaRepository<SensorArco, Long> {

    Optional<SensorArco> findByCodigo(String codigo);

    List<SensorArco> findByCanchaId(Long canchaId);
}

