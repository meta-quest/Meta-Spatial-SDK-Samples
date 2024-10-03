# Media View Documentation Files

This directory contains detailed documentation for the Media View project.

## Overview

Media View is a Mixed Reality media viewer built for Meta Quest devices, leveraging the Meta Spatial SDK.

## Documentation

For a comprehensive understanding of Media View, please refer to the detailed documentation:

The following files provide in-depth information about different aspects of the Media View project:

- **[Getting Started](./GettingStarted.md):** A step-by-step guide on setting up your development environment, building the application, and installing it on your Meta Quest device.
- **[Project Structure and Code Design](./ProjectStructure.md):** An in-depth look into the project's architecture, emphasizing the Model-View-ViewModel (MVVM) pattern and the use of Dagger Hilt for dependency injection. This document explains the core modules, key components, and the design principles employed in the development process.
- **[Theming and UI Design](./ThemingAndDesign.md):**  Guidelines for the visual design of Media View, including details on the color palette, typography, component usage (buttons, panels), and layout principles for a clear and intuitive VR interface.
- **[Media Handling](./MediaHandling.md):** Explains how Media View interacts with the Android MediaStore for managing media files. Learn about querying, media type detection, the `StorageType` enum, and the process of saving media files to the device.
- **[Immersive Experience](./ImmersiveExperience.md):** Explore the core mechanisms behind creating the immersive mixed-reality environment. This document details the `PanelDelegate` interface, the `ImmersiveActivity`, the `PanelManager` class, `PanelConfigOptions` for panel configuration, and tools used for spatial debugging.
- **[Navigation and Screens](./NavigationAndScreens.md):**  Provides a comprehensive overview of the application's navigation flow. Each screen is described, and the logic governing transitions between them is explained. Includes a navigation flow diagram for better visualization.
- **[Tooling](./Tooling.md):**  A detailed description of the third-party libraries crucial for Media View's functionality, including the Meta Spatial SDK, Jetpack Compose, Dagger Hilt, ExoPlayer, Coil, Timber, and Glide. This document also explains the build scripts and other essential development tools.

## Contributing

We welcome and encourage contributions to both the documentation and the Media View codebase. To learn how you can contribute to the project, please refer to the [Contribution Guide](../CONTRIBUTING.md).
