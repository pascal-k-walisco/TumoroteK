-- MySQL dump 10.11
--
-- Host: localhost    Database: tumo2interfacages
-- ------------------------------------------------------
-- Server version	5.0.67-0ubuntu6

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `BLOC_EXTERNE`
--

DROP TABLE IF EXISTS `BLOC_EXTERNE`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `BLOC_EXTERNE` (
  `BLOC_EXTERNE_ID` int(10) NOT NULL,
  `DOSSIER_EXTERNE_ID` int(10) NOT NULL,
  `ENTITE_ID` int(10) NOT NULL,
  `ORDRE` int(2) NOT NULL,
  PRIMARY KEY  (`BLOC_EXTERNE_ID`),
  KEY `FK_BLOC_EXTERNE_DOSSIER_EXTERNE_ID` (`DOSSIER_EXTERNE_ID`),
  CONSTRAINT `FK_BLOC_EXTERNE_DOSSIER_EXTERNE_ID` FOREIGN KEY (`DOSSIER_EXTERNE_ID`) REFERENCES `DOSSIER_EXTERNE` (`DOSSIER_EXTERNE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `BLOC_EXTERNE`
--

LOCK TABLES `BLOC_EXTERNE` WRITE;
/*!40000 ALTER TABLE `BLOC_EXTERNE` DISABLE KEYS */;
INSERT INTO `BLOC_EXTERNE` VALUES (1,1,1,1),(2,1,2,2),(3,1,3,3),(4,1,3,4),(5,6,1,1),(6,6,2,2);
/*!40000 ALTER TABLE `BLOC_EXTERNE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `DOSSIER_EXTERNE`
--

DROP TABLE IF EXISTS `DOSSIER_EXTERNE`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `DOSSIER_EXTERNE` (
  `DOSSIER_EXTERNE_ID` int(10) NOT NULL,
  `EMETTEUR_ID` int(10) NOT NULL,
  `IDENTIFICATION_DOSSIER` varchar(100) NOT NULL,
  `DATE_OPERATION` datetime default NULL,
  `OPERATION` varchar(50) default NULL,
  PRIMARY KEY  (`DOSSIER_EXTERNE_ID`),
  KEY `FK_DOSSIER_EXTERNE_EMETTEUR_ID` (`EMETTEUR_ID`),
  CONSTRAINT `FK_DOSSIER_EXTERNE_EMETTEUR_ID` FOREIGN KEY (`EMETTEUR_ID`) REFERENCES `EMETTEUR` (`EMETTEUR_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `DOSSIER_EXTERNE`
--

LOCK TABLES `DOSSIER_EXTERNE` WRITE;
/*!40000 ALTER TABLE `DOSSIER_EXTERNE` DISABLE KEYS */;
INSERT INTO `DOSSIER_EXTERNE` VALUES (1,2,'758910000','2011-08-04 08:12:00','CM'),(2,2,'10245XCDV56','2011-08-17 14:31:00','HD'),(3,2,'458710KI','2011-08-21 10:16:00','CM'),(4,3,'458710KI','2011-08-25 18:29:00','DC'),(5,2,'1000047UI','2011-09-04 08:58:00','HD'),(6,3,'HFYUIZ154','2010-10-03 16:01:00','CM');
/*!40000 ALTER TABLE `DOSSIER_EXTERNE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `EMETTEUR`
--

DROP TABLE IF EXISTS `EMETTEUR`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `EMETTEUR` (
  `EMETTEUR_ID` int(10) NOT NULL,
  `LOGICIEL_ID` int(10) NOT NULL,
  `IDENTIFICATION` varchar(50) NOT NULL,
  `SERVICE` varchar(50) default NULL,
  `OBSERVATIONS` text,
  PRIMARY KEY  (`EMETTEUR_ID`),
  KEY `FK_EMETTEUR_LOGICIEL_ID` (`LOGICIEL_ID`),
  CONSTRAINT `FK_EMETTEUR_LOGICIEL_ID` FOREIGN KEY (`LOGICIEL_ID`) REFERENCES `LOGICIEL` (`LOGICIEL_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `EMETTEUR`
--

LOCK TABLES `EMETTEUR` WRITE;
/*!40000 ALTER TABLE `EMETTEUR` DISABLE KEYS */;
INSERT INTO `EMETTEUR` VALUES (1,3,'Glims Hémato','Service Hématologie - Saint-Louis','SGL Glims pour le service d\'Hématologie de l\'Hôpital Saint-Louis'),(2,1,'Apix Anapath','Service Anatomo-Patholgie - Saint-Louis','SGL APIX pour le service d\'Anatomo-Patholgie de l\'Hôpital Saint-Louis'),(3,1,'Apix Hémato','Service Hématologie - Saint-Louis','SGL APIX pour le service d\'Hématologie de l\'Hôpital Saint-Louis');
/*!40000 ALTER TABLE `EMETTEUR` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `LOGICIEL`
--

DROP TABLE IF EXISTS `LOGICIEL`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `LOGICIEL` (
  `LOGICIEL_ID` int(10) NOT NULL,
  `NOM` varchar(50) NOT NULL,
  `EDITEUR` varchar(50) default NULL,
  `VERSION` varchar(50) default NULL,
  PRIMARY KEY  (`LOGICIEL_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `LOGICIEL`
--

LOCK TABLES `LOGICIEL` WRITE;
/*!40000 ALTER TABLE `LOGICIEL` DISABLE KEYS */;
INSERT INTO `LOGICIEL` VALUES (1,'APIX','TECHNIDATA','1.0'),(2,'DIAMIC','Infologic-santé','1.0'),(3,'Glims','MIPS','1.0');
/*!40000 ALTER TABLE `LOGICIEL` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `VALEUR_EXTERNE`
--

DROP TABLE IF EXISTS `VALEUR_EXTERNE`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `VALEUR_EXTERNE` (
  `VALEUR_EXTERNE_ID` int(10) NOT NULL,
  `BLOC_EXTERNE_ID` int(10) NOT NULL,
  `VALEUR` varchar(250) default NULL,
  `CHAMP_ENTITE_ID` int(10) default NULL,
  `CHAMP_ANNOTATION_ID` int(10) default NULL,
  PRIMARY KEY  (`VALEUR_EXTERNE_ID`),
  KEY `FK_VALEUR_EXTERNE_BLOC_EXTERNE_ID` (`BLOC_EXTERNE_ID`),
  CONSTRAINT `FK_VALEUR_EXTERNE_BLOC_EXTERNE_ID` FOREIGN KEY (`BLOC_EXTERNE_ID`) REFERENCES `BLOC_EXTERNE` (`BLOC_EXTERNE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `VALEUR_EXTERNE`
--

LOCK TABLES `VALEUR_EXTERNE` WRITE;
/*!40000 ALTER TABLE `VALEUR_EXTERNE` DISABLE KEYS */;
INSERT INTO `VALEUR_EXTERNE` VALUES (1,1,'HOUSE',3,NULL),(2,1,'GREGORY',5,NULL),(3,1,'M',6,NULL),(4,1,'ANNO',NULL,1),(5,2,'20110816102930',30,NULL),(6,2,'PRLVT_TEST',23,NULL),(7,2,'SANG',24,NULL);
/*!40000 ALTER TABLE `VALEUR_EXTERNE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `c3p0test`
--

DROP TABLE IF EXISTS `c3p0test`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `c3p0test` (
  `a` char(1) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `c3p0test`
--

LOCK TABLES `c3p0test` WRITE;
/*!40000 ALTER TABLE `c3p0test` DISABLE KEYS */;
/*!40000 ALTER TABLE `c3p0test` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-10-12 12:29:34
