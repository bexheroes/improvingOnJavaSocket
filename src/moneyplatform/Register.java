
package moneyplatform;

import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Register extends Thread{
    @Override
    public void run(){
        new UserFileControl().start();
    }
}

class UserFileControl extends Thread{
    @Override
    public void run(){
        File file = new File(new Constants().userFile);
        if(!file.exists()){
            try{
                file.createNewFile();
            }catch(Exception e){
                System.out.println("ERR21");
            }
        }
    }
}

class AddUser extends Thread{
    private String hashedData;
    public AddUser(String username,String password){
        hashedData = Hashing.SHA256(Hashing.MD5(username+""+password));
    }
    public void spreadNow(){
        try {
            // When ended update of user db
            this.sleep(5000);
        } catch (Exception e) {
            System.out.println("ERR25");
        }
        HashSet<String> NetworkList = Database.NetworkList;
        for(String neighbor : NetworkList){
            new SpreadUserListThread(neighbor, hashedData).start();
        }
    }
    @Override
    public void run(){
        spreadNow();
    }
}

class SpreadUserListThread extends Thread{
    private String ip;
    private String hashedData;
    public SpreadUserListThread(String ip,String hashedData){
        this.ip = ip;
        this.hashedData = hashedData;
    }
    @Override
    public void run(){
        try {
            Socket socket = new Socket(ip,new Constants().networkPort);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("<newuser>"+hashedData+"</newuser>");
        } catch (Exception e) {
            System.out.println("ERR22");
        }
    }
}

class LoginUser extends Thread{
    private String hashedData;
    public LoginUser(String username,String password){
        this.hashedData = Hashing.SHA256(Hashing.MD5(username+""+password));
    }
    @Override
    public void run(){
        new LoginUserThread(hashedData).start();
    }
}

class LoginUserThread extends Thread{
    private String hashedData;
    public LoginUserThread(String hashedData){
        this.hashedData = hashedData;
    }
    @Override
    public void run(){
        int counter = 0;
        while(true){
            if(counter>10){
                break;
            }
            
            if(Database.existUser(hashedData)){
                System.out.println("Login is successful");
                new Database().setLoginCondition(hashedData);
                break;
            }
            
            counter+=1;
            try {
                System.out.println("Failed... trying again");
                this.sleep(5000);
            } catch (Exception e) {
                System.out.println("ERR23");
            }
        }
    }
}