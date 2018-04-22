/**
 * Copyright ou © ou Copr. Ministère de la santé, FRANCE (01/01/2011)
 * dsi-projet.tk@aphp.fr
 * <p>
 * Ce logiciel est un programme informatique servant à la gestion de
 * l'activité de biobanques.
 * <p>
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
 * <p>
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
 * <p>
 * Le fait que vous puissiez accéder à cet en-tête signifie que vous
 * avez pris connaissance de la licence CeCILL, et que vous en avez
 * accepté les termes.
 **/
package fr.aphp.tumorotek.action.prelevement;

import static fr.aphp.tumorotek.model.contexte.EContexte.BTO;
import static fr.aphp.tumorotek.model.contexte.EContexte.OFSEP;
import static fr.aphp.tumorotek.webapp.general.SessionUtils.getCurrentContexte;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.Button;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import fr.aphp.tumorotek.action.MainWindow;
import fr.aphp.tumorotek.action.ManagerLocator;
import fr.aphp.tumorotek.action.annotation.FicheAnnotation;
import fr.aphp.tumorotek.action.annotation.FicheAnnotationInline;
import fr.aphp.tumorotek.action.controller.AbstractFicheCombineController;
import fr.aphp.tumorotek.action.controller.AbstractFicheModifMultiController;
import fr.aphp.tumorotek.action.controller.AbstractObjectTabController;
import fr.aphp.tumorotek.action.echantillon.EchantillonController;
import fr.aphp.tumorotek.action.echantillon.FicheMultiEchantillons;
import fr.aphp.tumorotek.action.patient.PatientController;
import fr.aphp.tumorotek.action.prelevement.bto.FichePrelevementStaticBTO;
import fr.aphp.tumorotek.action.prelevement.bto.ListePrelevementBTO;
import fr.aphp.tumorotek.action.prelevement.ofsep.FichePrelevementStaticOFSEP;
import fr.aphp.tumorotek.action.prelevement.ofsep.ListePrelevementOFSEP;
import fr.aphp.tumorotek.action.prelevement.serotk.PrelevementSeroRowRenderer;
import fr.aphp.tumorotek.action.prodderive.ProdDeriveController;
import fr.aphp.tumorotek.decorator.ObjectTypesFormatters;
import fr.aphp.tumorotek.model.TKAnnotableObject;
import fr.aphp.tumorotek.model.TKdataObject;
import fr.aphp.tumorotek.model.coeur.echantillon.Echantillon;
import fr.aphp.tumorotek.model.coeur.patient.Maladie;
import fr.aphp.tumorotek.model.coeur.prelevement.LaboInter;
import fr.aphp.tumorotek.model.coeur.prelevement.Prelevement;
import fr.aphp.tumorotek.model.contexte.Banque;
import fr.aphp.tumorotek.model.contexte.Collaborateur;
import fr.aphp.tumorotek.model.contexte.Coordonnee;
import fr.aphp.tumorotek.model.interfacage.PatientSip;
import fr.aphp.tumorotek.model.interfacage.Recepteur;
import fr.aphp.tumorotek.model.qualite.ObjetNonConforme;
import fr.aphp.tumorotek.model.systeme.Entite;
import fr.aphp.tumorotek.model.utilisateur.Utilisateur;
import fr.aphp.tumorotek.webapp.general.SessionUtils;

/**
 * Controller de l'onglet prelevement.
 * Controller créé le 23/11/2009.
 *
 * @author Pierre Ventadour
 * @version 2.2.0
 * @since 2.0.6
 */
public class PrelevementController extends AbstractObjectTabController
{

   private final Log log = LogFactory.getLog(PrelevementController.class);

   private static final long serialVersionUID = -5315034328095152785L;
   //private Prelevement prelevement;
   private Maladie maladie;

   private Div divPrelevementStatic;
   private Div divPrelevementEdit;
   private Div divLaboInter;
   //private Div divMultiEchantillons;
   private Div modifMultiDiv;
   private Component listePrelevement;
   private Component listePrelevementBTO;
   private Component listePrelevementOFSEP;
   
   // flag ordonnant le retour vers la fiche patient
   // et rafraichissement et ouverture panel maladie
   private boolean fromFichePatient = false;
   private boolean fromFicheMaladie = false;

   // flag indiquant que le panel echantillon a été atteint
   private boolean nextToEchanClicked = false;
   // flag indiquant que le code du prlvt a été modifié
   private boolean codeUpdated = false;
   // ancien code
   private String oldCode = null;

   // dossier externe provenant d'un SGL
   // private DossierExterne dossierExterne;

   private PatientSip patientSip;

   public Maladie getMaladie(){
      return maladie;
   }

   public void setMaladie(final Maladie m){
      this.maladie = m;
   }

   public boolean getFromFichePatient(){
      return fromFichePatient;
   }

   public void setFromFichePatient(final boolean fP){
      this.fromFichePatient = fP;
   }

   public boolean getFromFicheMaladie(){
      return fromFicheMaladie;
   }

   public void setFromFicheMaladie(final boolean fM){
      this.fromFicheMaladie = fM;
   }

   public boolean getNextToEchanClicked(){
      return nextToEchanClicked;
   }

   public void setNextToEchanClicked(final boolean n){
      this.nextToEchanClicked = n;
   }

   @Override
   public void doAfterCompose(final Component comp) throws Exception{
      setEntiteTab(ManagerLocator.getEntiteManager().findByNomManager("Prelevement").get(0));

      super.doAfterCompose(comp);

      if(BTO.equals(getCurrentContexte())){
         listePrelevementBTO.setVisible(true);
      }else if(OFSEP.equals(getCurrentContexte())){
         listePrelevementOFSEP.setVisible(true);
      }else {
         listePrelevement.setVisible(true);
      }

      setStaticDiv(divPrelevementStatic);
      setEditDiv(divPrelevementEdit);
      setModifMultiDiv(modifMultiDiv);
      if(BTO.equals(getCurrentContexte())){
         setEditZulPath("/zuls/prelevement/bto/FichePrelevementEditBTO.zul");
         setListZulPath("/zuls/prelevement/bto/ListePrelevementBTO.zul");
      }else if(OFSEP.equals(getCurrentContexte())){
          setEditZulPath("/zuls/prelevement/ofsep/FichePrelevementEditOFSEP.zul");
          setListZulPath("/zuls/prelevement/ofsep/ListePrelevementOFSEP.zul");
       }else{
         setEditZulPath("/zuls/prelevement/FichePrelevementEdit.zul");
         setListZulPath("/zuls/prelevement/ListePrelevement.zul");
      }
      setMultiEditZulPath("/zuls/prelevement/FicheModifMultiPrelevement.zul");
      //setListZulPath("/zuls/prelevement/ListePrelevement.zul");

      // drawListe();

      initFicheStatic();

      switchToOnlyListeMode();
      orderAnnotationDraw(false);
   }

   @Override
   public TKdataObject loadById(final Integer id){
      return ManagerLocator.getPrelevementManager().findByIdManager(id);
   }

   @Override
   public void drawListe(){
      if(SessionUtils.isSeroContexte(sessionScope)){
         setListZulPath("/zuls/prelevement/serotk/ListePrelevementSero.zul");
         setListRenderer(new PrelevementSeroRowRenderer(true, SessionUtils.getSelectedBanques(sessionScope).size() > 1));
      }
      super.drawListe();
   }

   @Override
   public void populateFicheStatic(){
      if(BTO.equals(getCurrentContexte())){
         setStaticZulPath("/zuls/prelevement/bto/FichePrelevementStaticBTO.zul");
      }else if(OFSEP.equals(getCurrentContexte())){
          setStaticZulPath("/zuls/prelevement/ofsep/FichePrelevementStaticOFSEP.zul");
       }else if(SessionUtils.isSeroContexte(sessionScope)){
         setStaticZulPath("/zuls/prelevement/serotk/FichePrelevementStaticSero.zul");
      }else{
         setStaticZulPath("/zuls/prelevement/FichePrelevementStatic.zul");
      }
      super.populateFicheStatic();
   }

   @Override
   public void populateFicheEdit(){
      if(SessionUtils.isSeroContexte(sessionScope)){
         setEditZulPath("/zuls/prelevement/serotk/FichePrelevementEditSero.zul");
      }
      super.populateFicheEdit();
   }

   @Override
   public FichePrelevementStatic getFicheStatic(){
      if(BTO.equals(getCurrentContexte())){
         return ((FichePrelevementStaticBTO) this.self.getFellow("divPrelevementStatic").getFellow("fwinPrelevementStaticBTO")
            .getAttributeOrFellow("fwinPrelevementStaticBTO$composer", true));
      }else if(OFSEP.equals(getCurrentContexte())){
    	 return ((FichePrelevementStaticOFSEP) this.self.getFellow("divPrelevementStatic").getFellow("fwinPrelevementStaticOFSEP")
            .getAttributeOrFellow("fwinPrelevementStaticOFSEP$composer", true));
      }else{
         return ((FichePrelevementStatic) this.self.getFellow("divPrelevementStatic").getFellow("fwinPrelevementStatic")
            .getAttributeOrFellow("fwinPrelevementStatic$composer", true));
      }
   }

   @Override
   public FichePrelevementEdit getFicheEdit(){
      if(BTO.equals(getCurrentContexte())){
         return ((FichePrelevementEdit) this.self.getFellow("divPrelevementEdit").getFellow("fwinPrelevementEditBTO")
            .getAttributeOrFellow("fwinPrelevementEditBTO$composer", true));
      }else if(OFSEP.equals(getCurrentContexte())){
         return ((FichePrelevementEdit) this.self.getFellow("divPrelevementEdit").getFellow("fwinPrelevementEditOFSEP")
            .getAttributeOrFellow("fwinPrelevementEditOFSEP$composer", true));
      }else{
         return ((FichePrelevementEdit) this.self.getFellow("divPrelevementEdit").getFellow("fwinPrelevementEdit")
            .getAttributeOrFellow("fwinPrelevementEdit$composer", true));
      }
   }

   @Override
   public ListePrelevement getListe(){
      if(BTO.equals(getCurrentContexte())){
         return ((ListePrelevementBTO)
         //this.self.getFellow("listePrelevement")
         // self.getFellow("lwinPrelevement")
         self.getFellow("listePrelevementBTO").getFellow("lwinPrelevementBTO").getAttributeOrFellow("lwinPrelevementBTO$composer", true));
      }else if(OFSEP.equals(getCurrentContexte())){
         return ((ListePrelevementOFSEP)
         //this.self.getFellow("listePrelevement")
         // self.getFellow("lwinPrelevement")
         self.getFellow("listePrelevementOFSEP").getFellow("lwinPrelevementOFSEP").getAttributeOrFellow("lwinPrelevementOFSEP$composer", true));
      }else{
         return ((ListePrelevement)
         //this.self.getFellow("listePrelevement")
         // self.getFellow("lwinPrelevement")
         self.getFellow("listePrelevement").getFellow("lwinPrelevement").getAttributeOrFellow("lwinPrelevement$composer", true));

      }
   }

   @Override
   public AbstractFicheModifMultiController getFicheModifMulti(){
      return ((FicheModifMultiPrelevement) self.getFellow("modifMultiDiv").getFellow("fwinModifMultiPrelevement")
         .getAttributeOrFellow("fwinModifMultiPrelevement$composer", true));
   }

   @Override
   public FicheAnnotation getFicheAnnotation(){
      if(self.getFellowIfAny("ficheAnnoPrelevement") != null){
         return ((FicheAnnotation) self.getFellow("ficheAnnoPrelevement").getFellow("fwinAnnotation")
            .getAttributeOrFellow("fwinAnnotation$composer", true));
      }else{
         return null;
      }
   }

   /**
    * ANNOTATION INLINE - Bêta
    *
    * @return
    * @since 2.2.0
    */
   @Override
   public FicheAnnotationInline getFicheAnnotationInline(){
      if(null != self.getFellow("divPrelevementEdit").getFellowIfAny("fwinPrelevementEdit") && null != self
         .getFellow("divPrelevementEdit").getFellow("fwinPrelevementEdit").getFellow("ficheTissuInlineAnnoPrelevement")){
         return ((FicheAnnotationInline) self.getFellow("divPrelevementEdit").getFellow("fwinPrelevementEdit")
            .getFellow("ficheTissuInlineAnnoPrelevement").getFellow("fwinAnnotationInline")
            .getAttributeOrFellow("fwinAnnotationInline$composer", true));
      }else if(null != self.getFellow("divPrelevementEdit").getFellowIfAny("fwinPrelevementEditBTO") && null != self
         .getFellow("divPrelevementEdit").getFellow("fwinPrelevementEditBTO").getFellow("ficheTissuInlineAnnoPrelevement")){
         return ((FicheAnnotationInline) self.getFellow("divPrelevementEdit").getFellow("fwinPrelevementEditBTO")
            .getFellow("ficheTissuInlineAnnoPrelevement").getFellow("fwinAnnotationInline")
            .getAttributeOrFellow("fwinAnnotationInline$composer", true));
      }else if(null != self.getFellow("divPrelevementEdit").getFellowIfAny("fwinPrelevementEditOFSEP") && null != self
    	         .getFellow("divPrelevementEdit").getFellow("fwinPrelevementEditOFSEP").getFellow("ficheTissuInlineAnnoPrelevement")){
          return ((FicheAnnotationInline) self.getFellow("divPrelevementEdit").getFellow("fwinPrelevementEditOFSEP")
             .getFellow("ficheTissuInlineAnnoPrelevement").getFellow("fwinAnnotationInline")
             .getAttributeOrFellow("fwinAnnotationInline$composer", true));
       }else{
         return null;
      }
   }

   @Override
   public void clearEditDiv(){
      super.clearEditDiv();
      Components.removeAllChildren(divLaboInter);
      //Components.removeAllChildren(divMultiEchantillons);
   }

   @Override
   public void switchToFicheAndListeMode(){
      super.switchToFicheAndListeMode();
   }

   @Override
   public void showStatic(final boolean s){
      super.showStatic(s);
      divLaboInter.setVisible(!s);
      //divMultiEchantillons.setVisible(!s);
   }

   @Override
   public void initLinkedControllers(){

      if(getMainWindow().isFullfilledComponent("patientPanel", "winPatient")){

         final PatientController patController = (PatientController) getMainWindow().getMainTabbox().getTabpanels()
            .getFellow("patientPanel").getFellow("winPatient").getAttributeOrFellow("winPatient$composer", true);
         getReferencingObjectControllers().add(patController);

         if(!patController.getReferencedObjectsControllers().contains(this)){
            patController.getReferencedObjectsControllers().add(this);
         }
      }

      if(getMainWindow().isFullfilledComponent("echantillonPanel", "winEchantillon")){

         final EchantillonController echanController = (EchantillonController) getMainWindow().getMainTabbox().getTabpanels()
            .getFellow("echantillonPanel").getFellow("winEchantillon").getAttributeOrFellow("winEchantillon$composer", true);
         getReferencedObjectsControllers().add(echanController);

         if(!echanController.getReferencingObjectControllers().contains(this)){
            echanController.getReferencingObjectControllers().add(this);
         }
      }

      // derives
      if(getMainWindow().isFullfilledComponent("derivePanel", "winProdDerive")){

         final ProdDeriveController deriveController = (ProdDeriveController) getMainWindow().getMainTabbox().getTabpanels()
            .getFellow("derivePanel").getFellow("winProdDerive").getAttributeOrFellow("winProdDerive$composer", true);
         getReferencedObjectsControllers().add(deriveController);

         if(!deriveController.getReferencingObjectControllers().contains(this)){
            deriveController.getReferencingObjectControllers().add(this);
         }
      }
   }

   /**
    * Recupere la liste de controllers des objets qui sont référencés
    * par l'objet avec surcharge de méthode pour créer le controller echantillon si il n'existe
    * pas.
    *
    * @param force create cree le controller Echantillon si il n'existe pas.
    * @return la liste de controllers.
    */
   public List<AbstractObjectTabController> getReferencedObjectsControllers(final boolean force){

      // echantillons
      // cree le contenu du panel au besoin
      if(force && !getMainWindow().isFullfilledComponent("echantillonPanel", "winEchantillon")){

         getMainWindow().createMacroComponent("/zuls/echantillon/Echantillon.zul", "winEchantillon",
            (Tabpanel) getMainWindow().getMainTabbox().getTabpanels().getFellow("echantillonPanel"));

         final EchantillonController echanController = (EchantillonController) getMainWindow().getMainTabbox().getTabpanels()
            .getFellow("echantillonPanel").getFellow("winEchantillon").getAttributeOrFellow("winEchantillon$composer", true);

         getReferencedObjectsControllers().add(0, echanController);

         if(!echanController.getReferencingObjectControllers().contains(this)){
            echanController.getReferencingObjectControllers().add(this);
         }

      }

      return getReferencedObjectsControllers();
   }

   /**
    * Méthode permettant de passer en mode édition des labos.
    *
    * @param prlvt Prlvt.
    */
   public void switchToLaboInterEditMode(final Prelevement prlvt){
      final Prelevement edit = prlvt;
      divPrelevementStatic.setVisible(false);
      divPrelevementEdit.setVisible(false);
      divLaboInter.setVisible(true);
      //divMultiEchantillons.setVisible(false);

      if(divLaboInter.getChildren().size() == 0){
    	 if(OFSEP.equals(getCurrentContexte())){
            Executions.createComponents("/zuls/prelevement/ofsep/FicheLaboInterOFSEP.zul", divLaboInter, null);
    	 }else {
    		Executions.createComponents("/zuls/prelevement/FicheLaboInter.zul", divLaboInter, null);
    	 }
         getFicheLaboInter().setObjectTabController(this);
         getFicheLaboInter().setObject(edit);
         getFicheLaboInter().switchToEditMode();
      }

   }

   /**
    * Méthode permettant de passer en mode création des labos.
    *
    * @param prlvt Prlvt.
    */
   public void switchToLaboInterCreateMode(final Prelevement prlvt, final Maladie parent, final List<LaboInter> labos){

      final Prelevement newPrlvt = prlvt;
      divPrelevementStatic.setVisible(false);
      divPrelevementEdit.setVisible(false);
      divLaboInter.setVisible(true);
      //divMultiEchantillons.setVisible(false);

      if(divLaboInter.getChildren().size() == 0){
    	 if(OFSEP.equals(getCurrentContexte())){
            Executions.createComponents("/zuls/prelevement/ofsep/FicheLaboInterOFSEP.zul", divLaboInter, null);
      	 }else {
      		Executions.createComponents("/zuls/prelevement/FicheLaboInter.zul", divLaboInter, null);
      	 }
         
         getFicheLaboInter().setObjectTabController(this);
         getFicheLaboInter().setObject(newPrlvt);
         getFicheLaboInter().setOldLaboInters(labos);
         getFicheLaboInter().switchToCreateMode();
      }
   }

   /**
    * Méthode permettant de passer en mode édition des échantillons
    * d'un prélèvement.
    *
    * @param prlvt Prlvt.
    */
   public void switchToMultiEchantillonsEditMode(final Prelevement prlvt, final List<LaboInter> labos,
      final List<LaboInter> labosToDelete){
      //divPrelevementStatic.setVisible(false);
      //divPrelevementEdit.setVisible(false);
      divLaboInter.setVisible(true);

      // enregistre le flag lors du premier acces a l'echantillon.
      if(!nextToEchanClicked){
         ((EchantillonController) getReferencedObjectsControllers(true).get(0)).switchToPrelevementEditMode(prlvt);
         getFicheMultiEchantillons().setPrelevementProcedure(true);
         getFicheMultiEchantillons().setLaboInters(labos);
         getFicheMultiEchantillons().setLaboIntersToDelete(labosToDelete);
         nextToEchanClicked = true;
      }

      // change d'onglet
      EchantillonController.backToMe(getMainWindow(), page);
   }

   /**
    * Méthode permettant de passer en mode création des échantillons
    * d'un prélèvement.
    *
    * @param prlvt Prlvt.
    */
   public void switchToMultiEchantillonsCreateMode(final Prelevement prlvt, final Maladie parent, final List<LaboInter> labos){

      //divPrelevementStatic.setVisible(false);
      //divPrelevementEdit.setVisible(false);
      divLaboInter.setVisible(true);
      //divMultiEchantillons.setVisible(true);

      // enregistre le flag lors du premier acces a l'echantillon.
      if(!nextToEchanClicked){
         ((EchantillonController) getReferencedObjectsControllers(true).get(0)).switchToCreateMode(prlvt);
         getFicheMultiEchantillons().setPrelevementProcedure(true);
         nextToEchanClicked = true;
      }else{
         if(((EchantillonController) getReferencedObjectsControllers(true).get(0)).hasMultiFicheEdit()){
            getFicheMultiEchantillons().setParentObject(prlvt);
         }else{
            ((EchantillonController) getReferencedObjectsControllers(true).get(0)).switchToCreateMode(prlvt);
            getFicheMultiEchantillons().setPrelevementProcedure(true);
            nextToEchanClicked = true;
         }
      }
      getFicheMultiEchantillons().setLaboInters(labos);

      // change d'onglet
      EchantillonController.backToMe(getMainWindow(), page);
   }

   /**
    * Méthode appelée lorsque l'utilisateur clique sur le bouton
    * "précédent" quand il est sur la page FicheLaboInter.
    */
   public void switchFromLaboToPrelevement(){
      divPrelevementStatic.setVisible(false);
      divPrelevementEdit.setVisible(true);
      divLaboInter.setVisible(false);
      //divMultiEchantillons.setVisible(false);
   }

   /**
    * Méthode appelée lorsque l'utilisateur clique sur le bouton
    * "précédent" quand il est sur la page FicheMultiEchantillons.
    */
   public void switchFromEchantillonsToLabo(){
      divPrelevementStatic.setVisible(false);
      divPrelevementEdit.setVisible(false);
      divLaboInter.setVisible(true);
      //divMultiEchantillons.setVisible(false);
      // change d'onglet
      PrelevementController.backToMe(getMainWindow(), page);
   }

   /**
    * Recupere le controller du composant representant la fiche associee
    * a l'entite de domaine a partir de l'evenement.
    *
    * @return fiche FicheLaboInter
    */
   public FicheLaboInter getFicheLaboInter(){
	  if(OFSEP.equals(getCurrentContexte())){
		 return ((FicheLaboInter) this.self.getFellow("divLaboInter").getFellow("fwinLaboInterOFSEP")
            .getAttributeOrFellow("fwinLaboInterOFSEP$composer", true));
	  }else {
		 return ((FicheLaboInter) this.self.getFellow("divLaboInter").getFellow("fwinLaboInter")
		            .getAttributeOrFellow("fwinLaboInter$composer", true));  
	  }
   }

   public boolean hasFicheLaboInter(){
	  if(OFSEP.equals(getCurrentContexte())){
	     if(this.self.getFellow("divLaboInter").getFellowIfAny("fwinLaboInterOFSEP") != null){
		    return true;
	     }else{
			return false;
	     } 
	  }else {
         if(this.self.getFellow("divLaboInter").getFellowIfAny("fwinLaboInter") != null){
		    return true;
		 }else{
		    return false;
		 }  
	  }
	  
   }

   public boolean isFicheLaboOpened(){
      return this.self.getFellow("divLaboInter").getFirstChild() != null;
   }

   /**
    * Recupere le controller du composant representant la fiche associee
    * a l'entite de domaine a partir de l'evenement.
    *
    * @return fiche FicheMultiEchantillons
    */
   public FicheMultiEchantillons getFicheMultiEchantillons(){
      return ((EchantillonController) getReferencedObjectsControllers(true).get(0)).getMultiFicheEdit();
   }

   /**
    * Selectionne le tab et renvoie le controller Prelevement.
    *
    * @param page
    * @return controller tab
    */
   public static AbstractObjectTabController backToMe(final MainWindow window, final Page page){

      PrelevementController tabController = null;

      // on récupère les panels
      final Tabbox panels = (Tabbox) page.getFellow("mainWin").getFellow("main").getFellow("mainTabbox");

      if(panels != null){
         // on récupère le panel de l'entite
         final Tabpanel panel = (Tabpanel) panels.getFellow("prelevementPanel");

         window.createMacroComponent("/zuls/prelevement/Prelevement.zul", "winPrelevement", panel);

         tabController =
            ((PrelevementController) panel.getFellow("winPrelevement").getAttributeOrFellow("winPrelevement$composer", true));

         panels.setSelectedPanel(panel);
      }

      return tabController;
   }

   @Override
   public void onCreateDone(){
      // create done hors fiche echantillon atteinte pourtant
      if(nextToEchanClicked){
         ((EchantillonController) getReferencedObjectsControllers(true).get(0)).onCancel();
         nextToEchanClicked = false;
      }
      super.onCreateDone();
   }

   @Override
   public void onEditDone(final TKdataObject obj){
      // update done hors fiche echantillon atteinte pourtant
      if(nextToEchanClicked){
         ((EchantillonController) getReferencedObjectsControllers(true).get(0)).onCancel();
         nextToEchanClicked = false;
      }
      super.onEditDone(obj);
   }

   @Override
   public AbstractFicheCombineController getFicheCombine(){
      return null;
   }

   /**
    * Méthode permettant de passer en mode "création", collapse la liste
    * et dessine la fiche édition/creation avec un objet vide.
    */;
   @Override
   public void switchToCreateMode(final TKdataObject parent){
      super.switchToCreateMode(parent);

      if(nextToEchanClicked){
         (getReferencedObjectsControllers(true).get(0)).onCancel();
         nextToEchanClicked = false;
      }

      // on efface le dossier externe
      // setDossierExterne(null);
      SessionUtils.setDossierExterneInjection(sessionScope, null);
   }

   @Override
   public void onCancel(){
      if(nextToEchanClicked){
         (getReferencedObjectsControllers(true).get(0)).onCancel();
         nextToEchanClicked = false;
      }
      super.onCancel();
   }

   @Override
   public void onRevert(){
      // cancel done hors fiche echantillon atteinte pourtant
      if(nextToEchanClicked){
         ((EchantillonController) getReferencedObjectsControllers(true).get(0)).onCancel();
         nextToEchanClicked = false;
      }
      super.onRevert();
   }

   public void createAnotherPrelevement(final Prelevement prlvt){
	  Map<String, String> params = new HashMap<String, String>();
	  params.put("width", "600px");
	  Button response = Messagebox.show(
		  Labels.getLabel("fichePrelevement.create.another.prelevement"),
		  Labels.getLabel("message.new.prelevement.title"), 
		  new Button[]{Messagebox.Button.YES, Messagebox.Button.CANCEL,Messagebox.Button.NO,Messagebox.Button.RETRY}, 
		  new String[]{
			  Labels.getLabel("fichePrelevement.afterCreatePrelevement.messageBox.yes"),
			  Labels.getLabel("fichePrelevement.afterCreatePrelevement.messageBox.no"),
			  Labels.getLabel("fichePrelevement.afterCreatePrelevement.messageBox.noSendMailAll"),
//			  Labels.getLabel("fichePrelevement.afterCreatePrelevement.messageBox.noSendMailMine"),
		  },
		  Messagebox.QUESTION,
		  null,
		  null,
		  params);
	  
      if(response == Messagebox.Button.YES){
         backToMe(getMainWindow(), page);

         // si il y eu edit avant et addNew depuis liste
         clearEditDiv();
         if(BTO.equals(getCurrentContexte())){
        	Executions.createComponents("/zuls/prelevement/bto/FichePrelevementEditBTO.zul", divPrelevementEdit, null);
         }else if(OFSEP.equals(getCurrentContexte())){
         	Executions.createComponents("/zuls/prelevement/ofsep/FichePrelevementEditOFSEP.zul", divPrelevementEdit, null);
          }else{
        	Executions.createComponents("/zuls/prelevement/FichePrelevementEdit.zul", divPrelevementEdit, null);
         }
         getFicheEdit().setObjectTabController(this);
         getFicheEdit().setNewObjectByCopy(prlvt);
         getFicheEdit().setParentObject(prlvt.getMaladie());
         getFicheEdit().switchToCreateMode();
         getFicheEdit().initSelectedInLists();

         if(canUpdateAnnotation()){
            getFicheAnnotation().switchToStaticOrEditMode(false, false);
         }

         if(!annoRegion.isOpen() && annoRegion.isVisible()){
            annoRegion.setOpen(true);
         }

         getListeRegion().setOpen(false);

         showStatic(false);
      } else if(response == Messagebox.Button.NO){
    	  // Emission de mails automatiques vers ARC du centre préleveur de tous les prélèvement saisis ces dernières 48h
    	  Banque currentBanque = SessionUtils.getCurrentBanque(sessionScope);
    	  final List<Prelevement> prelevements = new ArrayList<>(ManagerLocator.getPrelevementManager().findAllCreatedDuringLast48hByBank(currentBanque));
    	  generateAndOpenMails(prelevements);
//      } else if(response == Messagebox.Button.RETRY){
//    	  // Emission de mails automatiques vers ARC du centre préleveur de tous les prélèvement saisis ces dernières 48h saisis par l'utilisateur courant
//    	  Banque currentBanque = SessionUtils.getCurrentBanque(sessionScope);
//    	  Utilisateur currentUtilisateur = SessionUtils.getLoggedUser(sessionScope);
//    	  final List<Prelevement> prelevements = new ArrayList<>(ManagerLocator.getPrelevementManager().findAllCreatedDuringLast48hByBankAndUser(currentBanque, currentUtilisateur));
//    	  generateAndOpenMails(prelevements);
      }
   }
   
   /**
    * Générer les mails via mailto
    * @param prelevements
    */
   public void generateAndOpenMails(final List<Prelevement> prelevements) {
	  Map<Integer,Map<Integer,Prelevement>> groups = getPrelevementsGroupByPatientAndNature(prelevements);
 	  for (Integer keyPatient : groups.keySet()) {
		  generateAndOpenMail(groups.get(keyPatient));
 	  }
   }
   
   /**
    * Générer le mail via mailto
    * @param prelevements
    */
   public void generateAndOpenMail(final Map<Integer, Prelevement> prelevements) {
	   String mainContact = getMailMainContact();
	   String copyContacts = getMailCopyContacts();
	   String subject = getMailSubject();
	   String body = getMailBody(prelevements);
	   openMailto(mainContact, copyContacts, subject, body);
   }
   
   /**
	 * Générer le contenu du mail pour confirmer la recéption des prélèvements
	 * @param prelevements, la listes de prélèvements à confirmer
	 * @return
	 */
   private String getMailMainContact() {
	   Banque currentBanque = SessionUtils.getCurrentBanque(sessionScope);
	   return currentBanque.getContactArc();
   }
   
   /**
	 * Générer le contenu du mail pour confirmer la recéption des prélèvements
	 * @param prelevements, la listes de prélèvements à confirmer
	 * @return
	 */
  private String getMailCopyContacts() {
	  String mails = "";
	  Banque currentBanque = SessionUtils.getCurrentBanque(sessionScope);
	  
	  Collaborateur collaborateur = currentBanque.getCollaborateur();
	  if(collaborateur!=null) {
		  Set<Coordonnee> coordonnees = currentBanque.getCollaborateur().getCoordonnees();
		  if(coordonnees != null) {
			  int cpt = 0;
			  for(Coordonnee coordonnee : coordonnees) {
				  if(cpt>0) {
					  mails += ";";
				  }
				  mails += coordonnee.getMail();
				  cpt++;
			  }
		  }
	  }
	  return mails;
  }
  
   /**
	 * Générer le contenu du mail pour confirmer la recéption des prélèvements
	 * @param prelevements, la listes de prélèvements à confirmer
	 * @return
	 */
   private String getMailSubject() {
	   return Labels.getLabel("fichePrelevement.afterCreatePrelevement.mail.object");
   }
		  
	/**
	 * Générer le contenu du mail pour confirmer la recéption des prélèvements pour chaque patient
	 * @param prelevements, la listes de prélèvements à confirmer
	 * @return
	 */
   private String getMailBody(final Map<Integer,Prelevement> prelevements) {
	  String dateFomat = Labels.getLabel("fichePrelevement.afterCreatePrelevement.mail.dateFormat");
	  SimpleDateFormat formatter = new SimpleDateFormat(dateFomat);
	  
	  final String SYNTAX_WHITE = " ";
	  final String SIMPLE_QUOTE = "'";
	  final String SYNTAX_SIMPLE_QUOTE = " ";
	  final String SYNTAX_END_LINE = "\\n";
	  final String SYNTAX_END_TAB = "-    ";
	  
	  String body = Labels.getLabel("fichePrelevement.afterCreatePrelevement.mail.msgHello")+SYNTAX_WHITE + SYNTAX_END_LINE;
	  body += SYNTAX_END_LINE;
	  
	  body += Labels.getLabel("fichePrelevement.afterCreatePrelevement.mail.msgConfirm")+SYNTAX_WHITE + SYNTAX_END_LINE;
	  body += SYNTAX_END_LINE;
	  
	  Integer keyFirst = prelevements.keySet().iterator().next();
	  Prelevement first = prelevements.get(keyFirst);
	  
	  // ID EDMUS
	  body += Labels.getLabel("fichePrelevement.afterCreatePrelevement.mail.msgIdEdmus")+SYNTAX_WHITE + first.getMaladie().getPatient().getNom()+ SYNTAX_END_LINE;
	  
	  // Cohorte
	  body += Labels.getLabel("fichePrelevement.afterCreatePrelevement.mail.msgIdCohorte")+SYNTAX_WHITE + SYNTAX_END_LINE;
	  body += SYNTAX_END_LINE;
	  
	  // Liste des prelevements
	  body += Labels.getLabel("fichePrelevement.afterCreatePrelevement.mail.msgList")+SYNTAX_WHITE + SYNTAX_END_LINE;
	  body += SYNTAX_END_LINE;
	  
	  for (Integer keyNature : prelevements.keySet()) {
		  Prelevement prelevement = prelevements.get(keyNature);
		 
		  // Prélèvement TYPE
		  body += SYNTAX_END_TAB + Labels.getLabel("fichePrelevement.afterCreatePrelevement.mail.msgPrlvmt")+SYNTAX_WHITE + prelevement.getNature().getNom() + SYNTAX_END_LINE;
		
		  // Date de prélèvement  
		  body += SYNTAX_END_TAB + Labels.getLabel("fichePrelevement.afterCreatePrelevement.mail.msgPrlvmtDateTimeAct")+SYNTAX_WHITE;
		  if(prelevement.getWrongDatePeremption()) {
			  body += Labels.getLabel("fichePrelevement.afterCreatePrelevement.mail.dateMissmatch");
		  } else if(prelevement.getDatePrelevement()!=null && prelevement.getDatePrelevement().getTime()!=null) {
			  body += formatter.format(prelevement.getDatePrelevement().getTime());
		  } 
		  body += SYNTAX_END_LINE;
		  
		  // Date de reception
		  body += SYNTAX_END_TAB + Labels.getLabel("fichePrelevement.afterCreatePrelevement.mail.msgPrlvmtDateTimeRcpt")+SYNTAX_WHITE;
		  if(prelevement.getWrongDateArrive()) {
			  body += Labels.getLabel("fichePrelevement.afterCreatePrelevement.mail.dateMissmatch");
		  } else if(prelevement.getDateArrivee()!=null && prelevement.getDateArrivee().getTime()!=null) {
			  body += formatter.format(prelevement.getDateArrivee().getTime());
		  }
		  body += SYNTAX_END_LINE;
		  
		  // Conformité
		  body += SYNTAX_END_TAB + Labels.getLabel("fichePrelevement.afterCreatePrelevement.mail.msgPrlvmtCorform")+SYNTAX_WHITE;
		  body += getConformeArriveeFormated(prelevement);
		  body += SYNTAX_END_LINE;
		  
		  // Fin
		  body += SYNTAX_END_LINE;
	  }
	
	  body += Labels.getLabel("fichePrelevement.afterCreatePrelevement.mail.msgEnd") + SYNTAX_END_LINE;
	  body += Labels.getLabel("fichePrelevement.afterCreatePrelevement.mail.msgBye");	
	  
	  return body.replaceAll(SIMPLE_QUOTE, SYNTAX_SIMPLE_QUOTE);
   }
   
   /**
    * Formate la valeur du champ conformeArrivee.
    * @return Oui ou non.
    */
   public String getConformeArriveeFormated(Prelevement prelevement){
      final StringBuffer sb = new StringBuffer();
      if(prelevement != null){
         if(prelevement.getConformeArrivee() != null){
            sb.append(ObjectTypesFormatters.booleanLitteralFormatter(prelevement.getConformeArrivee()));

            if(prelevement.getConformeArrivee() != null && !prelevement.getConformeArrivee()){
               sb.append(" - ");
               final List<ObjetNonConforme> list =
                  ManagerLocator.getObjetNonConformeManager().findByObjetAndTypeManager(prelevement, "Arrivee");

               if(list.size() > 0){
                  for(int i = 0; i < list.size(); i++){
                     sb.append(list.get(i).getNonConformite().getNom());
                     if(i < list.size() - 1){
                        sb.append(", ");
                     }else{
                        sb.append(".");
                     }
                  }
               }else{
                  sb.append(Labels.getLabel("nonConformite.raison.inconnue"));
               }
            }
         }else{
            sb.append("-");
         }
      }
      return sb.toString();
   }
   /**
    * Groupe les prelevements par patient
    * @param prelevements
    * @return
    */
   private Map<Integer,Map<Integer,Prelevement>> getPrelevementsGroupByPatientAndNature(List<Prelevement> prelevements){
	   Map<Integer,Map<Integer, Prelevement>> groups = new HashMap<>();
	   
	   if(prelevements!=null) {
		   for(Prelevement prelevement: prelevements) {
			   if(prelevement.getMaladie()!=null && prelevement.getMaladie().getPatient()!=null && prelevement.getNature()!=null) {
				   // GroupByPatient
				   Integer patientId = prelevement.getMaladie().getPatient().getPatientId();
				   if(!groups.containsKey(patientId)) {
					   groups.put(patientId, new HashMap<Integer, Prelevement>());
				   }
				   
				   // GroupByNature
				   Integer natureId = prelevement.getNature().getNatureId();
				   if(!groups.get(patientId).containsKey(natureId)) {
					   prelevement.setWrongDatePeremption(false);
					   prelevement.setWrongDateArrive(false);
					   groups.get(patientId).put(natureId, prelevement);
				   }
				   Prelevement storedPrelevement = groups.get(patientId).get(natureId);
				   if(storedPrelevement!=null) {
					   if(!equalsCalendarDate(storedPrelevement.getDatePrelevement(), prelevement.getDatePrelevement())) {
						   storedPrelevement.setWrongDatePeremption(true);
					   }
					   if(!equalsCalendarDate(storedPrelevement.getDateArrivee(), prelevement.getDateArrivee())) {
						   storedPrelevement.setWrongDateArrive(true);
					   }
				   }
			   }
		   }
	   }
	   
	   return groups;
   }
   
   /**
    * Retourne vrai si les dates sont equales
    * @param cal1
    * @param cal2
    * @return
    */
   private boolean equalsCalendarDate(Calendar cal1, Calendar cal2) {
	   boolean res = false;
	   if((cal1 == null && cal2 == null)
		   || (cal1.getTime() == null && cal2.getTime() == null)
		   || (cal1.getTime().equals(cal2.getTime()))) {
		   res = true;
	   }
	   return res;
   }
   
   /**
   * Ouvrir le client client mail de l'utilisateur avec un mail pré-construit
   * @param mailto
   */
   private void openMailto(final String mainContact, final String copyContacts, final String subject, final String body) {
//	   String mailto = "mailto:"+contact+"?subject=encodeURI('"+subject+"')&body=encodeURI('"+body+"')";
	   
	   String javascript = 
    	  "var nodeA = document.createElement('a');"+
    	  
    	  "var attrId = document.createAttribute('id');"+
    	  "attrId.value = 'mailtoBtn';"+
    	  "nodeA.setAttributeNode(attrId);"+
    	  
		  "var mainContact = '"+mainContact+"';"+
		  "var copyContacts = '"+copyContacts+"';"+
		  "var subject = encodeURIComponent('"+subject+"');"+
		  "var body = encodeURIComponent('"+body+"');"+
    	  "var mailto = 'mailto:'+mainContact+'?cc='+copyContacts+'&subject='+subject+'&body='+body;"+

    	  "var attrHref = document.createAttribute('href');"+
    	  "attrHref.value = mailto;"+
    	  "nodeA.setAttributeNode(attrHref);"+
    	  
    	  "var attTarget = document.createAttribute('target');"+ 
    	  "attTarget.value = '_blank';"+
    	  "nodeA.setAttributeNode(attTarget);"+

     	  "var attStyle = document.createAttribute('style');"+
     	  "attStyle.value = 'display:none;';"+
    	  "nodeA.setAttributeNode(attStyle);"+
    	  
    	  "document.body.appendChild(nodeA);"+
    	  
    	  "document.getElementById('mailtoBtn').click();"+
    	  
    	  "document.getElementById('mailtoBtn').remove();";
 	  Clients.evalJavaScript(javascript);
   }
    
   /**
    * Si l'utilisateur a modifié le code d'un prélèvement, une
    * fenêtre sera affichée pour lui proposer d'afficher ses
    * échantillons afin de mettre à jour leurs codes.
    *
    * @param prlvt
    */
   public void showEchantillonsAfterUpdate(final Prelevement prlvt){
      final List<Echantillon> echans =
         new ArrayList<>(ManagerLocator.getPrelevementManager().getEchantillonsManager(prlvt));
      // si le code a été mis à jour et que le prlvt a des
      // échantillons
      if(codeUpdated && echans.size() > 0 && oldCode != null){
         openShowEchantillonsModaleWindow(echans, prlvt, oldCode);
      }
   }

   /**
    * Méthode appelée demander à l'utilisateur s'il souhaite afficher
    * les échantillons d'un prélèvement pour les mettre à jour.
    * pour changer le prelevement de collection.
    */
   public void openShowEchantillonsModaleWindow(final List<Echantillon> echans, final Prelevement prlvt, final String oldPrefixe){
      if(!isBlockModal()){

         setBlockModal(true);

         // nouvelle fenêtre
         final Window win = new Window();
         win.setVisible(false);
         win.setId("showEchantillonsWindow");
         win.setPage(page);
         win.setMaximizable(true);
         win.setSizable(true);
         win.setTitle(Labels.getLabel("general.edit"));
         win.setBorder("normal");
         win.setWidth("400px");
         win.setHeight("325px");
         win.setClosable(true);

         final HtmlMacroComponent ua = populateShowEchantillonsModal(echans, prlvt, oldPrefixe, page, getMainWindow(), win);
         ua.setVisible(false);

         win.addEventListener("onTimed", new EventListener<Event>()
         {
            @Override
            public void onEvent(final Event event) throws Exception{
               //progress.detach();
               ua.setVisible(true);
            }
         });

         final Timer timer = new Timer();
         timer.setDelay(500);
         timer.setRepeats(false);
         timer.addForward("onTimer", timer.getParent(), "onTimed");
         win.appendChild(timer);
         timer.start();

         try{
            win.onModal();
            setBlockModal(false);

         }catch(final SuspendNotAllowedException e){
            log.error(e);
         }
      }
   }

   private static HtmlMacroComponent populateShowEchantillonsModal(final List<Echantillon> echans, final Prelevement prlvt,
      final String oldPrefixe, final Page page, final MainWindow main, final Window win){
      HtmlMacroComponent ua;
      ua = (HtmlMacroComponent) page.getComponentDefinition("showEchantillonsModale", false).newInstance(page, null);
      ua.setParent(win);
      ua.setId("openShowEchantillonsModale");
      ua.applyProperties();
      ua.afterCompose();

      ((ShowEchantillonsModale) ua.getFellow("fwinShowEchantillons").getAttributeOrFellow("fwinShowEchantillons$composer", true))
         .init(echans, prlvt, oldPrefixe, main, page);

      return ua;
   }

   public boolean isCodeUpdated(){
      return codeUpdated;
   }

   public void setCodeUpdated(final boolean cUpdated){
      this.codeUpdated = cUpdated;
   }

   public String getOldCode(){
      return oldCode;
   }

   public void setOldCode(final String c){
      this.oldCode = c;
   }

   /**
    * Gère la communication entre TK et l'environnement applicatif
    * après création du dossier.
    * Suppression d'un dossier temporaire si enregistré en base.
    * Envoi d'un accusé de réception/synthèse du prélèvement.
    *
    * @param prel       Prelevement
    * @param controller (Prelvement/Echantillon) qui reçoit la validation
    * @version 2.1
    */
   public void handleExtCom(final Prelevement prel, final AbstractObjectTabController controller){

      String url = null;
      String dosExtId = null;

      // gère la suppression du dossier externe
      if(SessionUtils.getDossierExterneInjection(sessionScope) != null
         && SessionUtils.getDossierExterneInjection(sessionScope).getDossierExterne() != null
         && SessionUtils.getDossierExterneInjection(sessionScope).getDossierExterne().getDossierExterneId() != null){

         dosExtId = SessionUtils.getDossierExterneInjection(sessionScope).getDossierExterne().getIdentificationDossier();

         // since 2.0.13
         // suppr si pas info echantillon + validation depuis onglet Prelevement
         // ou si info échantillons + validation depuis onglet échantillon

         if(ManagerLocator.getBlocExterneManager()
            .findByDossierExterneAndEntiteManager(SessionUtils.getDossierExterneInjection(sessionScope).getDossierExterne(),
               ManagerLocator.getEntiteManager().findByIdManager(3))
            .isEmpty() || controller.getEntiteTab().getNom().equals("Echantillon")){
            ManagerLocator.getDossierExterneManager()
               .removeObjectManager(SessionUtils.getDossierExterneInjection(sessionScope).getDossierExterne());
         }

         // setDossierExterne(null);
         SessionUtils.setDossierExterneInjection(sessionScope, null);

         final HttpServletRequest req = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
         url = req.getScheme() + "://" + req.getServerName();
         if(req.getServerPort() > -1){
            url = url + ":" + req.getServerPort();
         }
         url = url + req.getContextPath() + "/ext/prelevement?id=" + prel.getPrelevementId();
      }

      try{
         // Recepteurs
         for(final Recepteur recept : SessionUtils.getRecepteursInterfacages(sessionScope)){
            ManagerLocator.getSenderFactory().sendMessage(recept, prel, dosExtId, url);
         }
      }catch(final Exception e){
         throw new RuntimeException(e);
      }
   }

   public PatientSip getPatientSip(){
      return patientSip;
   }

   public void setPatientSip(final PatientSip patientSip){
      this.patientSip = patientSip;
   }

   public void removePatientSip(){
      if(patientSip != null && patientSip.getPatientSipId() != null){
         ManagerLocator.getPatientSipManager().removeObjectManager(patientSip);

         setPatientSip(null);
      }
   }

   @Override
   public List<? extends Object> getChildrenObjects(final TKdataObject obj){
      final List<TKAnnotableObject> childrens = new ArrayList<>();

      final Set<Echantillon> echans = ManagerLocator.getPrelevementManager().getEchantillonsManager((Prelevement) obj);
      childrens.addAll(echans);
      for(final Echantillon echantillon : echans){
         childrens.addAll(ManagerLocator.getProdDeriveManager().findByParentManager(echantillon, true));
      }
      childrens.addAll(ManagerLocator.getProdDeriveManager().findByParentManager((Prelevement) obj, true));
      return childrens;
   }

   @Override
   public Map<Entite, List<Integer>> getChildrenObjectsIds(final List<Integer> ids){
      final Map<Entite, List<Integer>> children = new HashMap<>();

      // echantillo,s
      final Entite echanEntite = ManagerLocator.getEntiteManager().findByNomManager("Echantillon").get(0);
      children.put(echanEntite, ManagerLocator.getCorrespondanceIdManager().findTargetIdsFromIdsManager(ids, getEntiteTab(),
         echanEntite, SessionUtils.getSelectedBanques(sessionScope), false));

      // derives
      final Entite deriveEntite = ManagerLocator.getEntiteManager().findByNomManager("ProdDerive").get(0);
      children.put(deriveEntite, ManagerLocator.getCorrespondanceIdManager().findTargetIdsFromIdsManager(ids, getEntiteTab(),
         deriveEntite, SessionUtils.getSelectedBanques(sessionScope), true));

      return children;
   }

   @Override
   public Map<Entite, List<Integer>> getParentsObjectsIds(final List<Integer> ids){
      final Map<Entite, List<Integer>> parents = new HashMap<>();

      // patient
      final Entite patEntite = ManagerLocator.getEntiteManager().findByNomManager("Patient").get(0);
      parents.put(patEntite, ManagerLocator.getCorrespondanceIdManager().findTargetIdsFromIdsManager(ids, getEntiteTab(),
         patEntite, SessionUtils.getSelectedBanques(sessionScope), true));

      return parents;
   }
}
