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
package fr.aphp.tumorotek.action.io;

import org.zkoss.zk.ui.Component;

import fr.aphp.tumorotek.action.ManagerLocator;
import fr.aphp.tumorotek.action.annotation.FicheAnnotation;
import fr.aphp.tumorotek.action.controller.AbstractFicheCombineController;
import fr.aphp.tumorotek.action.controller.AbstractFicheEditController;
import fr.aphp.tumorotek.action.controller.AbstractFicheModifMultiController;
import fr.aphp.tumorotek.action.controller.AbstractFicheStaticController;
import fr.aphp.tumorotek.action.controller.AbstractListeController2;
import fr.aphp.tumorotek.action.controller.AbstractObjectTabController;
import fr.aphp.tumorotek.model.TKdataObject;
import fr.aphp.tumorotek.model.io.export.Recherche;

public class RechercheController extends AbstractObjectTabController {

	private static final long serialVersionUID = 4935823085963165549L;
	
	private Recherche recherche;
	private RechercheComplexeController topController;
	
	@Override
	public TKdataObject loadById(Integer id) {
		return ManagerLocator.getRechercheManager().findByIdManager(id);
	}

	public RechercheComplexeController getTopController() {
		return topController;
	}

	public void setTopController(RechercheComplexeController t) {
		this.topController = t;
	}

	public Recherche getRecherche() {
		return recherche;
	}

	public void setRecherche(Recherche c) {
		this.recherche = c;
	}

	/**
	 * Recupere le controller du composant representant la liste des
	 * recherches.
	 * @param event Event
	 * @return liste ListeRecherches
	 */
	public ListeRecherches getListeRecherches() {
		return ((ListeRecherches) 
				this.self.getFellow("listeRecherches")
				.getFellow("lwinRecherches")
				.getAttributeOrFellow("lwinRecherches$composer", true));
	}
	
	@Override
	public AbstractFicheCombineController getFicheCombine() {
		return ((FicheRecherche) 
				this.self.getFellow("ficheRecherche")
				.getFellow("winFicheRecherche")
				.getAttributeOrFellow("winFicheRecherche$composer", true));
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		setEntiteTab(ManagerLocator.getEntiteManager()
				.findByNomManager("Requete").get(0));
		super.doAfterCompose(comp);
		
		if (mainBorder != null) {
			mainBorder.setHeight(getMainWindow().getPanelHeight() - 110 + "px");
		}
		
		getFicheCombine().setObjectTabController(this);
		setStaticEditMode(false);
	}
	
	@Override
	public void reset() {
		// droits
		getListe().drawActionsButtons();
		getListe().applyDroitsOnListe();
		getListe().initObjectsBox();
		if (!sessionScope.containsKey("ToutesCollections")) {
			getFicheCombine().drawActionsButtons("Requete");
		} else {
			getFicheCombine().setCanNew(false);
			getFicheCombine().setCanEdit(false);
			getFicheCombine().setCanDelete(false);
		}
		
		clearStaticFiche();
	}
	
	@Override
	public void switchToFicheAndListeMode() {
		if (listeRegion.getWidth().equals("100%")) {
			listeRegion.setWidth("50%");
		}
	}

	@Override
	public AbstractFicheStaticController getFicheStatic() {
		return null;
	}

	@Override
	public AbstractListeController2 getListe() {
		return ((ListeRecherches) 
				this.self.getFellow("listeRecherches")
				.getFellow("lwinRecherches")
				.getAttributeOrFellow("lwinRecherches$composer", true));
	}

	@Override
	public FicheAnnotation getFicheAnnotation() {
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
}
