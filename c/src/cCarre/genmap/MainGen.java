package cCarre.genmap;
	
import java.io.IOException;

import cCarre.genmap.view.GenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class MainGen extends Application {
	private Stage primaryStage;
	private VBox mainLayout;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Editeur de Map");
		this.primaryStage.setMaximized(true);
		
		// Initialisation et ouverture de la fen�tre
		initMainLayout();
	}
	
	public void initMainLayout() {
		try {
			// Chargement du layout principal
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainGen.class.getResource("view/GenLayout.fxml"));
			mainLayout = (VBox) loader.load();
			
			// Affichage de la sc�ne contenant le layout pr�c�demment charg�
			Scene scene = new Scene(mainLayout);
			primaryStage.setScene(scene);
			
			// Mise en relation avec le controller
			GenController controller = loader.getController();
			
			primaryStage.show();
						
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
