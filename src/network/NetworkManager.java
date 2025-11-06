package network; 

import logic.GameController;
import logic.move.Move;
import java.io.*;
import java.net.*;
import javax.swing.SwingUtilities;

public class NetworkManager {

    private GameController gameController;
    private Socket socket;
    private ServerSocket serverSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private int restart = 0; 	//0 = host, -1 = client
    private String IPHost;

    private static final int DEFAULT_PORT = 9999; 

    public NetworkManager(GameController controller) {
        this.gameController = controller;
    }
    
    public int getRestart() {
    	return this.restart;
    }
    
    public void setRestart(int num) {
    	this.restart = num;
    }
    
    public String getIPHost() {
    	return this.IPHost;
    }

    public void startHost() {
        new Thread(() -> {
            
            try {
                this.serverSocket = new ServerSocket(DEFAULT_PORT); 
                System.out.println("Server đang chờ kết nối tại cổng " + DEFAULT_PORT + "...");
                socket = serverSocket.accept(); 
                System.out.println("Client đã kết nối!");
                
                setupStreams();
                gameController.setClientColor(true); 
                listenForObjects(); 
            } catch (IOException e) {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //Join 
    public void startJoin(String hostIp) {
    	this.IPHost = hostIp;
        new Thread(() -> {
            try {
                System.out.println("Đang kết nối tới " + hostIp + ":" + DEFAULT_PORT + "...");
                socket = new Socket(hostIp, DEFAULT_PORT);
                System.out.println("Đã kết nối tới server!");
                setupStreams(); 	//out,inputStream
                gameController.setClientColor(false); 
                listenForObjects();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public void close() {
        try {
            System.out.println("Đang đóng các kết nối mạng...");
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            if (serverSocket != null) serverSocket.close(); // <-- QUAN TRỌNG NHẤT
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupStreams() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }
    
    public void sendMove(Move move) {
        if (out != null) {
            try {
                out.writeObject(move);
                out.flush(); 
                System.out.println("Đã gửi nước đi: " + move.getFromRow() + "," + move.getFromCol());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void listenForObjects() { 
        try {
            while (true) {
                Object obj = in.readObject(); 

                if (obj instanceof Move) {
                    Move opponentMove = (Move) obj;
                    System.out.println("Đã nhận nước đi!");
                    SwingUtilities.invokeLater(() -> {
                        gameController.applyNetworkMove(opponentMove);
                    });

                } else if (obj instanceof String) {
                    String command = (String) obj;
                    System.out.println("Đã nhận lệnh: " + command);
                    SwingUtilities.invokeLater(() -> {
                        gameController.applyNetworkCommand(command); 
                    });
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Mất kết nối!");
        }
    }
    
    public void sendCommand(String command) {
        if (out != null) {
            try {
                out.writeObject(command); 
                out.flush();
                System.out.println("Đã gửi lệnh: " + command);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}