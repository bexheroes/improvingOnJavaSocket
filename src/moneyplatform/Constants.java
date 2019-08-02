
package moneyplatform;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class Constants {
    public final int networkPort;
    public final String networkFilePath;
    public final String pingMessage;
    public final String pingResponse;
    public final String myIp;
    public final String blockchainFile;
    public final String userFile;
    public Constants(){
        
        String systemipaddress = ""; 
        try{ 
            URL url_name = new URL("http://bot.whatismyipaddress.com"); 
            BufferedReader sc = new BufferedReader(new InputStreamReader(url_name.openStream())); 
            systemipaddress = sc.readLine().trim(); 
        } 
        catch (Exception e){
            systemipaddress = "Cannot Execute Properly"; 
        } 
        
        this.networkFilePath = "nodes.network";
        this.blockchainFile = "data.blockchain";
        this.userFile = "users.dat";
        this.networkPort = 5578;
        this.pingMessage = "JOIN";
        this.pingResponse = "ACCEPTED";
        this.myIp = systemipaddress;
    }
}
