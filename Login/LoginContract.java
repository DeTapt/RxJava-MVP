public class LoginContract {
    public interface View extends BaseView {
        void setCredentials(String username, String password);
        void onLoginSuccess();
        void onLoginFailure();
        void tryAutoLogin();
    }

    public interface Presenter extends BasePresenter {
        void startLogin(String username, String password);
    }
}