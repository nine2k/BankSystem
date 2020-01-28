import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

//----------------------------------------
// A simple server which listens on a port
// and spawns workers to deal with them
//----------------------------------------

public class portServer extends Thread {

  private BufferedReader in;
  private PrintWriter out;
  private Socket serverSocket = null;
  public int port;

  public portServer(int p) {
    System.out.println("Observation server open on port: "+p);
    port = p;
  }

  @Override
  public void run(){
    try{
      openServer(port);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  public void interupt(){
    System.out.println("Server thread interrupted");
    System.exit(0);
  }

  public void openServer(int portNum) throws IOException {
    // open the server port
      try (
              ServerSocket serverSocket = new ServerSocket(portNum);
      ) {
          // respond to socket requests
          while (true) {
          //accept a connection, spawns a worker thread for that connection.
            Socket s = serverSocket.accept();
            connectionHandler worker = new connectionHandler(s);
            worker.start();
            System.out.println("port server connection accepted");
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
  }
}
