ALTER TABLE `TABLE_ANNOTATION` ADD COLUMN `INLINE_DISPLAY` bit(1) DEFAULT b'0';
ALTER TABLE `CONTEXTE` ADD COLUMN libelle char(50) null;
ALTER TABLE `PRELEVEMENT` ADD COLUMN date_peremption datetime null;
UPDATE `CONTEXTE` SET nom='DEFAUT',libelle='Anatomopathologie' WHERE contexte_id = 1;
UPDATE `CONTEXTE` SET nom='SEROLOGIE',libelle='Sérologie' WHERE contexte_id = 2;
UPDATE `CONTEXTE` SET nom='BTO',libelle='Banque de tissus osseux' WHERE contexte_id = 3;
UPDATE `CONTEXTE` SET nom='OFSEP',libelle='OFSEP' WHERE contexte_id = 4;