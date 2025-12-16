import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Main class.
 */
public class Main {
  /**
   * Main method.
   */
  public static void main(String[] args) {
    ServerSocket serverSocket = null;
    ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    try {
      serverSocket = new ServerSocket(4221);

      // Since the tester restarts your program quite often, setting SO_REUSEADDR
      // ensures that we don't run into 'Address already in use' errors
      serverSocket.setReuseAddress(true);

      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("accepted new connection");

        FutureTask<Void> task = new FutureTask<>(() -> {
          BufferedReader bufferedReader =
              new BufferedReader(
                  new InputStreamReader(clientSocket.getInputStream())
              );
          BufferedWriter bufferedWriter =
              new BufferedWriter(
                  new OutputStreamWriter(clientSocket.getOutputStream())
              );
          Request request = new Request(bufferedReader);
          Response response = new Response(bufferedWriter);

          response.responseTo(request);

          bufferedReader.close();
          bufferedWriter.close();
          clientSocket.close();
          return null;
        });

        executorService.submit(task);
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    } finally {
      executorService.close();
      try {
        if (serverSocket != null) {
          serverSocket.close();
        }
      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      }
    }
  }
}
