/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import controller.MainController;
import java.util.prefs.Preferences;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

/**
 *
 * @author dell
 */
public class AskDialog {
    public AskDialog(){};
     
    public void alert(String s)
    {   boolean check=false;
        ButtonType Yes = new ButtonType("Yes"); 
        ButtonType No = new ButtonType("NO", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert a = new Alert(Alert.AlertType.NONE); 
        a.setTitle("Alert ASk");
        a.getDialogPane().getButtonTypes().addAll(Yes,No);
        a.setHeaderText(s);

         //a.setContentText(s);
         DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(
        getClass().getResource("/css/fullpackstyling.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");

        a.showAndWait();
       
           if(a.getResult()==Yes)
           {  
             //  Preferences pfrefs= Preferences.userNodeForPackage(MainController.class);   

               check=true;
               AccessFile.createFile();
             //  AccessFile.writeFile(pfrefs.get("username","not found")+".");  
           }
           else if (a.getResult()==No)
                   {
                       //check=true;
                   }  
    }
    
}
