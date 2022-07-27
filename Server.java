import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
public class Server extends JFrame
{
    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    private JLabel heading = new JLabel("Server Area");
    private JTextArea msgArea = new JTextArea();
    private JTextField msgWrite = new JTextField();
    private Font font = new Font("Comic Sans MS",Font.PLAIN,20);
    //Constructor
    public Server()
    {
        try
        {
            server = new ServerSocket(8080);
            System.out.println("Server is ready to accept connection");
            System.out.println("Waiting....");
            socket = server.accept();
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            createGUI();
            handleEvents();
            startReading();
            //startWriting();
        }
        catch (Exception e)
        {
            //TODO: handle exception
            e.printStackTrace();
        }
    }
    public void handleEvents() {
        msgWrite.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                System.out.println("Key released" + e.getKeyCode());
                if (e.getKeyCode() == 10) {
                    String contentToSend = msgWrite.getText();
                    msgArea.append("Me :" + contentToSend + "\n");
                    out.println(contentToSend);
                    out.flush();
                    msgWrite.setText("");
                    msgWrite.requestFocus();
                }
            }
        });
    }
    public void createGUI()
    {
        //GUI code...
        //this = window
        this.setTitle("Server Side");
        this.setSize(700,700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// program will stop on clicking cross button
        this.setVisible(true);
        //coding for component
        heading.setFont(font);
        msgArea.setFont(font);
        msgWrite.setFont(font);
        heading.setIcon(new ImageIcon("CA.jpg"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        msgArea.setEditable(false);
        msgWrite.setHorizontalAlignment(SwingConstants.CENTER);
        //let's set layout for our frame
        this.setLayout(new BorderLayout());
        //now we are going to add components to the frame
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(msgArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(msgWrite,BorderLayout.SOUTH);
    }
    public void startReading()
    {
        // thread- read krke deta rhega
        Runnable r1=()->
        {
            System.out.println("Reader started.");
            try
            {
                while (true) {
                    String msg;
                    msg = br.readLine();
                    if (msg.equals("exit"))
                    {
                        System.out.println("Client terminated the chat.");
                        JOptionPane.showMessageDialog(this ,"server terminated the chat");
                        msgWrite.setEnabled(false);
                        break;
                    }
                    msgArea.append("Client : " + msg+"\n");
                }
            }
            catch(Exception e)
            {
                //e.printStackTrace();
                System.out.println("Connection closed");
            }
        };
        new Thread(r1).start();
    }
    public void startWriting()
    {
        //thread- data ko user se lega then usko send krega client tk
        Runnable r2=()->
        {
            System.out.println("Writer started");
            try
            {
                while (true)
                {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();
                    if(content.equals("exit"))
                    {
                        System.out.println("Server terminated the chat.");
                        socket.close();
                        break;
                    }
                    System.out.println("Server: "+ content);
                }
            }
            catch(Exception e)
            {
                //e.printStackTrace();
                System.out.println("Connection closed");
            }
        };
        new Thread(r2).start();
    }
    public static void main(String[] args)
    {
        System.out.println("This is server..going to start server");
        new Server();
    }
}
