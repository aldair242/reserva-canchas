package com.reservacanchas.repository;

import com.reservacanchas.model.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {

    Optional<Dispositivo> findByCodigo(String codigo);

    List<Dispositivo> findByCanchaId(Long canchaId);

    boolean existsByCodigo(String codigo);
}

