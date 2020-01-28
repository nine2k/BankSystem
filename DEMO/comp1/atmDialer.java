// basic socket client with send function that recieves a reply

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class atmDialer {

    private ObjectInputStream is = null;
    private ObjectOutputStream os = null;
    private Socket socket = null;

    public atmDialer() {
    }

    public void connectToServer(String IP, int port) throws Exception{

        // Make connection
            socket = new Socket(IP, port);
            System.out.println("connected");
            os = new ObjectOutputStream(socket.getOutputStream());
            os.flush();
            is = new ObjectInputStream(socket.getInputStream());
    }

    // Send command for sending a message through the socket and returning the reply
    public Outcome send(Request r) {
        Outcome reply = null;
        try{
          os.writeObject(r);
          //os.close();
          System.out.println("sent a request!");
          reply = (Outcome) is.readObject();
          //is.close();
        }
        catch(Exception e) {
          System.out.println("unable to read object");
          e.printStackTrace();
        }
        return reply;
      }



    }
