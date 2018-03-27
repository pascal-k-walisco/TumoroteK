package fr.aphp.tumorotek.action.patient.ofsep;

import org.zkoss.zk.ui.Component;

import fr.aphp.tumorotek.action.patient.FichePatientStatic;

public class FichePatientStaticOFSEP extends FichePatientStatic
{

   /**
    * 
    */
   private static final long serialVersionUID = 1L;

   @Override
   public void doAfterCompose(final Component comp) throws Exception{

      super.doAfterCompose(comp);

      addMaladie.setVisible(false);
   }
}
