
package moneyplatform;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Mining extends Thread{
    private String myAccountNumber;
    @Override
    public void run(){
            while(true){
                try {
                    if(Database.loginCondition==1){
                        this.myAccountNumber = new Database().getAccountNumber();
                        int hardRate = 4;
                        int TimeToGainPrize = (int) (Math.random()*10000*hardRate);
                        int AmountOfPrize = (int) (Math.random()*10*(1+TimeToGainPrize/10000));
                        this.sleep(TimeToGainPrize/4);
                        System.out.println("[++--------]");
                        this.sleep(TimeToGainPrize/4);
                        System.out.println("[+++++-----]");
                        this.sleep(TimeToGainPrize/4);
                        System.out.println("[++++++++--]");
                        this.sleep(TimeToGainPrize/4);
                        HashSet<String> NetworkList = new Database().getNetworkList();
                        for(String ip : NetworkList){
                            new SpreadPrize(ip, myAccountNumber, AmountOfPrize).start();
                        }
                        System.out.println("Congrulation !!! You got "+AmountOfPrize+" prize");
                    }else{
                        System.out.println("You must be logined to Mining !!!");
                        this.sleep(4000);
                    }
                } catch (Exception e) {
                    System.out.println("ERR32");
                }
            }
    }
}

class SpreadPrize extends Thread{
    private String ip;
    private double amount;
    private String myAccountNumber;
    public SpreadPrize(String ip,String myAccountNumber,double amount){
        this.ip = ip;
        this.amount = amount;
        this.myAccountNumber = myAccountNumber;
    }
    @Override
    public void run(){
        try {
            Socket socket = new Socket(this.ip,new Constants().networkPort);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            String printedData = "<newprize>"+this.myAccountNumber+"-"+this.amount+"</newprize>";
            dataOutputStream.writeUTF(printedData);
        } catch (Exception e) {
            System.out.println("ERR31");
        }
    }
}