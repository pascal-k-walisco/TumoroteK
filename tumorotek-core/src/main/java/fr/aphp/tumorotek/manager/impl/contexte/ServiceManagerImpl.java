/** 
 * Copyright ou © ou Copr. Ministère de la santé, FRANCE (01/01/2011)
 * dsi-projet.tk@aphp.fr
 * 
 * Ce logiciel est un programme informatique servant à la gestion de 
 * l'activité de biobanques. 
 *
 * Ce logiciel est régi par la licence CeCILL soumise au droit français
 * et respectant les principes de diffusion des logiciels libres. Vous 
 * pouvez utiliser, modifier et/ou redistribuer ce programme sous les 
 * conditions de la licence CeCILL telle que diffusée par le CEA, le 
 * CNRS et l'INRIA sur le site "http://www.cecill.info". 
 * En contrepartie de l'accessibilité au code source et des droits de   
 * copie, de modification et de redistribution accordés par cette 
 * licence, il n'est offert aux utilisateurs qu'une garantie limitée. 
 * Pour les mêmes raisons, seule une responsabilité restreinte pèse sur 
 * l'auteur du programme, le titulaire des droits patrimoniaux et les 
 * concédants successifs.
 *
 * A cet égard  l'attention de l'utilisateur est attirée sur les 
 * risques associés au chargement,  à l'utilisation,  à la modification 
 * et/ou au  développement et à la reproduction du logiciel par 
 * l'utilisateur étant donné sa spécificité de logiciel libre, qui peut 
 * le rendre complexe à manipuler et qui le réserve donc à des 	
 * développeurs et des professionnels  avertis possédant  des 
 * connaissances  informatiques approfondies.  Les utilisateurs sont 
 * donc invités à charger  et  tester  l'adéquation  du logiciel à leurs
 * besoins dans des conditions permettant d'assurer la sécurité de leurs
 * systèmes et ou de leurs données et, plus généralement, à l'utiliser 
 * et l'exploiter dans les mêmes conditions de sécurité. 
 *	
 * Le fait que vous puissiez accéder à cet en-tête signifie que vous 
 * avez pris connaissance de la licence CeCILL, et que vous en avez 
 * accepté les termes. 
 **/
package fr.aphp.tumorotek.manager.impl.contexte;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Validator;

import fr.aphp.tumorotek.dao.contexte.CollaborateurDao;
import fr.aphp.tumorotek.dao.contexte.CoordonneeDao;
import fr.aphp.tumorotek.dao.contexte.EtablissementDao;
import fr.aphp.tumorotek.dao.contexte.ServiceDao;
import fr.aphp.tumorotek.dao.qualite.OperationTypeDao;
import fr.aphp.tumorotek.manager.context.CollaborateurManager;
import fr.aphp.tumorotek.manager.context.CoordonneeManager;
import fr.aphp.tumorotek.manager.context.ServiceManager;
import fr.aphp.tumorotek.manager.exception.DoublonFoundException;
import fr.aphp.tumorotek.manager.exception.ObjectReferencedException;
import fr.aphp.tumorotek.manager.exception.ObjectUsedException;
import fr.aphp.tumorotek.manager.exception.RequiredObjectIsNullException;
import fr.aphp.tumorotek.manager.impl.coeur.CreateOrUpdateUtilities;
import fr.aphp.tumorotek.manager.qualite.OperationManager;
import fr.aphp.tumorotek.manager.validation.BeanValidator;
import fr.aphp.tumorotek.manager.validation.contexte.CoordonneeValidator;
import fr.aphp.tumorotek.manager.validation.contexte.ServiceValidator;
import fr.aphp.tumorotek.model.cession.Cession;
import fr.aphp.tumorotek.model.cession.Contrat;
import fr.aphp.tumorotek.model.coeur.prelevement.LaboInter;
import fr.aphp.tumorotek.model.coeur.prelevement.Prelevement;
import fr.aphp.tumorotek.model.contexte.Banque;
import fr.aphp.tumorotek.model.contexte.Collaborateur;
import fr.aphp.tumorotek.model.contexte.Coordonnee;
import fr.aphp.tumorotek.model.contexte.Etablissement;
import fr.aphp.tumorotek.model.contexte.Service;
import fr.aphp.tumorotek.model.qualite.Operation;
import fr.aphp.tumorotek.model.stockage.Conteneur;
import fr.aphp.tumorotek.model.utilisateur.Utilisateur;
import fr.aphp.tumorotek.utils.Utils;

/**
 * 
 * Implémentation du manager du bean de domaine Service.
 * Interface créée le 01/10/09.
 * 
 * @author Pierre Ventadour
 * @version 2.0.10.6
 *
 */
public class ServiceManagerImpl implements ServiceManager {
	
	private Log log = LogFactory.getLog(ServiceManager.class);
	
	private ServiceDao serviceDao;
	private CollaborateurDao collaborateurDao;
	private EtablissementDao etablissementDao;
	private CoordonneeDao coordonneeDao;
	private CoordonneeManager coordonneeManager;
	private CollaborateurManager collaborateurManager;
	private ServiceValidator serviceValidator;
	private CoordonneeValidator coordonneeValidator;
	private OperationManager operationManager;
	private OperationTypeDao operationTypeDao;
	
	public void setServiceDao(ServiceDao sDao) {
		this.serviceDao = sDao;
	}

	public void setCollaborateurDao(CollaborateurDao cDao) {
		this.collaborateurDao = cDao;
	}

	public void setEtablissementDao(EtablissementDao eDao) {
		this.etablissementDao = eDao;
	}

	public void setCoordonneeDao(CoordonneeDao cDao) {
		this.coordonneeDao = cDao;
	}

	public void setServiceValidator(ServiceValidator sValidator) {
		this.serviceValidator = sValidator;
	}
	
	public void setCoordonneeManager(CoordonneeManager cManager) {
		this.coordonneeManager = cManager;
	}

	public void setCoordonneeValidator(CoordonneeValidator cValidator) {
		this.coordonneeValidator = cValidator;
	}

	public void setCollaborateurManager(CollaborateurManager cManager) {
		this.collaborateurManager = cManager;
	}

	public void setOperationManager(OperationManager oManager) {
		this.operationManager = oManager;
	}

	public void setOperationTypeDao(OperationTypeDao oDao) {
		this.operationTypeDao = oDao;
	}

	/**
	 * Recherche un Service dont l'identifiant est passé en paramètre.
	 * @param serviceId Identifiant du Service que l'on recherche.
	 * @return Un Service.
	 */
	public Service findByIdManager(Integer serviceId) {
		return serviceDao.findById(serviceId);
	}

	/**
	 * Recherche tous les Services présents dans la base.
	 * @return Liste de Services.
	 */
	public List<Service> findAllObjectsManager() {
		return serviceDao.findAll();
	}
	
	
	
	@Override
	public Long	findCountByEtablissementIdManager(Etablissement eta) {
		if(eta != null){
			return serviceDao.findCountByEtablissementId(eta.getEtablissementId()).get(0);	
		}
		else{
			return new Long(0);
		}
	}
	
	/**
	 * Recherche tous les Services présents dans la base.
	 * @return Liste ordonnée de Services.
	 */
	public List<Service> findAllObjectsWithOrderManager() {
		return serviceDao.findByOrder();
	}
	
	/**
	 * Recherche tous les Services présents dans la base.
	 * @return Liste ordonnée de Services.
	 */
	public List<Service> findAllActiveObjectsWithOrderManager() {
		return serviceDao.findByArchiveWithOrder(false);
	}
	
	
	@Override
	public List<Service> findByVilleLikeManager(String ville) {

		log.debug("Recherche Service par ville : " + ville);
		if (ville != null) {
			//ville = "%" + ville + "%";
			return serviceDao.findByVille(ville);
		} else {
			return new ArrayList<Service>();
		}
	}
	
	/**
	 * Recherche une liste de Services dont le nom commence comme
	 * celui passé en paramètre.
	 * @param nom Nom pour lequel on recherche des Services.
	 * @param exactMatch True si l'on souhaite seulement récuéprer les matchs
	 * exactes.
	 * @return Liste de Services.
	 */
	@Override
	public List<Service> findByNomLikeManager(String nom, boolean exactMatch) {
		
		log.debug("Recherche Service par nom : " 
				+ nom + " exactMatch " + String.valueOf(exactMatch));
		if (nom != null) {
			if (!exactMatch) {
				nom = nom + "%";
			}
			return serviceDao.findByNom(nom);
		} else {
			return new ArrayList<Service>();
		}
	}
	
	@Override
	public List<Service> findByNomLikeBothSideManager(String nom) {
		
		log.debug("Recherche Service par nom : " + nom);
		if (nom != null) {
			nom = "%" + nom + "%";
			return serviceDao.findByNom(nom);
		} else {
			return new ArrayList<Service>();
		}
	}
	
	/**
	 * Recherche les collaborateurs liés au service passé en paramètre.
	 * @param service Service pour lequel on recherche des
	 * collaborateurs.
	 * @return Liste de Collaborateurs.
	 */
	public Set<Collaborateur> getCollaborateursManager(Service service) {
		service = serviceDao.mergeObject(service);
		Set<Collaborateur> collabs = service.getCollaborateurs();
		collabs.size();
		
		return collabs;
	}
	
	/**
	 * Recherche les collaborateurs liés au service passé en paramètre.
	 * @param service Service pour lequel on recherche des
	 * collaborateurs.
	 * @return Liste ordonnée de Collaborateurs.
	 */
	public List<Collaborateur> getCollaborateursWithOrderManager(
			Service service) {
		if (service != null && service.getServiceId() != null) {
			return collaborateurDao.findByServiceIdWithOrder(
					service.getServiceId());
		} else {
			return new ArrayList<Collaborateur>();
		}
	}
	
	/**
	 * Recherche les collaborateurs non archivés liés au service passé 
	 * en paramètre.
	 * @param service Service pour lequel on recherche des
	 * collaborateurs.
	 * @return Liste ordonnée de Collaborateurs.
	 */
	public List<Collaborateur> getActiveCollaborateursWithOrderManager(
			Service service) {
		if (service != null && service.getServiceId() != null) {
			return collaborateurDao.findByServiceIdArchiveWithOrder(
					service.getServiceId(), false);
		} else {
			return new ArrayList<Collaborateur>();
		}
	}

	/**
	 * Recherche les doublons du Service passé en paramètre.
	 * @param service Service pour lequel on cherche des doublons.
	 * @return True s'il existe des doublons.
	 */
	@Override
	public Boolean findDoublonManager(Service service) {
		
		if (service.getServiceId() == null) {
			return serviceDao.findAll().contains(service);
		} else {
			return serviceDao.findByExcludedId(
					service.getServiceId()).contains(service);
		}
		
	}

	/**
	 * Persist une instance de Service dans la base de données.
	 * @param service Nouvelle instance de l'objet à créer.
	 * @param coordonnee Coordonnee associée au service.
	 * @param etablissement Etablissement associé.
	 * @param collaborateurs Collaborateurs associés au service.
	 */
	@Override
	public void createObjectManager(Service service, Coordonnee coordonnee,
			Etablissement etablissement, List<Collaborateur> collaborateurs,
			Utilisateur utilisateur,
			boolean cascadeArchive) {
		
		// On vérifie que l'établissement n'est pas null. Si c'est le 
		// cas on envoie une exception
		if (etablissement == null) {
			log.warn("Objet obligatoire Etablissement manquant lors de " 
					+  "la creation " +  "d'un objet Service");
			throw new RequiredObjectIsNullException(
					"Service", "creation", "Etablissement");
		} else {
			service.setEtablissement(
					etablissementDao.mergeObject(etablissement));
		}
		
		if (findDoublonManager(service)) {
			log.warn("Doublon lors de la creation de l'objet Service : " 
					+ service.toString());
			throw new DoublonFoundException("Service", "creation");
		} else {
			BeanValidator.validateObject(
					service, new Validator[]{serviceValidator});
		
			if (coordonnee != null) {
				BeanValidator.validateObject(
						coordonnee, new Validator[]{coordonneeValidator});
				if (coordonnee.getCoordonneeId() == null) {
					coordonneeManager.createObjectManager(coordonnee, null);
				} else {
					coordonneeManager.updateObjectManager(
							coordonnee, null, true);
				}
			} // else {
			//	service.setCoordonnee(null);
			// }
			service.setCoordonnee(coordonneeDao.mergeObject(coordonnee));
		
			serviceDao.createObject(service);
			
			if (collaborateurs != null) {
				updateCollaborateurs(service, collaborateurs);
			}
			
			if (cascadeArchive) {
				archiveCollaborateurs(service, collaborateurs,
						utilisateur);
			}
			
			log.info("Enregistrement de l'objet Service : " 
					+ service.toString());			
			
			//Enregistrement de l'operation associee
			Operation creationOp = new Operation();
			creationOp.setDate(Utils.getCurrentSystemCalendar());
			operationManager.createObjectManager(creationOp, utilisateur,
					operationTypeDao.findByNom("Creation").get(0), 
															service);
		}
		
	}
	
	/**
	 * Sauvegarde les modifications apportées à un objet persistant.
	 * @param service Objet à mettre à jour dans la base.
	 * @param coordonnee Coordonnee associée au service.
	 * @param etablissement Etablissement associé.
	 * @param collaborateurs Collaborateurs associés au service.
	 */
	@Override
	public void updateObjectManager(Service service, Coordonnee coordonnee,
			Etablissement etablissement, List<Collaborateur> collaborateurs,
			Utilisateur utilisateur,
			boolean cascadeArchive, boolean doValidation) {
		
		// On vérifie que l'établissement n'est pas null. Si c'est le 
		// cas on envoie une exception
		if (etablissement == null) {
			log.warn("Objet obligatoire Etablissement manquant lors de " 
					+  "la modification " +  "d'un objet Service");
			throw new RequiredObjectIsNullException(
					"Service", "modification", "Etablissement");
		} else {
			service.setEtablissement(
					etablissementDao.mergeObject(etablissement));
		}
		
		if (findDoublonManager(service)) {
			log.warn("Doublon lors de la modif de l'objet Service : " 
					+ service.toString());
			throw new DoublonFoundException("Service", "modification");
		} else {
			if (doValidation) {
				BeanValidator.validateObject(
						service, new Validator[]{serviceValidator});
			}
		
			if (coordonnee != null) {
				if (doValidation) {
					BeanValidator.validateObject(
						coordonnee, new Validator[]{coordonneeValidator});
				}
				if (coordonnee.getCoordonneeId() == null) {
					coordonneeManager.createObjectManager(coordonnee, null);
				} else {
					coordonneeManager.updateObjectManager(
							coordonnee, null, doValidation);
				}
			} // else {
			//	service.setCoordonnee(null);
			//}
			service.setCoordonnee(coordonneeDao.mergeObject(coordonnee));

			serviceDao.updateObject(service);
			
			if (collaborateurs != null) {
				updateCollaborateurs(service, collaborateurs);
			}
			
			if (cascadeArchive) {
				archiveCollaborateurs(service, 
					new ArrayList<Collaborateur>(
							getCollaborateursManager(service)), 
						utilisateur);
			}
			
			log.info("Modification de l'objet Service " 
					+ service.toString());			
			
			//Enregistrement de l'operation associee
			Operation creationOp = new Operation();
			creationOp.setDate(Utils.getCurrentSystemCalendar());
			operationManager.createObjectManager(creationOp, utilisateur,
					operationTypeDao.findByNom("Modification").get(0), 
															service);
		}
		
	}
	
	@Override
	public boolean isReferencedObjectManager(Service srv) {
		Service service = serviceDao.mergeObject(srv);
		
		Iterator<Collaborateur> collabsIt = service
							.getCollaborateurs().iterator();
		while (collabsIt.hasNext()) {
			if (collaborateurManager
				.isReferencedObjectManager(collabsIt.next())) {
					return true;
			}
		}
		
		return !service.getBanquesPossedees().isEmpty()
				|| !service.getPrelevements().isEmpty()
				|| !service.getLaboInters().isEmpty()
				|| !service.getCessions().isEmpty()
				|| !service.getContrats().isEmpty()
				|| !service.getConteneurs().isEmpty();
	}

	@Override
	public boolean isUsedObjectManager(Service srv) {
		return !getCollaborateursManager(srv).isEmpty();
	}

	@Override
	public void removeObjectManager(Service service, String comments,
													Utilisateur user) {
		
		if (service != null) {
			if (!isUsedObjectManager(service) 
					&& !isReferencedObjectManager(service)) {
				
				serviceDao.removeObject(service.getServiceId());
				
				//Supprime operations associes
				CreateOrUpdateUtilities
					.removeAssociateOperations(service, operationManager, 
														comments, user);
				log.info("Suppression de l'objet Service : " 
												+ service.toString());
			} else {
				if (!isReferencedObjectManager(service)) { // suppr possible
					log.warn("Objet utilisé lors de la suppression de l'objet " 
						+ "Service : " + service.toString());
					throw new ObjectUsedException("service.deletion." 
													+ "isUsedCascade", true);
				} else {
					log.warn("Objet référencé lors de la suppression " 
							+ "de l'objet Service : " 
							+ service.toString());
					throw new ObjectReferencedException("service"
									+ ".deletion.isReferencedCascade", true);
				}
			}
		}
		
	}
	
	/**
	 * Cette méthode met à jour les associations entre un service et
	 * une liste de collabs.
	 * @param service Service pour lequel on veut mettre à jour
	 * les associations.
	 * @param collabs Liste des Collaborateurs que l'on veut associer au 
	 * service.
	 */
	public void updateCollaborateurs(
			Service service, List<Collaborateur> collabs) {
		
		Service serv = serviceDao.mergeObject(service);
		
		Iterator<Collaborateur> it = serv.getCollaborateurs().iterator();
		List<Collaborateur> collabsToRemove = new ArrayList<Collaborateur>();
		// on parcourt les collabs qui sont actuellement associés
		// au service
		while (it.hasNext()) {
			Collaborateur tmp = it.next();
			// si un collab n'est pas dans la nouvelle liste, on
			// le conserve afin de le retirer par la suite
			if (!collabs.contains(tmp)) {
				collabsToRemove.add(tmp);
			}
		}
		
		// on parcourt la liste la liste des collabs à retirer de
		// l'association
		for (int i = 0; i < collabsToRemove.size(); i++) {
			Collaborateur collab = 
				collaborateurDao.mergeObject(collabsToRemove.get(i));
			// on retire le collab de chaque coté de l'association
			serv.getCollaborateurs().remove(collab);
			collab.getServices().remove(serv);
			
			log.debug("Suppression de l'association entre le collaborateur : " 
					+ collab.toString() + " et le service : "
					+ serv.toString());
		}
		
		// on parcourt la nouvelle liste de collabs
		for (int i = 0; i < collabs.size(); i++) {
			// si un collab n'était pas associé au service
			if (!serv.getCollaborateurs().contains(collabs.get(i))) {
				// on ajoute le collab des deux cotés de l'association
				serv.getCollaborateurs().add(
						collaborateurDao.mergeObject(collabs.get(i)));
				collaborateurDao.mergeObject(
						collabs.get(i)).getServices().add(serv);
				
				log.debug("Ajout de l'association entre le service : " 
						+ serv.toString() + " et le collaborateur : "
						+ collabs.get(i).toString());
			}
		}
	}
	
	public void archiveCollaborateurs(Service service, List<Collaborateur> 
		collaborateurs, Utilisateur utilisateur) {
		if (service != null) {
			// pour chaque collab
			for (int i = 0; i < collaborateurs.size(); i++) {
				Collaborateur collab = collaborateurs.get(i);
				// si le service est archivé et que le collab peut l'être
				if (service.getArchive()) {
					if (collaborateurManager.isArchivableManager(
							collab, service)) {
						// recup des coords du collab
						Iterator<Coordonnee> itC = collaborateurManager
							.getCoordonneesManager(collab).iterator();
						List<Coordonnee> coords = new ArrayList<Coordonnee>();
						while (itC.hasNext()) {
							coords.add(itC.next());
						}
						// MAJ du collab
						collab.setArchive(true);
						collaborateurManager.updateObjectManager(collab, 
								collab.getTitre(), 
								collab.getEtablissement(), 
								collab.getSpecialite(), 
								null, 
								coords,
								utilisateur,
								false);
					}
				} else {
					// si le service n'est pas archivé et que le collab l'est
					if (collab.getArchive()) {
						// recup des coords du collab
						Iterator<Coordonnee> itC = collaborateurManager
							.getCoordonneesManager(collab).iterator();
						List<Coordonnee> coords = new ArrayList<Coordonnee>();
						while (itC.hasNext()) {
							coords.add(itC.next());
						}
						// MAJ du collab
						collab.setArchive(false);
						collaborateurManager.updateObjectManager(collab, 
								collab.getTitre(), 
								collab.getEtablissement(), 
								collab.getSpecialite(), 
								null, 
								coords,
								utilisateur,
								false);
					}
				}
			}
		}
	}

	@Override
	public void removeObjectCascadeManager(Service srv, String comments,
			Utilisateur user) {
		if (srv != null) {
			log.info("Suppression en cascade depuis objet Service " 
													+ srv.toString());
			
			//Supprime les collaborateurs
			List<Collaborateur> collabs = new ArrayList<Collaborateur>();
			collabs.addAll(getCollaborateursManager(srv));
			Iterator<Collaborateur> collabsIt = collabs.iterator();
			
			while (collabsIt.hasNext()) {
				collaborateurManager
					.removeObjectCascadeManager(collabsIt.next(), srv, 
														comments, user);
			}
			srv.setCollaborateurs(new HashSet<Collaborateur>());
			
			removeObjectManager(srv, comments, user);
		}
		
	}
	
	
	
	@Override
	public void fusionServiceManager(int idActif, int idPassif, String comments,
			Utilisateur user){
		
		Service sActif = serviceDao.findById(idActif);
		Service sPassif = serviceDao.findById(idPassif);
				
		if (sActif != null && sPassif != null && sActif != sPassif) {
			
			//BANQUES
			Set<Banque> banquesP = new 
					HashSet<Banque>(sPassif.getBanquesPossedees());

			for (Banque bq : banquesP) {
				bq.setProprietaire(sActif);
				sActif.getBanquesPossedees().add(bq);
			}
			sPassif.getBanquesPossedees().clear();
			
			//CONTRATS
			Set<Contrat> contratsP = new 
						HashSet<Contrat>(sPassif.getContrats());

			for (Contrat cont : contratsP) {
				cont.setService(sActif);
				sActif.getContrats().add(cont);
			}	
			sPassif.getContrats().clear();
			
			//PRELEVEMENTS
			Set<Prelevement> prelevementsP = new 
					HashSet<Prelevement>(sPassif.getPrelevements());

			for (Prelevement prel : prelevementsP) {
				prel.setServicePreleveur(sActif);
				sActif.getPrelevements().add(prel);
			}
			sPassif.getPrelevements().clear();

			// LABO INTER
			Set<LaboInter> laboIntersP = new 
					HashSet<LaboInter>(sPassif.getLaboInters());

			for (LaboInter lab : laboIntersP) {
				lab.setService(sActif);
				sActif.getLaboInters().add(lab);
			}
			sPassif.getLaboInters().clear();
			
			//CESSIONS
			Set<Cession> cessionsP = new 
					HashSet<Cession>(sPassif.getCessions());
			
			for (Cession cess : cessionsP) {
				cess.setServiceDest(sActif);
				sActif.getCessions().add(cess);
			}
			sPassif.getCessions().clear();
			
			// CONTENEUR
			Set<Conteneur> conteneursP = new 
					HashSet<Conteneur>(sPassif.getConteneurs());
	
			for (Conteneur cont : conteneursP) {
				cont.setService(sActif);
				sActif.getConteneurs().add(cont);
			}
			sPassif.getConteneurs().clear();
			
			// COLLABORATEURS
			Set<Collaborateur> collaborateursP = sPassif.getCollaborateurs();
			Set<Collaborateur> collaborateursA = sActif.getCollaborateurs();
			
			collaborateursA.addAll(collaborateursP);

			updateCollaborateurs(sActif, new ArrayList<Collaborateur>(collaborateursA));
			updateCollaborateurs(sPassif, new ArrayList<Collaborateur>());

			// Operation FUSION attribuée à l'utilisateur actif
			Operation fusionOp = new Operation();
			fusionOp.setDate(Utils.getCurrentSystemCalendar());
			operationManager.createObjectManager(fusionOp, user, operationTypeDao.findByNom("Fusion").get(0), sActif);

			removeObjectManager(sPassif, "fusion id: " + idActif + " ." + comments, user);
		}
	}
}
