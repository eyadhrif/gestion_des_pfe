package model;

public class Etudiant {
    private int idEtudiant;
    private String nom;
    private String prenom;
    private String email;
    private String classe;

    public Etudiant() {}

    public Etudiant(int idEtudiant, String nom, String prenom, String email, String classe) {
        this.idEtudiant = idEtudiant;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.classe = classe;
    }

    // Getters et Setters (Tes méthodes existantes sont parfaites)
    public int getIdEtudiant() { return idEtudiant; }
    public void setIdEtudiant(int idEtudiant) { this.idEtudiant = idEtudiant; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }

    @Override
    public String toString() {
        return nom + " " + prenom;
    }
}