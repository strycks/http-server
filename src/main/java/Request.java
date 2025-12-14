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
  protected String userAgent = null;
  protected BufferedReader bufferedReader = null;

  /**
   * Constructor with reader from client's socket stream.
   */
  public Request(BufferedReader bufferedReader) throws IOException {
    this.bufferedReader = bufferedReader;
    String line = bufferedReader.readLine();
    if (line == null) {
      return;
    }
    String[] requestLineArgs = line.split(" ");
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

  /**
   * return userAgent or read until userAgent appears.
   */
  public String getUserAgent() throws IOException {
    if (userAgent == null && bufferedReader != null) {
      String line = bufferedReader.readLine();
      while (!line.contains("User-Agent: ")) {
        line = bufferedReader.readLine();
      }
      userAgent = line.split(" ")[1];
    }
    return userAgent;
  }
}
