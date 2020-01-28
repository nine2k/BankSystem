// The request handler fulfils the type of request specified in the request object by
// returning an outcome containing relevant account data.

public class requestHandler{

  public requestHandler(){

  }

  public Outcome handle(Request r){
    Outcome ret;
    long num = r.getNum();

    // check first 2 digits of account to see if this account belongs to this bank
    if (bankDB.bankID!=Bank.extractBankID(num)){
      return Bank.route(r); // if it doesnt, route to a bank that owns the account
    }
// pull the account from the database
    account act = bankDB.accountMap.get(num);
    if(act == null){ // check to make sure account actually exists.
      return new Outcome(false,"Error account does not exist");
    }
    // check for negative deposit/withdrawls
    if(r.getValue()<0){
      return new Outcome(false,"Error improper amount");
    }
    //check for too many digits after decimal
    if((""+r.getValue()).contains(".")){
      String h = ""+r.getValue();
      if(h.substring(h.indexOf(".")).length()>3)
        return new Outcome(false,"Error too many digits after decimal");
    }

    // switch to figure out how to deal with the request
    switch(r.getType()){
      case LOGON: // this is a log on request, check the pin.
      int a = r.getPin();
        if(act.check(a))
          ret = new Outcome(true,"Success!");
        else
          ret = new Outcome(false,"Error Wrong Pin");
        break;

      case DEPOSIT: // this is a deposit request, add $
        act.deposit(r.getValue());
        bankDB.accountMap.replace(num,act);
        ret = new Outcome(true,"Successfully Deposited: "+r.getValue());
        break;

      case WITHDRAW:// this is a withdrawl request remove $ if possible
        if(act.withdraw(r.getValue())){
          bankDB.accountMap.replace(num,act);
          ret = new Outcome(true,"Withdrawn "+r.getValue()+ " from account.");
        }
        else{
          ret = new Outcome(false,"Not Enough for withdrawal");
        }
       break;

      case BALANCE: // balance request, return account $ value
        ret = new Outcome(true,"Account Balance: "+act.getBalStr());
        break;

      default: // this is a incorrect request.
        ret = new Outcome(false,"Malformed request");
    }
    return ret;

  }
}
