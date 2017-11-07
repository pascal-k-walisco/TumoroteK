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
package fr.aphp.tumorotek.manager.code;

import java.util.List;

import fr.aphp.tumorotek.model.code.CodeCommon;

/**
 * Interface regroupant les methodes communes aux codifications médicales.
 * Date: 21/05/2010
 * 
 * @author Mathieu BARTHELEMT
 * @version 2.0
 *
 */
public interface CodeCommonManager {

	/**
	 * Recherche toutes les instances de codes présentes dans la codification.
	 * @return List contenant les codes.
	 */
	List< ? extends CodeCommon> findAllObjectsManager();

	/**
	 * Recherche les codes dont le code est like celui passé
	 * en paramètre.
	 * @param code Code pour lequel on recherche des codes.
	 * @param boolean exactMatch
	 * @return Liste de codes.
	 */
	List< ? extends CodeCommon> findByCodeLikeManager(String code, 
														boolean exactMatch);

	/**
	 * Recherche les codes dont le libellé est like celui passé en
	 * paramètre.
	 * @param libelle Description du code que l'on recherche.
	 * @param boolean exactMatch
	 * @return une liste de codes.
	 */
	List< ? extends CodeCommon> findByLibelleLikeManager(String libelle, 
														boolean exactMatch);
	
}