
package moneyplatform;

import java.math.BigInteger;
import java.security.MessageDigest;

public class Hashing {
    public static boolean SHA256CONTROL(String hash,String data){
        // data format = from:to:amount
        try{
            MessageDigest messagedigest = MessageDigest.getInstance("SHA-256");
            byte [] byted = messagedigest.digest(data.getBytes());
            BigInteger bigInteger = new BigInteger(1,byted);
            String hashedData = bigInteger.toString(16);
            while(hashedData.length()<32){
                hashedData = "0"+hashedData;
            }
            if(hashedData.equalsIgnoreCase(hash)){
                return true;
            }else{
                return false;
            }
        }catch(Exception e){
            System.out.println("ERR15");
        }
        return false;
    }
    public static String SHA256(String data){
        String hashedData = null;
        try{
            MessageDigest messagedigest = MessageDigest.getInstance("SHA-256");
            byte [] byted = messagedigest.digest(data.getBytes());
            BigInteger bigInteger = new BigInteger(1,byted);
            hashedData = bigInteger.toString(16);
            while(hashedData.length()<32){
                hashedData = "0"+hashedData;
            }
        }catch(Exception e){
            System.out.println("ERR18");
        }
        return hashedData;
    }
    public static String MD5(String data){
        String hashedData = null;
        try{
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            byte [] byted = messagedigest.digest(data.getBytes());
            BigInteger bigInteger = new BigInteger(1,byted);
            hashedData = bigInteger.toString(16);
            while(hashedData.length()<32){
                hashedData = "0"+hashedData;
            }
        }catch(Exception e){
            System.out.println("ERR20");
        }
        return hashedData;
    }
}
