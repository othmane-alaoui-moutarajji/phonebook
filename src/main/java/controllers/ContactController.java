package controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Contact;
import models.ContactDAO;
import utils.AlertUtils;

import java.util.Arrays;
import java.util.List;

public class ContactController {

    @FXML
    private TextField nomField;
    
    @FXML
    private TextField prenomField;
    
    @FXML
    private TextField telephoneField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextArea adresseArea;
    
    @FXML
    private DatePicker dateNaissancePicker;
    
    @FXML
    private ComboBox<String> categorieField;
    
    @FXML
    private TextArea notesArea;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    private Contact contact;
    private boolean isNewContact;
    private boolean readOnly;
    private MainController mainController;
    private ContactDAO contactDAO;
    private boolean contactSaved = false;
    
    @FXML
    private void initialize() {
        // Initialiser la liste des catégories
        List<String> categories = Arrays.asList("", "Famille", "Amis", "Travail", "Autre");
        categorieField.setItems(FXCollections.observableArrayList(categories));
        categorieField.setEditable(true);
        
        // Configurer les actions des boutons
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> handleCancel());
    }
    
    /**
     * Initialise les données pour le mode normal (avec MainController)
     */
    public void initData(Contact contact, boolean isNewContact, MainController mainController) {
        this.contact = contact;
        this.isNewContact = isNewContact;
        this.mainController = mainController;
        this.readOnly = false;
        this.contactDAO = null;
        
        initializeFields();
    }
    
    /**
     * Initialise les données pour le mode avec DAO direct
     */
    public void initData(Contact contact, boolean isNewContact, boolean readOnly, ContactDAO contactDAO) {
        this.contact = contact;
        this.isNewContact = isNewContact;
        this.readOnly = readOnly;
        this.contactDAO = contactDAO;
        this.mainController = null;
        
        initializeFields();
        
        // Configurer le mode lecture seule si nécessaire
        if (readOnly) {
            configureReadOnlyMode();
        }
        
        if (contactDAO != null) {
            try {
            	addPredefinedCategories();
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement des catégories: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
    }
    private void addPredefinedCategories() {
        try {
            System.out.println("Ajout de catégories prédéfinies...");
            
            // Liste de catégories prédéfinies
            List<String> predefinedCategories = Arrays.asList(
                "Famille",
                "Amis",
                "Travail",
                "École",
                "Loisirs",
                "Santé",
                "Autres"
            );
         // Ajouter les catégories au ComboBox
            categorieField.setItems(FXCollections.observableArrayList(predefinedCategories));
            categorieField.setEditable(false);
            
            System.out.println("Catégories prédéfinies ajoutées au ComboBox");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout des catégories prédéfinies: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialise les données pour le mode lecture seule (ancienne méthode)
     */
    public void initDataReadOnly(Contact contact) {
        this.contact = contact;
        this.isNewContact = false;
        this.readOnly = true;
        this.mainController = null;
        this.contactDAO = null;
        
        initializeFields();
        configureReadOnlyMode();
    }
    
    /**
     * Initialise les champs avec les données du contact
     */
    private void initializeFields() {
        // Remplir les champs avec les données du contact
        if (contact != null) {
            nomField.setText(contact.getNom());
            prenomField.setText(contact.getPrenom());
            telephoneField.setText(contact.getTelephone());
            emailField.setText(contact.getEmail());
            adresseArea.setText(contact.getAdresse());
            
            if (contact.getDateNaissance() != null) {
                dateNaissancePicker.setValue(contact.getDateNaissance());
            }
            
            categorieField.setValue(contact.getCategorie());
            notesArea.setText(contact.getNotes());
        }
        
        // Configurer le titre de la fenêtre après que la scène soit configurée
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) nomField.getScene().getWindow();
                if (stage != null) {
                    if (isNewContact) {
                        stage.setTitle("Nouveau contact");
                    } else if (readOnly) {
                        stage.setTitle("Détails du contact: " + contact.getNom() + " " + contact.getPrenom());
                    } else {
                        stage.setTitle("Modifier contact: " + contact.getNom() + " " + contact.getPrenom());
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la configuration du titre: " + e.getMessage());
            }
        });
    }
    
    /**
     * Configure le mode lecture seule
     */
    private void configureReadOnlyMode() {
        nomField.setEditable(false);
        prenomField.setEditable(false);
        telephoneField.setEditable(false);
        emailField.setEditable(false);
        adresseArea.setEditable(false);
        dateNaissancePicker.setDisable(true);
        categorieField.setDisable(true);
        notesArea.setEditable(false);
        
        // Cacher le bouton Enregistrer
        saveButton.setVisible(false);
        
        // Renommer le bouton Annuler en Fermer
        cancelButton.setText("Fermer");
    }
    
    @FXML
    private void handleSave() {
        try {
            // Vérifier les champs obligatoires
            if (nomField.getText().isEmpty() || prenomField.getText().isEmpty()) {
                AlertUtils.showWarningAlert("Champs obligatoires", null, 
                                          "Les champs Nom et Prénom sont obligatoires.");
                return;
            }
            
            // Mettre à jour l'objet contact
            contact.setNom(nomField.getText());
            contact.setPrenom(prenomField.getText());
            contact.setTelephone(telephoneField.getText());
            contact.setEmail(emailField.getText());
            contact.setAdresse(adresseArea.getText());
            contact.setDateNaissance(dateNaissancePicker.getValue());
            contact.setCategorie(categorieField.getValue());
            contact.setNotes(notesArea.getText());
            
            // Sauvegarder le contact
            boolean success;
            
            if (mainController != null) {
                // Utiliser le MainController
                if (isNewContact) {
                    success = mainController.addContact(contact);
                } else {
                    success = mainController.updateContact(contact);
                }
            } else if (contactDAO != null) {
                // Utiliser directement le DAO
                if (isNewContact) {
                    success = contactDAO.addContact(contact);
                } else {
                    success = contactDAO.updateContact(contact);
                }
            } else {
                // Aucune méthode de sauvegarde disponible
                AlertUtils.showErrorAlert("Erreur", null, 
                                        "Aucune méthode de sauvegarde disponible.");
                return;
            }
            
            if (success) {
                // Marquer comme sauvegardé et fermer la fenêtre
                contactSaved = true;
                closeWindow();
            } else {
                AlertUtils.showErrorAlert("Erreur", null, 
                                        "Une erreur est survenue lors de la sauvegarde du contact.");
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showErrorAlert("Erreur", "Erreur de sauvegarde", 
                                    "Une erreur est survenue lors de la sauvegarde du contact: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Indique si le contact a été sauvegardé
     * @return true si le contact a été sauvegardé, false sinon
     */
    public boolean isContactSaved() {
        return contactSaved;
    }
}