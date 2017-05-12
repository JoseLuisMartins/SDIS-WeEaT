package database;

/**
 * Created by PeaceOff on 09-05-2017.
 */
public class UserWeeat {

    public String username;
    public String email;

    public UserWeeat(String username, String email) {
        this.username = username;
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserWeeat{" +
                "username='" + username + '\'' +
                "email='" + email + '\'' +
                '}';
    }
}
