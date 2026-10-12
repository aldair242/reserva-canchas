package com.reservacanchas.repository;

import com.reservacanchas.model.Clip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClipRepository extends JpaRepository<Clip, Long> {

    List<Clip> findByPartidoIdAndVisibleTrue(Long partidoId);

    List<Clip> findByPartidoId(Long partidoId);

    Optional<Clip> findByIdAndVisibleTrue(Long id);

    Optional<Clip> findByGrabacionId(Long grabacionId);
}

