@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest extends BaseTest {

    @ClassRule
    public static DeviceAnimationTestRule deviceAnimationTestRule = new DeviceAnimationTestRule();

    @ClassRule
    public static GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule
    public IntentsTestRule<LoginActivity> activityTestRule = new IntentsTestRule<>(LoginActivity.class, true, false);

    @Before
    public void setup() {
        setIdlingPolicies();
    }

    @After
    public void destroy() {
        IdlingRegistry.getInstance().register(activityTestRule.getActivity().getCountingIdlingResource());
    }

    private void startActivity() {
        activityTestRule.launchActivity(new Intent());
        IdlingRegistry.getInstance().unregister(activityTestRule.getActivity().getCountingIdlingResource());
    }

    @Test
    public void test1_ShouldAutoLoginSucceed() throws InterruptedException {
        getApp().getPreferences().setUsername(UserService.VALID_USER_NAME);
        getApp().getPreferences().setPassword(UserService.VALID_PASSWORD);

        startActivity();

        onView(withText(R.string.login_automaticLoginInProcess))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

        assertCurrentActivityIsInstanceOf(RouteListActivity.class);
    }

    @Test
    public void test2_ShouldLogoutNotPermitAutoLogin() throws InterruptedException {
        test1_ShouldAutoLoginSucceed();

        onView(withText(R.string.logout)).perform(click());

        onView(withText(R.string.logoutWarning))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(android.R.id.button1)).perform(click());

        assertCurrentActivityIsInstanceOf(LoginActivity.class);

        onView(withId(R.id.username_edit))
                .check(matches(isEnabled()))
                .check(matches(withText("")));

        onView(withId(R.id.password_edit))
                .check(matches(isEnabled()))
                .check(matches(withText("")));
    }

    @Test
    public void test3_ShouldAutoLoginFail() throws InterruptedException {
        getApp().getPreferences().setUsername(UserService.NON_VALID_USER_NAME);
        getApp().getPreferences().setPassword(UserService.NON_VALID_PASSWORD);

        startActivity();

        onView(withText(R.string.login_automaticLoginInProcess))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

        onView(withText(R.string.login_loginFailed))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(android.R.id.button1)).perform(click());
    }

    @Test
    public void test4_ManualLoginShouldFail() {
        getApp().getPreferences().setUsername("");
        getApp().getPreferences().setPassword("");

        startActivity();

        onView(withId(R.id.username_edit))
                .check(matches(isEnabled()))
                .check(matches(withText("")))
                .perform(typeText(UserService.NON_VALID_USER_NAME));

        closeSoftKeyboard();

        onView(withId(R.id.password_edit))
                .check(matches(isEnabled()))
                .check(matches(withText("")))
                .perform(typeText(UserService.NON_VALID_PASSWORD));

        closeSoftKeyboard();

        onView(withId(R.id.login_button))
                .check(matches(isClickable()))
                .perform(click());

        onView(withText(R.string.login_loginFailed))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(android.R.id.button1)).perform(click());
    }

    @Test
    public void test5_ManualLoginShouldSucceed() {
        getApp().getPreferences().setUsername("");
        getApp().getPreferences().setPassword("");

        startActivity();

        onView(withId(R.id.username_edit))
                .check(matches(isEnabled()))
                .check(matches(withText("")))
                .perform(typeText(UserService.VALID_USER_NAME));

        closeSoftKeyboard();

        onView(withId(R.id.password_edit))
                .check(matches(isEnabled()))
                .check(matches(withText("")))
                .perform(typeText(UserService.VALID_PASSWORD));

        closeSoftKeyboard();

        onView(withId(R.id.login_button))
                .check(matches(isClickable()))
                .perform(click());

        assertCurrentActivityIsInstanceOf(RouteListActivity.class);
    }
}
