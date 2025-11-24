# Gaussian Splat Sample

The Guassian Splat Sample shows how to use the experiemental Splat Component to render Guassian Splats within a Spatial SDK application. The immersive 3D scenes can be navigated using the controllers. Resetting the view can be done with the "B" button on the right controller and re-centering the control panel can be done with the "A" button on the right controller.

![Gaussian Splat Sample](documentation/MPK.jpg)

## Highlighted features
The Gaussian Splat Sample highlights the following Meta Spatial SDK features:
* **Gaussian Splat Rendering**: This sample demonstrates how to use the experimental `SplatFeature` and `Splat` component to render photorealistic 3D scenes captured as Gaussian Splats (.ply or .spz files) within a Spatial SDK application.
* **Dynamic Asset Loading**: This sample shows how to load Splat assets at runtime from application assets (`apk://`). The Splat Feature also has support for network URLs, and local files.
* **Jetpack Compose Panels**: This sample implements an interactive UI control panel using Jetpack Compose with Meta's SpatialTheme, featuring large preview images and dynamic selection states.
* **Controller Input Handling**: This sample demonstrates how to detect and respond to controller button presses (A and B buttons) for recentering the view and repositioning UI panels relative to the user's head position.
