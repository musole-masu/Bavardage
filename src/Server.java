import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void acceptConnection(){
        while (!serverSocket.isClosed()){
            try {
                Socket socket = serverSocket.accept();
                System.out.println("=== New Connection Established!! ===");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            } catch (IOException e) {
                try {
                    if (serverSocket != null) serverSocket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
    public static void main(String[] args) {
        try {
            int portNum = Integer.parseInt(args[0]);
            ServerSocket serverSocket = new ServerSocket(portNum);
            System.out.println("Server is up and running (" + InetAddress.getLocalHost().getHostAddress() + "-" + portNum + ")");
            Server server = new Server(serverSocket);
            server.acceptConnection();
        } catch (IOException e) {
            System.out.println("Server could not start!!");
        }
    }
}
