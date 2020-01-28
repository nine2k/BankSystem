/*
A bank consists of a name and route number. and a series of accounts. accounts can be opened at a bank.
a bank can only be accesed by an ATM.
each bank can talk to every other bank. by passing an request and outcome objects.


Case:
-----------------
 bank owned Account: direct communication with atm.
-----------------
other bank owned Account: route via bank route number



*/

import java.lang.Math;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Bank {
  private static String [] ips;
  private static int [] ports;
  private static int [] ids;

  private static int bankID;
  private static int numAccounts =0;
  private static int bankPort;
  private static int atmPort;
  private static HashMap<Integer,atmDialer> connectionMap;
  private static ConcurrentHashMap<Long,account> accountMap;
  private static Scanner in;

  public static void main(String[] args) throws Exception {

    // get the user entered id of this bank
    in = new Scanner(System.in);
    System.out.println("Enter the ID of this Bank: ");
    try{
      bankID = in.nextInt();
    }
    catch(Exception e){e.printStackTrace();}

    //initialize the hash maps for connections and accounts.
    accountMap = new ConcurrentHashMap<Long,account>();
    connectionMap = new HashMap<Integer,atmDialer>();

    // read file and figure out where to connect to.
    conObject[] co = getDataCo(readFile("banks.csv"));
    for (int i =0;i<co.length;i++){
      if(co[i].id == bankID){
        bankPort=co[i].port;
        atmPort = bankPort+1000;
      }
    }
    System.out.println("banks.csv processed..");
    //read file and populate account map.
    getDataAc(readFile("accounts.csv"));
    //load the accountMap into the "database"
    bankDB.accountMap = accountMap;
    bankDB.bankID = bankID;

    // start up bank socket receivers
    System.out.println("Starting Servers..");
    portServer interBank = new portServer(bankPort);
    portServer atmHandler = new portServer(atmPort);
    atmHandler.start();
    interBank.start();


    // other bank connection loop.. keep trying to connect until all banks are connected.
    int i=0;
    int connections =0;
    while(connections<co.length-1){
      if(connectionMap.get(co[i].id)==null&&co[i].id!=bankID){
        try{
          System.out.println("Trying to connect to bank "+co[i].id);
          createConnection(co[i].ip,co[i].port,co[i].id);
          System.out.println("Successfully connected with bank "+co[i].id);
          connections++;
        }
        catch(Exception e){System.out.println("couldnt connect to bank "+co[i].id);}
      }
      i++;
      if(i>=co.length) i=0;
    }


    // At this point all the set up is done, now the bank just runs,
    // and optionally more accounts can be created, or the database saved to file
    System.out.println("bank setup finished");
    System.out.println("to add an account type a name and hit enter. ");
    System.out.println("you can also type 'save' to save accounts to csv, or 'exit' to end command input");
    String sIn = "";
    while(!sIn.equals("exit")){
    try{
      if(sIn.equals("save")){
        sIn="";
        saveDB();
      }
      if(!sIn.equals("")) addAccount(sIn);
      sIn = in.nextLine();
    }
    catch(Exception e){e.printStackTrace();}
  }


  }
  // save the current accounts into a accounts.csv file
public static void saveDB(){
  List<String> list = new ArrayList<String>();
  for (Long num: bankDB.accountMap.keySet()){
    account ac = bankDB.accountMap.get(num);
    list.add(num+","+ac.getPin()+","+ac.getName()+","+ac.getBalStr());
  }
  saveFile(list,"accounts");
}

 // generate unique account number with the specified bank id.
  public static long generateNum(){
    long id = bankID*100000000000000L+(long)(Math.random()*100000000000000L);
    while(accountMap.get(id)!=null){
      id = bankID*100000000000000L+(long)(Math.random()*100000000000000L);
    }
    return id;
  }
// add an account to the accountmap.
  public static long addAccount(String Name){
    numAccounts++;
    account a = new account(Name,0000,generateNum());
    bankDB.accountMap.put(a.getNum(),a); // store in DB
    return a.getNum();
  }

// get the first 2 digits of the account number (the bank ID of the account)
  public static int extractBankID(long l){
    String s = ""+l;
    int i =-1;
    try{
    i= Integer.parseInt(s.substring(0,2));
    }
    catch(Exception e){e.printStackTrace();}
    return i;
  }

// retrieve the dialer for the specified bank id, and forward the request through the dialer.
  public static Outcome route(Request r){
    atmDialer dialer = connectionMap.get(extractBankID(r.getNum()));
    if(dialer == null){
      return new Outcome(false,"couldnt reach other bank");
    }
    return dialer.send(r);
  }

//create a dialer with a open socket to the specified bank, store it in the connection map.
  public static void createConnection(String ip,int port,int id) throws Exception{
    atmDialer dialer = new atmDialer();
    dialer.connectToServer(ip,port);
    connectionMap.put(id,dialer);
  }




//------ utility functions ------------

public static void getDataAc(List<String> list){
  for (int i = 0;i<list.size();i++){
    String s = list.get(i);
    System.out.println(s);
      long id = Long.parseLong(s.substring(0,s.indexOf(",")));
      s= cut(s);
      int pin = Integer.parseInt(s.substring(0,s.indexOf(",")));
      s= cut(s);
      String name = s.substring(0,s.indexOf(","));
      s=cut(s);
      double bal = Double.parseDouble(s);
      accountMap.put(id,new account(id,pin,name,bal));
  }
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

//--- csv saving function ---
  public static void saveFile(List<String> outdata, String name ) {
      try {
          PrintWriter out = new PrintWriter(name+".csv");
          Iterator<String> it = outdata.iterator();
          while (it.hasNext()) {
              out.println(it.next());
          }
          out.close();
      } catch (FileNotFoundException e) {
          e.printStackTrace();
      }
  }

  // string cutting utility function
  private static String cut(String s){
    return s.substring(s.indexOf(",")+1);
  }

}


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
