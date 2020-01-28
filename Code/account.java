import java.io.Serializable;

// an account number has 16 digits, the first two are the bank id.

public class account implements Serializable{
  private static final long serialVersionUID = 1L;
  private long accountNumber;
  private int pin;
  private String Name;
  private double accountBalance;

  public account(String n, int Pin,long an){
    pin = Pin;
    Name = n;
    accountNumber = an;
  }
  public account(long a,int b, String c, double d){
    pin = b;
    Name = c;
    accountNumber = a;
    accountBalance = d;
  }
  //print out account contents
public void print(){
  System.out.println("Account: "+accountNumber+" pin: "+pin+" Name: "+Name+" balance "+accountBalance);
}

// verify pin
  public boolean check(int p){
    if (p==pin){
      return true;
    }
    return false;
  }
  //return pin
  public int getPin(){
    return pin;
  }
  //get balance as stirng
  public String getBalStr(){
    return ""+accountBalance;
  }

// get account #
  public long getNum(){
    return accountNumber;
  }
// make withdrawl, but first check if available $ on account
  public boolean withdraw(double amt){
    if (check(amt)){
      accountBalance = accountBalance-amt;
      return true;
    }
    return false;
  }
// get owner name
  public String getName(){
    return Name;
  }
// check balance
  private boolean check(double amt){
    if(amt<=accountBalance)
      return true;
    return false;
  }
  //make depoosit
  public void deposit(double d){
    accountBalance=accountBalance+d;
  }
}
