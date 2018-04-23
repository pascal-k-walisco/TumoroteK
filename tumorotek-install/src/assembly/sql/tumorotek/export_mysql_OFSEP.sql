-- ------------------------------------------------------
--  procedures PATIENT
-- ------------------------------------------------------

delimiter $$

DROP PROCEDURE IF EXISTS `create_tmp_patient_ofsep_table`;

CREATE PROCEDURE `create_tmp_patient_ofsep_table`()
BEGIN

	DROP TEMPORARY TABLE IF EXISTS TMP_PATIENT_EXPORT;
	
	CREATE TEMPORARY TABLE TMP_PATIENT_EXPORT (
		PATIENT_ID int(10),
		PATIENT_NIP varchar(50),
--		NOM_NAISSANCE varchar(50) ,
		PATIENT_NOM varchar(50),
		PATIENT_PRENOM varchar(50),
		PATIENT_SEXE char(3),
		PATIENT_DATE_NAISSANCE datetime,
--		VILLE_NAISSANCE varchar(100),
--		PAYS_NAISSANCE varchar(100),
		PATIENT_ETAT varchar(10),
--		DATE_ETAT date,
--		DATE_DECES date,
--		MEDECIN_PATIENT varchar(300),
--		CODE_ORGANE VARCHAR(500),
--		NOMBRE_PRELEVEMENT int(4),
--		DATE_HEURE_SAISIE datetime,
--		UTILISATEUR_SAISIE varchar(100),
--    	MALADIE_ID varchar(100),
  	  	PRIMARY KEY(PATIENT_ID)        
	)ENGINE=MYISAM, default character SET = utf8;

END$$

delimiter $$

DROP PROCEDURE IF EXISTS `fill_tmp_table_patient_ofsep`;

CREATE PROCEDURE `fill_tmp_table_patient_ofsep`(IN id INTEGER)
BEGIN 

	INSERT INTO TMP_PATIENT_EXPORT (
		PATIENT_ID, 
		PATIENT_NIP, 
--		NOM_NAISSANCE, 
		PATIENT_NOM, 
		PATIENT_PRENOM, 
		PATIENT_SEXE, 
		PATIENT_DATE_NAISSANCE,
--	    VILLE_NAISSANCE, 
--	    PAYS_NAISSANCE, 
	    PATIENT_ETAT
--	    DATE_ETAT, 
--	    DATE_DECES, 
--	    MEDECIN_PATIENT, 
--	    CODE_ORGANE, 
--	    NOMBRE_PRELEVEMENT, 
--	    UTILISATEUR_SAISIE, 
--	    DATE_HEURE_SAISIE, 
--	    MALADIE_ID
	) 
	SELECT 
		id, 
		nom, 
--		nom_naissance, 
		nom, 
		nom, 
		sexe, 
		date_naissance,
--		ville_naissance, 
--		pays_naissance, 
		patient_etat 
--		date_etat, 
--		date_deces,
--		(
--			SELECT GROUP_CONCAT(c.nom) 
--			FROM PATIENT_MEDECIN pm 
--			JOIN COLLABORATEUR c 
--			WHERE pm.COLLABORATEUR_ID = c.COLLABORATEUR_ID 
--			AND pm.PATIENT_id = id
--		),
--		(
--			SELECT GROUP_CONCAT(distinct(ca.code) ORDER BY ca.ordre) 
--			FROM PATIENT p 
--			INNER JOIN MALADIE m 
--			INNER JOIN PRELEVEMENT pr 
--			INNER JOIN ECHANTILLON e 
--			JOIN CODE_ASSIGNE ca 
--			WHERE p.patient_id = m.patient_id 
--			AND m.maladie_id = pr.maladie_id 
--			AND pr.prelevement_id = e.prelevement_id 
--			AND e.echantillon_id = ca.echantillon_id 
--			AND ca.IS_ORGANE=1 
--			AND p.patient_id = id
--		),
--		(
--			SELECT count(*) 
--			FROM PRELEVEMENT pr 
--			INNER JOIN MALADIE m 
--			WHERE pr.maladie_id = m.maladie_id 
--			AND m.patient_id = id
--		),
--		(
--			SELECT ut.login 
--			FROM UTILISATEUR ut 
--			JOIN OPERATION op ON ut.utilisateur_id = op.utilisateur_id 
--			WHERE op.OPERATION_TYPE_ID = 3 
--			AND op.entite_id = 1 
--			AND op.objet_id = id
--		),
--		(
--			SELECT op.date_ 
--			FROM OPERATION op 
--			WHERE op.OPERATION_TYPE_ID = 3 
--			AND op.entite_id = 1 
--			AND op.objet_id = id
--		),
--		(
--			SELECT GROUP_CONCAT(maladie_id) 
--			FROM MALADIE 
--			WHERE patient_id = id
--		)
		FROM PATIENT 
		WHERE patient_id = id;
		
END$$

-- ------------------------------------------------------
--  procedures PRELEVEMENT
-- ------------------------------------------------------

delimiter $$

DROP PROCEDURE IF EXISTS `create_tmp_prelevement_ofsep_table`;

CREATE PROCEDURE `create_tmp_prelevement_ofsep_table`()
BEGIN

	DROP TEMPORARY TABLE IF EXISTS TMP_PRELEVEMENT_EXPORT;

	CREATE TEMPORARY TABLE TMP_PRELEVEMENT_EXPORT (
	PRELEVEMENT_ID int(10),
	PRELEVEMENT_BANQUE varchar(300),
	PRELEVEMENT_CODE varchar(50) ,
--  	NUMERO_LABO varchar(50),
	PRELEVEMENT_NATURE varchar(200),
    PRELEVEMENT_DATE_PRELEVEMENT datetime,
	PRELEVEMENT_TYPE varchar(200) ,
   	PRELEVEMENT_STERILE boolean,
--    	RISQUE varchar(200),
        PRELEVEMENT_CONFORME_ARRIVEE boolean,
        PRELEVEMENT_RAISON_NC_TRAITEMENT varchar(1000),
        PRELEVEMENT_ETABLISSEMENT varchar(100),
        PRELEVEMENT_SERVICE_PRELEVEUR varchar(100),
--      PRELEVEUR varchar(100),
--      CONDIT_TYPE varchar(200),
--      CONDIT_NBR int(11),
--	    CONDIT_MILIEU varchar(200),
     PRELEVEMENT_CONSENT_TYPE varchar(200),
--      CONSENT_DATE date,
--      DATE_DEPART datetime,
--      TRANSPORTEUR varchar(50),
--      TRANSPORT_TEMP	DECIMAL(12,3),
        PRELEVEMENT_DATE_ARRIVEE datetime,
--	OPERATEUR varchar(50),
--      CONG_DEPART boolean,
--      CONG_ARRIVEE boolean,
--      LABO_INTER varchar(3),
--      QUANTITE DECIMAL(12,3),
--      PATIENT_NDA varchar(20),
--      DIAGNOSTIC	varchar(500),
--		CODE_ORGANE VARCHAR(500),
--      ECHAN_TOTAL int(4),
--      ECHAN_RESTANT int(4),
--      ECHAN_STOCKE int(4),
--        PRELEVEMENT_AGE_PREL int(4),
--	NOMBRE_DERIVES int(4),
/*
	PRELEVEMENT_DATE_HEURE_SAISIE datetime,
	PRELEVEMENT_UTILISATEUR_SAISIE varchar(100), */
	ECHANTILLON_ID int(10),
	ECHANTILLON_CODE varchar(50),
	ECHANTILLON_TYPE varchar(200),
	ECHANTILLON_QUANTITE decimal(12,3),
	ECHANTILLON_QUANTITE_INIT decimal(12,3),
	ECHANTILLON_QUANTITE_UNITE varchar(30),
	ECHANTILLON_MODE_PREPA varchar(200),
	ECHANTILLON_STERILE tinyint(1),
	ECHANTILLON_DATE_HEURE_STOCK datetime,
	ECHANTILLON_DELAI_CGL decimal(9,2),
	ECHANTILLON_CONF_APRES_TTMT tinyint(1),
	ECHANTILLON_CONF_CESSION tinyint(1),
	ECHANTILLON_STATUT varchar(20),
    MALADIE_ID int(10),
    MALADIE_LIBELLE varchar(300),
	MALADIE_CODE_MALADIE varchar(50) ,
--	MALADIE_DATE_DIAGNOSTIC date,
--	MALADIE_DATE_DEBUT date ,
	MALADIE_MEDECIN_MALADIE varchar(300),	
    PATIENT_ID int (10),
    PRIMARY KEY (PRELEVEMENT_ID, ECHANTILLON_ID),
	INDEX (PATIENT_ID),
	INDEX (MALADIE_ID)
	)ENGINE=MYISAM, default character SET = utf8;

END$$

delimiter $$

DROP PROCEDURE IF EXISTS `fill_tmp_table_prel_ofsep`;

CREATE PROCEDURE `fill_tmp_table_prel_ofsep`(IN id INTEGER)
BEGIN 
	INSERT INTO TMP_PRELEVEMENT_EXPORT (
	    PRELEVEMENT_ID ,
	    PRELEVEMENT_BANQUE ,
	    PRELEVEMENT_CODE  ,
--	    NUMERO_LABO ,
	    PRELEVEMENT_NATURE ,
	    PRELEVEMENT_DATE_PRELEVEMENT ,
	    PRELEVEMENT_TYPE  ,
	    PRELEVEMENT_STERILE ,
--	    RISQUE ,
	    PRELEVEMENT_CONFORME_ARRIVEE ,
	    PRELEVEMENT_RAISON_NC_TRAITEMENT ,
	    PRELEVEMENT_ETABLISSEMENT ,
	    PRELEVEMENT_SERVICE_PRELEVEUR ,
--	    PRELEVEUR ,
--	    CONDIT_TYPE ,
--	    CONDIT_NBR ,
--	    CONDIT_MILIEU ,
	    PRELEVEMENT_CONSENT_TYPE,
--	    CONSENT_DATE,
--	    DATE_DEPART ,
--	    TRANSPORTEUR ,
--	    TRANSPORT_TEMP,
	    PRELEVEMENT_DATE_ARRIVEE ,
--	    OPERATEUR,
--	    CONG_DEPART,
--	    CONG_ARRIVEE,
--    	LABO_INTER,
--	    QUANTITE ,
--	    PATIENT_NDA ,
--	    CODE_ORGANE,
--	    DIAGNOSTIC,
/*
	    ECHAN_TOTAL,
	    ECHAN_RESTANT,
	    ECHAN_STOCKE,
*/
--	    PRELEVEMENT_AGE_PREL,
--	    PRELEVEMENT_NOMBRE_DERIVES,
--	    PRELEVEMENT_DATE_HEURE_SAISIE,
--	    PRELEVEMENT_UTILISATEUR_SAISIE,
	    ECHANTILLON_ID,
	    ECHANTILLON_CODE,
	    ECHANTILLON_TYPE,
	    ECHANTILLON_QUANTITE,
	    ECHANTILLON_QUANTITE_INIT,
	    ECHANTILLON_QUANTITE_UNITE,
	    ECHANTILLON_MODE_PREPA,
	    ECHANTILLON_STERILE,
	    ECHANTILLON_DATE_HEURE_STOCK,
	    ECHANTILLON_DELAI_CGL,
	    ECHANTILLON_CONF_APRES_TTMT,
--	    ECHANTILLON_CONF_APRES_TTMT,
	    ECHANTILLON_CONF_CESSION,    
--	     ECHANTILLON_CONF_CESSIONè, 
		ECHANTILLON_STATUT,
	    MALADIE_ID,
	    MALADIE_LIBELLE,
	    MALADIE_CODE_MALADIE,
--	    MALADIE_DATE_DIAGNOSTIC,
--	    MALADIE_DATE_DEBUT,
	    MALADIE_MEDECIN_MALADIE,
	    PATIENT_ID)  
	SELECT 
		p.prelevement_id, 
		b.nom as 'collection', 
		p.code, 
--		p.numero_labo as laboratoire, 
		n.nature, 
		p.date_prelevement, 
		pt.type, 
		p.sterile, 
--		(
--			select GROUP_CONCAT(r.nom) 
--			from RISQUE r 
--			JOIN PRELEVEMENT_RISQUE pr ON r.risque_id = pr.risque_id 
--			WHERE pr.prelevement_id = p.prelevement_id
--		) as 'risque_infectieux', 
		p.conforme_arrivee,  
		(
			select GROUP_CONCAT(nc.nom)
			FROM OBJET_NON_CONFORME onc 
			LEFT JOIN NON_CONFORMITE nc ON onc.non_conformite_id = nc.non_conformite_id 
			LEFT JOIN CONFORMITE_TYPE ct ON nc.conformite_type_id = ct.conformite_type_id  
			WHERE ct.conformite_type_id = 1 AND p.prelevement_id = onc.objet_id 
		) as 'Raison_de_non_conformité', 
		-- GROUP_CONCAT(nc.nom) as 'Raison_de_non_conformité', 
		et.nom as 'établissement_préleveur', 
		s.nom as 'Service_préleveur',
--		co.nom as 'Préleveur', 
--		ct.type as 'Type_de_conditionnement', 
--		p.condit_nbr as 'Nombre_de_prélevements',
--		cm.milieu, 
		consent.type as 'Statut_juridique', 
--		p.consent_date as 'date_du_statut', 
--		p.date_depart,
--		tr.nom as 'Transporteur', 
--		p.transport_temp as 'Temps_de_transport', 
		p.date_arrivee,
--		coco.nom as 'Opérateur', 
--		p.cong_depart, 
--		p.cong_arrivee, 
--		(
--			select count(l.labo_inter_id) 
--			FROM LABO_INTER l 
--			where l.prelevement_id = id
--		), 
--		p.quantite, 
--		p.patient_nda as 'Num_Dossier_Patient', 
--		(
--			SELECT GROUP_CONCAT(distinct(ca.code) ORDER BY ca.ordre) 
--			FROM CODE_ASSIGNE ca 
--			INNER JOIN ECHANTILLON e ON e.echantillon_id = ca.echantillon_id 
--			WHERE ca.IS_ORGANE=1 AND e.prelevement_id = id
--		),
--		(
--			SELECT GROUP_CONCAT(distinct(ca.code) ORDER BY ca.ordre) 
--			FROM CODE_ASSIGNE ca INNER JOIN ECHANTILLON e ON e.echantillon_id = ca.echantillon_id 
--			WHERE ca.IS_MORPHO=1 
--			AND e.prelevement_id = id
--		),
/*
		(
			SELECT count(e.prelevement_id) 
			FROM ECHANTILLON e 
			WHERE e.prelevement_id = p.prelevement_id
		) AS 'Total_Echantillons', 
		(
			SELECT count(e1.prelevement_id) 
			FROM ECHANTILLON e1 
			WHERE e1.prelevement_id = p.prelevement_id 
			AND e1.quantite> 0
		) AS 'Echantillons_restants',
		(
			SELECT count(e2.prelevement_id) 
			FROM ECHANTILLON e2 
			INNER JOIN OBJET_STATUT os ON e2.objet_statut_id = os.objet_statut_id 
			AND (os.statut = 'STOCKE' OR os.statut = 'RESERVE') 
			WHERE e2.prelevement_id = p.prelevement_id
		) as 'Echantillons_stockés',
*/
		/*
		(
			select FLOOR(datediff(p.date_prelevement, pat.DATE_NAISSANCE)/365.25)
		) as 'AGE_AU_PREL',*/
		/*
		(
			SELECT COUNT(tr.objet_id) 
			FROM TRANSFORMATION tr 
			INNER JOIN PROD_DERIVE pd ON tr.TRANSFORMATION_ID = pd.TRANSFORMATION_ID 
			WHERE tr.OBJET_ID = id and tr.entite_id = 2
		) as 'Nb_Produits_dérivés',
		*/

		/*(
			SELECT op.date_ 
			FROM OPERATION op 
			WHERE op.OPERATION_TYPE_ID = 3 AND op.entite_id = 2 AND op.objet_id = id
		) as 'date_heure_saisie', 
		(
			SELECT ut.login 
			FROM UTILISATEUR ut 
			JOIN OPERATION op ON ut.utilisateur_id = op.utilisateur_id 
			WHERE op.OPERATION_TYPE_ID = 3 AND op.entite_id = 2 AND op.objet_id = id
		) as 'Utilisateur_saisie',*/
		COALESCE(ec.echantillon_id, -1) as 'Enchantillon ID',
		ec.code as 'N° échantillon',
		ect.type as 'Type échantillon',
		ec.quantite as 'Quantité échantillon',
		ec.quantite_init as 'Quantité initiale échantillon',
		eciu.unite as 'Unité quantité échantillon',
		ecmp.nom as 'Mode de préparation échantillong',
		ec.sterile as 'Stérile échantillon',
		ec.date_stock as 'Date stockage échantillon',
		ec.delai_cgl as 'Délai de congélation échantillon',
		ec.conforme_traitement as 'Echantillon conforme apèrs traitement',
		ec.conforme_cession as 'Enchantillon conforme à la cession',
		os.statut,
		p.maladie_id, 
		'SCLÉROSE EN PLAQUES', 
		'G35', 
--		m.date_diagnostic, 
--		m.date_debut,
		(
			select GROUP_CONCAT(c.nom) 
			FROM MALADIE_MEDECIN mm 
			JOIN COLLABORATEUR c ON mm.collaborateur_id = c.collaborateur_id 
			WHERE p.maladie_id = mm.maladie_id
		),
		pat.patient_id
		FROM PRELEVEMENT p 
		INNER JOIN BANQUE b 
		INNER JOIN NATURE n 
		INNER JOIN ENTITE ent 
		LEFT JOIN ECHANTILLON ec ON ec.prelevement_id = p.prelevement_id
		LEFT JOIN ECHANTILLON_TYPE ect ON ec.echantillon_type_id = ect.echantillon_type_id
		LEFT JOIN UNITE eciu ON ec.quantite_unite_id = eciu.unite_id
		LEFT JOIN MODE_PREPA ecmp ON ec.mode_prepa_id = ecmp.mode_prepa_id
		LEFT JOIN PRELEVEMENT_TYPE pt ON p.prelevement_type_id = pt.prelevement_type_id
		-- LEFT JOIN OBJET_NON_CONFORME onc ON p.prelevement_id = onc.objet_id 
		-- LEFT JOIN NON_CONFORMITE nc ON onc.non_conformite_id = nc.non_conformite_id 
		LEFT JOIN SERVICE s ON p.service_preleveur_id = s.service_id LEFT JOIN ETABLISSEMENT et ON s.etablissement_id = et.etablissement_id
		LEFT JOIN COLLABORATEUR co ON p.preleveur_id = co.collaborateur_id
		LEFT JOIN CONDIT_TYPE ct ON p.condit_type_id = ct.condit_type_id
		LEFT JOIN CONDIT_MILIEU cm ON p.condit_milieu_id = cm.condit_milieu_id
		LEFT JOIN CONSENT_TYPE consent ON p.consent_type_id = consent.consent_type_id
		LEFT JOIN TRANSPORTEUR tr ON p.transporteur_id = tr.transporteur_id
		LEFT JOIN COLLABORATEUR coco ON p.operateur_id = coco.collaborateur_id
		LEFT JOIN MALADIE m on p.maladie_id = m.maladie_id 
		LEFT JOIN PATIENT pat ON m.patient_id = pat.patient_id 
		LEFT JOIN OBJET_STATUT os ON ec.objet_statut_id = os.objet_statut_id 
		WHERE p.banque_id = b.banque_id AND p.nature_id = n.nature_id AND ent.ENTITE_ID = 2 AND p.prelevement_id = id;
		
END$$
