import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Respond to client's request.
 */
public class Response {
  protected String protocol;
  protected StatusCode statusCode;
  protected String header = "";
  protected String body = "";
  protected BufferedWriter bufferedWriter;

  /**
   * Construct a response, attach client's writer to it.
   */
  public Response(BufferedWriter bufferedWriter) {
    this.bufferedWriter = bufferedWriter;
  }

  /**
   * Response client.
   */
  public void response() throws IOException {
    bufferedWriter.write(
        String.format("%s %s\r\n%s\r\n%s", protocol, statusCode.getContent(), header, body));
    bufferedWriter.flush();
  }

  /**
   * Response to a request. Provided that current writer is its writer.
   */
  public void responseTo(Request request) throws IOException {
    header = "";
    body = "";
    protocol = "HTTP/1.1";
    String[] targets = request.getRequestTarget().split("/");
    System.out.println(Arrays.toString(targets));
    if (targets.length == 0) {
      statusCode = StatusCode.OK;
    } else if (targets.length >= 2 && targets[targets.length - 2].equals("echo")) {
      statusCode = StatusCode.OK;
      header += "Content-Type: text/plain\r\n";
      header += "Content-Length: " + targets[targets.length - 1].length() + "\r\n";
      body += targets[targets.length - 1];
    } else if (targets[targets.length - 1].equals("user-agent")) {
      statusCode = StatusCode.OK;
      header += "Content-Type: text/plain\r\n";
      header += "Content-Length: " + request.getUserAgent().length() + "\r\n";
      body += request.getUserAgent();
    } else {
      statusCode = StatusCode.NOT_FOUND;
    }
    response();
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public StatusCode getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(StatusCode statusCode) {
    this.statusCode = statusCode;
  }

  public String getHeader() {
    return header;
  }

  public void setHeader(String header) {
    this.header = header;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public BufferedWriter getBufferedWriter() {
    return bufferedWriter;
  }

  public void setBufferedWriter(BufferedWriter bufferedWriter) {
    this.bufferedWriter = bufferedWriter;
  }
}
