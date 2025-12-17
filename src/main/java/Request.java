import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
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
  protected Socket socket = null;
  protected BufferedReader reader = null;
  protected boolean closing = false;

  /**
   * Constructor with reader from client's socket stream.
   */
  public Request(Socket socket) throws IOException {
    this.socket = socket;
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
  }

  /**
   * Parse request.
   */
  public void parseRequest() throws IOException {
    String line = reader.readLine();
    clean();
    List<String> requestHeaders = new ArrayList<>();
    do {
      line = line.trim();
      requestHeaders.add(line);
      if (line.toLowerCase().startsWith("user-agent: ")) {
        userAgent = line.substring("user-agent: ".length());
      } else if (line.toLowerCase().startsWith("content-length: ")) {
        contentLength = Integer.parseInt(line.substring("content-length: ".length()));
      } else if (line.toLowerCase().startsWith("accept-encoding: ")) {
        compressions = Arrays.asList(line.substring("accept-encoding: ".length()).split(","));
        compressions.replaceAll(String::trim);
      } else if (line.toLowerCase().startsWith("connection: close")) {
        closing = true;
        return;
      }
    } while ((line = reader.readLine()) != null && !line.isEmpty());
    if (requestHeaders.isEmpty()) {
      return;
    }
    if (contentLength > 0) {
      body = new byte[contentLength];
      for (int i = 0; i < contentLength; i++) {
        body[i] = (byte) reader.read();
      }
    }
    String[] requestLineArgs = requestHeaders.getFirst().split(" ");
    methodType = MethodType.valueOf(requestLineArgs[0]);
    requestTarget = requestLineArgs[1];
    protocol = requestLineArgs[2];
  }

  private void clean() {
    methodType = MethodType.GET;
    requestTarget = "";
    protocol = "";
    userAgent = "";
    compressions = new ArrayList<>();
    contentLength = 0;
    body = null;
    closing = false;
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

  public boolean isClosing() {
    return closing;
  }

  public BufferedReader getReader() {
    return reader;
  }
}
