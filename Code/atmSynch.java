import java.util.concurrent.Semaphore;
// shared memory for the atm
public class atmSynch{
  public static String button;
  public static String msg;
  public static Semaphore key;

  public static void keyRelease(){
    key.release(1);
  }

}
