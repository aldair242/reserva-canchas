package com.reservacanchas.repository;

import com.reservacanchas.model.Cancha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CanchaRepository extends JpaRepository<Cancha, Long> {

    List<Cancha> findBySedeId(Long sedeId);

    @Query("SELECT c FROM Cancha c WHERE " +
           "(:sedeId IS NULL OR c.sede.id = :sedeId) AND " +
           "(:tipo IS NULL OR TYPE(c) = :tipo)")
    List<Cancha> buscarConFiltros(@Param("sedeId") Long sedeId,
                                  @Param("tipo") Class<? extends Cancha> tipo);
}
