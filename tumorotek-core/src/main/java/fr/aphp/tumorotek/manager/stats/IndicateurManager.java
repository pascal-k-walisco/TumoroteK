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
package fr.aphp.tumorotek.manager.stats;

import java.util.List;

import fr.aphp.tumorotek.model.stats.SModele;
import fr.aphp.tumorotek.model.stats.Indicateur;
import fr.aphp.tumorotek.model.stats.Subdivision;
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

/**
 * Interface pour le manager du bean de domaine Indicateur.
 * 
 * @author jhusson
 * @date 19 Mars 2013
 * @version 2.0.10
 * 
 */
public interface IndicateurManager {

	/**
	 * Recherche un Indicateur dont l'identifiant est passé en paramètre.
	 * 
	 * @param IndicateurId
	 *            Id du Indicateur recherche.
	 * @return Un Indicateur.
	 */
	Indicateur findByIdManager(Integer IndicateurId);

	/**
	 * Recherche tous les Indicateurs présentes dans la base.
	 * 
	 * @return Liste de Indicateurs.
	 */
	List<Indicateur> findAllObjectsManager();
	
	/**
	 * Recherche les Indicateurs associés à aucune subdivision.
	 * 
	 * @return Une liste de Indicateurs.
	 */
	List<Indicateur> findNullSubdivisionIndicateursManager();

	/**
	 * Recherche les Indicateurs dont le modèle est passé en paramètre.
	 * 
	 * @param modele
	 *            Modele des Indicateurs que l'on recherche.
	 * @return Une liste de Indicateurs.
	 */
	List<Indicateur> findBySModeleManager(SModele modele);
	
	/**
	 * Recherche les Indicateurs dont la subdivision est passée en paramètre.
	 * 
	 * @param subdivision
	 *            subdivision des Indicateurs que l'on recherche.
	 * @return Une liste de Indicateurs.
	 */
	List<Indicateur> findBySubdivisionManager(Subdivision subdivision);
	
	/**
	 * Recherche les doublons du Indicateur passé en paramètre.
	 * @param Indicateur Un Indicateur pour lequel on cherche des doublons.
	 * @return True s'il existe des doublons.
	 */
	Boolean findDoublonManager(Indicateur Indicateur);

	/**
	 * Persist une instance de Indicateur dans la base de données.
	 * 
	 * @param Indicateur
	 *            Nouvelle instance de l'objet à créer.
	 * @param modele
	 *            Modele.
	 */
	void createObjectManager(Indicateur stmt);

	/**
	 * Persist une instance de Indicateur dans la base de données.
	 * 
	 * @param Indicateur
	 *            Instance de l'objet à maj.
	 */
	void updateObjectManager(Indicateur stmt);

	/**
	 * Supprime une Indicateur de la base de données.
	 * 
	 * @param Indicateur
	 *            requete à supprimer de la base de données.
	 */
	void removeObjectManager(Indicateur indic);

}
