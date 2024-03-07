package cCarre.AffichageMap.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GroundSlab extends Rectangle{

	public GroundSlab(int x, int y, int width, int height, Color color) {
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.setWidth(width);
        this.setHeight(height/3);
        this.setFill(color);
        this.getProperties().put("alive", true);
	}
}
