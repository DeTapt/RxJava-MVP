public class BaseActivity extends RxAppCompatActivity implements BaseView {
    public static final int RETRY_INTERVAL = 10 * 1000;

    private CountingIdlingResource countingIdlingResource = new CountingIdlingResource("MAIN");
    private PopupWindow loadingIndicator;
    private DialogUtils dialogUtils;
    public DialogUtils getDialogUtils() { return dialogUtils; }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogUtils = new DialogUtils(this);
    }

    public App getApp() {
        return (App)getApplication();
    }
    public void showLoadingIndicator() {
        if (loadingIndicator != null) {
            return;
        }
        EspressoIdlingResource.increment();
        View rooView = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        loadingIndicator = new PopupWindow(inflater.inflate(R.layout.view_loader, null),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loadingIndicator.showAtLocation(rooView, Gravity.CENTER, 0, 0);
    }

    public void hideLoadingIndicator() {
        if (loadingIndicator == null) {
            return;
        }
        EspressoIdlingResource.decrement();
        loadingIndicator.dismiss();
        loadingIndicator = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void show(AppCompatDialogFragment dialog) {
        dialog.show(getSupportFragmentManager(), String.valueOf(System.currentTimeMillis()));
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return countingIdlingResource;
    }

    @Override
    public void openDialog(@StringRes int titleResId, @StringRes int bodyResId) {
        dialogUtils.showAlertDialog(titleResId, bodyResId);
    }

    @Override
    public void showToast(@StringRes int messageId) {
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
    }
}
