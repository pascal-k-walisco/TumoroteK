-- TABLES d'ANNOTATIONS POUR LE CONTEXTE/CATALOGUE OFSEP
SET FOREIGN_KEY_CHECKS=0;
ALTER TABLE TABLE_ANNOTATION modify TABLE_ANNOTATION_ID int(10) NOT NULL auto_increment;
ALTER TABLE CHAMP_ANNOTATION modify CHAMP_ANNOTATION_ID int(10) NOT NULL auto_increment;
ALTER TABLE ANNOTATION_DEFAUT modify ANNOTATION_DEFAUT_ID int(10) NOT NULL auto_increment;
ALTER TABLE ITEM modify ITEM_ID int(10) NOT NULL auto_increment;

-- Ajout du contexte OFSEP
ALTER TABLE CONTEXTE modify CONTEXTE_ID int(10) NOT NULL auto_increment;
INSERT INTO CONTEXTE (NOM,LIBELLE) VALUES ('OFSEP','OFSEP');
ALTER TABLE CONTEXTE modify CONTEXTE_ID int(10) NOT NULL;-- enleve l'auto_increment

-- Catalogue d'annotations associé au contexte OFSEP
ALTER TABLE CATALOGUE modify CATALOGUE_ID int(10) NOT NULL auto_increment;
INSERT INTO CATALOGUE (NOM, DESCRIPTION, ICONE) VALUES('OFSEP', 'OFSEP', 'ofsep');
ALTER TABLE CATALOGUE modify CATALOGUE_ID int(10) NOT NULL;-- enleve l'auto_increment

-- Liaison CATALOGUE BTO au CONTEXTE OFSEP
INSERT INTO CATALOGUE_CONTEXTE (CATALOGUE_ID, CONTEXTE_ID) VALUES((SELECT CATALOGUE_ID FROM CATALOGUE WHERE NOM='OFSEP'), (SELECT CONTEXTE_ID FROM CONTEXTE WHERE NOM='OFSEP'));

-- Ajout dans la table NATURE
ALTER TABLE NATURE modify NATURE_ID int(10) NOT NULL auto_increment;
ALTER TABLE NATURE modify NATURE_ID int(10) NOT NULL;-- enleve l'auto_increment

-- Ajout dans la table ECHANTILLON_TYPE
ALTER TABLE ECHANTILLON_TYPE modify ECHANTILLON_TYPE_ID int(2) NOT NULL auto_increment;
ALTER TABLE ECHANTILLON_TYPE modify ECHANTILLON_TYPE_ID int(2) NOT NULL;-- enleve l'auto_increment

-- ------------------------------------------
-- Table d'annotations Patient 
-- ------------------------------------------

-- ------------------------------------------
-- table d'annotations Prélèvements 
-- ------------------------------------------

-- ------------------------------------------
-- table d'annotations Echantillons
-- ------------------------------------------

-- ------------------------------------------
-- table d'annotation Cession 
-- ------------------------------------------
