// a outcome object is sent back to the requester.
import java.io.Serializable;

public class Outcome implements Serializable{
  private static final long serialVersionUID = 1L;
  private String msg;
  private boolean complete;

  public Outcome(boolean b, String s){
    msg = s;
    complete = b;
  }

  public Outcome(boolean b){
    complete =b;
  }

  public boolean result(){
    return complete;
  }

  public String getMsg(){
    return msg;
  }
}
