# PFDB - Personal Finance Dashboard

PFDB is a premium, native Android application designed to track household net worth, asset allocation, and wealth trends. Built with modern Android technologies, it offers a "Premium Dark Mode" experience identical to a high-end web dashboard.

## 📱 Key Features

- **Household Management**: Add and manage multiple family members to track individual and aggregate net worth.
- **Comprehensive Portfolio**: Track Assets (Bank Accounts, Stocks, Mutual Funds, Movable/Immovable property) and Liabilities with dedicated sections.
- **Dynamic Dashboard**:
    - **Total Net Worth**: Real-time calculation of Assets - Liabilities.
    - **Family Overview**: Horizontal scrolling view of each member's financial standing.
    - **Asset Allocation**: Toggle between a visual Donut Chart and a detailed list view with percentage breakdowns.
- **Wealth Timeline**: Visualize your financial growth over the last 6 months with a smooth trend graph.
- **Secure Data Management**:
    - **Base64 Backups**: Export your data to a secure, encoded format for portability.
    - **Restore**: Easily import backups from previous devices or cloud storage.
    - **Safe Reset**: Protect your data with mandatory confirmation prompts for deletion.

## 🛠 Tech Stack

- **UI**: Jetpack Compose (Modern native UI)
- **Database**: Room Database (Local persistent storage)
- **State Management**: Kotlin Coroutines & Flow
- **Styling**: Custom Material3 "Premium Dark Mode" with Glassmorphism effects.

## 🚀 Getting Started

### Download the App
You can download and install the application directly from this repository:
- [Download PFDB APK](./app-debug.apk)

### Installation Note
Since this APK is not from the Play Store, you may need to enable "Install from unknown sources" in your Android settings to install it.

### Build from Source
1. Clone the repository.
2. Open the project in Android Studio (Ladybug or newer).
3. Perform a Gradle Sync.
4. Build and Run on your device or emulator.

## 🔒 Privacy & Security
PFDB is a local-first application. All your financial data is stored securely on your device's local database. No data is sent to external servers unless you manually perform a "Backup" to share the encoded file.

---
*Developed with ❤️ as a native Android experience.*
