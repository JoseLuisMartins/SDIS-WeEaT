package database;

/**
 * Created by PeaceOff on 09-05-2017.
 */
public class UserWeeat {

    public String username;
    public String email;
    public String image_url;

    public UserWeeat(String username, String email,String image_url) {
        this.username = username;
        this.email = email;
        this.image_url = image_url;
    }

    @Override
    public String toString() {
        return "UserWeeat{" +
                "username='" + username + '\'' +
                "email='" + email + '\'' +
                "image_url='" + image_url + '\'' +
                '}';
    }


    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getImage_url() {
        return image_url;
    }
}
