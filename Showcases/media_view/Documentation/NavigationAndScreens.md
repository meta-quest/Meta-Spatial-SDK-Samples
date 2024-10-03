## Media View Navigation and Screens

Media View's navigation flow is designed to be intuitive and user-friendly, guiding users through different sections of the app within the mixed reality environment.  This document outlines the key screens, their purposes, and the navigation logic that connects them.

### Screen Flow Diagram

```kotlin
Start
│
▼
Permission Request Screen
│
▼
Immersive Environment
│
│
│    Download Panel ────► (Download Success)
│           ▲                    │
│           │                    ▼
└────► Gallery Panel ◄─ (Media Refresh)
            │
      (Media Selected)
            │
            ▼
      Media Player Panel
            ▲
            │
            ▼
      Immersive Menu
      (Panel Maximized)
```

### Screen Descriptions

1. **Permission Request Screen (`PermissionActivity`):**
   - **Purpose:**  The initial screen that requests the necessary permissions from the user, primarily storage access, to enable the app's functionality.
   - **UI:**  A simple layout with explanatory text informing the user about the required permissions and a button to initiate the permission request.

2. **Immersive Environment (`ImmersiveActivity`):**
   - **Purpose:** The core environment for the mixed reality experience, responsible for rendering the 3D scene, managing panels, and handling user interactions. The `ImmersiveActivity` works in conjunction with the `PanelManager` to create and manage panels.
   - **UI:** The immersive environment is primarily a 3D space rendered using the Meta Spatial SDK. Panels are overlaid on this 3D space, providing interactive surfaces for the user.

3. **Gallery Panel (`GalleryActivity`):**
   - **Purpose:** Displays the user's media library, allowing browsing and selection of media items.
   - **UI:**  Uses a grid layout (`LazyVerticalGrid` in Jetpack Compose) to efficiently present media items. Offers filtering and sorting options to categorize media by type (images, videos, 360 content, etc.).

4. **Media Player Panel (`PlayerActivity`):**
   - **Purpose:**  Displays and plays the selected media item. Supports playback of various media types, including images, videos, and panoramas.
   - **UI:** The player panel adapts to the media type. It uses `ImageView` for images, `VideoView` (which uses ExoPlayer for playback) for videos, and `PanoramaImageView` (which uses Glide for efficient image loading) for panoramas. Provides basic playback controls (play/pause, volume, etc.).

5. **Upload Panel (`UploadActivity`):**
   - **Purpose:** Allows users to download media content from their Google Drive account.
   - **UI:** Embeds a `WebView` to interact with the Google Drive API. Provides a file picker interface for selecting media to download.

6. **Immersive Menu (`ImmersiveMenuActivity`):**
   - **Purpose:** Provides options for controlling a maximized media panel (e.g., minimizing or closing) while in a fully immersive view.
   - **UI:**  A simple menu with buttons for relevant actions, displayed within the immersive environment.

### Navigation Logic

Navigation between screens is primarily event-driven, often triggered by user actions:

- **Permission Request to Immersive Environment:** Once the user grants storage permission, the app automatically transitions from the `PermissionActivity` to the `ImmersiveActivity`, launching the immersive environment.
- **Opening/Closing Panels:**
   - **Gallery Panel:**  The gallery panel is typically open by default within the immersive environment.
   - **Media Player Panel:**  Selecting a media item from the gallery triggers the opening of the media player panel. Closing the player panel returns the user to the gallery.
   - **Upload Panel:** Users can open the upload panel from a button in the gallery. Closing the upload panel returns the user to the gallery.
- **Maximizing/Minimizing Media:**
   - Users can maximize a media player panel to enter a more immersive viewing experience.  This typically hides other panels and disables passthrough.
   - The immersive menu provides options for minimizing the panel (returning to the previous view) or closing it completely.
- **Upload Success:**  After a successful download, the upload panel closes, and the gallery panel refreshes to display the newly downloaded media.

### Code Examples

**Opening the Media Player Panel (`GalleryViewModel`):**

```kotlin
fun onMediaSelected(mediaModel: MediaModel) {
    Timber.i("Opening media: $mediaModel")
    panelDelegate.openMediaPanel(mediaModel)
}
```

**Maximizing a Media Panel (`PlayerViewModel`):**

```kotlin
fun onMaximize() {
    panelDelegate.maximizeMedia(mediaModel)
}
```

**Closing the Upload Panel (`UploadViewModel`):**

```kotlin
fun onDismiss() {
    panelDelegate.closeUploadPanel()
}
```

Media View's navigation flow is designed to be logical, context-aware, and unobtrusive. It prioritizes a seamless and immersive user experience, allowing users to easily explore their media, interact with content, and download new media without disrupting the flow of interaction within the mixed reality environment.
