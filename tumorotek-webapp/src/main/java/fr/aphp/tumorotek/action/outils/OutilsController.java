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
package fr.aphp.tumorotek.action.outils;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Div;

import fr.aphp.tumorotek.action.annotation.FicheAnnotation;
import fr.aphp.tumorotek.action.controller.AbstractFicheCombineController;
import fr.aphp.tumorotek.action.controller.AbstractFicheEditController;
import fr.aphp.tumorotek.action.controller.AbstractFicheModifMultiController;
import fr.aphp.tumorotek.action.controller.AbstractFicheStaticController;
import fr.aphp.tumorotek.action.controller.AbstractListeController2;
import fr.aphp.tumorotek.action.controller.AbstractObjectTabController;
import fr.aphp.tumorotek.model.TKdataObject;

public class OutilsController extends AbstractObjectTabController {

	private static final long serialVersionUID = 6928688177245348985L;
	
	// components
	private Div divOutil;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setStaticEditMode(false);
	}
	
	@Override
	public TKdataObject loadById(Integer id) {
		return null;
	}
	
	@Override
	public AbstractListeController2 getListe() {
		return ((ListeOutils) 
				this.self.getFellow("listeOutils")
				.getFellow("lwinOutils")
				.getAttributeOrFellow("lwinOutils$composer", true));
	}
	
	/**
	 * Méthode appelée pour afficher la fiche correspondant
	 * à un outil.
	 * @param Outil dont on souhaite afficher
	 * la fiche.
	 */
	public void showFicheOutil(Outil outil) {
		clearOutilDiv();
		if (outil.getNom().equals("fusionPatients")) {
			switchToFusionPatientsMode();
		} else if (outil.getNom().equals("fusionCollaborateurs")) {
			switchToFusionCollaborateursMode();
		}
	}
	
	/**
	 * Méthode permettant de passer en mode fusion des patients.
	 */
	public void switchToFusionPatientsMode() {	
//		divFusionPatients.setVisible(true);
		if (!divOutil.hasFellow("winContexteFusion")) {
			Executions
				.createComponents("/zuls/outils/FicheFusionPatients.zul",
					divOutil, null);
		}
	}
	
	/**
	 * Méthode permettant de passer en mode fusion de collaborateurs.
	 */
	public void switchToFusionCollaborateursMode() {	
//		divFusionPatients.setVisible(true);
		if (!divOutil.hasFellow("fwinFusionPatients")) {
			Executions
				.createComponents("/zuls/outils/contexte/fusion/Fusion.zul", divOutil, null);
		}
	}
	
	/**
	 * Méthode qui nettoie le div contenant la fusion des
	 * patients.
	 */
	public void clearOutilDiv() {
		Components.removeAllChildren(divOutil);
		// divOutil.setVisible(false);
	}

	@Override
	public FicheAnnotation getFicheAnnotation() {
		return null;
	}

	@Override
	public AbstractFicheCombineController getFicheCombine() {
		return null;
	}

	@Override
	public AbstractFicheEditController getFicheEdit() {
		return null;
	}

	@Override
	public AbstractFicheModifMultiController getFicheModifMulti() {
		return null;
	}

	@Override
	public AbstractFicheStaticController getFicheStatic() {
		return null;
	}
}
