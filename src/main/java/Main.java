import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

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
    AtomicInteger completed = new AtomicInteger();
    AtomicInteger started = new AtomicInteger();
    try {
      serverSocket = new ServerSocket(4221, 250);

      // Since the tester restarts your program quite often, setting SO_REUSEADDR
      // ensures that we don't run into 'Address already in use' errors
      serverSocket.setReuseAddress(true);

      while (true) {
        Socket clientSocket = serverSocket.accept();

        FutureTask<Void> task = new FutureTask<>(() -> {
          try (Socket socket = clientSocket;
               BufferedReader bufferedReader =
                   new BufferedReader(
                       new InputStreamReader(clientSocket.getInputStream())
                   );
               BufferedWriter bufferedWriter =
                   new BufferedWriter(
                       new OutputStreamWriter(clientSocket.getOutputStream())
                   );
               ) {
            System.out.println("accepted new connection" + started.getAndIncrement());


            Request request = new Request(bufferedReader);
            Response response = new Response(bufferedWriter);

            response.responseTo(request);

            System.out.println(completed.getAndIncrement());
          } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
          }
          return null;
        });

        try {
          executorService.submit(task);
        } catch (RejectedExecutionException e) {
          System.err.println("RejectedExecutionException" + e.getMessage());
        }
      }
    } catch (IOException e) {
      System.err.println("IOException: " + e.getMessage());
    } finally {
      executorService.close();
      try {
        if (serverSocket != null) {
          serverSocket.close();
        }
      } catch (IOException e) {
        System.err.println("IOException: " + e.getMessage());
      }
    }
  }
}
