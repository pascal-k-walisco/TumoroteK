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
package fr.aphp.tumorotek.dao.interfacage.scan;

import java.util.List;

import fr.aphp.tumorotek.dao.GenericDaoJpa;
import fr.aphp.tumorotek.model.interfacage.scan.ScanDevice;
import fr.aphp.tumorotek.model.interfacage.scan.ScanTerminale;

/**
 * 
 * Interface pour le DAO du bean de domaine ScanTerminale.
 * Interface créée le 24/04/2016.
 * Persiste un scan d'une boite sous la forme d'une entrée ScanTerminale + 
 * liste de ScanTube.
 * 
 * @author Mathieu BARTHELEMY
 * @version 2.1
 *
 */
public interface ScanTerminaleDao extends GenericDaoJpa<ScanTerminale, Integer> {
	
	/**
	 * Renvoie les scans de boites pour le scanner passé en paramètre dans l'ordre 
	 * inverse de leur création (du plus récent au plus ancien)
	 * @param scanDevice scanner
	 * @return List<ScanTerminale>
	 */
	public List<ScanTerminale> findByScanDevice(ScanDevice device);
	
	/**
	 * Renvoie le liste des codes correspondant au tubes scannées pour la 
	 * boite passée en paramètre.
	 * @param sT scanTerminale
	 * @return List<String> codes
	 */
	
	public List<String> findTKObjectCodes(ScanTerminale sT);

}
