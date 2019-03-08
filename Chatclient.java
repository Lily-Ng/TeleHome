/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author Lily Ng
 * thn248@nyu.edu   
 * 
 */
public class Chatclient {
    
    String server;
    String username;
    boolean usernameSet;
    boolean serverSet;
    JFrame serverSelect;
    JFrame jf;
    JTextField msgbox;
    JTextArea topbox;
    PrintStream sout;

    /**
     * @param args the command line arguments
     */
    
    public Chatclient(){
        serverSet = false;
        usernameSet = false;
        serverSelect = new JFrame("Choose a server");
        serverSelect.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        serverSelect.setSize(200,400);
        GridLayout myGridLayout = new GridLayout(5,1,5,5);
        serverSelect.setLayout(myGridLayout);
        serverSelect.add(new Label("Select a server to enter below:", Label.CENTER));
        JButton a = new JButton("A");
        a.addActionListener(new ButtonAction());
        JButton b = new JButton("B");
        b.addActionListener(new ButtonAction());
        JButton c = new JButton("C");
        c.addActionListener(new ButtonAction());
        JButton d = new JButton("D");
        d.addActionListener(new ButtonAction());
        serverSelect.add(a);
        serverSelect.add(b);
        serverSelect.add(c);
        serverSelect.add(d);
        serverSelect.setVisible(true);
        jf = new JFrame("Chat Client");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(700,500);
        jf.setLayout(new GridLayout(3,1,20,0));
        topbox = new JTextArea();
        topbox.setLineWrap(true);
        topbox.setEditable(false);
        JScrollPane scroll = new JScrollPane(topbox);
        msgbox = new JTextField();
        JButton submit = new JButton("SEND");
        submit.addActionListener(new SubmitAction());
        jf.add(scroll, BorderLayout.NORTH);
        jf.add(msgbox, BorderLayout.CENTER);
        jf.add(submit, BorderLayout.SOUTH);
        Socket s = null;
        try {
            s = new Socket("127.0.0.1",5190);
            if (s.isConnected()){
                sout = new PrintStream(s.getOutputStream());
            }
        } catch (IOException ex) {
            System.out.println("No Connection. Check to see that the server is running first.");
        }
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        new Chatclient();
    }
    
    class ButtonAction implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton target = (JButton) e.getSource();
            server = target.getText();
            serverSet = true;
            serverSelect.setVisible(false);
            serverSelect.dispose();
            jf.setVisible(true);
        }
    }
    
    class SubmitAction implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if (usernameSet){
                sout.print(msgbox.getText()+"\r\n");
            }else{
                username = msgbox.getText();
                usernameSet = true;
                sout.print(server + " " + username + "\r\n");
                new OngoingChat().start();
            }
            msgbox.setText("");
        }
    }
    
    class OngoingChat extends Thread{
    public void run(){
        while(true){
        Connection conn = null; //The SQL Connection
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://127.0.0.1/java";	// connect to database hosted locally
            String dbuser="root";	// database username here
            String password = "";	// database password here
            conn = DriverManager.getConnection(url,dbuser,password);
            Statement s;
            ResultSet rs;
            s = conn.createStatement();
            s.executeQuery("select * from chat where server = \'"+ server + "\';");
            rs = s.getResultSet();
            String result = "";
            while(rs.next()){
                String u = rs.getString("username");
                String m = rs.getString("message");
                result += u + ": "+ m + "\r\n";
            }
            result += "";
            topbox.setText(result);
            rs.close();
            s.close();
            conn.close();
        }catch (Exception e){
            System.out.println("Unable to connect to Database");
        }}}
    }
}