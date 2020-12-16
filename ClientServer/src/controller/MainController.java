package controller;
import helper.AccessFile;
import helper.AskDialog;
import helper.CurrentDateTime;
import helper.CustomDialog;
import helper.IPvalidatation;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class MainController implements Initializable{

    
    @FXML
    private  Button btnWatchGame;
    @FXML
    private Rectangle recWatchGame;

    private boolean btnEnable = false;
    Preferences prefs ;
    int checkname;
    static boolean isrecord=false;
    static boolean x=false;
     static boolean checkip=false;

    Socket socket;
    DataInputStream dis;
    PrintStream ps;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
           btnWatchGame.setDisable(false);
           recWatchGame.setVisible(false); 
      /*
        if(btnEnable)
        {
            btnWatchGame.setDisable(false);
            recWatchGame.setVisible(false); 
        }**/
        prefs = Preferences.userNodeForPackage(MainController.class);            
    }

    /**
     * changeSceneToSinglePlayer.
     * when called scene will be change to single player mode.
     * @param event 
     */
    public void changeSceneToSinglePlayer(ActionEvent event) {
        try {
         //   CurrentDateTime c=new CurrentDateTime();
        // System.out.println(c.getCurrentDateTime());

            if(prefs.nodeExists("/controller"))
            {
                String s=prefs.get("username","");
                System.out.println(s.length());
                if(s.length()==0)
                {
                    CustomDialog cd = new CustomDialog();
                    Boolean isCancled = cd.displayDialog("Enter Your Name");
                    prefs.put("username", cd.getName());
                    if(!isCancled){
                        try {
                            //get scene
                            AskDialog isrecoredGame = new AskDialog();
                  Boolean check=isrecoredGame.alert("Do you want to record game ?");
                //  if(check)
                //  {
                   //  AccessFile.createFile();
                     
                  //   isrecord=true;
                  //}
                    
                            Parent singlePlayerParent = FXMLLoader.load(getClass().getResource("/view/SinglePlayFXML.fxml"));
                            
                            //generate new scene
                            Scene singlePlayerScene = new Scene(singlePlayerParent,btnWatchGame.getScene().getWidth(),
                                    btnWatchGame.getScene().getHeight());
                            
                            //get stage information
                            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                            window.setTitle("Single play Mode");
                            window.setScene(singlePlayerScene);
                            window.show();
                         }catch (IOException ex) {
                            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                
            else
            {
                  AskDialog isrecoredGame = new AskDialog();
                  Boolean check=isrecoredGame.alert("Do you want to record game ?");
                  if(check)
                  {
                   AccessFile.createFile();
                   AccessFile.writeFile("username1"+".");
                   AccessFile.writeFile("username2"+".");

                     isrecord=true;
                  }
                  
            
                Parent singlePlayerParent = FXMLLoader.load(getClass().getResource("/view/SinglePlayFXML.fxml"));
                
                //generate new scene
                Scene singlePlayerScene = new Scene(singlePlayerParent,btnWatchGame.getScene().getWidth(),
                        btnWatchGame.getScene().getHeight());
                
                //get stage information
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                
                window.setTitle("Single play Mode");
                window.setScene(singlePlayerScene);
                window.show();
                
            }
            }
            
    } catch (BackingStoreException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
                           
    }
    /**
     * changeSceneToTwoPlayers.
     * when called scene will be change to Two players mode.
     * @param event 
     */
    public void changeSceneToTwoPlayers(ActionEvent event) {
        
        System.out.println("changeSceneToTwoPlayers: called");
        try {
            //get scene
           Parent twoPlayerParent = FXMLLoader.load(getClass().getResource("/view/TwoPlayerFXML.fxml"));
            //generate new scene
            Scene twoPlayerScene = new Scene(twoPlayerParent,btnWatchGame.getScene().getWidth(),
           btnWatchGame.getScene().getHeight());
        
            //get stage information
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
            window.setTitle("Two players Mode");
            window.setScene(twoPlayerScene);
            window.show();
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }       
        
    }

    /**
     * changeSceneToOnlineGame.
     * when called scene will be change to online game mode.
     * @param event 
     */
    public void changeSceneToOnlineGame(ActionEvent event) {

        System.out.println("changeSceneToOnlineGame: called");
               if(!checkip)
                {     System.out.println(checkip);               
                 CustomDialog cd = new CustomDialog();
                 Boolean isCancled = cd.displayDialog("Enter Server IP");
                   
                 if(IPvalidatation.isValidIPAddress(cd.getName()))
                    { 
                        System.out.println("you entered ip ="+cd.getName());
                    if(!isCancled){
                             
                            try {
                                checkip=true;
                                socket = new Socket(IPvalidatation.getIp(),9876);
                                System.out.println("conncet valid ip ");
                                System.out.println(IPvalidatation.getIp());
                                dis = new DataInputStream(socket.getInputStream());
                                ps = new PrintStream(socket.getOutputStream());

                                //get scene
                                Parent singlePlayerParent = FXMLLoader.load(getClass().getResource("/view/LoginOrRegister.fxml"));
                                //generate new scene
                                Scene singlePlayerScene = new Scene(singlePlayerParent,btnWatchGame.getScene().getWidth(),
                                 btnWatchGame.getScene().getHeight());
                                
                                //get stage information
                                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                                window.setTitle("Single play Mode");
                                window.setScene(singlePlayerScene);
                                window.show();
                            }catch (IOException ex) {
                                 System.out.println("disconnect");
                                 AskDialog AskIp=new AskDialog();
                                Boolean c= AskIp.alert("You Enter Not Valid Ip Do You Want To Enter IP Again");
                                if(c)
                                {
                                   cd.displayDialog("Enter Server IP");
                                   checkip=true;
                                 }
                                else {checkip=false;
                                }
                                }
                      
                   }
                    
                    }
                }  
               else if (checkip)
               { 
            try {
                Parent singlePlayerParent = FXMLLoader.load(getClass().getResource("/view/LoginOrRegister.fxml"));
                
                //generate new scene
                Scene singlePlayerScene = new Scene(singlePlayerParent,btnWatchGame.getScene().getWidth(),
                        btnWatchGame.getScene().getHeight());
                
                //get stage information
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setTitle("Single play Mode");
                window.setScene(singlePlayerScene);
                window.show();
            } catch (IOException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
               }
    
    }
    
    /**
     * changeSceneToWatchGame.
     * when called scene will be change to watch game mode.
     * @param event 
     */
     public void changeSceneToWatchGame(ActionEvent event){
         System.out.println("changeSceneToOnlineGame: called");
        
        try {
            //get scene

            Parent onlineGameParent = FXMLLoader.load(getClass().getResource("/view/ListRecordedGames.fxml"));

            //generate new scene
            Scene onlineGameScene = new Scene(onlineGameParent,btnWatchGame.getScene().getWidth(),
           btnWatchGame.getScene().getHeight());
        
            //get stage information
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

            window.setTitle("ListRecordedGames");
            window.setScene(onlineGameScene);
            window.show();
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }       
    
    }
}
