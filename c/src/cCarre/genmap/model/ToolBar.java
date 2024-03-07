package cCarre.genmap.model;

import javafx.scene.paint.Color;

public final class ToolBar {
	private static String item;
	private static boolean test = false;
	private static int mostX = 0;
	private static int startPlaced = -1;
	private static int endPlaced = -1;
	private static Color backgroundColor = null;
	private static Color groundColor = null;
	private static Color obstacleColor = null;
	private static Color coinColor = null;

	public ToolBar() {}
	
	public static void init() {
		item = "";
		test = false;
//		click;
		mostX = 0;
		startPlaced = -1;
		endPlaced = -1;
		backgroundColor = null;
		groundColor = null;
		obstacleColor = null;
		coinColor = null;
	}
	
	
	// Getter - Setters ---------------------------------------------------------------------------
	public static void setItem(String id) {
		item = id;
	}
	
	public static String getItem() {
		if(item == null) {
			return "rien";
		} 
		return item;			
	}

	public static int getMostX() {
		return mostX;
	}

	public static void setMostX(int mostX) {
		ToolBar.mostX = mostX;
	}
	
	public static boolean isStartPlaced() {
		return (startPlaced == -1) ? false : true;
	}

	public static void setStartPlaced(int startPlaced) {
		ToolBar.startPlaced = startPlaced;
	}
	
	public static int getStartPlace() {
		return startPlaced;
	}

	public static boolean isEndPlaced() {
		return (endPlaced == -1) ? false : true;
	}

	public static void setEndPlaced(int endPlaced) {
		ToolBar.endPlaced = endPlaced;
	}
	
	public static int getEndPlace() {
		return endPlaced;
	}
	
	// Couleurs
	
	public static Color getBackgroundColor() {
		return groundColor;
	}

	public static void setBackgroundColor(Color backgroundColor) {
		ToolBar.backgroundColor = backgroundColor;
	}
	
	public static Color getGroundColor() {
		return groundColor;
	}

	public static void setGroundColor(Color groundColor) {
		ToolBar.groundColor = groundColor;
	}

	public static Color getObstacleColor() {
		return obstacleColor;
	}

	public static void setObstacleColor(Color obstacleColor) {
		ToolBar.obstacleColor = obstacleColor;
	}

	public static Color getCoinColor() {
		return coinColor;
	}

	public static void setCoinColor(Color coinColor) {
		ToolBar.coinColor = coinColor;
	}
}
