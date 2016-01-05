package ca.nbsoft.whereareyou.backend.api;

/**
 * Created by Nicolas on 2015-12-18.
 */
public class InvalidUserException extends Exception {
    public InvalidUserException()
    {

    }

    public InvalidUserException(String m)
    {
        super(m);
    }
}
