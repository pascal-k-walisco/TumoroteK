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
package fr.aphp.tumorotek.manager.impl.coeur.patient;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Validator;

import fr.aphp.tumorotek.dao.coeur.patient.MaladieDao;
import fr.aphp.tumorotek.dao.coeur.patient.PatientDao;
import fr.aphp.tumorotek.dao.coeur.patient.PatientLienDao;
import fr.aphp.tumorotek.dao.coeur.patient.PatientMedecinDao;
import fr.aphp.tumorotek.dao.coeur.prelevement.PrelevementDao;
import fr.aphp.tumorotek.dao.qualite.OperationTypeDao;
import fr.aphp.tumorotek.dao.systeme.EntiteDao;
import fr.aphp.tumorotek.manager.coeur.annotation.AnnotationValeurManager;
import fr.aphp.tumorotek.manager.coeur.patient.MaladieManager;
import fr.aphp.tumorotek.manager.coeur.patient.PatientManager;
import fr.aphp.tumorotek.manager.coeur.patient.PatientValidator;
import fr.aphp.tumorotek.manager.exception.DoublonFoundException;
import fr.aphp.tumorotek.manager.exception.ObjectUsedException;
import fr.aphp.tumorotek.manager.exception.TKException;
import fr.aphp.tumorotek.manager.impl.coeur.CreateOrUpdateUtilities;
import fr.aphp.tumorotek.manager.io.imports.ImportHistoriqueManager;
import fr.aphp.tumorotek.manager.qualite.OperationManager;
import fr.aphp.tumorotek.manager.validation.BeanValidator;
import fr.aphp.tumorotek.model.coeur.annotation.AnnotationValeur;
import fr.aphp.tumorotek.model.coeur.annotation.ChampAnnotation;
import fr.aphp.tumorotek.model.coeur.patient.Maladie;
import fr.aphp.tumorotek.model.coeur.patient.Patient;
import fr.aphp.tumorotek.model.coeur.patient.PatientLien;
import fr.aphp.tumorotek.model.coeur.patient.PatientLienPK;
import fr.aphp.tumorotek.model.coeur.patient.PatientMedecin;
import fr.aphp.tumorotek.model.coeur.patient.PatientMedecinPK;
import fr.aphp.tumorotek.model.coeur.prelevement.Prelevement;
import fr.aphp.tumorotek.model.contexte.Banque;
import fr.aphp.tumorotek.model.contexte.Collaborateur;
import fr.aphp.tumorotek.model.interfacage.PatientSip;
import fr.aphp.tumorotek.model.qualite.OperationType;
import fr.aphp.tumorotek.model.utilisateur.Utilisateur;

/**
 * 
 * Implémentation du manager du bean de domaine Patient.
 * Classe créée le 30/10/09.
 * 
 * @author Mathieu BARTHELEMY
 * @version 2.0
 *
 */
public class PatientManagerImpl implements PatientManager {

	private Log log = LogFactory.getLog(PatientManager.class);

	/* Beans injectes par Spring*/
	private PatientDao patientDao;
	private MaladieDao maladieDao;
	private MaladieManager maladieManager;
	private PatientMedecinDao patientMedecinDao;
	private PatientLienDao patientLienDao;
	private PatientValidator patientValidator;
	private OperationTypeDao operationTypeDao;
	private OperationManager operationManager;
	private EntityManagerFactory entityManagerFactory;
	private EntiteDao entiteDao;
	private PrelevementDao prelevementDao;
	private AnnotationValeurManager annotationValeurManager;
	private ImportHistoriqueManager importHistoriqueManager;

	public PatientManagerImpl() {
	}

	/* Properties setters */
	public void setPatientDao(PatientDao pDao) {
		this.patientDao = pDao;
	}

	public void setMaladieDao(MaladieDao mDao) {
		this.maladieDao = mDao;
	}

	public void setMaladieManager(MaladieManager mManager) {
		this.maladieManager = mManager;
	}

	public void setPatientMedecinDao(PatientMedecinDao pmDao) {
		this.patientMedecinDao = pmDao;
	}

	public void setPatientLienDao(PatientLienDao plDao) {
		this.patientLienDao = plDao;
	}

	public void setPatientValidator(PatientValidator pValidator) {
		this.patientValidator = pValidator;
	}

	public void setOperationManager(OperationManager oManager) {
		this.operationManager = oManager;
	}

	public void setOperationTypeDao(OperationTypeDao otDao) {
		this.operationTypeDao = otDao;
	}

	public void setEntityManagerFactory(EntityManagerFactory emf) {
		this.entityManagerFactory = emf;
	}

	public void setEntiteDao(EntiteDao eDao) {
		this.entiteDao = eDao;
	}

	public void setPrelevementDao(PrelevementDao prelDao) {
		this.prelevementDao = prelDao;
	}

	public void setAnnotationValeurManager(AnnotationValeurManager aManager) {
		this.annotationValeurManager = aManager;
	}

	public void setImportHistoriqueManager(
			ImportHistoriqueManager iManager) {
		this.importHistoriqueManager = iManager;
	}

	@Override
	public void createOrUpdateObjectManager(Patient patient,
			List<Maladie> maladies, List<Collaborateur> medecins,
			List<PatientLien> patientLiens, 
			List<AnnotationValeur> listAnnoToCreateOrUpdate,
			List<AnnotationValeur> listAnnoToDelete,
			List<File> filesCreated, List<File> filesToDelete,
			Utilisateur utilisateur,
			String operation, 
			String baseDir,
			boolean isImport) {

		if (patient != null) {
			if (operation == null) {
				throw new NullPointerException("operation cannot be "
						+ "set to null for createorUpdateMethod");
			}
			//Validation
			BeanValidator.validateObject(patient,
					new Validator[] { patientValidator });
			//Doublon
			if (!operation.equals("fusion") 
					&& findDoublonManager(patient)) {
				log.warn("Doublon lors " + operation + " objet Patient "
						+ patient.toString());
				throw new DoublonFoundException("Patient", operation, patient.getNip(), null);
			} else {
				if ((operation.equals("creation") || operation
						.equals("modification"))
						|| operation.equals("modifMulti")
						|| operation.equals("synchronisation")
						|| operation.equals("fusion")) {

					OperationType oType;

					if (operation.equals("creation")) {
						patientDao.createObject(patient);
						log.info("Enregistrement objet Patient "
								+ patient.toString());

						oType = operationTypeDao.findByNom("Creation").get(0);
					} else {
						patientDao.updateObject(patient);
						log.info("Modification objet Patient "
								+ patient.toString());

						if (operation.equals("modification")) {
							oType = operationTypeDao.findByNom("Modification")
							.get(0);
						} else if (operation.equals("synchronisation")) {
							oType = operationTypeDao.findByNom(
							"Synchronisation").get(0);
						} else if (operation.equals("modifMulti")) {
							oType = operationTypeDao.findByNom("ModifMultiple")
							.get(0);
						} else { // fusion
							oType = operationTypeDao.findByNom("Fusion").get(0);
						}

					}

					CreateOrUpdateUtilities.createAssociateOperation(patient,
							operationManager, oType, utilisateur);

					// ajout association vers maladies
					if (maladies != null) {
						updateMaladies(patient, maladies);
					}
					// ajout association vers collaborateurs
					if (medecins != null) {
						updateMedecins(patient, medecins);
					}
					// ajout association vers liens familiaux
					if (patientLiens != null) {
						updateLiens(patient, patientLiens);
					}

					try {
						// Annotations
						// suppr les annotations
						if (listAnnoToDelete != null) {
							annotationValeurManager
								.removeAnnotationValeurListManager(
										listAnnoToDelete, filesToDelete);
						}

						// update les annotations, null operation pour
						// laisser la possibilité création/modification au sein 
						// de la liste
						if (listAnnoToCreateOrUpdate != null) {
							annotationValeurManager
							.createAnnotationValeurListManager(
									listAnnoToCreateOrUpdate, patient,
									utilisateur, null, baseDir, filesCreated, filesToDelete);
						}
						// enregistre operation associee annotation 
						// si il y a eu des deletes et pas d'updates
						if ((listAnnoToCreateOrUpdate == null 
								|| listAnnoToCreateOrUpdate.isEmpty())
								&& (listAnnoToDelete != null 
										&& !listAnnoToDelete.isEmpty())) {
							CreateOrUpdateUtilities.createAssociateOperation(
									patient, operationManager, operationTypeDao
									.findByNom("Annotation").get(0),
									utilisateur);
						}
						if (filesToDelete != null) {
							for (File f : filesToDelete) {
								f.delete();
							}
						}
						
					} catch (RuntimeException re) {
						// rollback au besoin...
						if (filesCreated != null) {
							for (File f : filesCreated) {
								f.delete();
							}
						} else {
							log.warn("Rollback création fichier n'a pas pu être réalisée");
						}
						if (operation.equals("creation")
								&& !isImport) {
							patient.setPatientId(null);
						}
						throw re;
					}

				} else {
					throw new IllegalArgumentException("Operation must match "
							+ "'creation/modification/synchronisation' values");
				}
			}
		}			
	}
	
	@Override
	public Patient findByIdManager(Integer id) {
		Patient a = patientDao.findById(id);
		return a;
	}

	@Override
	public boolean findDoublonManager(Patient patient) {
		boolean doublon = false;
		if (patient.getPatientId() == null) {
			doublon = patientDao.findByNom(patient.getNom())
			.contains(patient);
		} else {
			doublon = patientDao.findByExcludedId(
					patient.getPatientId(), patient.getNom())
					.contains((patient));
		}

		// s'il n'y a pas de doublon sur le EQUALS du Patient, on va
		// vérifier qu'il n'y a aucun patient ayant le même NIP
		if (doublon) {
			return doublon;
		} else {
			if (patient.getPatientId() == null) {
				return patientDao.findByNip(patient.getNip()).size() > 0;
			} else {
				return patientDao.findByNipWithExcludedId(
						patient.getNip(),
						patient.getPatientId()).size() > 0;
			}
		}
	}
	
	@Override
	public Patient getExistingPatientManager(Patient pat) {
		if (pat != null) {
			// recherche par identité
			List<Patient> patients = patientDao.findByNom(pat.getNom());
			
			// renvoie patient si contenu dans la liste
			if (patients.contains(pat)) {
				return patients.get(patients.indexOf(pat));
			} 
			
			patients.clear();
			
			//recherche par nip
			// fonction appelée lors Import en modification 
			// donc pas pertinent de faire recherche par NIP!
			// patients.addAll(patientDao.findByNip(pat.getNip()));
			
			// renvoie patient si contenu dans la liste
			// if (patients.isEmpty()) {
			//	return patients.get(0);
			// } 
		}
		return null;
	}

	@Override
	public List<Patient> findAllObjectsManager() {
		log.debug("Recherche totalite des Patients");
		return patientDao.findAll();
	}

	@Override
	public List<Integer> findAllObjectsIdsManager() {
		log.debug("Recherche totalite des ids des Patients");
		return patientDao.findByAllIds();
	}

	@Override
	public List<Integer> findAllObjectsIdsWithBanquesManager(
			List<Banque> banques) {
		log.debug("Recherche totalite des ids des Patients en" 
				+ "fonction d'une liste de banques");
		if (banques != null && banques.size() > 0) {
			return patientDao.findByAllIdsWithBanques(banques);
		} else {
			return new ArrayList<Integer>();
		}
	}

	@Override
	public List<Patient> findByNomLikeManager(String nom,
			boolean exactMatch) {
		if (!exactMatch) {
			nom = "%" + nom + "%";
		}
		log.debug("Recherche Patient par nom: " 
				+ nom + " exactMatch " + String.valueOf(exactMatch));
		return patientDao.findByNom(nom);
	}

	@Override
	public List<Patient> findByNipLikeManager(String nip, 
			boolean exactMatch) {
		if (!exactMatch) {
			nip = "%" + nip + "%";
		}
		log.debug("Recherche Patient par nip: " 
				+ nip + " exactMatch " + String.valueOf(exactMatch));
		return patientDao.findByNip(nip);
	}

	/**
	 * Recherche toutes les patients dont le nom est egal ou 'like' 
	 * celui passé en parametre.
	 * @param nom
	 * @param boolean exactMatch
	 * @return Liste de Patient.
	 */
	@Override
	public List<Patient> 
		findByNomLikeBothSideManager(String nom, boolean exactMatch) {
		if (!exactMatch) {
			nom = "%" + nom + "%";
		}
		log.debug("Recherche Patient par nom: " 
				+ nom + " exactMatch " + String.valueOf(exactMatch));
		return patientDao.findByNom(nom);
	}

	/**
	 * Recherche toutes les patients dont le nom est egal ou 'like' 
	 * celui passé en parametre.
	 * @param nom
	 * @param boolean exactMatch
	 * @return Liste de Patient.
	 */
	@Override
	public List<Integer> findByNomLikeBothSideReturnIdsManager(String nom, 
							List<Banque> banques, boolean exactMatch) {
		if (!exactMatch) {
			nom = "%" + nom + "%";
		}
		log.debug("Recherche Patient par nom: " 
				+ nom + " exactMatch " + String.valueOf(exactMatch));
		if (banques != null && banques.size() > 0) {
			return patientDao.findByNomReturnIds(nom, banques);
		} else {
			return new ArrayList<Integer>();
		}
	}

	/**
	 * Recherche toutes les patients dont le nip est egal
	 * ou 'like' celui en parametre.
	 * @param nip
	 * @param boolean exactMatch
	 * @return Liste de Patient.
	 */
	@Override
	public List<Integer> findByNipLikeBothSideReturnIdsManager(String nip, 
								List<Banque> banques, boolean exactMatch) {
		if (!exactMatch) {
			nip = "%" + nip + "%";
		}
		log.debug("Recherche Patient par nip: " 
				+ nip + " exactMatch " + String.valueOf(exactMatch));
		if (banques != null && banques.size() > 0) {
			return patientDao.findByNipReturnIds(nip, banques);
		} else {
			return new ArrayList<Integer>();
		}
	}

	@Override
	public List<Integer> findAfterDateCreationReturnIdsManager(
			Calendar date,
			List<Banque> banques) {
		List<Integer> liste = new ArrayList<Integer>();	
		if (date != null && banques != null) {
			log.debug("Recherche des Patients enregistres apres la date " 
					+ new SimpleDateFormat("yyyy/MMM/dd HH:mm:ss")
					.format(date.getTime()));
			liste = findByOperationTypeAndDateWithBanquesReturnIds(
					operationTypeDao.findByNom("Creation").get(0), 
					date, banques);	
		}
		return liste;
	}

	@Override
	public List<Patient> findAfterDateModificationManager(Calendar date) {
		List<Patient> liste = new ArrayList<Patient>();	
		if (date != null) {
			log.debug("Recherche des Patients modifies apres la date " 
					+ new SimpleDateFormat("yyyy/MMM/dd HH:mm:ss")
					.format(date.getTime()));
			liste = findByOperationTypeAndDate(operationTypeDao
					.findByNom("Modification").get(0), 
					date);	
		}
		return liste;
	}

	@Override
	public List<Patient> findByDateNaissanceManager(Date date) {
		if (date != null) {
			log.debug("Recherche Patient par date de naissance: " 
					+ date.toString());
		}
		return patientDao.findByDateNaissance(date);
	}

	@Override
	public List<Patient> findByEtatIncompletManager() {
		log.debug("Recherche Patient incomplets");
		return patientDao.findByEtatIncomplet();
	}

	@Override
	public List<String> findAllNipsManager() {
		return patientDao.findAllNips();
	}

	@Override
	public List<String> findAllNomsManager() {
		return patientDao.findAllNoms();
	}

	@Override
	public List<Patient> findByNdaLikeManager(String nda, boolean exactMatch) {
		List<Patient> patients = new ArrayList<Patient>();
		if (!exactMatch) {
			nda = "%" + nda + "%";
		}
		log.debug("Recherche Patient par nda: " 
				+ nda + " exactMatch " + String.valueOf(exactMatch));
		List<Prelevement> prels = prelevementDao.findByNdaLike(nda);
		for (int i = 0; i < prels.size(); i++) {
			if (prels.get(i).getMaladie() != null) { 
				patients.add(prels.get(i).getMaladie().getPatient());
			}
		}
		return patients;
	}

	@Override
	public void removeObjectManager(Patient patient, String comments, 
			Utilisateur user, List<File> filesToDelete) {
		if (patient != null) {
			if (!isUsedObjectManager(patient)) {

				// Supprime les maladies associées
				Iterator<Maladie> malsIt = maladieManager
				.findByPatientManager(patient).iterator();
				while (malsIt.hasNext()) {
					maladieManager
					.removeObjectManager(malsIt.next(), comments, user);
				}

				patientDao.removeObject(patient.getPatientId());
				log.info("Suppression objet Patient " + patient.toString());

				//Supprime operations associes
				CreateOrUpdateUtilities.
				removeAssociateOperations(patient, operationManager, 
						comments, user);

				//Supprime importations associes
				CreateOrUpdateUtilities.
				removeAssociateImportations(patient, 
						importHistoriqueManager);

				//Supprime annotations associes
				annotationValeurManager
				.removeAnnotationValeurListManager(annotationValeurManager
						.findByObjectManager(patient), filesToDelete);
			} else {
				throw new ObjectUsedException("patient.deletion.isUsed", false);
			}
		} else {
			log.warn("Suppression d'un Patient null");
		}
	}

	@Override
	public Set<PatientMedecin> getPatientMedecinsManager(Patient patient) {
		patient = patientDao.mergeObject(patient);
		Set<PatientMedecin> medecins = patient.getPatientMedecins();
		medecins.isEmpty(); // operation empechant LazyInitialisationException
		return medecins;
	}

	@Override
	public List<Collaborateur> getMedecinsManager(Patient patient) {
		List<Collaborateur> colls = 
			new ArrayList<Collaborateur>();
		if (patient != null) {
			patient = patientDao.mergeObject(patient);
			LinkedHashSet<PatientMedecin> medecins = 
				new LinkedHashSet<PatientMedecin>(patient.getPatientMedecins());
			Iterator<PatientMedecin> it = medecins.iterator();
			while (it.hasNext()) {
				PatientMedecin pm = it.next();
				colls.add(pm.getCollaborateur());
			}
		}
		return colls;
	}

	@Override
	public Set<PatientLien> getPatientLiensManager(Patient patient) {
		Set<PatientLien> liens = new HashSet<PatientLien>();
		if (patient != null) {
			patient = patientDao.mergeObject(patient);
			liens.addAll(patient.getPatientLiens());
			liens.addAll(patient.getPatientLiens2());
			liens.isEmpty(); // operation empechant LazyInitialisationException
		}
		return liens;
	}

	/**
	 * Cette méthode met à jour les associations entre un patient et
	 * une liste de maladies.
	 * @param patient pour lequel on veut mettre à jour
	 * les associations.
	 * @param maladies Liste des maladies que l'on veut associer 
	 * au patient.
	 */
	private void updateMaladies(
			Patient patient, List<Maladie> maladies) {

		Patient pat = patientDao.mergeObject(patient);

		Iterator<Maladie> it = pat.getMaladies().iterator();
		List<Maladie> malsToRemove 
		= new ArrayList<Maladie>();
		// on parcourt les maladies qui sont actuellement associées
		// au patient
		while (it.hasNext()) {
			Maladie tmp = it.next();
			// si une maladie n'est pas dans la nouvelle liste, on
			// le conserve afin de le retirer par la suite
			if (!maladies.contains(tmp)) {
				malsToRemove.add(tmp);
			}
		}

		// on parcourt la liste la liste des maladies à retirer de
		// l'association
		for (int i = 0; i < malsToRemove.size(); i++) {
			Maladie mal = maladieDao
			.mergeObject(malsToRemove.get(i));
			// on retire la maladie de l'association et on la supprime
			pat.getMaladies().remove(mal);
			maladieDao.removeObject(mal.getMaladieId());

			log.debug("Suppression de l'association entre le patient : " 
					+ pat.toString() + " et suppression de la maladie : "
					+ mal.toString());
		}

		// on parcourt la nouvelle liste de maladies
		for (int i = 0; i < maladies.size(); i++) {
			// si une maladie n'était pas associé au patient
			if (!pat.getMaladies().contains(maladies.get(i))) {
				// on ajoute la maladie à l'association
				pat.getMaladies()
				.add(maladieDao.mergeObject(maladies.get(i)));

				log.debug("Ajout de l'association entre le patient : " 
						+ pat.toString() + " et la maladie : "
						+ maladies.get(i).toString());
			}
		}
	}

	/**
	 * Cette méthode met à jour les associations entre un patient et
	 * une liste de patientMedecin.
	 * @param patient pour lequel on veut mettre à jour
	 * les associations.
	 * @param collaborateurs Liste ordonnée des medecins referents 
	 * que l'on veut associer au patient.
	 */
	private void updateMedecins(
			Patient patient, List<Collaborateur> collaborateurs) {

		Patient pat = patientDao.mergeObject(patient);

		Iterator<PatientMedecin> it = pat.getPatientMedecins().iterator();
		List<PatientMedecin> medsToRemove 
		= new ArrayList<PatientMedecin>();
		// on parcourt les medecins qui sont actuellement associés
		// au patient
		while (it.hasNext()) {
			PatientMedecin tmp = it.next();
			// si un medecin n'est pas dans la nouvelle liste, on
			// le conserve afin de le retirer par la suite
			if (!collaborateurs.contains(tmp.getCollaborateur())) {
				medsToRemove.add(tmp);
			}
		}

		// on parcourt la liste la liste des medecins à retirer de
		// l'association
		for (int i = 0; i < medsToRemove.size(); i++) {
			PatientMedecin med = patientMedecinDao
			.mergeObject(medsToRemove.get(i));
			// on retire le medecin de l'association et on le supprime
			pat.getPatientMedecins().remove(med);
			patientMedecinDao.removeObject(med.getPk());

			log.debug("Suppression de l'association entre le patient : " 
					+ pat.toString() + " et suppression du medecin : "
					+ med.toString());
		}

		// on parcourt la nouvelle liste de medecins
		for (int i = 0; i < collaborateurs.size(); i++) {
			PatientMedecin pm = new PatientMedecin();
			PatientMedecinPK pk = new PatientMedecinPK(collaborateurs.get(i), 
					pat);
			pm.setPk(pk);
			pm.setOrdre(i + 1);
			// si un medecin n'était pas associé au patient
			if (!pat.getPatientMedecins().contains(pm)) {
				// on ajoute le medecin dans l'association dans le bon ordre
				pat.getPatientMedecins()
				.add(patientMedecinDao.mergeObject(pm));

				log.debug("Ajout de l'association entre le patient : " 
						+ pat.toString() + " et le medecin : "
						+ collaborateurs.get(i).toString());
			} else { // on modifie l'ordre du medecin present avec la liste
				pm = patientMedecinDao.findById(pk);
				pm.setOrdre(i + 1);
				patientMedecinDao.mergeObject(pm);
			}
		}
	}

	/**
	 * Cette méthode met à jour les associations entre un patient et
	 * une liste de patientLien.
	 * @param patient pour lequel on veut mettre à jour
	 * les liens familiaux.
	 * @param patients Liste de patients que l'on veut que 
	 * l'on veut lier au patient.
	 */
	private void updateLiens(Patient patient, List<PatientLien> liens) {

		Patient pat = patientDao.mergeObject(patient);
		Patient pat2;

		Iterator<PatientLien> it = pat.getPatientLiens().iterator();
		List<PatientLien> liensToRemove = new ArrayList<PatientLien>();
		// on parcourt les liens qui sont actuellement associés
		// au patient
		while (it.hasNext()) {
			PatientLien tmp = it.next();
			// si un lien n'est pas dans la nouvelle liste, on
			// le conserve afin de le retirer par la suite
			if (!liens.contains(tmp)) {
				liensToRemove.add(tmp);
			}
		}

		// on parcourt la liste la liste des liens à retirer de
		// l'association
		for (int i = 0; i < liensToRemove.size(); i++) {
			PatientLien lien = patientLienDao
			.mergeObject(liensToRemove.get(i));
			// on retire le lien de l'association et on le supprime
			pat2 = lien.getPatient2();
			pat.getPatientLiens().remove(lien);
			pat2.getPatientLiens2().remove(lien);
			patientLienDao.removeObject(lien.getPk());

			log.debug("Suppression de l'association entre le patient : " 
					+ pat.toString() + " et suppression du lien : "
					+ lien.toString());
		}

		// on parcourt la nouvelle liste de liens
		for (int i = 0; i < liens.size(); i++) {
			// si un lien n'était pas associé au patient
			if (!pat.getPatientLiens().contains(liens.get(i))) {
				PatientLien lien = new PatientLien();
				PatientLienPK pk = new PatientLienPK();
				pk.setPatient1(pat); 
				pk.setPatient2(liens.get(i).getPatient2());
				lien.setPk(pk);
				lien.setLienFamilial(liens.get(i).getLienFamilial());
				// on ajoute le lien dans l'association
				pat.getPatientLiens()
				.add(patientLienDao.mergeObject(lien));
				//				patientDao.mergeObject((liens.get(i).getPatient2()))
				//									.getPatientLiens2().add(liens.get(i));


				log.debug("Ajout de l'association entre le patient : " 
						+ pat.toString() + " et le lien : "
						+ liens.get(i).toString());
			}
		}
	}

	/**
	 * Recupere la liste de patients en fonction du type d'operation et 
	 * d'une date a laquelle la date d'enregistrement de l'operation doit
	 * etre superieure ou egale.
	 * Dans un premier temps, recupere la liste des objetIds qui sont ensuite
	 * utilises pour recuperer les patients.
	 * @param oType OperationType
	 * @param date
	 * @return List de Patient
	 */
	@SuppressWarnings("unchecked")
	private List<Patient> findByOperationTypeAndDate(OperationType oType, 
			Calendar date) {
		EntityManager em = entityManagerFactory.createEntityManager();
		Query opQuery = em.createQuery("SELECT DISTINCT o.objetId FROM "
				+ "Operation o WHERE o.entite = :entite AND "
				+ "o.operationType = :oType AND date >= :date");
		opQuery.setParameter("entite", entiteDao.findByNom("Patient"));
		opQuery.setParameter("oType", oType);
		opQuery.setParameter("date", date);
		List<Integer> ids =  opQuery.getResultList();
		Query prelQuery;
		if (ids.size() > 1) { //HQL IN () si liste taille > 1
			prelQuery = em.createQuery("SELECT DISTINCT p FROM Patient p "
					+ "WHERE p.patientId IN (:ids)");
			prelQuery.setParameter("ids", ids);
		} else if (ids.size() == 1) {
			prelQuery = em.createQuery("SELECT DISTINCT p FROM Patient p "
					+ "WHERE p.patientId = :id");
			prelQuery.setParameter("id", ids.get(0));
		} else {
			return new ArrayList<Patient>();
		}

		return prelQuery.getResultList();
	}

	/**
	 * Recupere la liste de patients en fonction du type d'operation et 
	 * d'une date a laquelle la date d'enregistrement de l'operation doit
	 * etre superieure ou egale.
	 * Dans un premier temps, recupere la liste des objetIds qui sont ensuite
	 * utilises pour recuperer les patients.
	 * @param oType OperationType
	 * @param date
	 * @return List de Patient
	 */
	@SuppressWarnings("unchecked")
	private List<Integer> findByOperationTypeAndDateWithBanquesReturnIds(
			OperationType oType, 
			Calendar date,
			List<Banque> banques) {
		EntityManager em = entityManagerFactory.createEntityManager();
		Query opQuery = em.createQuery("SELECT DISTINCT o.objetId FROM "
				+ "Operation o WHERE o.entite = :entite AND "
				+ "o.operationType = :oType AND date >= :date");
		opQuery.setParameter("entite", entiteDao.findByNom("Patient"));
		opQuery.setParameter("oType", oType);
		opQuery.setParameter("date", date);
		List<Integer> ids =  opQuery.getResultList();
		Query prelQuery;
		if (banques.size() > 0 && ids.size() > 1) { 
			//HQL IN () si liste taille > 1
			prelQuery = em.createQuery("SELECT DISTINCT p.patientId " 
					+ "FROM Patient p "
					+ "JOIN p.maladies m " 
					+ "JOIN m.prelevements prlvts "
					+ "WHERE p.patientId IN (:ids) " 
					+ "AND prlvts.banque IN (:banques)");
			prelQuery.setParameter("ids", ids);
			prelQuery.setParameter("banques", banques);
		} else if (banques.size() > 0 && ids.size() == 1) {
			prelQuery = em.createQuery("SELECT DISTINCT p.patientId " 
					+ "FROM Patient p "
					+ "JOIN p.maladies m " 
					+ "JOIN m.prelevements prlvts "
					+ "WHERE p.patientId = :id "
					+ "AND prlvts.banque IN (:banques)");
			prelQuery.setParameter("id", ids.get(0));
			prelQuery.setParameter("banques", banques);
		} else {
			return new ArrayList<Integer>();
		}

		return prelQuery.getResultList();
	}

	/**
	 * Recherche les derniers Patients créés dans le systeme.
	 * @param nbResults Nombre de résultats souhaités.
	 * @return Liste de Patients.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Patient> findLastCreationManager(List<Banque> banques,
			int nbResults) {

		List<Patient> liste = new ArrayList<Patient>();
		if (banques != null && banques.size() > 0
				&& nbResults > 0) {
			log.debug("Recherche des " + nbResults + " derniers Patients " 
					+ "enregistres.");
			// liste = findByLastOperationType(operationTypeDao
			//		.findByNom("Creation").get(0), banques, nbResults);
			EntityManager em = entityManagerFactory.createEntityManager();
			Query query = em.createQuery("SELECT distinct p " 
					+ "FROM Patient p " 
					+ "JOIN p.maladies m " 
					+ "JOIN m.prelevements prlvts "
					+ "WHERE prlvts.banque in (:banques) "
					+ "ORDER BY p.patientId DESC");

			query.setParameter("banques", banques);
			query.setFirstResult(0);
			query.setMaxResults(nbResults);
			
			liste.addAll(query.getResultList());
		}
		return liste;

	}

//	/**
//	 * Récupère une liste de Patients en fonction d'un type d'opération. 
//	 * Cette liste est ordonnée par la date de l'opération.
//	 * Sa taille maximale est fixée par un paramètre.
//	 * @param oType Type de l'opération.
//	 * @param nbResults Nombre max de Patients souhaités.
//	 * @return Liste de Patients.
//	 */
//	@SuppressWarnings("unchecked")
//	private List<Patient> findByLastOperationType(OperationType oType,
//			List<Banque> banques,
//			int nbResults) {
//
//		EntityManager em = entityManagerFactory.createEntityManager();
//		Query query = em.createQuery("SELECT DISTINCT p, o.date " 
//				+ "FROM Patient p, Operation o " 
//				+ "JOIN p.maladies m " 
//				+ "JOIN m.prelevements prlvts "
//				+ "WHERE o.objetId = p.patientId " 
//				+ "AND o.entite = :entite " 
//				+ "AND o.operationType = :oType "
//				+ "AND prlvts.banque IN (:banques) "
//				+ "ORDER BY o.date DESC");
//		query.setParameter("entite", entiteDao.findByNom("Patient"));
//		query.setParameter("oType", oType);
//		query.setParameter("banques", banques);
//		query.setMaxResults(nbResults);
//
//		List<Patient> patients = new ArrayList<Patient>();
//		Iterator<Object[]> resIt = query.getResultList().iterator();
//		while (resIt.hasNext()) {
//			patients.add((Patient) resIt.next()[0]);
//		}
//
//		//List<Patient> patients = query.getResultList();
//
//		return patients;		
//	}

	@Override
	public Long getTotMaladiesCountManager(Patient p) {
		return patientDao.findCountMaladies(p).get(0);
	}

	@Override
	public Long getTotPrelevementsCountManager(Patient p) {
		return patientDao.findCountPrelevements(p).get(0);
	}

	@Override
	public Long getCountPrelevementsByBanqueManager(Patient pat, Banque bank) {
		return patientDao.findCountPrelevementsByBanque(pat, bank).get(0);
	}

	@Override
	public void updateMultipleObjectsManager(List<Patient> patients,
			List<Patient> basePatients,
			List<AnnotationValeur> listAnnoToCreateOrUpdate, 
			List<AnnotationValeur> listAnnoToDelete,
			Utilisateur utilisateur,
			String baseDir) {
		
		List<File> filesCreated = new ArrayList<File>();
		List<File> filesToDelete = new ArrayList<File>();

		for (int i = 0; i < patients.size(); i++) {
			Patient pat = patients.get(i);
			try {
				createOrUpdateObjectManager(pat, null, null, null, 
						null, null, null, null, utilisateur, "modifMulti", 
						baseDir, false);
			} catch (RuntimeException e) {
				if (e instanceof TKException) {
					((TKException) e).setEntiteObjetException(
					"Patient");
					((TKException) e).setIdentificationObjetException(
							pat.getNom());
				}
				throw e;
			}
		}
		
		try {
			// suppr les annotations pour tous les prelevements
			annotationValeurManager
					.removeAnnotationValeurListManager(listAnnoToDelete, filesToDelete);
			
			if (listAnnoToCreateOrUpdate != null) {
				// traite en premier et retire les annotations 
				// création de fichiers pour 
				// enregistrement en batch 
				List<AnnotationValeur> fileVals = new ArrayList<AnnotationValeur>();
				for (AnnotationValeur val : listAnnoToCreateOrUpdate) {
					if (val.getFichier() != null && val.getStream() != null) {
						annotationValeurManager
							.createFileBatchForTKObjectsManager(basePatients, 
									val.getFichier(), val.getStream(), 
									val.getChampAnnotation(), val.getBanque(), 
									utilisateur, baseDir, filesCreated);
						fileVals.add(val);
					}
				}
				listAnnoToCreateOrUpdate.removeAll(fileVals);
			
				// update les annotations, null operation pour
				// laisser la possibilité création/modification au sein 
				// de la liste
				annotationValeurManager
					.createAnnotationValeurListManager(listAnnoToCreateOrUpdate, 
						null, utilisateur, null, baseDir, filesCreated, filesToDelete);
			}
			
			for (File f : filesToDelete) {
				f.delete();
			}
		} catch (RuntimeException e) {
			for (File f : filesCreated) {
				f.delete();
			}
		}
	}

	@Override
	public boolean isUsedObjectManager(Patient patient) {
		return getTotPrelevementsCountManager(patient) > 0;
	}

	@Override
	public List<Patient> findByIdsInListManager(List<Integer> ids) {
		if (ids != null && ids.size() > 0) {
			return patientDao.findByIdInList(ids);
		} else {
			return new ArrayList<Patient>();
		}
	}

	@Override
	public List<Field> isSynchronizedPatientManager(PatientSip sip, 
			Patient inBase) {
		List<Field> fields = new ArrayList<Field>();
		if (sip != null && inBase != null) {
			try {
				if (sip.getNip() != null 
						&& !sip.getNip().equals(inBase.getNip())) {
					fields.add(inBase.getClass()
							.getDeclaredField("nip"));
				}
				if (!sip.getNom().equals(inBase.getNom())) {
					fields.add(inBase.getClass()
							.getDeclaredField("nom"));
				}
				// efface nom naissance si null sip
				if ((sip.getNomNaissance() != null
						&& !sip.getNomNaissance()
						.equals(inBase.getNomNaissance())) 
						|| (sip.getNomNaissance() == null
								&& inBase.getNomNaissance() != null)) {
					fields.add(inBase.getClass()
							.getDeclaredField("nomNaissance"));
				}
				if (!sip.getPrenom().equals(inBase.getPrenom())) {
					fields.add(inBase.getClass()
							.getDeclaredField("prenom"));
				}
				if (!sip.getDateNaissance()
						.equals(inBase.getDateNaissance())) {
					fields.add(inBase.getClass()
							.getDeclaredField("dateNaissance"));
				}
				if (!sip.getSexe().equals(inBase.getSexe())) {
					fields.add(inBase.getClass()
							.getDeclaredField("sexe"));
				}
				if (sip.getVilleNaissance() != null
						&& !sip.getVilleNaissance()
						.equals(inBase.getVilleNaissance())) {
					fields.add(inBase.getClass()
							.getDeclaredField("villeNaissance"));
				}				
				if (sip.getPaysNaissance() != null
						&& !sip.getPaysNaissance()
						.equals(inBase.getPaysNaissance())) {
					fields.add(inBase.getClass()
							.getDeclaredField("paysNaissance"));
				}
				if (!sip.getPatientEtat()
						.equals(inBase.getPatientEtat())) {
					fields.add(inBase.getClass()
							.getDeclaredField("patientEtat"));
				}
				if (sip.getDateEtat() != null
						&& !sip.getDateEtat()
						.equals(inBase.getDateEtat())) {
					fields.add(inBase.getClass()
							.getDeclaredField("dateEtat"));
				}
				if (sip.getDateDeces() != null
						&& !sip.getDateDeces()
						.equals(inBase.getDateDeces())) {
					fields.add(inBase.getClass()
							.getDeclaredField("dateDeces"));
				}
			} catch (NoSuchFieldException ne) {
				log.error(ne.getMessage());
			}
		}
		return fields;
	}

	@Override
	public void fusionPatientManager(Patient patient, Patient passif, 
			Utilisateur u, String comments) {

		if (patient != null && passif != null) {		

			// recuperation des medecins referents
			List<Collaborateur> medsP = getMedecinsManager(passif);
			List<Collaborateur> medsA = getMedecinsManager(patient);
			// ajoute a la liste les nouveaux medecins
			for (int i = 0; i < medsP.size(); i++) {
				if (!medsA.contains(medsP.get(i))) {
					medsA.add(medsP.get(i));
				}
			}

			// recuperation des liens familiaux
			List<PatientLien> liensP = new 
			ArrayList<PatientLien>(getPatientLiensManager(passif));
			List<PatientLien> liensA = new 
			ArrayList<PatientLien>(getPatientLiensManager(patient));
			// ajoute a la liste les nouveaux liens
			for (int i = 0; i < liensP.size(); i++) {
				liensP.get(i).getPk().setPatient1(patient);
				if (!liensA.contains(liensP.get(i))) {
					liensA.add(liensP.get(i));
				}
			}

			// gestion des annotations
			List<AnnotationValeur> valeursActives = annotationValeurManager
			.findByObjectManager(patient);
			List<AnnotationValeur> valeursPassives = annotationValeurManager
			.findByObjectManager(passif);
			Hashtable<ChampAnnotation, AnnotationValeur> champsValeurs = 
				new Hashtable<ChampAnnotation, AnnotationValeur>();
			// on classe les valeurs actives en fct de leur champ
			for (int i = 0; i < valeursActives.size(); i++) {
				champsValeurs.put(valeursActives.get(i).getChampAnnotation(), 
						valeursActives.get(i));
			}
			// liste des annotations du passif à conserver
			List<AnnotationValeur> valeursAConserver = 
				new ArrayList<AnnotationValeur>();
			// liste des annotations du passif à supprimer
			List<AnnotationValeur> valeursASupprimer = 
				new ArrayList<AnnotationValeur>();
			for (int i = 0; i < valeursPassives.size(); i++) {
				// si le patient actif a déjà une annotation pour le
				// champ de l'annotation du passif
				if (champsValeurs.containsKey(valeursPassives.get(i)
						.getChampAnnotation())) {
					// si cette valeur est pour la même collection : 
					// on va supprimer cette annotation
					// if (valeursPassives.get(i).getBanque().equals(
					// 		champsValeurs.get(valeursPassives.get(i)
					//				.getChampAnnotation()).getBanque())) {
					//	valeursASupprimer.add(valeursPassives.get(i));
					// } else {
					//	valeursAConserver.add(valeursPassives.get(i));
					// }
					valeursASupprimer.add(valeursPassives.get(i));
				} else {
					valeursAConserver.add(valeursPassives.get(i));
				}
			}

			// mise a jour du Patient actif
			createOrUpdateObjectManager(patient, null, medsA, liensA, 
					valeursAConserver, 
					null, null, null, u, "fusion", null, false);


			// recuperation des maladies
			Set<Maladie> malP = maladieManager.getMaladiesManager(passif);
			List<Maladie> malA = new ArrayList<Maladie>(maladieManager
					.getMaladiesManager(patient));
			// Ajoute la maladie si n'existe pas sinon ajoute ses prels
			Iterator<Prelevement> prelsIt;
			List<Maladie> malsToRemove = new ArrayList<Maladie>();
			Maladie maladie;
			Prelevement prel;
			Iterator<Maladie> malIt = malP.iterator();
			while (malIt.hasNext()) {
				maladie = malIt.next();
				prelsIt = maladieManager
				.getPrelevementsManager(maladie).iterator();
				maladie.setPatient(patient); // pour appliquer equals()
				if (!malA.contains(maladie)) { // ajoute la maladie
					maladieDao.updateObject(maladie);
				} else { // ajoute prelevements a la maladie existante
					int index = malA.indexOf(maladie);
					maladie.setPatient(passif); //recupere son patient
					while (prelsIt.hasNext()) {
						prel = prelsIt.next();
						prel.setMaladie(malA.get(index));
						prelevementDao.updateObject(prel);
					}
					maladie.getPrelevements().clear();
					malsToRemove.add(maladie);
				}
			}


			// fantomization (oh le beau mot) du passif
			for (int i = 0; i < malsToRemove.size(); i++) {
				maladieManager.removeObjectManager(malsToRemove.get(i), 
						comments, u);
				//passif.getMaladies().remove(malsToRemove.get(i));
			}

			// remove patient et objets associes
			patientDao.removeObject(passif.getPatientId());
			//Supprime operations associes
			CreateOrUpdateUtilities.
			removeAssociateOperations(passif, operationManager, 
					comments, u);

			//Supprime importations associes
			CreateOrUpdateUtilities.
			removeAssociateImportations(passif, 
					importHistoriqueManager);

			//Supprime annotations associes
			/*annotationValeurManager
				.removeAnnotationValeurListManager(annotationValeurManager
						.findByObjectManager(passif));*/
			List<File> filesToDelete = new ArrayList<File>();
			annotationValeurManager
				.removeAnnotationValeurListManager(valeursASupprimer, filesToDelete);
			for (File f : filesToDelete) {
				f.delete();
			}
		}
	}

	@Override
	public List<Integer> findByNipInListManager(List<String> criteres,
			List<Banque> banques) {
		if (criteres != null && criteres.size() > 0
				&& banques != null && banques.size() > 0) {
			return patientDao.findByNipInList(criteres, banques);
		} else {
			return new ArrayList<Integer>();
		}
	}

	@Override
	public List<Integer> findByNomInListManager(List<String> criteres,
			List<Banque> banques) {
		if (criteres != null && criteres.size() > 0
				&& banques != null && banques.size() > 0) {
			return patientDao.findByNomInList(criteres, banques);
		} else {
			return new ArrayList<Integer>();
		}
	}
	
	@Override
	public Long findCountByReferentManager(Collaborateur colla){
		if (colla != null) {
			return patientDao.findCountByReferent(colla).get(0);
		}
		else{
			return new Long(0);
		}
	}
	
	@Override
	public void removeListFromIdsManager(List<Integer> ids, String comment, 
			Utilisateur u) {
		if (ids != null) {
			List<File> filesToDelete = new ArrayList<File>();
			Patient p;
			for (Integer id : ids) {
				p = findByIdManager(id);
				if (p != null) {
					removeObjectManager(p, comment, u, filesToDelete);
				}
			}
			if (filesToDelete != null) {
				for (File f : filesToDelete) {
					f.delete();
				}
			}
		}
	}
}
