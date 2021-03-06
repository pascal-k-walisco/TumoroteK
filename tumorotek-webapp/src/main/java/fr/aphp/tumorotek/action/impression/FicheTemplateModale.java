/** 
 * Copyright ou © ou Copr. Ministère de la santé, FRANCE (01/01/2011)
 * dsi-projet.tk@aphp.fr
 * 
 * Ce logiciel est un programme informatique servant à la gestion de 
 * l'activité de biobanques. 
 *
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
 *
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
 *	
 * Le fait que vous puissiez accéder à cet en-tête signifie que vous 
 * avez pris connaissance de la licence CeCILL, et que vous en avez 
 * accepté les termes. 
 **/
package fr.aphp.tumorotek.action.impression;

import fr.aphp.tumorotek.action.ManagerLocator;
import fr.aphp.tumorotek.action.cession.retour.RetourDecorator;
import fr.aphp.tumorotek.action.constraints.ConstWord;
import fr.aphp.tumorotek.action.controller.AbstractImpressionController;
import fr.aphp.tumorotek.action.echantillon.EchantillonRowRenderer;
import fr.aphp.tumorotek.action.patient.FicheMaladie;
import fr.aphp.tumorotek.action.patient.FichePatientStatic;
import fr.aphp.tumorotek.action.patient.PatientUtils;
import fr.aphp.tumorotek.action.prodderive.ProdDeriveRowRenderer;
import fr.aphp.tumorotek.action.utils.PrelevementUtils;
import fr.aphp.tumorotek.decorator.BlocImpressionDecorator;
import fr.aphp.tumorotek.decorator.BlocImpressionRowRenderer;
import fr.aphp.tumorotek.decorator.CederObjetDecorator;
import fr.aphp.tumorotek.decorator.ObjectTypesFormatters;
import fr.aphp.tumorotek.decorator.factory.CederObjetDecoratorFactory;
import fr.aphp.tumorotek.manager.impl.xml.*;
import fr.aphp.tumorotek.model.TKAnnotableObject;
import fr.aphp.tumorotek.model.TKdataObject;
import fr.aphp.tumorotek.model.cession.Cession;
import fr.aphp.tumorotek.model.cession.Retour;
import fr.aphp.tumorotek.model.code.CodeAssigne;
import fr.aphp.tumorotek.model.coeur.annotation.AnnotationValeur;
import fr.aphp.tumorotek.model.coeur.annotation.ChampAnnotation;
import fr.aphp.tumorotek.model.coeur.annotation.TableAnnotation;
import fr.aphp.tumorotek.model.coeur.echantillon.Echantillon;
import fr.aphp.tumorotek.model.coeur.patient.Maladie;
import fr.aphp.tumorotek.model.coeur.patient.Patient;
import fr.aphp.tumorotek.model.coeur.prelevement.LaboInter;
import fr.aphp.tumorotek.model.coeur.prelevement.Prelevement;
import fr.aphp.tumorotek.model.coeur.prelevement.Risque;
import fr.aphp.tumorotek.model.coeur.prodderive.ProdDerive;
import fr.aphp.tumorotek.model.contexte.Collaborateur;
import fr.aphp.tumorotek.model.contexte.Service;
import fr.aphp.tumorotek.model.impression.BlocImpression;
import fr.aphp.tumorotek.model.impression.BlocImpressionTemplate;
import fr.aphp.tumorotek.model.impression.TableAnnotationTemplate;
import fr.aphp.tumorotek.model.impression.Template;
import fr.aphp.tumorotek.model.io.export.ChampEntite;
import fr.aphp.tumorotek.model.qualite.ObjetNonConforme;
import fr.aphp.tumorotek.model.qualite.Operation;
import fr.aphp.tumorotek.model.stockage.Conteneur;
import fr.aphp.tumorotek.model.stockage.Emplacement;
import fr.aphp.tumorotek.model.systeme.Entite;
import fr.aphp.tumorotek.webapp.general.SessionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

public class FicheTemplateModale extends AbstractImpressionController {
	
	private Log log = LogFactory.getLog(FicheTemplateModale.class);
	
	private static final long serialVersionUID = -8743924081789346031L;
	
	private Group groupContenu;
	private Listbox formatsBox;
	private Button print;
	
	/**
	 *  Static Components pour le mode static.
	 */
	private Label enteteLabel;
	private Label piedPageLabel;
	private Grid contenuStaticGrid;
	private Row descriptionRow;
	
	/**
	 *  Editable components : mode d'édition ou de création.
	 */
	private Textbox piedPageBox;
	private Textbox enteteBox;
	private Row rowHistorique;
	private Checkbox historiqueBox;
	
	/**
	 *  Objets Principaux.
	 */
	private Template template;
	private Document document;
	private Element pageXML;
	private FichePatientStatic fichePatient;
	
	/**
	 *  Associations.
	 */
	private Entite selectedEntite;
	private Object objectToPrint;
	private List<Template> templates = new ArrayList<Template>();
	private Template selectedTemplate;
	
	/**
	 * Variables formulaire.
	 */
	private String nomFile;
	private List<String> formats = new ArrayList<String>();
	private String selectedFormat;
	private Boolean anonyme;
	private Boolean canHistorique;
	
	private CederObjetDecoratorFactory cedeObjFactory;

	private BlocImpressionRowRenderer blocImpressionRenderer 
		= new BlocImpressionRowRenderer(false);
	private BlocImpressionRowRenderer blocImpressionRendererEdit
		= new BlocImpressionRowRenderer(true);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// reference vers des boutons non affichés
		editC = new Button();
		validateC = new Button();
		createC = new Button();
		revertC = new Button();
		deleteC = new Button();	
		cancelC = new Button();
		addNewC = new Button();
		
		super.doAfterCompose(comp);
		
		if (winPanel != null) {
			winPanel.setHeight(getMainWindow().getPanelHeight() - 10 + "px");
		}
		
		// Initialisation des listes de composants
		setObjLabelsComponents(new Component[]{
				descriptionRow,
				enteteLabel,
				piedPageLabel,
				contenuStaticGrid,
				groupContenu
		});
		
		setObjBoxsComponents(new Component[]{
				enteteBox,
				piedPageBox,
				contenuEditGrid
		});
		
		setRequiredMarks(new Component[]{
		});	
		
		getBinder().loadAll();
	}
	
	@Override
	public TKdataObject getObject() {
		return this.template;
	}

	@Override
	public void setObject(TKdataObject obj) {
		this.template = (Template) obj;
		blocImpressionRenderer.setTemplate(template);
		
		blocImpressionsDecorated = new ArrayList<BlocImpressionDecorator>();
		if (this.template.getTemplateId() != null) {
			generateListeBlocs();
		}
		
		super.setObject(obj);
	}
	
	/**
	 * Méthode générant la liste des BlocImpressionDecorators en
	 * fonction de l'ordre des objets en base.
	 */
	public void generateListeBlocs() {
		List<BlocImpressionTemplate> temps = ManagerLocator
			.getBlocImpressionTemplateManager()
			.findByTemplateManager(template);
		
		List<TableAnnotationTemplate> tables = ManagerLocator
			.getTableAnnotationTemplateManager()
			.findByTemplateManager(template);
		
		int i = 0;
		int j = 0;
		// on parcourt les 2 listes en entier
		while (i < temps.size() || j < tables.size()) {
			BlocImpressionTemplate bloc = null;
			TableAnnotationTemplate anno = null;
			if (i < temps.size()) {
				bloc = temps.get(i);
			}
			if (j < tables.size()) {
				anno = tables.get(j);
			}
			
			// si on arrive a extraire un bloc et une annotation
			if (bloc != null && anno != null) {
				// on ajoute à la liste finale celui qui a le +
				// petit ordre
				if (anno.getOrdre() < bloc.getOrdre()) {
					BlocImpressionDecorator deco = 
						new BlocImpressionDecorator(
								null, anno.getTableAnnotation(), template);
					blocImpressionsDecorated.add(deco);
					++j;
				} else {
					BlocImpressionDecorator deco = 
						new BlocImpressionDecorator(
								bloc.getBlocImpression(), null, template);
					blocImpressionsDecorated.add(deco);
					++i;
				}
			} else if (bloc != null) {
				// s'il ne reste que des blocs
				BlocImpressionDecorator deco = 
					new BlocImpressionDecorator(
							bloc.getBlocImpression(), null, template);
				blocImpressionsDecorated.add(deco);
				++i;
			} else if (anno != null) {
				// s'il ne reste que des annotations
				BlocImpressionDecorator deco = 
					new BlocImpressionDecorator(
							null, anno.getTableAnnotation(), template);
				blocImpressionsDecorated.add(deco);
				++j;
			}
		}
	}
	
	@Override
	public void setNewObject() {
		setObject(new Template());	
	}
	
	@Override
	public void cloneObject() {
		setClone(this.template.clone());
	}
	
	@Override
	public void onClick$editC() {		
	}
	
	@Override
	public void onClick$addNewC() {	
	}
	
	@Override
	public void onClick$deleteC() {
	}
	
	@Override
	public void onClick$createC() {
	}
	
	@Override
	public void onClick$validateC() {
	}
	
	@Override
	public void onClick$revertC() {
	}	
	
	public void switchToCreateMode() {		
		super.switchToCreateMode();
		
		this.blocImpressionsDecorated = 
			new ArrayList<BlocImpressionDecorator>();	
		
		contenuEditGrid.setVisible(false);	
	}
	
	public void switchToStaticMode() {
		super.switchToStaticMode(this.template.equals(new Template()));
	}
	
	
	public void switchToEditMode() {
		super.switchToEditMode();		
	}
	

	/***********************************************************/
	/*****************  Events controllers.  *******************/
	/***********************************************************/

	@Override
	public void createNewObject() {
	}
	
	public void defineAllBlocImpressions() {
		groupContenu.setVisible(true);
		
		this.template.setEntite(selectedEntite);
		contenuEditGrid.setVisible(true);
		
		// on récupère tous les blocs pour l'entité
		List<BlocImpression> blocImpressions = 
			ManagerLocator.getBlocImpressionManager()
			.findByEntiteManager(selectedEntite);
		
		for (int i = 0; i < blocImpressions.size(); i++) {
			BlocImpressionDecorator deco = 
				new BlocImpressionDecorator(
						blocImpressions.get(i), null, template);
			blocImpressionsDecorated.add(deco);
		}
		
		// on récupère toutes les tables d'annotations pour
		// l'entité et la banque
		List<TableAnnotation> tables = ManagerLocator
			.getTableAnnotationManager()
			.findByEntiteAndBanqueManager(
			selectedEntite, SessionUtils
					.getSelectedBanques(sessionScope).get(0));
		for (int i = 0; i < tables.size(); i++) {
			BlocImpressionDecorator deco = 
				new BlocImpressionDecorator(null, 
						tables.get(i), template);
			blocImpressionsDecorated.add(deco);
		}
	}

	@Override
	public void setEmptyToNulls() {	
	}

	@Override
	public void updateObject() {
	}	
	
	public void onSelect$templatesBox() {
		if (selectedTemplate.getTemplateId() != null) {
			setObject(selectedTemplate);
			switchToStaticMode();
		} else {
			switchToCreateMode();
			defineAllBlocImpressions();
		}
	}
	
	@Override
	public void setFieldsToUpperCase() {
	}
	
	public void onSelect$formatsBox() {
		selectedFormat = formats.get(formatsBox.getSelectedIndex());
		
		if (nomFile.contains(".")) {
			nomFile = nomFile.substring(0, nomFile.indexOf(".") + 1);
		}
		
		nomFile = nomFile.concat(selectedFormat.toLowerCase());
	}
	
	public void initFicheModale(Object obj, Boolean ano, 
			Boolean canHisto) {
	   // [YouTrack] TK-62
		//if (obj.getClass().getSimpleName().equals("FichePatientStatic")) {
	   if (obj instanceof FichePatientStatic) {
			fichePatient = (FichePatientStatic) obj;
			this.objectToPrint = fichePatient.getObject();
		} else {
			this.objectToPrint = obj;
		}
		
		this.anonyme = ano;
		this.canHistorique = canHisto;
		
		selectedEntite = ManagerLocator.getEntiteManager()
			.findByNomManager(objectToPrint.getClass()
			.getSimpleName()).get(0);
		
		templates = ManagerLocator.getTemplateManager()
			.findByBanqueEntiteManager(
					SessionUtils.getSelectedBanques(sessionScope).get(0), 
															selectedEntite);
		Template newOne = new Template();
		newOne.setNom(Labels.getLabel("template.new"));
		templates.add(newOne);
		selectedTemplate = templates.get(0);
		
		if (selectedTemplate.getTemplateId() != null) {
			setObject(selectedTemplate);
			switchToStaticMode();
		} else {
			switchToCreateMode();
			defineAllBlocImpressions();
		}
		
		rowHistorique.setVisible(canHistorique);
		
		nomFile = selectedEntite.getNom().toLowerCase() + ".pdf";
		formats.add("PDF");
		formats.add("HTML");
		selectedFormat = formats.get(0);
		
		getBinder().loadComponent(self);
	}
	
	/**
	 * Clic sur le bouton print.
	 */
	public void onClick$print() {
		if (checksBlocValid()) {
			Clients.showBusy(Labels.getLabel("impression.encours"));
			Events.echoEvent("onLaterPrint", self, null);
		} else {
			throw new WrongValueException(
					print, Labels.getLabel("impression.error"));
		}
	}
	
	public Document creerDocumentBoite() {
		Document doc = ManagerLocator.getXmlUtils().createJDomDocumentBoites();
		Element root = doc.getRootElement();
		Element page = ManagerLocator.getXmlUtils().addPageBoite(root, 
				"Déstockage des échantillons");
		
		ManagerLocator.getXmlUtils()
								.addTitreIntermediaire(page, "Echantillons");
		
		return doc;
	}
	
	public void onLaterPrint() {
		// création du document XML contenant les données à imprimer
		createDocumentXML();
		
		// Transformation du document en fichier
		byte[] dl = null;
		try {
			if (selectedFormat.equals("PDF")) {
				dl = ManagerLocator.getXmlUtils().creerPdf(document);
			} else if (selectedFormat.equals("HTML")) {
				dl = ManagerLocator.getXmlUtils().creerHtml(document);
			}
			
		} catch (Exception e) {
			log.error(e);
		}
		
		// ferme wait message
		Clients.clearBusy();
		
		// envoie du fichier à imprimer à l'utilisateur
		if (dl != null) {
			// si le fichier est un pdf, l'utilisateur doit le dl
			if (selectedFormat.equals("PDF")) {
				Filedownload.save(dl, "application/pdf", nomFile);
			} else if (selectedFormat.equals("HTML")) {
				// si c'est au format html, on va ouvrir une nouvelle
				// fenêtre
				try {
					sessionScope.put("File", dl);
					execution.sendRedirect("/impression", "_blank");
				} catch (Exception e) {
					if (sessionScope.containsKey("File")) {
						sessionScope.remove("File");
						dl = null;
					}
				}
			}
			dl = null;
		}
		
		// fermeture de la fenêtre
		Events.postEvent(new Event("onClose", self.getRoot()));
	}
	
	/**
	 * Appelle la bonne méthode de création du document en fonction
	 * du type d'objet à imprimer.
	 */
	public void createDocumentXML() {
		document = ManagerLocator.getXmlUtils().createJDomDocument();
		Element root = document.getRootElement();
		
		// création du titre
		String titre = null;
		if (selectedEntite.getNom().equals("Patient")) {
			if (!anonyme) {
				Patient patient = (Patient) objectToPrint;
				StringBuffer sb = new StringBuffer();
				sb.append(patient.getNom());
				sb.append(" ");
				sb.append(patient.getPrenom());
				titre = ObjectTypesFormatters
					.getLabel("impression.titre.patient", 
						new String[]{sb.toString()});
			}
		} else if (selectedEntite.getNom().equals("Prelevement")) {
			Prelevement prlvt = (Prelevement) objectToPrint;
			titre = ObjectTypesFormatters
				.getLabel("impression.titre.prelevement", 
					new String[]{prlvt.getCode()});
		} else if (selectedEntite.getNom().equals("Echantillon")) {
			Echantillon echan = (Echantillon) objectToPrint;
			titre = ObjectTypesFormatters
				.getLabel("impression.titre.echantillon", 
					new String[]{echan.getCode()});
		} else if (selectedEntite.getNom().equals("ProdDerive")) {
			ProdDerive prod = (ProdDerive) objectToPrint;
			titre = ObjectTypesFormatters
				.getLabel("impression.titre.prodDerive", 
					new String[]{prod.getCode()});
		} else if (selectedEntite.getNom().equals("Cession")) {
			Cession cess = (Cession) objectToPrint;
			titre = ObjectTypesFormatters
				.getLabel("impression.titre.cession", 
					new String[]{String.valueOf(cess.getNumero())});
		}
		
		pageXML = ManagerLocator.getXmlUtils().addPage(root, titre);
		
		// ajout de la date en pied de page
		StringBuffer sb = new StringBuffer();
		if (template.getPiedPage() != null
				&& !template.getPiedPage().equals("")) {
			sb.append(template.getPiedPage());
			sb.append(" - ");
		}
		Calendar cal = Calendar.getInstance();
		String date = new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
		sb.append(date);
		
		ManagerLocator.getXmlUtils().addBasDePage(root, sb.toString());
		ManagerLocator.getXmlUtils().addHautDePage(root, template.getEnTete(),
				false, null);
		
		if (selectedEntite.getNom().equals("Patient")) {
			createDocumentForPatient();
		} else if (selectedEntite.getNom().equals("Prelevement")) {
			createDocumentForPrelevement();
		} else if (selectedEntite.getNom().equals("Echantillon")) {
			createDocumentForEchantillon();
		} else if (selectedEntite.getNom().equals("ProdDerive")) {
			createDocumentForProdDerives();
		} else if (selectedEntite.getNom().equals("Cession")) {
			createDocumentForCession();
		}
	}
	
	/**
	 * Crée le document pour les patients.
	 */
	public void createDocumentForPatient() {
		Patient patient = (Patient) objectToPrint;
		for (int i = 0; i < blocImpressionsDecorated.size(); i++) {
			if (blocImpressionsDecorated.get(i).getImprimer()) {
				if (blocImpressionsDecorated.get(i)
						.getBlocImpression() != null) {
					if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.patient.principal")) {
						createBlocPrincipalPatient(patient);
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.patient.medecins")) {
						createBlocListeCollaborateurs(
								fichePatient.getMedecins(), 
								blocImpressionsDecorated.get(i)
									.getChampEntites());
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.patient.maladies")) {
						createBlocListeMaladies(
								fichePatient.getMaladies(), 
								blocImpressionsDecorated.get(i)
								.getChampEntites());
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.patient.prelevements")) {
						List<Prelevement> prelevements = new 
							ArrayList<Prelevement>();
						prelevements.addAll(fichePatient
							.getPrelevementsFromOtherMaladies());
						
						for (int j = 0; j < fichePatient
							.getMaladiePanels().size(); j++) {
							FicheMaladie fiche = fichePatient
								.getMaladiePanels().get(j);
							prelevements.addAll(fiche.getPrelevements());
							prelevements.addAll(fiche
									.getPrelevementsFromOtherBanks());
						}
						
						createBlocListePrelevements(prelevements, 
								blocImpressionsDecorated.get(i)
								.getChampEntites());
					}
				} else if (blocImpressionsDecorated.get(i)
						.getTableAnnotation() != null) {
					createBlocAnnotationForObject(
							patient, 
							blocImpressionsDecorated.get(i)
								.getTableAnnotation());
				}
			}
		}
		if (canHistorique && historiqueBox.isChecked()) {
			createBlocListeOperation(ManagerLocator
				.getOperationManager().findByObjectManager(patient));
		}
	}
	
	/**
	 * Crée le document pour les prélèvements.
	 */
	public void createDocumentForPrelevement() {
		Prelevement prelevement = (Prelevement) objectToPrint;
		for (int i = 0; i < blocImpressionsDecorated.size(); i++) {
			if (blocImpressionsDecorated.get(i).getImprimer()) {
				if (blocImpressionsDecorated.get(i)
						.getBlocImpression() != null) {
					if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.prelevement.principal")) {
						createBlocPrincipalPrelevement(prelevement);
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.prelevement.patient")) {
						createBlocPatientForPrelevement(prelevement);
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.prelevement" 
								+ ".informations.prelevement")) {
						createBlocInfosPrelevement(prelevement);
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.prelevement.laboInter")) {
						createBlocLaboIntersPrelevement(prelevement);
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.prelevement.echantillons")) {
						Set<Echantillon> echantillons = ManagerLocator
							.getPrelevementManager()
							.getEchantillonsManager(prelevement);
						createBlocListeEchantillon(echantillons, 
								blocImpressionsDecorated.get(i)
								.getChampEntites());
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.prelevement.prodDerives")) {
						createBlocListeProdDerive(
								ManagerLocator.getPrelevementManager()
									.getProdDerivesManager(prelevement), 
								blocImpressionsDecorated.get(i)
									.getChampEntites());
					}
				} else if (blocImpressionsDecorated.get(i)
						.getTableAnnotation() != null) {
					createBlocAnnotationForObject(
							prelevement, 
							blocImpressionsDecorated.get(i)
								.getTableAnnotation());
				}
			}
		}
		if (canHistorique && historiqueBox.isChecked()) {
			createBlocListeOperation(ManagerLocator
				.getOperationManager().findByObjectManager(prelevement));
		}
	}
	
	/**
	 * Crée le document pour les échantillons.
	 */
	public void createDocumentForEchantillon() {
		cedeObjFactory = new CederObjetDecoratorFactory(anonyme, null);
		Echantillon echantillon = (Echantillon) objectToPrint;
		for (int i = 0; i < blocImpressionsDecorated.size(); i++) {
			if (blocImpressionsDecorated.get(i).getImprimer()) {
				if (blocImpressionsDecorated.get(i)
						.getBlocImpression() != null) {
					if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.echantillon.principal")) {
						createBlocPrincipalEchantillon(echantillon);
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.echantillon.informations" 
								+ ".prelevement")) {
						createBlocPrelevementForEchantillon(echantillon);
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.echantillon" 
								+ ".informations.echantillon")) {
						createBlocInfosForEchantillon(echantillon);
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.echantillon" 
								+ ".informations.complementaires")) {
						createBlocComplementaireForEchantillon(echantillon);
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.echantillon.prodDerives")) {
						createBlocListeProdDerive(
								ManagerLocator.getEchantillonManager()
									.getProdDerivesManager(echantillon), 
								blocImpressionsDecorated.get(i)
									.getChampEntites());
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.echantillon.cessions")) {
						createBlocListeCessions(
							cedeObjFactory.decorateListe(
								ManagerLocator.getCederObjetManager()
								.findByObjetManager(echantillon)), 
							blocImpressionsDecorated.get(i)
								.getChampEntites());
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.echantillon.retours")) {
						createBlocListeRetours(ManagerLocator
								.getRetourManager()
								.getRetoursForObjectManager(echantillon));
					}
				} else if (blocImpressionsDecorated.get(i)
						.getTableAnnotation() != null) {
					createBlocAnnotationForObject(
							echantillon, 
							blocImpressionsDecorated.get(i)
								.getTableAnnotation());
				}
			}
		}
		if (canHistorique && historiqueBox.isChecked()) {
			createBlocListeOperation(ManagerLocator
				.getOperationManager().findByObjectManager(echantillon));
		}
	}
	
	/**
	 * Crée le document pour les ProdDerives.
	 */
	public void createDocumentForProdDerives() {
		cedeObjFactory = new CederObjetDecoratorFactory(anonyme, null);
		ProdDerive derive = (ProdDerive) objectToPrint;
		for (int i = 0; i < blocImpressionsDecorated.size(); i++) {
			if (blocImpressionsDecorated.get(i).getImprimer()) {
				if (blocImpressionsDecorated.get(i)
						.getBlocImpression() != null) {
					if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.prodDerive.principal")) {
						createBlocPrincipalProdDerive(derive);
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.prodDerive.parent")) {
						createBlocParentForProdDerive(derive);
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.prodDerive.informations" 
									+ ".complementaires")) {
						createBlocComplementaireForProdDerive(derive);
					} else if (blocImpressionsDecorated.get(i)
								.getBlocImpression().getNom()
								.equals("bloc.prodDerive.prodDerives")) {
						createBlocListeProdDerive(
								ManagerLocator.getProdDeriveManager()
									.getProdDerivesManager(derive), 
								blocImpressionsDecorated.get(i)
									.getChampEntites());
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.prodDerive.cessions")) {
						createBlocListeCessions(cedeObjFactory.decorateListe(
								ManagerLocator.getCederObjetManager()
								.findByObjetManager(derive)), 
							blocImpressionsDecorated.get(i)
								.getChampEntites());
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.prodDerive.retours")) {
						createBlocListeRetours(ManagerLocator
								.getRetourManager()
								.getRetoursForObjectManager(derive));
					}
				} else if (blocImpressionsDecorated.get(i)
						.getTableAnnotation() != null) {
					createBlocAnnotationForObject(
							derive, 
							blocImpressionsDecorated.get(i)
								.getTableAnnotation());
				}
			}
		}
		if (canHistorique && historiqueBox.isChecked()) {
			createBlocListeOperation(ManagerLocator
				.getOperationManager().findByObjectManager(derive));
		}
	}
	
	/**
	 * Crée le document pour les cessions.
	 */
	public void createDocumentForCession() {
		cedeObjFactory = new CederObjetDecoratorFactory(anonyme, null);
		Cession cession = (Cession) objectToPrint;
		for (int i = 0; i < blocImpressionsDecorated.size(); i++) {
			if (blocImpressionsDecorated.get(i).getImprimer()) {
				if (blocImpressionsDecorated.get(i)
						.getBlocImpression() != null) {
					if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.cession.principal")) {
						createBlocPrincipalCession(cession);
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.cession.echantillons")) {
						
						createBlocListeCederObjets(cedeObjFactory
								.decorateListe(ManagerLocator
										.getCederObjetManager()
										.getEchantillonsCedesByCessionManager(
												cession)), 
								blocImpressionsDecorated.get(i)
									.getChampEntites(),
									true);
						
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.cession.prodDerives")) {
						
						createBlocListeCederObjets(cedeObjFactory
								.decorateListe(ManagerLocator
										.getCederObjetManager()
										.getProdDerivesCedesByCessionManager(
												cession)), 
								blocImpressionsDecorated.get(i)
									.getChampEntites(),
									false);
						
					} else if (blocImpressionsDecorated.get(i)
							.getBlocImpression().getNom()
							.equals("bloc.cession.informations.cession")) {
						createBlocInformationsCession(cession);
					}
				} else if (blocImpressionsDecorated.get(i)
						.getTableAnnotation() != null) {
					createBlocAnnotationForObject(
							cession, 
							blocImpressionsDecorated.get(i)
								.getTableAnnotation());
				}
			}
		}
		if (canHistorique && historiqueBox.isChecked()) {
			createBlocListeOperation(ManagerLocator
				.getOperationManager().findByObjectManager(cession));
		}
	}
	
	/*****************************************************************/
	/*************     Impression des patients        ****************/
	/*****************************************************************/
	/**
	 * Crée la partie du document qui contiendra le paragraphe sur les
	 * infos principales du patient.
	 * @param patient Patient à imprimer.
	 */
	public void createBlocPrincipalPatient(Patient patient) {
		
		String tmp = "";
		CoupleValeur cpVide = new CoupleValeur("", "");
		
		// NIP
		if (patient.getNip() != null) {
			tmp = patient.getNip();
		} else {
			tmp = "-";
		}
		CoupleValeur cp1 = new CoupleValeur(
				Labels.getLabel("Champ.Patient.Nip"), 
				tmp);
		cp1.setAnonyme(anonyme);
		LigneParagraphe li1 = new LigneParagraphe("",
				new CoupleValeur[]{cp1, cpVide});
		
		// Nom
		CoupleValeur cp2 = new CoupleValeur(
				Labels.getLabel("Champ.Patient.Nom"), 
				patient.getNom());
		cp2.setAnonyme(anonyme);
		// Nom patronymique
		if (patient.getNomNaissance() != null) {
			tmp = patient.getNomNaissance();
		} else {
			tmp = "-";
		}
		CoupleValeur cp3 = new CoupleValeur(
				Labels.getLabel("Champ.Patient.NomNaissance"), 
				tmp);
		cp3.setAnonyme(anonyme);
		LigneParagraphe li2 = new LigneParagraphe("",
				new CoupleValeur[]{cp2, cp3});
		
		// Prénom
		if (patient.getPrenom() != null) {
			tmp = patient.getPrenom();
		} else {
			tmp = "-";
		}
		CoupleValeur cp4 = new CoupleValeur(
				Labels.getLabel("Champ.Patient.Prenom"), 
				tmp);
		cp4.setAnonyme(anonyme);
		// Nom patronymique
		if (patient.getSexe() != null) {
			tmp = PatientUtils.setSexeFromDBValue(patient);
		} else {
			tmp = "-";
		}
		CoupleValeur cp5 = new CoupleValeur(
				Labels.getLabel("Champ.Patient.Sexe"), 
				tmp);
		LigneParagraphe li3 = new LigneParagraphe("",
				new CoupleValeur[]{cp4, cp5});
		
		// Date de naissance
		if (patient.getDateNaissance() != null) {
			tmp = ObjectTypesFormatters
				.dateRenderer2(patient.getDateNaissance());
		} else {
			tmp = "-";
		}
		CoupleValeur cp6 = new CoupleValeur(
				Labels.getLabel("Champ.Patient.DateNaissance"), 
				tmp);
		cp6.setAnonyme(anonyme);
		LigneParagraphe li4 = new LigneParagraphe("",
				new CoupleValeur[]{cp6, cpVide});
		
		// Pays
		if (patient.getPaysNaissance() != null) {
			tmp = patient.getPaysNaissance();
		} else {
			tmp = "-";
		}
		CoupleValeur cp7 = new CoupleValeur(
				Labels.getLabel("Champ.Patient.PaysNaissance"), 
				tmp);
		// Ville
		if (patient.getVilleNaissance() != null) {
			tmp = patient.getVilleNaissance();
		} else {
			tmp = "-";
		}
		CoupleValeur cp8 = new CoupleValeur(
				Labels.getLabel("Champ.Patient.VilleNaissance"), 
				tmp);
		LigneParagraphe li5 = new LigneParagraphe("",
				new CoupleValeur[]{cp7, cp8});
		
		// Etat
		if (patient.getPatientEtat() != null) {
			tmp = PatientUtils.setEtatFromDBValue(patient);
		} else {
			tmp = "-";
		}
		CoupleValeur cp9 = new CoupleValeur(
				Labels.getLabel("Champ.Patient.PatientEtat"), 
				tmp);
		// Date état
		if (patient.getDateEtat() != null) {
			tmp = PatientUtils.getDateDecesOrEtat(patient);
		} else {
			tmp = "-";
		}
		CoupleValeur cp10 = new CoupleValeur(
				Labels.getLabel("Champ.Patient.DateEtat"), 
				tmp);
		LigneParagraphe li6 = new LigneParagraphe("",
				new CoupleValeur[]{cp9, cp10});
		
		Paragraphe par1 = new Paragraphe(
				Labels.getLabel("bloc.patient.principal"),  
				new LigneParagraphe[]{li1, li2, li3, li4, li5, li6}, 
				null,
				null, null);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par1);
	}
	
	/*****************************************************************/
	/*************     Impression des prélèvements    ****************/
	/*****************************************************************/
	/**
	 * Crée la partie du document qui contiendra le paragraphe sur les
	 * infis principales du prélèvement.
	 * @param prelevement Prelevement à imprimer.
	 */
	public void createBlocPrincipalPrelevement(Prelevement prelevement) {
		CoupleValeur cp1 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.Code"), 
				prelevement.getCode());
		CoupleValeur cp2 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.NumeroLabo"), 
				prelevement.getNumeroLabo());
		CoupleValeur cp3 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.Nature"), 
				prelevement.getNature().getNature());
		CoupleValeur cpVide = new CoupleValeur("", "");
		LigneParagraphe li1 = new LigneParagraphe("li1",
				new CoupleValeur[]{cp1, cp2});
		LigneParagraphe li1Bis = new LigneParagraphe("li1Bis",
				new CoupleValeur[]{cp3, cpVide});
		Paragraphe par1 = new Paragraphe(
				Labels.getLabel("bloc.prelevement.principal"),  
				new LigneParagraphe[]{li1, li1Bis}, 
				null,
				null, null);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par1);
	}
	
	/**
	 * Crée la partie du document qui contiendra le paragraphe sur les
	 * infos complémentaires du prélèvement.
	 * @param prelevement Prelevement à imprimer.
	 */
	public void createBlocInfosPrelevement(Prelevement prelevement) {
		// Informations prélèvement
		String tmp = "";
		CoupleValeur cpVide = new CoupleValeur("", "");
		CoupleValeur cp12 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.DatePrelevement"), 
				ObjectTypesFormatters
					.dateRenderer2(prelevement.getDatePrelevement()));
		
		// prele type
		if (prelevement.getPrelevementType() != null) {
			tmp = prelevement.getPrelevementType().getType();
		} else {
			tmp = "";
		}
		CoupleValeur cp13 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.PrelevementType"), 
				tmp);
		
		// sterile
		CoupleValeur cp14 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.Sterile"), 
				ObjectTypesFormatters
				.booleanLitteralFormatter(prelevement.getSterile()));
		
		// risque
		Iterator<Risque> risksIt = ManagerLocator
			.getPrelevementManager()
			.getRisquesManager(prelevement).iterator();
		StringBuffer sb = new StringBuffer();
		while (risksIt.hasNext()) {
			sb.append(risksIt.next().getNom());
			if (risksIt.hasNext()) {
				sb.append(", ");
			} else {
				sb.append(".");
			}
		}
		CoupleValeur cp24 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.Risque"), 
				sb.toString());
		
		// établissement
		if (prelevement.getServicePreleveur() != null) {
			sb = new StringBuffer();
			if (prelevement.getServicePreleveur()
					.getEtablissement() != null) {
				if (prelevement.getServicePreleveur()
						.getEtablissement().getNom() != null) {
					sb.append(prelevement.getServicePreleveur()
							.getEtablissement().getNom());
				}
				if (prelevement.getServicePreleveur()
						.getEtablissement().getCoordonnee() != null
						&& prelevement.getServicePreleveur()
						.getEtablissement().getCoordonnee().getPays() != null
						&& !prelevement.getServicePreleveur()
						.getEtablissement().getCoordonnee()
						.getPays().equals("")) {
					sb.append(" (");
					sb.append(prelevement.getServicePreleveur()
						.getEtablissement().getCoordonnee().getPays());
					sb.append(")");
				}
			}
			tmp = sb.toString();
		} else {
			tmp = "";
		}
		CoupleValeur cp16 = new CoupleValeur(
				Labels.getLabel("prelevement.etablissement"), 
				tmp);
		
		// service
		if (prelevement.getServicePreleveur() != null) {
			tmp = prelevement.getServicePreleveur().getNom();
		} else {
			tmp = "";
		}
		CoupleValeur cp17 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.ServicePreleveur"), 
				tmp);
		
		// opérateur
		if (prelevement.getPreleveur() != null) {
			tmp = prelevement.getPreleveur().getNomAndPrenom();
		} else {
			tmp = "";
		}
		CoupleValeur cp18 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.Preleveur"), 
				tmp);
		LigneParagraphe li6 = new LigneParagraphe("",
				new CoupleValeur[]{cp12, cp13});
		LigneParagraphe li7 = new LigneParagraphe("",
				new CoupleValeur[]{cp14, cp24});
		LigneDeuxColonnesParagraphe li8 = new LigneDeuxColonnesParagraphe(
				cp16);
		LigneDeuxColonnesParagraphe li9 = new LigneDeuxColonnesParagraphe(
				cp17);
		LigneDeuxColonnesParagraphe li10 = new LigneDeuxColonnesParagraphe(
				cp18);
		// conditionnement
		if (prelevement.getConditType() != null) {
			tmp = prelevement.getConditType().getType();
		} else {
			tmp = "";
		}
		CoupleValeur cp19 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.ConditType"), 
				tmp);
		// nb conditionnement
		if (prelevement.getConditNbr() != null) {
			tmp = prelevement.getConditNbr().toString();
		} else {
			tmp = "";
		}
		CoupleValeur cp20 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.ConditNbr"), 
				tmp);
		// milieu
		if (prelevement.getConditMilieu() != null) {
			tmp = prelevement.getConditMilieu().getMilieu();
		} else {
			tmp = "";
		}
		CoupleValeur cp21 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.ConditMilieu"), 
				tmp);
		LigneParagraphe li11 = new LigneParagraphe("",
				new CoupleValeur[]{cp19, cp20});
		LigneParagraphe li12 = new LigneParagraphe("",
				new CoupleValeur[]{cpVide, cp21});
		SousParagraphe sousPar2 = new SousParagraphe(
				Labels.getLabel("fichePrelevement.group.conditionnement"), 
				new LigneParagraphe[]{li11, li12},
				null, null);
		// statut juridique
		if (prelevement.getConsentType() != null) {
			tmp = prelevement.getConsentType().getType();
		} else {
			tmp = "";
		}
		CoupleValeur cp22 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.ConsentType"), 
				tmp);
		CoupleValeur cp23 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.ConsentDate"), 
				ObjectTypesFormatters
					.dateRenderer2(prelevement.getConsentDate()));
		LigneParagraphe li13 = new LigneParagraphe("",
				new CoupleValeur[]{cp22, cp23});
		SousParagraphe sousPar3 = new SousParagraphe(
				Labels.getLabel("Champ.Prelevement.ConsentType"), 
				new LigneParagraphe[]{li13},
				null, null);
		
		Paragraphe par3 = new Paragraphe(
				Labels.getLabel("bloc.prelevement.informations.prelevement"),  
				new Object[]{li6, li7, li8, li9, li10}, 
				new SousParagraphe[]{sousPar2, sousPar3},
				null, null);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par3);
	}
	
	/**
	 * Crée la partie du document qui contiendra le paragraphe sur le
	 * patient du prélèvement.
	 * @param prelevement Prelevement à imprimer.
	 */
	public void createBlocPatientForPrelevement(Prelevement prelevement) {
		// Partie Patient
		if (prelevement.getMaladie() == null) {
			Paragraphe par2 = new Paragraphe(
					Labels.getLabel("Champ.Maladie.Patient"),  
					null, 
					null, 
					Labels.getLabel("prelevement.patient.inconnu"), 
					null);
			ManagerLocator.getXmlUtils().addParagraphe(pageXML, par2);
		} else {
			CoupleValeur cp4 = new CoupleValeur(
					Labels.getLabel("Champ.Patient.Nip"), 
					prelevement.getMaladie().getPatient().getNip());
			cp4.setAnonyme(anonyme);
			CoupleValeur cp5 = new CoupleValeur(
					Labels.getLabel("Champ.Prelevement.PatientNda"), 
					prelevement.getPatientNda());
			cp5.setAnonyme(anonyme);
			CoupleValeur cp6 = new CoupleValeur(
					Labels.getLabel("Champ.Patient.Nom"), 
					prelevement.getMaladie().getPatient().getNom());
			cp6.setAnonyme(anonyme);
			CoupleValeur cp7 = new CoupleValeur(
					Labels.getLabel("Champ.Patient.Prenom"), 
					prelevement.getMaladie().getPatient().getPrenom());
			cp7.setAnonyme(anonyme);
			CoupleValeur cp8 = new CoupleValeur(
					Labels.getLabel("Champ.Patient.DateNaissance"), 
					ObjectTypesFormatters
						.dateRenderer2(prelevement.getMaladie().getPatient()
							.getDateNaissance()));
			cp8.setAnonyme(anonyme);
			CoupleValeur cp9 = new CoupleValeur(
					Labels.getLabel("Champ.Patient.Sexe"), 
					prelevement.getMaladie().getPatient().getSexe());
			
			// etat + date de l'état
			CoupleValeur cp12 = new CoupleValeur(
					Labels.getLabel("Champ.Patient.PatientEtat"), 
					PatientUtils.setEtatFromDBValue(
							prelevement.getMaladie().getPatient()));
			CoupleValeur cp13 = new CoupleValeur(
					Labels.getLabel("Champ.Patient.DateEtat"), 
					PatientUtils.getDateDecesOrEtat(
							prelevement.getMaladie().getPatient()));
			LigneParagraphe li2 = new LigneParagraphe("li2",
					new CoupleValeur[]{cp4, cp5});
			LigneParagraphe li3 = new LigneParagraphe("li3",
					new CoupleValeur[]{cp6, cp7});
			LigneParagraphe li4 = new LigneParagraphe("li4",
					new CoupleValeur[]{cp8, cp9});
			LigneParagraphe li6 = new LigneParagraphe("li6",
					new CoupleValeur[]{cp12, cp13});
			
			CoupleValeur cp10 = new CoupleValeur(
					Labels.getLabel("Champ.Maladie.Libelle"), 
					prelevement.getMaladie().getLibelle());
			CoupleValeur cp11 = new CoupleValeur(
					Labels.getLabel("Champ.Maladie.Code"), 
					prelevement.getMaladie().getCode());
			LigneParagraphe li5 = new LigneParagraphe("li5",
					new CoupleValeur[]{cp10, cp11});
			SousParagraphe sousPar1 = new SousParagraphe(
					Labels.getLabel("Champ.Prelevement.Maladie"), 
					new LigneParagraphe[]{li5},
					null, null);
			Paragraphe par2 = new Paragraphe(
					Labels.getLabel("bloc.prelevement.patient"),  
					new LigneParagraphe[]{li2, li3, li4, li6}, 
					new SousParagraphe[]{sousPar1},
					null, null);
			ManagerLocator.getXmlUtils().addParagraphe(pageXML, par2);
		}
		
	}
	
	/**
	 * Crée la partie du document qui contiendra le paragraphe sur les
	 * labo intermédiaires du prélèvement.
	 * @param prelevement Prelevement à imprimer.
	 */
	public void createBlocLaboIntersPrelevement(Prelevement prelevement) {
		String tmp = "";
		CoupleValeur cp1 = new CoupleValeur(
			Labels.getLabel("Champ.Prelevement.DateDepart"), 
				ObjectTypesFormatters
								.dateRenderer2(prelevement.getDateDepart()));
		// transporteur
		if (prelevement.getTransporteur() != null) {
			tmp = prelevement.getTransporteur().getNom();
		} else {
			tmp = "";
		}
		CoupleValeur cp2 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.Transporteur"), 
				tmp);
		// temp de transport
		if (prelevement.getTransportTemp() != null) {
			tmp = prelevement.getTransportTemp().toString() + "°C";
		} else {
			tmp = "";
		}
		CoupleValeur cp3 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.TransportTemp"), 
				tmp);
		LigneParagraphe li1 = new LigneParagraphe("",
				new CoupleValeur[]{cp1});
		LigneParagraphe li2 = new LigneParagraphe("",
				new CoupleValeur[]{cp2, cp3});
		SousParagraphe sousPar4 = new SousParagraphe(
				Labels.getLabel("fichePrelevement.departPreleveur"), 
				new LigneParagraphe[]{li1, li2},
				null, null);
		
		// Sites intermédiaires
		EnteteListe entetes = new EnteteListe(new String[]{
				Labels.getLabel("laboInter.dateArrivee"),
				Labels.getLabel("laboInter.dateDepart"),
				Labels.getLabel("laboInter.service"),
				Labels.getLabel("prelevement.laboInters.transportTemp")
		});
		
		List<LaboInter> laboInters = ManagerLocator
			.getPrelevementManager()
			.getLaboIntersWithOrderManager(prelevement);
		// liste des labo intermédiaires
		
		LigneListe[] liste = new LigneListe[laboInters.size()];
		for (int i = 0; i < laboInters.size(); i++) {
			String labo = "";
			// service
			if (laboInters.get(i).getService() != null) {
				labo = laboInters.get(i).getService().getNom();
			} else {
				labo = "";
			}
			
			// température
			String temp = "";
			if (laboInters.get(i).getConservTemp() != null) {
				temp = laboInters.get(i).getConservTemp() + "°C";
			} else {
				temp = "";
			}
			LigneListe ligne = new LigneListe(new String[]{
					ObjectTypesFormatters
						.dateRenderer2(laboInters.get(i).getDateArrivee()),
					ObjectTypesFormatters
						.dateRenderer2(laboInters.get(i).getDateDepart()),
					labo,
					temp
			});
			liste[i] = ligne;
		}
		ListeElement listeSites = null;
		if (laboInters.size() > 0) {
			listeSites = new ListeElement(null, entetes, liste);
		}
		SousParagraphe sousPar5 = new SousParagraphe(
				Labels.getLabel("fichePrelevement.laboInters"), 
				null,
				null, listeSites);
		
		// Arrivée
		CoupleValeur cp4 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.DateArrivee"), 
				ObjectTypesFormatters
					.dateRenderer2(prelevement.getDateArrivee()));
		// opérateur
		if (prelevement.getOperateur() != null) {
			tmp = prelevement.getOperateur().getNomAndPrenom();
		} else {
			tmp = "";
		}
		CoupleValeur cp5 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.Operateur"), 
				tmp);
		// quantité
		if (prelevement.getQuantite() != null) {
			tmp = prelevement.getQuantite().toString();
		} else {
			tmp = "-";
		}
		
		if (prelevement.getQuantiteUnite() != null) {
			tmp = tmp + prelevement.getQuantiteUnite().getUnite();
		}
		CoupleValeur cp6 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.Quantite"), 
				tmp);
		// conforme à l'arrivée
		if (prelevement.getConformeArrivee() != null) {
			StringBuffer sb = new StringBuffer();
			sb.append(ObjectTypesFormatters
				.booleanLitteralFormatter(
						prelevement.getConformeArrivee()));
			
			if (!prelevement.getConformeArrivee()) {
				sb.append(" - ");
				List<ObjetNonConforme> list = ManagerLocator
					.getObjetNonConformeManager()
					.findByObjetAndTypeManager(prelevement, "Arrivee");
				
				if (list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						sb.append(list.get(i).getNonConformite()
								.getNom());
						if (i < list.size() - 1) {
							sb.append(", ");
						} else {
							sb.append(".");
						}
					}
				} else {
					sb.append(Labels.getLabel("nonConformite.raison.inconnue"));
				}
			}
			tmp = sb.toString();
		} else {
			tmp = "-";
		}
		CoupleValeur cp7 = new CoupleValeur(
				Labels.getLabel("Champ.Prelevement.ConformeArrivee"), 
				tmp);
		
		LigneParagraphe li3 = new LigneParagraphe("",
				new CoupleValeur[]{cp4, cp5});
		LigneParagraphe li4 = new LigneParagraphe("",
				new CoupleValeur[]{cp6});
		LigneParagraphe li5 = new LigneParagraphe("",
				new CoupleValeur[]{cp7});
		SousParagraphe sousPar6 = new SousParagraphe(
				Labels.getLabel("fichePrelevement.arriveeStockage"), 
				new LigneParagraphe[]{li3, li4, li5},
				null, null);
		
		Paragraphe par4 = new Paragraphe(
				Labels.getLabel("bloc.prelevement.laboInter"),  
				null, 
				new SousParagraphe[]{sousPar4, sousPar5, sousPar6},
				null, null);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par4);
	}
	
	/**
	 * Crée la partie du document qui contiendra le paragraphe sur 
	 * une table d'annotation.
	 * @param obj Objet à imprimer.
	 */
	public void createBlocAnnotationForObject(
			TKAnnotableObject obj, TableAnnotation table) {
		
		// on récupère tous les champs de la table
		List<ChampAnnotation> champs = ManagerLocator
			.getChampAnnotationManager()
			.findByTableManager(table);
		
		LigneSimpleParagraphe[] liste = new 
			LigneSimpleParagraphe[champs.size()];
		// pour chaque champ
		for (int i = 0; i < champs.size(); i++) {
			// on récupère toutes les valeurs pour le prélèvement
			List<AnnotationValeur> valeurs = ManagerLocator
				.getAnnotationValeurManager()
				.findByChampAndObjetManager(
						champs.get(i), obj);
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < valeurs.size(); j++) {
				sb.append(ObjectTypesFormatters
						.annotationValeurRenderer(valeurs.get(j)));
				
				if (j < valeurs.size() - 1) {
					sb.append(", ");
				}
			}
			
			CoupleSimpleValeur cp1 = new 
				CoupleSimpleValeur(champs.get(i).getNom(), 
						sb.toString());
			LigneSimpleParagraphe li = new LigneSimpleParagraphe(cp1);
			liste[i] = li;
		}
		
		// ajout du paragraphe
		StringBuffer titre = new StringBuffer();
		titre.append(table.getNom());
		titre.append(" (");
		titre.append(Labels.getLabel("blocImpression.annotation"));
		titre.append(")");
		Paragraphe par = new Paragraphe(
				titre.toString(),  
				liste, 
				null,
				null, null);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par);
	}
	
	public void createBlocAnnotation2ForObject(
			TKAnnotableObject obj, TableAnnotation table) {
		
		// on récupère tous les champs de la table
		List<ChampAnnotation> champs = ManagerLocator
			.getChampAnnotationManager()
			.findByTableManager(table);
		
		int size = Math.round((float) champs.size() / 2);
		LigneParagraphe[] liste = new LigneParagraphe[size];
		// pour chaque champ
		int cptCp = 0;
		int cptLi = 0;
		CoupleValeur cp1 = null;
		CoupleValeur cp2 = null;
		for (int i = 0; i < champs.size(); i++) {
			++cptCp;
			// on récupère toutes les valeurs pour le prélèvement
			List<AnnotationValeur> valeurs = ManagerLocator
				.getAnnotationValeurManager()
				.findByChampAndObjetManager(
						champs.get(i), obj);
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < valeurs.size(); j++) {
				sb.append(ObjectTypesFormatters
						.annotationValeurRenderer(valeurs.get(j)));
				
				if (j < valeurs.size() - 1) {
					sb.append(", ");
				}
			}
			
			if (cptCp == 1) {
				cp1 = new CoupleValeur(
						champs.get(i).getNom(), 
						sb.toString());
				if (i + 1 == champs.size()) {
					LigneParagraphe li = new LigneParagraphe("",
							new CoupleValeur[]{cp1});
					liste[cptLi] = li;
				}
			} else if (cptCp == 2) {
				cp2 = new CoupleValeur(
						champs.get(i).getNom(), 
						sb.toString());
				
				LigneParagraphe li = new LigneParagraphe("",
						new CoupleValeur[]{cp1, cp2});
				liste[cptLi] = li;
				cptCp = 0;
				cptLi++;
			}
			
		}
		
		// ajout du paragraphe
		StringBuffer titre = new StringBuffer();
		titre.append(table.getNom());
		titre.append(" (");
		titre.append(Labels.getLabel("blocImpression.annotation"));
		titre.append(")");
		Paragraphe par = new Paragraphe(
				titre.toString(),  
				liste, 
				null,
				null, null);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par);
	}
	
	/*****************************************************************/
	/*************     Impression des échantillons    ****************/
	/*****************************************************************/
	/**
	 * Crée la partie du document qui contiendra le paragraphe sur les
	 * infos principales de l'échantillon.
	 * @param echantillon Echantillon à imprimer.
	 */
	public void createBlocPrincipalEchantillon(Echantillon echantillon) {
		CoupleValeur cp1 = new CoupleValeur(
				Labels.getLabel("Champ.Echantillon.Code"), 
				echantillon.getCode());
		CoupleValeur cp2 = new CoupleValeur(
				Labels.getLabel("Champ.Echantillon.EchantillonType"), 
				echantillon.getEchantillonType().getType());
		LigneParagraphe li1 = new LigneParagraphe("li1",
				new CoupleValeur[]{cp1, cp2});
		Paragraphe par1 = new Paragraphe(
				Labels.getLabel("bloc.echantillon.principal"),  
				new LigneParagraphe[]{li1}, 
				null,
				null, null);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par1);
	}
	
	/**
	 * Crée la partie du document qui contiendra le paragraphe sur les
	 * infos du prélèvement de l'échantillon.
	 * @param echantillon Echantillon à imprimer.
	 */
	public void createBlocPrelevementForEchantillon(Echantillon echantillon) {
		Paragraphe par1 = null;
		Prelevement prlvt = ManagerLocator.getEchantillonManager()
			.getPrelevementManager(echantillon);
		if (prlvt != null) {
			CoupleValeur cp1 = new CoupleValeur(
					Labels.getLabel("Champ.Prelevement.Code"), 
					prlvt.getCode());
			CoupleValeur cp2 = new CoupleValeur(
					Labels.getLabel("Champ.Prelevement.Nature"), 
					prlvt.getNature().getNature());
			LigneParagraphe li1 = new LigneParagraphe("li1",
					new CoupleValeur[]{cp1, cp2});
			
			CoupleValeur cp3 = new CoupleValeur(
					Labels.getLabel("prelevement.patient"), 
					PrelevementUtils.getPatientNomAndPrenom(
							prlvt));
			cp3.setAnonyme(anonyme);
			LigneParagraphe li2 = new LigneParagraphe("li2",
					new CoupleValeur[]{cp3});
			
			par1 = new Paragraphe(
					Labels.getLabel("bloc.echantillon" 
						+ ".informations.prelevement"),  
					new LigneParagraphe[]{li1, li2}, 
					null,
					null, null);
		} else {
			par1 = new Paragraphe(
					Labels.getLabel("bloc.echantillon" 
						+ ".informations.prelevement"),  
					null, 
					null,
					Labels.getLabel("ficheEchantillon.prelevement.inconnu"), 
					null);
		}
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par1);
	}
	
	/**
	 * Crée la partie du document qui contiendra le paragraphe sur les
	 * infos de l'échantillon.
	 * @param echantillon Echantillon à imprimer.
	 */
	public void createBlocInfosForEchantillon(Echantillon echantillon) {
		EchantillonRowRenderer renderer = new EchantillonRowRenderer(false, false);
		// Quantité
		String tmp = "";
		CoupleValeur cpVide = new CoupleValeur("", "");
		tmp = renderer.getQuantite(echantillon);
		CoupleValeur cp1 = new CoupleValeur(
				Labels.getLabel("Champ.Echantillon.Quantite"), 
				tmp);
		LigneParagraphe li1 = new LigneParagraphe("",
				new CoupleValeur[]{cp1, cpVide});
		
		// Date de stockage
		if (echantillon.getDateStock() != null) {
			tmp = ObjectTypesFormatters
				.dateRenderer2(echantillon.getDateStock());
		} else {
			tmp = "-";
		}
		CoupleValeur cp2 = new CoupleValeur(
				Labels.getLabel("Champ.Echantillon.DateStock"), 
				tmp);
		// Délai Cgl
		CoupleValeur cp3 = new CoupleValeur(
				Labels.getLabel("Champ.Echantillon.DelaiCgl"), 
				renderer.getDelaiCgl(echantillon));
		LigneParagraphe li2 = new LigneParagraphe("",
				new CoupleValeur[]{cp2, cp3});
		
		// Opérateur
		if (echantillon.getCollaborateur() != null) {
			tmp = echantillon.getCollaborateur().getNomAndPrenom();
		} else {
			tmp = "-";
		}
		CoupleValeur cp4 = new CoupleValeur(
				Labels.getLabel("Champ.Echantillon.Collaborateur"), 
				tmp);
		LigneParagraphe li3 = new LigneParagraphe("",
				new CoupleValeur[]{cp4, cpVide});
		
		// Emplacement
		CoupleValeur cp5 = new CoupleValeur(
				Labels.getLabel("Champ.Echantillon.Emplacement"), 
				renderer.getEmplacementAdrl(echantillon));
		cp5.setAnonyme(anonyme);
		// Statut
		if (echantillon.getObjetStatut() != null) {
			tmp = ObjectTypesFormatters
					.ILNObjectStatut(echantillon.getObjetStatut());
		} else {
			tmp = "-";
		}
		CoupleValeur cp6 = new CoupleValeur(
				Labels.getLabel("Champ.Echantillon.ObjetStatut"), 
				tmp);
		LigneParagraphe li4 = new LigneParagraphe("",
				new CoupleValeur[]{cp6, cp5});
		
		// Qualité
		if (echantillon.getEchanQualite() != null) {
			tmp = echantillon.getEchanQualite().getEchanQualite();
		} else {
			tmp = "-";
		}
		CoupleValeur cp7 = new CoupleValeur(
				Labels.getLabel("Champ.Echantillon.EchanQualite"), 
				tmp);
		// Température
		Emplacement emp = ManagerLocator.getEchantillonManager()
			.getEmplacementManager(echantillon);
		StringBuffer temp = new StringBuffer();
		if (emp != null) {
			Conteneur cont = ManagerLocator.getEmplacementManager()
				.getConteneurManager(emp);
			if (cont != null && cont.getTemp() != null) {
				temp.append(cont.getTemp());
			}
		}
		temp.append("°C");
		CoupleValeur cpTemp = new CoupleValeur(
				Labels.getLabel("ficheEchantillon.temperatureLabel"), 
				temp.toString());
		LigneParagraphe li5 = new LigneParagraphe("",
				new CoupleValeur[]{cp7, cpTemp});
		
		// Mode prépa
		if (echantillon.getModePrepa() != null) {
			tmp = echantillon.getModePrepa().getNom();
		} else {
			tmp = "-";
		}
		CoupleValeur cp8 = new CoupleValeur(
				Labels.getLabel("Champ.Echantillon.ModePrepa"), 
				tmp);
		// Stérile
		if (echantillon.getSterile() != null) {
			tmp = ObjectTypesFormatters
				.booleanLitteralFormatter(echantillon.getSterile());
		} else {
			tmp = "-";
		}
		CoupleValeur cp9 = new CoupleValeur(
				Labels.getLabel("Champ.Echantillon.Sterile"), 
				tmp);
		LigneParagraphe li6 = new LigneParagraphe("",
				new CoupleValeur[]{cp8, cp9});
		
		// Conforme traitement
		if (echantillon.getConformeTraitement() != null) {
			StringBuffer sb = new StringBuffer();
			sb.append(ObjectTypesFormatters
				.booleanLitteralFormatter(
						echantillon.getConformeTraitement()));
			
			if (!echantillon.getConformeTraitement()) {
				sb.append(" - ");
				List<ObjetNonConforme> list = ManagerLocator
					.getObjetNonConformeManager()
					.findByObjetAndTypeManager(echantillon, "Traitement");
				
				if (list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						sb.append(list.get(i).getNonConformite()
								.getNom());
						if (i < list.size() - 1) {
							sb.append(", ");
						} else {
							sb.append(".");
						}
					}
				} else {
					sb.append(Labels.getLabel("nonConformite.raison.inconnue"));
				}
			}
			tmp = sb.toString();
		} else {
			tmp = "-";
		}
		CoupleValeur cp10 = new CoupleValeur(
				Labels.getLabel("Champ.Echantillon.ConformeTraitement"), 
				tmp);
		
		// Conforme cession
		if (echantillon.getConformeCession() != null) {
			StringBuffer sb = new StringBuffer();
			sb.append(ObjectTypesFormatters
				.booleanLitteralFormatter(
						echantillon.getConformeCession()));
			
			if (!echantillon.getConformeCession()) {
				sb.append(" - ");
				List<ObjetNonConforme> list = ManagerLocator
					.getObjetNonConformeManager()
					.findByObjetAndTypeManager(echantillon, "Cession");
				
				if (list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						sb.append(list.get(i).getNonConformite()
								.getNom());
						if (i < list.size() - 1) {
							sb.append(", ");
						} else {
							sb.append(".");
						}
					}
				} else {
					sb.append(Labels.getLabel("nonConformite.raison.inconnue"));
				}
			}
			tmp = sb.toString();
		} else {
			tmp = "-";
		}
		CoupleValeur cp11 = new CoupleValeur(
				Labels.getLabel("Champ.Echantillon.ConformeCession"), 
				tmp);
		
		LigneParagraphe li7 = new LigneParagraphe("",
				new CoupleValeur[]{cp10});
		LigneParagraphe li8 = new LigneParagraphe("",
				new CoupleValeur[]{cp11});
		
		Paragraphe par1 = new Paragraphe(
				Labels.getLabel("bloc.echantillon.informations.echantillon"),  
				new LigneParagraphe[]{li1, li6, li2, li3, li4, li5, li7, li8}, 
				null,
				null, null);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par1);
	}
	
	/**
	 * Crée la partie du document qui contiendra le paragraphe sur les
	 * infos complémentaires de l'échantillon.
	 * @param echantillon Echantillon à imprimer.
	 */
	public void createBlocComplementaireForEchantillon(
			Echantillon echantillon) {
		// EchantillonRowRenderer renderer = new EchantillonRowRenderer(false, false);
		// Tumoral
		String tmp = "";
		CoupleValeur cpVide = new CoupleValeur("", "");
		if (echantillon.getTumoral() != null) {
			tmp = ObjectTypesFormatters
			.booleanLitteralFormatter(echantillon.getTumoral());
		} else {
			tmp = "";
		}
		CoupleValeur cp1 = new CoupleValeur(
				Labels.getLabel("Champ.Echantillon.Tumoral"), 
				tmp);
		LigneParagraphe li1 = new LigneParagraphe("",
				new CoupleValeur[]{cp1, cpVide});
		
		// Organe
		StringBuffer sb = new StringBuffer();
		List<CodeAssigne> codes = ManagerLocator.getCodeAssigneManager()
			.findCodesOrganeByEchantillonManager(echantillon);
		Iterator<CodeAssigne> it = codes.iterator();
		CodeAssigne next;
		while (it.hasNext()) {
			next = it.next();
			sb.append(next.getCode());
			if (next.getLibelle() != null) {
				sb.append(" (");
				sb.append(next.getLibelle());
				sb.append(")");
			}
			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		CoupleValeur cp2 = new CoupleValeur(
				Labels.getLabel("Champ.Echantillon.AdicapOrgane"), 
				sb.toString());
		LigneDeuxColonnesParagraphe li2 = new LigneDeuxColonnesParagraphe(cp2);
		
		// Lateralité
		if (echantillon.getLateralite() != null) {
			tmp = Labels.getLabel("echantillon.lateralite." 
					+ echantillon.getLateralite());
		} else {
			tmp = "-";
		}
		CoupleValeur cp3 = new CoupleValeur(
				Labels.getLabel("Champ.Echantillon.Lateralite"), 
				tmp);
		LigneParagraphe li3 = new LigneParagraphe("",
				new CoupleValeur[]{cp3, cpVide});
		
		// Codes lésionnels
		sb = new StringBuffer();
		codes = ManagerLocator.getCodeAssigneManager()
			.findCodesMorphoByEchantillonManager(echantillon);
		it = codes.iterator();
		while (it.hasNext()) {
			next = it.next();
			sb.append(next.getCode());
			if (next.getLibelle() != null) {
				sb.append(" (");
				sb.append(next.getLibelle());
				sb.append(")");
			}
			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		CoupleValeur cp4 = new CoupleValeur(
				Labels.getLabel("Champ.Echantillon.CodeLes"), 
				sb.toString());
		LigneDeuxColonnesParagraphe li4 = new LigneDeuxColonnesParagraphe(cp4);
		
		Paragraphe par1 = new Paragraphe(
				Labels.getLabel("bloc.echantillon" 
					+ ".informations.complementaires"),  
				new Object[]{li1, li2, li3, li4}, 
				null,
				null, null);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par1);
	}
	
	/*****************************************************************/
	/*************   Impression des produits dérivés    **************/
	/*****************************************************************/
	/**
	 * Crée la partie du document qui contiendra le paragraphe sur les
	 * infos principales du produit dérivé.
	 * @param derive ProdDerive à imprimer.
	 */
	public void createBlocPrincipalProdDerive(ProdDerive derive) {
		CoupleValeur cp1 = new CoupleValeur(
				Labels.getLabel("Champ.ProdDerive.Code"), 
				derive.getCode());
		CoupleValeur cp2 = new CoupleValeur(
				Labels.getLabel("Champ.ProdDerive.ProdType"), 
				derive.getProdType().getType());
		LigneParagraphe li1 = new LigneParagraphe("li1",
				new CoupleValeur[]{cp1, cp2});
		Paragraphe par1 = new Paragraphe(
				Labels.getLabel("bloc.prodDerive.principal"),  
				new LigneParagraphe[]{li1}, 
				null,
				null, null);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par1);
	}
	
	/**
	 * Crée la partie du document qui contiendra le paragraphe sur le
	 * parent du dérivé.
	 * @param derive ProdDerive à imprimer.
	 */
	public void createBlocParentForProdDerive(ProdDerive derive) {
		ProdDeriveRowRenderer renderer = new ProdDeriveRowRenderer(false, false);
		
		Paragraphe par1 = null;
		CoupleValeur cpVide = new CoupleValeur("", "");
		if (derive.getTransformation() != null) {
			LigneParagraphe li1 = null;
			LigneParagraphe li2 = null;
			LigneParagraphe li3 = null;
			String typeParent = renderer.getTypeParent(derive);
			String keyTitle = "";
			String tmp = "";
			
			// si le parent est un prelevement
			if (typeParent.equals("Prelevement")) {
				Prelevement parentPrlvt = (Prelevement) 
					ManagerLocator.getEntiteManager()
					.findObjectByEntiteAndIdManager(
						derive.getTransformation().getEntite(),
						derive.getTransformation().getObjetId());
				keyTitle = "ficheProdDerive.prelevement.titre";
				
				// code
				CoupleValeur cp1 = new CoupleValeur(
						Labels.getLabel("Champ.Prelevement.Code"), 
						parentPrlvt.getCode());
				// type
				CoupleValeur cp2 = new CoupleValeur(
						Labels.getLabel("Champ.Prelevement.Nature"), 
						parentPrlvt.getNature().getNature());
				li1 = new LigneParagraphe("",
					new CoupleValeur[]{cp1, cp2});
				
				// date
				if (parentPrlvt.getDatePrelevement() != null) {
					tmp = ObjectTypesFormatters
						.dateRenderer2(parentPrlvt.getDatePrelevement());
				} else {
					tmp = "";
				}
				CoupleValeur cp3 = new CoupleValeur(
						Labels.getLabel("Champ.Prelevement.DatePrelevement"), 
						tmp);
				// operateur
				if (parentPrlvt.getPreleveur() != null) {
					tmp = parentPrlvt.getPreleveur().getNomAndPrenom();
				} else {
					tmp = "";
				}
				CoupleValeur cp4 = new CoupleValeur(
						Labels.getLabel("Champ.Prelevement.Preleveur"), 
						tmp);
				li2 = new LigneParagraphe("",
					new CoupleValeur[]{cp3, cp4});
				
				// quantite
				StringBuffer sb = new StringBuffer();
				if (parentPrlvt.getQuantite() != null) {
					sb.append(parentPrlvt.getQuantite());
				} else {
					sb.append("-");
				}
				if (parentPrlvt.getQuantiteUnite() != null) {
					sb.append(parentPrlvt.getQuantiteUnite().getUnite());
				} else {
					sb.append("-");
				}
				CoupleValeur cp5 = new CoupleValeur(
						Labels.getLabel("Champ.Prelevement.Quantite"), 
						sb.toString());
				li3 = new LigneParagraphe("",
					new CoupleValeur[]{cp5, cpVide});
				
			} else if (typeParent.equals("Echantillon")) {
				// si le parent est un échantillon
				Echantillon parentEchan = (Echantillon) 
				ManagerLocator.getEntiteManager()
				.findObjectByEntiteAndIdManager(
					derive.getTransformation().getEntite(),
					derive.getTransformation().getObjetId());
				keyTitle = "ficheProdDerive.echantillon.titre";
				
				// code
				CoupleValeur cp1 = new CoupleValeur(
						Labels.getLabel("Champ.Echantillon.Code"), 
						parentEchan.getCode());
				// type
				CoupleValeur cp2 = new CoupleValeur(
						Labels.getLabel("Champ.Echantillon.EchantillonType"), 
						parentEchan.getEchantillonType().getType());
				li1 = new LigneParagraphe("",
					new CoupleValeur[]{cp1, cp2});
				
				// emplacement
				tmp = ManagerLocator.getEchantillonManager()
						.getEmplacementAdrlManager(parentEchan);
				CoupleValeur cp3 = new CoupleValeur(
						Labels.getLabel("Champ.Echantillon.Emplacement"), 
						tmp);
				cp3.setAnonyme(anonyme);
				// Statut
				if (parentEchan.getObjetStatut() != null) {
					tmp = ObjectTypesFormatters
							.ILNObjectStatut(parentEchan.getObjetStatut());
				} else {
					tmp = "";
				}
				CoupleValeur cp4 = new CoupleValeur(
						Labels.getLabel("Champ.Echantillon.ObjetStatut"), 
						tmp);
				li2 = new LigneParagraphe("",
					new CoupleValeur[]{cp3, cp4});
				
				// code lésionnel
				StringBuffer sb = new StringBuffer();
				List<CodeAssigne> codes = ManagerLocator
					.getCodeAssigneManager()
					.findCodesMorphoByEchantillonManager(parentEchan);
				Iterator<CodeAssigne> it = codes.iterator();
				while (it.hasNext()) {
					CodeAssigne next = it.next();
					sb.append(next.getCode());
					if (next.getLibelle() != null) {
						sb.append(" (");
						sb.append(next.getLibelle());
						sb.append(")");
					}
					if (it.hasNext()) {
						sb.append(", ");
					}
				}
				CoupleValeur cp5 = new CoupleValeur(
						Labels.getLabel("Champ.Echantillon.CodeLes"), 
						sb.toString());
				li3 = new LigneParagraphe("",
					new CoupleValeur[]{cp5, cpVide});
				
			} else if (typeParent.equals("ProdDerive")) {
				ProdDerive parentDerive = (ProdDerive) 
				ManagerLocator.getEntiteManager()
				.findObjectByEntiteAndIdManager(
					derive.getTransformation().getEntite(),
					derive.getTransformation().getObjetId());
				keyTitle = "ficheProdDerive.prodderive.titre";
				
				// code
				CoupleValeur cp1 = new CoupleValeur(
						Labels.getLabel("Champ.ProdDerive.Code"), 
						parentDerive.getCode());
				// type
				CoupleValeur cp2 = new CoupleValeur(
						Labels.getLabel("Champ.ProdDerive.ProdType"), 
						parentDerive.getProdType().getType());
				li1 = new LigneParagraphe("",
					new CoupleValeur[]{cp1, cp2});
				
				// emplacement
				tmp = ManagerLocator.getProdDeriveManager()
					.getEmplacementAdrlManager(parentDerive);
				CoupleValeur cp3 = new CoupleValeur(
						Labels.getLabel("Champ.ProdDerive.Emplacement"), 
						tmp);
				// statut
				if (parentDerive.getObjetStatut() != null) {
					tmp = ObjectTypesFormatters
							.ILNObjectStatut(parentDerive.getObjetStatut());
				} else {
					tmp = "";
				}
				CoupleValeur cp4 = new CoupleValeur(
						Labels.getLabel("Champ.ProdDerive.ObjetStatut"), 
						tmp);
				li2 = new LigneParagraphe("",
					new CoupleValeur[]{cp3, cp4});
				
				// quantite
				StringBuffer sb = new StringBuffer();
				if (parentDerive.getQuantite() != null) {
					sb.append(parentDerive.getQuantite());
				} else {
					sb.append("-");
				}
				if (parentDerive.getQuantiteUnite() != null) {
					sb.append(parentDerive.getQuantiteUnite().getUnite());
				} else {
					sb.append("-");
				}
				CoupleValeur cp5 = new CoupleValeur(
						Labels.getLabel("Champ.ProdDerive.Quantite"), 
						sb.toString());
				li3 = new LigneParagraphe("",
					new CoupleValeur[]{cp5, cpVide});
			}
			
			// transformation
			// date
			if (derive.getDateTransformation() != null) {
				tmp = ObjectTypesFormatters
						.dateRenderer2(derive.getDateTransformation());
			} else {
				tmp = "";
			}
			CoupleValeur cp1 = new CoupleValeur(
					Labels.getLabel("Champ.ProdDerive.DateTransformation"), 
					tmp);
			
			// quantite
			StringBuffer sb = new StringBuffer();
			if (derive.getTransformation().getQuantite() != null) {
				sb.append(derive.getTransformation().getQuantite());
			} else {
				sb.append("-");
			}
			if (derive.getTransformation().getQuantiteUnite() != null) {
				sb.append(derive.getTransformation()
						.getQuantiteUnite().getUnite());
			} else {
				sb.append("-");
			}
			CoupleValeur cp2 = new CoupleValeur(
					Labels.getLabel("ficheProdDerive.transformation.quantité"), 
					sb.toString());
			LigneParagraphe li4 = new LigneParagraphe("",
					new CoupleValeur[]{cp1, cp2});
			
			// patient
			Prelevement prlvt = null;
			if (derive.getProdDeriveId() != null) {
				prlvt = ManagerLocator.getProdDeriveManager()
					.getPrelevementParent(derive);
			}
			if (prlvt != null) {
				tmp =  PrelevementUtils.getPatientNomAndPrenom(prlvt);
			} else {
				tmp = "-";
			}
			CoupleValeur cp3 = new CoupleValeur(
					Labels.getLabel("prelevement.patient"), 
					tmp);
			cp3.setAnonyme(anonyme);
			LigneParagraphe li5 = new LigneParagraphe("",
				new CoupleValeur[]{cp3, cpVide});
			
			SousParagraphe sousPar1 = new SousParagraphe(
					Labels.getLabel("ficheProdDerive.transformation.title"), 
					new LigneParagraphe[]{li4, li5},
					null, null);
			
			par1 = new Paragraphe(
					Labels.getLabel(keyTitle),  
					new LigneParagraphe[]{li1, li2, li3}, 
					new SousParagraphe[]{sousPar1},
					null, 
					null);
			
		} else {
			par1 = new Paragraphe(
					Labels.getLabel(
						"bloc.prodDerive.parent"),  
					null, 
					null,
					Labels.getLabel("ficheProdDerive.transformation.inconnue"), 
					null);
		}
		
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par1);
	}
	
	/**
	 * Crée la partie du document qui contiendra le paragraphe sur les
	 * infos complémentaires du dérivé.
	 * @param derive ProdDerive à imprimer.
	 */
	public void createBlocComplementaireForProdDerive(
			ProdDerive derive) {
		ProdDeriveRowRenderer renderer = new ProdDeriveRowRenderer(false, false);
		// N° labo
		String tmp = "";
		CoupleValeur cpVide = new CoupleValeur("", "");
		if (derive.getCodeLabo() != null) {
			tmp = derive.getCodeLabo();
		} else {
			tmp = "";
		}
		CoupleValeur cp1 = new CoupleValeur(
				Labels.getLabel("Champ.ProdDerive.CodeLabo"), 
				tmp);
		LigneParagraphe li1 = new LigneParagraphe("",
				new CoupleValeur[]{cp1, cpVide});
		
		// Volume
		CoupleValeur cp2 = new CoupleValeur(
				Labels.getLabel("ficheProdDerive.volumeLabel"), 
				renderer.getVolume(derive));
		// Concentration
		CoupleValeur cp3 = new CoupleValeur(
				Labels.getLabel("Champ.ProdDerive.Conc"), 
				renderer.getConcentration(derive));
		LigneParagraphe li2 = new LigneParagraphe("",
				new CoupleValeur[]{cp2, cp3});
		
		// Quantite
		CoupleValeur cp4 = new CoupleValeur(
				Labels.getLabel("ficheProdDerive.quantiteLabel"), 
				renderer.getQuantite(derive));
		LigneParagraphe li3 = new LigneParagraphe("",
				new CoupleValeur[]{cp4, cpVide});
		
		// date stockage
		if (derive.getDateStock() != null) {
			tmp = ObjectTypesFormatters.dateRenderer2(derive.getDateStock());
		} else {
			tmp = "";
		}
		CoupleValeur cp5 = new CoupleValeur(
				Labels.getLabel("Champ.ProdDerive.DateStock"), 
				tmp);
		// qualite
		if (derive.getProdQualite() != null) {
			tmp = derive.getProdQualite().getProdQualite();
		} else {
			tmp = "";
		}
		CoupleValeur cp6 = new CoupleValeur(
				Labels.getLabel("Champ.ProdDerive.ProdQualite"), 
				tmp);
		LigneParagraphe li4 = new LigneParagraphe("",
				new CoupleValeur[]{cp5, cp6});
		
		// Opérateur
		if (derive.getCollaborateur() != null) {
			tmp = derive.getCollaborateur().getNomAndPrenom();
		} else {
			tmp = "";
		}
		CoupleValeur cp7 = new CoupleValeur(
				Labels.getLabel("Champ.ProdDerive.Collaborateur"), 
				tmp);
		LigneParagraphe li5 = new LigneParagraphe("",
				new CoupleValeur[]{cp7, cpVide});
		
		// emplacement
		CoupleValeur cp8 = new CoupleValeur(
				Labels.getLabel("Champ.ProdDerive.Emplacement"), 
				renderer.getEmplacementAdrl(derive));
		cp8.setAnonyme(anonyme);
		// statut
		if (derive.getObjetStatut() != null) {
			tmp = ObjectTypesFormatters
					.ILNObjectStatut(derive.getObjetStatut());
		} else {
			tmp = "";
		}
		CoupleValeur cp9 = new CoupleValeur(
				Labels.getLabel("Champ.ProdDerive.ObjetStatut"), 
				tmp);
		LigneParagraphe li6 = new LigneParagraphe("",
				new CoupleValeur[]{cp8, cp9});
		
		// température
		Emplacement emp = ManagerLocator.getProdDeriveManager()
			.getEmplacementManager(derive);
		StringBuffer temp = new StringBuffer();
		if (emp != null) {
			Conteneur cont = ManagerLocator.getEmplacementManager()
				.getConteneurManager(emp);
			if (cont != null && cont.getTemp() != null) {
				temp.append(cont.getTemp());
			}
		}
		temp.append("°C");
		CoupleValeur cp10 = new CoupleValeur(
				Labels.getLabel("ficheProdDerive.temperatureLabel"), 
				temp.toString());
		// Préparation
		if (derive.getModePrepaDerive() != null) {
			tmp = derive.getModePrepaDerive().getNom();
		} else {
			tmp = "";
		}
		CoupleValeur cp11 = new CoupleValeur(
				Labels.getLabel("Champ.ProdDerive.ModePrepaDerive"), 
				tmp);
		LigneParagraphe li7 = new LigneParagraphe("",
				new CoupleValeur[]{cp11, cp10});
		
		// Conforme traitement
		if (derive.getConformeTraitement() != null) {
			StringBuffer sb = new StringBuffer();
			sb.append(ObjectTypesFormatters
				.booleanLitteralFormatter(
						derive.getConformeTraitement()));
			
			if (!derive.getConformeTraitement()) {
				sb.append(" - ");
				List<ObjetNonConforme> list = ManagerLocator
					.getObjetNonConformeManager()
					.findByObjetAndTypeManager(derive, "Traitement");
				
				if (list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						sb.append(list.get(i).getNonConformite()
								.getNom());
						if (i < list.size() - 1) {
							sb.append(", ");
						} else {
							sb.append(".");
						}
					}
				} else {
					sb.append(Labels.getLabel("nonConformite.raison.inconnue"));
				}
			}
			tmp = sb.toString();
		} else {
			tmp = "-";
		}
		CoupleValeur cp12 = new CoupleValeur(
				Labels.getLabel("Champ.ProdDerive.ConformeTraitement"), 
				tmp);
		
		// Conforme cession
		if (derive.getConformeCession() != null) {
			StringBuffer sb = new StringBuffer();
			sb.append(ObjectTypesFormatters
				.booleanLitteralFormatter(
						derive.getConformeCession()));
			
			if (!derive.getConformeCession()) {
				sb.append(" - ");
				List<ObjetNonConforme> list = ManagerLocator
					.getObjetNonConformeManager()
					.findByObjetAndTypeManager(derive, "Cession");
				
				if (list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						sb.append(list.get(i).getNonConformite()
								.getNom());
						if (i < list.size() - 1) {
							sb.append(", ");
						} else {
							sb.append(".");
						}
					}
				} else {
					sb.append(Labels.getLabel("nonConformite.raison.inconnue"));
				}
			}
			tmp = sb.toString();
		} else {
			tmp = "-";
		}
		CoupleValeur cp13 = new CoupleValeur(
				Labels.getLabel("Champ.ProdDerive.ConformeCession"), 
				tmp);
		
		LigneParagraphe li8 = new LigneParagraphe("",
				new CoupleValeur[]{cp12});
		LigneParagraphe li9 = new LigneParagraphe("",
				new CoupleValeur[]{cp13});
		
		Paragraphe par1 = new Paragraphe(
				Labels.getLabel(
					"bloc.prodDerive.informations.complementaires"),  
				new LigneParagraphe[]{li1, li2, li3, li4, li5, li6, 
					li7, li8, li9}, 
				null,
				null, null);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par1);
	}
	
	/*****************************************************************/
	/*************       Impression des cessions      ****************/
	/*****************************************************************/
	/**
	 * Crée la partie du document qui contiendra le paragraphe sur les
	 * infos principales de la cession.
	 * @param cession Cession à imprimer.
	 */
	public void createBlocPrincipalCession(Cession cession) {
		StringBuffer sb = new StringBuffer(cession.getNumero());
		CoupleValeur cp1 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Numero"), 
				sb.toString());
		CoupleValeur cp2 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.CessionType"), 
				cession.getCessionType().getType());
		LigneParagraphe li1 = new LigneParagraphe("li1",
				new CoupleValeur[]{cp1, cp2});
		Paragraphe par1 = new Paragraphe(
				Labels.getLabel("bloc.cession.principal"),  
				new LigneParagraphe[]{li1}, 
				null,
				null, null);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par1);
	}
	
	/**
	 * Crée la partie du document qui contiendra le paragraphe sur les
	 * infos complémentaires d'une cession.
	 * @param cession Cession à imprimer.
	 */
	public void createBlocInformationsCession(Cession cession) {
		Paragraphe p1 = null;
		
		if (cession.getCessionType().getType().toUpperCase()
				.equals("RECHERCHE")) {
			p1 = createParagrapheRecherche(cession);
		} else if (cession.getCessionType().getType().toUpperCase()
				.equals("SANITAIRE")) {
			p1 = createParagrapheSanitaire(cession);
		} else if (cession.getCessionType().getType().toUpperCase()
				.equals("DESTRUCTION")) {
			p1 = createParagrapheDestruction(cession);
		}
		
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, p1);
	}
	
	/**
	 * Remplie les infos d'une cession si elle est de type RECHERCHE.
	 * @param cession Cession a imprimer.
	 * @return Paragraphe contenant les données de la cession.
	 */
	public Paragraphe createParagrapheRecherche(Cession cession) {
		
		// Demandeur
		String tmp = "";
		CoupleValeur cpVide = new CoupleValeur("", "");
		if (cession.getDemandeur() != null) {
			tmp = cession.getDemandeur().getNomAndPrenom();
		} else {
			tmp = "";
		}
		CoupleValeur cp1 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Demandeur"), 
				tmp);
		// Date demande
		if (cession.getDemandeDate() != null) {
			tmp = ObjectTypesFormatters.dateRenderer2(cession.getDemandeDate());
		} else {
			tmp = "";
		}
		CoupleValeur cp2 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.DemandeDate"), 
				tmp);
		LigneParagraphe li1 = new LigneParagraphe("",
				new CoupleValeur[]{cp1, cp2});
		
		// Contrat
		if (cession.getContrat() != null) {
			tmp = cession.getContrat().getNumero();
		} else {
			tmp = "";
		}
		CoupleValeur cp3 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Contrat"), 
				tmp);
		LigneParagraphe li2 = new LigneParagraphe("",
				new CoupleValeur[]{cp3, cpVide});
		
		// Titre de l'étude
		if (cession.getEtudeTitre() != null) {
			tmp = cession.getEtudeTitre();
		} else {
			tmp = "";
		}
		CoupleValeur cp4 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.EtudeTitre"), 
				tmp);
		LigneParagraphe li3 = new LigneParagraphe("",
				new CoupleValeur[]{cp4, cpVide});
		
		// Description
		if (cession.getDescription() != null) {
			tmp = cession.getDescription();
		} else {
			tmp = "";
		}
		CoupleValeur cp5 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Description"), 
				tmp);
		LigneDeuxColonnesParagraphe li4 = new LigneDeuxColonnesParagraphe(cp5);
		
		// Destinataire
		if (cession.getDestinataire() != null) {
			tmp = cession.getDestinataire().getNomAndPrenom();
		} else {
			tmp = "";
		}
		CoupleValeur cp6 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Destinataire"), 
				tmp);
		LigneParagraphe li5 = new LigneParagraphe("",
				new CoupleValeur[]{cp6, cpVide});
		
		// Service destinataire
		if (cession.getServiceDest() != null) {
			tmp = cession.getServiceDest().getNom();
		} else {
			tmp = "";
		}
		CoupleValeur cp7 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.ServiceDest"), 
				tmp);
		LigneParagraphe li6 = new LigneParagraphe("",
				new CoupleValeur[]{cp7, cpVide});
		
		// date validation
		if (cession.getValidationDate() != null) {
			tmp = ObjectTypesFormatters
					.dateRenderer2(cession.getValidationDate());
		} else {
			tmp = "";
		}
		CoupleValeur cp8 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.ValidationDate"), 
				tmp);
		// statut
		if (cession.getCessionStatut() != null) {
			tmp = ObjectTypesFormatters
					.ILNObjectStatut(cession.getCessionStatut());
		} else {
			tmp = "";
		}
		CoupleValeur cp9 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.CessionStatut"), 
				tmp);
		LigneParagraphe li7 = new LigneParagraphe("",
				new CoupleValeur[]{cp8, cp9});
		SousParagraphe sousPar1 = new SousParagraphe(
				Labels.getLabel("Champ.Cession.CessionStatut"), 
				new LigneParagraphe[]{li7},
				null, null);
		
		// Opérateur
		if (cession.getExecutant() != null) {
			tmp = cession.getExecutant().getNomAndPrenom();
		} else {
			tmp = "";
		}
		CoupleValeur cp10 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Executant"), 
				tmp);
		LigneParagraphe li8 = new LigneParagraphe("",
				new CoupleValeur[]{cp10, cpVide});
		
		// date de départ
		if (cession.getDepartDate() != null) {
			tmp = ObjectTypesFormatters
					.dateRenderer2(cession.getDepartDate());
		} else {
			tmp = "";
		}
		CoupleValeur cp12 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.DepartDate"), 
				tmp);
		// date d'arrivée
		if (cession.getArriveeDate() != null) {
			tmp = ObjectTypesFormatters
					.dateRenderer2(cession.getArriveeDate());
		} else {
			tmp = "";
		}
		CoupleValeur cp13 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.ArriveeDate"), 
				tmp);
		LigneParagraphe li9 = new LigneParagraphe("",
				new CoupleValeur[]{cp12, cp13});
		
		// transporteur
		if (cession.getTransporteur() != null) {
			tmp = cession.getTransporteur().getNom();
		} else {
			tmp = "";
		}
		CoupleValeur cp14 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Transporteur"), 
				tmp);
		// température
		if (cession.getTemperature() != null) {
			tmp = cession.getTemperature() + " °C";
		} else {
			tmp = "";
		}
		CoupleValeur cp15 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Temperature"), 
				tmp);
		LigneParagraphe li10 = new LigneParagraphe("",
				new CoupleValeur[]{cp14, cp15});
		
		// obserations
		if (cession.getObservations() != null) {
			tmp = cession.getObservations();
		} else {
			tmp = "";
		}
		CoupleValeur cp16 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Observations"), 
				tmp);
		LigneDeuxColonnesParagraphe li11 = 
			new LigneDeuxColonnesParagraphe(cp16);
		
		SousParagraphe sousPar2 = new SousParagraphe(
				Labels.getLabel("Champ.Cession.Observations"), 
				new Object[]{li8, li9, li10, li11},
				null, null);
		
		Paragraphe par = new Paragraphe(
				Labels.getLabel("bloc.cession.informations.cession"),  
				new Object[]{li1, li2, li3, li4, li5, li6}, 
				new SousParagraphe[]{sousPar1, sousPar2},
				null, null);
		
		return par;
	}
	
	/**
	 * Remplie les infos d'une cession si elle est de type SANITAIRE.
	 * @param cession Cession a imprimer.
	 * @return Paragraphe contenant les données de la cession.
	 */
	public Paragraphe createParagrapheSanitaire(Cession cession) {
		
		// Demandeur
		String tmp = "";
		CoupleValeur cpVide = new CoupleValeur("", "");
		if (cession.getDemandeur() != null) {
			tmp = cession.getDemandeur().getNomAndPrenom();
		} else {
			tmp = "";
		}
		CoupleValeur cp1 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Demandeur"), 
				tmp);
		// Date demande
		if (cession.getDemandeDate() != null) {
			tmp = ObjectTypesFormatters
					.dateRenderer2(cession.getDemandeDate());
		} else {
			tmp = "";
		}
		CoupleValeur cp2 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.DemandeDate"), 
				tmp);
		LigneParagraphe li1 = new LigneParagraphe("",
				new CoupleValeur[]{cp1, cp2});
		
		// Examen
		if (cession.getCessionExamen() != null) {
			tmp = cession.getCessionExamen().getExamen();
		} else {
			tmp = "";
		}
		CoupleValeur cp3 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.CessionExamen"), 
				tmp);
		LigneParagraphe li2 = new LigneParagraphe("",
				new CoupleValeur[]{cp3, cpVide});
		
		// Description
		if (cession.getDescription() != null) {
			tmp = cession.getDescription();
		} else {
			tmp = "";
		}
		CoupleValeur cp5 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Description"), 
				tmp);
		LigneDeuxColonnesParagraphe li4 = new LigneDeuxColonnesParagraphe(cp5);
		
		// Destinataire
		if (cession.getDestinataire() != null) {
			tmp = cession.getDestinataire().getNomAndPrenom();
		} else {
			tmp = "";
		}
		CoupleValeur cp6 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Destinataire"), 
				tmp);
		LigneParagraphe li5 = new LigneParagraphe("",
				new CoupleValeur[]{cp6, cpVide});
		
		// Service destinataire
		if (cession.getServiceDest() != null) {
			tmp = cession.getServiceDest().getNom();
		} else {
			tmp = "";
		}
		CoupleValeur cp7 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.ServiceDest"), 
				tmp);
		LigneParagraphe li6 = new LigneParagraphe("",
				new CoupleValeur[]{cp7, cpVide});
		
		// date validation
		if (cession.getValidationDate() != null) {
			tmp = ObjectTypesFormatters
						.dateRenderer2(cession.getValidationDate());
		} else {
			tmp = "";
		}
		CoupleValeur cp8 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.ValidationDate"), 
				tmp);
		// statut
		if (cession.getCessionStatut() != null) {
			tmp = ObjectTypesFormatters
					.ILNObjectStatut(cession.getCessionStatut());
		} else {
			tmp = "";
		}
		CoupleValeur cp9 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.CessionStatut"), 
				tmp);
		LigneParagraphe li7 = new LigneParagraphe("",
				new CoupleValeur[]{cp8, cp9});
		SousParagraphe sousPar1 = new SousParagraphe(
				Labels.getLabel("Champ.Cession.CessionStatut"), 
				new LigneParagraphe[]{li7},
				null, null);
		
		// Opérateur
		if (cession.getExecutant() != null) {
			tmp = cession.getExecutant().getNomAndPrenom();
		} else {
			tmp = "";
		}
		CoupleValeur cp10 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Executant"), 
				tmp);
		LigneParagraphe li8 = new LigneParagraphe("",
				new CoupleValeur[]{cp10, cpVide});
		
		// date de départ
		if (cession.getDepartDate() != null) {
			tmp = ObjectTypesFormatters
							.dateRenderer2(cession.getDepartDate());
		} else {
			tmp = "";
		}
		CoupleValeur cp12 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.DepartDate"), 
				tmp);
		// date d'arrivée
		if (cession.getArriveeDate() != null) {
			tmp = ObjectTypesFormatters
							.dateRenderer2(cession.getArriveeDate());
		} else {
			tmp = "";
		}
		CoupleValeur cp13 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.ArriveeDate"), 
				tmp);
		LigneParagraphe li9 = new LigneParagraphe("",
				new CoupleValeur[]{cp12, cp13});
		
		// transporteur
		if (cession.getTransporteur() != null) {
			tmp = cession.getTransporteur().getNom();
		} else {
			tmp = "";
		}
		CoupleValeur cp14 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Transporteur"), 
				tmp);
		// température
		if (cession.getTemperature() != null) {
			tmp = cession.getTemperature() + " °C";
		} else {
			tmp = "";
		}
		CoupleValeur cp15 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Temperature"), 
				tmp);
		LigneParagraphe li10 = new LigneParagraphe("",
				new CoupleValeur[]{cp14, cp15});
		
		// obserations
		if (cession.getObservations() != null) {
			tmp = cession.getObservations();
		} else {
			tmp = "";
		}
		CoupleValeur cp16 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Observations"), 
				tmp);
		LigneDeuxColonnesParagraphe li11 = 
			new LigneDeuxColonnesParagraphe(cp16);
		
		SousParagraphe sousPar2 = new SousParagraphe(
				Labels.getLabel("Champ.Cession.Observations"), 
				new Object[]{li8, li9, li10, li11},
				null, null);
		
		Paragraphe par = new Paragraphe(
				Labels.getLabel("bloc.cession.informations.cession"),  
				new Object[]{li1, li2, li4, li5, li6}, 
				new SousParagraphe[]{sousPar1, sousPar2},
				null, null);
		
		return par;
	}
	
	/**
	 * Remplie les infos d'une cession si elle est de type DESTRUCTION.
	 * @param cession Cession a imprimer.
	 * @return Paragraphe contenant les données de la cession.
	 */
	public Paragraphe createParagrapheDestruction(Cession cession) {
		// Demandeur
		String tmp = "";
		CoupleValeur cpVide = new CoupleValeur("", "");
		if (cession.getDemandeur() != null) {
			tmp = cession.getDemandeur().getNomAndPrenom();
		} else {
			tmp = "";
		}
		CoupleValeur cp1 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Demandeur"), 
				tmp);
		// Date demande
		if (cession.getDemandeDate() != null) {
			tmp = ObjectTypesFormatters
						.dateRenderer2(cession.getDemandeDate());
		} else {
			tmp = "";
		}
		CoupleValeur cp2 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.DemandeDate"), 
				tmp);
		LigneParagraphe li1 = new LigneParagraphe("",
				new CoupleValeur[]{cp1, cp2});
		
		// DestructionMotif
		if (cession.getDestructionMotif() != null) {
			tmp = cession.getDestructionMotif().getMotif();
		} else {
			tmp = "";
		}
		CoupleValeur cp3 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.DestructionMotif"), 
				tmp);
		// DestructionDate
		if (cession.getDestructionDate() != null) {
			tmp = ObjectTypesFormatters
					.dateRenderer2(cession.getDestructionDate());
		} else {
			tmp = "";
		}
		CoupleValeur cp4 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.DestructionDate"), 
				tmp);
		LigneParagraphe li2 = new LigneParagraphe("",
				new CoupleValeur[]{cp3, cp4});
		
		// CessionStatut
		if (cession.getCessionStatut() != null) {
			tmp = ObjectTypesFormatters
					.ILNObjectStatut(cession.getCessionStatut());
		} else {
			tmp = "";
		}
		CoupleValeur cp5 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.CessionStatut"), 
				tmp);
		LigneParagraphe li3 = new LigneParagraphe("",
				new CoupleValeur[]{cp5, cpVide});
		
		// Executant
		if (cession.getExecutant() != null) {
			tmp = cession.getExecutant().getNomAndPrenom();
		} else {
			tmp = "";
		}
		CoupleValeur cp6 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Executant"), 
				tmp);
		LigneParagraphe li4 = new LigneParagraphe("",
				new CoupleValeur[]{cp6, cpVide});
		
		// Observations
		if (cession.getObservations() != null) {
			tmp = cession.getObservations();
		} else {
			tmp = "";
		}
		CoupleValeur cp7 = new CoupleValeur(
				Labels.getLabel("Champ.Cession.Observations"), 
				tmp);
		LigneDeuxColonnesParagraphe li5 = 
			new LigneDeuxColonnesParagraphe(cp7);
				
		Paragraphe par = new Paragraphe(
				Labels.getLabel("bloc.cession.informations.cession"),  
				new Object[]{li1, li2, li3, li4, li5}, 
				null,
				null, null);
		
		return par;
	}
	
	/*****************************************************************/
	/*************     Impression des listes          ****************/
	/*****************************************************************/
	/**
	 * Crée le bloc contenant une liste d'objets cédés.
	 * @param cessions Cessions à imprimer.
	 * @param champs Colonnes à imprimer.
	 */
	public void createBlocListeCederObjets(List<CederObjetDecorator> cedes,
			List<ChampEntite> champs, boolean areEchantillons) {
		// Entete
		String[] listeEntete = new String[champs.size()];
		for (int i = 0; i < champs.size(); i++) {
			StringBuffer iProperty = new StringBuffer();
			iProperty.append("Champ.");
			iProperty.append(champs.get(i).getEntite().getNom());
			iProperty.append(".");
			
			String champOk = "";
			// si le nom du champ finit par "Id", on le retire
			if (champs.get(i).getNom().endsWith("Id")) {
				champOk = champs.get(i).getNom().substring(
						0, champs.get(i).getNom().length() - 2);
			} else {
				champOk = champs.get(i).getNom();
			}
			iProperty.append(champOk);
			listeEntete[i] = Labels.getLabel(iProperty.toString());
		}
		EnteteListe entetes = new EnteteListe(listeEntete);
		
		// liste des cédés
		LigneListe[] liste = new LigneListe[cedes.size()];
		for (int i = 0; i < cedes.size(); i++) {
			CederObjetDecorator deco = cedes.get(i);
			String[] valeurs = new String[champs.size()];
			for (int j = 0; j < champs.size(); j++) {
				StringBuffer val = new StringBuffer();
				
				if (champs.get(j).getNom().equals("Code")) {
					if (areEchantillons) {
						val.append(deco.getEchantillonCode());
					} else {
						val.append(deco.getProdDeriveCode());
					}
				} else if (champs.get(j).getNom().equals("EchantillonTypeId")) {
					val.append(deco.getEchantillonType());
				} else if (champs.get(j).getNom().equals("ProdTypeId")) {
					val.append(deco.getProdDeriveType());
				} else if (champs.get(j).getNom().equals("ConsentTypeId")) {
					val.append(deco.getStatutJuridique());
				} else if (champs.get(j).getNom().equals("QuantiteDemandee")) {
					val.append(deco.getCederQuantiteWithUnite());
				} else if (champs.get(j).getNom().equals("QuantiteRestante")) {
					val.append(deco.getQuantiteRestanteWithUnite());
				} else if (champs.get(j).getNom().equals("EmplacementId")) {
					val.append(deco.getEmplacementAdrl());
				}  else if (champs.get(j).getNom().equals("Nom")) {
					val.append(deco.getNomPatient() != null ? deco.getNomPatient() : "");
				} else if (champs.get(j).getNom().equals("Sorties")) {
					val.append(deco.getNbSorties());
				}
				
				valeurs[j] = val.toString();
			}
			LigneListe ligne = new LigneListe(valeurs);
			liste[i] = ligne;
		}
		
		ListeElement listeSites = null;
		if (cedes.size() > 0) {
			listeSites = new ListeElement(null, entetes, liste);
		}
		
		// ajout du paragraphe
		StringBuffer titre = new StringBuffer();
		if (areEchantillons) {
			titre.append(Labels.getLabel("bloc.cession.echantillons"));
		} else {
			titre.append(Labels.getLabel("bloc.cession.prodDerives"));
		}
		titre.append(" (");
		titre.append(cedes.size());
		titre.append(")");
		Paragraphe par = new Paragraphe(
				titre.toString(),  
				null, 
				null,
				null, listeSites);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par);
	}
	
	/**
	 * Crée le bloc contenant une liste de cessions.
	 * @param cessions Cessions à imprimer.
	 * @param champs Colonnes à imprimer.
	 */
	public void createBlocListeCessions(List<CederObjetDecorator> cedes,
			List<ChampEntite> champs) {
		// Entete
		String[] listeEntete = new String[champs.size()];
		for (int i = 0; i < champs.size(); i++) {
			StringBuffer iProperty = new StringBuffer();
			iProperty.append("Champ.");
			iProperty.append(champs.get(i).getEntite().getNom());
			iProperty.append(".");
			
			String champOk = "";
			// si le nom du champ finit par "Id", on le retire
			if (champs.get(i).getNom().endsWith("Id")) {
				champOk = champs.get(i).getNom().substring(
						0, champs.get(i).getNom().length() - 2);
			} else {
				champOk = champs.get(i).getNom();
			}
			iProperty.append(champOk);
			listeEntete[i] = Labels.getLabel(iProperty.toString());
		}
		EnteteListe entetes = new EnteteListe(listeEntete);
		
		// liste des cédés
		LigneListe[] liste = new LigneListe[cedes.size()];
		for (int i = 0; i < cedes.size(); i++) {
			CederObjetDecorator deco = cedes.get(i);
			String[] valeurs = new String[champs.size()];
			for (int j = 0; j < champs.size(); j++) {
				StringBuffer val = new StringBuffer();
				
				if (champs.get(j).getNom().equals("Numero")) {
					val.append(deco.getNumeroCession());
				} else if (champs.get(j).getNom().equals("DemandeDate")) {
					val.append(deco.getDateDemandeCessionFormatted());
				} else if (champs.get(j).getNom().equals("ValidationDate")) {
					val.append(deco.getDateValidationCessionFormatted());
				} else if (champs.get(j).getNom().equals("DestinataireId")) {
					val.append(deco.getCessionDestinataire());
				} else if (champs.get(j).getNom().equals("QuantiteDemandee")) {
					if (deco.getQuantiteDemandeeFormatted() != null) {
						val.append(deco.getQuantiteDemandeeFormatted());
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("QuantiteCedee")) {
					if (deco.getQuantiteCedeeFormatted() != null) {
						val.append(deco.getQuantiteCedeeFormatted());
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("CessionStatutId")) {
					val.append(deco.getCessionStatut());
				} else if (champs.get(j).getNom().equals("DemandeurId")) {
					val.append(deco.getCessionDemandeur());
				} else if (champs.get(j).getNom().equals("CessionTypeId")) {
					val.append(deco.getCessionType());
				}
				
				
				valeurs[j] = val.toString();
			}
			LigneListe ligne = new LigneListe(valeurs);
			liste[i] = ligne;
		}
		ListeElement listeSites = null;
		if (cedes.size() > 0) {
			listeSites = new ListeElement(null, entetes, liste);
		}
		
		// ajout du paragraphe
		StringBuffer titre = new StringBuffer();
		titre.append(Labels.getLabel("bloc.prodDerive.cessions"));
		titre.append(" (");
		titre.append(cedes.size());
		titre.append(")");
		Paragraphe par = new Paragraphe(
				titre.toString(),  
				null, 
				null,
				null, listeSites);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par);
	}
	
	/**
	 * Crée le bloc contenant une liste de collaborateurs.
	 * @param collaborateurs Collaborateurs à imprimer.
	 * @param champs Colonnes à imprimer.
	 */
	public void createBlocListeCollaborateurs(
			List<Collaborateur> collaborateurs,
			List<ChampEntite> champs) {
		// Entete
		String[] listeEntete = new String[champs.size()];
		for (int i = 0; i < champs.size(); i++) {
			StringBuffer iProperty = new StringBuffer();
			iProperty.append("Champ.");
			iProperty.append(champs.get(i).getEntite().getNom());
			iProperty.append(".");
			
			String champOk = "";
			// si le nom du champ finit par "Id", on le retire
			if (champs.get(i).getNom().endsWith("Id")) {
				champOk = champs.get(i).getNom().substring(
						0, champs.get(i).getNom().length() - 2);
			} else {
				champOk = champs.get(i).getNom();
			}
			iProperty.append(champOk);
			listeEntete[i] = Labels.getLabel(iProperty.toString());
		}
		EnteteListe entetes = new EnteteListe(listeEntete);
		
		// liste des cédés
		LigneListe[] liste = new LigneListe[collaborateurs.size()];
		for (int i = 0; i < collaborateurs.size(); i++) {
			Collaborateur collab = collaborateurs.get(i);
			String[] valeurs = new String[champs.size()];
			for (int j = 0; j < champs.size(); j++) {
				StringBuffer val = new StringBuffer();
				
				if (champs.get(j).getNom().equals("TitreId")) {
					if (collab.getTitre() != null) {
						val.append(collab.getTitre().getTitre());
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("Nom")) {
					val.append(collab.getNom());
				} else if (champs.get(j).getNom().equals("Prenom")) {
					if (collab.getPrenom() != null) {
						val.append(collab.getPrenom());
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("SpecialiteId")) {
					if (collab.getSpecialite() != null) {
						val.append(collab.getSpecialite().getNom());
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("EtablissementId")) {
					if (collab.getEtablissement() != null) {
						val.append(collab.getEtablissement().getNom());
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("ServiceId")) {
					Set<Service> set = ManagerLocator
						.getCollaborateurManager()
						.getServicesManager(collab);
					Iterator<Service> it = set.iterator();
					int cpt = 0;
					while (it.hasNext()) {
						Service serv = it.next();
						val.append(serv.getNom());
						if (cpt < set.size() - 1) {
							val.append(", ");
						}
					}
				}
				
				valeurs[j] = val.toString();
			}
			LigneListe ligne = new LigneListe(valeurs);
			liste[i] = ligne;
		}
		ListeElement listeSites = null;
		if (collaborateurs.size() > 0) {
			listeSites = new ListeElement(null, entetes, liste);
		}
		
		// ajout du paragraphe
		StringBuffer titre = new StringBuffer();
		titre.append(Labels.getLabel("bloc.patient.medecins"));
		titre.append(" (");
		titre.append(collaborateurs.size());
		titre.append(")");
		Paragraphe par = new Paragraphe(
				titre.toString(),  
				null, 
				null,
				null, listeSites);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par);
	}
	
	/**
	 * Crée le bloc contenant une liste de maladies.
	 * @param maladies Maladies à imprimer.
	 * @param champs Colonnes à imprimer.
	 */
	public void createBlocListeMaladies(
			List<Maladie> maladies,
			List<ChampEntite> champs) {
		// Entete
		String[] listeEntete = new String[champs.size()];
		for (int i = 0; i < champs.size(); i++) {
			StringBuffer iProperty = new StringBuffer();
			iProperty.append("Champ.");
			iProperty.append(champs.get(i).getEntite().getNom());
			iProperty.append(".");
			
			String champOk = "";
			// si le nom du champ finit par "Id", on le retire
			if (champs.get(i).getNom().endsWith("Id")) {
				champOk = champs.get(i).getNom().substring(
						0, champs.get(i).getNom().length() - 2);
			} else {
				champOk = champs.get(i).getNom();
			}
			iProperty.append(champOk);
			listeEntete[i] = Labels.getLabel(iProperty.toString());
		}
		EnteteListe entetes = new EnteteListe(listeEntete);
		
		// liste des cédés
		LigneListe[] liste = new LigneListe[maladies.size()];
		for (int i = 0; i < maladies.size(); i++) {
			Maladie maladie = maladies.get(i);
			String[] valeurs = new String[champs.size()];
			for (int j = 0; j < champs.size(); j++) {
				StringBuffer val = new StringBuffer();
				
				if (champs.get(j).getNom().equals("Libelle")) {
					if (maladie.getLibelle() != null) {
						val.append(maladie.getLibelle());
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("Code")) {
					if (maladie.getCode() != null) {
						val.append(maladie.getCode());
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("DateDiagnostic")) {
					if (maladie.getDateDiagnostic() != null) {
						val.append(ObjectTypesFormatters
							.dateRenderer2(maladie.getDateDiagnostic()));
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("DateDebut")) {
					if (maladie.getDateDebut() != null) {
						val.append(ObjectTypesFormatters
							.dateRenderer2(maladie.getDateDebut()));
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("SystemeDefaut")) {
					val.append(ObjectTypesFormatters
						.booleanLitteralFormatter(
							maladie.getSystemeDefaut()));
				}
				
				valeurs[j] = val.toString();
			}
			LigneListe ligne = new LigneListe(valeurs);
			liste[i] = ligne;
		}
		ListeElement listeSites = null;
		if (maladies.size() > 0) {
			listeSites = new ListeElement(null, entetes, liste);
		}
		
		// ajout du paragraphe
		StringBuffer titre = new StringBuffer();
		titre.append(Labels.getLabel("bloc.patient.maladies"));
		titre.append(" (");
		titre.append(maladies.size());
		titre.append(")");
		Paragraphe par = new Paragraphe(
				titre.toString(),  
				null, 
				null,
				null, listeSites);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par);
	}
	
	/**
	 * Crée le bloc contenant une liste de Prelevements.
	 * @param prelevements Prelevements à imprimer.
	 * @param champs Colonnes à imprimer.
	 */
	public void createBlocListePrelevements(
			List<Prelevement> prelevements,
			List<ChampEntite> champs) {
		// Entete
		String[] listeEntete = new String[champs.size()];
		for (int i = 0; i < champs.size(); i++) {
			StringBuffer iProperty = new StringBuffer();
			iProperty.append("Champ.");
			iProperty.append(champs.get(i).getEntite().getNom());
			iProperty.append(".");
			
			String champOk = "";
			// si le nom du champ finit par "Id", on le retire
			if (champs.get(i).getNom().endsWith("Id")) {
				champOk = champs.get(i).getNom().substring(
						0, champs.get(i).getNom().length() - 2);
			} else {
				champOk = champs.get(i).getNom();
			}
			iProperty.append(champOk);
			listeEntete[i] = Labels.getLabel(iProperty.toString());
		}
		EnteteListe entetes = new EnteteListe(listeEntete);
		
		// liste des prelevements
		LigneListe[] liste = new LigneListe[prelevements.size()];
		for (int i = 0; i < prelevements.size(); i++) {
			Prelevement prlvt = prelevements.get(i);
			String[] valeurs = new String[champs.size()];
			for (int j = 0; j < champs.size(); j++) {
				StringBuffer val = new StringBuffer();
				
				if (champs.get(j).getNom().equals("Code")) {
					val.append(prlvt.getCode());
				} else if (champs.get(j).getNom().equals("DatePrelevement")) {
					if (prlvt.getDatePrelevement() != null) {
						val.append(ObjectTypesFormatters
							.dateRenderer2(prlvt.getDatePrelevement()));
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("BanqueId")) {
					val.append(prlvt.getBanque().getNom());
				} else if (champs.get(j).getNom().equals("MaladieId")) {
					if (prlvt.getMaladie() != null 
						&&  prlvt.getMaladie().getLibelle() != null) {
						val.append(prlvt.getMaladie().getLibelle());
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("NatureId")) {
					val.append(prlvt.getNature().getNature());
				} else if (champs.get(j).getNom().equals("PrelevementTypeId")) {
					if (prlvt.getPrelevementType() != null) {
						val.append(prlvt.getPrelevementType().getType());
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("ConsentTypeId")) {
					val.append(prlvt.getConsentType().getType());
				} else if (champs.get(j).getNom().equals("NbEchantillons")) {
					val.append(PrelevementUtils
	        				.getNbEchanRestants(prlvt) + "/"
	        				+ ManagerLocator.getPrelevementManager()
	        				.getEchantillonsManager(prlvt).size());
				} else if (champs.get(j).getNom().equals("Diagnostic")) {
					List<String> codes = ManagerLocator.getCodeAssigneManager()
						.formatCodesAsStringsManager(
							ManagerLocator.getCodeAssigneManager()
							.findFirstCodesLesByPrelevementManager(
									prlvt));
					List<String> codesFinaux = new ArrayList<String>();
					for (int k = 0; k < codes.size(); k++) {
						if (!codesFinaux.contains(codes.get(k))) {
							codesFinaux.add(codes.get(k));
						}
					}
					StringBuffer sb = new StringBuffer();
					for (int k = 0; k < codesFinaux.size(); k++) {
						sb.append(codesFinaux.get(k));
						if (k + 1 < codesFinaux.size()) {
							sb.append(", ");
						}
					}
					val.append(sb.toString());
				} else if (champs.get(j).getNom().equals("Stockes")) {
					val.append(PrelevementUtils
	        				.getNbEchanStockesSurTotal(prlvt));
				}
				
				valeurs[j] = val.toString();
			}
			LigneListe ligne = new LigneListe(valeurs);
			liste[i] = ligne;
		}
		ListeElement listeSites = null;
		if (prelevements.size() > 0) {
			listeSites = new ListeElement(null, entetes, liste);
		}
		
		// ajout du paragraphe
		StringBuffer titre = new StringBuffer();
		titre.append(Labels.getLabel("bloc.patient.prelevements"));
		titre.append(" (");
		titre.append(prelevements.size());
		titre.append(")");
		Paragraphe par = new Paragraphe(
				titre.toString(),  
				null, 
				null,
				null, listeSites);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par);
	}
	
	/**
	 * Crée le bloc contenant une liste d'échantillons.
	 * @param echantillons Echantillons à imprimer.
	 * @param champs Colonnes à imprimer.
	 */
	public void createBlocListeEchantillon(Set<Echantillon> echantillons,
			List<ChampEntite> champs) {
		// Entete
		String[] listeEntete = new String[champs.size()];
		EchantillonRowRenderer renderer = new EchantillonRowRenderer(false, false);
		for (int i = 0; i < champs.size(); i++) {
			StringBuffer iProperty = new StringBuffer();
			iProperty.append("Champ.");
			iProperty.append(champs.get(i).getEntite().getNom());
			iProperty.append(".");
			
			String champOk = "";
			// si le nom du champ finit par "Id", on le retire
			if (champs.get(i).getNom().endsWith("Id")) {
				champOk = champs.get(i).getNom().substring(
						0, champs.get(i).getNom().length() - 2);
			} else {
				champOk = champs.get(i).getNom();
			}
			iProperty.append(champOk);
			listeEntete[i] = Labels.getLabel(iProperty.toString());
		}
		EnteteListe entetes = new EnteteListe(listeEntete);
		
		// liste des échantillons
		LigneListe[] liste = new LigneListe[echantillons.size()];
		Iterator<Echantillon> it = echantillons.iterator();
		int i = 0;
		while (it.hasNext()) {
			Echantillon echan = it.next();
			String[] valeurs = new String[champs.size()];
			for (int j = 0; j < champs.size(); j++) {
				StringBuffer val = new StringBuffer();
				
				if (champs.get(j).getNom().equals("Code")) {
					val.append(echan.getCode());
				} else if (champs.get(j).getNom().equals("DateStock")) {
					val.append(ObjectTypesFormatters
							.dateRenderer2(echan.getDateStock()));
				} else if (champs.get(j).getNom().equals("EchantillonTypeId")) {
					if (echan.getEchantillonType() != null) {
						val.append(echan.getEchantillonType().getType());
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("AdicapOrganeId")) {
					List<String> codes = ManagerLocator.getCodeAssigneManager()
						.formatCodesAsStringsManager(
								ManagerLocator.getCodeAssigneManager()
								.findCodesOrganeByEchantillonManager(echan));
					StringBuffer sb = new StringBuffer();
					for (int k = 0; k < codes.size(); k++) {
						sb.append(codes.get(k));
						if (k + 1 < codes.size()) {
							sb.append(", ");
						}
					}
					val.append(sb.toString());
				} else if (champs.get(j).getNom().equals("CodeAssigneId")) {
					List<String> codes = ManagerLocator.getCodeAssigneManager()
						.formatCodesAsStringsManager(
								ManagerLocator.getCodeAssigneManager()
								.findCodesMorphoByEchantillonManager(echan));
					StringBuffer sb = new StringBuffer();
					for (int k = 0; k < codes.size(); k++) {
						sb.append(codes.get(k));
						if (k + 1 < codes.size()) {
							sb.append(", ");
						}
					}
					val.append(sb.toString());
				} else if (champs.get(j).getNom().equals("Quantite")) {
					val.append(renderer.getQuantite(echan));
				} else if (champs.get(j).getNom().equals("ObjetStatutId")) {
					if (echan.getObjetStatut() != null) {
						val.append(ObjectTypesFormatters
								.ILNObjectStatut(echan.getObjetStatut()));
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("EmplacementId")) {
					if (!anonyme) {
						val.append(renderer.getEmplacementAdrl(echan));
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("DelaiCgl")) {
					val.append(renderer.getDelaiCgl(echan));
				} else if (champs.get(j).getNom().equals("EchanQualiteId")) {
					if (echan.getEchanQualite() != null) {
						val.append(echan.getEchanQualite().getEchanQualite());
					} else {
						val.append("-");
					}
				}
				
				valeurs[j] = val.toString();
			}
			LigneListe ligne = new LigneListe(valeurs);
			liste[i] = ligne;
			++i;
		}
		ListeElement listeSites = null;
		if (echantillons.size() > 0) {
			listeSites = new ListeElement(null, entetes, liste);
		}
		
		// ajout du paragraphe
		StringBuffer titre = new StringBuffer();
		titre.append(Labels.getLabel("bloc.prelevement.echantillons"));
		titre.append(" (");
		titre.append(echantillons.size());
		titre.append(")");
		Paragraphe par = new Paragraphe(
				titre.toString(),  
				null, 
				null,
				null, listeSites);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par);
	}
	
	/**
	 * Crée le bloc contenant une liste de dérivés.
	 * @param derives ProdDerives à imprimer.
	 * @param champs Colonnes à imprimer.
	 */
	public void createBlocListeProdDerive(List<ProdDerive> derives,
			List<ChampEntite> champs) {
		// Entete
		String[] listeEntete = new String[champs.size()];
		ProdDeriveRowRenderer renderer = new ProdDeriveRowRenderer(false, false);
		for (int i = 0; i < champs.size(); i++) {
			StringBuffer iProperty = new StringBuffer();
			iProperty.append("Champ.");
			iProperty.append(champs.get(i).getEntite().getNom());
			iProperty.append(".");
			
			String champOk = "";
			// si le nom du champ finit par "Id", on le retire
			if (champs.get(i).getNom().endsWith("Id")) {
				champOk = champs.get(i).getNom().substring(
						0, champs.get(i).getNom().length() - 2);
			} else {
				champOk = champs.get(i).getNom();
			}
			iProperty.append(champOk);
			listeEntete[i] = Labels.getLabel(iProperty.toString());
		}
		EnteteListe entetes = new EnteteListe(listeEntete);
		
		// liste des échantillons
		LigneListe[] liste = new LigneListe[derives.size()];
		for (int i = 0; i < derives.size(); i++) {
			ProdDerive prod = derives.get(i);
			String[] valeurs = new String[champs.size()];
			for (int j = 0; j < champs.size(); j++) {
				StringBuffer val = new StringBuffer();
				
				if (champs.get(j).getNom().equals("Code")) {
					val.append(prod.getCode());
				} else if (champs.get(j).getNom().equals("DateStock")) {
					if (prod.getDateStock() != null) {
						val.append(ObjectTypesFormatters
								.dateRenderer2(prod.getDateStock()));
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("ProdTypeId")) {
					if (prod.getProdType() != null) {
						val.append(prod.getProdType().getType());
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("Volume")) {
					val.append(renderer.getVolume(prod));
				} else if (champs.get(j).getNom().equals("Quantite")) {
					val.append(renderer.getQuantite(prod));
				} else if (champs.get(j).getNom().equals("ObjetStatutId")) {
					if (prod.getObjetStatut() != null) {
						val.append(ObjectTypesFormatters
								.ILNObjectStatut(prod.getObjetStatut()));
					} else {
						val.append("-");
					}
				} else if (champs.get(j).getNom().equals("EmplacementId")) {
					if (!anonyme) {
						val.append(renderer.getEmplacementAdrl(prod));
					} else {
						val.append("-");
					}
				}
				
				valeurs[j] = val.toString();
			}
			LigneListe ligne = new LigneListe(valeurs);
			liste[i] = ligne;
		}
		ListeElement listeSites = null;
		if (derives.size() > 0) {
			listeSites = new ListeElement(null,	entetes, liste);
		}
		
		// ajout du paragraphe
		StringBuffer titre = new StringBuffer();
		titre.append(Labels.getLabel("bloc.prelevement.prodDerives"));
		titre.append(" (");
		titre.append(derives.size());
		titre.append(")");
		Paragraphe par = new Paragraphe(
				titre.toString(),  
				null, 
				null,
				null, listeSites);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par);
	}
	
	/**
	 * Crée le bloc contenant une liste de dérivés.
	 * @param derives ProdDerives à imprimer.
	 * @param champs Colonnes à imprimer.
	 */
	public void createBlocListeRetours(List<Retour> retours) {
		// Entete
		String[] listeEntete = new String[6];
		List<RetourDecorator> decos = RetourDecorator.decorateListe(retours, null);
		listeEntete[0] = Labels.getLabel("Champ.Retour.DateSortie");
		listeEntete[1] = Labels.getLabel("Champ.Retour.DateRetour");
		listeEntete[2] = Labels.getLabel("listeRetour.delai");
		listeEntete[3] = Labels.getLabel("listeRetour.objet");
		listeEntete[4] = Labels.getLabel("Champ.Retour.TempMoyenne");
		listeEntete[4] = Labels.getLabel("Champ.Echantillon.Emplacement");
		EnteteListe entetes = new EnteteListe(listeEntete);
		
		// liste des retours
		LigneListe[] liste = new LigneListe[decos.size()];
		for (int i = 0; i < decos.size(); i++) {
			String[] valeurs = new String[6];
			valeurs[0] = decos.get(i).getDateSortie();
			valeurs[1] = decos.get(i).getDateRetour();
			valeurs[2] = decos.get(i).getDelaiInHeureMin();
			valeurs[3] = decos.get(i).getSortieObjet();
			valeurs[4] = decos.get(i).getTempMoyenne();
			valeurs[5] = decos.get(i).getRetour().getOldEmplacementAdrl();
			
			LigneListe ligne = new LigneListe(valeurs);
			liste[i] = ligne;
		}
		ListeElement listeSites = null;
		if (decos.size() > 0) {
			listeSites = new ListeElement(null,	entetes, liste);
		}
		
		// ajout du paragraphe
		StringBuffer titre = new StringBuffer();
		titre.append(Labels.getLabel("bloc.echantillon.retours"));
		titre.append(" (");
		titre.append(decos.size());
		titre.append(")");
		Paragraphe par = new Paragraphe(
				titre.toString(),  
				null, 
				null,
				null, listeSites);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par);
	}
	
	/**
	 * Crée le bloc contenant une liste d'opérations.
	 * @param operations Operations à imprimer.
	 */
	public void createBlocListeOperation(List<Operation> operations) {
		// Entete
		String[] listeEntete = new String[3];
		listeEntete[0] = Labels.getLabel("historique.utilisateur");
		listeEntete[1] = Labels.getLabel("historique.operationType");
		listeEntete[2] = Labels.getLabel("historique.date");
		EnteteListe entetes = new EnteteListe(listeEntete);
		
		// liste des opérations
		LigneListe[] liste = new LigneListe[operations.size()];
		for (int i = 0; i < operations.size(); i++) {
			Operation op = operations.get(i);
			String[] valeurs = new String[3];
			valeurs[0] = op.getUtilisateur().getLogin();
			valeurs[1] = op.getOperationType().getNom();
			valeurs[2] = ObjectTypesFormatters
				.dateRenderer2(op.getDate());
			LigneListe ligne = new LigneListe(valeurs);
			liste[i] = ligne;
		}
		ListeElement listeSites = null;
		if (operations.size() > 0) {
			listeSites = new ListeElement(null,	entetes, liste);
		}
		
		// ajout du paragraphe
		StringBuffer titre = new StringBuffer();
		titre.append(Labels.getLabel("general.historique"));
		Paragraphe par = new Paragraphe(
				titre.toString(),  
				null, 
				null,
				null, listeSites);
		ManagerLocator.getXmlUtils().addParagraphe(pageXML, par);
	}
	
	/*******************************************************/
	/**                  GETTERS - SETTERS                 */
	/*******************************************************/

	

	public BlocImpressionRowRenderer getBlocImpressionRenderer() {
		return blocImpressionRenderer;
	}

	public void setBlocImpressionRenderer(
			BlocImpressionRowRenderer bRenderer) {
		blocImpressionRenderer = bRenderer;
	}

	public Entite getSelectedEntite() {
		return selectedEntite;
	}

	public void setSelectedEntite(Entite selected) {
		this.selectedEntite = selected;
	}

	public BlocImpressionRowRenderer getBlocImpressionRendererEdit() {
		return blocImpressionRendererEdit;
	}

	public void setBlocImpressionRendererEdit(
			BlocImpressionRowRenderer bRendererEdit) {
		blocImpressionRendererEdit = bRendererEdit;
	}

	public Object getObjectToPrint() {
		return objectToPrint;
	}

	public void setObjectToPrint(Object objectToP) {
		this.objectToPrint = objectToP;
	}

	public List<Template> getTemplates() {
		return templates;
	}

	public void setTemplates(List<Template> t) {
		this.templates = t;
	}

	public Template getSelectedTemplate() {
		return selectedTemplate;
	}

	public void setSelectedTemplate(Template selected) {
		this.selectedTemplate = selected;
	}

	public String getNomFile() {
		return nomFile;
	}

	public void setNomFile(String file) {
		this.nomFile = file;
	}

	public List<String> getFormats() {
		return formats;
	}

	public void setFormats(List<String> f) {
		this.formats = f;
	}

	public String getSelectedFormat() {
		return selectedFormat;
	}

	public void setSelectedFormat(String selected) {
		this.selectedFormat = selected;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document doc) {
		this.document = doc;
	}

	public Element getPageXML() {
		return pageXML;
	}

	public void setPageXML(Element p) {
		this.pageXML = p;
	}
	
	public ConstWord getNomConstraint() {
		return TemplateConstraints.getNomConstraint();
	}
	
	public ConstWord getNomNullConstraint() {
		return TemplateConstraints.getNomNullConstraint();
	}

	public FichePatientStatic getFichePatient() {
		return fichePatient;
	}

	public void setFichePatient(FichePatientStatic ficheP) {
		this.fichePatient = ficheP;
	}

	public Boolean getAnonyme() {
		return anonyme;
	}

	public void setAnonyme(Boolean ano) {
		this.anonyme = ano;
	}

	@Override
	public void setFocusOnElement() {		
	}

	public Boolean getCanHistorique() {
		return canHistorique;
	}

	public void setCanHistorique(Boolean a) {
		this.canHistorique = a;
	}

	@Override
	public String getDeleteWaitLabel() {
		return null;
	}

	@Override
	public TKdataObject getParentObject() {
		return null;
	}

	@Override
	public boolean prepareDeleteObject() {
		return false;
	}

	@Override
	public void removeObject(String comments) {		
	}

	@Override
	public void setParentObject(TKdataObject obj) {		
	}
}
