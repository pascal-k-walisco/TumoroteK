package fr.aphp.tumorotek.action.prelevement.ofsep;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import fr.aphp.tumorotek.action.ManagerLocator;
import fr.aphp.tumorotek.action.annotation.FicheAnnotation;
import fr.aphp.tumorotek.action.patient.FicheMaladie;
import fr.aphp.tumorotek.action.patient.FichePatientEdit;
import fr.aphp.tumorotek.action.patient.ofsep.FichePatientEditOFSEP;
import fr.aphp.tumorotek.action.prelevement.FichePrelevementEdit;
import fr.aphp.tumorotek.action.prelevement.ReferenceurPatient;
import fr.aphp.tumorotek.component.CalendarBox;
import fr.aphp.tumorotek.manager.exception.DoublonFoundException;
import fr.aphp.tumorotek.model.TKAnnotableObject;
import fr.aphp.tumorotek.model.TKdataObject;
import fr.aphp.tumorotek.model.coeur.prelevement.LaboInter;
import fr.aphp.tumorotek.model.coeur.prelevement.Prelevement;
import fr.aphp.tumorotek.model.contexte.Collaborateur;
import fr.aphp.tumorotek.model.contexte.Transporteur;
import fr.aphp.tumorotek.model.systeme.Unite;
import fr.aphp.tumorotek.webapp.general.SessionUtils;

/**
 * TODO Javadoc
 */
public class FichePrelevementEditOFSEP extends FichePrelevementEdit
{
   private static final long serialVersionUID = 1L;

   // protected CalendarBox datePeremptionCalBox;

   @Override
   public void doAfterCompose(final Component comp) throws Exception{
      super.doAfterCompose(comp);
      //datePeremptionCalBox.setDisabled(true);
      if(prelevement.getConsentType() == null){
    	  this.selectedConsentType = consentTypes.get(2);
      }
   }
   
   @Override
   public void onClick$create(){

      if(selectedNature == null){
         Clients.scrollIntoView(naturesBoxPrlvt);
         throw new WrongValueException(naturesBoxPrlvt, Labels.getLabel("fichePrelevement.error.nature"));
      }
      if(selectedMode == null){
         Clients.scrollIntoView(modesBoxPrlvt);
         throw new WrongValueException(modesBoxPrlvt, Labels.getLabel("fichePrelevement.error.mode"));
      }
      if(selectedEtablissement == null){
         Clients.scrollIntoView(etabsBoxPrlvt);
         throw new WrongValueException(etabsBoxPrlvt, Labels.getLabel("fichePrelevement.error.etabs"));
      }
      /*if(selectedConsentType == null){
         Clients.scrollIntoView(consentTypesBoxPrlvt);
         throw new WrongValueException(consentTypesBoxPrlvt, Labels.getLabel("fichePrelevement.error.consenType"));
      }*/

      // valide les dates donc
      validateAllDateComps();

      // récupere les objets à partir des formulaires embarqués
      setEmbeddedObjects();

      if(warnNoPatient()){ // warning si pas patient
         super.onClick$create();
      }
   }

   @Override
   public void onClick$validate(){

      if(selectedNature == null){
         Clients.scrollIntoView(naturesBoxPrlvt);
         throw new WrongValueException(naturesBoxPrlvt, Labels.getLabel("fichePrelevement.error.nature"));
      }
      if(selectedMode == null){
         Clients.scrollIntoView(modesBoxPrlvt);
         throw new WrongValueException(modesBoxPrlvt, Labels.getLabel("fichePrelevement.error.mode"));
      }
      if(selectedEtablissement == null){
         Clients.scrollIntoView(etabsBoxPrlvt);
         throw new WrongValueException(etabsBoxPrlvt, Labels.getLabel("fichePrelevement.error.etabs"));
      }
      /*if(selectedConsentType == null){
         Clients.scrollIntoView(consentTypesBoxPrlvt);
         throw new WrongValueException(consentTypesBoxPrlvt, Labels.getLabel("fichePrelevement.error.consenType"));
      }*/

      // valide les dates donc
      validateAllDateComps();

      super.onClick$validate();
   }
   
   @Override
   public void createNewObject(){

      final List<File> filesCreated = new ArrayList<>();

      try{
         setEmptyToNulls();
         setFieldsToUpperCase();

         // on ne change pas les associations qui ne sont pas présentes
         // dans le formulaire
         final Transporteur transporteur = this.prelevement.getTransporteur();
         final Collaborateur operateur = this.prelevement.getOperateur();
         final Unite quantiteUnite = this.prelevement.getQuantiteUnite();

         getObject().setRisques(findSelectedRisques());
         getObject().setLaboInters(new HashSet<LaboInter>());

         // update de l'objet
         ManagerLocator.getPrelevementManager().createObjectManager(prelevement,
            SessionUtils.getSelectedBanques(sessionScope).get(0), selectedNature, this.maladie, selectedConsentType,
            selectedCollaborateur, selectedService, selectedMode, selectedConditType, selectedConditMilieu, transporteur,
            operateur, quantiteUnite, null, getObjectTabController().getFicheAnnotation().getValeursToCreateOrUpdate(),
            filesCreated, SessionUtils.getLoggedUser(sessionScope), true, SessionUtils.getSystemBaseDir(), false);

         if(null != referenceur){
            if(this.patientEmbedded){

               final Component embeddedPatient =
                  referenceur.getFellow("winRefPatient").getFellow("newPatientDiv").getFellow("fichePatientDiv").getFirstChild();
               // enregistre le formulaire dans le backing-bean
               // meme si il est vide...
               ((FichePatientEdit) embeddedPatient.getFellow("fwinPatientEditOFSEP")
                  .getAttributeOrFellow("fwinPatientEditOFSEP$composer", true)).getBinder()
                     .saveComponent(embeddedPatient.getFellow("fwinPatientEditOFSEP"));

            }
         }
         // rafraichit la maladie pour avoir les references
         this.maladie = ManagerLocator.getPrelevementManager().getMaladieManager(this.prelevement);

         // suppression du patientSip
         getObjectTabController().removePatientSip();
         // gestion de la communication des infos et de l'éventuel dossier externe
         getObjectTabController().handleExtCom(getObject(), getObjectTabController());
      }catch(final RuntimeException re){
         for(final File f : filesCreated){
            f.delete();
         }
         throw (re);
      }
   }
   
   @Override
   protected void setEmbeddedObjects(){
      if(referenceur != null){

         // vérifie la présence du formulaire embarqué patient
         if(this.patientEmbedded){
            // maladie associee
            final Component embeddedMaladie = referenceur.getFellow("winRefPatient").getFellow("newPatientDiv")
               .getFellow("ficheMaladieWithPatientDiv").getFirstChild();

            // force la validation des dates maladies
            // car les dates de naissance
            // et deces du patient ont pu être modifée à posteriori
            ((FicheMaladie) embeddedMaladie.getFellow("fwinMaladie").getAttributeOrFellow("fwinMaladie$composer", true))
               .validateAllDateComps();

            setMaladieFromEmbedded(embeddedMaladie);

            final Component embeddedPatient =
               referenceur.getFellow("winRefPatient").getFellow("newPatientDiv").getFellow("fichePatientDiv").getFirstChild();
            // enregistre le formulaire dans le backing-bean
            // meme si il est vide...
            ((FichePatientEdit) embeddedPatient.getFellow("fwinPatientEditOFSEP")
               .getAttributeOrFellow("fwinPatientEditOFSEP$composer", true)).getBinder()
                  .saveComponent(embeddedPatient.getFellow("fwinPatientEditOFSEP"));

            // prepare les valeurs des attributs
            ((FichePatientEdit) embeddedPatient.getFellow("fwinPatientEditOFSEP")
               .getAttributeOrFellow("fwinPatientEditOFSEP$composer", true)).prepareDataBeforeSave(true);

            // re-assigne la reference ndaBox vers le composant embedded
            this.ndaBox = (Textbox) embeddedPatient.getFellow("fwinPatientEditOFSEP").getFellow("ndaBox");

            // vérifie la présence du formulaire embarqué maladie
         }else{
            if(this.maladieEmbedded){
               final Component embedded = referenceur.getFellow("winRefPatient").getFellow("embeddedFicheMaladieRow")
                  .getFellow("embeddedFicheMaladieDiv");
               setMaladieFromEmbedded(embedded);
            }else if(((ReferenceurPatient) referenceur.getAttributeOrFellow("winRefPatient$composer", true)).getNoRadio()
               .isChecked()
               || ((ReferenceurPatient) referenceur.getAttributeOrFellow("winRefPatient$composer", true))
                  .getSelectedPatient() == null){
               this.maladie = null;
               this.prelevement.setMaladie(null);
            }
            if(((ReferenceurPatient) referenceur.getAttributeOrFellow("winRefPatient$composer", true)).getNdaBox()
               .getValue() != null){
               this.ndaBox = ((ReferenceurPatient) referenceur.getAttributeOrFellow("winRefPatient$composer", true)).getNdaBox();
            }
         }
      }
   }
   
   /**
    * Passe à la fiche des labos inters.
    *
    * @version 2.1
    */
   @Override
   public void onClick$next(){
      // validation des champs obligatoires
      if(selectedNature == null){
         Clients.scrollIntoView(naturesBoxPrlvt);
         throw new WrongValueException(naturesBoxPrlvt, Labels.getLabel("fichePrelevement.error.nature"));
      }
      Clients.clearWrongValue(naturesBoxPrlvt);
      if(selectedMode == null){
         Clients.scrollIntoView(modesBoxPrlvt);
         throw new WrongValueException(modesBoxPrlvt, Labels.getLabel("fichePrelevement.error.mode"));
      }
      Clients.clearWrongValue(modesBoxPrlvt);
      if(selectedEtablissement == null){
         Clients.scrollIntoView(etabsBoxPrlvt);
         throw new WrongValueException(etabsBoxPrlvt, Labels.getLabel("fichePrelevement.error.etabs"));
      }
      Clients.clearWrongValue(etabsBoxPrlvt);
      /*if(selectedConsentType == null){
         Clients.scrollIntoView(consentTypesBoxPrlvt);
         throw new WrongValueException(consentTypesBoxPrlvt, Labels.getLabel("fichePrelevement.error.consenType"));
      }
      Clients.clearWrongValue(consentTypesBoxPrlvt);*/

      // valide les dates donc
      validateAllDateComps();

      // récupere les objets à partir des formulaires embarqués
      setEmbeddedObjects();

      if(warnNoPatient()){ // warning si pas patient

         // on remplit le prlvt en fonction des champs nulls
         setEmptyToNulls();

         // on recopie les sélections dans le prlvt
         if(this.prelevement.getBanque() == null){
            this.prelevement.setBanque(SessionUtils.getSelectedBanques(sessionScope).get(0));
         }
         this.prelevement.setNature(selectedNature);
         this.prelevement.setConsentType(selectedConsentType);
         this.prelevement.setPreleveur(selectedCollaborateur);
         this.prelevement.setServicePreleveur(selectedService);
         this.prelevement.setPrelevementType(selectedMode);
         this.prelevement.setConditType(selectedConditType);
         this.prelevement.setConditMilieu(selectedConditMilieu);
         getObject().setRisques(findSelectedRisques());

         this.prelevement.setMaladie(this.maladie);

         // on vérifie que le prélèvement n'a pas de doublons
         if(!ManagerLocator.getPrelevementManager().findDoublonManager(prelevement)){

            Clients.showBusy(Labels.getLabel("general.wait"));
            Events.echoEvent("onLaterNextStep", self, null);

         }else{
            try{
               throw new DoublonFoundException("Prelevement", "creation", prelevement.getCode(), null);
            }catch(final DoublonFoundException re){
               Clients.clearBusy();

               final HashMap<String, Object> map = new HashMap<>();
               map.put("title", Labels.getLabel("error.unhandled"));
               map.put("message", handleExceptionMessage(re));
               map.put("exception", re);

               final Window window =
                  (Window) Executions.createComponents("/zuls/component/DynamicMultiLineMessageBox.zul", null, map);
               window.doModal();
            }catch(final RuntimeException re){
               Messagebox.show(handleExceptionMessage(re), "Error", Messagebox.OK, Messagebox.ERROR);
            }
         }
      }
   }
   
   /**
    * Méthode appelée après la saisie d'une valeur dans le champ
    * codeBoxPrlvt. Cette valeur sera mise en majuscules.
    */
   @Override
   public void onBlur$codeBoxPrlvt(){
      codeBoxPrlvt.setValue(codeBoxPrlvt.getValue().toUpperCase().trim());

      // si le code a été modifié lors de l'update du prélèvement
      if(prelevement.getPrelevementId() != null && !((Prelevement) getCopy()).getCode().equals(codeBoxPrlvt.getValue())){
         getObjectTabController().setCodeUpdated(true);
         getObjectTabController().setOldCode(((Prelevement) getCopy()).getCode());
      }
      
      final String codePrlv = codeBoxPrlvt.getValue();
      if(codePrlv != null && codePrlv.split(".").length > 0) {
  	     ((FichePatientEditOFSEP) referenceur.getFellow("winRefPatient").getFellow("fwinPatientEditOFSEP").getAttributeOrFellow("fwinPatientEditOFSEP$composer", true))
      	    .getNomBox().setValue(codePrlv.split(".")[0]);
  	     ((ReferenceurPatient) referenceur.getAttributeOrFellow("winRefPatient$composer", true)).getNomNipNdaBox().setValue(codePrlv.split(".")[0]);
      }
   }
   
   /**
    * Calcul automaticuament la date de péremption, 5 ans après la date de prélèvement
    */
   /*private void autoDatePeremption(){
      final Calendar c = Calendar.getInstance();

      if(null != datePrelCalBox.getValue()){
         c.setTime(datePrelCalBox.getValue().getTime());
         c.add(Calendar.YEAR, 5);

         datePeremptionCalBox.setValue(c);
         this.prelevement.setDatePeremption(datePeremptionCalBox.getValue());
      }
   }*/

   /**
    * à la sortie du champs Date de Prélèvement on calcul la date de péremption 5 ans après
    */
   /*@Override
   public void onBlur$datePrelCalBox(){
      super.onBlur$datePrelCalBox();
      this.autoDatePeremption();
   }*/

   /**
    * Lors du click sur la date de prélèvement, on défini la date de péremption 5 ans après
    */
   /*public void onClick$datePrelCalBox(){
      this.autoDatePeremption();
   }*/

   /**
    * Lorsque l'utilisateur sors du champs Date de Peremption
    */
   /*public void onBlur$datePeremptionCalBox(){
      if(null != datePeremptionCalBox && null != datePeremptionCalBox.getValue()){
         this.prelevement.setDatePeremption(datePeremptionCalBox.getValue());
      }
   }*/

}
