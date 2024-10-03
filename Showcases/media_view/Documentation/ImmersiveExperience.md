## Media View Immersive Experience

Media View's immersive experience leverages the Meta Spatial SDK to create a dynamic and interactive environment within the Meta Quest headset. This document details the mechanisms behind panel management, panel configuration, and spatial debugging, highlighting how Media View blends the virtual and real worlds.

### Panel Management

Panels are the primary means of displaying content and facilitating user interaction in Media View's immersive environment. They are essentially virtual surfaces that exist in the user's 3D space, capable of displaying images, videos, UI elements, and more.

- **`PanelDelegate` Interface:**
   - The `PanelDelegate` interface defines a set of methods for creating, closing, maximizing, minimizing, and otherwise interacting with panels. This interface serves as a contract between the `ImmersiveActivity`, which manages the immersive environment, and other components that need to interact with panels.
   - Key methods include:
     - `openMediaPanel(mediaModel: MediaModel)`: Creates and displays a panel for viewing a specific media item.
     - `closeMediaPanel(mediaModel: MediaModel)`: Closes the panel associated with a given media item.
     - `maximizeMedia(mediaModel: MediaModel)`:  Maximizes the panel for a given media item, potentially entering an immersive mode where the panel takes up most of the user's view.
     - `minimizeMedia(close: Boolean)`: Minimizes a maximized panel, optionally closing it completely.
     - `openUploadPanel()`: Creates and displays a panel for uploading media from Google Drive.
     - `closeUploadPanel()`:  Closes the upload panel.
     - `closeAllMedia()`: Closes all open media panels.

- **`ImmersiveActivity` Implementation:**
   - The `ImmersiveActivity` implements the `PanelDelegate` interface, providing the concrete logic for panel management. It uses the Meta Spatial SDK to create, position, and manipulate panels within the 3D environment.
   - The `ImmersiveActivity` works closely with the `PanelManager` class to handle the specifics of panel creation, registration, and configuration.

### Panel Configuration (`PanelConfigOptions`)

The `PanelConfigOptions` class provides a flexible way to customize the appearance and behavior of panels. Key configuration options include:

- **Dimensions (`width`, `height`):** Specify the physical dimensions of the panel in meters. This determines the size of the panel as perceived by the user within their 3D space.
- **`layerConfig`:** Controls the rendering of the panel's quad layer, allowing for adjustments to transparency and other properties.
- **`panelShader`:**  Determines the shader used to render the panel, allowing for visual effects and customizations.
- **`alphaMode`:**  Sets the alpha blending mode for the panel, influencing how it interacts with other objects in the scene.
- **`includeGlass`:**  Adds a glass-like effect to the panel, making it appear more physically present.
- **`sceneMeshCreator`:** A lambda function that allows you to create custom scene meshes for the panel. This provides maximum flexibility in defining the panel's shape and geometry.

### Spatial Debugging

Media View includes components for spatial debugging, aiding in the development and testing of interactions within the 3D environment.

- **`SpatialDebugComponent`:**
   - This component can be attached to entities to provide visual debugging information about their position, rotation, and scale. 
   - It offers properties to control how the entity is positioned and oriented relative to the user's head, including axis of rotation, rotation speed, follow speed, and offsets.

- **`SpatialDebugSystem`:**
   - This system operates on entities that have the `SpatialDebugComponent` attached.
   - It calculates and applies transformations to these entities, allowing developers to visualize how objects move and interact in the 3D space.
   - It also includes smoothing and interpolation logic to make the debug visualizations more fluid.

### Code Examples

**`PanelDelegate` Interface:**

```kotlin
interface PanelDelegate {
    fun openMediaPanel(mediaModel: MediaModel)
    // ... Other panel management methods
}
```

**Panel Creation (Using `PanelManager`):**

The `PanelManager` class is responsible for creating, configuring, and managing the lifecycle of panels within the `ImmersiveActivity`.

Here's an example of how the `PanelManager` is used to create a gallery panel:

```kotlin
// Inside ImmersiveActivity (assuming 'panelManager' is an instance of PanelManager)
override fun onSceneReady() {
    super.onSceneReady()
    // ... other setup code

    // Create the gallery panel
    val galleryPanel = panelManager.createGalleryPanel()
}
```

The `createGalleryPanel()` method of the `PanelManager` handles the details of panel creation, including configuration using `PanelConfigOptions` and registering the panel with the Meta Spatial SDK.

**Custom Panel Mesh (`PanelConfigOptions`):**

```kotlin
sceneMeshCreator = { texture: SceneTexture ->
    val unlitMaterial = SceneMaterial(texture, AlphaMode.OPAQUE, SceneMaterial.UNLIT_SHADER)
    SceneMesh.cylinderSurface(5.0f, 5.0f, 0.7f, unlitMaterial) // Create a cylindrical panel
}
```

**Spatial Debugging (`SpatialDebugComponent`):**

```kotlin
class SpatialDebugComponent(
    axis: LookAtAxis = LookAtAxis.Y,
    // ... Other properties
) : ComponentBase() {
    // ... Attribute definitions for controlling debug behavior
}
```

Through these mechanisms, Media View creates a compelling and interactive mixed reality experience. Panel management provides a structured way to display and interact with content, while panel configuration options enable developers to fine-tune the appearance and behavior of panels. Spatial debugging tools aid in the development and testing of the immersive environment, ensuring a polished and engaging user experience.