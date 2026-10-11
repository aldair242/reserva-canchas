package com.reservacanchas.repository;

import com.reservacanchas.model.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Long> {

    List<Sede> findByAdministradorSedeId(Long administradorSedeId);
}
