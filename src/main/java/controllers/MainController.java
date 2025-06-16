package main.java.controllers;

import main.java.models.Contact;
import main.java.models.ContactDAO;
import main.java.models.MySQLContactDAO;
import main.java.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;

public class MainController {

    @FXML
    private TableView<Contact> contactTable;
    
    @FXML
    private TableColumn<Contact, Integer> idColumn;
    
    @FXML
    private TableColumn<Contact, String> nomColumn;
    
    @FXML
    private TableColumn<Contact, String> prenomColumn;
    
    @FXML
    private TableColumn<Contact, String> telephoneColumn;
    
    @FXML
    private TableColumn<Contact, String> emailColumn;
    
    @FXML
    private TableColumn<Contact, String> categorieColumn;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> categoryFilter;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Button newButton;
    
    @FXML
    private Button editButton;
    
    @FXML
    private Button deleteButton;
    
    @FXML
    private Button viewButton;
    
    @FXML
    private Button refreshButton;
    
    private ContactDAO contactDAO;
    

    @FXML
    private void initialize() {
        try {
            // Initialiser la DAO
            contactDAO = new MySQLContactDAO();
            
            // Configurer les colonnes de la TableView
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
            prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
            telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
            
            System.out.println("Colonnes de la TableView configurées");
            
            // Configurer la sélection dans la table
            contactTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    boolean hasSelection = (newValue != null);
                    editButton.setDisable(!hasSelection);
                    deleteButton.setDisable(!hasSelection);
                    viewButton.setDisable(!hasSelection);
                });
            
            // Initialiser le filtre de catégorie
            initCategoryFilter();
            
            // Configurer la recherche
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.isEmpty()) {
                    loadContacts();
                } else {
                    searchContacts(newValue);
                }
            });
            
            // Configurer le gestionnaire d'événements pour le ComboBox de catégories
            categoryFilter.setOnAction(event -> {
                String selectedCategory = categoryFilter.getSelectionModel().getSelectedItem();
                System.out.println("Catégorie sélectionnée: " + selectedCategory);
                
                if (selectedCategory != null) {
                    if (selectedCategory.equals("Toutes les catégories")) {
                        loadContacts(); // Charger tous les contacts
                    } else {
                        filterByCategory(selectedCategory); // Filtrer par la catégorie sélectionnée
                    }
                }
            });
            
            // Désactiver les boutons au démarrage
            editButton.setDisable(true);
            deleteButton.setDisable(true);
            viewButton.setDisable(true);
            
            // Charger les contacts
            loadContacts();
            
            System.out.println("Contrôleur initialisé");
            statusLabel.setText("Application démarrée avec succès");
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
            statusLabel.setText("Erreur d'initialisation");
            AlertUtils.showErrorAlert("Erreur", "Erreur d'initialisation", 
                                     "Une erreur est survenue lors de l'initialisation de l'application: " + e.getMessage());
        }
    }

    private void loadContacts() {
        try {
            List<Contact> contacts = contactDAO.getAllContacts();
            contactTable.getItems().setAll(contacts);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des contacts: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showErrorAlert("Erreur", "Erreur de chargement", 
                                     "Une erreur est survenue lors du chargement des contacts.");
        }
    }
    
    private void initCategoryFilter() {
        try {
            List<String> categories = contactDAO.getAllCategories();
            categories.add(0, "Toutes les catégories");
            categoryFilter.setItems(FXCollections.observableArrayList(categories));
            categoryFilter.getSelectionModel().selectFirst();
            System.out.println("Filtre de catégorie initialisé");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation du filtre: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void searchContacts(String searchTerm) {
        try {
            List<Contact> searchResults = contactDAO.searchContacts(searchTerm);
            contactTable.setItems(FXCollections.observableArrayList(searchResults));
            statusLabel.setText(searchResults.size() + " contacts trouvés pour \"" + searchTerm + "\"");
            System.out.println("Recherche: " + searchTerm + " - " + searchResults.size() + " résultats");
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche: " + e.getMessage());
            e.printStackTrace();
            statusLabel.setText("Erreur lors de la recherche");
        }
    }
    
    private void filterByCategory(String category) {
        try {
            List<Contact> filteredContacts = contactDAO.filterByCategory(category);
            contactTable.setItems(FXCollections.observableArrayList(filteredContacts));
            statusLabel.setText(filteredContacts.size() + " contacts dans la catégorie " + category);
            System.out.println("Filtrage par catégorie: " + category);
        } catch (Exception e) {
            System.err.println("Erreur lors du filtrage par catégorie: " + e.getMessage());
            e.printStackTrace();
            statusLabel.setText("Erreur lors du filtrage");
        }
    }
    
    public boolean addContact(Contact contact) {
        try {
            boolean success = contactDAO.addContact(contact);
            if (success) {
                loadContacts();
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout du contact: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateContact(Contact contact) {
        try {
            boolean success = contactDAO.updateContact(contact);
            if (success) {
                loadContacts();
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du contact: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @FXML
    private void handleNewContact() {
        try {
            // Créer un nouveau contact vide
            Contact newContact = new Contact();
            
            // Ouvrir la fenêtre de détails en mode création
            openContactDetails(newContact, true);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la création d'un nouveau contact: " + e.getMessage());
            e.printStackTrace();
            statusLabel.setText("Erreur lors de la création d'un contact");
            AlertUtils.showErrorAlert("Erreur", "Erreur de création", 
                                     "Une erreur est survenue lors de la création d'un nouveau contact.");
        }
    }
    
    @FXML
    private void handleEditContact() {
        Contact selectedContact = contactTable.getSelectionModel().getSelectedItem();
        
        if (selectedContact == null) {
            AlertUtils.showWarningAlert("Aucune sélection", "Aucun contact sélectionné", 
                                       "Veuillez sélectionner un contact à modifier.");
            return;
        }
        
        try {
            // Ouvrir la fenêtre de détails en mode édition
            openContactDetails(selectedContact, false);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la modification du contact: " + e.getMessage());
            e.printStackTrace();
            statusLabel.setText("Erreur lors de la modification du contact");
            AlertUtils.showErrorAlert("Erreur", "Erreur de modification", 
                                     "Une erreur est survenue lors de la modification du contact.");
        }
    }
    
    @FXML
    private void handleDeleteContact() {
        Contact selectedContact = contactTable.getSelectionModel().getSelectedItem();
        
        if (selectedContact == null) {
            AlertUtils.showWarningAlert("Aucune sélection", "Aucun contact sélectionné", 
                                       "Veuillez sélectionner un contact à supprimer.");
            return;
        }
        
        boolean confirm = AlertUtils.showConfirmationAlert("Confirmation de suppression", 
                                                         "Supprimer le contact", 
                                                         "Êtes-vous sûr de vouloir supprimer " + 
                                                         selectedContact.getPrenom() + " " + 
                                                         selectedContact.getNom() + " ?");
        
        if (confirm) {
            try {
                System.out.println("Suppression du contact ID: " + selectedContact.getId());
                boolean success = contactDAO.deleteContact(selectedContact.getId());
                
                if (success) {
                    loadContacts();
                    initCategoryFilter();
                    
                    AlertUtils.showInfoAlert("Suppression réussie", null, 
                                            "Le contact a été supprimé avec succès.");
                    statusLabel.setText("Contact supprimé avec succès");
                } else {
                    AlertUtils.showErrorAlert("Erreur de suppression", null, 
                                             "Impossible de supprimer le contact.");
                    statusLabel.setText("Erreur lors de la suppression du contact");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la suppression: " + e.getMessage());
                e.printStackTrace();
                statusLabel.setText("Erreur lors de la suppression");
                AlertUtils.showErrorAlert("Erreur", "Erreur de suppression", 
                                         "Une erreur est survenue lors de la suppression du contact.");
            }
        } else {
            System.out.println("Suppression annulée par l'utilisateur");
        }
    }
    
    @FXML
    private void handleViewContact() {
        Contact selectedContact = contactTable.getSelectionModel().getSelectedItem();
        
        if (selectedContact == null) {
            AlertUtils.showWarningAlert("Aucune sélection", "Aucun contact sélectionné", 
                                       "Veuillez sélectionner un contact à afficher.");
            return;
        }
        
        try {
            // Ouvrir la fenêtre de détails en mode lecture seule
            openContactDetails(selectedContact, false, true);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage du contact: " + e.getMessage());
            e.printStackTrace();
            statusLabel.setText("Erreur lors de l'affichage du contact");
            AlertUtils.showErrorAlert("Erreur", "Erreur d'affichage", 
                                     "Une erreur est survenue lors de l'affichage du contact.");
        }
    }
    
    private void openContactDetails(Contact contact, boolean isNewContact) {
        openContactDetails(contact, isNewContact, false);
    }
    
    private void openContactDetails(Contact contact, boolean isNewContact, boolean readOnly) {
        try {
            // Charger le FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/views/ContactView.fxml"));
            Parent root = loader.load();
            
            // Obtenir le contrôleur
            ContactController controller = loader.getController();
            
            // Initialiser le contrôleur avec le contact et le mode
            controller.initData(contact, isNewContact, readOnly, contactDAO);
            
            // Configurer la fenêtre
            Stage stage = new Stage();
            stage.setTitle(isNewContact ? "Nouveau contact" : (readOnly ? "Détails du contact" : "Modifier le contact"));
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            
            // Définir un gestionnaire pour la fermeture de la fenêtre
            stage.setOnHidden(e -> {
                if (controller.isContactSaved()) {
                    loadContacts();
                    initCategoryFilter();
                    statusLabel.setText("Contact " + (isNewContact ? "ajouté" : "mis à jour") + " avec succès");
                }
            });
            
            // Afficher la fenêtre
            stage.showAndWait();
            
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture de la fenêtre de détails: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showErrorAlert("Erreur", "Erreur d'interface", 
                                     "Une erreur est survenue lors de l'ouverture de la fenêtre de détails.");
        }
    }
    
    @FXML
    private void handleRefresh() {
        loadContacts();
        initCategoryFilter();
        searchField.clear();
        statusLabel.setText("Liste des contacts actualisée");
    }
}