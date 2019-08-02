
package moneyplatform;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Network extends Thread{
    @Override
    public void run(){
        new SubNetwork().start();
        new Server().start();
        new Client().start();
    }
}

class SubNetwork extends Thread{
    @Override
    public void run(){
        
        
        // Check if network file exist
        Constants constants = new Constants();
        File file = new File(constants.networkFilePath);
        if(!file.exists()){
            try{
                file.createNewFile();
            }catch(Exception e){
                System.out.println("ERR1");
            }
        }
        
    }
}

class Client extends Thread{
    @Override
    public void run(){
        try{
            Constants constants = new Constants();
            String getLine;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(constants.networkFilePath));
            while((getLine=bufferedReader.readLine())!=null){
                getLine = getLine.replace("\n", "");
                getLine = getLine.trim();
                if(!Database.existNode(getLine)){
                    Runnable r = new ClientThread(getLine);
                    new Thread(r).start();
                }
            }
        }catch(Exception e){
            System.out.println("ERR8");
        }
        
    }
}

class ClientThread extends Thread implements Runnable{
    private String getNode;
    public ClientThread(String getNode){
        this.getNode = getNode;
    }
    @Override
    public void run(){
        Constants constants = new Constants();
        try {
            Socket socket = new Socket(getNode,constants.networkPort);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream.writeUTF("<addnode>"+constants.myIp+"</addnode>");
            String getResponse = dataInputStream.readUTF();
            if(getResponse.equalsIgnoreCase(constants.pingResponse)){
                HashSet<String> NetworkList = new Database().getNetworkList();
                NetworkList.add(getNode);
            }
        } catch (Exception e) {
            // NO OUTPUT
        }
    }
}

class Server extends Thread{
    @Override
    public void run(){
        try {
            ServerSocket serverSocket = new ServerSocket(new Constants().networkPort);
            while(true){
                Socket socket = serverSocket.accept();
                Runnable r = new ServerThread(socket);
                new Thread(r).start();
            }
        } catch (Exception e) {
            System.out.println("ERR3");
        }
    }
}

class ServerThread extends Thread implements Runnable{
    private Socket socket;
    public ServerThread(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run(){
        try{
            String getLine;
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            while((getLine=dataInputStream.readUTF())!=null){
                if(getLine.contains("<newnode>") && getLine.contains("</newnode>")){
                    getLine = getLine.replace("<newnode>", "");
                    getLine = getLine.replace("</newnode>", "");
                        if(!Database.existNode(getLine)){
                            HashSet<String> NetworkList = new Database().getNetworkList();
                            System.out.println(getLine);
                            NetworkList.add(getLine);
                        }
                        
                }else if(getLine.contains("<addnode>") && getLine.contains("</addnode>")){
                    getLine = getLine.replace("<addnode>", "");
                    getLine = getLine.replace("</addnode>", "");
                        HashSet<String> NetworkList = new Database().getNetworkList();
                            dataOutputStream.writeUTF(new Constants().pingResponse);
                        
                        // Spread nodes to all network
                        if(NetworkList.size()>0){
                            for(String neighborIp : new Database().getNetworkList()){
                                Runnable r = new SpreadNetwork(neighborIp);
                                new Thread(r).start();
                            }
                        }
                        
                }else if(getLine.contains("<datacontrol>") && getLine.contains("</datacontrol>")){
                    //
                    // Data Network Field
                    //
                    
                    getLine = getLine.replace("<datacontrol>", "");
                    getLine = getLine.replace("</datacontrol>","");
                    if(!Database.existHash(getLine)){
                        dataOutputStream.writeUTF("NO");
                    }else{
                        dataOutputStream.writeUTF("YES");
                    }
                    
                    
                }else if(getLine.contains("<data>") && getLine.contains("</data>")){
                    getLine = getLine.replace("<data>", "");
                    getLine = getLine.replace("</data>", "");
                    String [] splitedLine = getLine.split("-");
                    String getHash = splitedLine[0];
                    String getData = splitedLine[1];
                    new Database().getBlockchain().put(getHash, getData);
                    
                }else if(getLine.contains("<newuser>") && getLine.contains("</newuser>")){
                    getLine = getLine.replace("<newuser>", "");
                    getLine = getLine.replace("</newuser>", "");
                    if(!Database.existUser(getLine)){
                        new Database().getUserList().add(getLine);
                        try{
                            FileWriter fileWriter = new FileWriter(new Constants().userFile,true);
                            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                            PrintWriter printWriter = new PrintWriter(bufferedWriter);
                            printWriter.println(getLine);
                            printWriter.flush();
                        }catch(Exception e){
                            System.out.println("NOOOOO");
                        }
                    }
                }else if(getLine.contains("<request>") && getLine.contains("</request>")){
                    getLine = getLine.replace("<request>", "");
                    getLine = getLine.replace("</request>", "");
                    String [] splitedData = getLine.split(":");
                    String from = splitedData[0];
                    String to = splitedData[1];
                    double amount = Double.parseDouble(splitedData[2]);
                    double TOTAL_AMOUNT_OF_FROM = 0;
                    double TOTAL_AMOUNT_OF_TO = 0;
                    HashMap<String,String> myHashMap = (HashMap<String,String>) Database.Blockchain.clone();
                    HashSet<String> hashSet = new HashSet<String>(myHashMap.values());
                    for(String s : hashSet){
                        String [] splitedBlock = s.split(":");
                        String blockFrom = splitedBlock[0];
                        String blockTo = splitedBlock[1];
                        double blockAmount = Double.parseDouble(splitedBlock[2]);
                        if(from.equalsIgnoreCase(blockFrom)){
                            TOTAL_AMOUNT_OF_FROM-=blockAmount;
                        }else if(from.equalsIgnoreCase(blockTo)){
                            TOTAL_AMOUNT_OF_FROM+=blockAmount;
                        }
                    }
                    if(TOTAL_AMOUNT_OF_FROM>amount){
                        dataOutputStream.writeUTF("OK");
                    }else{
                        dataOutputStream.writeUTF("NO");
                    }
                }else if(getLine.contains("<newblock>") && getLine.contains("</newblock>")){
                    getLine = getLine.replace("<newblock>", "");
                    getLine = getLine.replace("</newblock>", "");
                    String hashedData = Hashing.SHA256(Hashing.MD5(getLine));
                    String [] splitedData = getLine.split(":");
                    String from = splitedData[0];
                    String to = splitedData[1];
                    double amount = Double.parseDouble(splitedData[2]);
                    new Database().getBlockchain().put(hashedData, getLine);
                    PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(new Constants().blockchainFile,true)));
                    String printThatData = from+"\n"+to+"\n"+amount+"\n"+"</endofblock>"+"\n";
                    printWriter.println(printThatData);
                    printWriter.flush();
                }else if(getLine.contains("<newprize>") && getLine.contains("</newprize>")){
                    getLine = getLine.replace("<newprize>", "");
                    getLine = getLine.replace("</newprize>", "");
                    String [] splitedData = getLine.split("-");
                    String accountNumber = splitedData[0];
                    double amount = Double.parseDouble(splitedData[1]);
                    String source = Hashing.SHA256(Hashing.MD5("pool"));
                    String printedData = source+"\n"+accountNumber+"\n"+amount+"\n"+"</endofblock>\n";
                    PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(new Constants().blockchainFile,true)));
                    printWriter.println(printedData);
                    printWriter.flush();
                }
                
            }
            
        }catch(Exception e){
            
        }
        
    }
}

class SpreadNetwork extends Thread implements Runnable{
    private String strangerIp;
    private Socket socket;
    public SpreadNetwork(String strangerIp){
        this.strangerIp = strangerIp;
        try{
            this.socket = new Socket(strangerIp,new Constants().networkPort);
        }catch(Exception e){
            System.out.println("ERR7");
        }
    }
    @Override
    public void run(){
        if(!strangerIp.equals(new Constants().myIp)){
            try{
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF("<newnode>"+strangerIp+"</newnode>");
            }catch(Exception e){
                System.out.println("ERR8");
            }
        }
        
    }
}
