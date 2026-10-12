package com.reservacanchas.repository;

import com.reservacanchas.model.CamaraCancha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CamaraCanchaRepository extends JpaRepository<CamaraCancha, Long> {

    Optional<CamaraCancha> findByCodigo(String codigo);

    Optional<CamaraCancha> findFirstByCanchaIdAndActivoTrue(Long canchaId);

    List<CamaraCancha> findByCanchaId(Long canchaId);
}

