package com.reservacanchas.repository;

import com.reservacanchas.model.Invitado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitadoRepository extends JpaRepository<Invitado, Long> {
}
