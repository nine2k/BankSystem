import java.util.concurrent.ConcurrentHashMap;
public class bankDB{
// shared memory for the bank, this is where the accounts are stored
  public static ConcurrentHashMap<Long,account> accountMap = new ConcurrentHashMap<Long,account>();
  public static int bankID;

}
