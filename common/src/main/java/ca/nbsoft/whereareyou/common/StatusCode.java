package ca.nbsoft.whereareyou.common;

public class StatusCode {
    // Failure results
    public static final int RESULT_ERROR = -1;
    public static final int RESULT_CONTACT_ALREADY_ADDED = -2;
    public static final int RESULT_CONTACT_REQUEST_PENDING = -3;
    public static final int RESULT_NO_PENDING_REQUEST = -4;
    public static final int RESULT_NOT_IN_CONTACT = -5;
    public static final int RESULT_USER_UNSUBSCRIBED = -6;
    public static final int RESULT_NO_USER_WITH_EMAIL = -7;
    public static final int RESULT_NOT_REGISTERED = -8;

    // Success result
    public static final int RESULT_OK = 0;
    public static final int RESULT_ACCOUNT_ALREADY_EXISTS = 1;

}
