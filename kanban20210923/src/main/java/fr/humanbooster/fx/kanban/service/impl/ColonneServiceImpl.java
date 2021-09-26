package fr.humanbooster.fx.kanban.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.humanbooster.fx.kanban.business.Colonne;
import fr.humanbooster.fx.kanban.dao.ColonneDao;
import fr.humanbooster.fx.kanban.service.ColonneService;

@Service
public class ColonneServiceImpl implements ColonneService {

	// Nous avons d'un objet de type DAO (la couche en dessous)
	private final ColonneDao colonneDao;
	
	public ColonneServiceImpl(ColonneDao colonneDao) {
		super();
		this.colonneDao = colonneDao;
	}


	@Override
	public Colonne ajouterColonne(String nom) {
		Colonne colonne = new Colonne(nom);
		// On confie l'objet colonne à la DAO
		colonneDao.save(colonne);
		return colonne;
	}


	@Override
	public List<Colonne> recupererColonnes() {
		return colonneDao.findAll();
	}


	@Override
	public Colonne recupererColonne(Long id_colonne) {
		return colonneDao.findById(id_colonne).orElse(null);
	}

}
