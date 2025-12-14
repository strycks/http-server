import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Main class.
 */
public class Main {
  /**
   * Main method.
   */
  public static void main(String[] args) {
    try {
      ServerSocket serverSocket = new ServerSocket(4221);

      // Since the tester restarts your program quite often, setting SO_REUSEADDR
      // ensures that we don't run into 'Address already in use' errors
      serverSocket.setReuseAddress(true);

      Socket clientSocket = serverSocket.accept(); // Wait for connection from client.
      System.out.println("accepted new connection");

      OutputStreamWriter outputStreamWriter =
          new OutputStreamWriter(clientSocket.getOutputStream());
      BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
      bufferedWriter.write("HTTP/1.1 200 OK\r\n\r\n");
      bufferedWriter.flush();

    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
