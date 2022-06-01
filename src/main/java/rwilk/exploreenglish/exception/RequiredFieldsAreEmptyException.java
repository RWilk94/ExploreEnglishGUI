package rwilk.exploreenglish.exception;

public class RequiredFieldsAreEmptyException extends RuntimeException {

  public RequiredFieldsAreEmptyException(final String message) {
    super(message);
  }
}
