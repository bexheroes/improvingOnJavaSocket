
package moneyplatform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Database {
    public static HashSet<String> NetworkList = new HashSet<String>();
    public static HashMap<String,String> Blockchain =  new HashMap<String, String>();
    public static List<String> UserList  = new ArrayList<String>() ;
    public static int loginCondition = 0;
    public static String myAccountNumber;
    public HashSet<String> getNetworkList(){
        return NetworkList;
    }
    public HashMap<String,String> getBlockchain(){
        return Blockchain;
    }
    public List<String> getUserList(){
        return UserList;
    }
    public int getLoginCondition(){
        return loginCondition;
    }
    public String getAccountNumber(){
        return myAccountNumber;
    }
    public void setLoginCondition(String hashedData){
        if(getLoginCondition()==0){
            this.loginCondition = 1;
            myAccountNumber = hashedData;
        }else{
            this.loginCondition = 0;
            myAccountNumber = null;
        }
    }
    public static boolean existNode(String dataField){
        if(NetworkList.size()>0){
            Iterator iterator = NetworkList.iterator();
            while(iterator.hasNext()){
                String getLine = iterator.next().toString();
                if(getLine.equalsIgnoreCase(dataField)){
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    public static boolean existHash(String hash){
        if(Blockchain.size()>0){
            HashSet<String> BlockchainKeyset = new HashSet<String>(Blockchain.keySet());
            for(String getHash : BlockchainKeyset){
                if(getHash.equalsIgnoreCase(hash)){
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    public static boolean existUser(String dataField){
        if(UserList.size()>0){
            Iterator iterator = UserList.iterator();
            while(iterator.hasNext()){
                String getLine = iterator.next().toString();
                if(getLine.equalsIgnoreCase(dataField)){
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
