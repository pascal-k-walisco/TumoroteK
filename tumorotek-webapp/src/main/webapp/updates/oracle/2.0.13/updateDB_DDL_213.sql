-- suite au probleme de performances observé par Fabien HCLs 
-- lors récupération > 700 000 fichiers 
create index sipNipIdx on PATIENT_SIP(NIP);

-- indexes must exists
CREATE INDEX objIdx on ANNOTATION_VALEUR (OBJET_ID, CHAMP_ANNOTATION_ID)
DROP INDEX opTypeIdx; -- ON OPERATION (operation_type_id)';
DROP INDEX opEntIdx; -- ON OPERATION (entite_id)';
CREATE INDEX opObjIdx ON OPERATION (objet_id);

-- 2.0.13.1
CREATE TABLE CONSULTATION_INTF (
  	CONSULTATION_INTF_ID NUMBER(22) NOT NULL,
  	IDENTIFICATION VARCHAR2(100) NOT NULL,
	DATE_ DATE NOT NULL,
 	UTILISATEUR_ID NUMBER(22) NOT NULL,
 	EMETTEUR_IDENT VARCHAR2(100) NOT NULL,
  	constraint PK_CONSULTATION_ID primary key (CONSULTATION_INTF_ID)
);

ALTER TABLE CONSULTATION_INTF 
	ADD CONSTRAINT FK_CONSULT_INTF_UTIL_ID 
	FOREIGN KEY (UTILISATEUR_ID) 
	REFERENCES UTILISATEUR (UTILISATEUR_ID);
