package sample.server;

public class UserOfServer {

    public String UserName , Password;
    public UserOfServer() {

    }
    public UserOfServer(String userName, String password) {
        UserName = userName;
        Password = password;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
