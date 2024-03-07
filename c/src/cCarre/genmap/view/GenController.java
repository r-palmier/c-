package cCarre.genmap.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.eventbus.Subscribe;

import cCarre.MainMenu;
import cCarre.AffichageMap.model.Level;
import cCarre.AffichageMap.view.MainController;
import cCarre.genmap.events.AddLengthGrilleEvent;
import cCarre.genmap.events.Ebus;
import cCarre.genmap.events.LaunchGameEvent;
import cCarre.genmap.events.MoveGridEvent;
import cCarre.genmap.events.PopupEvent;
import cCarre.genmap.events.RemoveLengthGrilleEvent;
import cCarre.genmap.events.RemovePartGrilleEvent;
import cCarre.genmap.model.Cell;
import cCarre.genmap.model.ToolBar;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GenController {
	// Blocs -------------------------
    @FXML
    private Label labelTest;
	
	@FXML
    private AnchorPane root;
	
	@FXML
    private HBox toolBar;
	
    @FXML
    private Button pillarBtn;

    @FXML
    private Button groundBtn;
    
    @FXML
    private Button coinBtn;
    
    @FXML
    private Button obstacleBtn;
    
    @FXML
    private Button reverseObstacleBtn;
    
    @FXML
    private Button groundSlabBtn;
    
    @FXML
    private ColorPicker backgroundColor;
    
    @FXML
    private ColorPicker coinColor;
    
    @FXML
    private ColorPicker groundColor;
    
    @FXML
    private ColorPicker obstacleColor;
    
    @FXML
    private MenuBar menuBar;
   
    @FXML
    private Button test;
    
    @FXML
    private HBox saveBar;
    
    @FXML
    private HBox upBar;
	
	// Vars --------------------------
    double initialPtX = 0;
	double initialPtY = 0;
	
	Rectangle select;

	FileChooser fileChooser = new FileChooser();
	
	
    int widthCell = (60 - 1);
    
    private GridPane grille;
	private double oldX;
	private double newX;
	private double hBar = 0;
	private boolean inTesting = false;
	private double coTest = 0;
	
	private Rectangle2D screenBounds;
	private Rectangle selected;

	private MainController mainController;
	
	@SuppressWarnings("unused")
	private double playerSpeed = 0;
	
	@FXML
	private void initialize() {
		ToolBar.init();
		
		// Set les couleurs par défaut des différents elements
        ToolBar.setGroundColor(groundColor.getValue());
        ToolBar.setObstacleColor(obstacleColor.getValue());
        ToolBar.setCoinColor(coinColor.getValue());
        ToolBar.setBackgroundColor(backgroundColor.getValue());
        //Set les variables de la Toolbar par defaut
        ToolBar.setItem("groundBtn");

		screenBounds = Screen.getPrimary().getBounds();
		
		hBar = upBar.getPrefHeight();
		

		widthCell = (int) (screenBounds.getWidth()/32 - 1);
		
		double rWidth = screenBounds.getWidth() / widthCell;
		double rHeight = (screenBounds.getHeight() - hBar) / widthCell;
		
		grille = new GridPane();
		grille.setHgap(1);
		grille.setVgap(1);
		grille.setGridLinesVisible(true);
		
		
		// Remplissage de la grille
		for(int y = 0; y < rHeight - 1; y++) {
			for(int x = 0; x < rWidth - 1; x++) {
				Cell cell = new Cell(widthCell, x, y);
				grille.add(cell, x, y);
			}
		}
		root.getChildren().add(grille);
		
		
		// G�re le depl de la grille ac le clic molette
		handleMouseEvents();
		
		// Permet a cette classe de s'abonner � des events 
		Ebus.get().register(this);
		
		
		// Tracking des btns de la toolBar --------------------------------------------------------
		for(Node btn : toolBar.getChildren()) {
			btn.setOnMouseClicked(e -> {
				final Node btnAct = (Node) e.getSource();
				String id = btnAct.getId();
				this.unselect();
				
				ToolBar.setItem(id);
			});
		}
		
		// Rectangle rouge de s�lection
		select = new Rectangle();
		select.setFill(Color.RED);
		select.setOpacity(0.2);
		select.setFocusTraversable(true);
		
		// Rectanlge a bord gris de a �t� s�lectionn�
		selected = new Rectangle();
		selected.setFill(Color.TRANSPARENT);
		selected.setStroke(Color.web("0x696C82", 0.5));
		selected.setStrokeWidth(5);
		selected.setStrokeDashOffset(150.0);
		selected.setCursor(Cursor.MOVE);
		
		// G�re les changements de couleur des blocs
		handleChangeColor();
        
	}
	
	// QuickTest ----------------------------------------------------------------------------------
	@FXML
    void handleTest(ActionEvent event){
		if(!inTesting) {
			ToolBar.setItem("test");	
			
			// D�sactive tout les btns de la toolbar et change le retour
			toolBar.setDisable(true);
			test.setDisable(true);
			saveBar.setDisable(true);
			
			inTesting = true;
			
			Ebus.get().post(new PopupEvent("Warning !", "Click on a cell to place the player and start the test."));
			
		} 
    }
	
	@SuppressWarnings("unchecked")
	@Subscribe
	private void launchGame(LaunchGameEvent e) throws IOException {
		// Charge la map
		JSONObject mapGen = this.getCustomMap();
		
		// Met le focus sur l'anchorPane pour ne pas appuyer sur un btn, et pour permettre l'event keyPressed du saut
		root.requestFocus();
		
		// D�finis la map � utiliser, attend un JSONArray
		Level.setJsonLevel(mapGen);
		Level.setPreview(false);
		
		// Set the x coordinate of the start of the game
		coTest = grille.getLayoutX();
		
		// Load game FXML
		FXMLLoader gameLoader = new FXMLLoader();
		gameLoader.setLocation(MainMenu.class.getResource("./AffichageMap/view/mainLayout.fxml"));
		AnchorPane game = (AnchorPane) gameLoader.load();
		game.setManaged(false);
		
		// Met le jeu par dessus la grille
		root.getChildren().add(game);
		
		mainController = gameLoader.getController();
		playerSpeed = mainController.getSpeedPlayer();
		mainController.setEdit(true, hBar);
		
		root.setOnKeyPressed(evt ->{
			mainController.startJump();
		});
		root.setOnKeyReleased(evt -> {
			mainController.stopJump();
		});
	}
	
	@Subscribe
	private void gridGameMoving(MoveGridEvent e) {
		grille.setLayoutX(e.getX());
	}
	
	// Grile dynamique -----------------------------------------------------------------------------
	
	/**
	 * D�placement de la grille avec le clic molette
	 */
	private void handleMouseEvents() {
		// Event qui attendent le drag de la fen�tre ----------------------------------------------
		grille.setOnMousePressed(e -> {
			// D�but / init
			if(e.getButton() == MouseButton.MIDDLE) {
				// D�placement de la grille
				e.setDragDetect(true);
				newX = e.getSceneX();
				
			} else if(e.getButton() == MouseButton.PRIMARY && ToolBar.getItem().equals("select")) {

				if(e.getTarget() instanceof Cell) {
					Cell c = (Cell) e.getTarget();
					
					if(c.isSelected()) {
						// Depl de la selection
						
						
						
					} else {
						// Zone de s�lection
						
						this.unselect();
						select.setLayoutX(e.getX());
						select.setLayoutY(e.getY());
						select.setWidth(0);
						select.setHeight(0);
						initialPtX = e.getX() + grille.getLayoutX();
						initialPtY = e.getY();
						
						root.getChildren().add(select);
					}
				}
			}
		});
		grille.setOnMouseDragged(e -> {
			// D�placement de la souris
			if(e.getButton() == MouseButton.MIDDLE) {
				// Si on drag avec le clic molette .... ->
				double mouseX = e.getSceneX();
				double delta = 0;
							
				oldX = newX;
				newX = mouseX;
				delta = newX - oldX;
				
//				System.out.println("old : " + oldX + " / new : " + newX + " / delta : " + delta);
//				System.out.println(-mostRight +" / " + grille.getLayoutX());
//				System.out.println(grille.getLayoutX());
				
				// D�place uniqument si c'est pas < � 0 et Sup au MostX
				if((grille.getLayoutX() + delta) <= 0 && (grille.getLayoutX() + delta) > -((widthCell + 1) * ToolBar.getMostX()) + (widthCell / 2)) {
					grille.setLayoutX(grille.getLayoutX() + delta);
				}
			} else if(e.getButton() == MouseButton.PRIMARY && ToolBar.getItem().equals("select")) {
				// S�lection---------------------------------------------------------------------------------
				
				
//				if(false) {
//					// Depl de la sel�ction
//					
//					
//				} else {
					// Zone de s�lection
					switch (ToolBar.getItem()) {
					case "select":
						double deltaX = (e.getX() + grille.getLayoutX()) - initialPtX;
					    double deltaY = e.getY() - initialPtY;

					    if(deltaX < 0) {
					        select.setLayoutX(e.getX() + grille.getLayoutX());
					        select.setWidth(-deltaX);
					    } else {
					        select.setLayoutX(initialPtX);
					        select.setWidth((e.getX() + grille.getLayoutX()) - initialPtX);
					    }

					    if(deltaY < 0) {
					        select.setLayoutY( e.getY());
					        select.setHeight(-deltaY);
					    } else {
					        select.setLayoutY(initialPtY);
					        select.setHeight(e.getY() - initialPtY);
					    }
						
						break;
					}
//				}
			}
			
		});
		grille.setOnMouseReleased(e -> {
			// Relachement du clic
			if(e.getButton() == MouseButton.PRIMARY && ToolBar.getItem().equals("select")) {
//				// Regarde toutes les cases 
//				for(Node cell : grille.getChildren()) {
//					Cell c = new Cell(cell);
//					if(cell instanceof Cell) {
//						c = (Cell) cell;
//					}
//					
//					// Cherche les cellules dans la zone de selection
//					if(((c.getX()+1) * widthCell) > select.getLayoutX() && (c.getX() * widthCell) < (select.getLayoutX() + select.getWidth()) 
//					&& ((c.getY()+1) * widthCell) > select.getLayoutY() && (c.getY() * widthCell) < (select.getLayoutY() + select.getHeight())) {
//						// Si y a pas que le background, alors 
//						if(c.getChildrenUnmodifiable().size() > 3) {
//							c.setSelected(true);
//						}							
//					}
//				}
				
				AnchorPane cadre = new AnchorPane();
				// Calcul des coordonn�es : Cox - (ses co dans sa case) + (Le nbr de cases) - (Le d�calage de la grille) 
				double x = select.getLayoutX() - (select.getLayoutX() % widthCell) + Math.floor(select.getLayoutX() / widthCell) - grille.getLayoutX();
				double y = Math.floor(select.getLayoutY() - (select.getLayoutY() % widthCell) + (select.getLayoutY() / widthCell));
				
				// x1 : distance cot� gauche par rapport au d�but de la case
				double x1 = (select.getLayoutX() % (widthCell + 1));
				// x1 : distance cot� droite par rapport au d�but de la case
				double x2 = (select.getWidth() + x1) % (widthCell + 1);
				// Addition de x1, la largeur, et x2 (+ 1 :D)
				double width = (x1 + select.getWidth()) + (widthCell - x2) + 1;
				
				// x1 : distance cot� gauche par rapport au d�but de la case
				double y1 = (select.getLayoutY() % (widthCell + 1));
				// x1 : distance cot� droite par rapport au d�but de la case
				double y2 = (select.getHeight() + y1) % (widthCell + 1);
				// Addition de x1, la largeur, et x2 (+ 1 :D)
				double height = (y1 + select.getHeight()) + (widthCell - y2) + 1;
				
				selected.setLayoutX(x);
				selected.setLayoutY(y);
				selected.setWidth(width);
				selected.setHeight(height);
				
				root.getChildren().remove(select);
				
				if(select.getWidth() > 10 && select.getHeight() > 10) {
					root.getChildren().add(selected);
				}
			}
		});
	}
	
	
	private void handleChangeColor() {
		// Event handler pour le choix des couleurs
        EventHandler<ActionEvent> changeColorEvent = new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent e)
        	{
        		if(e.getSource() == groundColor) {
        			ToolBar.setGroundColor(groundColor.getValue());
        			for(Node cell : grille.getChildren()) {
        				Cell c = new Cell(cell);
        				if(cell instanceof Cell) {
        					c = (Cell) cell;
        					if(c.getCellId() == '1') {
        						c.erase(false);
        						c.paint("groundBtn");
        					}else if(c.getCellId() == '5') {
        						c.erase(false);
        						c.paint("groundSlabBtn");
        					}else if(c.getCellId() == '6') {
        						c.erase(false);
        						c.paint("pillarBtn");
        					}
        				}
        			}
        		}else if(e.getSource() == obstacleColor){
        			ToolBar.setObstacleColor(obstacleColor.getValue());
        			for(Node cell : grille.getChildren()) {
        				Cell c = new Cell(cell);
        				if(cell instanceof Cell) {
        					c = (Cell) cell;
        					if(c.getCellId() == '2') {
        						c.erase(false);
        						c.paint("obstacleBtn");
        					}else if(c.getCellId() == '4') {
        						c.erase(false);
        						c.paint("reverseObstacleBtn");
        					}
        				}
        			}
        		}else if(e.getSource() == coinColor){
        			ToolBar.setCoinColor(coinColor.getValue());
        			for(Node cell : grille.getChildren()) {
        				Cell c = new Cell(cell);
        				if(cell instanceof Cell) {
        					c = (Cell) cell;
        					if(c.getCellId() == '3') {
        						c.erase(false);
        						c.paint("coinBtn");
        					}
        				}
        			}
        		}else if(e.getSource() == backgroundColor){
        			ToolBar.setBackgroundColor(backgroundColor.getValue());
        			for(Node cell : grille.getChildren()) {
        				Cell c = new Cell(cell);
        				if(cell instanceof Cell) {
        					c = (Cell) cell;
        					c.setBack(backgroundColor.getValue());
        				}
        			}
        		}
        		
        	}
        };
  
        // Listener des changements de couleur
        groundColor.setOnAction(changeColorEvent);
        obstacleColor.setOnAction(changeColorEvent);
        coinColor.setOnAction(changeColorEvent);
        backgroundColor.setOnAction(changeColorEvent);
	}
	
	/**
	 * D�selectionne toutes les cases de la grille
	 */
	private void unselect() {
//		for(Node cell : grille.getChildren()) {
//			Cell c;
//			if(cell instanceof Cell) {
//				c = (Cell) cell;
//			} else {
//				c = new Cell(cell);
//			}
//			
//			if(c.isSelected()) {
//				c.setSelected(false);
// 			}
//		}
		
		if(root.getChildren().contains(selected)) {
			root.getChildren().remove(selected);
			selected.setWidth(0);
			selected.setHeight(0);
			selected.setLayoutX(0);
			selected.setLayoutY(0);			
		}
	}
	
	// Ecoute le bus d'�vent pour savoir si la taille de la grille doit changer -------------------
	/**
	 * G�re l'ajout de colonnes � la grille, se d�lcenche via l'event bus
	 * @param e l'event auquel il est abonn�
	 */
	@Subscribe
	private void handleAddLenght(AddLengthGrilleEvent e) {
		int deltaX = e.getX() - ToolBar.getMostX();
		ToolBar.setMostX(e.getX());
		
		// Cr�e des colonne de grille, autant de fois que le delta nouveau-ancien
		int nCol = 0;
		int nRow = 0;

		for(int j = 0; j < deltaX; j++) {
			// Regarde le num de col et de ligne de la derni�re cellule
			Cell c = (Cell) grille.getChildren().get(grille.getChildren().size() - 1);
			
			nCol = c.getX();
			nRow = c.getY();
			
			// Ajout de colonnes --------------------------------------------------
			for(int i = 0; i <= nRow ;i++) {
				Cell cells = new Cell(widthCell, nCol + 1, i);
				grille.addColumn(nCol + 1, cells);			
			}
		}
		
		// Change la couleur des nouvelles cellules
		ToolBar.setBackgroundColor(backgroundColor.getValue());
		for(Node cell : grille.getChildren()) {
			Cell c = new Cell(cell);
			if(cell instanceof Cell) {
				c = (Cell) cell;
				c.setBack(backgroundColor.getValue());
			}
		}
	}
	
	/**
	 * G�re la suppression de colonnes � la grille, se d�lcnche via l'event bus
	 * @param e l'event auquel il est abonn�
	 */
	@Subscribe
	private void handleRemoveLenght(RemoveLengthGrilleEvent e) {
		int x = 0;
		// Regarde toutes les cases pour d�finir quelle colonne est la derni�req
		for(Node cell : grille.getChildren()) {
			Cell c = new Cell(cell);
			if(cell instanceof Cell) {
				c = (Cell) cell;
			}
			
			// Si y a pas que le background, alors on change le mostX
			if(c.getChildrenUnmodifiable().size() > 3) {
				x = (c.getX() > x) ? c.getX() : x;
			}
		}
		int deltaX = x - ToolBar.getMostX();
		ToolBar.setMostX(x);
		
		// D�cale la cam si y a plus assez de cases peintes dans le champ
		if(grille.getLayoutX() < -((widthCell + 1) * ToolBar.getMostX()) + (widthCell / 2)) {
			double x2 = -((widthCell + 1) * ToolBar.getMostX()) + (widthCell / 2);
			x2 = (x2 >= 0) ? 0 : x2;

			grille.setLayoutX(x2);
		}
		
		// Obtention du nbr de colonnes et lignes
		Node cells = grille.getChildren().get(grille.getChildren().size() - 1);
		Cell c1 = new Cell(cells);
		if(cells instanceof Cell) {
			c1 = (Cell) cells;
		}
		int nCol = c1.getX();

		// Supprime une colones de grille
		ArrayList<Cell> toRem = new ArrayList<Cell>();
		
		for(Node cell : grille.getChildren()) {
			Cell c = new Cell(cell);
			if(cell instanceof Cell) {
				c = (Cell) cell;
			}
			
			// suppr via les y
			if(c.getX() >= nCol + deltaX + 1) {
				toRem.add(c);
			}
		}
		for(Cell rem : toRem) {
			grille.getChildren().remove(rem);
		}
	}
	
	@Subscribe
	private void handleRemovePart(RemovePartGrilleEvent e) {
		ArrayList<Cell> toRem = new ArrayList<Cell>();
		int x = 0;
		ToolBar.setMostX(e.getX());

		// Fait defiler les cells
		for(Node cell : grille.getChildren()) {
			Cell c = new Cell(cell);
			if(cell instanceof Cell) {
				c = (Cell) cell;
			}
			if(c.getX() > e.getX()) {
				c.erase(true);
			}

			// Si y a pas que le background, alors on change le mostX
			if(c.getChildrenUnmodifiable().size() > 2) {
				x = (c.getX() > x) ? c.getX() : x;
			}
		}
		ToolBar.setMostX(x);

		// Decale la cam si y a plus assez de cases peintes dans le champ
		if(grille.getLayoutX() < -((widthCell + 1) * ToolBar.getMostX()) + (widthCell / 2)) {
			double x2 = -((widthCell + 1) * ToolBar.getMostX()) + (widthCell / 2);
			x2 = (x2 >= 0) ? 0 : x2;

			grille.setLayoutX(x2);
		}
	}
	
	// btn Retour ---------------------------------------------------------------------------------
	public void btnReturn(ActionEvent event) throws IOException {
		if(!inTesting) {
			if(showConfirmation("Confirm", "Want to leave ?", "Are you sure you want to leave? It won't save your job.")) {
				Ebus.get().unregister(this);
				
				// Pas en test, on revient au menu
				Parent tableViewParent = FXMLLoader.load(getClass().getResource("../../Menu/BaseMenu.fxml"));
				Scene tableViewScene = new Scene(tableViewParent);
				
				Stage window = (Stage) (((Node) event.getSource()).getScene().getWindow());
				
				window.setScene(tableViewScene);
				window.setMaximized(true);
				window.show();	
				System.gc();
			}
			
		} else if (inTesting && mainController == null) {
			ToolBar.setItem("");
			
			// Active tout les btns de la toolbar et change le retour
			toolBar.setDisable(false);
			test.setDisable(false);
			saveBar.setDisable(false);
			
			// Arr�te le test
			inTesting = false;
			
		} else if(inTesting && mainController != null){
			// En test, on sort du jeu
			mainController.setStop();
			root.getChildren().remove(root.getChildren().size() - 1);
			mainController = null;
			System.gc();
			
			// Active tout les btns de la toolbar et change le retour
			toolBar.setDisable(false);
			test.setDisable(false);
			saveBar.setDisable(false);
			
			// Arr�te le test
			inTesting = false;
			
			// Replace la cam
			grille.setLayoutX(coTest);
		} 
	}
	
	/**
	 * Popup de confirmation
	 * @param title Le titre
	 * @param header Le header
	 * @param text Le contenu
	 * @return true si on appui sur oui, sinon false 
	 */
	private boolean showConfirmation(String title, String header, String text) {
	      Alert alert = new Alert(AlertType.CONFIRMATION);
	      alert.setTitle(title);
	      alert.setHeaderText(header);
	      alert.setContentText(text);

	      // option != null.
	      Optional<ButtonType> option = alert.showAndWait();

	      if (option.get() == ButtonType.OK) {
	         return true;
	      }
	      
	      return false;
	   }
	
	
	@Subscribe
	public void myPopup(PopupEvent e) {
		// Tailles max
		int width = 400;
		int height = 200;
		
		// Cr�ation de la vBox, et set de set propri�t�s et css
		VBox popup = new VBox();
		popup.setPrefWidth(width);
		popup.setMaxHeight(height);
		popup.setLayoutX((screenBounds.getWidth() / 2) - (popup.getPrefWidth()/ 2));
		popup.setLayoutY((screenBounds.getHeight() / 4) * 3 - 50);
		popup.setAlignment(Pos.CENTER);
		popup.setStyle("-fx-opacity: 0.8; -fx-padding: 20px; -fx-background-color: white; -fx-border-color: red; -fx-border-radius: 20px;");
		
		// Labels de titre et de contenu
		Label title = new Label();
		title.setText(e.getTitle());
		
		Label text = new Label();
		text.setText(e.getText());
		
		// impl�mentation des labels � la popup, et elle-m�me au root
		popup.getChildren().addAll(title, text);
		root.getChildren().add(popup);
		
		// Pause de 2s, puis fait disparaitre la popup
		PauseTransition delay = new PauseTransition(Duration.seconds(2));
		delay.setOnFinished( event -> root.getChildren().remove(popup));
		delay.play();
	}
	
	
	// -------------------------- PARTIE DEDIEE A LA SAVE ----------------------------------------------
	public void GoToSave(ActionEvent event) throws IOException, ParseException {
		// Load (mais n'affiche pas) la page fxml d�di�e � la save pour ensuite envoyer la map � la classe SaveController
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Save.fxml"));
		Parent root = (Parent) loader.load();
		
		// Envoie la map ( charg�e par getCustomMap() ) � la classe SaveController � travers la fonction setData() dans SaveController qui r�cup�re la map et lance la save()
		SaveController sc = loader.getController();
		sc.setData(getCustomMap());
	}

    public void loadMap() throws FileNotFoundException, IOException, ParseException {
    	JSONParser parser = new JSONParser();

    	// Demande � l'utilisateur de choisir un fichier
        File file = fileChooser.showOpenDialog(new Stage());
    	Reader reader = new FileReader(file);
      	
        JSONObject jsonObject = (JSONObject) parser.parse(reader); // parse
            	    	
    	JSONArray element1 = (JSONArray) jsonObject.get("map"); // element1 recupere la map  
    	
        JSONArray element2 = (JSONArray) element1.get(0);      
        
        // Agrandi la grille en fonction de la map charg�e
		Ebus.get().post(new AddLengthGrilleEvent(element2.size()));

        // Cr�er un tableau 2D pour exploiter la map choisie
    	char[][] tabMap = new char[element1.size()][element2.size()];
    	
    	// Remplit le tableau 2D
        for (int i = 0; i < element1.size(); ++i) {
            element2 = (JSONArray) element1.get(i); // passe � la prochaine ligne
            for(int j = 0; j < element2.size(); ++j) {
                System.out.print(element2.get(j));
                
                int iO = ((Long) element2.get(j)).intValue();
                char cO = (char) (iO + '0');
                tabMap[i][j] = cO;
                //tabMap[i][j] = (char) ((Long) element2.get(j)).intValue();
            }
            System.out.println(); // saute une ligne
        }
        
		// Remplit la grille avec la map charg�e
        int lastX = 0;
		for(Node cell : grille.getChildren()) {
			Cell c = new Cell(cell);
			if(cell instanceof Cell) {
				c = (Cell) cell;
				c.setBack(stringToColor(jsonObject, "background"));
			}
			if(c.getY() == element1.size()-1 && c.getX() == element2.size()-1){
				break;
			}
			c.setCellId(tabMap[c.getY()][c.getX()]);
			c.loadMapPaint(jsonObject);
			lastX = c.getX();
		}
		
		// Supprime les cases vides en trop (Casse l'editeur de map !!)
		//Ebus.get().post(new RemovePartGrilleEvent(lastX));
		
		// Set les colors pickers sur la couleur de la map chargee pour pas que le joueur ait � re-pick sa couleur
    	groundColor.setValue(stringToColor(jsonObject, "ground"));
    	obstacleColor.setValue(stringToColor(jsonObject, "obstacle"));
    	coinColor.setValue(stringToColor(jsonObject, "coin"));
    	backgroundColor.setValue(stringToColor(jsonObject, "background"));
    	
		ToolBar.setGroundColor(groundColor.getValue());
		ToolBar.setObstacleColor(obstacleColor.getValue());
		ToolBar.setCoinColor(coinColor.getValue());
		ToolBar.setBackgroundColor(backgroundColor.getValue());

    }

	// Passe une couleur de String � Color
    Color stringToColor(JSONObject jsonObject, String mapElement){
		Color customColor;
		String color;
		String hexColor;
		
		color = (String) ((JSONObject) jsonObject.get("color")).get(mapElement);

		hexColor = "#"+color.substring(2,8);
        customColor = Color.valueOf(hexColor);
		return customColor;
    }
	
	@SuppressWarnings("unchecked")
	private JSONObject getCustomMap() {
		JSONObject customMapObject = new JSONObject();
		
		// Obtention du nbr de colonnes et lignes
		Node cells = grille.getChildren().get(grille.getChildren().size() - 1);
		Cell c1 = new Cell(cells);
		if(cells instanceof Cell) {
			c1 = (Cell) cells;
		}
		int nCol = c1.getX();
		int nRow = c1.getY();
		
		//Cr�ation du tableau 2D
		char[][] cellTab = new char[nRow+1][nCol+1];
//		System.out.println(" MAX : "+nCol+", "+nRow);
		
		// remplit le tableau 2D
		for(Node cell : grille.getChildren()) {
			Cell c = new Cell(cell);
			if(cell instanceof Cell) {
				c = (Cell) cell;
			}
			cellTab[c.getY()][c.getX()] = c.getCellId();
		}
		
		// Transforme le tableau en JsonArray
    	JSONArray mapJsonArray = new JSONArray();
    	for (int y = 0; y < cellTab.length; y++) {
            char[] line = cellTab[y];
            JSONArray lineJSON = new JSONArray();
            mapJsonArray.add(lineJSON);
            for (int x = 0; x < line.length; x++) {
            	lineJSON.add(line[x]);
            	lineJSON.get(x);
            }
        }
  		
    	JSONObject colorArray = new JSONObject();
    	colorArray.put("ground",""+groundColor.getValue());
    	colorArray.put("obstacle",""+obstacleColor.getValue());
    	colorArray.put("coin",""+coinColor.getValue());
    	colorArray.put("background",""+backgroundColor.getValue());

		customMapObject.put("music", "musique1");
		customMapObject.put("color", colorArray);
		customMapObject.put("map", mapJsonArray);

		return customMapObject;
	}
	
	public JSONObject getColors() {
    	JSONObject colors = new JSONObject();
    	colors.put("ground",""+groundColor.getValue());
    	colors.put("obstacle",""+obstacleColor.getValue());
    	colors.put("coin",""+coinColor.getValue());
    	colors.put("background",""+backgroundColor.getValue());
		return colors;
	}
	
	
}
