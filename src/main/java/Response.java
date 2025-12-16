import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Respond to client's request.
 */
public class Response {
  protected String protocol;
  protected StatusCode statusCode;
  protected String header = "";
  protected String body = "";
  protected String storagePath = "";
  protected BufferedWriter bufferedWriter;
  protected final String[] availableCompressions = {"gzip"};

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
    String target = request.getRequestTarget();
    switch (request.getMethodType()) {
      case POST:
        if (target.startsWith("/files/")) {
          String str = target.replaceFirst("/files/", "");
          File file = new File(storagePath + str);
          if (file.getAbsolutePath().endsWith("/")) {
            statusCode = StatusCode.BAD_REQUEST;
            break;
          }

          BufferedOutputStream bufferedOutputStream =
              new BufferedOutputStream(new FileOutputStream(file));
          bufferedOutputStream.write(request.getBody());
          bufferedOutputStream.flush();
          statusCode = StatusCode.CREATED;
          bufferedOutputStream.close();
        }
        break;
      default: // get
        if (target.equals("/")) {
          statusCode = StatusCode.OK;
        } else if (target.startsWith("/echo/")) {
          String str = target.replaceFirst("/echo/", "");
          statusCode = StatusCode.OK;
          header += "Content-Type: text/plain\r\n";
          header += "Content-Length: " + str.length() + "\r\n";
          body += str;
        } else if (target.startsWith("/user-agent")) {
          statusCode = StatusCode.OK;
          header += "Content-Type: text/plain\r\n";
          header += "Content-Length: " + request.getUserAgent().length() + "\r\n";
          body += request.getUserAgent();
        } else if (target.startsWith("/files/")) {
          String str = target.replaceFirst("/files/", "");
          File file = new File(storagePath + str);
          if (file.exists() && file.isFile()) {
            statusCode = StatusCode.OK;
            header += "Content-Type: application/octet-stream\r\n";
            header += "Content-Length: " + file.length() + "\r\n";
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] binary = fileInputStream.readAllBytes();
            body += new String(binary, StandardCharsets.US_ASCII);
            fileInputStream.close();
          } else {
            statusCode = StatusCode.NOT_FOUND;
          }
        } else {
          statusCode = StatusCode.NOT_FOUND;
        }
    }
    if (request.getCompressions().contains(availableCompressions[0])
        && statusCode == StatusCode.OK) {
      header += "Content-Encoding: " + availableCompressions[0] + "\r\n";
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

  public String getStoragePath() {
    return storagePath;
  }

  public void setStoragePath(String storagePath) {
    this.storagePath = storagePath;
  }
}
