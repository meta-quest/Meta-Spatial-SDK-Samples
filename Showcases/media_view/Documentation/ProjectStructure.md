## Media View Project Structure and Code Design

The Media View project is structured using the Model-View-ViewModel (MVVM) architectural pattern, ensuring a clear separation of concerns between the UI, business logic, and data handling. Dagger Hilt, a dependency injection framework, streamlines the management of dependencies throughout the application. The codebase is written in Kotlin, leveraging modern language features for conciseness and readability.

### Core Modules

The project is organized into three core modules:

1. **`app`:**
   - **Purpose:**  Contains the main application logic and configuration.
   - **Key Components:**
     - `MediaViewApplication`: The main application class, responsible for initialization and dependency injection setup.
     - `ContextModule`: A Dagger Hilt module that provides application-level dependencies, including `ContentResolver`, `AssetManager`, and `SharedPreferences`.
     - `DispatcherModule`: A Dagger Hilt module that provides coroutine dispatchers for managing asynchronous operations (`DefaultDispatcher`, `IoDispatcher`, `MainDispatcher`).
     - `MediaViewDebugTree`: A custom Timber debug tree for logging.

2. **`data`:**
   - **Purpose:**  Handles all data-related operations, including interactions with the device gallery and user preferences.
   - **Sub-Modules:**
      
3. **`gallery`:**
      - **Purpose:**  Manages media retrieval and storage using the Android MediaStore.
         - **Key Components:**
           - `GalleryRepository`: The central repository for interacting with the device gallery.
           - `DeviceGalleryService`:  Encapsulates the logic for querying and interacting with the MediaStore.
           - `MediaStoreFileDto`: A data transfer object representing a media file from the MediaStore.
           - `MediaStoreQueryBuilder`:  A utility class for constructing MediaStore queries based on filters and sort options.
           - `MediaStoreDebugUtils`:  Provides debugging utilities for dumping MediaStore query results.

      - **`user`:**
         - **Purpose:** Manages user preferences, such as whether sample media has been saved.
         - **Key Components:**
            - `UserRepository`:  The central repository for accessing and modifying user preferences.
            - `UserPreferencesService`:  Handles the interaction with `SharedPreferences` for storing and retrieving user preferences.

4. **`presentation`:**
   - **Purpose:** Contains the UI components, ViewModels, and presentation logic. Also includes the `shared/model` package, which handles core logic related to media and panel dimensions and configuration.
   - **Sub-Modules:**
      - **`gallery`:**
         - **Purpose:**  Presents the media gallery, filtering options, and interactions with media items.
         - **Key Components:**
           - `GalleryActivity`:  The main activity for displaying the media gallery.
           - `GalleryViewModel`:  Provides data and handles user interactions related to the media gallery.
           - `GalleryView`:  The Compose composable for rendering the gallery UI.
           - `MediaItemView`:  The Compose composable for rendering an individual media item.

      - **`immersive`:**
         - **Purpose:**  Handles the immersive experience within the Meta Quest environment, including panel creation, positioning, and interactions.
         - **Key Components:**
           - `ImmersiveActivity`:  The main activity for the immersive environment, responsible for registering and managing panels. This activity collaborates with the `PanelManager` class to create and manage panels effectively.
           - `PanelDelegate`:  An interface defining methods for interacting with panels in the immersive environment.
           - `SpatialDebugComponent`:  A component used for debugging spatial interactions (e.g., positioning and orientation of objects).
           - `SpatialDebugSystem`:  A system that operates on entities with the `SpatialDebugComponent` to facilitate spatial debugging.

      - **`panel`:**
         - **Purpose:** Provides the `PanelDelegate` interface, which is used by other modules to interact with panels in the immersive environment.

      - **`permission`:**
         - **Purpose:** Manages permissions required for the application, such as storage access.
         - **Key Components:**
           - `PermissionActivity`:  The activity responsible for requesting and handling storage permissions.
           - `PermissionViewModel`:  Provides the logic for requesting permissions and handling the results.
           - `PermissionState`:  A sealed class representing the different states of the permission request process.

      - **`player`:**
         - **Purpose:**  Handles media playback and controls for different media types.
         - **Key Components:**
           - `PlayerActivity`:  The activity responsible for displaying and controlling media playback.
           - `PlayerViewModel`:  Provides data and handles user interactions related to media playback.
           - `PlayerState`:  A sealed class representing the different states of the media player.
           - `ImageView`, `VideoView`, `PanoramaImageView`:  Compose composables for rendering different media types.

      - **`shared`:**
         - **Purpose:** Contains shared UI components, theming, and utilities.
         - **Key Components:**
           - `Media ViewTheme`:  Defines the application's theme and color palette.
           - `Typography`:  Defines the typography styles used in the application.
           - `Dimens`:  Provides common dimension values used for spacing and sizing.
           - `UiState`:  A sealed class representing different UI states (loading, success, error).
           - `MenuButton`, `ErrorView`, `LoadingView`:  Reusable Compose composables.

      - **`upload`:**
         - **Purpose:**  Handles the uploading of media content from Google Drive.
         - **Key Components:**
           - `UploadActivity`:  The activity that hosts the WebView for the Google Drive picker.
           - `UploadViewModel`:  Provides the logic for handling uploads and managing upload results.
           - `UploadResult`:  A data class representing the outcome of an upload attempt.
           - `UploadResultListener`:  An interface for classes that want to be notified of upload results.
           - `UploadResultSubject`:  A subject that allows the `UploadViewModel` to broadcast upload results to listeners.

## Design Principles

The Media View project adheres to several key design principles:

- **Modularity:**  The project's modular structure promotes separation of concerns, making it easier to understand, maintain, and test individual components.
- **Clean Architecture:**  The codebase follows clean architecture principles, ensuring that business logic is isolated from framework details, improving testability and maintainability.
- **Testability:** Components are designed with testability in mind, featuring clear interfaces and dependencies, facilitating the creation of unit and integration tests.
