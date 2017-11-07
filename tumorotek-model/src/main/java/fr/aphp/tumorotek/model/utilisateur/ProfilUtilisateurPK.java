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
package fr.aphp.tumorotek.model.utilisateur;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import fr.aphp.tumorotek.model.contexte.Banque;

/**
 * 
 * Embedded Id pour la table PROFIL_UTILISATEUR.
 * Classe créée le 22/09/09.
 * 
 * @author Mathieu BARTHELEMY
 * @see http://boris.kirzner.info/blog/archives/2008/07/19/
 * hibernate-annotations-the-many-to-many-association-with-composite-key/
 * @version 2.0
 * 
 */
@Embeddable
public class ProfilUtilisateurPK implements Serializable {
 
	private static final long serialVersionUID = -5029452269080724892L;
	
	private Profil profil;
    private Utilisateur utilisateur;
    private Banque banque;

	/** Constructeur par défaut. */
    public ProfilUtilisateurPK() {
    }
    
    public ProfilUtilisateurPK(Profil prof, Utilisateur util,
    		Banque bank) {
    	this.profil = prof;
    	this.utilisateur = util;
    	this.banque = bank;
    }
 
	@ManyToOne(targetEntity = Profil.class)
    public Profil getProfil() {
        return profil;
    }
 
    public void setProfil(Profil prof) {
        this.profil = prof;
    }
 
	@ManyToOne
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
 
    public void setUtilisateur(Utilisateur user) {
        this.utilisateur = user;
    }
    
	@ManyToOne
    public Banque getBanque() {
		return banque;
	}

	public void setBanque(Banque bank) {
		this.banque = bank;
	}
 
	/**
	 * 2 PKs sont considérés comme égales si elles sont composees 
	 * des mêmes clés.
	 * @param obj est la PK à tester.
	 * @return true si les PK sont egales.
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) {
			return true;
		}
		if ((obj == null) || obj.getClass() != this.getClass()) {
			return false;
		}
		ProfilUtilisateurPK test = (ProfilUtilisateurPK) obj;
		return ((this.utilisateur == test.utilisateur 
				|| (this.utilisateur != null 
						&& this.utilisateur.equals(test.utilisateur))) 
				&& (this.banque == test.banque || (this.banque != null 
						&& this.banque.equals(test.banque))) 
				&& (this.profil == test.profil || (this.profil != null 
						&& this.profil.equals(test.profil))));
	}
 
	/**
	 * Le hashcode est calculé sur les clés.
	 * @return la valeur du hashcode.
	 */
	@Override
	public int hashCode() {
		
		int hash = 7;
		int hashUtilisateur = 0;
		int hashBanque = 0;
		int hashProfil = 0;
		
		if (this.utilisateur != null) {
			hashUtilisateur = this.utilisateur.hashCode();
		}
		if (this.banque != null) {
			hashBanque = this.banque.hashCode();
		}
		if (this.profil != null) {
			hashProfil = this.profil.hashCode();
		}

		
		hash = 7 * hash + hashUtilisateur;
		hash = 7 * hash + hashBanque;
		hash = 7 * hash + hashProfil;
		
		return hash;
		
	}
	
	/**
	 * Méthode surchargeant le toString() de l'objet.
	 */
	@Override
	public String toString() {
		if (this.profil != null 
				&& this.utilisateur != null
				&& this.banque != null) {
			return "{" + profil.toString() + " (Profil), " 
					+ utilisateur.toString() + " (Utilisateur), " 
					+ banque.toString() + " (Banque)}";
		} else {
			return "{Empty ProfilUtilisateurPK}";
		}
	}
}
