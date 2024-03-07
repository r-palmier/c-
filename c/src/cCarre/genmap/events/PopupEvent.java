package cCarre.genmap.events;

public class PopupEvent {
	String title = "";
	String text = "";
	
	/**
	 * Cr�e un popup sur l'�diteur, avec le titre en premi�re ligne et le texte en dessous
	 * @param title Le titre 
	 * @param text Le contenu
	 */
	public PopupEvent(String title, String text) {
		this.title = title;
		this.text = text;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
}
