import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Extract useful information from client's requests.
 */
public class Request {
  protected MethodType methodType = MethodType.GET;
  protected String requestTarget = "";
  protected String protocol = "";
  protected String userAgent = "";

  /**
   * Constructor with reader from client's socket stream.
   */
  public Request(BufferedReader bufferedReader) throws IOException {
    List<String> requestContent = new ArrayList<>();
    String line;
    // Socket is like an infinite stream (unlike file, which has definite eof)
    while ((line = bufferedReader.readLine()) != null) {
      requestContent.add(line);
      if (line.contains("User-Agent: ")) {
        userAgent = line.split(" ")[1];
        break;
      }
    }

    if (requestContent.isEmpty()) {
      return;
    }

    System.out.println("requestContent: " + requestContent);
    String[] requestLineArgs = requestContent.getFirst().split(" ");
    methodType = MethodType.valueOf(requestLineArgs[0]);
    requestTarget = requestLineArgs[1];
    protocol = requestLineArgs[2];
  }

  public MethodType getMethodType() {
    return methodType;
  }

  public String getRequestTarget() {
    return requestTarget;
  }

  public String getProtocol() {
    return protocol;
  }

  public String getUserAgent() {
    return userAgent;
  }
}
