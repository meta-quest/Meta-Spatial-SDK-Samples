# Meta Spatial SDK

This is a **Meta Quest VR/MR headset** app, not a standard Android phone/tablet app. It renders 3D content in the user's physical space with head tracking, hand tracking, and controller input.

## Architecture

The SDK uses an **Entity-Component-System (ECS)** architecture:

- **Entities** hold **Components** (data). **Systems** process them each frame via queries.
- **Custom components** are declared in XML schemas (`app/src/main/components/*.xml`) and auto-generate Kotlin classes at build time.
- **Scenes** can be authored visually in Meta Spatial Editor (`.glxf` files) or built entirely in code at runtime.
- **Panels** render 2D Android UI (Compose or XML layouts) onto surfaces positioned in 3D space.
- **Units are meters.** e.g. `Transform(Pose(Vector3(0f, 1.5f, -2f)))` = 1.5m up, 2m forward.

## Key Patterns

```kotlin
// Create entity with components
Entity.create(listOf(Transform(pose), Mesh(Uri.parse("mesh://box"))))

// Procedural meshes (no 3D model file needed)
// mesh://sphere, mesh://box, mesh://plane, mesh://quad, mesh://skybox

// Query entities in a System
val q = Query.where { has(Transform.id, MyComponent.id) }
for (entity in q.eval()) { /* per-frame logic */ }

// Register custom components and systems
componentManager.registerComponent<MyComp>(MyComp.Companion)
systemManager.registerSystem(MySystem())
```

## Docs

https://developers.meta.com/horizon/llmstxt/documentation/spatial-sdk/llms.txt/
