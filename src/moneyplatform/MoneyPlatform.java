
package moneyplatform;

public class MoneyPlatform {
    public static void main(String[] args){
        new Network().start();
        new Blockchain().start();
    }
}
