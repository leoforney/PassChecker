package tk.leoforney.passchecker;

/**
 * Created by Leo on 5/5/2018.
 */
public class User {
    public String token, name;

    public User() {

    }

    public User(String name, String token) {
        this.name = name;
        this.token = token;
    }

    boolean isValid() {
        if (token == null) token = "";
        if (name == null) name = "";
        return token.length() != 0 && name.length() != 0;
    }
}
