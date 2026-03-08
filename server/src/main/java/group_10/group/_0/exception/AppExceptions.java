package group_10.group._0.exception;

public class AppExceptions extends RuntimeException {
    private ErrorCode errorCode;
    public AppExceptions(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
