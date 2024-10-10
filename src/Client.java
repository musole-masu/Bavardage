import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public Client(Socket socket){
        try {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            closeConnection(socket, objectInputStream, objectOutputStream);
        }
    }



    public void waitForServerResp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (socket.isConnected()) {
                        String message = String.valueOf(objectInputStream.readObject());
                        System.out.println(message);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    closeConnection(socket, objectInputStream, objectOutputStream);
                }
            }
        }).start();
    }

    public void sendMessage(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Please Enter a message: \n");

        try {
            while (socket.isConnected()){
                String message = sc.nextLine();
                objectOutputStream.writeObject(message);
            }
        } catch (IOException e) {
            closeConnection(socket, objectInputStream, objectOutputStream);
        }
    }
    public void closeConnection(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
        try {
            if (socket != null) socket.close();
            if (objectOutputStream != null) objectOutputStream.close();
            if (objectInputStream != null) objectInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) throws IOException {
        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        Socket socket = new Socket(serverIP, serverPort);
        Client client = new Client(socket);
        client.waitForServerResp();
        client.sendMessage();
    }
}
