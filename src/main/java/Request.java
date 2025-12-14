import java.io.BufferedReader;
import java.io.IOException;

/**
 * Extract useful information from client's requests.
 */
public class Request {
  protected MethodType methodType = MethodType.GET;
  protected String requestTarget = "";
  protected String protocol = "";

  /**
   * Constructor with reader from client's socket stream.
   */
  public Request(BufferedReader bufferedReader) throws IOException {
    if (bufferedReader.ready()) {
      String[] requestLineArgs = bufferedReader.readLine().split(" ");
      methodType = MethodType.valueOf(requestLineArgs[0]);
      requestTarget = requestLineArgs[1];
      protocol = requestLineArgs[2];
    }
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
}
