/*============================================*/
/* Script de migration de la version 2.2.0    */
/* à la version 2.2.0 OFSEP      	          */
/* Tumorotek version : 2.2.0		    */
/* Created on: 17/06/2009		              */ 
/*============================================*/

/*==============================================================*/
/* Nettoyage tables CIM version 1.11                            */
/*==============================================================*/
insert into OPERATION_TYPE values (24,'ExportOFSEP',0);

select '******************************';
select 'Migration version 2.2.0 OFSEP terminee';
select '******************************';
