package cCarre.AffichageMap.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Obstacle extends Polygon{
	
	int elementSize;

	public Obstacle(int x, int y, int width, int height, Color color) {
		this.elementSize = width;     
		this.getPoints().addAll(new Double[]{
                (double) elementSize / 2, (double) 0, 
                (double) 0, (double) elementSize, 
                (double) elementSize, (double) elementSize, 
             }); 
		this.setLayoutX(x);
		this.setLayoutY(y);

        this.setFill(color);
	}
}
