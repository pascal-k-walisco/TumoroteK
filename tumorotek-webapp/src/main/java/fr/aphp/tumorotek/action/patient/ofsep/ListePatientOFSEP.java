package fr.aphp.tumorotek.action.patient.ofsep;

import org.zkoss.zk.ui.Component;

import fr.aphp.tumorotek.action.patient.ListePatient;

public class ListePatientOFSEP extends ListePatient
{
   private static final long serialVersionUID = 1L;
   
   @Override
   public void doAfterCompose(final Component comp) throws Exception{
      super.doAfterCompose(comp);

      addNew.setVisible(false);
   }
}
