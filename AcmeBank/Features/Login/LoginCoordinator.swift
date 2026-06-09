import SwiftUI

/// Coordinator for the Login feature.
///
/// Owns the `LoginViewModel` and drives the `NavigationStack` for the
/// login flow. When `LoginViewModel.signedInSession` becomes non-nil the
/// coordinator calls its `onSignedIn` closure so the app-level
/// composition root (`AcmeBankApp`) can swap the root view to the
/// authenticated home screen.
///
/// Marked `@MainActor` so all published-state mutations are guaranteed
/// to run on the main thread, consistent with the `LoginViewModel`
/// isolation.
@MainActor
final class LoginCoordinator: ObservableObject {

    // MARK: - Dependencies

    /// The ViewModel whose `signedInSession` this coordinator observes.
    let viewModel: LoginViewModel

    // MARK: - Callbacks

    /// Called when sign-in succeeds. Receives the authenticated
    /// `UserSession` so the composition root can route to the home
    /// screen without the coordinator knowing about NavigationPath or
    /// the home screen's type.
    var onSignedIn: ((UserSession) -> Void)?

    // MARK: - Init

    /// - Parameter authService: injected auth backend; forwarded to
    ///   `LoginViewModel`. The coordinator owns its ViewModel.
    init(authService: AuthService) {
        self.viewModel = LoginViewModel(authService: authService)
        observeSession()
    }

    // MARK: - Session observation

    /// Watch `viewModel.signedInSession` and fire `onSignedIn` the
    /// moment it becomes non-nil. Uses `@MainActor`-isolated `Task`
    /// suspension so the observation doesn't outlive the coordinator.
    private func observeSession() {
        Task { [weak self] in
            guard let self else { return }
            for await session in self.viewModel.$signedInSession.values {
                guard let session else { continue }
                self.onSignedIn?(session)
            }
        }
    }

    // MARK: - Root view

    /// The root view for the login flow, ready to embed in a
    /// `NavigationStack` or present as the app's initial scene.
    @ViewBuilder
    var rootView: some View {
        LoginView(viewModel: viewModel)
    }
}
