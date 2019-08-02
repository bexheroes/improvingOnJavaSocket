
package moneyplatform;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Blockchain extends Thread{
    @Override
    public void run(){
        new BlockchainFileControl().start();
        new PassDataHashmap().start();
        new LiveData().start(); // UPDATE DATA EVERY SECONDS
        new Register().start();
        new Samplegui().start();
    }
}

class BlockchainFileControl extends Thread{
    @Override
    public void run(){
        File file = new File(new Constants().blockchainFile);
        if(!file.exists()){
            try{
                file.createNewFile();
            }catch(Exception e){
                System.out.println("ERR16");
            }
        }
    }
}

// Keep blockchain datas in hashmap

class PassDataHashmap extends Thread{
    @Override
    public void run(){
        File file = new File(new Constants().blockchainFile);
        String block = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new Constants().blockchainFile));
            String getLine;
            while((getLine=bufferedReader.readLine())!=null){
                if(!getLine.contains("</endofblock>")){
                    getLine = getLine.replace("\n", "");
                    block = block + getLine + ":";
                }else{
                    String hashedData = Hashing.SHA256(block);
                    HashMap<String,String> Blockchain = new Database().getBlockchain();
                    if(!Database.existHash(hashedData)){
                        Blockchain.put(hashedData, block);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("ERR19");
        }
        
    }
}

// Check every seconds your data
// is equal to another's ?

class LiveData extends Thread{
    @Override
    public void run(){
        while(true){
            
            if(new Database().getNetworkList().size()>0){
                for(String getNeighbor : new Database().getNetworkList()){
                    Iterator iterator2 = new Database().getBlockchain().entrySet().iterator();
                        while(iterator2.hasNext()){
                            Map.Entry<String,String> pair = (Map.Entry<String,String>)iterator2.next();
                            String getKey = pair.getKey();
                            String getValue = pair.getValue();

                        }
                }
            }
            // No overload memory
            try {
                this.sleep(2000);
            } catch (Exception e) {
                System.out.println("ERR10");
            }
        }
    }
}

class SendData extends Thread{
    private String hash;
    private String data;
    private String neigborIp;
    private Socket socket;
    public SendData(String neigborIp,String hash){
        this.neigborIp = neigborIp;
        this.hash = hash;
        this.data = data;
        try{
            this.socket = new Socket(neigborIp,new Constants().networkPort);
        }catch(Exception e){
            System.out.println("ERR11");
        }
    }
    @Override
    public void run(){
        try{
            if(Hashing.SHA256CONTROL(hash, data)){
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream.writeUTF("<datacontrol>"+hash+"</datacontrol>");
                String getResponse = dataInputStream.readUTF();
                if(getResponse.equalsIgnoreCase("NO")){
                    new SendFullData(neigborIp, hash, data).start();
                }  
            }else{
                File file = new File(new Constants().blockchainFile);
                if(file.exists()){
                    file.delete();
                }
            }
        }catch(Exception e){
            System.out.println("ERR12");
        }
        
    }
}

class SendFullData extends Thread{
    private String hash;
    private String data;
    private String neighborIp;
    private Socket socket;
    public SendFullData(String neighborIp,String hash,String data){
        this.neighborIp = neighborIp;
        this.hash = hash;
        this.data = data;
        try{
            this.socket = new Socket(neighborIp,new Constants().networkPort);
        }catch(Exception e){
            System.out.println("ERR11");
        }
    }
    @Override
    public void run(){
        try{
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream.writeUTF("<data>"+hash+"-"+data+"</data>");
        }catch(Exception e){
            System.out.println("ERR13");
        }
    }
}