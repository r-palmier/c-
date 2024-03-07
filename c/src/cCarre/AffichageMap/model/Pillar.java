package cCarre.AffichageMap.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Pillar extends Rectangle{

	public Pillar(int x, int y, int width, int height, Color color) {
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.setX(width/3);
        this.setWidth(width/3);
        this.setHeight(height);
        this.setFill(color);
        this.getProperties().put("alive", true);
	}
}
