/**
 * Status codes.
 */
public enum StatusCode {
  OK("200 OK"),
  NOT_FOUND("404 Not Found"),
  CREATED("201 Created"),
  BAD_REQUEST("400 Bad Request");

  private final String content;

  StatusCode(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }
}
