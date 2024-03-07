package cCarre.AffichageMap.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Level {
	private final int idLevel;
	private int levelLength;
	private int totalCoin;
	private int levelWidth;
	private int levelHeight;
	
	private static JSONObject jsonMap;
	
	private static boolean preview = false;
	private static int elemHeight;

	public Level() {
		this.idLevel = 0;
		this.levelLength = ((JSONArray) ((JSONArray) jsonMap.get("map")).get(0)).size();
		this.levelWidth = this.levelLength * 60;
		this.totalCoin = 0;
		this.levelHeight = ((JSONArray) jsonMap.get("map")).size();
	}
	
	public static boolean isPreview() {
		return preview;
	}

	public static void setPreview(boolean preview) {
		Level.preview = preview;
	}
	
	public int getLevelWidth(){
		return levelWidth;
	}

	public JSONObject getLevel() {
		return jsonMap;
	}

	public int getLevelHeight() {
		return levelHeight;
	}

	public void setLevelHeight(int levelHeight) {
		this.levelHeight = levelHeight;
	}

	public int getLevelLength() {
		return levelLength;
	}

	public void setLevelLength(int levelLength) {
		this.levelLength = levelLength;
	}

	public int getTotalCoin() {
		return totalCoin;
	}

	public void setTotalCoin(int totalCoin) {
		this.totalCoin = totalCoin;
	}

	public int getIdLevel() {
		return idLevel;
	}
	
	/**
	 * M�thode servant � d�finir le level � utiliser, � set avant d'instancier Level (et donc le contrller du jeu)
	 * @param json Le JSONArray de la map a utiliser
	 */
	public static void setJsonLevel(JSONObject json) {
		Level.jsonMap = json;
	}

	public static void setElemHeight(int elemHeight) {
		Level.elemHeight = elemHeight;
	}
	
	public static int getElemHeight() {
		return Level.elemHeight;
	}
}
