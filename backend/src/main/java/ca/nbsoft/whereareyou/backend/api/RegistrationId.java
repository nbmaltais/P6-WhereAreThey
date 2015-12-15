package ca.nbsoft.whereareyou.backend.api;

/**
 * Created by Nicolas on 2015-12-03.
 */
public class RegistrationId {

    private String mToken;

    public String getToken() {
        return mToken;
    }

    public void setToken(String t) {
        mToken = t;
    }

    public RegistrationId()
    {

    }

    public RegistrationId(String token)
    {
        mToken=token;
    }

}
