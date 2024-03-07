package cCarre.genmap.model;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import cCarre.genmap.events.AddLengthGrilleEvent;
import cCarre.genmap.events.Ebus;
import cCarre.genmap.events.LaunchGameEvent;
import cCarre.genmap.events.PopupEvent;
import cCarre.genmap.events.RemoveLengthGrilleEvent;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class Cell extends Region {
	private boolean occuped = false;
	private int width;
	private int x, y;
	private Rectangle back;
	private Rectangle selection;
	private Rectangle hover;
	private char cellId;
	private boolean selected = false;

	public Cell(Node cell) {
		super();
	}

	public Cell(int width, int x, int y) {
		super();
		this.width = width;
		this.x = x;
		this.y = y;
		this.cellId = '0';
		
		back = new Rectangle();
		back.setFill(Color.FLORALWHITE);
		back.setWidth(width);
		back.setHeight(width);
		back.setMouseTransparent(true);
		this.getChildren().add(back);
		
		this.setPrefWidth(width);
		this.setPrefHeight(width);
		
		selection = new Rectangle();
		selection.setStroke(Color.YELLOW);
		selection.setStrokeWidth(4);
		selection.setStrokeType(StrokeType.INSIDE);
		selection.setFill(Color.TRANSPARENT);
		selection.setWidth(width);
		selection.setHeight(width);
		selection.setOpacity(0);
		selection.setMouseTransparent(true);
		
		
		hover = new Rectangle();
		hover.setFill(Color.DARKCYAN);
		hover.setWidth(width);
		hover.setHeight(width);
		hover.setOpacity(0);
		hover.setMouseTransparent(true);
		
		this.getChildren().add(hover);
		
		
		this.getChildren().add(selection);
		
		this.setOnMousePressed(e -> {
			if(e.getButton() == MouseButton.PRIMARY) {
				e.setDragDetect(true);
				onPaint();
			} else if(e.getButton() == MouseButton.SECONDARY && !ToolBar.getItem().equals("test")) {
				e.setDragDetect(true);
				erase(true);
			}
		});
		this.setOnDragDetected(e -> {
			if(e.getButton() == MouseButton.PRIMARY) {
				this.startFullDrag();
			} else if(e.getButton() == MouseButton.SECONDARY && !ToolBar.getItem().equals("test")) {
				this.startFullDrag();
			}
		});
		this.setOnMouseDragOver (e -> {
			if(e.getButton() == MouseButton.PRIMARY) {
				onPaint();
			} else if(e.getButton() == MouseButton.SECONDARY && !ToolBar.getItem().equals("test")) {
				erase(true);
			}
		});
		
		
		
		this.setOnMouseEntered(e -> {
			if(ToolBar.getItem().equals("test")) {
				hover.setOpacity(0.3);
			}
		});
		this.setOnMouseExited(e -> {
			if(ToolBar.getItem().equals("test")) {
				hover.setOpacity(0);
			}
		});
	}
	
	public void setBack(Color color) {
		this.back.setFill(color);
	}
	
	private void onPaint() {
		if(!occuped || (occuped && ToolBar.getItem().equals("test") && this.getCellId() == '8')) {
			paint(null);
		} 
	}
	
	public void loadMapPaint(JSONObject mapObject) {
		occuped = true;
		
		// Met le background color
		Color backgroundColor = stringToColor(mapObject, "background");
		ToolBar.setBackgroundColor(backgroundColor);
		
		switch (cellId) {
		case '0':
			erase(true);
			break;
			
		case '1': 
			Rectangle ground = new Rectangle();
			ground.setWidth(width);
			ground.setHeight(width);
			ground.setFill(stringToColor(mapObject, "ground"));
			ground.setMouseTransparent(true);

			this.getChildren().add(ground);
			break;
			
		case '2':
			// Ajoute un carré vide avant de mettre le triangle
			Rectangle vide2 = new Rectangle();
			vide2.setWidth(width);
			vide2.setHeight(width);
			vide2.setFill(backgroundColor);
			vide2.setMouseTransparent(true);

			this.getChildren().add(vide2);
			
			// Ajoute le triangle
			Polygon triangle = new Polygon();
			triangle.getPoints().addAll(new Double[]{
	                (double) width / 2, (double) 0, 
	                (double) 0, (double) width, 
	                (double) width, (double) width, 
	             });
			triangle.setFill(stringToColor(mapObject, "obstacle"));
			triangle.setMouseTransparent(true);

			this.getChildren().add(triangle);
			break;
			
		case '3': 
			// Ajoute un carre vide avant de mettre le coin
			Rectangle vide3 = new Rectangle();
			vide3.setWidth(width);
			vide3.setHeight(width);
			vide3.setFill(backgroundColor);
			vide3.setMouseTransparent(true);

			this.getChildren().add(vide3);
			
			//Ajoute la piece
			Rectangle coin = new Rectangle();
			coin.setWidth(width/2);
			coin.setHeight(width/2);
			coin.setTranslateX(width / 4);
			coin.setTranslateY(width / 4);
			coin.setFill(stringToColor(mapObject, "coin"));
			coin.setMouseTransparent(true);

			this.getChildren().add(coin);
			break;
			
		case '4':
			// Ajoute un carré vide avant de mettre le triangle
			Rectangle vide4 = new Rectangle();
			vide4.setWidth(width);
			vide4.setHeight(width);
			vide4.setFill(backgroundColor);
			vide4.setMouseTransparent(true);

			this.getChildren().add(vide4);
			
			// Ajoute le triangle
			Polygon reverseTriangle = new Polygon();
			reverseTriangle.getPoints().addAll(new Double[]{
	                (double) (0), (double) 0,
	                (double) width, (double) (0), 
	                (double) (width/2), (double) (width), 
	             });
			reverseTriangle.setFill(stringToColor(mapObject, "obstacle"));
			reverseTriangle.setMouseTransparent(true);

			this.getChildren().add(reverseTriangle);
			break;
			
		case '5': 
			Rectangle groundSlab = new Rectangle();
			groundSlab.setWidth(width);
			groundSlab.setHeight(width/3);
			groundSlab.setFill(stringToColor(mapObject, "ground"));
			groundSlab.setMouseTransparent(true);

			this.getChildren().add(groundSlab);
			break;
			
		case '6': 
			Rectangle pillar = new Rectangle();
			pillar.setX(width/3);
			pillar.setWidth(width/3);
			pillar.setHeight(width);
			pillar.setFill(stringToColor(mapObject, "ground"));
			pillar.setMouseTransparent(true);

			this.getChildren().add(pillar);
			break;
			
		case '8':
			if(!ToolBar.isStartPlaced()) {
				// S'il n'y a pas encore de dï¿½part placï¿½
				
				if(ToolBar.isEndPlaced() == false || ToolBar.getEndPlace() > this.x) {
					// Si la fin est bien a droite du start, ou pas placï¿½e
					Rectangle start = new Rectangle();
					start.setWidth(width);
					start.setHeight(width);
					start.setFill(Color.DARKGREEN);
					start.setId("start");
					start.setMouseTransparent(true);
					cellId = '8';
					
					this.getChildren().add(start);
					ToolBar.setStartPlaced(x);
					
				} else {
					occuped = false;
					
					Ebus.get().post(new PopupEvent("Attention !", "Le dï¿½part doit ï¿½tre placï¿½ ï¿½ gauche de l'arrivï¿½e"));
				}
			} else {
				// Si un dï¿½part a dï¿½jï¿½ ï¿½tï¿½ placï¿½ 
				occuped = false;

				Ebus.get().post(new PopupEvent("Attention !", "Un dï¿½part a dï¿½jï¿½ ï¿½tï¿½ placï¿½"));
			}
			break;
			
		case '9':
			if(!ToolBar.isEndPlaced()) {
				// S'il n'y a pas encore d'arrivï¿½e placï¿½e
				
				if(ToolBar.isStartPlaced() == false || ToolBar.getStartPlace() < this.x) {
					// Si le start est bien a gauche de la fin, ou pas placï¿½e
					Rectangle end = new Rectangle();
					end.setWidth(width);
					end.setHeight(width);
					end.setFill(Color.DARKRED);
					end.setId("end");
					end.setMouseTransparent(true);
					cellId = '9';
					
					this.getChildren().add(end);
					ToolBar.setEndPlaced(x);
					
				} else {
					occuped = false;
					
					Ebus.get().post(new PopupEvent("Attention !", "L'arrivï¿½e doit ï¿½tre placï¿½e ï¿½ droite du dï¿½part"));
				}
			} else {
				// Si une arrivï¿½e a dï¿½jï¿½ ï¿½tï¿½ placï¿½e
				occuped = false;

				Ebus.get().post(new PopupEvent("Attention !", "Une arrivï¿½e a dï¿½jï¿½ ï¿½tï¿½ placï¿½e"));
			}
			break;
			
		case 's': 
			cellId = 's';
			break;
			
		default:
			cellId = 0;
			occuped = false;
			break;
		}
		
		// Si la case a ï¿½tï¿½ peinte, on vï¿½rifie si le x est sup au plus grand x, pour la taille de la grille 
		if(occuped) {
			if(ToolBar.getMostX() < x) {
				Ebus.get().post(new AddLengthGrilleEvent(x));
			}
		}
	}
	
	// Passe une couleur de String ï¿½ Color
    Color stringToColor(JSONObject jsonObject, String mapElement){
		Color customColor;
		String color;
		String hexColor;
		
		color = (String) ((JSONObject) jsonObject.get("color")).get(mapElement);

		hexColor = "#"+color.substring(2,8);
        customColor = Color.valueOf(hexColor);
		return customColor;
    }
	
	// Mettre customItem en null par defaut, customItem est utilisï¿½ par le changement de couleur
	public void paint(String customItem) {
		occuped = true;
		
		// Regarde quel item est sï¿½lectionnï¿½ pour la peinture
		String item = ToolBar.getItem();
		if(customItem != null) {
			item = customItem;
		}
		
		switch (item) {
		case "groundBtn": 
            
            //Crï¿½er le rectangle
			Rectangle ground = new Rectangle();
			ground.setWidth(width);
			ground.setHeight(width);
			ground.setFill(ToolBar.getGroundColor());
			ground.setMouseTransparent(true);
			cellId = '1';
			
			this.getChildren().add(ground);
			break;
			
		case "obstacleBtn":
			Polygon triangle = new Polygon();
			triangle.getPoints().addAll(new Double[]{
	                (double) width / 2, (double) 0, 
	                (double) 0, (double) width, 
	                (double) width, (double) width, 
	             });
			triangle.setFill(ToolBar.getObstacleColor());
			triangle.setMouseTransparent(true);
			cellId = '2';

			this.getChildren().add(triangle);
			break;
			
		case "coinBtn": 
			Rectangle coin = new Rectangle();
			coin.setWidth(width/2);
			coin.setHeight(width/2);
			coin.setTranslateX(width / 4);
			coin.setTranslateY(width / 4);
			coin.setFill(ToolBar.getCoinColor());
			coin.setMouseTransparent(true);
			cellId = '3';
			//Coin coin = new Coin(x*elementSize + (elementSize / 4), y*elementSize + (elementSize / 4), elementSize / 2, elementSize / 2, Color.YELLOW, rootLayout);
			this.getChildren().add(coin);
			break;
			
		case "reverseObstacleBtn":
			Polygon reverseTriangle = new Polygon();
			reverseTriangle.getPoints().addAll(new Double[]{
	                (double) (0), (double) 0,
	                (double) width, (double) (0), 
	                (double) (width/2), (double) (width), 
	             });
			reverseTriangle.setFill(ToolBar.getObstacleColor());
			reverseTriangle.setMouseTransparent(true);
			cellId = '4';
			
			this.getChildren().add(reverseTriangle);
			break;
			
		case "groundSlabBtn": 
            
            //Crï¿½er le rectangle
			Rectangle groundSlab = new Rectangle();
			groundSlab.setWidth(width);
			groundSlab.setHeight(width/3);
			groundSlab.setFill(ToolBar.getGroundColor());
			groundSlab.setMouseTransparent(true);
			cellId = '5';
			
			this.getChildren().add(groundSlab);
			break;
		case "pillarBtn": 
            
            //Crï¿½er le rectangle
			Rectangle pillar = new Rectangle();
			pillar.setX(width/3);
			pillar.setWidth(width/3);
			pillar.setHeight(width);
			pillar.setFill(ToolBar.getGroundColor());
			pillar.setMouseTransparent(true);
			cellId = '6';
			
			this.getChildren().add(pillar);
			break;
			
		case "departBtn":
			if(!ToolBar.isStartPlaced()) {
				// S'il n'y a pas encore de dï¿½part placï¿½
				
				if(ToolBar.isEndPlaced() == false || ToolBar.getEndPlace() > this.x) {
					// Si la fin est bien a droite du start, ou pas placï¿½e
					Rectangle start = new Rectangle();
					start.setWidth(width);
					start.setHeight(width);
					start.setFill(Color.DARKGREEN);
					start.setId("start");
					start.setMouseTransparent(true);
					cellId = '8';
					
					this.getChildren().add(start);
					ToolBar.setStartPlaced(x);
					
				} else {
					occuped = false;
					
					Ebus.get().post(new PopupEvent("Warning !", "The start must be placed to the left of the finish line"));
				}
			} else {
				// Si un dï¿½part a dï¿½jï¿½ ï¿½tï¿½ placï¿½ 
				occuped = false;

				Ebus.get().post(new PopupEvent("Warning !", "A Start has already been placed"));
			}
			break;
			
		case "arriveeBtn":
			if(!ToolBar.isEndPlaced() || ToolBar.getEndPlace() == this.x) {
				// S'il n'y a pas encore d'arrivï¿½e placï¿½e
				
				if(ToolBar.getStartPlace() < this.x) {
					// Si le start est bien a gauche de la fin, ou pas placï¿½e
					Rectangle end = new Rectangle();
					end.setWidth(width);
					end.setHeight(width);
					end.setFill(Color.DARKRED);
					end.setId("end");
					end.setMouseTransparent(true);
					cellId = '9';
					
					this.getChildren().add(end);
					ToolBar.setEndPlaced(x);
					
				} else {
					occuped = false;
					
					Ebus.get().post(new PopupEvent("Warning !", "The finish line must be placed to the right of the start"));
				}
			} else {
				// Si une arrivï¿½e a dï¿½jï¿½ ï¿½tï¿½ placï¿½e
				occuped = false;

				Ebus.get().post(new PopupEvent("Warning !", "A finish line has alerady been placed"));
			}
			break;
			
		case "test": 
			char oldId = cellId;
			cellId = 's';
			ToolBar.setItem("");
			hover.setOpacity(0);

			Ebus.get().post(new LaunchGameEvent());
			cellId = oldId;
			occuped = (cellId == '8') ? true : false; 
			System.out.println(cellId);
			break;
			
		default:
			occuped = false;
			break;
		}
		
		// Si la case a ï¿½tï¿½ peinte, on vï¿½rifie si le x est sup au plus grand x, pour la taille de la grille 
		if(occuped) {
			if(ToolBar.getMostX() < x) {
				Ebus.get().post(new AddLengthGrilleEvent(x));
			}
		}
	}
	
	// RemoveLenght n'est sur false que pour le changement de couleur dans le colorPicker
	public void erase(boolean RemoveLenght) {
		ArrayList<Node> toRem = new ArrayList<Node>();
		if(occuped) {
			for(Node node : this.getChildren()) {
				if(node != back && node != selection && node != hover) {
					// Vï¿½rifie si c'est le start ou le dï¿½but qui a ï¿½tï¿½ delete
					ToolBar.setStartPlaced((node.getId() == "start" ) ? -1 : ToolBar.getStartPlace());
					ToolBar.setEndPlaced((node.getId() == "end" ) ? -1 : ToolBar.getEndPlace());
					
					toRem.add(node);
					cellId = '0';
				}
			}
			for(Node rem : toRem) {
				this.getChildren().remove(rem);
			}
			
			if(ToolBar.getMostX() == x && RemoveLenght == true) {
				Ebus.get().post(new RemoveLengthGrilleEvent(x));
			}
			occuped = false;
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + " / x: " + this.x + " / y: " + this.y;
	}	
	
	public char getCellId() {
		return cellId;
	}

	public void setCellId(char cellId) {
		this.cellId = cellId;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public int getMyWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		if(selected && !this.selected) {
			selection.setOpacity(1);
			selection.toFront();
			
		} else if(!selected && this.selected) {
			selection.setOpacity(0);
		}
		this.selected = selected;
	}
}
