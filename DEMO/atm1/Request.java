import java.io.Serializable;

// a request object, sent to the bank
public class Request implements Serializable {
  private double amount;
  private long accountNum;
  private int pin;
  private static final long serialVersionUID = 1L;
  private requestType rt;

  public Request(requestType r,long acc,double a){
    rt =r;
    accountNum = acc;
    amount = a;
  }
  public Request(requestType r,long acc,int a){
    rt =r;
    accountNum = acc;
    pin = a;
  }

  public requestType getType(){
    return rt;
  }

  public double getValue(){
    return amount;
  }
  public int getPin(){
    return pin;
  }

  public long getNum(){
    return accountNum;
  }
  public void print(){
    System.out.println("Request: for account "+accountNum+ " with amount "+amount);
  }

}
