import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class Server {
    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while(true){
                new HandleClient(serverSocket.accept()).start();
            }


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
