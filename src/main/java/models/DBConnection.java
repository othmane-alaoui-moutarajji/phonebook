package main.java.models;

import java.sql.*;
import main.java.utils.AlertUtils;

public class DBConnection {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/contact_manager";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    private static Connection connection;
    
    // Méthode pour obtenir une connexion à la base de données
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Charger le pilote JDBC
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Établir la connexion
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Connexion à la base de données établie");
            }
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur: Pilote JDBC MySQL introuvable");
            e.printStackTrace();
            AlertUtils.showErrorAlert("Erreur de connexion", "Pilote JDBC introuvable", 
                                     "Veuillez vérifier que le pilote MySQL JDBC est dans le classpath.");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
            
            // Tenter de créer la base de données si elle n'existe pas
            if (e.getMessage().contains("Unknown database")) {
                createDatabase();
                return getConnection(); // Réessayer après création
            } else {
                AlertUtils.showErrorAlert("Erreur de connexion", "Impossible de se connecter à la base de données", 
                                         "Vérifiez que le serveur MySQL est en cours d'exécution et que les informations de connexion sont correctes.");
            }
        }
        return null;
    }
    
    // Méthode pour créer la base de données si elle n'existe pas
    private static void createDatabase() {
        try {
            // Connexion sans spécifier de base de données
            Connection tempConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306", DB_USER, DB_PASSWORD);
            
            Statement stmt = tempConnection.createStatement();
            
            // Créer la base de données
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS contact_manager");
            System.out.println("Base de données contact_manager créée");
            
            // Sélectionner la base de données
            stmt.executeUpdate("USE contact_manager");
            
            // Créer la table contacts
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS contacts (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nom VARCHAR(50) NOT NULL, " +
                "prenom VARCHAR(50) NOT NULL, " +
                "telephone VARCHAR(20), " +
                "email VARCHAR(100), " +
                "adresse TEXT, " +
                "date_naissance DATE, " +
                "categorie VARCHAR(50), " +
                "notes TEXT)");
            System.out.println("Table contacts créée");
            
            // Insérer quelques données de test
            stmt.executeUpdate("INSERT INTO contacts (nom, prenom, telephone, email, categorie) VALUES " +
                "('Dupont', 'Jean', '0612345678', 'jean.dupont@email.com', 'Famille'), " +
                "('Martin', 'Sophie', '0687654321', 'sophie.martin@email.com', 'Amis'), " +
                "('Dubois', 'Pierre', '0654321987', 'pierre.dubois@email.com', 'Travail')");
            System.out.println("Données de test insérées");
            
            stmt.close();
            tempConnection.close();
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la base de données: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showErrorAlert("Erreur", "Impossible de créer la base de données", 
                                     "Une erreur est survenue lors de la création de la base de données: " + e.getMessage());
        }
    }
    
    // Méthode pour fermer la connexion
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion à la base de données fermée");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Méthode pour exécuter une requête de mise à jour (INSERT, UPDATE, DELETE)
    public static int executeUpdate(String sql, Object... params) {
        try {
            Connection conn = getConnection();
            if (conn == null) return -1;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            // Définir les paramètres
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            int result = stmt.executeUpdate();
            stmt.close();
            return result;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'exécution de la requête: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
    
    // Méthode pour exécuter une requête de sélection (SELECT)
    public static ResultSet executeQuery(String sql, Object... params) {
        try {
            Connection conn = getConnection();
            if (conn == null) return null;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            // Définir les paramètres
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            return stmt.executeQuery();
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'exécution de la requête: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}