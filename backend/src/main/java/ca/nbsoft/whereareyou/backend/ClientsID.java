package ca.nbsoft.whereareyou.backend;

import WhereAreYou.BuildConfig;

/**
 * Created by Nicolas on 2015-12-06.
 */
public class ClientsID {
    static final public String WEB_CLIENT_ID = BuildConfig.WEB_CLIENT_ID;
    static final public String ANDROID_DEBUG_CLIENT_ID = BuildConfig.ANDROID_DEBUG_CLIENT_ID;
    static final public String ANDROID_AUDIENCE = WEB_CLIENT_ID;
    public static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
}
