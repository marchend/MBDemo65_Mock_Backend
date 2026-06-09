import SwiftUI

// MARK: - AcmeBank brand colour palette
//
// Canonical location: `DesignSystem/Colors.swift` per bootstrap.md §10.
//
// All brand-colour tokens and the `Color(hex:)` String initialiser live here
// so every screen in the app imports from one place. The previous
// `Shared/Extensions/Color+Brand.swift` file (which used a `UInt32`-based
// private initialiser) has been deleted to eliminate the future symbol
// conflict described in the PR review.

extension Color {

    // MARK: Primary palette

    /// AcmeBank primary navy — `#1B2A4A`.
    ///
    /// Used for primary buttons, active checkbox fills, and brand
    /// headings throughout the app.
    static let acmeNavy = Color(hex: "#1B2A4A")

    /// App background — `#F5F7FA`.
    ///
    /// Screen-level background for all major views.
    static let acmeBackground = Color(hex: "#F5F7FA")

    /// Surface / card background — `#FFFFFF`.
    ///
    /// Used for card surfaces, modal sheets, and list-row backgrounds.
    static let acmeSurface = Color(hex: "#FFFFFF")

    // MARK: Text

    /// Primary text — `#1A1A2E`.
    static let acmeText = Color(hex: "#1A1A2E")

    /// Secondary / subtext — `#6B7280`.
    static let acmeSubtext = Color(hex: "#6B7280")

    // MARK: Semantic

    /// Positive / success green — `#16A34A`.
    ///
    /// Used for positive balances and success banners.
    static let acmeGreen = Color(hex: "#16A34A")

    /// Error / badge red — `#DC2626`.
    ///
    /// Used for error banners, negative balance labels, and notification
    /// badges.
    static let acmeBadgeRed = Color(hex: "#DC2626")
}

// MARK: - String hex initialiser

extension Color {
    /// Initialises a `Color` from a CSS-style RGB hex string.
    ///
    /// Accepted formats: `"#RRGGBB"` and `"RRGGBB"` (the `#` prefix is
    /// optional). The initialiser is intentionally `internal` (not
    /// `private`) so any file in the `AcmeBank` module can call
    /// `Color(hex: "#AABBCC")` without duplicating the bit-shift logic.
    ///
    /// - Parameter hex: A six-character hexadecimal colour string,
    ///   optionally prefixed with `#`.
    init(hex: String) {
        var cleaned = hex
        if cleaned.hasPrefix("#") { cleaned.removeFirst() }
        let value = UInt32(cleaned, radix: 16) ?? 0
        let r = Double((value >> 16) & 0xFF) / 255.0
        let g = Double((value >>  8) & 0xFF) / 255.0
        let b = Double( value        & 0xFF) / 255.0
        self.init(red: r, green: g, blue: b)
    }
}
