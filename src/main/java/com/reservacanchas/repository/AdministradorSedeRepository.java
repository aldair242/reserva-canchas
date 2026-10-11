package com.reservacanchas.repository;

import com.reservacanchas.model.AdministradorSede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorSedeRepository extends JpaRepository<AdministradorSede, Long> {
}
