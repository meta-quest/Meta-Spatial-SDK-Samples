# Custom Components/Systems

Five custom components and systems were created for this application.

1. **Spinnable** – for the user to spin the globe
2. **GrabbableNoRotation** – for the user to move the globe and panel around their play space
3. **Tether** – for the panel to stay on a short leash near the globe, and always rotate to face the user
4. **Pinnable** – for the user to drop a pin on the globe during explore mode
5. **LandmarkSpawn** – to spawn and manage the landmark models
6. **Spin** – to slowly spin the clouds model around the globe

# Table of Contents

- [Custom Components/Systems](#custom-componentssystems)
- [Table of Contents](#table-of-contents)
- [Spinnable](#spinnable)
- [GrabbableNoRotation](#grabbablenorotation)
- [Tether](#tether)
- [Pinnable](#pinnable)
- [LandmarkSpawn](#landmarkspawn)
- [Spin](#spin)

# Spinnable

![Spinnable system](/Documentation/Media/animated/GlobeSpin.gif 'Spinnable system')

The `Spinnable` component and `SpinnableSystem` ECS system handle all of the globe rotation behavior when the user points their controller or hand a the globe, and grabs it to spin it around. This rotation behavior includes pitch rotation clamped at 45 degrees if the user pitches their controller while holding the globe, or applying yaw rotation if the user rotates their controller on the y axis while holding the globe.

Two behaviors which we believe make this spinning rotation behavior more engaging and intuitive to use are:

1. If the user points their controller at the globe, and presses the spin button to grab the globe for spinning, the globe will continue to spin while the button is held down and the controller is rotating – even if the user is no longer pointing at the globe. This gives the user greater flexibility to rotate the globe any way they want without having to repeatedly grab and rotate to look at a point on the opposite side of the globe from what they're facing.
2. If the user flings their wrist and releases the spin hold button at the same time, the globe will spin about its y-axis, smoothly slowing down until it stops. Note that this inertia behavior is applied to the yaw rotation, but not the pitch rotation.

The `SpinnableSystem` follows an architectural pattern you'll see in a number of the custom ECS implementations described on this page wherein a `Query` is used to get the corresponding SceneObjects for the entities with the `Spinnable` component, and input listeners are attached to those scene objects. When the user presses the designated button while pointing at the object, the triggered input handler then stores an instance of a data class which encapsulates all of the data needed for the system processing. Later in the `execute` function, all of the grabbed entities and their rotation behavior are processed. This data class for the `SpinnableSystem` grabbed entities looks like this:

```kotlin
private data class SpinningInfo(
    // the Entity which represents either the user's hand or held controller –
    // whichever "grabbed" the Spinnable
    val inputSource: Entity,

    // the "grabbed" Spinnable Entity
    val entity: Entity,

    // yaw and pitch offsets in radians of the vectors which represents the
    // direction from the "grabbed" Entity to the controller, and the
    // controller's forward vector
    val initialYawOffset: Float,
    val initialPitchOffset: Float,

    // the initial absolute rotation of the "grabbed" Entity. Used in
    // conjunction with the initial offsets to determine the new rotation
    val initialRotation: Quaternion,

    // cached value indicating whether or not the Entity has a
    // GrabbableNoRotation component also attached to it, so we know to disable
    // it until after the spin grab button is released
    val hasGrabbable: Boolean = false,

    // both used for calculating the rotation speed, and spin inertia for
    // gradually slowing down
    var lastYawOffsetDeg: Float = 0f,
    var yawInertiaDeg: Float = 0f
)
```

The `Spinnable` component itself also contains some properties which determine rotation behavior:

**size: Float** – used in the yaw rotation calulation to determine how much to rotate the grabbed spinnable object per degree of rotation of the user's controller. This is used in conjunction with the arc length of the controller rotation, with respect to the distance between the spinnable and the controller, to create a more natural rotation amount which is independent of the user's distance to the globe.

**drag: Float** – used to determine how quickly the spinning slows down to 0 if the user flicks their wrist and releases the hold button at the same time. A higher value means the globe stops sooner.

**Spinnable.Companion.MAX_PITCH_RAD: Float** – a static constant of the `Spinnable` component which determines the maximum amount of pitch rotation the user can apply to an entity.

The final rotation behavior and implementation involves a number of 3D mathematical calculations which won't be covered here. To get more explanation on this implementation, please see the commented function `SpinnableSystem.processSpinnables` after familiarizing yourself with the Spatial SDK's basics, and basics of Vector3 and Quaternion operations.

# GrabbableNoRotation

This system behaves nearly identically to the built-in Spatial SDK `Grabbable` Component, except that it doesn't alter the grabbed entity's rotation. This was built to allow the user to move the globe around their play space, but not interfere with the globe's spinning.

# Tether

![Tether system](/Documentation/Media/animated/Tether.gif 'Tether system')

The `Tether` component and `TetherSystem` ECS system were built to have the panels always float next to the globe in 3D space, even if the user grabs and moves the globe around their play space. In most situations, the built-in `TransformParent` component could be used to accomplish this, but in our case, rotating the globe through the `SpinnableSystem` would have also spun the panel entities with the `Tether` component around the globe – a behavior we didn't want.

In addition to anchoring the panel positions around the globe, the `TetherSystem` also handles continuously orienting the panels to face the user – even if the user grabs and moves the globe around their play space. This is accomplished through several steps:

1. Calculate the vector which represents the direction from the the Tether anchor entity (or, the entity which the `Tether` entity is tethered to) to the user's head.
2. Calculate the target position of the panel using that vector, a distance offset along that vector, and a rotation around the yaw of the tether anchor entity.
3. Calculate the target rotation, which is a quaternion representing a rotation around the world up vector, with the forward vector being the cross product of the world up vector and the vector representing the direction from the Tether anchor entity to the new target position.
4. Smoothly interpolate the panel Transform position and rotation to the calculated target position and target rotation.

This tethering behavior involves a number of 3D mathematical calculations which won't be covered here. To get more explanation on this implementation, please see the commented function `TetherSystem.processTethers` after familiarizing yourself with the Spatial SDK's basics, and basics of Vector3 and Quaternion operations.

# Pinnable

![Pinnable system](/Documentation/Media/animated/PinDrop.gif 'Pinnable system')

If the user selects a point on the globe during the Explore play mode, an entity with a pin mesh is dropped at that location. In addition to that visual cue, the `MainActivity` is notified of the geocoordinates upon which the pin was dropped, and some application services are triggered to fetch and display information about the equivalent location on our planet Earth. More information about those services can be found on the Google Maps API [page](./GoogleMapsAPI.md) and Llama3 [page](./Llama3.md).

The pinnable custom ECS implementation is composed of a `Pinnable` component and `PinnableSystem` system, and handles the behavior of the pin entity, as well as notifying the `MainActivity` of the pin drop location. This is accomplished through a similar implementation pattern found in other custom ECS described on this page, wherin a `Query` is used to find the entities and their scene objects, and attach an input listener which handles the select event.

# LandmarkSpawn

This system is structured a bit differently than the other custom ECS described on this page, and is an atypical usage of the Spatial SDK. This was purposefully done to showcase how you may implement a system which consumes a data object, and parses it to add `Entities` and `Componenents` to your scene after the initial app launch. One possible use case for this may be to fetch data from a remote server which describes entities which should be spawned in your scene. You would typically use the `AppSystemActivity.inflateIntoDataModel` function to populate a scene with your entities, and could fetch a `scene.xml` from a remote source to do so, but this implementation demonstrates how you may already have inflated your `scene.xml` into the data model, but want to dynamically add entities after the fact.

In our example, the local file at `res/xml/landmarks.xml` is loaded and parsed when the system is initialized. Each of the nodes in this file represents a landmark object that is spawned on the globe, and the node's child elements are the parameters which describe how to position and orient it properly. The `Entity.create` is then used to spawn the `Entity`, and attach the appropriately configured `Component` objects. Here is a sample of a landmark node:

```xml
<landmark
    name="Great Egyptian Pyramids"
    description="Three majestic pyramids in Giza, built around 2580 BC, served as ancient tombs for Pharaohs Khufu, Khafre, and Menkaure. They are engineering marvels of limestone and granite, showcasing the architectural genius of ancient Egypt."
    latitude="29.979167"
    longitude="31.134167"
    model="pyramid.glb"
    scale="1.0"
    yaw="45"
    zOffset="0.37" />
```

Note that a query during the `execute` function is still used to get all of the references to the SceneObjects which correspond to the landmark entities. This must be done after the system initializes and spawns the entities, because ECS system registration/initialization occurs in the `AppSystemActivity.onCreate` call, before the `AppSystemActivity.onSceneReady` execution.

Besides spawning the landmark entities, the `LandmarkSpawnSystem` has 3 other purposes:

1. Show the landmark entities if the user enters the Explore play mode; otherwise, hide them.
2. Scale the landmark entities up slightly when facing the user on the globe.
3. Notify the `MainActivity` that the user has clicked on a landmark, and should display the landmark's info on the panel.

# Spin

![Spin system](/Documentation/Media/animated/Clouds.gif 'Spin system')

The simplest of the custom ECS in this application, the `Spin` component and `SpinSystem` system rotate an entity's transform around a given axis, at a given speed. In this application, this code is used to slowly rotate the clouds model around the globe.

`Spin` – component which accepts two arguments: `speed: Float` and `axis: Vector3`.

`SpinSystem` – system which executes several steps in order to spin entities.

1. Using a `Query`, gathers entities in the scene which contain the `Transform` and `Spin` components.
2. Iterates through the gathered entities, calculating the new rotation for each entity in the form of a `Quaternion`, and applying the new rotation to each entity's `Transform` component.
