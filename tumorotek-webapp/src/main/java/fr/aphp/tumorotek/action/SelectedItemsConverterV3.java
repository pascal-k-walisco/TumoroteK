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
package fr.aphp.tumorotek.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

/**
 * Convert selected items to bean and vice versa.
 * @see http://www.zkoss.org/forum/listComment/7871/1/20
 * @see http://zh.zkoss.org/forum/listComment/5446
 *
 */
public class SelectedItemsConverterV3 implements TypeConverter, java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public Object coerceToBean(Object val, Component comp) {
		if (val instanceof Set) {
			HashSet<Object> selectedValues = new HashSet<Object>(); // should be a Set to be consistent with Listbox.getSelectedItems
			Set set = (Set) val;
			Iterator iter = set.iterator();
			while (iter.hasNext()) {
				Object item = iter.next();
				if (item instanceof Listitem) {
					Listitem listitem = (Listitem) item;
					selectedValues.add(listitem.getValue()); // todo: deal with posibility of model being set on Listbox
				} else throw new IllegalStateException("Expected Listitem, found " + item.getClass().getName());
			}
			return selectedValues;
		} else throw new IllegalStateException("Expected Set, found " + val.getClass().getName());
	}

	@SuppressWarnings("unchecked")
	public Object coerceToUi(Object val, Component comp) {
		if (val instanceof Collection) {
			Collection values = (Collection) val;
			if (comp instanceof Listbox) {
				HashSet<Listitem> selectedItems = new HashSet<Listitem>(); // have to return a Set since MultiBinding/Listbox.get/setSelectedItems does same
				Listbox listbox = (Listbox) comp;
				listbox.clearSelection();
				Iterator iter = values.iterator();
				while (iter.hasNext()) {
					Object key = iter.next();
					Iterator iter2 = listbox.getChildren().iterator();
					while (iter2.hasNext()) {
						Listitem listitem = (Listitem) iter2.next();
						if (listitem.getValue().equals(key)) { // should work for integers, todo: deal with posibility of model being set on Listbox
							selectedItems.add(listitem);
						}
					}
				}
				return selectedItems;
			} else {
				throw new IllegalArgumentException("Expected Listbox, found " + comp.getClass().getName());
			}
		} else {
			throw new IllegalArgumentException("Expected Collection, found " + val.getClass().getName());
		}
	}		
}