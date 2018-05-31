public class LoginPresenter implements LoginContract.Presenter {

    private static String TAG = LoginPresenter.class.getSimpleName();

    private LoginContract.View view;
    private BaseUserService userService;
    private BaseLocationService locationService;
    private Preferences preferences;

    public LoginPresenter(LoginContract.View view, BaseUserService userService,
                          BaseLocationService locationService, Preferences preferences) {
        this.view = view;
        this.userService = userService;
        this.locationService = locationService;
        this.preferences = preferences;
    }

    @Override
    public void onStart() {
        locationService.start()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(everyThingIsCool -> {
                    view.setCredentials(preferences.getUsername(), preferences.getPassword());
                    view.tryAutoLogin();
                }, error -> {
                    view.openDialog(R.string.genericError_title, R.string.noLocationPermissionError_body);
                });
    }

    @Override
    public void startLogin(String username, String password) {
        Observable.defer(() -> {
            userService.reset();
            return userService.login(username, password);
        })
        //.compose(bindToLifecycle())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(user -> {
            preferences.setUsername(user.getUsername());
            preferences.setPassword(user.getPassword());
            Crashlytics.setUserIdentifier(String.valueOf(user.getUserId()));
            Crashlytics.setUserName(user.getUsername());
            return Observable.just(user);
        })
        .subscribe(user -> {
            view.onLoginSuccess();
        }, error -> {
            view.onLoginFailure();
            preferences.setUsername(null);
            preferences.setPassword(null);
            Log.e(TAG, "startLogin: ", error);
        });
    }
}
