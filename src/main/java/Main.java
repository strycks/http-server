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
    final AtomicInteger completed = new AtomicInteger();
    final AtomicInteger started = new AtomicInteger();

    final String path;
    if (args.length == 2 && args[0].equals("--directory")) {
      path = args[1];
    } else {
      path = "";
    }

    try (ServerSocket serverSocket = new ServerSocket(4221, 250);
         ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()
    ) {
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
            System.out.println("accepted new connection " + started.getAndIncrement());

            Request request = new Request(bufferedReader);
            Response response = new Response(bufferedWriter);
            response.setStoragePath(path);

            response.responseTo(request);

            System.out.println("completed connection " + completed.getAndIncrement());
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
    }
  }
}
