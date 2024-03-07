package cCarre.AffichageMap.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class ReverseObstacle extends Polygon{
	
	int elementSize;

	public ReverseObstacle(int x, int y, int width, int height, Color color) {
		this.elementSize = width;     
		this.getPoints().addAll(new Double[]{
                (double) (0), (double) 0,
                (double) elementSize, (double) (0), 
                (double) (elementSize/2), (double) (elementSize), 
             }); 
		this.setLayoutX(x);
		this.setLayoutY(y);

        this.setFill(color);
	}
}
