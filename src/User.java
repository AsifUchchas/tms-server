import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String nid;
    private String email;
    private String phone;
    private String passwords;
    private String type;

    public User(String name, String nid, String email, String passwords, String phone, String type) {
        this.name = name;
        this.nid = nid;
        this.phone = phone;
        this.email = email;
        this.passwords = passwords;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswords() {
        return passwords;
    }

    public void setPasswords(String passwords) {
        this.passwords = passwords;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
