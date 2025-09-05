# Panels and interaction with spatial objects

Focus features twelve main panels, including the Home Panel, the Toolbar, and its sub-panels.
But users are also creating panels when they generate Sticky Notes, Timers, WebViews or Spatial Tasks.

![Panels](./Resources/panels.jpg)

## Creating a panel depending on the type

There are several ways to create Panels in Spatial SDK. Your decision will probably depend on the panel functionality. You´d better check Spatial SDK *Runtime Limitations* documentation to know how many panels of a specific type you can create.

The main thing for an Android Developer to understand is that spatial panels are basically views in the space. In order to build one, you will need to create an entity with a Panel component and link it to your view to be able to see it in space.

First of all, you will need to register the panels in your scene by overriding the function registerPanels() in your immersive activity:
```kotlin
// ImmersiveActivity.kt
override fun registerPanels(): List<PanelRegistration> {
    return PanelManager.instance.registerFocusPanels()
}

// PanelManager.kt
fun registerFocusPanels(): List<PanelRegistration>  {
    return listOf(
        panelRegistration(PanelRegistrationIds.HomePanel, 0.58f, 0.41f, true) {},
        panelRegistration(PanelRegistrationIds.Toolbar, 0.65f, 0.065f) { ToolbarPanel() },
        panelRegistration(PanelRegistrationIds.TasksPanel, 0.275f, 0.5f) { TasksPanel() },
        panelRegistration(PanelRegistrationIds.AIPanel, 0.3f, 0.5f) { AIPanel() },
        panelRegistration(PanelRegistrationIds.StickySubPanel, 0.26f, 0.042f) { StickySubPanel() },
        panelRegistration(PanelRegistrationIds.LabelSubPanel, 0.46f, 0.042f) { LabelSubPanel() },
        panelRegistration(PanelRegistrationIds.ArrowSubPanel, 0.24f, 0.042f) { ArrowSubPanel() },
        panelRegistration(PanelRegistrationIds.BoardSubPanel, 0.18f, 0.042f) { BoardSubPanel() },
        panelRegistration(PanelRegistrationIds.ShapesSubPanel, 0.25f, 0.042f) { ShapeSubPanel() },
        panelRegistration(PanelRegistrationIds.StickerSubPanel, 0.25f, 0.042f) { StickerSubPanel() },
        panelRegistration(PanelRegistrationIds.TimerSubPanel, 0.35f, 0.042f) { TimerSubPanel() },
    )
}
```

Register your panels by setting the appropriate configuration, including size and other properties:
```kotlin
// PanelManager.kt
fun panelRegistration(
    registrationId: Int,
    widthInMeters: Float,
    heightInMeters: Float,
    content: @Composable () -> Unit,
): PanelRegistration {
    return PanelRegistration(registrationId) { _ ->
        config {
            width = widthInMeters
            height = heightInMeters
            layoutWidthInDp = FOCUS_DP * width
            layerConfig = LayerConfig()
            enableTransparent = true
            includeGlass = false
            themeResourceId = R.style.Theme_Focus_Transparent
        }

        composePanel { setContent { content() } }
    }
}
```

You'll have at least one activity in your project. If you want to convert it directly into a panel in space, you can check our Home Panel, that is basically our main 2D activity.
In the panel registration, you will have to indicate the activity you want to convert in the attribute *activityClass*.
```kotlin
fun panelRegistration(
    // ...
): PanelRegistration {
    return PanelRegistration(registrationId) { _ ->
        // ...
        if (homePanel) {
            activityClass = MainActivity::class.java
        } else {
            composePanel { setContent { content() } }
        }
    }
}
```

After registering your panels, you need to create entities with panel components and link them with the panels. You must enter the same id you set in the panelRegistration phase.
You can also add a Grabbable component to your entity if you want this spatial panel to be grabbable by the user.
```kotlin
fun createToolbarPanel() {
    toolbarPanel =
        Entity.createPanelEntity(
            PanelRegistrationIds.Toolbar,
            Transform(Pose(Vector3(0f))),
            Grabbable(true, GrabbableType.FACE),
            Visible(false)
        )
}
```

In case you want to create a panel dynamically, you can also register it just after creating the panel entity.
Remember to set the same id in the registration and creation of the entity.
```kotlin
// StickyNote.kt

var id = getDisposableID()

// Register the panel
immA?.registerPanel(
    PanelManager.instance.panelRegistration(id, 0.14f, 0.14f) {
        // Composable Sticky Note Panel
        StickyNotePanel(
            uuid = uuid,
            message = message,
            color = color
        )
    }
)

// Create a grabbable entity with a Panel
val sticky: Entity = Entity.createPanelEntity(
    id, Transform(pose), 
    Grabbable(true, GrabbableType.PIVOT_Y)
)
```

You can also create a panel from a .xml file. In that case you will need to indicate the layout resource you want to use.
```kotlin
var id = getDisposableID()

// Register the panel
immA?.registerPanel(
    PanelRegistration(id) {
        layoutResourceId = R.layout.sticky_layout
        config {
            themeResourceId = R.style.Theme_Focus_Transparent
            width = 0.14f
            height = 0.14f
        }
    }
)

// Create a grabbable entity with a Panel
```

## Access panels and give them functionality

If you are using Jetpack Compose to build your panels, you can give functionality to a button directly in the Composable function of the panel.
```kotlin
//ToolbarSubpanelButton.kt
@Composable
fun ToolbarSubpanelButton(
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
    ) {
        // ...
    }
}
```

To update a panel after it’s created, store its dynamic values in a mutableStateOf variable using remember. 
When you change these state variables, Jetpack Compose automatically redraws the panel with the new values.
```kotlin
@Composable
fun MyPanel() {
    var text by remember { mutableStateOf("Initial value") }

    Button(onClick = { text = "Updated value" }) {
        Text(text)
    }
}
```

## Panels transparency and spatial text

To use spatial text effectively, create a panel with a transparent background by designing a new theme style and setting the *windowBackground* property to transparent.
```xml
<style name="Theme.Focus.Transparent" parent="Base.Theme.Focus">
…
  <item name="android:windowBackground">@android:color/transparent</item>
…
 </style>
```

Then you have to apply the theme to the panel configuration. You will probably want to set the *includeGlass* attribute to false in the PanelConfigOptions if you don't want the panel to be seen.
```kotlin
fun panelRegistration(
    // ...
): PanelRegistration {
    return PanelRegistration(registrationId) { _ ->
        config {
            // ...
            themeResourceId = R.style.Theme_Focus_Transparent
        }

        // ...
    }
}
```

![Transparency](./Resources/transparency.jpg)

If you want to attach a spatial text to an object, you can check our explanation of Composed Objects in [**Creating spatial objects: object hierarchy**](../Documentation/ObjectHierarchy.md).
