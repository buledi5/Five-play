# FivePlay Localhost Android APK project

This Android Studio project packages the FivePlay wrapper in an APK. On launch,
the app starts a tiny HTTP server bound to `127.0.0.1:8000`, serves the bundled
`index.html` with COOP/COEP headers, and opens that localhost URL in Android WebView.

## Build an APK with GitHub Actions
1. Create a GitHub repository and upload all files in this project.
2. Open **Actions** and run **Build FivePlay APK** (or push to main/master).
3. Download the `FivePlay-Localhost-debug-apk` artifact.
4. Extract it and install `app-debug.apk` on your Android device.

## Build with Android Studio
Open this folder as a project and run the `app` configuration, or run:
`gradle assembleDebug`

## Important limitations
- The APK embeds the wrapper and localhost server; it does not download or bundle
  FivePlay's proprietary game files.
- The remote game must permit embedding and support the required cross-origin
  isolation headers itself. Local COOP/COEP headers cannot force a third-party
  iframe to become cross-origin isolated. If the game still reports the same
  warning, the fix must come from FivePlay's server or a supported official embed.
- Popup restrictions are enabled, but this is not a complete in-game ad blocker.
  Android Private DNS can provide additional filtering, but may affect game services.
- This project is source code; a compiled APK is produced by the GitHub Actions
  workflow. A debug APK is not a Play Store release.
