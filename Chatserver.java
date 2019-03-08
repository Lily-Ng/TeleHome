/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
/ Database looks like this...
/ Inside table called "chat":
/ username(varchar 25)|server(char)|message(TEXT)
/
*/
package chatserver;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;

/**
 *
 * @author Lily Ng
 * thn248@nyu.edu
 * 
 */
public class Chatserver {
    static int portNum=5190;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(portNum);
            while (true){
                Socket client = ss.accept();
                new ProcessConnection(client).start();
            }
        } catch (IOException ex) {
            System.out.println("Could not get the socket!");
        }
    }
}

class ProcessConnection extends Thread{
    boolean idSet;
    Socket client;
    String server;
    String id;
    ProcessConnection(Socket news){
        client = news;
        idSet = false;
    }
    public void run(){
        try {
            Scanner sin = new Scanner(client.getInputStream());
            PrintStream sout = new PrintStream(client.getOutputStream());
            String clientmsg="";
            while (!clientmsg.equals("EXIT")){
                clientmsg = sin.nextLine();
                if (idSet == false){
                    id = clientmsg.substring(2,clientmsg.length());
                    // servers are identified by the first character of the first message.
                    // ex. "A tester123" would indicate that the user wants to access server "A" and wants his username to be "tester123"
                    server = clientmsg.substring(0,1);
                    System.out.println(client.getInetAddress().toString()+"'s username set to "+id+" on Server "+server);
                    idSet = true;
                } else {
                    if (!clientmsg.equals("EXIT")){
                        Connection conn = null; //The SQL Connection
                        try{
                            // Insert the messsage received into the underlying database
                            Class.forName("com.mysql.jdbc.Driver").newInstance();
                            // This is only my credentials. Please change if necessary when grading.
                            String url = "jdbc:mysql://127.0.0.1/java";	// connect to database hosted locally
                            String dbuser="root"; // database username here
                            String password = ""; // database password here
                            conn = DriverManager.getConnection(url,dbuser,password);
                            Statement s = conn.createStatement();
                            // all clients connected to the same server will now have access to the messages (message "relayed" to clients)
                            s.executeUpdate("INSERT INTO chat VALUES (\'"+ id +"\',\'"+server+"\',\'"+clientmsg+"\');");
                            s.close();
                            conn.close();
                        }
                        catch (Exception e){
                            System.out.println("Unable to connect to database.");
                        }
                    }
                }
            }
            client.close();
        } catch (IOException ex) {
        }
    }
}