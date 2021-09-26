package fr.humanbooster.fx.kanban.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.humanbooster.fx.kanban.business.Colonne;
import fr.humanbooster.fx.kanban.business.Developpeur;
import fr.humanbooster.fx.kanban.business.Tache;
import fr.humanbooster.fx.kanban.dao.ColonneDao;
import fr.humanbooster.fx.kanban.dao.DeveloppeurDao;
import fr.humanbooster.fx.kanban.dao.TacheDao;
import fr.humanbooster.fx.kanban.dao.TypeTacheDao;
import fr.humanbooster.fx.kanban.service.TacheService;

@Service
public class TacheServiceImpl implements TacheService {

	private final TacheDao tacheDao;

	private final ColonneDao colonneDao;

	private final DeveloppeurDao developpeurDao;

	private final TypeTacheDao typeTacheDao;

	private static Random random = new Random();

	public TacheServiceImpl(TacheDao tacheDao, ColonneDao colonneDao, DeveloppeurDao developpeurDao,
			TypeTacheDao typeTacheDao) {
		super();
		this.tacheDao = tacheDao;
		this.colonneDao = colonneDao;
		this.developpeurDao = developpeurDao;
		this.typeTacheDao = typeTacheDao;
	}

	@Override
	public Tache ajouterTache(String intitule) {
		Tache tache = new Tache(intitule);
		tache.setNbHeuresEstimees(1 + random.nextInt(100));
		tache.setNbHeuresReelles(1 + random.nextInt(100));
		tache.setColonne(colonneDao.findAll().get(0));
		tache.setTypeTache(typeTacheDao.findAll().get(random.nextInt((int) typeTacheDao.count())));
		List<Developpeur> developpeurs = new ArrayList<>();
		developpeurs.add(developpeurDao.findAll().get(random.nextInt((int) developpeurDao.count())));
		tache.setDeveloppeurs(developpeurs);
		tacheDao.save(tache);
		return tache;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Tache> recupererTaches() {

		return tacheDao.findAll();
	}

	/**
	 * Cette méthode récupère une tâche dont l'id est donné en paramètre
	 */
	@Override
	@Transactional(readOnly = true)
	public Tache recupererTache(Long id) {
		return tacheDao.findById(id).orElse(null);
	}

	@Override
	public void supprimerTache(Tache tache) {
		tacheDao.delete(tache);
	}

	@Override
	public Tache enregistrerTache(Tache tache) {
		return tacheDao.save(tache);
	}

	@Override
	public Page<Tache> recupererTaches(Pageable pageable) {
		return tacheDao.findAll(pageable);
	}

	@Override
	public List<Tache> recupererTacheParNom(String nom) {
		return tacheDao.findByIntituleContaining(nom);
	}

	@Override
	public List<Tache> recupererTachesBetween(Date dateDebut, Date dateFin) {
		return tacheDao.findByDateCreationBetween(dateDebut, dateFin);
	}

	@Override
	public Page<Tache> recupererTaches(Colonne colonne, Pageable page) {
		return tacheDao.findAllByColonne(colonne, page);
	}

}