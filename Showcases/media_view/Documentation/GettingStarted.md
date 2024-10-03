## Getting Started with Media View: An Immersive Mixed Reality Media Viewer

This comprehensive guide walks you through setting up your development environment, building Media View, installing it on your Meta Quest device, and exploring its features.

### 1. System Setup and Prerequisites

Before diving into the exciting world of mixed reality media, ensure you have the following:

**A. Software Requirements**

- **Android Studio:**
    - Download and install the latest stable version of Android Studio from [https://developer.android.com/studio](https://developer.android.com/studio).
    - During installation, choose a "Custom" setup and ensure that the following components are selected:
        - **Android SDK:** Essential for building Android applications.
        - **Android SDK Platform-Tools:** Includes tools like `adb` for interacting with Android devices.
        - **Android Virtual Device (AVD):** (Optional) Allows you to create and run Android emulators for testing, but a physical Quest device is recommended for the best experience.
- **Java Development Kit (JDK) 17:**
    - Media View requires JDK 17. Check if it's installed by running `javac -version` in your terminal. If not, download and install it from [https://www.oracle.com/java/technologies/downloads/](https://www.oracle.com/java/technologies/downloads/).
    - Configure JDK 17 in Android Studio:
        1. Go to **File > Project Structure > SDK Location**.
        2. Set the "JDK location" to the directory where you installed JDK 17.
- **Meta Spatial SDK:**
    - The Media View project includes the Meta Spatial SDK. Ensure you are using the version specified in the documentation (version 0.2.0 at the time of writing). Newer versions may require code adjustments.
- **Git LFS (Large File Storage):**
    - You'll need to have [Git LFS](https://git-lfs.com/) installed on your system.

- **(Optional) SideQuest:**
    - While not strictly required, SideQuest greatly simplifies sideloading apps and managing files on your Quest:
       - Download and install SideQuest from [https://sidequestvr.com/](https://sidequestvr.com/). We recommend using the **Advanced Installer** option for more control.

**B. Hardware**

- **Meta Quest Device:** A Meta Quest device (Quest 3 or later is recommended for optimal compatibility and performance).
- **USB Cable:** A high-quality USB cable to connect your Quest to your computer for development, debugging, and APK installation.

**C. Quest Device Setup**

1. **Enable Developer Mode:**
   - This is essential for installing apps outside the official Oculus Store.
   - You can enable Developer Mode through the Meta Horizon mobile app (Device > Headset Settings > Developer mode). If you don't find the option, please follow the instructions at the [Meta Quest Developer Hub website](https://developer.oculus.com/documentation/native/android/mobile-device-setup/). You might need to create a developer account.

### 2. Project Setup

1. **Install Git LFS:**
   - Media View likely uses Git LFS (Large File Storage) to manage large media files. Install it to ensure you clone the repository correctly:
  
     ```bash
     git lfs install
     ```

2. **Clone the Media View Repository:**
   - Clone the repository from GitHub:
  
     ```bash
     git clone https://github.com/levin-riegner/kreos-aether
     ```

3. **Import into Android Studio:**
   - Open Android Studio.
   - Select **File > Open...** and navigate to the cloned Media View project directory.
   - Click **Open**.
   - Android Studio will automatically sync the project with Gradle, downloading dependencies. Resolve any Gradle sync errors before continuing.
  
### 3. Build the APK

1. **Select Build Variant:**
   - In Android Studio, switch the build variant from "debug" to "release" using the dropdown menu on the toolbar. This generates a release-ready APK optimized for performance.

2. **Build the APK:**
   - Navigate to **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

3. **Locate the APK:**
   - After a successful build, the APK file will be located in the `app/build/outputs/apk/release` directory within your project folder.

### 4. Installing the APK

**A. Using SideQuest (Recommended):**

1. **Connect Your Quest:** Plug your Quest device into your computer using the USB cable.
2. **Open SideQuest:** Launch the SideQuest application. It should automatically detect your connected Quest device.
3. **Install the APK:**
   - Click the "Install APK" button (usually a folder icon with a plus sign) in SideQuest.
   - Navigate to the `app/build/outputs/apk/release` directory and select the `app-release.apk` file (or your APK file name if different).
   - SideQuest will install the APK onto your Quest device.

**B. Using ADB:**

1. **Connect Your Quest:**  Connect your Quest using the USB cable.
2. **Open Terminal/Command Prompt:** Open a terminal (macOS/Linux) or command prompt (Windows).
3. **Navigate to APK Directory:** Use the `cd` command to navigate to the directory containing the APK:

   ```bash
   cd path/to/your/project/app/build/outputs/apk/release
   ```

4. **Install APK:** Run the following ADB command:

   ```bash
   adb install app-release.apk
   ```

### 5. Running Media View

1. **Put on Your Headset:** Wear your Meta Quest headset.
2. **Locate Media View:** Find Media View in your app library under "Unknown Sources" (if you sideloaded it) or in your main app library (if installed via the Oculus Store).
3. **Launch:**  Select the Media View icon to start the application.

### 6. Granting Permissions

- When you first launch Media View, it will request permission to access your device's storage. This permission is essential for Media View to read your media files.  Make sure to grant this permission.

### 7. Exploring the App

Now you're ready to dive into the immersive world of Media View!

- **Browse Media:** The gallery view presents your available media content.
- **Select and Play:** Choose a media item to experience it within the mixed reality environment.
- **Interact:** Use hand gestures and controller buttons to navigate, control playback, and adjust panel positions.
- **Upload:**  Use the Upload panel to add new media from your Google Drive account.

### 8. Learning More

Explore the full power and features of Media View:

- **[Media View Documentation](./README.md):** Dive deeper into the technical details, project structure, design principles, and more.
- **[Contributing Guide](./CONTRIBUTING.md):**  Learn how to contribute to the project's codebase, documentation, or testing efforts.

---

Congratulations! You are now ready to experience the future of media viewing with Media View!  Enjoy exploring the immersive possibilities of mixed reality.
