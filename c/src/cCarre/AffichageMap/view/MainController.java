package cCarre.AffichageMap.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.eventbus.Subscribe;

import cCarre.MainMenu;
import cCarre.AffichageMap.model.Coin;
import cCarre.AffichageMap.model.FinishBlock;
import cCarre.AffichageMap.model.Ground;
import cCarre.AffichageMap.model.GroundSlab;
import cCarre.AffichageMap.model.Level;
import cCarre.AffichageMap.model.Obstacle;
import cCarre.AffichageMap.model.Pillar;
import cCarre.AffichageMap.model.Player;
import cCarre.AffichageMap.model.ReverseObstacle;
import cCarre.genmap.events.Ebus;
import cCarre.genmap.events.PauseEvent;
import cCarre.genmap.events.PlayerState;
import cCarre.genmap.events.RestartGameEvent;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController {
	private ArrayList<Shape> platforms = new ArrayList<Shape>();
	private ArrayList<Shape> triangles = new ArrayList<Shape>();
	private ArrayList<FinishBlock> finishBlocks = new ArrayList<FinishBlock>();
	private ArrayList<Coin> coins = new ArrayList<Coin>();

	int elementSize = 60;

	// Vars ---------------
	long oldTime;
	long newTime;
	double dt; //dt par sec
	double temps;
	int frame;
	long time;
	
	Timeline time1 = null;
	
	double vitesse;
	double distanceX;
	double distanceY;
	double verticalVelocity = 0;
	int spawnX, spawnY;
	
	int pieces = 0;
	
	int oldDiv = 0;
	
	boolean jump = false;
	boolean onGround = true;
	
	String level = "";
	Pane pauseMenu = null;

	boolean running = true;
	boolean pause = false;
	boolean finish = false;
	boolean recc = true; 

	boolean newSpawn = false;
	
	boolean dead = false;
	private Rectangle ragdoll = null;
	
	Shape[][] mapRender = null;
	
	MediaPlayer mediaPlayer = null;
	MediaPlayer musicPlayer = null;
	Media mediaSound = null;
	String nameMusic = "Projet64_2.wav";
	
	boolean newGround = false;
	boolean oldGround = false;
	
	ProgressBar pBar = null;
	
	// Pour changer la vitesse
	int constV = 430; 
	int constGrav = 900;
	double jumpForce = 600;
	
	// taille de l'�cran
	private Rectangle2D screenBounds = Screen.getPrimary().getBounds();

	boolean edit = false;
	double toolBarHeight = 0;
	
	double vAnimDeath = 1000;
	
	private boolean preview = false;
	
	double score = 0;
	DoubleProperty progressProperty = new SimpleDoubleProperty(score);
	double bestScore = 0;
	StringProperty bestProperty = new SimpleStringProperty("Best : 0.0%");
	HBox boxScore = null;

	@FXML
	private Player player;
	
	@FXML
	private Label Coin;
	
	@FXML
	private Label popup;

	@FXML
	private AnchorPane rootLayout;
	

	
	@SuppressWarnings("static-access")
	@FXML
	private void initialize() {
		// Adapte la vitesse et la gravit� et les �l�ments � la taille de l'�cran
		float varVit = (float)1920/constV;		
		constV = (int) ((int) screenBounds.getWidth()/varVit);
		
		float varGrav = (float)1920/constGrav;		
		constGrav = (int) ((int) screenBounds.getWidth()/varGrav);
		
		// Init la taille des case et la force du saut par rapport � la r�solution
		jumpForce = (int) (screenBounds.getWidth()/(1920/jumpForce));
		elementSize = (int) (screenBounds.getWidth()/(1920/elementSize));


		// Init du Level
		Level level = new Level();
		int levelLength = level.getLevelLength();
		int levelHeight = level.getLevelHeight();
		preview = level.isPreview();
		if(preview) {
			elementSize = level.getElemHeight();
			levelLength = Math.min(levelLength, 30);
		}
		JSONObject Level = level.getLevel();
		JSONArray map = (JSONArray) Level.get("map");
		
		String backgroundColor = getHexColor(Level, "background");
		// Met la couleur sur le debut du niveau
	    rootLayout.setStyle("-fx-background-color: "+backgroundColor);

	    // Création du tableau dans lequel seront stockées toutes les formes de la map
		mapRender = new Shape[levelHeight][levelLength];
		int xFinish = levelLength;
		
		for(int y = 0; y < levelHeight; y++) {
			for(int x = 0; x < levelLength; x++) {
				char text = '0';
				
				// V�rifie la provenance de la map
				if(((JSONArray) map.get(y)).get(x) instanceof Long) {
					int text1 = ((Long) ((JSONArray) map.get(y)).get(x)).intValue();
					text = (char) (text1 + '0');
					
				} else {
					text = (char) ((JSONArray) map.get(y)).get(x);
				}
				

				switch(text) {
				case '0' :
					// c'est vide, c'est l'air
					mapRender[y][x] = null;
					break;
				case '1' :
					Ground platform = new Ground(x*elementSize, y*elementSize, elementSize, elementSize, Color.valueOf((String) ((JSONObject) Level.get("color")).get("ground")));
										
					// Ajout au tableau de rendu de la map
					mapRender[y][x] = platform;
					break;
				case '2' :
					Obstacle triangle = new Obstacle(x*elementSize, y*elementSize, elementSize, elementSize, Color.valueOf((String) ((JSONObject) Level.get("color")).get("obstacle")));

					// Ajout au tableau de rendu de la map
					mapRender[y][x] = triangle;
					break;
				case '3' :
					Coin coin = new Coin(x*elementSize + (elementSize / 4), y*elementSize + (elementSize / 4), elementSize / 2, elementSize / 2, Color.valueOf((String) ((JSONObject) Level.get("color")).get("coin")));

					// Ajout au tableau de rendu de la map
					mapRender[y][x] = coin;
					break;
				case '4' :
					ReverseObstacle reverseTriangle = new ReverseObstacle(x*elementSize, y*elementSize, elementSize, elementSize, Color.valueOf((String) ((JSONObject) Level.get("color")).get("obstacle")));

					// Ajout au tableau de rendu de la map
					mapRender[y][x] = reverseTriangle;
					break;
				case '5' :
					GroundSlab platformSlab = new GroundSlab(x*elementSize, y*elementSize, elementSize, elementSize, Color.valueOf((String) ((JSONObject) Level.get("color")).get("ground")));
					
					// Ajout au tableau de rendu de la map
					mapRender[y][x] = platformSlab;
					break;
				case '6' :
					Pillar pillar = new Pillar(x*elementSize, y*elementSize, elementSize, elementSize, Color.valueOf((String) ((JSONObject) Level.get("color")).get("ground")));
					
					// Ajout au tableau de rendu de la map
					mapRender[y][x] = pillar;
					break;
				case '8' :
					if(!newSpawn) {
						spawnX = x * elementSize;
						spawnY = y * elementSize - 1;
					}
					break;
				case '9' :
					FinishBlock finishBlock = new FinishBlock(x*elementSize, y*elementSize, elementSize, elementSize, Color.GREEN);

					// Ajout au tableau de rendu de la map
					mapRender[y][x] = finishBlock;
					xFinish = x - 1;
					break;
				case 's' :
					// Test rapide de l'�diteur
					spawnX = x * elementSize;
					spawnY = y * elementSize - 1;
					newSpawn = true;
					break;
				}
			}
		}
		Color color = Color.BLUE;
		//JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
         
        try (FileReader reader = new FileReader("./Color.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONObject ColorList = (JSONObject) obj;
            ColorList.get("variable");
            color = parseColor((String) ColorList.get("variable"));
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

		// Charge le player
		player = new Player(spawnX, spawnY, elementSize, elementSize, color, rootLayout, constGrav, constV);

		// pr�charge le spawn
		loadSpawn();
		
		
		final int fLevelLength = levelLength;
		final int fxFinish = xFinish;
		
		// La cam�ra suit le joueur
        player.translateXProperty().addListener((obs, old, newValue) -> {
            int offset = newValue.intValue();
            if (offset > 300 && offset < level.getLevelWidth() - 300) {
                rootLayout.setLayoutX(-(offset - 300));
                Coin.setLayoutX(+(offset - 300));
                Coin.toFront();
                boxScore.setLayoutX((+(offset - 300) + screenBounds.getWidth() / 2) - (pBar.getPrefWidth() / 2));
                boxScore.toFront();
                
        		// Adapte la taille de l'achor pane au niveau jou�, puis change la background color
                rootLayout.resize((fLevelLength+25)*elementSize, (levelHeight+6)*elementSize);
        	    rootLayout.setStyle("-fx-background-color: "+backgroundColor);

                // Si le jeu vient de l'�diteur, transmet les coo � la grille
//				Ebus.get().post(new MoveGridEvent(-(offset - 300)));
				
				
            }

            // Update de l'affichage de la map
            int div = (int) (player.getTranslateX() / elementSize);
            
            if(div > oldDiv) {
            	oldDiv = div;
            	// Pourcentage, divisé par 100 car on attend qqchose entre 0 et 1
            	score = (div * 100 / fxFinish);
            	progressProperty.set(score / 100) ;
            	this.renderMap();
            }
        });
		
       

        if(!preview) {
        	Coin.setTextFill(Color.DARKGRAY);
        	
        	Ebus.get().register(this);
        	
        	// Hbox du score
        	boxScore = new HBox();
        	boxScore.setLayoutY(15);
        	boxScore.setAlignment(Pos.CENTER);
        	boxScore.setSpacing(20);
        	
        	// ProgressBar
        	pBar = new ProgressBar();
        	pBar.setPrefWidth(800);
        	pBar.setProgress(0);
        	pBar.progressProperty().bind(progressProperty);
        	pBar.setStyle("-fx-accent: " + this.getHexColor(Level, "ground"));
        	boxScore.getChildren().add(pBar);
        	
        	// Score
        	Label lScore = new Label();
        	lScore.textProperty().bind(bestProperty);
        	lScore.setTextFill(Color.DARKGRAY);
        	lScore.setFont(Font.font ("Courier new", 20));
        	lScore.setStyle("-fx-font-weight: bold");
        	boxScore.getChildren().add(lScore);
        	
        	boxScore.setLayoutX((screenBounds.getWidth() / 2) - (pBar.getPrefWidth() / 2));
        	rootLayout.getChildren().add(boxScore);
        	
        	
        	// Charge le fichier des coins
        	loadCoin();
        	
        	// Cr�ation du cube d'anim de mort
        	ragdoll = new Rectangle();
        	ragdoll.setManaged(false);
        	
        	// Opacit� de base
        	ragdoll.setOpacity(0.5);
        	
        	PauseTransition delay = new PauseTransition(Duration.seconds(1));
        	delay.setOnFinished( event -> {
        		//init temps
        		newTime = System.nanoTime();
        		time = System.currentTimeMillis();
        		
        		// Let's go into the GAME !
        		loop(150); 
        		
        		// Joue la musique
        		playMusic();
        	});
        	delay.play();
        }

	}
	

	/**
	 * Chef d'orchestre du jeu, c'est un boucle qui update @fps fois par seconde
	 * @param fps Le nombre d'update, et donc d'images par seconde
	 */
	private void loop(int fps) {
		time1 = new Timeline(new KeyFrame(Duration.millis(1000 / (fps - 2)), e -> {
			dt = affFPS();
			temps = dt / 1000000000; //dt par sec
//			temps /= 8; // ralentit le jeu pour les tests
			
			
			if(running && !pause && !finish) {
				double gravity = player.p2.distance(player.centreX, player.centreY) * 2;
				
				// distanceX vect entre centre du joueur et le point (vitesse)
				vitesse = player.p1.distance(player.centreX, player.centreY);
				
				// Est-ce que le cube est au sol ?
				if(playerOnGround() == true) {
					verticalVelocity = 0;
					
					// Saut si oui
					if(jump == true) {
						playSound("Jump.wav", 1);
						verticalVelocity = jumpForce;
						jump = false; 
					}
				} else {
					verticalVelocity -= gravity * temps;
				}
	
				distanceX = vitesse * temps;
				distanceY = verticalVelocity * temps;
				
				// Met a jour les position
				player.depl(distanceX, distanceY, jumpForce, verticalVelocity);
				
				collisions(); // check les collision et la mort du joueur
				
				coinCollision(); // ramasse les coins si on passe dessus
				
				// Si le joueur touche la ligne d'arriv�e
	            boolean collisionDetected = false;
	            for (Shape finishBlock : finishBlocks) {
	            	if (finishBlock != player.playerRectangle) {
	
	            		Shape intersect = Shape.intersect(player.playerRectangle, finishBlock);
	            		if (intersect.getBoundsInLocal().getWidth() != -1) {
	            			collisionDetected = true;
	            		}
	            	}
	
	            	if (collisionDetected) {
	            		finish = true;
	            		musicPlayer.stop();
	            		fin(recc);
	            	}
	            }
	            
				
				
				Coin.setText("Coins : "+pieces);
				
				//Sound landing
				oldGround = newGround;
				newGround = playerOnGround();
				if(!oldGround && newGround) {
					playSound("Land.wav", 0.5);
				}
				
			} else if (!running && dead){
				// Le joueur est mort
				double facteur = vAnimDeath * temps;
				
				ragdoll.setHeight(ragdoll.getHeight() + (facteur*2));
				ragdoll.setWidth(ragdoll.getWidth() + (facteur*2));
				ragdoll.setLayoutX(ragdoll.getLayoutX() - facteur);
				ragdoll.setLayoutY(ragdoll.getLayoutY() - facteur);
				
				ragdoll.setOpacity(ragdoll.getOpacity() - 0.0065);
			}
			
		}));

		time1.setCycleCount(Animation.INDEFINITE);
		time1.play();
	}
	
	
	/**
	 * Pr�charge le spawn de la map avant l'apparition du player, et r�initialise les liste de blocs
	 */
	private void loadSpawn() {
		// Chargement du spawn de la map
		
		double init = spawnX - elementSize * 6;
		double end = spawnX + screenBounds.getWidth();
		
		// Reset des listes
		rootLayout.getChildren().removeAll(platforms);
		rootLayout.getChildren().removeAll(triangles);
		rootLayout.getChildren().removeAll(finishBlocks);
		rootLayout.getChildren().removeAll(coins);
		
		platforms = new ArrayList<Shape>();
		triangles = new ArrayList<Shape>();
		finishBlocks = new ArrayList<FinishBlock>();
		coins = new ArrayList<Coin>();
		
		// lis la map de haut en bas, seulement les x dont il a besoin
		for(int y = 0; y < mapRender.length; y++) {
			for(int x = (int) Math.max(0, init); x < (int) Math.min(mapRender[0].length, end); x++) {
				
				if(mapRender[y][x] != null && ((x * elementSize) > init && (x * elementSize) < end)) {
					
					if(y > (player.getTranslateY() / elementSize) - 3) {
						
						if(mapRender[y][x] instanceof Ground) {
							platforms.add((Ground) mapRender[y][x]);
							
						} else if(mapRender[y][x] instanceof GroundSlab) {
							platforms.add((GroundSlab) mapRender[y][x]);
							
						} else if(mapRender[y][x] instanceof Pillar) {
							platforms.add((Pillar) mapRender[y][x]);
							
						} else if(mapRender[y][x] instanceof Obstacle) {
							triangles.add((Obstacle) mapRender[y][x]);
							
						} else if(mapRender[y][x] instanceof ReverseObstacle) {
							triangles.add((ReverseObstacle) mapRender[y][x]);
							
						} else if(mapRender[y][x] instanceof Coin) {
							coins.add((Coin) mapRender[y][x]);
							
						} else if(mapRender[y][x] instanceof FinishBlock) {
							finishBlocks.add((FinishBlock) mapRender[y][x]);
						}
						
					}
					if(!rootLayout.getChildren().contains(mapRender[y][x])) {
						rootLayout.getChildren().add(mapRender[y][x]);
					}
				}
			}
		}
	}
	
	/**
	 * Supprime les blocs qui ne sont plus dans le champ, et affiche ceux qui y arrivent
	 */
	private void renderMap() {
		// constante de marges gauches et droites
		final int spaceLeft = 6;
		final int spaceRight = 4;
		
		double init = player.getTranslateX() - (elementSize * spaceLeft);
		double end = player.getTranslateX() + (screenBounds.getWidth() - (elementSize * spaceRight));
		
		
		// lis la map de haut en bas, seulement les x dont il a besoin
		for(int y = 0; y < mapRender.length; y++) {
			
			for(int x = Math.max(0, (int) (init / elementSize) - 4); x < Math.min(mapRender[0].length, end / elementSize); x++) {
				
				// Suppr ce qui est derri�re le player
				if(mapRender[y][x] != null && x < (player.getTranslateX() / elementSize) - 1) {
					
					// Suppr des listes de collisions
					if(mapRender[y][x] instanceof Ground && platforms.contains(mapRender[y][x])) {
						platforms.remove((Ground) mapRender[y][x]);
						
					} else if(mapRender[y][x] instanceof GroundSlab && platforms.contains(mapRender[y][x])) {
						platforms.remove((GroundSlab) mapRender[y][x]);
						
					} else if(mapRender[y][x] instanceof Pillar && platforms.contains(mapRender[y][x])) {
						platforms.remove((Pillar) mapRender[y][x]);
						
					} else if(mapRender[y][x] instanceof Obstacle && triangles.contains(mapRender[y][x])) {
						triangles.remove((Obstacle) mapRender[y][x]);
						
					} else if(mapRender[y][x] instanceof ReverseObstacle && triangles.contains(mapRender[y][x])) {
						triangles.remove((ReverseObstacle) mapRender[y][x]);
						
					} else if(mapRender[y][x] instanceof Coin && coins.contains(mapRender[y][x])) {
						coins.remove((Coin) mapRender[y][x]);
						
					} else if(mapRender[y][x] instanceof FinishBlock && finishBlocks.contains(mapRender[y][x])) {
						finishBlocks.remove((FinishBlock) mapRender[y][x]);
					}
					
				}
				
				// Supprime ce qui sort de l'�cran (visuel)
				if(mapRender[y][x] != null && mapRender[y][x].getLayoutX() < init) {
					if(rootLayout.getChildren().contains(mapRender[y][x])) {
						// Suppr le visible
						rootLayout.getChildren().remove(mapRender[y][x]);
					}
				}
				
				
				
				// Ajoute les cases si besoin | Grand IF (visuel)
				if(mapRender[y][x] != null && (mapRender[y][x].getLayoutX() > init) && (mapRender[y][x].getLayoutX() < player.getTranslateX() + end)) {
					
					// Petit if (collisions)
					if(y > (player.getTranslateY() / elementSize) - 3 && x < (player.getTranslateX() / elementSize) + 3 && x > (player.getTranslateX() / elementSize)) {
						
						// Ajout aux listes de collisions
						if(mapRender[y][x] instanceof Ground && !platforms.contains(mapRender[y][x])) {
							platforms.add((Ground) mapRender[y][x]);
							
						} else if(mapRender[y][x] instanceof GroundSlab && !platforms.contains(mapRender[y][x])) {
							platforms.add((GroundSlab) mapRender[y][x]);
							
						} else if(mapRender[y][x] instanceof Pillar && !platforms.contains(mapRender[y][x])) {
							platforms.add((Pillar) mapRender[y][x]);
							
						} else if(mapRender[y][x] instanceof Obstacle && !triangles.contains(mapRender[y][x])) {
							triangles.add((Obstacle) mapRender[y][x]);
							
						} else if(mapRender[y][x] instanceof ReverseObstacle && !triangles.contains(mapRender[y][x])) {
							triangles.add((ReverseObstacle) mapRender[y][x]);
							
						} else if(mapRender[y][x] instanceof Coin && !coins.contains(mapRender[y][x])) {
							coins.add((Coin) mapRender[y][x]);
							
						} else if(mapRender[y][x] instanceof FinishBlock && !finishBlocks.contains(mapRender[y][x])) {
							finishBlocks.add((FinishBlock) mapRender[y][x]);
						}
					}

					if(!rootLayout.getChildren().contains(mapRender[y][x]) && x > (player.getTranslateX() / elementSize)) {
						// ajoute le visible
						rootLayout.getChildren().add(mapRender[y][x]);
					}
				}
			}
		}
	}

	/**
	 * R�re les collisions et la mort du joueur
	 */
	private void collisions() {
		if(platfollision() || triangleCollision()) {
			player.death(spawnX, spawnY, rootLayout, Coin);
		}
		// meurt quand tombe dans le vide
		if(player.getTranslateY() > screenBounds.getHeight() - toolBarHeight || player.getTranslateY() < 0) {
			player.death(spawnX, spawnY, rootLayout, Coin);
			playSound("Roblox-Death-Sound-cut.wav", 4);
		}
	}
	
	/**
	 * G�re la d�tection de la collision avec les plateformes, 
	 * tue si le player est sur le c�t�, au sol s'il est sur le dessus
	 */
	private boolean platfollision() {
		boolean collisionDetected = false;
		onGround = false;
		
		// Collisions au sol
		for (Shape platform : platforms) {
        	if (platform != player.playerRectangle) {
        		Shape intersect = Shape.intersect(player.playerRectangle, platform);
        		if (intersect.getBoundsInLocal().getHeight() != -1) {
        			if (intersect.getBoundsInLocal().getHeight() <= intersect.getBoundsInLocal().getWidth()) {
        				if(intersect.getBoundsInLocal().getMinY() - toolBarHeight > platform.getLayoutY()) {
							// plafond -> MORT
	        				verticalVelocity = 0;
	        				collisionDetected = true;
	        				System.out.println("Ca c le plafond -------------------------------------------------------------------------------------");
	        				playSound("Minecraft-Death-Sound-cut.wav", 4);
						} else {
							// Sol
	        				player.setTranslateY(platform.getLayoutY() - (player.getHeight() - 0.0001));
							verticalVelocity = 0;
							onGround = true;
						}
					} 
        		}
        	}
		}
		
		// Collisions cot�
		for (Shape platform : platforms) {
        	if (platform != player.playerRectangle) {
        		Shape intersect = Shape.intersect(player.playerRectangle, platform);
        		if (intersect.getBoundsInLocal().getHeight() != -1) {
        			if (intersect.getBoundsInLocal().getHeight() > intersect.getBoundsInLocal().getWidth()) {
						// Cot� -> MORT
						System.out.println("Ca c un bord -------------------------------------------------------------------------------------");
						verticalVelocity = 0;
						collisionDetected = true;
						playSound("Minecraft-Death-Sound-cut.wav", 4);
        			}
        		}
        	}
        }
		
		return collisionDetected;
		
	}

	/**
	 * Gestion de la collision avec les formes en triangles
	 */
	private boolean triangleCollision() {
		boolean collisionDetected = false;
		for (Shape triangle : triangles) {
			if (triangle != player.playerRectangle) {
				Shape intersect = Shape.intersect(player.playerRectangle, triangle);
				if (intersect.getBoundsInLocal().getWidth() != -1) {
					collisionDetected = true;
					System.out.println("Ca c un triangle -------------------------------------------------------------------------------------");
					playSound("Roblox-Death-Sound-cut.wav", 3);
					verticalVelocity = 0;
				}
			}
		}
		
		return collisionDetected;
	}
	
	
	/**
	 * Gestion de la collision avec un Coin (une pi�ce)
	 */
	private void coinCollision() {
		if(!edit) {
			// Check si le joueur touche une piece et change le statut de la piece
			for (Shape coin : coins) {
				if (coin != player.playerRectangle) {
					Shape intersect = Shape.intersect(player.playerRectangle, coin);
					if (intersect.getBoundsInLocal().getWidth() != -1) {
						coin.getProperties().put("alive", false);
						playSound("Coin.wav", 2);
					}
				}
			}
	
			// On supprime les coins ramass�s avec iterator car on ne peut pas delete quand on boucle sur la liste
			for (Iterator<Coin> it = coins.iterator(); it.hasNext(); ) {
				Shape coin = it.next();
				if (!(Boolean)coin.getProperties().get("alive")) {
					it.remove();
					rootLayout.getChildren().remove(coin);
					pieces ++;
					
					// Supprime le coin du tableau de rendu de la map
					if(coin instanceof Coin) {
						Coin c = (Coin) coin;
						int x = (int) Math.round(c.getLayoutX() / elementSize);
						int y = (int) Math.round(c.getLayoutY() / elementSize);
						
						mapRender[y][x] = null;
					}
				}
			}
		}
	}
	
	/**
	 * @return SI le joueur est au sol ou pas
	 */
	public boolean playerOnGround() {
		return onGround;
	}

	/**
	 * Calcule l'interval entre les frames, et affiche le nombre de fps calcul�
	 * @return L'interval entre chaque frame, en nanosecondes
	 */
	private double affFPS () {
		// Calculs FPS
		frame++;
		oldTime = newTime;
		newTime = System.nanoTime(); 
		dt = newTime - oldTime;

		// Affichage FPS
		if(System.currentTimeMillis() - time >= 1000) {
			//fps.setText("FPS : " + frame);
			System.out.println("FPS : " + frame);
			frame = 0;
			time = System.currentTimeMillis();				
		} 

		return dt;
	}

	public void startJump() {
		jump = true;
	}
	
	public void stopJump() {
		jump = false;
	}
	public void pause() throws IOException {
		if (!pause && !finish && musicPlayer != null) {
			pause = true;
			Ebus.get().post(new PauseEvent());
			
			FXMLLoader pauseLoader = new FXMLLoader();
			pauseLoader.setLocation(MainMenu.class.getResource("./AffichageMap/view/PauseMenu.fxml"));
			pauseMenu = (Pane) pauseLoader.load();
			pauseMenu.setLayoutX(-rootLayout.getLayoutX());
			PauseMenuController PauseController = pauseLoader.getController();
			
			PauseController.setController(this);
			
			// Met le jeu par dessus la grille
			rootLayout.getChildren().add(pauseMenu);
			
			musicPlayer.pause();
		}else if (pause && !finish){
			pause = false;
			rootLayout.getChildren().remove(pauseMenu);
			Ebus.get().post(new PauseEvent());
			if(!dead) {
				musicPlayer.play();
			}
		}
		
	}
	@Subscribe
	public void restart(RestartGameEvent e) {
		pause = false;
		rootLayout.getChildren().remove(pauseMenu);
		Ebus.get().post(new PauseEvent());
		if(!dead) {
			musicPlayer.play();
		}
	}
	public double getSpeedPlayer() {
		return player.getSpeed();
	}


	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit, double height) {
		this.edit = edit;
		this.toolBarHeight = (edit == true) ? height : 0;
	}

	public void setMap(String string) {
		this.level = string;
	}
	
	@SuppressWarnings("unchecked")
	public void saveCoin(int pieces) {
		FileWriter file = null;
		JSONObject obj = new JSONObject();
		obj.put("nbrsCoin", new Integer(pieces));
		
		try {
			file =new FileWriter("./pieces.json");
			file.write(obj.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
 
            try {
                file.flush();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }	
	}
	
	public void loadCoin() {
		
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
         
        try (FileReader reader = new FileReader("pieces.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            
            // Cast JSON file
            JSONObject JsonCoin = (JSONObject) obj;
            
            pieces = ((Long) JsonCoin.get("nbrsCoin")).intValue();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
	}
	
	private void fin(boolean recc) {
		if (recc == true) {
			
			popup();
			
			recc = false;
		}
	}
	
//	@Subscribe
	public void popup() {
		// Tailles max
		int width = 400;
		int height = 200;
		
		// Cr�ation de la vBox, et set de set propri�t�s et css
		VBox popup = new VBox();
		popup.setPrefWidth(width);
		popup.setMaxHeight(height);
		popup.setLayoutX((screenBounds.getWidth() / 2) - (popup.getPrefWidth()/ 2) - rootLayout.getLayoutX());
		popup.setLayoutY((screenBounds.getHeight() / 2)  - 150);
		popup.setAlignment(Pos.CENTER);
		popup.setStyle("-fx-background-color: #121212; -fx-background-radius: 10 10 10 10; -fx-padding: 10; -fx-border-color: #c50808; -fx-border-width: 5;");
		popup.setSpacing(50);
		popup.toFront();
		
		
		Label text = new Label();
		text.setText("Bravo !");
		text.setStyle("-fx-background-color: #121212; -fx-text-fill: yellow; -fx-font-size: 40px");
		
		Button retour = new Button();
		retour.setText("Menu");
		retour.setStyle("-fx-font-size: 30px; -fx-padding: 10 30 10 30");
		retour.setOnAction(evt -> {
			rootLayout.getChildren().remove(popup);
			
			if(!edit) {
				Parent menu = null;
				try {
					menu = FXMLLoader.load(getClass().getResource("../../Menu/GameMenu2.fxml"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Scene GameMenu = new Scene(menu);
				Ebus.get().unregister(this);
				
				Stage window = (Stage) rootLayout.getScene().getWindow();
				window.setScene(GameMenu);
				window.setMaximized(true);
				window.setHeight(1080);
				window.setWidth(1920);
				window.show();
				
			}
		});
		
		popup.getChildren().add(text);
		popup.getChildren().add(retour);
		rootLayout.getChildren().add(popup);
	}


	public void setStop() {
		time1.stop();
		musicPlayer.stop();
		
	}
	
	
	@Subscribe
	public void setPlayerState(PlayerState e) {
		
		if(e.getState()) {
			// Sauf si la timeline est stop,
			if(time1.getStatus() != Animation.Status.STOPPED) {
				// Le joueur respawn
				oldDiv = 0;
				
				// Reset de la cam
		    	rootLayout.setLayoutX(0); // TP la cam�ra au d�but du jeu
		    	Coin.setLayoutX(0);
				boxScore.setLayoutX((screenBounds.getWidth() / 2) - (pBar.getPrefWidth() / 2));
				progressProperty.set(0);
				
				running = e.getState();
				dead = !e.getState();
				
				musicPlayer.play();
				this.loadSpawn();
				Coin.toFront();
				boxScore.toFront();
			}
			
			rootLayout.getChildren().remove(ragdoll);
		} else {
			// Le joueur meurt
			running = e.getState();
			dead = !e.getState();
			
			// Placement du ragdoll de mort
			ragdoll.setWidth(player.getPlayerRectangle().getWidth() - (player.getPlayerRectangle().getWidth() / 2));
			ragdoll.setHeight(player.getPlayerRectangle().getHeight() - (player.getPlayerRectangle().getHeight() / 2));
			ragdoll.setLayoutX(player.getTranslateX() + player.getPlayerRectangle().getTranslateX() + (player.getPlayerRectangle().getWidth() / 4));
			ragdoll.setLayoutY(player.getTranslateY() + player.getPlayerRectangle().getTranslateY() + (player.getPlayerRectangle().getHeight() / 4));
			ragdoll.setFill(player.getPlayerRectangle().getFill());
			
			ragdoll.setOpacity(0.5);
			rootLayout.getChildren().add(ragdoll);
			
			saveCoin(pieces);
			musicPlayer.stop();
			
			if(bestScore < score) {
				bestScore = Math.floor(score);
				bestProperty.set("Best : " + bestScore + "%");				
			}
		}
	}
	
	
	/**
	 * Fais jouer un son se trouvant dans le dossier resources/audio/
	 * @param name Le nom du fichier (avec l'extension)
	 * @param volume Le volume de 0 � 10
	 */
	private void playSound(String name, double volume) {
		File file = new File("resources/audio/" + name);
		
		mediaSound = new Media(file.toURI().toString());
		
		mediaPlayer = new MediaPlayer(mediaSound);
		
		mediaPlayer.setVolume(volume / 10);
		mediaPlayer.play();
	}
	
	private void playMusic() {
		File file = new File("resources/audio/" + nameMusic);
		
		Media media = new Media(file.toURI().toString());
		
		musicPlayer = new MediaPlayer(media);
		
		musicPlayer.setVolume(1.5 / 10);
		musicPlayer.play();
	}
	
	private Color parseColor(String colors) {
		Color color = Color.BLUE; 
		switch (colors) {
			case "blue":
				color = Color.BLUE;
				break;
				
			case "red":
				color = Color.RED;
				break;
				
			case "green":
				color = Color.GREEN;
				break;
				
			case "yellow":
				color = Color.YELLOW;
				break;
		}
		return color;
	}
	// Passe une couleur de String � Color
    private String getHexColor(JSONObject jsonObject, String mapElement){
		String color;
		String hexColor;
		
		color = (String) ((JSONObject) jsonObject.get("color")).get(mapElement);

		hexColor = "#"+color.substring(2,8);
		return hexColor;
    }
	
}
