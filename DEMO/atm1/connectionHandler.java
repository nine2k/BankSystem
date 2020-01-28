
// thread responsible for handling a connection.
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class connectionHandler extends Thread {

  private Socket worker;

  public connectionHandler(Socket s) {
    worker = s;
  }

  @Override
  public void run(){
    // in reads stuff from the socket.
    System.out.println("connection handler started");
    try{
      ObjectOutputStream os = new ObjectOutputStream(worker.getOutputStream());
      os.flush();
      ObjectInputStream is = new ObjectInputStream(worker.getInputStream());

      while(true){
        Request input = (Request) is.readObject();
        // create a request handler to handle the request
        requestHandler rh = new requestHandler();
        os.writeObject(rh.handle(input));
      }
    }
    catch(Exception e) {e.printStackTrace();}
    }
  }
