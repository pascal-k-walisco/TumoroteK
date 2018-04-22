package fr.aphp.tumorotek.action.patient.ofsep;

import java.util.Calendar;

import org.zkoss.zk.ui.Component;

import fr.aphp.tumorotek.action.patient.FichePatientStatic;

public class FichePatientStaticOFSEP extends FichePatientStatic
{
   private static final long serialVersionUID = 1L;
   
   @Override
   public void doAfterCompose(final Component comp) throws Exception{

      super.doAfterCompose(comp);
      // addMaladie.setVisible(false);
      
   }
   
   public String getDateNaisFormatted(){
      if(isAnonyme()){
         //makeLabelAnonyme(dateNaisLabel, false);
         return getAnonymeString();
      }else{
  		if (this.patient.getDateNaissance() != null) {
  			Calendar c = Calendar.getInstance();
  			c.setTime(this.patient.getDateNaissance());
  			return String.valueOf(c.get(Calendar.YEAR));
  		}
  		return null;
      }
   }
}
