package fr.aphp.tumorotek.action.patient.ofsep;

import fr.aphp.tumorotek.action.patient.FichePatientEdit;

public class FichePatientEditOFSEP extends FichePatientEdit
{
   private static final long serialVersionUID = 1L;
   
   /**
    * Méthode appelée après la saisie d'une valeur dans le champ
    * nomBox. Cette valeur sera utilisée pour prenomBox et nipBox.
    */
   public void onBlur$nomBox(){
      getNomBox().setValue(getNomBox().getValue().toUpperCase().trim());
	  getNipBox().setValue(getNomBox().getValue().toUpperCase().trim());
	  getPrenomBox().setValue(getNomBox().getValue().toUpperCase().trim());
   }
}
