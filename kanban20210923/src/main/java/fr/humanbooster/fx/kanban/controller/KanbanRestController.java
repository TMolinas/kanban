package fr.humanbooster.fx.kanban.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.humanbooster.fx.kanban.business.Colonne;
import fr.humanbooster.fx.kanban.business.Developpeur;
import fr.humanbooster.fx.kanban.business.Tache;
import fr.humanbooster.fx.kanban.business.TypeTache;
import fr.humanbooster.fx.kanban.service.ColonneService;
import fr.humanbooster.fx.kanban.service.DeveloppeurService;
import fr.humanbooster.fx.kanban.service.TacheService;
import fr.humanbooster.fx.kanban.service.TypeTacheService;

@RestController
@RequestMapping("api/")
public class KanbanRestController {

	private final TacheService tacheService;
	private final TypeTacheService typeTacheService;
	private final DeveloppeurService developpeurService;
	private final ColonneService colonneService;



	public KanbanRestController(TacheService tacheService, TypeTacheService typeTacheService,
			DeveloppeurService developpeurService, ColonneService colonneService) {
		super();
		this.tacheService = tacheService;
		this.typeTacheService = typeTacheService;
		this.developpeurService = developpeurService;
		this.colonneService = colonneService;
	}

	/**
	 * A) une méthode qui ajoute une tâche en précisant son intitulé et le type de tâche. La
méthode choisira au hasard un développeur et un nombre d’heures prévues entre 1
et 144. La tâche sera ajoutée sur la colonne 1 : « A faire ».
Exemple : http://localhost:8080/ taches/Corriger%20CSS/Bug

	 * @param nom
	 * @param type_tache
	 * @return
	 */
	@PostMapping("taches/{nom}/{type_tache}")
	public Tache ajouterTacheAleatoire(@PathVariable String nom, @PathVariable String type_tache) {
		Tache tache = new Tache(nom);
		TypeTache typeTache = typeTacheService.recupererTypeTacheParNom(type_tache);
		tache.setTypeTache(typeTache);
		int randomNum = ThreadLocalRandom.current().nextInt(1, 15 + 1);
		Developpeur developpeur = developpeurService.recupererDeveloppeur(Long.valueOf(randomNum));
		List<Developpeur> developpeurs = new ArrayList<>();
		developpeurs.add(developpeur);
		tache.setDeveloppeurs(developpeurs);
		int nbHeuresEstimees = ThreadLocalRandom.current().nextInt(1, 144 + 1);
		tache.setNbHeuresEstimees(nbHeuresEstimees);
		tacheService.enregistrerTache(tache);
		return tache;
		
	}
	/**
	 *  B) une méthode permettant d’obtenir toutes les informations sur une tâch
	 *  
	 * @param id
	 * @return
	 */
	@GetMapping("taches/{id}")
	public Tache recupererTache(@PathVariable Long id) {
		return tacheService.recupererTache(id);
	}

	/**
	 * C) une méthode qui permet de mettre à jour l’intitulé d’une tâche dont l’id est 
précisé dans l’URL
	 * 
	 * @param id
	 * @param nom
	 * @return
	 */

	@PutMapping("taches/{id}/{nom}")
	public Tache modifierTache(@PathVariable Long id, @PathVariable String nom) {
		Tache tache = tacheService.recupererTache(id);
		if(tache != null) {
			tache.setIntitule(nom);
			return tacheService.enregistrerTache(tache);
		}
		else
			return null;


	}

	/**  
	 *  D) une méthode permettant de supprimer une tâche en précisant son
	 * @param id
	 * @return
	 */
	@DeleteMapping("taches/{id}")
	public boolean supprimerTache(@PathVariable Long id) {
		Tache tache = tacheService.recupererTache(id);
		if(tache != null) {
			tacheService.supprimerTache(tache);
			return true;
		}
		else 
			return false;
	}
	
	@GetMapping("colonnes/{id}/taches")
	public Page<Tache> recupererTacheParColonne(@PathVariable Long id,
			@PageableDefault(size=10, page=0) Pageable page) {
		Colonne colonne = colonneService.recupererColonne(id);
		Page<Tache> tachesARecuperer = tacheService.recupererTaches(colonne, page);
		return tachesARecuperer;
	}
	

	/** F) une méthode permettant d’obtenir les tâches ayant le statut « à faire » et confiées 
à un développeur en particulie
	 * 
	 * @param id
	 * @return
	 */

	@GetMapping("developpeurs/{id}/tacheAFaire")
	public List<Tache> recupererTachesAFaire(@PathVariable Long id) {
		Developpeur developpeur = developpeurService.recupererDeveloppeur(id);

		List<Tache> taches = tacheService.recupererTaches();
		List<Tache> tachesARenvoyer = new ArrayList<>();
		for(Tache tache: taches) {
			if (tache.getColonne().getNom().equals("A faire")) {

				for(Developpeur developpeurATester: tache.getDeveloppeurs()) {

					if(developpeur == developpeurATester) {
						tachesARenvoyer.add(tache);
					}
				}

			}
		}
		return tachesARenvoyer;
	}

	/**
	 *  G) une méthode permettant d’obtenir toutes les tâches dont l’intitulé contient le mot
précisé dans l’URL
	 * @param nom
	 * @return
	 */
	@GetMapping("taches")
	public List<Tache> recupererTacheParNom(@RequestParam(value ="nom", defaultValue="") String nom) {
		return tacheService.recupererTacheParNom(nom);
	}

	/**
	 *  H) une méthode permettant de déterminer le total des heures prévues pour les 
tâchées créées entre deux dates données en paramètre
	 * @param dateDebut
	 * @param dateFin
	 * @return
	 */
	@GetMapping("totalHeuresPrevues")
	public int recupererLeNbHeuresEntreDeuxTaches(
			@DateTimeFormat(pattern = "dd-MM-yyyy") @RequestParam(name = "dateDebut", required = false) Date dateDebut,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "dateFin", required = false) Date dateFin) {
		List<Tache> taches = tacheService.recupererTachesBetween(dateDebut, dateFin);
		int nbHeures = 0;
		for(Tache tache: taches) {
			nbHeures += tache.getNbHeuresEstimees();
		}
		return nbHeures;
	}
	
	/**
	 *  I) une méthode permettant de supprimer toutes les tâches d’une colon
	 * @param id_colonne
	 * @return
	 */
	@DeleteMapping("colonne/{id_colonne}")
	public boolean supprimerTachesParColonne(@PathVariable Long id_colonne) {
		boolean isDelete = false;
		Colonne colonne = colonneService.recupererColonne(id_colonne);
		List<Tache> taches = tacheService.recupererTaches();
		for(Tache tache: taches) {
			if(tache.getColonne() == colonne) {
				tacheService.supprimerTache(tache);
				isDelete = true;
			}
		}
		return isDelete;
	}
	
}
