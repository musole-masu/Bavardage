import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientList = new ArrayList<>();

    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String clientIP;
    private int clientPort;

    public ClientHandler(Socket socket){
        try {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.clientIP = socket.getInetAddress().getHostAddress();
            this.clientPort = socket.getPort();

            clientList.add(this);

            broadcast("=== Client ("+clientIP + "-"+ clientPort +") just joined Bavardage!! :)");
        } catch (IOException e) {
            closeConnection(socket, objectInputStream, objectOutputStream);
        }

    }


    public void closeConnection(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
        clientList.remove(this);
        broadcast("=== Client ("+clientIP + "-"+ clientPort +") just left Bavardage!! :(");
        try {
            if (socket != null) socket.close();
            if (objectOutputStream != null) objectOutputStream.close();
            if (objectInputStream != null) objectInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void broadcast(String msg){
        for (ClientHandler client : clientList){
            try {
                client.objectOutputStream.writeObject(msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void run() {
        while (socket.isConnected()){
            try {
                String messageFromClient = String.valueOf(objectInputStream.readObject());
                System.out.println("=== From Client: " + messageFromClient);
                broadcast("=== Client ("+clientIP + "-"+ clientPort +"): "+messageFromClient);
            } catch (IOException | ClassNotFoundException e) {
                closeConnection(socket, objectInputStream, objectOutputStream);
                break;
            }
        }

    }
}
