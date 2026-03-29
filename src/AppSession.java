public class AppSession {

    private UserRecord currentUser;

    public void login(UserRecord user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public UserRecord getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
