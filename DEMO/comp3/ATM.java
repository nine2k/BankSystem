/* The ATM class Displays a GUI and allows the user to interact with their bank account.
On start up of the ATM, the atm establishes and holds a connection with the bank. if the connection is broken the ATM is out of order till restarted.

1. First the user needs to enter a card number. (physical card)
upon receiving the card, the ATM sends the number to its bank and waits to receive back an account object.
This object is locked at the bank. (only 1 access to the account from a terminal at a time.)

2. the user is given 4 options on GUI, check balance (a), withdraw (b), deposit (c),  exit(d);

3a. i. check balance sent to bank, wait for reply;
3a. ii. display Ok button, and balance.
3a. iii. navigate to main menu.

3b. i. amount box and okay button. (optional only specific bill amounts.)
3b. ii. Accepted/rejected message (reject -> time out go to main menu, accept -> accept message main menu);

3c. i. amount box and okay button.  after okay go to main menu.

3d. i. Good bye message, close account;

*/

/*---screen names/numbers---
three screen types
1 - input SCREEN
2 - display SCREEN
3 - menu SCREEN
----------------------------

atm states and type of screen used
1 - account SCREEN 1
2 - pin SCREEN 1
3 - menu SCREEN 3
4 - incorrect pin screen 2
5 - balance SCREEN  2
6 - deposit SCREEN 1
7 - withdraw SCREEN 1
8 - deposit result SCREEN 2
9 - withdraw result SCREEN 2

*/
import java.lang.Math;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
public class ATM {

  public static int atmState;
  private static long accountNum =0;
  public static atmDisplay atm;

  public static void main (String args[])throws Exception {

    //read in the bank to connect to
    Scanner in;
    in = new Scanner(System.in);
    System.out.println("Enter the ID of the Bank this ATM is to connect to: ");
    int bankID=0;
    try{
      bankID = in.nextInt();
    }
    catch(Exception e){e.printStackTrace();}

    //read the banks.csv file
    int bankPort=0;
    String bankIP="";
    conObject[] co = getDataCo(readFile("banks.csv"));
    for (int i =0;i<co.length;i++){
      if(co[i].id == bankID){
        bankPort=co[i].port+1000;
        bankIP = co[i].ip;
      }
      break;
    }

    // set up ATM vars
    atmSynch.key = new Semaphore(0,true);
    String IP = bankIP;
    int port = bankPort;
    atmState = 1;
    // activate the GUI
    atm = new atmDisplay();

    atmSynch.msg = "Enter Account:";
    atm.setScreen(1);

    // communication channel set up and connection to bank
    atmDialer comms = new atmDialer();
    comms.connectToServer(IP,port);
    System.out.println("Atm connected to bank");

    // main logic statemachine loop

    while(true){
      int am;
      double am2;
      long act;
      Request r;
      Outcome o;
      //wait for gui inpout
      //wait for semaphore to be released, get button pushed
      atmSynch.key.acquire(1);
      String but = atmSynch.button;
      System.out.println("got from button: "+but);
      //resolve state machine
      switch(atmState){
        case 1:
        try{
          act = Long.parseLong(but);
        }
        catch(NumberFormatException e){
          System.out.println("bad input");
          gotoState(4,"improper input, account rejected");
          break;
        }
        accountNum = act;
        gotoState(2,"");
        break;

        case 2:
        try{
          am = Integer.parseInt(but);
        }
        catch(NumberFormatException e){
          System.out.println("bad input");
          gotoState(4,"improper pin input, account rejected");
          break;
        }

        atm.setScreen(0);
        // make a new request to check Pin
        r = new Request(requestType.LOGON,accountNum,am);
        r.print();
        o = comms.send(r);
        if(o.result()){
          gotoState(3,"");
        }
        else{
          gotoState(4,o.getMsg());
        }
        break;

        case 3:
        if(but.equals("Deposit")){
          gotoState(6,"");
          break;
        }
        if(but.equals("Withdraw")){
          gotoState(7,"");
          break;
        }
        if(but.equals("Exit")){
          gotoState(1,"");
          accountNum =0;
          break;
        }
        if(but.equals("Check Balance")){
          r = new Request(requestType.BALANCE,accountNum,0);
          o = comms.send(r);
          gotoState(5,o.getMsg());
          break;
        }

        case 4:
        accountNum = 0;
        gotoState(1,"");
        break;

        case 6:
        try{
          am2 = Double.parseDouble(but);
        }
        catch(NumberFormatException e){
          System.out.println("bad input");
          gotoState(8,"improper input, transaction rejected");
          break;
        }
        atm.setScreen(0);
        r = new Request(requestType.DEPOSIT,accountNum,am2);
        o = comms.send(r);
        gotoState(8,o.getMsg());
        break;

        case 7:
        try{
          am2 = Double.parseDouble(but);
        }
        catch(NumberFormatException e){
          System.out.println("bad input");
          gotoState(9,"improper input, transaction rejected");
          break;
        }
        atm.setScreen(0);
        r = new Request(requestType.WITHDRAW,accountNum,am2);
        o = comms.send(r);
        gotoState(9,o.getMsg());
        break;

        case 5: case 8: case 9:
        gotoState(3,"");
        break;

        default:
        System.out.println("state machine error");
        break;

      }
    }
  }

/*  atm states
  1 - account SCREEN 1
  2 - pin SCREEN 1
  3 - menu SCREEN 3
  4 - incorrect pin screen 2
  5 - balance SCREEN  2
  6 - deposit SCREEN 1
  7 - withdraw SCREEN 1
  8 - deposit result SCREEN 2
  9 - withdraw result SCREEN 2
*/
  public static void gotoState(int i,String s){
    atmState = i;
    switch(i){

      case 1:
      atmSynch.msg = "Enter Account:";
      atm.setScreen(1);
      break;
      case 2:
      atmSynch.msg = "Enter Pin:";
      atm.setScreen(1);
      break;
      case 3:
      atm.setScreen(3);
      break;
      case 4:
      atmSynch.msg = s;
      atm.setScreen(2);
      break;
      case 5:
      atmSynch.msg = s;
      atm.setScreen(2);
      break;
      case 6:
      atmSynch.msg = "Deposit Amount:";
      atm.setScreen(1);
      break;
      case 7:
      atmSynch.msg = "Withdraw Amount:";
      atm.setScreen(1);
      break;
      case 8:
      atmSynch.msg = s;
      atm.setScreen(2);
      break;
      case 9:
      atmSynch.msg = s;
      atm.setScreen(2);
      break;

    }
  }

  // --- csv reading function that gives a list of strings. ---
    public static List<String> readFile(String fileName) {
        int count = 1;
        File file = new File(fileName);
        // this gives you a 2-dimensional array of strings
        List<String> data = new ArrayList<>();
        Scanner inputStream;
        try {
            inputStream = new Scanner(file);

            while (inputStream.hasNext()) {
                data.add(inputStream.nextLine());
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static conObject[] getDataCo(List<String> list){
      conObject [] ret = new conObject [list.size()];
      for (int i = 0;i<list.size();i++){
        String s = list.get(i);
          int id = Integer.parseInt(s.substring(0,s.indexOf(",")));
          s= cut(s);
          String ip = s.substring(0,s.indexOf(","));
          s= cut(s);
          int port = Integer.parseInt(s);
          ret[i] = new conObject(ip,id,port);
      }
      return ret;

    }

    // string cutting utility function
    private static String cut(String s){
      return s.substring(s.indexOf(",")+1);
    }
}

// connection object. just groups ip,id, and port
class conObject{
  public String ip;
  public int id;
  public int port;

  public conObject(String IP,int ID,int PORT){
    ip = IP;
    id=ID;
    port =PORT;
  }

}
