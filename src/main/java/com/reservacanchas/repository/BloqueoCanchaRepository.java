package com.reservacanchas.repository;

import com.reservacanchas.model.BloqueoCancha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloqueoCanchaRepository extends JpaRepository<BloqueoCancha, Long> {

    List<BloqueoCancha> findByCanchaId(Long canchaId);
}
