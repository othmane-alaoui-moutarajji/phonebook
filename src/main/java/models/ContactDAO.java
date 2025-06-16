package models;

import java.util.List;

public interface ContactDAO {
    
    // Récupérer tous les contacts
    List<Contact> getAllContacts();
    
    // Récupérer un contact par son ID
    Contact getContactById(int id);
    
    // Ajouter un nouveau contact
    boolean addContact(Contact contact);
    
    // Mettre à jour un contact existant
    boolean updateContact(Contact contact);
    
    // Supprimer un contact
    boolean deleteContact(int id);
    
    // Rechercher des contacts
    List<Contact> searchContacts(String searchTerm);
    
    // Filtrer les contacts par catégorie
    List<Contact> filterByCategory(String category);
    
    // Récupérer toutes les catégories distinctes
    List<String> getAllCategories();
}