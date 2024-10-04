# Code Structure

# Table of Contents

- [Code Structure](#code-structure)
- [Table of Contents](#table-of-contents)
- [Activities](#activities)
- [ECS](#ecs)
- [Enums](#enums)
- [Models](#models)
- [Services](#services)
- [UI](#ui)
- [Utils](#utils)

The Kotlin code in this project has been organized into 5 main folders within the `:app` module main source code folder:

```
/com.meta.pixelandtexel.geovoyage
    /activities
    /ecs
    /enums
    /models
    /services
    /ui
    /utils
    /viewmodels
```

- The `/activities` folder includes the main activity which launches on app start, as well as all panel activities.
- The `/ecs` folder includes the custom ECS components and systems used for complex spatial manipulation of 3D objects. For more information on these custom components and systems, see [this page](./CustomECS.md).
- The `/enums` folder includes any enum classes used throughout the application code.
- The `/models` folder includes any data classes used throughout the application code.
- The `/services` folder includes the integration of application services like Llama 3. For more information regarding these services and their usage, see the corresponding pages for [Llama 3](./Llama3.md) and [Wit.ai](./WitAi.md) integrations.
- The `/ui` folder contains all of the Jetpack Compose UI component definitions.
- The `/utils` folder contains some utility functions and logic accessed by various places throughout the application code.
- The `/viewmodels` folder contains all view models used throughout the Jetpack Compose UI for the main panel.

# Activities

```
/com.meta.pixelandtexel.geovoyage
    /activities
        AskEarthActivity.kt
        DailyQuizActivity.kt
        ExploreActivity.kt
        MainActivity.kt
        MainNavigatorActivity.kt
        TodayInHistoryActivity.kt
        TriggerAskEarthActivity.kt
```

**MainActivity** is the primary activity which runs when the app launches on-device, as determined by the activity intent filters defined in the `:app` module `AndroidManifest.xml`. This activity inheirits from `AppSystemActivity`, and includes all of the code to register features, panel object generators, custom systems and components, as well as setting up our 3D scene. This activity also follows the singleton pattern, and is called by several places in the codebase to trigger different play modes and communicate between the UI and 3D scene objects.

All other activities correspond to a different panel floating in 3D space. These activities all inheirit from `ComponentActivity`, and contain Jetpack Compose UI.

# ECS

```
/com.meta.pixelandtexel.geovoyage
    /ecs
        /grabbablenorotation
            GrabbableNoRotation.kt
            GrabbableNoRotationSystem.kt

        /landmarkspawn
            LandmarkSpawnSystem.kt

        /pinnable
            Pinnable.kt
            PinnableSystem.kt

        /spin
            Spin.kt
            SpinSystem.kt

        /spinnable
            Spinnable.kt
            SpinnableSystem.kt

        /tether
            Tether.kt
            TetherSystem.kt
```

Within each of these subfolders, a custom component and/or custom system is defined. The naming scheme follows `<component name>.kt` for components, and `<component name>System.kt` for their corresponding systems. These components and systems are registred with the `componentManager` and `systemManager` objects in `MainActivity` to be used by and executed on entities in the 3D scene.

Within each of the component classes, you will also see a corresponding `<component name>Loader` class. This class is used by Meta's Spatial Framework to parse and create instances of your custom components that you've assigned to entities in your scene.xml.

For more information on these custom components and systems, see [this page](./CustomECS.md).

# Enums

```
/com.meta.pixelandtexel.geovoyage
    /enums
        LlamaServerType.kt
        PlayMode.kt
```

**LlamaServerType** enum class indicating which Llama 3 server type is currently being used by the `QueryLlamaService.kt`.

**PlayMode** enum class used for discerning which app play mode is currently active, and cleanly switching to a different mode.

# Models

```
/com.meta.pixelandtexel.geovoyage
    /models
        GeoCoordinates.kt
        Landmark.kt
        PanoMetadata.kt
        TriviaQuestion.kt
```

**GeoCoordinates** contains a data class representing a point on a globe with latitude and longitude number values, and some helper conversion and serialization functions. This class is used in several places throughout the application code, including to determine where the user points and "clicks" on the globe, and where to spawn landmarks on the globe.

**Landmark** contains the data class which represents a landmark object that is spawned on the globe and visible during the Explore play mode. This includes placement and orientation data, as well as info about the real-world landmark that is displayed on the Explore info panel.

**PanoMetadata** data class containing all of the properties for using and displaying panoramic images fetched from the Google Map Tiles API in the MR to VR feature.

**TriviaQuestion** data class containing all of the information for each question asked during the Daily Quiz play mode. The trivia questions are parsed from the `xml/trivia_questions.xml`, and deserialized into a collection of this model type.

# Services

```
/com.meta.pixelandtexel.geovoyage
    /services
        SettingsService.kt

        /googlemaps
            GoogleMapsService.kt
            GoogleTilesService.kt
            IGeocodeServiceHandler.kt
            IPanoramaServiceHandler.kt

            /enums
                GeocodeStatus.kt

            /models
                GeocodeAddressComponent.kt
                GeocodePlusCode.kt
                GeocodeResponse.kt
                GeocodeResult.kt
                GoogleLocation.kt
                SessionRequest.kt
                SessionResponse.kt

        /llama
            IQueryLlamaServiceHandler.kt
            QueryLlamaService.kt

            /models
                BedrockRequest.kt
                BedrockResponse.kt
                OllamaRequest.kt
                OllamaResponse.kt

        /witai
            IWitAiServiceHandler.kt
            WitAiFlowService.kt
            WitAiService.kt

            /enums
                WitAiEntityType.kt
                WitAiIntentType.kt
                WitAiResponseType.kt
                WitAiStartResult.kt
                WitAiTraitType.kt

            /models
                WitAiEntity.kt
                WitAiIntent.kt
                WitAiResponse.kt
                WitAiStreamingResponse.kt
                WitAiTrait.kt
```

**SettingsService** is a service that consolidates the usage of the app context [SharedPreferences](https://developer.android.com/reference/kotlin/android/content/SharedPreferences) and provides helper functions to access specific key/value pairs in the preferences. These preferences include:

- `llama_server_type`: Integer value indicating the cloud service for accessing the Llama 3 model (0 for ollama, 1 for AWS Bedrock)
- `ollama_url`: String value specifying the URL of a server running ollama
- `wit_ai_filtering_enabled`: Boolean value controlling user query filtering before sending to Llama 3
- `silence_detection_enabled`: Boolean value for automatic detection of user speech cessation
- `landmarks_enabled`: Boolean value controlling landmark display in Explore play mode
- `last_daily_quiz`: Days offset from the user's local current date and 1/1/2024, used in Daily Quiz play mode for consistent question selection across users

The `/llama` and `/witai` subdirectories contain logic for speech recognition and query answering. For detailed information on these services, refer to the [Llama 3](./Llama3.md) and [Wit.ai](./WitAi.md) integration pages.

The `/googlemaps` subdirectory houses logic for reverse-geocoding coordinates upon pin drops and fetching Street View imagery for VR mode. For implementation details, see the [Google Maps API](./GoogleMapsAPI.md) page.

# UI

```
/com.meta.pixelandtexel.geovoyage
    /ui
        /askearth
            /AskEarthScreen.kt
            /ErrorScreen.kt
            /ListeningScreen.kt
            /RejectedScreen.kt
            /SuccessScreen.kt
            /ThinkingScreen.kt
        /components
            /buttons
                /CircleButton.kt
                /NavButton.kt
                /PrimaryButton.kt
                /SecondaryButton.kt
            /panel
                /PrimaryPanel.kt
                /SecondaryPanel.kt
            /ScrollContainerCustomScrollBar.kt
            /TitleBar.kt
            /ToggleChip.kt
        /dailyquiz
            /DailyQuizScreen.kt
            /QuestionScreen.kt
            /ResultsScreen.kt
            /StartScreen.kt
        /explore
            /ExploreScreen.kt
        /intro
            /IntroScreen.kt
        /mainnavigator
            /GeoVoyageDestination.kt
            /PanelNavContainer.kt
        /settings
            /SettingsScreen.kt
        /theme
            /Colors.kt
            /Shapes.kt
            /Theme.kt
            /Type.kt
        /todayinhistory
            /TodayInHIstoryScreen.kt
```

The `/ui` folder contains all of the Jetpack Compose UI component definitions for the application. Here's a brief overview of the subfolders and their contents:

- `/askearth`: Contains screens related to the Ask Earth feature, including the main screen, error handling, listening state, and response screens.
- `/components`: Houses reusable UI components such as buttons, panels, and custom scrollbars.
- `/dailyquiz`: Includes screens for the Daily Quiz feature, from start to results.
- `/explore`: Contains the main Explore screen.
- `/intro`: Houses the introductory screen for new users.
- `/mainnavigator`: Includes navigation-related components and definitions.
- `/settings`: Contains the Settings screen.
- `/theme`: Defines the app's visual theme, including colors, shapes, and typography.
- `/todayinhistory`: Houses the Today in History feature screen.

Each of these files is responsible for defining the UI structure and behavior for its respective feature or component, utilizing Jetpack Compose for a declarative and efficient UI implementation.

# Utils

```
/com.meta.pixelandtexel.geovoyage
    /utils
        AudioUtils.kt
        DisplayUtils.kt
        Extensions.kt
        MathUtils.kt
        NetworkUtils.kt
```

The `/utils` package contains various utility classes that provide helper functions and common operations used throughout the application:

- **AudioUtils.kt**: Handles the processing of microphone audio signals during user question input. It implements logic to determine when a user has finished speaking. For more details on this implementation, refer to the [Wit.ai integration page](./WitAi.md).

- **DisplayUtils.kt**: Provides utility functions and constants for calculating display sizes of panels and various Jetpack Compose UI elements. These helpers ensure consistent sizing and layout across different screens.

- **Extensions.kt**: Contains a collection of extension functions for various classes and types used in the application. These extensions enhance the functionality of existing classes without modifying their source code.

- **MathUtils.kt**: Offers mathematical utility functions used throughout the application. It includes extension functions for the Float type, as well as for Vector3 and Quaternion data classes from Meta's Spatial Framework core library. These utilities simplify complex mathematical operations in the app.

- **NetworkUtils.kt**: Defines an `OkHttpClient` object with common settings, such as read timeouts, to be used by the services in the `/services` package. This ensures consistent network configurations across different API calls.

These utility classes promote code reusability, improve maintainability, and provide a centralized location for common operations used across different parts of the application.
