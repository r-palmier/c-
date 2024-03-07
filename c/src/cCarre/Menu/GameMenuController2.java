package cCarre.Menu;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cCarre.MainMenu;
import cCarre.AffichageMap.model.Level;
import cCarre.AffichageMap.view.MainController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GameMenuController2 {
	@FXML 
	public Button GoToBaseMenu;
	
	@FXML
    private StackPane gamePreview;

	MediaPlayer mediaPlayer;
	
	// FileChooser de l'import
	FileChooser fileChooser = new FileChooser();
	
	// taille de l'�cran
	private Rectangle2D screenBounds = Screen.getPrimary().getBounds();
	
	// Tableau des maps
	ArrayList<String> mapList = new ArrayList<String>();
	public int indexMap = 0;
	
	// Vars preview
	int elementSize = 60;
	
	@FXML
	private void initialize() throws IOException, ParseException {
		
		mapList.add("Intro");
		mapList.add("Map2.6");
		handlePreview();
	}
	public void nextLevel() throws IOException, ParseException {
		indexMap += 1;
		if (indexMap > mapList.size() -1) {
			indexMap = 0;

		}
		handlePreview();
	}
	
	public void prevLevel() throws IOException, ParseException {
		indexMap -= 1;
		
		if (indexMap < 0) {
			indexMap = mapList.size() -1;
		}
		handlePreview();
	}
	
	
	public void handlePreview() throws IOException, ParseException {
		// Settings de la preview
		Level.setJsonLevel(readJSON(mapList.get(indexMap)));
		Level.setPreview(true);
		Level.setElemHeight((int) ((screenBounds.getHeight()/(1080/elementSize)) / 8) * 6);
		
		// Load root layout from fxml file.
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainMenu.class.getResource("./AffichageMap/view/mainLayout.fxml"));
		AnchorPane game = (AnchorPane) loader.load();
		
		// Place le FXML sur la HBox
		gamePreview.getChildren().add(game);
		gamePreview.setStyle("-fx-border-color: gray; -fx-border-width: 3;");
		
		// Button play
		Button play = new Button();
		play.setText("Play");
		play.setPrefWidth(200);
		play.setFont(Font.font ("Courier new", 48));
		play.setPrefHeight(100);
		play.setStyle("-fx-font-weight: bold");
		play.setOnAction(e -> {
			try {
				LaunchGame(e);
			} catch (IOException | ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		play.setOnMouseMoved(e -> {
			play.setText("Play");
			play.setPrefWidth(200);
			play.setFont(Font.font ("Courier new", 48));
			play.setPrefHeight(100);
			play.setStyle("-fx-font-weight: bold");
		});
		
		play.setOnMousePressed(e -> {
			play.setText("Play");
			play.setPrefWidth(200);
			play.setFont(Font.font ("Courier new", 48));
			play.setPrefHeight(100);
			play.setStyle("-fx-font-weight: bold");
		});
		gamePreview.getChildren().add(play);
	}
	
	
	public void GoToBaseMenu(ActionEvent event) throws IOException {
		playSound("Click_Menus.wav");
		Parent tableViewParent = FXMLLoader.load(getClass().getResource("BaseMenu.fxml"));
		Scene tableViewScene = new Scene(tableViewParent);
		
		Stage window = (Stage) (((Node) event.getSource()).getScene().getWindow());
		
		window.setScene(tableViewScene);
		window.setMaximized(true);
		window.show();
	}
	
	public void LaunchGame(ActionEvent event) throws IOException, ParseException {
		// D�finis la map � utiliser, attend un JSONArray
		Level.setJsonLevel(readJSON(mapList.get(indexMap)));
		Level.setPreview(false);
		
		// Load root layout from fxml file.
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainMenu.class.getResource("./AffichageMap/view/mainLayout.fxml"));
		Pane game = (Pane) loader.load();
		
		Stage window = (Stage) (((Node) event.getSource()).getScene().getWindow());

		// Show the scene containing the root layout.
        Scene scene = new Scene(game);
        window.setScene(scene);
        
        MainController controller = loader.getController();
        
        window.setMaximized(true);
		window.show();
		
		scene.setOnKeyReleased(e -> {
			if(e.getCode() == KeyCode.SPACE) {
				controller.stopJump();
			}
		});

		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			
			public void handle(KeyEvent event) {
				switch(event.getCode()) {
				
				case ESCAPE:
					try {
						controller.pause();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
				case SPACE:
					controller.startJump();
					break;
				default:
					break;
				}
			}
		});
	}
	public void GoToEditLevel(ActionEvent event) throws IOException {
		playSound("Click_Menus.wav");
		Parent tableViewParent = FXMLLoader.load(getClass().getResource("../genmap/view/genLayout.fxml"));
		Scene tableViewScene = new Scene(tableViewParent);
		
		Stage window = (Stage) (((Node) event.getSource()).getScene().getWindow());
		
		window.setScene(tableViewScene);
		window.setMaximized(true);
		window.show();
	}
	
	@FXML
    void ImportGame(ActionEvent event) throws IOException, ParseException {
		JSONParser parser = new JSONParser();

    	// Demande � l'utilisateur de choisir un fichier
        File file = fileChooser.showOpenDialog(new Stage());

        if(file != null) {
        	Reader reader = new FileReader(file);
        	
        	JSONObject jsonObject = (JSONObject) parser.parse(reader); // parse
        	
        	// D�finis la map � utiliser, attend un JSONArray
        	Level.setJsonLevel(jsonObject);
        	Level.setPreview(false);
        	
        	// Load root layout from fxml file.
        	FXMLLoader loader = new FXMLLoader();
        	loader.setLocation(MainMenu.class.getResource("./AffichageMap/view/mainLayout.fxml"));
        	Pane BaseMenu = (Pane) loader.load();
        	
        	Stage window = (Stage) (((Node) event.getSource()).getScene().getWindow());
        	
        	// Show the scene containing the root layout.
        	Scene scene = new Scene(BaseMenu);
        	window.setScene(scene);
        	
        	MainController controller = loader.getController();
        	
        	window.setMaximized(true);
        	window.show();
        	
        	scene.setOnKeyReleased(e -> {
    			if(e.getCode() == KeyCode.SPACE) {
    				controller.stopJump();
    			}
    		});

    		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
    			
    			public void handle(KeyEvent event) {
    				switch(event.getCode()) {
    				
    				case ESCAPE:
    					try {
    						controller.pause();
    						
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
    					
    					break;
    				case SPACE:
    					controller.startJump();
    					break;
    				default:
    					break;
    				}
    			}
    		});
        	
        }
    }
	
	
	/**
	 * Fais jouer un son se trouvant dans le dossier resources/audio/
	 * @param name Le nom du fichier (avec l'extension)
	 * @param volume Le volume de 0 � 10
	 */
	private void playSound(String name) {
		File file = new File("resources/audio/" + name);
		
		Media media = new Media(file.toURI().toString());
		
		mediaPlayer = new MediaPlayer(media);
		
		mediaPlayer.setVolume(5.0 / 10);
		mediaPlayer.play();
	}
	
	/**
	 * Renvoie une map se trouvant dans le dossier resources/maps/ en Objet JSON
	 * @param name Le nom du fichier de la map, sans le .json
	 * @return Le JSONObject de la map
	 * @throws IOException
	 * @throws ParseException
	 */
	private JSONObject readJSON(String name) throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		
		File fileJson = new File("resources/maps/" + name + ".json");
		Reader reader = new FileReader(fileJson);
		
		JSONObject jsonObject = (JSONObject) parser.parse(reader); // parse
    	
    	return jsonObject; 
	}
}