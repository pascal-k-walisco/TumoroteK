
package fr.aphp.tumorotek.action.echantillon.ofsep;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import fr.aphp.tumorotek.action.echantillon.FicheEchantillonEdit;
import fr.aphp.tumorotek.manager.exception.DoublonFoundException;
import fr.aphp.tumorotek.model.TKdataObject;

public class FicheEchantillonEditOFSEP extends FicheEchantillonEdit
{

   private static final long serialVersionUID = 1L;
   
   @Override
   public void onSelect$typesBoxEchan(){
      if(getSelectedType() == null){
         Clients.scrollIntoView(typesBoxEchan);
         throw new WrongValueException(typesBoxEchan, Labels.getLabel("ficheEchantillon.error.type"));
      }else{
         Clients.clearWrongValue(typesBoxEchan);
         calculDelaiCgl();
      }
   }
   
   /**
    * Méthode initialisant le champs de formulaire pour le délai
    * de congélation.
    */
   @Override
   public void initDelaiCgl(){

      if(this.echantillon.getDelaiCgl() != null && this.echantillon.getDelaiCgl() < 0){
         this.echantillon.setDelaiCgl(null);
      }

      if(this.echantillon.getDelaiCgl() != null){
         final Float heure = this.echantillon.getDelaiCgl() / 60;
         if(heure > 0){
            heureDelai = heure.intValue();
            minDelai = this.echantillon.getDelaiCgl().intValue() - (heureDelai * 60);
         }else{
            heureDelai = 0;
            minDelai = this.echantillon.getDelaiCgl().intValue();
         }
         
       	 if(selectedType.getNom().equals("PBMC") || selectedType.getNom().equals("CELLULES")) {
       		if( this.echantillon.getDelaiCgl().intValue() > 1440 ){ 
       			delaiCglBox.setStyle("border:1px solid red;"); 
       		}else {
       			delaiCglBox.setStyle("border:1px solid green;"); 
       		}
       	 }else {
       	    if( this.echantillon.getDelaiCgl().intValue() > 240 ){ 
       	    	delaiCglBox.setStyle("border:1px solid red;"); 
       	    }else {
       	    	delaiCglBox.setStyle("border:1px solid green;"); 
       	    }  	  
       	 }
       	 // TODO ajouter un seuil pour le thésaurus d'échantillons
       	 /*if( this.echantillon.getDelaiCgl().intValue() > selectedType.getSeuil() ){ 
     	    delaiCglBox.setStyle("border:1px solid red;"); 
     	 }else {
     		delaiCglBox.setStyle("border:1px solid green;"); 
     	 }*/
      }else{
         calculDelaiCgl();
      }
   }
   
   /**
    * Cette méthode calcule automatiquement le délaiCgl.
    * Calcule la différence entre la date de prélèvement, la date associée
    * à la congélation dans la fiche labointer ou la date de stockage de l'
    * echantillon.
    * Ces dates doivent contenir des heures/minutes pour etre prise en
    * compte dans le calcul.
    */
   @Override
   public void calculDelaiCgl(){
      setHeureDelai(null);
      setMinDelai(null);
      // on vérifie que la date de prlvt est exploitables
      if(getParentObject() != null && getParentObject().getDatePrelevement() != null
         && (getParentObject().getDatePrelevement().get(Calendar.HOUR_OF_DAY) != 0
            || getParentObject().getDatePrelevement().get(Calendar.MINUTE) != 0
            || getParentObject().getDatePrelevement().get(Calendar.SECOND) != 0)){

         long milli = -1;
         
         if(this.echantillon.getDateStock() != null){
            if(this.echantillon.getDateStock().get(Calendar.HOUR_OF_DAY) != 0
               || this.echantillon.getDateStock().get(Calendar.MINUTE) != 0
               || this.echantillon.getDateStock().get(Calendar.SECOND) != 0){
               milli =
                  this.echantillon.getDateStock().getTimeInMillis() - getParentObject().getDatePrelevement().getTimeInMillis();
            }
         }
         if(milli > 0){
            final Float min = (float) (milli / 60000);

            final Float heure = min / 60;
            if(heure > 0){
               setHeureDelai(heure.intValue());
               setMinDelai(min.intValue() - (getHeureDelai() * 60));
            }else{
               setHeureDelai(0);
               setMinDelai(min.intValue());
            }
         }
         
         if(getHeureDelai()!=null && getMinDelai()!=null && selectedType!=null) {
       	  if(selectedType.getNom().equals("PBMC") || selectedType.getNom().equals("CELLULES")) {
       		 if( ( getHeureDelai()*60 + getMinDelai() ) > 1440 ){ 
       			 delaiCglBox.setStyle("border:1px solid red;"); 
       		 }else {
       			 delaiCglBox.setStyle("border:1px solid green;"); 
       		 }
       	  }else {
       	     if( ( getHeureDelai()*60 + getMinDelai() ) > 240 ){ 
       	    	 delaiCglBox.setStyle("border:1px solid red;"); 
         		 }else {
         			delaiCglBox.setStyle("border:1px solid green;"); 
         		 }  	  
       	  }
       	  // TODO ajouter un seuil pour le thésaurus d'échantillons
       	  /*if( ( getHeureDelai()*60 + getMinDelai() ) > selectedType.getSeuil() ){ 
     	         delaiCglBox.setStyle("border:1px solid red;"); 
     		  }else {
     		     delaiCglBox.setStyle("border:1px solid green;"); 
     		  }*/
         }else {
       	  delaiCglBox.setStyle("border:none;");
         }
      }
   }
   
   @Override
   public boolean onLaterUpdate(){

      if(getSelectedType() == null){
         // ferme wait message
         Clients.clearBusy();
         throw new WrongValueException(typesBoxEchan, Labels.getLabel("ficheEchantillon.error.type"));
      }
      
      if(getSelectedQuantiteUnite() == null){
    	 Clients.clearBusy();
    	 throw new WrongValueException(quaniteUnitesBoxEchan, Labels.getLabel("ficheEchantillon.error.qantiteUnite"));
      }
       
      if(getPrepas() == null){
    	 Clients.clearBusy();
    	 throw new WrongValueException(prepasBox, Labels.getLabel("ficheEchantillon.error.prepas"));
      }
      
      if(conformeTraitementBoxOui.isChecked()){
         getEchantillon().setConformeTraitement(true);
      }else if(conformeTraitementBoxNon.isChecked()){
         getEchantillon().setConformeTraitement(false);
      }else{
         getEchantillon().setConformeTraitement(null);
         throw new WrongValueException(conformeTraitementBoxNon, Labels.getLabel("ficheEchantillon.error.confTtmt"));
      }
      
      try{
         updateObjectWithAnnots();
         // updateObject();

         // update de la liste
         if(getObjectTabController().getListe() != null){
            getObjectTabController().getListe().updateObjectGridList(getObject());
         }

         if(getParentObject() != null){
            if(getPrelevementController() != null){
               getPrelevementController().getListe().updateObjectGridListFromOtherPage(getParentObject(), true);

               //update patient
               if(getParentObject().getMaladie() != null){
                  if(!getPrelevementController().getReferencingObjectControllers().isEmpty()){
                     getPrelevementController().getReferencingObjectControllers().get(0).getListe()
                        .updateObjectGridListFromOtherPage(getParentObject().getMaladie().getPatient(), true);
                  }
               }
            }
         }

         // update de la liste des enfants et l'enfant en fiche
         getObjectTabController()
            .updateReferencedObjects((List<TKdataObject>) getObjectTabController().getChildrenObjects(getObject()));

         // commande le passage en mode statique
         getObjectTabController().onEditDone(getObject());

         // ferme wait message
         Clients.clearBusy();

         return true;
      }catch(final DoublonFoundException re){
         Clients.clearBusy();

         final HashMap<String, Object> map = new HashMap<>();
         map.put("title", Labels.getLabel("error.unhandled"));
         map.put("message", handleExceptionMessage(re));
         map.put("exception", re);

         final Window window = (Window) Executions.createComponents("/zuls/component/DynamicMultiLineMessageBox.zul", null, map);
         window.doModal();

         return false;
      }catch(final RuntimeException re){
         // ferme wait message
         Clients.clearBusy();
         Messagebox.show(handleExceptionMessage(re), "Error", Messagebox.OK, Messagebox.ERROR);
         return false;
      }
   }
}
