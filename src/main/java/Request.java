import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Extract useful information from client's requests.
 */
public class Request {
  protected MethodType methodType = MethodType.GET;
  protected String requestTarget = "";
  protected String protocol = "";
  protected String userAgent = "";
  protected List<String> compressions = new ArrayList<>();
  protected int contentLength = 0;
  protected byte[] body = null;
  protected BufferedReader bufferedReader = null;

  /**
   * Constructor with reader from client's socket stream.
   */
  public Request(BufferedReader bufferedReader) throws IOException {
    this.bufferedReader = bufferedReader;
    String line = null;
    List<String> requestHeaders = new ArrayList<>();
    while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
      line = line.trim();
      requestHeaders.add(line);
      if (line.toLowerCase().startsWith("user-agent: ")) {
        userAgent = line.substring("user-agent: ".length());
      } else if (line.toLowerCase().startsWith("content-length: ")) {
        contentLength = Integer.parseInt(line.substring("content-length: ".length()));
      } else if (line.toLowerCase().startsWith("accept-encoding: ")) {
        compressions = Arrays.asList(line.substring("accept-encoding: ".length()).split(","));
        compressions.replaceAll(String::trim);
      }
    }
    if (requestHeaders.isEmpty()) {
      return;
    }
    if (contentLength > 0) {
      body = new byte[contentLength];
      for (int i = 0; i < contentLength; i++) {
        body[i] = (byte) bufferedReader.read();
      }
    }
    String[] requestLineArgs = requestHeaders.getFirst().split(" ");
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

  public byte[] getBody() {
    return body;
  }

  public List<String> getCompressions() {
    return compressions;
  }
}
