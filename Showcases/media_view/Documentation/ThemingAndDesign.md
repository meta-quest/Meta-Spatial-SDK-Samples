## Media View Theming and UI Design

Media View's visual design emphasizes a modern, clean, and immersive experience that aligns with the Meta brand and the nature of mixed reality applications. The theming and UI components are primarily built using Jetpack Compose, providing a declarative and flexible approach to UI development.

### Color Palette

The app utilizes a dark theme for a visually appealing and immersive experience, particularly within VR environments. The color palette is defined in the `AppColor` object and consists of:

- **Primary:** Meta Blue (`#1C65C1`) - Conveys a sense of trust and professionalism, aligning with the Meta brand.
- **Secondary:** Black (`#000000`) - Provides a neutral background that complements the primary color and emphasizes the content.
- **Tertiary:** White (`#DBE4EB`) - Used for text, icons, and other UI elements to ensure clear visibility against the dark background.

A subtle vertical gradient, defined as `BackgroundSweep` in the `AppColor` object, blending from a dark blue-gray (`#0d1521fc`) to a lighter blue (`#7eafedfc`), is applied to backgrounds to add depth and visual interest.

### Typography

The typography choices aim for clarity and readability, particularly important within the VR environment.

- **Headings:**  The `titleLarge` (H1) and `titleMedium` (H3) styles from Material 3 are used for headings, providing a clear visual hierarchy.
- **Body Text:** The `bodyMedium` style from Material 3 is used for most body text. The `Roboto` font family, the default for Android, ensures consistency with the overall system design.

### Components

Media View utilizes standard Material 3 components, adhering to established design patterns and user expectations:

- **Buttons:**  Buttons are styled with rounded corners to soften their appearance and provide a more approachable feel. The primary color (Meta Blue) is used for the background, with white text for contrast.
- **Panels:** Panels are the primary means of displaying content and interacting with the app within the immersive environment. They are rectangular surfaces positioned in the user's 3D space.  Key considerations for panel design:
    - **Size and Aspect Ratio:** Panel sizes are carefully calculated based on media content to ensure a comfortable viewing experience. The `calculateDimensionsInMeters()` function in the `shared/model` package handles this calculation.
    - **Positioning:**  Panels are positioned strategically in the user's space to be easily accessible and visible. The `PanelManager` and `PanelTransformations` classes handle panel placement and transformations.
    - **Interaction:**  Panels are designed for intuitive interaction using controllers or hand gestures.

### Layout

The UI layouts are designed to be straightforward and intuitive:

- **Media Gallery:** A grid layout (`LazyVerticalGrid` in Jetpack Compose) is used for the media gallery, allowing users to efficiently browse through media items. The grid adapts to different screen sizes and orientations, ensuring a consistent experience.
- **Panel Placement:**  The placement of panels in the immersive environment follows these principles:
    - **User Comfort:** Panels are positioned within a comfortable viewing distance and angle.
    - **Contextual Relevance:** Panels related to a specific object or interaction are placed near that object for easy access.
    - **Spatial Awareness:**  Panel placement considers the user's physical surroundings and avoids obstructing their view.

### Code Examples

**Theme Definition (Jetpack Compose):**

```kotlin
private val DarkColorScheme = darkColorScheme(
    primary = AppColor.MetaBlu,
    secondary = AppColor.Black,
    tertiary = AppColor.White,
)
```

**Button Styling (Jetpack Compose):**

```kotlin
Button(onClick = onClick,
       modifier = modifier,
       colors = ButtonDefaults.buttonColors(
           containerColor = AppColor.MetaBlu, // Meta Blue background
           contentColor = Color.White       // White text color
       )
) {
    // ... Button content
}
```

**Media Gallery Layout (Jetpack Compose):**

```kotlin
LazyVerticalGrid(
    modifier = modifier,
    columns = GridCells.Adaptive(minSize = 256.dp) // Adaptive grid for different screen sizes
) {
    // ... Media item rendering logic
}
```

**Panel Configuration (`PanelConfigOptions`):**

```kotlin
val config = PanelConfigOptions(
    width = 1.6f, // Panel width in meters
    height = 1.2f, // Panel height in meters
    layerConfig = QuadLayerConfig(), // Configuration for the panel's quad layer
    panelShader = "data/shaders/punch/punch", // Shader used for rendering the panel
    alphaMode = AlphaMode.HOLE_PUNCH,  // Alpha blending mode
    includeGlass = false, // Whether to include a glass effect
)
```

By combining a well-defined color palette, clear typography, standard components, and thoughtful layout principles, Media View creates a visually appealing and user-friendly experience within the mixed reality environment. The use of Jetpack Compose ensures that the UI is declarative, flexible, and easy to maintain.
