package main.java.models;

import java.time.LocalDate;

public class Contact {
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String adresse;
    private LocalDate dateNaissance;
    private String categorie;
    private String notes;
    
    // Constructeur par défaut
    public Contact() {
    }
    
    // Constructeur avec paramètres
    public Contact(String nom, String prenom, String telephone, String email, 
                  String adresse, LocalDate dateNaissance, String categorie, String notes) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
        this.dateNaissance = dateNaissance;
        this.categorie = categorie;
        this.notes = notes;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
    public LocalDate getDateNaissance() {
        return dateNaissance;
    }
    
    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
    
    public String getCategorie() {
        return categorie;
    }
    
    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    // Méthode toString pour l'affichage
    @Override
    public String toString() {
        return nom + " " + prenom;
    }
}