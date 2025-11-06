package network; 

import logic.GameController;
import logic.move.Move;
import java.io.*;
import java.net.*;
import javax.swing.SwingUtilities;

public class NetworkManager {

    private GameController gameController;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private boolean isHost;


    private static final int DEFAULT_PORT = 9999; 

    public NetworkManager(GameController controller) {
        this.gameController = controller;
    }

    //
    public void startHost() {
        this.isHost = true;
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
                System.out.println("Server đang chờ kết nối tại cổng " + DEFAULT_PORT + "...");
                socket = serverSocket.accept();
                System.out.println("Client đã kết nối!");
                
                setupStreams();
                gameController.setClientColor(true); 
                listenForMoves();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //Join 
    public void startJoin(String hostIp) {
        this.isHost = false;
        new Thread(() -> {
            try {
                System.out.println("Đang kết nối tới " + hostIp + ":" + DEFAULT_PORT + "...");
                socket = new Socket(hostIp, DEFAULT_PORT);
                System.out.println("Đã kết nối tới server!");
                
                setupStreams(); 	//out,inputStream
                gameController.setClientColor(false); 
                listenForMoves();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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

    private void listenForMoves() {
        try {
            while (true) {
                Move opponentMove = (Move) in.readObject();
                System.out.println("Đã nhận nước đi!");
                
                

                SwingUtilities.invokeLater(() -> {
                    gameController.applyNetworkMove(opponentMove);
                });
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Mất kết nối!");
        }
    }
}