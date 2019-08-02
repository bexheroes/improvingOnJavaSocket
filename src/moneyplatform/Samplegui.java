
package moneyplatform;

public class Samplegui extends Thread{
    @Override
    public void run(){
        new AddUser("emre", "emre1234").start();
        new LoginUser("emre", "emre1234").start();
        new Mining().start();
        String from = Hashing.SHA256(Hashing.MD5("emre1234"));
        String to = Hashing.SHA256(Hashing.MD5("pool"));
        int amount = 10;
        new Transation(from, to, amount).start();
    }
}
