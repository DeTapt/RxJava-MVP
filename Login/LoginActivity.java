public class LoginActivity extends BaseActivity implements LoginContract.View {

    private static String TAG = LoginActivity.class.getSimpleName();

    private ActivityLoginBinding bindings;

    @Inject
    protected LoginContract.Presenter presenter;

    private boolean isPerformingLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View view = findViewById(R.id.login_button);
            if (view != null) {
                setTheme(R.style.LoginTheme);
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        bindings = DataBindingUtil.setContentView(this, R.layout.activity_login);

        bindings.loginButton.setOnClickListener(vw -> {
            RxPermissions.getInstance(this)
                    .request(Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                    .subscribe(granted -> {
                        if (granted) {
                            isPerformingLogin = true;
                            showLoadingIndicator();
                            presenter.startLogin(bindings.usernameEdit.getText().toString(),
                                    bindings.passwordEdit.getText().toString());
                        } else {
                            getDialogUtils().showAlertDialog(R.string.genericError_title,
                                    R.string.noLocationPermissionError_body);
                        }
                    });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    private void showRideList() {
        Intent intent = new Intent(this, RouteListActivity.class);
        startActivity(intent);
    }

    @Override
    public void setCredentials(String username, String password) {
        bindings.usernameEdit.setText(username);
        bindings.passwordEdit.setText(password);
    }

    @Override
    public void onLoginSuccess() {
        isPerformingLogin = false;
        hideLoadingIndicator();
        showRideList();
    }

    @Override
    public void onLoginFailure() {
        LoginActivity.this.isPerformingLogin = false;
        hideLoadingIndicator();
        EspressoIdlingResource.increment();
        getDialogUtils().showAlertDialog(R.string.genericError_title, R.string.login_loginFailed);
        EspressoIdlingResource.decrement();
    }

    @Override
    public void tryAutoLogin() {
        if (isPerformingLogin) {
            return;
        }
        bindings.loginButton.post(() -> {
            final String username = bindings.usernameEdit.getText().toString();
            final String password = bindings.passwordEdit.getText().toString();

            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                RxPermissions.getInstance(this)
                        .request(Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
                                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                        .subscribe(granted -> {
                            if (granted) {
                                Toast.makeText(getApplicationContext(), R.string.login_automaticLoginInProcess, Toast.LENGTH_SHORT).show();
                                isPerformingLogin = true;
                                showLoadingIndicator();
                                presenter.startLogin(username, password);
                            } else {
                                getDialogUtils().showAlertDialog(R.string.genericError_title,
                                        R.string.noLocationPermissionError_body);
                            }
                        });
            }
        });
    }
}