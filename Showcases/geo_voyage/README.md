# Geo Voyage

![Hero image](media/hero-image.png 'Hero image')

# Project Overview

**Geo Voyage** is a virtual and mixed reality app created to show developers how to build apps that make Quest users excited about learning and using the new **Meta Spatial SDK**. This app transforms your space into a global exploration platform. You may spin the stylized 3D globe, drop a pin anywhere, and dive into immersive experiences with 360 degree images and daily challenges. Geo Voyage’s AI companion will guide you through your journey, answering your questions and helping you discover fascinating facts about our planet.

Some of the goals of this project are to:

1. Develop an experience that inspires developers to build with this application framework.
2. Create an open source project that demonstrates best practices and clean development.
3. Provide documentation that helps developers navigate the project and understand the intricacies of the development – including detailed breakdowns of how different aspects of the project have been implemented, how they were optimized for Quest, and best practices for content creation.

This document also serves as a guide for setting up the app to build and run locally on your machine, and provide a high-level overview of the application architecture and features. More in-depth documentation on the integration of cloud services and implementation of core systems within the app can be found in the official Horizon documentation [here](https://developers.meta.com/horizon/documentation/spatial-sdk/geo-voyage-overview).

# Table of Contents

- [Geo Voyage](#geo-voyage)
- [Project Overview](#project-overview)
- [Table of Contents](#table-of-contents)
- [Health and Safety Guidelines](#health-and-safety-guidelines)
- [Design Flow](#design-flow)
- [Device Compatibility](#device-compatibility)
- [Key Components](#key-components)
- [Getting Started](#getting-started)
  - [Install Android Studio](#install-android-studio)
    - [Install Android SDKs](#install-android-sdks)
  - [Configuring Your Wit.ai App](#configuring-your-witai-app)
  - [Setup AWS Bedrock (or Ollama server)](#setup-aws-bedrock-or-ollama-server)
    - [AWS Bedrock Setup](#aws-bedrock-setup)
    - [Running Ollama Locally Via Docker](#running-ollama-locally-via-docker)
  - [Setup Google Maps API](#setup-google-maps-api)
  - [Adding Your App Secrets](#adding-your-app-secrets)
- [Running the Project](#running-the-project)
- [Main Scene](#main-scene)
  - [Panel](#panel)
    - [Explore UI](#explore-ui)
    - [Ask Earth UI](#ask-earth-ui)
    - [Today in History UI](#today-in-history-ui)
    - [Daily Quiz UI](#daily-quiz-ui)
  - [3D Objects](#3d-objects)
- [Other Scenes](#other-scenes)
- [Dependencies](#dependencies)
  - [Cloud Services](#cloud-services)
  - [Android Dependencies](#android-dependencies)
- [License](#license)
- [Attributions](#attributions)

# Health and Safety Guidelines

When building mixed reality experiences, we highly recommend evaluating your
content from a health and safety perspective to offer your users a comfortable
and safe experience. Please read the
[Mixed Reality H&S Guidelines](https://developer.oculus.com/resources/mr-health-safety-guideline/)
before designing and developing your app using this sample project, or any of
our Presence Platform features.

Developers should avoid improper occlusion, which occurs when virtual content
does not respect the physicality of the user’s environment. Improper Occlusion
can result in a misperception of actionable space.

- See [Occlusions with Virtual Content](https://developer.oculus.com/resources/mr-health-safety-guideline/#passthrough)
- To avoid improper occlusion, developers should ensure that users have (1)
  completed Space Setup and (2) granted Spatial Data permission (setup design)
  to allow proper occlusion in content placement, mesh collisions, and air
  navigation.

Using semi-transparent content lets the user have a better view of their
physical space and reduces the occlusion of objects or people that are not part
of the scanned mesh.

- Spatial data won’t incorporate dynamic elements of a user’s living space (for
  example, a chair that was moved after capture or a moving person/pet in the
  space).
- Uncaptured dynamic elements may be occluded by virtual content, making it more
  difficult for a user to safely avoid such hazards while engaged in the mixed
  reality experience.

Respect the user’s personal space. Avoid having virtual content pass through
their body or loom close to their face. When content crosses into a user’s
personal space they may experience a psychological or visual discomfort, or take
actions to avoid the virtual content that may increase the risk of injury or
damage (for example, backing up into a wall or chair). Dynamic virtual content
may also distract the user from their surroundings.

# Design Flow

This application experience consists of 4 different "play modes":

1. **Ask Earth** – speak a question related to Earth ecology, culture, or history, and get a text response from your AI assistant – powered by Wit.ai, Meta's Llama 3 8B LLM, and AWS Bedrock.

![Ask Earth](media/animated/AskEarth.gif 'Ask Earth')

2. **Explore** – spin the globe around, find and drop a pin on a place of interest, and view information about a notable city or landmark near that location – powered by the Google Maps Geocoding API, Llama 3 8B, and AWS Bedrock. If available at that location, you may also transport yourself there by entering a virtual 360 degree view of the corresponding place on Earth – powered by the Google Map Tiles API. Additionally, select 3D models of notable landmarks placed around the globe to view their brief descriptions – pre-generated by Llama 3 8B.

![Explore](media/animated/Explore.gif 'Explore')

3. **Daily Quiz** – challenge yourself with 5 daily quiz questions related to earth geography and cultures – pre-generated by Llama 3 8B. Compare your results with your friends, and return the following day for new questions.

![Daily Quiz](media//animated/DailyQuiz.gif 'Daily Quiz')

4. **Today in History** – learn about one notable event in history that occurred on today's date – powered by Llama 3 8B and AWS Bedrock. Return the following day for a new fact.

![Today in History](media/animated/TodayInHistory.gif 'Today in History')

![Design Flow](media/design-flow.png 'Design Flow')

# Device Compatibility

Meta's Spatial Framework is compatible with the following devices:

- Quest 3
- Quest Pro
- Quest 2 (supported, but not recommended)

The Quest 3 was primarily used for testing throughout the development of this project. Attempting to run this application on unsupported devices is not recommended, and may result in errors and/or undesirable performance or application behavior.

In order to build and run this application, ensure that you have one of the Quest headset models listed above, and that [the steps](https://developer.oculus.com/documentation/native/android/mobile-device-setup/) to setup your headset for development have been completed.

# Key Components

The main features of this application can be divided into the following 4 areas. Each of these areas has detailed documentation on their implementation and usage in the pages respectively linked.

1. [Wit.ai Integration](https://developers.meta.com/horizon/documentation/spatial-sdk/geo-voyage-process-audio)
2. [Llama 3 Querying](https://developers.meta.com/horizon/documentation/spatial-sdk/geo-voyage-query-llama)
3. [Custom Components & Systems](https://developers.meta.com/horizon/documentation/spatial-sdk/geo-voyage-component-system)
4. [VR & Google Maps API Integration](https://developers.meta.com/horizon/documentation/spatial-sdk/geo-voyage-transport-users)

# Getting Started

## Install Android Studio

This application was built with [Android Studio](https://developer.android.com/studio) Koala (2024.1.1) and Kotlin (2.0.0). To begin, make sure you've downloaded and installed the required software.

### Install Android SDKs

As per the Meta Quest [documentation](https://developer.oculus.com/resources/publish-mobile-manifest/) regarding Android SDK version requirements for submitting your app to the Meta Horizon Store, this application uses the following SDK versions:

- `android:minSdkVersion` 29
- `android:targetSdkVersion` 32

> Note: even though we are targeting API level 32, the `compileSdk` version has been set to 34 to enable support for some Jetpack Compose and Material 3 dependencies.

These SDK versions can be downloaded and installed via the Android Studio SDK Manager window, accessible through the `Tools/SDK Manager` menu item. Note that these SDK version numbers are specified in the `app/build.gradle.kts` gradle script, and the app `AndroidManifest.xml`.

![Android SDK Manager](media/targetSdkVersion-34.png 'Android SDK Manager')

## Configuring Your Wit.ai App

[Wit.ai](https://wit.ai/) is used by this application to perform speech to text transcription, as well as understand what the user is intending. Follow these steps to ensure that Wit.ai integration will properly function.

1. **Follow the Quickstart guide** [here](https://wit.ai/docs/quickstart) to set up your Wit.ai app and train it. Note that the Wit.ai intents, entities, and traits used by this application are defined as enumerations in the Android project files located in the `:app/com.meta.pixelandtexel.geovoyage/services/witai/enums` directory. You may choose to name the intents, entities, and traits defined in your Wit.ai app to match the names of the enumeration values in those code files, or rename the values in the code. To learn more about this application's integration of Wit.ai and its usage, see [this documentation](https://developers.meta.com/horizon/documentation/spatial-sdk/geo-voyage-process-audio).
2. **Get your Wit.ai app Client Access Token** from the Settings page of your app in the Wit.ai web dashboard. This can be accessed by selecting the `Management/Settings` menu item on the left side. Once you've opened the Settings page, you should see the Client Access Token value there. You'll need it in the below step on [adding your app secrets](#adding-your-app-secrets) to the Android project. The Wit.ai HTTP API uses OAuth2 to authenticate requests via a bearer token in the `Authorization` header value. Note that access tokens are app and user specific, and should not be shared or posted publicly. More info on Wit.ai HTTP API can be found [here](https://wit.ai/docs/http/20240304/).

## Setup AWS Bedrock (or Ollama server)

A core part of this application is its ability to prompt Meta's Llama 3 8B LLM. This is used for several purposes throughout the application. To read more detail about that, see [this page](https://developers.meta.com/horizon/documentation/spatial-sdk/geo-voyage-query-llama). In order to build and run this application, you'll need to take these steps to ensure Llama querying functions.

This application has built-in support for invoking the Llama via two different methods:

- [AWS Bedrock](https://aws.amazon.com/bedrock/) – AWS's solution for invoking various LLM models, as well as the capability to train and upload your own model for querying. This option has a [cost](https://aws.amazon.com/bedrock/pricing/) associated with it which should be considered.
- [Ollama](https://ollama.com/) is a free option, but requires you to set up a server to run the tool. You could also run ollama locally on a device on your network and connect to it using the device's IP address and port number – an option we found useful during development. See [the instruction below](#running-ollama-locally-via-docker) on running ollama via docker. Alternatively, you can use the executable available on the ollama website.

### AWS Bedrock Setup

1. You must first [create an AWS account](https://aws.amazon.com/resources/create-account/) if you don't already have one.
2. Next, login to the AWS console, and navigate to the Bedrock service.
3. At the bottom of the left side navigation bar, select the `Bedrock configurations/Model access` menu item. Scroll down to make sure that access has been granted to the Llama 3 8B Instruct model. If not, you may have to select the "Modify model access" button at the top of the page to gain access.
4. Next, you'll create a custom permissions policy which only grants `InvokeModelWithResponseStream` access on the `"arn:aws:bedrock:<your region>::foundation-model/meta.llama3-8b-instruct-v1:0"` resource (replace `<your region>` with the region within which you setup your AWS account and Bedrock service). Follow [these instructions](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies_create.html) to create the policy, and remember the name you've given the policy – e.g. `InvokeBedrockLlama3Model`. Your policy JSON should look similar to this (except, perhaps, the region name):

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "VisualEditor0",
      "Effect": "Allow",
      "Action": ["bedrock:InvokeModelWithResponseStream"],
      "Resource": [
        "arn:aws:bedrock:us-east-1::foundation-model/meta.llama3-8b-instruct-v1:0"
      ]
    }
  ]
}
```

5. Next, follow the [instructions here](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_users_create.html) to create and configure an IAM user.
   1. You may assign a descriptive User name of your choice – e.g. `LlamaServiceAccount`
   2. It is not necessary to grant the user access to the cloud console.
   3. For this new user, assign the policy you just created to the new user.
6. Finally, you must generate an access key for this new AWS IAM user, and save those credentials.
   1. Navigate to the IAM service dashboard, and then select the `Access management/Users` menu item on the left side navigation bar.
   2. Find and select the user you just created to view their account page.
   3. Select the `Security credentials` tab on the mid-page navigation, and scroll down to the `Access keys` card.
   4. Select the "Create access key" button on the right side.
   5. Select the "Other" option as your use-case.
   6. Add a description – e.g. `Service account access key for invoking Llama3 in Geo Voyage project.`
   7. Select the "Create access key" button, and save the "Secret access key" value someplace secure, as you will not be able to access that value after you leave the webpage. You'll need the access key and secret access key in the below step on [adding your app secrets](#adding-your-app-secrets) to the Android project.
7. Note that you should always follow [best practices](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_access-keys.html#securing_access-keys) for keeping your access key secure.

### Running Ollama Locally Via Docker

To quickly get ollama running locally, you may utilize the provided docker-compose script. First, install docker locally and then open terminal in the project folder. Run the following command to create the Ollama server:

```bash
docker compose up -d
```

Next, execute the below command to run the Llama 3 model model:

```bash
docker exec -it ollama ollama run llama3
```

This will download the latest llama3 model locally in the `data` folder and expose it to your network at port 11434.

## Setup Google Maps API

1. Sign up or in to the [Google Cloud Platform](https://console.cloud.google.com/).
2. Navigate to your Google Maps Platform [APIs & Services page](https://console.cloud.google.com/google/maps-apis/api-list), and enable the "Geocoding API" and "Map Tiles API".
3. Navigate to the Google Maps Platform [Credentials page](https://console.cloud.google.com/google/maps-apis/credentials) and create a new API key by selecting the "Create Credentials" dropdown at the top menu bar, and selecting the "API key" option.
   1. After creating it, select the "Edit API key" option from the Actions list which becomes visible after clicking on the kebab button on the right side of your new API key in the "API Keys" list in the middle of the webpage.
   2. Name the key something meaningful – e.g. "Geo Voyage Maps API Key"
   3. Restrict the key to the APIs you enabled in the previous steps.

![Google Maps API Key Restrictions](media/google-maps-api-key-restrictions.png 'Google Maps API Key Restrictions')

4. Save the "API key" value someplace secure, as you will need it in the below step on [adding your app secrets](#adding-your-app-secrets) to the Android project.

## Adding Your App Secrets

The cloud services outlined below in [Dependencies](#dependencies) require access tokens. These tokens must be provided in a `secrets.properties` file, which is ignored by git. Those key/value pairs are parsed in the Android project `app/build.gradle.kts` gradle script, and are injected into the application's generated `BuildConfig.java` file to be accessed by the application code.

Start by duplicating the `secrets.properties.example` file, and renaming it to `secrets.properties`. In this new file (pictured below), replace the values with your access tokens. Note that depending on which service you have chosen to provide Llama 3 invocation in the instructions above, you must only provide secrets for one of the given values below – AWS Bedrock keys or your Ollama server URL (including port number).

```properties
# Token for Wit.ai client REST API
WIT_AI_CLIENT_ACCESS_TOKEN=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

# (optional) URL to ollama server
OLLAMA_SERVER_URL=https://your-domain:port

# AWS Bedrock keys
AWS_BEDROCK_ACCESS_KEY=BBBBBBBBBBBBBBBBBBBB
AWS_BEDROCK_SECRET_KEY=CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

# Google Maps API key
GOOGLE_MAPS_API_KEY=DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
```

# Running the Project

After you have completed the steps in [Getting Started](#getting-started), you are ready to build and run the app locally. Open the project in Android Studio by selecting the `File/Open...` menu item, and selecting the top-level project directory in the file browser.

After the project has successfully opened, make sure to Sync the project with the gradle files. You can do this by selecting the Sync icon near the end of the top menu bar. This may take some time as dependencies are downloaded.

![Gradle Sync](media/gradle-sync.png 'Gradle Sync')

Next, ensure that your headset is plugged in to your computer, and that the device appears and is selected in the target devices dropdown – also in the top menu bar.

![Target Device Drop-down](media/target-device.png 'Target Device Drop-down')

Next, select the Make Module icon in the top menu bar to compile the project code. Note that this step will generate the `BuildConfig.java` file, which should include the access token key values you entered into your `secrets.properties` file.

![Make module button](media/make-app-module.png 'Make module button')

Finally, select Run from the top menu bar or from the Run menu item. The application should install onto your device and launch.

![Run app](media/run-app.png 'Run app')

# Main Scene

The primary scene is composed of the 3D objects and UI panel which load and are displayed when the application starts. The entities in this scene and their attached components are primarily defined in the `res/xml/scene.xml` file, and are loaded with the command `inflateIntoDataModel(R.xml.scene)` in the `MainActivity.onSceneReady` function.

This scene consists of 5 entities (1 panel and 4 3D objects), detailed below.

## Panel

This application uses one main panel object for all UI. The layout and UI elements were created with [Jetpack Compose](https://developer.android.com/compose), and are rendered into the scene via a Spatial SDK PanelSceneObject. The design follows [Material Design](https://m3.material.io/) standards, and was engineered to be similar to a landscape tablet screen view.

![Main panel](media/main-panel.png 'Main panel')

This panel floats to the right of the globe, and is the main navigation means for the user. Tapping on the list items on the right side of this panel initiates the 4 corresponding play modes – each with their own content which appears in the middle of the panel on elevated green surfaces. Additionally, the cog button on the upper right exposes a Settings menu with various controls over application behavior, while the title text at the top displays persistent relevant information about or of the current play mode.

### Explore UI

![Explore prompt](media/explore-prompt.png 'Explore prompt')

![Explore results](media/explore-results.png 'Explore results')

### Ask Earth UI

![Ask Earth panel](media/ask-earth-listening.png 'Ask Earth panel')

![Ask Earth panel thinking](media/ask-earth-thinking.png 'Ask Earth panel thinking')

![Ask Earth panel result](media/ask-earth-result.png 'Ask Earth panel result')

### Today in History UI

![Today in History panel](media/today-in-history-panel.png 'Today in History panel')

### Daily Quiz UI

![Daily Quiz start](media/daily-quiz-start.png 'Daily Quiz start')

![Daily Quiz question](media/daily-quiz-question.png 'Daily Quiz question')

![Daily Quiz results](media/daily-quiz-results.png 'Daily Quiz results')

## 3D Objects

**Globe Models** – the globe object is the central focus of the 3D scene. It is composed of 3 separate entities: the globe, the trees atop the globe surface, and the clouds spinning over the globe surface. The clouds are separate from the globe model in order to rotate independently, and the trees model was separated so it could be hidden programmatically during different parts of the Explore play mode.

![Globe model](media/globe-model.png 'Globe model')

**Pin model** – the pin object is initially hidden, and only appears when the user enters the Explore play mode, and selects a spot on the globe.

![Pin model](media/pin-model.png 'Pin model')

**Landmark Entities** – In addition to the 3D mesh and panel entities listed above, a number of mesh entities are created when the app first begins in the `LandmarkSpawnSystem.kt` file (more info on that [here](https://developers.meta.com/horizon/documentation/spatial-sdk/geo-voyage-component-system#landmarkspawn)). Each of these entities represent different iconic landmarks around the world, and appear across all of the continents.

# Other Scenes

In addition to the main scene detailed above, additional test scenes may be found in the same directory with the naming convention `res/xml/scene_<name>.xml`:

- `res/xml/scene_globe_only.xml` – this scene contains only the globe entity and its associated clouds and trees entities. It can be used to test the custom `Spinnable` and `GrabbableNoRotation` components and systems without the UI panels getting in the way.

# Dependencies

## Cloud Services

The following cloud services are used by this application:

- [Wit.ai](https://wit.ai/) – for speech-to-text transcription, and Entity and Intent understanding extraction
- [AWS Bedrock](https://aws.amazon.com/bedrock/) – Llama 3 8B querying
- [Google Maps API](https://developers.google.com/maps/documentation) – Geocoding API and Street View Tiles API

Optional:

- [Ollama](https://ollama.com/) – an alternative to AWS Bedrock, for serving your Llama 3 model over local network, or for connecting to a remote server running ollama.

## Android Dependencies

These dependencies and their corresponding versions can be found in the `app/build.gradle.kts` and `libs.versions.toml` files in the Android project.

- Kotlin 2.0.0
- [Meta's Spatial Framework](https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-overview)
- [gson](https://github.com/google/gson) – for deserializing JSON response objects from the aforementioned cloud services
- [compose-markdown](https://github.com/jeziellago/compose-markdown) – for displaying markdown-formatted responses from Llama3 in Jetpack Compose
- [AWS Kotlin SDK](https://github.com/awslabs/aws-sdk-kotlin) – for integrating AWS Bedrock for Llama 3 querying
- [Jetpack Compose](https://developer.android.com/compose) – for building modern Android UI with Material Design 3 support

# License

MIT License

Copyright (c) 2024 Meta

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

# Attributions

| Dependency       | Source                                                        | License            |
| ---------------- | ------------------------------------------------------------- | ------------------ |
| okhttp           | [GitHub repo](https://github.com/square/okhttp)               | Apache License 2.0 |
| gson             | [GitHub repo](https://github.com/google/gson)                 | Apache License 2.0 |
| compose-markdown | [GitHub repo](https://github.com/jeziellago/compose-markdown) | MIT License        |
