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

import java.io.IOException;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treerow;

import fr.aphp.tumorotek.action.controller.AbstractController;

public class TreeController extends AbstractController {

	private static final long serialVersionUID = 1L;
	
	private Tree tree;
	
	public void setTree(Tree t) {
		this.tree = t;
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		Treechildren child1 = new Treechildren();
		child1.setParent(tree);
		Treeitem item1 = new Treeitem();
		item1.setParent(child1);
		Treerow row1 = new Treerow();
		row1.setId("row1");
		row1.setDraggable("true");
		row1.setDroppable("true");
		row1.getDefinition().setApply("MyDropEvent");
		row1.addEventListener(Events.ON_DROP, new EventListener() {
			public void onEvent(Event e) throws Exception {
				DropEvent event = (DropEvent) e;
				Component dragged = event.getDragged();
				Component target = event.getTarget();
				echanger(target, dragged);
			}
		});
		row1.setParent(item1);
		Treecell cell1 = new Treecell("Critère 1");
		cell1.setParent(row1);
		Treeitem item2 = new Treeitem();
		item2.setParent(child1);
		Treerow row2 = new Treerow();
		row2.setParent(item2);
		Treecell cell2 = new Treecell("OU");
		cell2.setParent(row2);
		
		Treeitem item3 = new Treeitem();
		item3.setParent(child1);
		Treechildren child2 = new Treechildren();
		child2.setParent(item3);
		Treeitem item4 = new Treeitem();
		item4.setParent(child2);
		Treerow row3 = new Treerow();
		row3.setId("row3");
		row3.setDraggable("true");
		row3.setDroppable("true");
		row3.getDefinition().setApply("MyDropEvent");
		row3.addEventListener(Events.ON_DROP, new EventListener() {
			@SuppressWarnings("unchecked")
			public void onEvent(Event event) throws Exception {
				DropEvent dropevent = ((DropEvent) event);
				List list = dropevent.getTarget().getParent().getChildren();
				for (Object element : list) {
					if (element instanceof Treechildren) {
						((Treechildren) element).appendChild((dropevent
								.getDragged().getParent()));
					}
				}
			}
		});
		row3.addEventListener(Events.ON_DROP, new EventListener() {
			public void onEvent(Event e) throws Exception {
				DropEvent event = (DropEvent) e;
				Component dragged = event.getDragged();
				Component target = event.getTarget();
				echanger(target, dragged);
			}
		});
		row3.setParent(item4);
		Treecell cell3 = new Treecell("Critère 5");
		cell3.setParent(row3);
		Treeitem item5 = new Treeitem();
		item5.setParent(child2);
		Treerow row4 = new Treerow();
		row4.setParent(item5);
		Treecell cell4 = new Treecell("ET");
		cell4.setParent(row4);
		Treeitem item6 = new Treeitem();
		item6.setParent(child2);
		Treerow row5 = new Treerow();
		row5.setId("row5");
		row5.setDraggable("true");
		row5.setDroppable("true");
		row5.getDefinition().setApply("MyDropEvent");
		row5.addEventListener(Events.ON_DROP, new EventListener() {
			public void onEvent(Event e) throws Exception {
				DropEvent event = (DropEvent) e;
				Component dragged = event.getDragged();
				Component target = event.getTarget();
				echanger(target, dragged);
			}
		});
		row5.setParent(item6);
		Treecell cell5 = new Treecell("Critère 4");
		cell5.setParent(row5);
		
		
	}
	
	
	private void echanger(Component c1, Component c2) throws IOException {
		Component parent1 = (Component) c1.getParent();
		Component parent2 = (Component) c2.getParent();
		parent1.removeChild(c1);
		parent2.removeChild(c2);
		parent1.appendChild(c2);
		parent2.appendChild(c1);
	}
	
	
}
