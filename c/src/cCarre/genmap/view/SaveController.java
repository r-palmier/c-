package cCarre.genmap.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SaveController implements Initializable {

    FileChooser fileChooser = new FileChooser();
    JSONObject levelObject;

    private String contenu = "";
        
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Json file (*.json", "*.json");
    	fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setInitialDirectory(new File("C:\\"));
    }
    
    //Added null check to check rather a file is picked or not
    @FXML
    void getText(MouseEvent event) {
        File file = fileChooser.showOpenDialog(new Stage());

        if(file != null){
            try {
                Scanner scanner = new Scanner(file);
                while(scanner.hasNextLine()){
                	contenu+=(scanner.nextLine() + "\n");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void save(MouseEvent event) throws IOException {
        File file = fileChooser.showSaveDialog(new Stage());
        if(file != null){
        	saveSystem(file, contenu);
        }
    }
    
    public void saveSystem(File file, String content) throws IOException{
        try {
        	PrintWriter printWriter = new PrintWriter(file);

        	levelObject.writeJSONString(printWriter);

            printWriter.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
	public void setData(JSONObject customMapObject) throws IOException {
		// R�cup�re la map
		levelObject = customMapObject;
		System.out.println(((JSONArray) ((JSONArray) customMapObject.get("map")).get(1)).get(1).getClass());
		// Lance la save
		save(null);
	}
}