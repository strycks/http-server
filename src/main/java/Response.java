import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

/**
 * Respond to client's request.
 */
public class Response {
  protected String protocol;
  protected StatusCode statusCode;
  protected String header = "";
  protected byte[] body = "".getBytes();
  protected String storagePath = "";
  protected Socket socket;
  protected final String[] availableCompressions = {"gzip"};

  /**
   * Construct a response, attach client's writer to it.
   */
  public Response(Socket socket) throws IOException {
    this.socket = socket;
  }

  /**
   * Response client.
   */
  public void response() throws IOException {
    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
    bufferedOutputStream.write(
        String.format("%s %s\r\n%s\r\n", protocol, statusCode.getContent(), header).getBytes());
    bufferedOutputStream.write(body);
    bufferedOutputStream.flush();
  }

  /**
   * Response to a request. Provided that current writer is its writer.
   */
  public void responseTo(Request request) throws IOException {
    clean();
    String target = request.getRequestTarget();
    long contentLen = -1;
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
          contentLen = str.length();
          // header += "Content-Length: " + str.length() + "\r\n";
          body = str.getBytes();
        } else if (target.startsWith("/user-agent")) {
          statusCode = StatusCode.OK;
          header += "Content-Type: text/plain\r\n";
          contentLen = request.getUserAgent().length();
          // header += "Content-Length: " + request.getUserAgent().length() + "\r\n";
          body = request.getUserAgent().getBytes();
        } else if (target.startsWith("/files/")) {
          String str = target.replaceFirst("/files/", "");
          File file = new File(storagePath + str);
          if (file.exists() && file.isFile()) {
            statusCode = StatusCode.OK;
            header += "Content-Type: application/octet-stream\r\n";
            contentLen = file.length();
            // header += "Content-Length: " + file.length() + "\r\n";
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] binary = fileInputStream.readAllBytes();
            body = new String(binary, StandardCharsets.US_ASCII).getBytes();
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
      ByteArrayOutputStream baos = null;
      GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baos = new ByteArrayOutputStream());
      gzipOutputStream.write(body);
      gzipOutputStream.flush();
      gzipOutputStream.close();

      body = baos.toByteArray();
      contentLen = body.length;
      header += "Content-Length: " + contentLen + "\r\n";
      header += "Content-Encoding: " + availableCompressions[0] + "\r\n";
    } else if (contentLen != -1) {
      header += "Content-Length: " + contentLen + "\r\n";
    }
    response();
  }

  private void clean() {
    protocol = "HTTP/1.1";
    statusCode = StatusCode.OK;
    header = "";
    body = "".getBytes();
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

  public byte[] getBody() {
    return body;
  }

  public void setBody(byte[] body) {
    this.body = body;
  }

  public String getStoragePath() {
    return storagePath;
  }

  public void setStoragePath(String storagePath) {
    this.storagePath = storagePath;
  }
}
