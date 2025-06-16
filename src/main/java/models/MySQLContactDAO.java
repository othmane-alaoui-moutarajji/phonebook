package models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLContactDAO implements ContactDAO {
    
    @Override
    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contacts ORDER BY nom, prenom";
        
        try (ResultSet rs = DBConnection.executeQuery(sql)) {
            if (rs != null) {
                while (rs.next()) {
                    contacts.add(extractContactFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des contacts: " + e.getMessage());
            e.printStackTrace();
        }
        
        return contacts;
    }
    
    @Override
    public Contact getContactById(int id) {
        String sql = "SELECT * FROM contacts WHERE id = ?";
        
        try (ResultSet rs = DBConnection.executeQuery(sql, id)) {
            if (rs != null && rs.next()) {
                return extractContactFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du contact: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public boolean addContact(Contact contact) {
        String sql = "INSERT INTO contacts (nom, prenom, telephone, email, adresse, date_naissance, categorie, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        int result = DBConnection.executeUpdate(sql, 
                contact.getNom(), 
                contact.getPrenom(), 
                contact.getTelephone(), 
                contact.getEmail(), 
                contact.getAdresse(), 
                contact.getDateNaissance(), 
                contact.getCategorie(), 
                contact.getNotes());
        
        if (result > 0) {
            // Récupérer l'ID généré
            try (ResultSet rs = DBConnection.executeQuery("SELECT LAST_INSERT_ID()")) {
                if (rs != null && rs.next()) {
                    contact.setId(rs.getInt(1));
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la récupération de l'ID: " + e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean updateContact(Contact contact) {
        String sql = "UPDATE contacts SET nom = ?, prenom = ?, telephone = ?, email = ?, " +
                    "adresse = ?, date_naissance = ?, categorie = ?, notes = ? WHERE id = ?";
        
        int result = DBConnection.executeUpdate(sql, 
                contact.getNom(), 
                contact.getPrenom(), 
                contact.getTelephone(), 
                contact.getEmail(), 
                contact.getAdresse(), 
                contact.getDateNaissance(), 
                contact.getCategorie(), 
                contact.getNotes(), 
                contact.getId());
        
        return result > 0;
    }
    
    @Override
    public boolean deleteContact(int id) {
        String sql = "DELETE FROM contacts WHERE id = ?";
        int result = DBConnection.executeUpdate(sql, id);
        return result > 0;
    }
    
    @Override
    public List<Contact> searchContacts(String searchTerm) {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contacts WHERE " +
                    "nom LIKE ? OR prenom LIKE ? OR telephone LIKE ? OR email LIKE ? OR categorie LIKE ? " +
                    "ORDER BY nom, prenom";
        
        String term = "%" + searchTerm + "%";
        
        try (ResultSet rs = DBConnection.executeQuery(sql, term, term, term, term, term)) {
            if (rs != null) {
                while (rs.next()) {
                    contacts.add(extractContactFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des contacts: " + e.getMessage());
            e.printStackTrace();
        }
        
        return contacts;
    }
    
    @Override
    public List<Contact> filterByCategory(String category) {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contacts WHERE categorie = ? ORDER BY nom, prenom";
        
        try (ResultSet rs = DBConnection.executeQuery(sql, category)) {
            if (rs != null) {
                while (rs.next()) {
                    contacts.add(extractContactFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du filtrage des contacts: " + e.getMessage());
            e.printStackTrace();
        }
        
        return contacts;
    }
    
    @Override
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT categorie FROM contacts WHERE categorie IS NOT NULL AND categorie != '' ORDER BY categorie";
        
        try (ResultSet rs = DBConnection.executeQuery(sql)) {
            if (rs != null) {
                while (rs.next()) {
                    categories.add(rs.getString("categorie"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des catégories: " + e.getMessage());
            e.printStackTrace();
        }
        
        return categories;
    }
    
    // Méthode utilitaire pour extraire un contact d'un ResultSet
    private Contact extractContactFromResultSet(ResultSet rs) throws SQLException {
        Contact contact = new Contact();
        contact.setId(rs.getInt("id"));
        contact.setNom(rs.getString("nom"));
        contact.setPrenom(rs.getString("prenom"));
        contact.setTelephone(rs.getString("telephone"));
        contact.setEmail(rs.getString("email"));
        contact.setAdresse(rs.getString("adresse"));
        
        Date dateNaissance = rs.getDate("date_naissance");
        if (dateNaissance != null) {
            contact.setDateNaissance(dateNaissance.toLocalDate());
        }
        
        contact.setCategorie(rs.getString("categorie"));
        contact.setNotes(rs.getString("notes"));
        
        return contact;
    }
}