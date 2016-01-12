package ca.nbsoft.whereareyou.backend.api;

/**
 * Created by Nicolas on 2016-01-08.
 */
public class NewAccountInfo {
    String displayName;
    String photoUrl;

    public NewAccountInfo()
    {

    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


}
