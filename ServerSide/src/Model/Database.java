/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.jdbc.ClientDriver;

/**
 *
 * @author Amin
 */
public class Database {
    private static Database instanceData;
    private Connection con;
    private ResultSet rs ;
    private PreparedStatement pst;
    
    public synchronized ResultSet getResultSet(){
        return rs;
    }
    
    private  Database() throws SQLException{
         DriverManager.registerDriver(new ClientDriver());
         con = DriverManager.getConnection("jdbc:derby://localhost:1527/TicTackToy","root","root");
    }

    public synchronized static Database getDataBase() throws SQLException {
        if(instanceData == null){
            instanceData = new Database();
        }
        return instanceData;
    }
    
    public synchronized void updateResultSet(){
        
        try {
            this.pst =con.prepareStatement("Select * from player",ResultSet.TYPE_SCROLL_SENSITIVE ,ResultSet.CONCUR_READ_ONLY  );
            this.rs = pst.executeQuery(); // rs has all data
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public synchronized int getCountOfOfflineUserse(){
        try {
            this.pst =con.prepareStatement("select count(*) from player where isactive = false",ResultSet.TYPE_SCROLL_SENSITIVE ,ResultSet.CONCUR_READ_ONLY  );
            ResultSet r =pst.executeQuery(); // rs has all data            System.out.println("has next"+r.next());

            return r.next() ? r.getInt(1) : -1;
        }catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("catch getactive");
        }
        return -1;
    }
    public synchronized void  setActive(boolean state , String email){
        try {
            pst = con.prepareStatement("update player set isActive = ? where email = ?");
            pst.setString(1, state+"");
            pst.setString(2, email);
            System.out.println("db mail active:"+email);
            pst.executeUpdate(); // rs has all data
            updateResultSet();
        } catch (SQLException ex) {
            System.out.println("change state to offline catch");
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public synchronized void setNotPlaying(String email){
        try {
            pst = con.prepareStatement("update player set isPlaying = false where email = ?");
            pst.setString(1, email);
            pst.executeUpdate();
            updateResultSet();
        } catch (SQLException ex) {
            System.out.println("change state to notplaying catch");
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
        public synchronized ResultSet getActivePlayers( ){
        
        try {
            this.pst =con.prepareStatement("Select * from player where isactive = true ",ResultSet.TYPE_SCROLL_SENSITIVE ,ResultSet.CONCUR_READ_ONLY  );
            return pst.executeQuery(); // rs has all data
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("catch getactive");
            return null;
        }
        
    }
   
    
    public synchronized void disableConnection() throws SQLException{
        changeStateToOffline();
        changeStateToNotPlaying();

        rs.close();
        pst.close();
        con.close();
        instanceData = null;
    }
    public synchronized void changeStateToNotPlaying(){
         try {
            pst = con.prepareStatement("update player set isPlaying = ? ",ResultSet.TYPE_SCROLL_SENSITIVE ,ResultSet.CONCUR_UPDATABLE  );
            pst.setString(1, "false");
            pst.executeUpdate(); // rs has all data
            updateResultSet();
        } catch (SQLException ex) {
            System.out.println("change state to offline");
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void changeStateToOffline(){
        try {
            pst = con.prepareStatement("update player set isActive = ? ",ResultSet.TYPE_SCROLL_SENSITIVE ,ResultSet.CONCUR_UPDATABLE  );
            pst.setString(1, "false");
            pst.executeUpdate(); // rs has all data
            updateResultSet();
        } catch (SQLException ex) {
            System.out.println("change state to offline");
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public synchronized void login(String email,String password) throws SQLException{

        pst = con.prepareStatement("update player set isActive = ?  where email = ? and password = ? ",ResultSet.TYPE_SCROLL_SENSITIVE ,ResultSet.CONCUR_UPDATABLE  );
        pst.setString(1, "true");
        pst.setString(2, email);
        pst.setString(3, password);
        pst.executeUpdate(); // rs has all data
        updateResultSet();          
    }
    
    public synchronized void SignUp(String username , String email, String password) throws SQLException{

        pst = con.prepareStatement("insert into player(username,email,password) values(?,?,?)");
        pst.setString(1, username);
        pst.setString(2, email);
        pst.setString(3, password);
        pst.executeUpdate(); // rs has all data
        login(email,password);

    }

    public synchronized String checkRegister(String username , String email){
        ResultSet checkRs;
        PreparedStatement pstCheck;
        
        try {
            //        String queryString= new String("select username from player where username = ?");
            pstCheck = con.prepareStatement("select * from player where username = ? and email = ?");
            pstCheck.setString(1, username);
            pstCheck.setString(2, email);
            checkRs = pstCheck.executeQuery();
            if(checkRs.next()){
                return "already signed-up";
            }
        } catch (SQLException ex) {
            System.out.println("here ");
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Registered Successfully";
    }
    public synchronized String checkSignIn(String email, String password){
        ResultSet checkRs;
        PreparedStatement pstCheck;
        String check;       
        System.out.println("checkSignIn " +checkIsActive(email));
        if(!checkIsActive(email)){
            System.out.println(" checkSignIn: " +checkIsActive(email));
                try { 
            pstCheck = con.prepareStatement("select * from player where email = ? ");
            pstCheck.setString(1, email);
            checkRs = pstCheck.executeQuery();
            if(checkRs.next()){
                if(password.equals(checkRs.getString(4))){
                    return "Logged in successfully";
                }
                return "Password is incorrect";
            }
            return "Email is incorrect";
          } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            return "Connection issue, please try again later";
             }
        }else{
            System.out.println("This Email alreay sign-in " + checkIsActive(email));
           return "This Email is alreay sign-in";  
        }
              
    }
    
    public synchronized int getScore(String email){
        int score;
        ResultSet checkRs;
        PreparedStatement pstCheck;
 
        try {
            pstCheck = con.prepareStatement("select * from player where email = ?");
            pstCheck.setString(1, email);
            checkRs = pstCheck.executeQuery();
            checkRs.next();
            score = checkRs.getInt(5);
            return score;
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    } 
    
    public synchronized void updateScore(String mail, int score){
        try {
            pst = con.prepareStatement("update player set score = ?  where email = ?",ResultSet.TYPE_SCROLL_SENSITIVE ,ResultSet.CONCUR_UPDATABLE  );
            pst.setInt(1, score);
            pst.setString(2, mail);
            pst.executeUpdate();
            updateResultSet();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public synchronized String getEmail(String username){
        String email;
        ResultSet checkRs;
        PreparedStatement pstCheck;
        try {
            pstCheck = con.prepareStatement("select * from player where email = ?");
            pstCheck.setString(1, username);
            checkRs = pstCheck.executeQuery();
            checkRs.next();
            email = checkRs.getString(3);
            return email;
        } catch (SQLException ex) {
            System.out.println("Invalod Email address");
            //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * getUserName
     * get user name to pass it to login method
     * @param email
     * @return 
     */
     public synchronized String getUserName(String email){
        String userName;
        ResultSet checkRs;
        PreparedStatement pstCheck;
        try {
            pstCheck = con.prepareStatement("select * from player where email = ?");
            pstCheck.setString(1, email);
            checkRs = pstCheck.executeQuery();
            checkRs.next();
            userName = checkRs.getString(2);
            return userName;
        } catch (SQLException ex) {
            System.out.println("Invalod Email address");
            //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public synchronized void makePlaying(String player1, String player2){
        try {
            pst = con.prepareStatement("update player set isPlaying = true  where email = ?",ResultSet.TYPE_SCROLL_SENSITIVE ,ResultSet.CONCUR_UPDATABLE  );
            pst.setString(1, player1);
            pst.executeUpdate(); // rs has all data
            pst = con.prepareStatement("update player set isPlaying = true  where email = ?",ResultSet.TYPE_SCROLL_SENSITIVE ,ResultSet.CONCUR_UPDATABLE  );
            pst.setString(1, player2);
            pst.executeUpdate();
            updateResultSet();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


     
     /**
      * checkIsActive
      * check if player login or not
      * @param email
      * @return 
      */
     public Boolean checkIsActive(String email){
          ResultSet checkRs;
          PreparedStatement pstCheck;
          Boolean isActive ;
         try {
            pstCheck = con.prepareStatement("select isactive from player where email = ?");
            pstCheck.setString(1, email);
            checkRs = pstCheck.executeQuery();
            checkRs.next();
            System.out.println("checkIsActive true ");
            isActive = checkRs.getBoolean("isactive");
            System.out.println("checkIsActive " +isActive);
            return isActive ;
         } catch (SQLException ex) {
            System.out.println("Invalod Email address");
            //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
         }
         return false;
         
     }




    public synchronized boolean checkPlaying(String player){

        boolean available;
        ResultSet checkAv;
        PreparedStatement pstCheckAv;
        try {
            pstCheckAv = con.prepareStatement("select * from player where username = ?");
            pstCheckAv.setString(1, player);
            checkAv = pstCheckAv.executeQuery();
            checkAv.next();
            available = checkAv.getBoolean(4);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }
}


