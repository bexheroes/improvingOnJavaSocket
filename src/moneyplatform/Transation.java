
package moneyplatform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Transation extends Thread{
    private String from;
    private String to;
    private double amount;
    private String hash;
    int TOTAL_COMPUTER_IN_NETWORK;
    int NEED_COMPUTER_TO_APPROVE;
    int[] TOTAL_REQUEST_TO_APPROVE;
    int[] TOTAL_RESPOND_TO_APPROVE;
    public Transation(String from,String to,double amount){
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.hash = Hashing.SHA256(Hashing.MD5(from+":"+to+":"+amount+":"));
        TOTAL_COMPUTER_IN_NETWORK = Database.NetworkList.size();
        NEED_COMPUTER_TO_APPROVE = (int)(Math.floor(TOTAL_COMPUTER_IN_NETWORK/2));
        TOTAL_RESPOND_TO_APPROVE = new int[1];
        TOTAL_RESPOND_TO_APPROVE[0] = 0;
        TOTAL_REQUEST_TO_APPROVE = new int[1];
        TOTAL_REQUEST_TO_APPROVE[0] = 0;
    }
    @Override
    public void run(){
        
        for(String ip: new Database().getNetworkList()){
            new TransationThread(ip, from, to, amount, TOTAL_REQUEST_TO_APPROVE, TOTAL_RESPOND_TO_APPROVE).start();
        }
        try {
            this.sleep(3000);
        } catch (Exception e) {
            System.out.println("ERR28");
        }
        if(TOTAL_RESPOND_TO_APPROVE[0] >= NEED_COMPUTER_TO_APPROVE){
            for(String ip: new Database().getNetworkList()){
                new NewBlockThread(ip, from, to, amount).start();
                System.out.println("[\nfrom => "+from+"\nto => "+to+"\n"+"amount => "+amount+"\n]");
            }
        }else{
            System.out.println("[!]");
        }
        
    }
}

class NewBlockThread extends Thread{
    private String ip;
    private String from;
    private String to;
    private double amount;
    public NewBlockThread(String ip,String from,String to,double amount){
        this.ip = ip;
        this.from = from;
        this.to = to;
        this.amount = amount;
    }
    @Override
    public void run(){
        try{
            Socket socket = new Socket(this.ip,new Constants().networkPort);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream.writeUTF("<newblock>"+from+":"+to+":"+amount+":"+"</newblock>");
        }catch(Exception e){
            System.out.println("ERR29");
        }
    }
}

class TransationThread extends Thread{
    private String ip;
    private String from;
    private String to;
    private double amount;
    private int REQUEST[];
    private int RESPOND[];
    public TransationThread(String ip,String from,String to,double amount,int REQUEST[],int RESPOND[]){
        this.ip = ip;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.REQUEST = REQUEST;
        this.RESPOND = RESPOND;
    }
    @Override
    public void run(){
        try {
            REQUEST[0]+=1;
            Socket socket = new Socket(this.ip,new Constants().networkPort);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream.writeUTF("<request>"+from+":"+to+":"+amount+":"+"</request>");
            String respond = dataInputStream.readUTF();
            if(respond.equalsIgnoreCase("OK")){
                RESPOND[0]+=1;
            }
        } catch (Exception e) {
            System.out.println("ERR27");
        }
    }
}

