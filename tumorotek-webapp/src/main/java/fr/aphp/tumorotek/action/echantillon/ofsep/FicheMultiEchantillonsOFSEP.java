package fr.aphp.tumorotek.action.echantillon.ofsep;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;

import fr.aphp.tumorotek.action.ManagerLocator;
import fr.aphp.tumorotek.action.code.CodeAssigneDecorator;
import fr.aphp.tumorotek.action.echantillon.FicheMultiEchantillons;
import fr.aphp.tumorotek.dto.EchantillonDTO;
import fr.aphp.tumorotek.manager.helper.FileBatch;
import fr.aphp.tumorotek.manager.validation.exception.ValidationException;
import fr.aphp.tumorotek.model.coeur.ObjetStatut;
import fr.aphp.tumorotek.model.coeur.annotation.AnnotationValeur;
import fr.aphp.tumorotek.model.coeur.echantillon.Echantillon;

public class FicheMultiEchantillonsOFSEP extends FicheMultiEchantillons
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
   
   /**
    * Méthode exécutée lors du clic sur le bouton addEchantillons.
    * Les nouveaux échantillons seront créés (mais pas encore
    * sauvegardés) et ajoutés à la liste.
    * @param event : clic sur le lien addEchantillons.
    */
   @Override
   public void onClick$addEchantillons(final Event event){
      try{
         onBlur$dateStockCalBox();
         
         if(getSelectedType() == null){
            throw new WrongValueException(typesBoxEchan, Labels.getLabel("ficheEchantillon.error.type"));
         }
         
         if(getSelectedQuantiteUnite() == null){
            throw new WrongValueException(quaniteUnitesBoxEchan, Labels.getLabel("ficheEchantillon.error.qantiteUnite"));
         }
         
         if(getPrepas() == null){
            throw new WrongValueException(prepasBox, Labels.getLabel("ficheEchantillon.error.prepas"));
         }
         
         validateSterilite(sterileBox.isChecked());

         // on remplit l'échantillon en fonction des champs nulls
         setEmptyToNulls();
         getEchantillon().setTumoral(tumoraleBox.isChecked());
         getEchantillon().setSterile(sterileBox.isChecked());

         // gestion de la non conformitée après traitement
         if(conformeTraitementBoxOui.isChecked()){
            getEchantillon().setConformeTraitement(true);
         }else if(conformeTraitementBoxNon.isChecked()){
            getEchantillon().setConformeTraitement(false);
         }else{
        	getEchantillon().setConformeTraitement(null);
        	throw new WrongValueException(conformeTraitementBoxNon, Labels.getLabel("ficheEchantillon.error.confTtmt"));
         }
         if(getEchantillon().getConformeTraitement() == null){
            setSelectedNonConformiteTraitement(null);
         }else if(getEchantillon().getConformeTraitement()){
            setSelectedNonConformiteTraitement(null);
         }

         // gestion de la non conformitée à la cession
         if(conformeCessionBoxOui.isChecked()){
            getEchantillon().setConformeCession(true);
         }else if(conformeCessionBoxNon.isChecked()){
            getEchantillon().setConformeCession(false);
         }else{
        	getEchantillon().setConformeCession(null);
         }
         if(getEchantillon().getConformeCession() == null){
            setSelectedNonConformiteCession(null);
         }else if(getEchantillon().getConformeCession()){
            setSelectedNonConformiteCession(null);
         }

         // Gestion du collaborateur
         final String selectedNomAndPremon = this.collabBox.getValue().toUpperCase();
         this.collabBox.setValue(selectedNomAndPremon);
         final int ind = getNomsAndPrenoms().indexOf(selectedNomAndPremon);
         if(ind > -1){
            setSelectedCollaborateur(getCollaborateurs().get(ind));
         }else{
            setSelectedCollaborateur(null);
         }

         // la quantité et le volume restants sont égaux aux valeurs
         // intiales
         getEchantillon().setQuantite(getEchantillon().getQuantiteInit());

         getEchantillon().setObjetStatut(findStatutForTKStockableObject(getEchantillon()));
         //TODO statut = stockage distant  
         final ObjetStatut statut = ManagerLocator.getObjetStatutManager().findByStatutLikeManager("NON STOCKE", true).get(0);
         
         prepareCrAnapath();

         prepareCodes();

         String lateralite = null;
         // lateralite
         if(getSelectedLateralite() != null && !getSelectedLateralite().equals("")){
            lateralite = getSelectedLateralite();
         }

         // prepare les listes de valeurs à manipuler
         getObjectTabController().getFicheAnnotation().populateValeursActionLists(true, false);
         /**
          * ANNOTATION INLINE - Bêta
          *
          * @since 2.2.0
          */
         //getObjectTabController().getFicheAnnotationInline().populateValeursActionLists(true, false);
         // clone liste valeurs annotations
         final List<AnnotationValeur> clonesValeurs = new ArrayList<>();

         for(int i = 0; i < getObjectTabController().getFicheAnnotation().getValeursToCreateOrUpdate().size(); i++){

            final AnnotationValeur val = getObjectTabController().getFicheAnnotation().getValeursToCreateOrUpdate().get(i);
            // retire les annotations fichier car créés en batches ultérieurement
            if(val.getChampAnnotation().getDataType().getDataTypeId() == 8){
               if(val.getFichier() != null && val.getStream() != null){
                  final FileBatch batch = new FileBatch();
                  batch.setChamp(val.getChampAnnotation());
                  batch.setFile(val.getFichier());
                  batch.setStream(val.getStream());
                  batches.add(batch);
               }
            }else{
               clonesValeurs.add(getObjectTabController().getFicheAnnotation().getValeursToCreateOrUpdate().get(i).clone());
            }
         }

         // gestion des bornes pour la création
         int first = 0;
         int last = 0;
         if(numNombres.isChecked()){
            first = premierCode;
            last = dernierCode;
         }else{
            first = lettres.indexOf(premiereLettre.toUpperCase());
            last = lettres.indexOf(derniereLettre.toUpperCase());
         }

         // Création de tous les nouveaux échantillons
         for(int i = first; i <= last; i++){

            // création du code échantillon en fct de celui du prlvt et
            // du numéro saisi
            final StringBuffer sb = new StringBuffer();

            // 2.0.10.6 VIROBIOTEC création codes sans prefixe
            if(getCodePrefixe() != null && !getCodePrefixe().trim().equals("")){
               sb.append(getCodePrefixe());
               //sb.append(".");
               sb.append(separatorBox.getValue() != null ? separatorBox.getValue() : "");
            }
            if(numNombres.isChecked()){
               sb.append(i);
            }else{
               sb.append(lettres.get(i));
            }

            usedCodesEchantillons.add(sb.toString());

            final Echantillon newEchantillon = new Echantillon();
            newEchantillon.setBanque(getBanque());
            newEchantillon.setPrelevement(getParentObject());
            newEchantillon.setCollaborateur(getSelectedCollaborateur());
            newEchantillon.setCode(sb.toString());
            newEchantillon.setDateStock(getEchantillon().getDateStock());
            newEchantillon.setEchantillonType(getSelectedType());
            newEchantillon.setQuantite(getEchantillon().getQuantite());
            newEchantillon.setQuantiteInit(getEchantillon().getQuantiteInit());
            newEchantillon.setQuantiteUnite(getSelectedQuantiteUnite());
            newEchantillon.setDelaiCgl(getEchantillon().getDelaiCgl());
            newEchantillon.setEchanQualite(getSelectedQualite());
            newEchantillon.setTumoral(getEchantillon().getTumoral());
            newEchantillon.setModePrepa(getSelectedPrepa());
            newEchantillon.setCrAnapath(getCrAnapath() != null ? getCrAnapath().cloneNoId() : null);
            newEchantillon.setAnapathStream(i == first ? getAnapathStream() : null);
            newEchantillon.setSterile(getEchantillon().getSterile());
            newEchantillon.setObjetStatut(statut);
            newEchantillon.setLateralite(lateralite);
            newEchantillon.setConformeTraitement(getEchantillon().getConformeTraitement());
            newEchantillon.setConformeCession(getEchantillon().getConformeCession());

            echantillons.add(newEchantillon);
            addedEchantillons.add(newEchantillon);
            final EchantillonDTO deco = new EchantillonDTO(newEchantillon);

            deco.setCodesOrgsToCreateOrEdit(
               CodeAssigneDecorator.undecorateListe(getCodesOrganeController().getObjToCreateOrEdit()));
            //deco.setCodeOrganeToExport(getCodeOrganeToExport());
            deco.setCodesLesToCreateOrEdit(
               CodeAssigneDecorator.undecorateListe(getCodesMorphoController().getObjToCreateOrEdit()));
            //deco.setCodeLesToExport(getCodeLesToExport());

            deco.setValeursToCreateOrUpdate(clonesValeurs);

            deco.setNonConformiteTraitements(findSelectedNonConformitesTraitement());
            deco.setNonConformiteCessions(findSelectedNonConformitesCession());

            //				deco.setAnapatStream(getAnapathStream());
            echantillonsDecorated.add(deco);

            // file batches
            for(final FileBatch batch : batches){
               if(!batch.isCompleted()){
                  batch.getObjs().add(newEchantillon);
               }
            }
         }

         for(final FileBatch batch : batches){
            batch.setCompleted(true);
         }

         final ListModel<EchantillonDTO> list = new ListModelList<>(echantillonsDecorated);
         echantillonsList.setModel(list);

         getEchanDecoRenderer().setUsedCodes(usedCodesEchantillons);

         /*String id = stockageEchantillons.getUuid();
         String idTop = panelChildrenWithScroll.getUuid();
         Clients.evalJavaScript("document.getElementById('" + idTop + "')"
         		+ ".scrollTop = document.getElementById('" + id + "')"
         		+ ".offsetTop;");	*/
         Clients.scrollIntoView(stockageEchantillons);

         clearForm(true);

         // active le lien vers le stockage
         if(!echantillons.isEmpty()){
            //stockageEchantillons.setSclass("formLink");
            stockageEchantillons.setDisabled(false);
         }
         
         delaiCglBox.setStyle("border:none;");
      }catch(final ValidationException ve){
         Messagebox.show(handleExceptionMessage(ve), "Error", Messagebox.OK, Messagebox.ERROR);
      }

   }

}
