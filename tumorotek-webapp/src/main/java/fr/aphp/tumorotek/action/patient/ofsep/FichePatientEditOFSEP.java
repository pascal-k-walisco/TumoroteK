package fr.aphp.tumorotek.action.patient.ofsep;

import java.util.Calendar;
import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Intbox;

import fr.aphp.tumorotek.action.patient.FichePatientEdit;

public class FichePatientEditOFSEP extends FichePatientEdit
{
   private static final long serialVersionUID = 1L;
   
   protected Intbox dateNaisYearBox;
   
   @Override
   public void doAfterCompose(final Component comp) throws Exception{
      super.doAfterCompose(comp);
   }
   
   @Override
   public void switchToEditMode(){
      super.switchToEditMode();
      
      //Récupération de l'année de naissance
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(dateNaisBox.getValue());
      dateNaisYearBox.setValue(calendar.get(Calendar.YEAR));
   }
   
   /**
    * Méthode appelée après la saisie d'une valeur dans le champ
    * nomBox. Cette valeur sera utilisée pour prenomBox et nipBox.
    */
   public void onBlur$nomBox(){
      nomBox.setValue(nomBox.getValue().toUpperCase().trim());
	  nipBox.setValue(nomBox.getValue().toUpperCase().trim());
	  prenomBox.setValue(nomBox.getValue().toUpperCase().trim());
   }
   
   /**
    * Méthode appelée après la saisie d'une valeur dans le champ
    * dateNaisYearBox. Cette valeur sera utilisée pour dateNaisBox.
    */
   public void onBlur$dateNaisYearBox(){
	  Integer year = dateNaisYearBox.getValue();
	  Calendar calendar = Calendar.getInstance();
	  calendar.set(year, 0, 1);
	  Date date = calendar.getTime();
	  dateNaisBox.setValue(date);
   }
}
