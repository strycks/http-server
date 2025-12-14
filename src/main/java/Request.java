import java.io.BufferedReader;
import java.io.IOException;

/**
 * Extract useful information from client's requests.
 */
public class Request {
  protected final MethodType methodType;
  protected final String requestTarget;
  protected final String protocol;

  /**
   * Constructor with reader from client's socket stream.
   */
  public Request(BufferedReader bufferedReader) throws IOException {
    String[] requestLineArgs = bufferedReader.readLine().split(" ");
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
}
