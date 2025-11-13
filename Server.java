import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;

public class Server extends JFrame {
    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter chep;

    private JLabel heading = new JLabel("Server Messenger");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Segoe UI", Font.PLAIN, 20);

    public Server() {
        try {
            server = new ServerSocket(1522);
            System.out.println("Server is ready to accept connection.\nWaiting...");
            socket = server.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            chep = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();
            startReading();
            // startWriting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        setTitle("Server Messenger");
        setSize(650, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- UI setup ---
        heading.setFont(font);
        heading.setForeground(new Color(0, 128, 0)); // green when connected
        heading.setIcon(new ImageIcon("newCA.jpg"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        messageArea.setFont(font);
        messageArea.setEditable(false);
        messageInput.setFont(font);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        // --- Layout setup ---
        setLayout(new BorderLayout());
        JScrollPane scroll = new JScrollPane(messageArea);
        add(heading, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(messageInput, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void handleEvents() {
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String contentToSend = messageInput.getText();
                    String time = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());
                    messageArea.append("Me: " + contentToSend + " [" + time + "]\n");

                    chep.println(contentToSend);
                    chep.flush();

                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }
        });
    }

    private void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader started...");
            try {
                while (true) {
                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        System.out.println("Client terminated the chat.");
                        JOptionPane.showMessageDialog(this, "Client terminated the chat. Connection closed.");
                        messageInput.setEnabled(false);
                        heading.setText("Disconnected..");
                        heading.setForeground(Color.RED);
                        socket.close();
                        break;
                    }
                    String time = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());
                    messageArea.append("Client: " + msg + " [" + time + "]\n");
                }
            } catch (Exception e) {
                System.out.println("Connection closed.. reader");
            }
        };
        new Thread(r1).start();
    }

    private void startWriting() {
        Runnable r2 = () -> {
            System.out.println("Writer started...");
            try {
                while (!socket.isClosed()) {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    chep.println(content);
                    chep.flush();
                    if (content.equals("exit")) {
                        socket.close();
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Connection closed.. writer");
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is Server.. Starting Server");
        new Server();
    }
}
