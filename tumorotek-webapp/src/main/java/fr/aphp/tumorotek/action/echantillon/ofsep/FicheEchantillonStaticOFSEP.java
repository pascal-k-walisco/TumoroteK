package fr.aphp.tumorotek.action.echantillon.ofsep;

import org.zkoss.util.resource.Labels;

import fr.aphp.tumorotek.action.echantillon.FicheEchantillonStatic;
import fr.aphp.tumorotek.action.utils.PrelevementUtils;
import fr.aphp.tumorotek.decorator.ObjectTypesFormatters;

public class FicheEchantillonStaticOFSEP extends FicheEchantillonStatic
{

   /**
    * 
    */
   private static final long serialVersionUID = 1L;
   
   /**
    * Méthode initialisant le champs de formulaire pour le délai
    * de congélation.
    */
   @Override
   public void initDelaiCgl(){

      if(this.echantillon.getDelaiCgl() != null && this.echantillon.getDelaiCgl() > -1){
         delaiLabel = ObjectTypesFormatters.getHeureMinuteLabel(this.echantillon.getDelaiCgl().intValue());
         if(this.echantillon.getDelaiCgl().intValue() > 240 ){ 
         	delaiCglLabel.setStyle("color:red;"); 
   		 }else {
   			delaiCglLabel.setStyle("color:green;"); 
   		 }
         //TODO seuil echantillon
         /*if( this.echantillon.getDelaiCgl().intValue() > this.echantillon.getEchantillonType().getSeuil() ){ 
        	delaiCglLabel.setStyle("color:red;"); 
  		 }else {
  			delaiCglLabel.setStyle("color:green;"); 
  		 }*/
      }else{
         delaiLabel = Labels.getLabel("ficheEchantillon.delaiCgl.inconnu");
      }
   }
   
   //Retroune uniquement le 'nom' du patient (id edmus)
   @Override
   public String getNomPatient(){
      if(getPrelevement() != null){
         return getPrelevement().getMaladie().getPatient().getNom();
      }
      return "";
   }
}
