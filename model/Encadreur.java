/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author ASUS
 */
public class Encadreur {
    
    private int idEncadreur;
    private String nom;
    private String prenom;
    private String grade;
    private String email;
    
    public Encadreur() {}

    public Encadreur(int idEncadreur, String nom, String prenom,String grade, String email) {
        this.idEncadreur = idEncadreur;
        this.nom = nom;
        this.prenom = prenom;
        this.grade = grade;
        this.email = email;
    }

    public int getIdEncadreur() { return idEncadreur; }
    public void setIdEncadreur(int idEncadreur) { this.idEncadreur = idEncadreur; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
     public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    
    
    
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade= grade; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Encadreur{" +
               "idEncadreur=" + idEncadreur +
               ", name='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", grade='" + grade + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}
