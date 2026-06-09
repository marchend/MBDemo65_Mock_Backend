import XCTest

/// XCUITest suite for the Login critical user flow.
///
/// Satisfies bootstrap.md §13.2 and the §15 Story Implementation
/// Checklist: "If the story is a critical user flow (login / transfer /
/// sign-out) it also has an XCUITest in `AcmeBankUITests/`."
///
/// Coverage:
///   1. Sign-in button is disabled when username / password are empty.
///   2. Sign-in button enables only when both fields are non-empty.
///   3. Happy-path: field entry → button enabled → tap → home tab bar visible.
///   4. Password visibility toggle shows and hides the plain-text field.
///   5. "Need help?" button opens the help sheet.
final class LoginUITests: XCTestCase {

    // MARK: - Setup

    var app: XCUIApplication!

    override func setUpWithError() throws {
        continueAfterFailure = false
        app = XCUIApplication()
        // Launch with a flag so the app can stub authentication
        // without hitting the real Okta network.
        app.launchArguments = ["--uitesting", "--stub-auth-success"]
        app.launch()
    }

    override func tearDownWithError() throws {
        app = nil
    }

    // MARK: - Helpers

    private var loginView: XCUIElement {
        app.otherElements["LoginView"]
    }

    private var usernameField: XCUIElement {
        app.textFields["LoginView.usernameField"]
    }

    private var passwordFieldSecure: XCUIElement {
        app.secureTextFields["LoginView.passwordFieldSecure"]
    }

    private var passwordFieldVisible: XCUIElement {
        app.textFields["LoginView.passwordFieldVisible"]
    }

    private var passwordToggle: XCUIElement {
        app.buttons["LoginView.passwordToggle"]
    }

    private var signInButton: XCUIElement {
        app.buttons["LoginView.signInButton"]
    }

    private var needHelpButton: XCUIElement {
        app.buttons["LoginView.needHelpButton"]
    }

    // MARK: - Tests

    /// Sign-in button is disabled when the form is empty.
    func test_signInButton_isDisabledWhenFieldsAreEmpty() {
        XCTAssertTrue(loginView.waitForExistence(timeout: 5),
                      "LoginView should be the initial screen")
        XCTAssertFalse(signInButton.isEnabled,
                       "Sign-in button must be disabled when username and password are empty")
    }

    /// Sign-in button is still disabled when only the username is filled.
    func test_signInButton_isDisabledWithOnlyUsername() {
        XCTAssertTrue(loginView.waitForExistence(timeout: 5))
        usernameField.tap()
        usernameField.typeText("marc@acmebank.test")
        XCTAssertFalse(signInButton.isEnabled,
                       "Sign-in button must remain disabled when only username is provided")
    }

    /// Sign-in button is still disabled when only the password is filled.
    func test_signInButton_isDisabledWithOnlyPassword() {
        XCTAssertTrue(loginView.waitForExistence(timeout: 5))
        passwordFieldSecure.tap()
        passwordFieldSecure.typeText("p@ssw0rd!")
        XCTAssertFalse(signInButton.isEnabled,
                       "Sign-in button must remain disabled when only password is provided")
    }

    /// Sign-in button enables when both fields are non-empty.
    func test_signInButton_enablesWhenBothFieldsFilled() {
        XCTAssertTrue(loginView.waitForExistence(timeout: 5))
        usernameField.tap()
        usernameField.typeText("marc@acmebank.test")
        passwordFieldSecure.tap()
        passwordFieldSecure.typeText("p@ssw0rd!")
        XCTAssertTrue(signInButton.isEnabled,
                      "Sign-in button must be enabled when both fields are non-empty")
    }

    /// Happy path: fill credentials → tap Sign in → home tab bar appears.
    ///
    /// The app is launched with `--stub-auth-success` so the
    /// `OktaAuthService` is replaced with a stub that returns a
    /// `UserSession` immediately. The test asserts that the home tab bar
    /// becomes visible, confirming the coordinator correctly routed to
    /// the post-login screen.
    func test_happyPath_signIn_navigatesToHomeTabBar() {
        XCTAssertTrue(loginView.waitForExistence(timeout: 5))

        usernameField.tap()
        usernameField.typeText("marc@acmebank.test")
        passwordFieldSecure.tap()
        passwordFieldSecure.typeText("p@ssw0rd!")

        XCTAssertTrue(signInButton.isEnabled)
        signInButton.tap()

        // After a successful stub sign-in the app should navigate away
        // from LoginView and present the main tab bar.
        let tabBar = app.tabBars.firstMatch
        XCTAssertTrue(tabBar.waitForExistence(timeout: 5),
                      "Home tab bar should appear after a successful sign-in")
        XCTAssertFalse(loginView.exists,
                       "LoginView should no longer be visible after sign-in")
    }

    /// Password visibility toggle reveals the plain-text field.
    func test_passwordToggle_showsPlainTextField() {
        XCTAssertTrue(loginView.waitForExistence(timeout: 5))

        // The secure field should be present initially.
        XCTAssertTrue(passwordFieldSecure.exists,
                      "SecureField should be shown before the toggle is tapped")
        XCTAssertFalse(passwordFieldVisible.exists,
                       "Plain TextField should be hidden before the toggle is tapped")

        passwordToggle.tap()

        XCTAssertFalse(passwordFieldSecure.exists,
                       "SecureField should be hidden after toggle is tapped")
        XCTAssertTrue(passwordFieldVisible.exists,
                      "Plain TextField should be visible after toggle is tapped")
    }

    /// Password visibility toggle hides the plain-text field on second tap.
    func test_passwordToggle_hidesPlainTextField_onSecondTap() {
        XCTAssertTrue(loginView.waitForExistence(timeout: 5))

        passwordToggle.tap()
        XCTAssertTrue(passwordFieldVisible.exists,
                      "precondition: plain field visible after first tap")

        passwordToggle.tap()
        XCTAssertFalse(passwordFieldVisible.exists,
                       "Plain TextField should be hidden after a second toggle tap")
        XCTAssertTrue(passwordFieldSecure.exists,
                      "SecureField should be restored after a second toggle tap")
    }

    /// "Need help?" button presents the help sheet.
    func test_needHelpButton_presentsHelpSheet() {
        XCTAssertTrue(loginView.waitForExistence(timeout: 5))

        needHelpButton.tap()

        // The help sheet is a SafariSheetView — verify a web view
        // appears (Safari sheet uses a WKWebView inside a sheet).
        let webView = app.webViews.firstMatch
        XCTAssertTrue(webView.waitForExistence(timeout: 5),
                      "A web view should appear after tapping 'Need help?'")
    }
}
