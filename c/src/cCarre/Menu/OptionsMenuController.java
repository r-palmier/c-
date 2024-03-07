package cCarre.Menu;

import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class OptionsMenuController {
	@FXML public Button GoToBaseMenu;
	
	// L'objet qui joue la musique, Ã  importer
	MediaPlayer mediaPlayer;
	
	public void GoToBaseMenu(ActionEvent event) throws IOException {
		playSound("Click_Menus.wav");
		Parent tableViewParent = FXMLLoader.load(getClass().getResource("BaseMenu.fxml"));
		Scene tableViewScene = new Scene(tableViewParent);
		
		Stage window = (Stage) (((Node) event.getSource()).getScene().getWindow());
		
		window.setScene(tableViewScene);
		window.setMaximized(true);
		window.show();
	}
	

	
	/**
	 * Fais jouer un son se trouvant dans le dossier resources/audio/ 
	 * @param name Le nom du fichier (avec l'extension)
	 * @param volume Le volume de 0 Ã  10
	 */
	private void playSound(String name) {
		// Fichier Ã  mettre dans le dossier indiquÃ© en dessous, Ã  partir de la raÃ§ine du projet
		File file = new File("resources/audio/" + name);
		Media media = new Media(file.toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		
		// Volume Ã  rÃ©gler en double !! 
		mediaPlayer.setVolume(7.0 / 10);
		
		// Joue le son
		mediaPlayer.play();
	}
}
