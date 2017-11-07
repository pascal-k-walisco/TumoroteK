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
package fr.aphp.tumorotek.manager.impl.stockage;

import java.util.ArrayList;
import java.util.List;

import fr.aphp.tumorotek.dao.stockage.EnceinteDao;
import fr.aphp.tumorotek.dao.stockage.TerminaleDao;
import fr.aphp.tumorotek.manager.stockage.CheckPositionManager;
import fr.aphp.tumorotek.model.stockage.Conteneur;
import fr.aphp.tumorotek.model.stockage.Enceinte;
import fr.aphp.tumorotek.model.stockage.Terminale;

public class CheckPositionManagerImpl implements CheckPositionManager {
	
	private TerminaleDao terminaleDao;
	private EnceinteDao enceinteDao;
	
	public void setTerminaleDao(TerminaleDao tDao) {
		this.terminaleDao = tDao;
	}

	public void setEnceinteDao(EnceinteDao eDao) {
		this.enceinteDao = eDao;
	}


	@Override
	public Boolean checkPositionLibreInEnceinteManager(Enceinte enceinte,
			Integer position, Integer terminaleId,
			Integer enceinteId) {
		// si l'enceinte est la position ne sont pas nulles
		if (enceinte != null && position != null) {
			List<Terminale> terms = new ArrayList<Terminale>();
			List<Enceinte> encs = new ArrayList<Enceinte>();
			
			// si un id a exclure est spécifié
			if (terminaleId != null) {
				terms = terminaleDao.findByEnceinteAndPositionExcludedId(
						enceinte, position, terminaleId);
			} else {
				terms = terminaleDao
					.findByEnceinteAndPosition(enceinte, position);
			}
			
			// si un id a exclure est spécifié
			if (enceinteId != null) {
				encs = enceinteDao.findByEnceintePereAndPositionExcludedId(
						enceinte, position, enceinteId);
			} else {
				encs = enceinteDao
					.findByEnceintePereAndPosition(enceinte, position);
			}
			
			return (terms.size() == 0 && encs.size() == 0);
		} else {
			return false;
		}
		
	}
	
	@Override
	public Boolean checkPositionLibreInConteneurManager(Conteneur conteneur,
			Integer position, Integer enceinteId) {
		// si le conteneur et la position ne sont pas nulls
		if (conteneur != null && position != null) {
			List<Enceinte> encs = new ArrayList<Enceinte>();
			
			// si un id a exclure est spécifié
			if (enceinteId != null) {
				encs = enceinteDao.findByConteneurAndPositionExcludedId(
						conteneur, position, enceinteId);
			} else {
				encs = enceinteDao
					.findByConteneurAndPosition(conteneur, position);
			}
			
			return (encs.size() == 0);
		} else {
			return false;
		}
	}
}
