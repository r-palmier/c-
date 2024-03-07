package cCarre.AffichageMap.model;

import java.awt.geom.Point2D;

import com.google.common.eventbus.Subscribe;

import cCarre.genmap.events.Ebus;
import cCarre.genmap.events.MoveGridEvent;
import cCarre.genmap.events.PauseEvent;
import cCarre.genmap.events.PlayerState;
import javafx.animation.Animation.Status;
import javafx.animation.PauseTransition;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Player extends Parent{
	
	public double centreX, centreY;
	
	public Rectangle playerRectangle;
	
	public Point2D p1, p2, p3;
	
	public Line vVitesse, vG, vVert;
	
	int height,width;
	
	int constGrav;
	
	PauseTransition delay = new PauseTransition(Duration.seconds(1));;
	
	public void SetColor(Color color) {
		playerRectangle.setFill(color);
	}
	
	public Player(int x, int y, int width, int height, Color color, AnchorPane rootLayout, int constGrav, int constV) {
		this.setWidth(width);
		this.setHeight(height);
		
		this.height = height;
		this.width = width;
		this.constGrav = constGrav;
		
		playerRectangle = new Rectangle();
		
		this.setTranslateX(x);
		this.setTranslateY(y);
		playerRectangle.setWidth(width);
		playerRectangle.setHeight(height);
		playerRectangle.setFill(color);
		playerRectangle.getProperties().put("alive", true);
        
		this.getChildren().add(playerRectangle);
		
		// init centre cube
		centreX = playerRectangle.getWidth() / 2;
		centreY = playerRectangle.getHeight() / 2;
		
		//init point
		Point2D p1 = new Point2D.Double(centreX + constV, centreY);
		Line vVitesse = new Line(centreX,centreY,p1.getX() / 4,p1.getY());
		vVitesse.setStroke(Color.RED);
		this.p1 = p1;
		this.vVitesse = vVitesse;
		
		Point2D p2 = new Point2D.Double(centreX, centreY + constGrav);
		Line vG = new Line(centreX,centreY,p2.getX(),(p2.getY() / 4));
		vG.setStroke(Color.PURPLE);
		this.p2 = p2;
		this.vG = vG;

		Point2D p3 = new Point2D.Double(centreX, centreY);
		Line vVert = new Line(centreX,centreY,p3.getX(),p3.getY()/4);
		vVert.setStroke(Color.GREEN);

		this.p3 = p3;
		this.vVert = vVert;

		// Ajout des vecteurs au joueur
//		this.getChildren().add(vVitesse);
//		this.getChildren().add(vG);
//		this.getChildren().add(vVert);
        
        rootLayout.getChildren().add(this);
        
        Ebus.get().register(this);
	}
	
	public void depl(double distanceX, double distanceY, double jumpForce, double verticalVelocity) {
		this.setTranslateX(this.getTranslateX() + distanceX);
		this.setTranslateY(this.getTranslateY() - distanceY);
		
		vVitesse.setStartX(centreX);
		vVitesse.setEndX(p1.getX()/4);
		
		vG.setStartY(centreY);
		vG.setEndY(p2.getY()/4);
		
		vVert.setStartY(centreY);
		vVert.setEndY(p3.getY()/4);
		
		p1.setLocation(p1.getX(), centreY);
		p2.setLocation(p2.getX(), centreY + constGrav);
		p3.setLocation(p3.getX(), centreY - verticalVelocity);
	}
	
	public void respawn(double X, double Y){
		this.setTranslateX(X);
		this.setTranslateY(Y);
		
		vVitesse.setStartX(centreX);
		vVitesse.setEndX(p1.getX());
		
		vG.setStartY(centreY);
		vG.setEndY(p2.getY());
		
		vVert.setStartY(centreY);
		vVert.setEndY(p3.getY());
	}
	
	public void death(double spawnX, double spawnY, AnchorPane rootLayout, Label Coin){
		// Pause du jeu et lancement de l'anim de mort
		Ebus.get().post(new PlayerState(false));
		
		// Disparition du player
		this.setOpacity(0);
		
		// Pause de 1s, puis fait disparaitre la popup
		delay = new PauseTransition(Duration.seconds(1));
		delay.setOnFinished( event -> {
			// Set le player au d�part
			respawn(spawnX, spawnY);
			
	    	Ebus.get().post(new MoveGridEvent(0));
	    	
	    	// Arrete l'anim de mort
	    	Ebus.get().post(new PlayerState(true));
	    	
	    	// R�apparition du player
	    	this.setOpacity(1);
	    	
		});
		delay.play();
		
	}
	
	@Subscribe
	public void pause(PauseEvent e) {
		if(delay.getStatus() == Status.PAUSED) {
			delay.play();
		} else {
			delay.pause();
		}
	}

	public int getHeight() {
		return height;
	}



	public void setHeight(int height) {
		this.height = height;
	}



	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public Rectangle getPlayerRectangle() {
		return playerRectangle;
	}
	
	public double getSpeed() {
		return p1.distance(centreX, centreY);
	}
}
