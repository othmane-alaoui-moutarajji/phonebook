
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("Démarrage de l'application...");
            
            // Charger le fichier FXML principal
            // NEW, CORRECT LINE
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainView.fxml"));
            Parent root = loader.load();
            
            // Configurer la scène
            Scene scene = new Scene(root, 900, 600);
            
            // Configurer la fenêtre principale
            primaryStage.setTitle("Gestion de Contacts");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            System.out.println("Application démarrée avec succès");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage de l'application: " + e.getMessage());
            e.printStackTrace();
            
            // Créer une interface de secours en cas d'erreur
            createFallbackUI(primaryStage, e);
        }
    }
    
    private void createFallbackUI(Stage stage, Exception e) {
        try {
            // Interface de secours simple pour informer l'utilisateur
            javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(10);
            root.setPadding(new javafx.geometry.Insets(20));
            
            javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("Erreur de démarrage");
            titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: red;");
            
            javafx.scene.control.Label messageLabel = new javafx.scene.control.Label(
                "Une erreur est survenue lors du démarrage de l'application:");
            
            javafx.scene.control.TextArea errorArea = new javafx.scene.control.TextArea();
            errorArea.setText(e.toString());
            errorArea.setEditable(false);
            errorArea.setPrefHeight(200);
            
            javafx.scene.control.Button closeButton = new javafx.scene.control.Button("Fermer");
            closeButton.setOnAction(event -> stage.close());
            
            root.getChildren().addAll(titleLabel, messageLabel, errorArea, closeButton);
            
            Scene scene = new Scene(root, 600, 400);
            stage.setTitle("Erreur - Gestion de Contacts");
            stage.setScene(scene);
            stage.show();
            
        } catch (Exception ex) {
            // En cas d'erreur dans l'interface de secours, afficher dans la console
            System.err.println("Erreur fatale lors de la création de l'interface de secours: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("Lancement de l'application de gestion de contacts");
            launch(args);
        } catch (Exception e) {
            System.err.println("Erreur fatale lors du lancement de l'application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}