
// DRAWING AN ATM SCREEN VISUALIZATION

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import javax.swing.*;
import java.awt.FlowLayout;

  public class atmDisplay {
    public JFrame f;
    public JPanel j;

    public atmDisplay(){
      //initialize the atm display
  		f =new JFrame("ATM");
  		f.setSize(300,300);
  		f.setLayout(null);
      f.setResizable(false);
  		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  		}
// set the Jpanel to the required one, and update the frame
      public void setScreen(int type){
        JPanel screen = buildScreen(type);
        if(j!=null) f.remove(j);
        j = screen;
        f.add(j);
        f.revalidate();
        f.repaint();
        f.setVisible(true);
        System.out.println("screen repainted");

      }


      public JPanel buildScreen(int type){
        //Returns a JPanel object with the specifications for each type of SCREEN
        /*---screen names/numbers---
        three screen types
        1 - input SCREEN
        2 - display SCREEN
        3 - menu SCREEN
        other - wait SCREEN
        ----------------------------
        */
        System.out.println("screen "+type+" built!");

        JPanel screen = new JPanel();
        screen.setLayout(null);
        screen.setBackground(Color.RED);
        screen.setBounds(0,0,300,300);
        JLabel label = new JLabel();

        if (type == 3){
          //build the menu screen
          label.setBounds(50, 10, 200, 90);
          label.setText("Select Option");

          JButton bBalance=new JButton("Check Balance");
          bBalance.setBounds(75,95,150, 40);
          bBalance = addAL(bBalance);

          JButton bDeposit=new JButton("Deposit");
          bDeposit.setBounds(75,140,150, 40);
          bDeposit = addAL(bDeposit);

          JButton bWithdraw=new JButton("Withdraw");
          bWithdraw.setBounds(75,185,150, 40);
          bWithdraw = addAL(bWithdraw);

          JButton bExit=new JButton("Exit");
          bExit.setBounds(75,230,150, 40);
          bExit = addAL(bExit);

          screen.add(label);
          screen.add(bBalance);
          screen.add(bDeposit);
          screen.add(bWithdraw);
          screen.add(bExit);

          return screen;
        }

        if (type == 2){
          // build the display screen
          label.setBounds(10, 10, 250, 80);
          label.setText(atmSynch.msg);

          JButton bOkay=new JButton("Okay");
          bOkay.setBounds(75,100,150, 40);
          bOkay = addAL(bOkay);

          screen.add(label);
          screen.add(bOkay);

          return screen;
        }

        if (type == 1){
          //build the input SCREEN
          label.setBounds(10, 10, 250, 80);
          label.setText(atmSynch.msg);

          JTextField textfield= new JTextField();
          textfield.setBounds(75, 60, 150, 30);

          JButton bSubmit=new JButton("Submit");
          bSubmit.setBounds(75,100,150, 40);

          bSubmit.addActionListener(new ActionListener() {
      			@Override
      			public void actionPerformed(ActionEvent arg0) {
              atmSynch.button = textfield.getText();
              atmSynch.keyRelease();
      			}
          });

          screen.add(label);
          screen.add(bSubmit);
          screen.add(textfield);

          return screen;
        }
        // build the please wait screen
        label.setText("Please Wait...");
        label.setBounds(50, 10, 200, 90);

        screen.add(label);

        return screen;


      }
// add action listeners to buttons.
      public JButton addAL (JButton j){
        j.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
            atmSynch.button = j.getText();
            atmSynch.keyRelease();
          }
        });
        return j;
      }


   }
