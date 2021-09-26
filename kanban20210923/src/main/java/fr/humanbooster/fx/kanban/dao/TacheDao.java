package fr.humanbooster.fx.kanban.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import fr.humanbooster.fx.kanban.business.Colonne;
import fr.humanbooster.fx.kanban.business.Tache;

public interface TacheDao extends JpaRepository<Tache, Long> {

	List<Tache> findByIntituleContaining(String nom);

	List<Tache> findByDateCreationBetween(Date dateDebut, Date dateFin);

	Page<Tache> findAllByColonne(Colonne colonne, Pageable page);

	
}