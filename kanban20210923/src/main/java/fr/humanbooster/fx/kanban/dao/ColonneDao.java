package fr.humanbooster.fx.kanban.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.humanbooster.fx.kanban.business.Colonne;

public interface ColonneDao extends JpaRepository<Colonne, Long> {

}
