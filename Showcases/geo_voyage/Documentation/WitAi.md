# Wit.ai Integration

This application uses [Wit.ai](https://wit.ai) for the following purposes:

1. Speech-to-text transcription
2. Understanding the user's utterance

By transcribing the user's speech, and by understanding what the user is saying or asking, the application can react to the user's utterance in an appropriate way by taking some predetermined action. For demonstration purposes, this application uses only a small handful of intents and entities, defined and trained in the Wit.ai web dashboard, to determine whether or not to query llama with the transcribed speech.

Wit.ai can be used for so much more in your applications, including creating bots that people can chat with, enabling people to use their voices to control smart home devices, and much more. To learn more, see the [Get Started](https://wit.ai/docs/quickstart) webpage.

# Table of Contents

- [Wit.ai Integration](#witai-integration)
- [Table of Contents](#table-of-contents)
- [Core Files](#core-files)
- [Getting a Transcription and Understanding](#getting-a-transcription-and-understanding)
  - [Example Usage](#example-usage)
  - [Finished Speaking Detection](#finished-speaking-detection)
- [Using the Understanding](#using-the-understanding)
  - [Improved Filtering](#improved-filtering)

# Core Files

The core files for this integration are located within the directory `:app/com.meta.pixelandtexel.geovoyage/services/witai` and its subfolders.

```
/services
    /witai
        IWitAiServiceHandler.kt
        WitAiFlowService.kt
        WitAiService.kt

        /enums
            ...
        /models
            ...
```

For a more in-depth breakdown of the files within this directory, see the [Code Structure](./CodeStructure.md) documentation.

# Getting a Transcription and Understanding

The main entry point to using Wit.ai in this application is the `WitAiService.startSpeechToText` object function. Much of the implementation of this function was adapted directly from a Wit.ai example Android project located [here](https://github.com/wit-ai/android-voice-demo), and subsequently ported to Kotlin.

This `startSpeechToText` function performs the following:

* start the device microphone input
* start a HTTP streaming request to the Wit.ai [POST /speech endpoint](https://wit.ai/docs/http/20240304/#post__speech_link)
* write the microphone input data to the request stream
* stop the streaming and finish the request when the user selects stop or pauses their speech
* read the response stream from Wit.ai, parsing partial transcriptions
* return the final understanding when the response finishes

> **Note**: while it can be argued that the `startListening` implementation could have more strictly followed the separation-of-concerns code principle, the choice was made to consolidate the microphone recording and Wit.ai request streaming functionalities into a single service for demonstration purposes, and ease of use in this application.

The `startSpeechToText` function accepts 1 `IWitAiServiceHandler` argument, and returns a `WitAiStartResult` enum value. The `WitAiStartResult` enum value should be used to handle any errors which may have occurred when attempting to start the service, and the `IWitAiServiceHandler` argument is used to accept callbacks from the service, and handle the events and results appropriately.

The final callback function `IWitAiServiceHandler.onFinished` receives an instance of the `WitAiUnderstoodResponse` model, and should be used by the calling function to take any appropriate action based on the transcription and matched Wit.ai intents, entities, and traits.

> **Note** that this service uses multithreading because it is a long-running operation which streams the response back as Wit.ai determines the transcription and understanding of the user's utterance.

## Example Usage

```kotlin
val startResult = WitAiService.startSpeechToText(
    object : IWitAiServiceHandler {
        override fun onStartedListening() {
            // (optional) indicate to the user that the microphone input has
            // started
        }

        override fun onAmplitudeChanged(amplitude: Int) {
            // (optional) indicate to the user a change in the microphone's
            // detected volume
        }

        override fun onFinishedListening() {
            // (optional) indicate to the user that the microphone input has
            // stopped
        }

        override fun onPartial(partial: String) {
            // (optional) update our UI to show a partial transcription
        }

        override fun onFinished(result: WitAiUnderstoodResponse) {
            // use the understood result to take the appropriate action, or
            // simply display or use the result.text transcription value
        }

        override fun onError(reason: String) {
            // handle request error
        }
    }
)

if (startResult != WitAiStartResult.SUCCESS) {
    // handle a microphone start error
}
```

You can also see an example usage in the `startListening` function in the `:app/com.meta.pixelandtexel.geovoyage/ui/askearth/AskEarthNavigatorHost.kt` file, where updates to UI and error handling have been implemented. You should always take care to gracefully handle errors with services like this, and update the user-visible UI appropriately.

## Finished Speaking Detection

A nice-to-have feature of any application or service which accepts user voice prompts is to automatically detect if the user has finished speaking. This feature simply eliminates the need for the user to manually indicate that they've finished speaking, but the implementation of this type of feature is far from simple. Computer scientists and audio engineers use a number of voice activity detection (VAD) techniques, often more than one, to determine whether an audio signal has human speech, and when the speech has started or stopped. Some of these VAD techniques include:

- Spectral analysis
- Zero-crossing rate (ZCR)
- Machine learning
- Energy and silence detection

Implementing a robust VAD solution was out-of-scope for this project, but a very rudimentary energy and silence detection implementation can be found in the `startListening` function, which uses the `NoiseLevelAdjuster` class and utility functions in `:app/com.meta.pixelandtexel.geovoyage/utils/AudioUtils.kt`.

A high-level breakdown of this implementation is as follows:

1. Keep a smoothed running average of the absolute amplitude of the audio signal over a short period of time.
2. Use that baseline noise level and an arbitrary adjustment multiplier to determine a silence threshold.
3. Compare the volume level with the silence threshold to determine if the user has stopped speaking.
4. Stop the microphone if the user hasn't spoken for a given period of time.

While this solution isn't perfect, it gets it right _most of the time_. And because it isn't perfect, a toggle has been included in the Settings menu to disable the auto-stop recording functionality, should the user find that the feature isn't reliable in their noise environment.

# Using the Understanding

After receiving an understanding of the user's speech from Wit.ai in the form of an instance of the `WitAiUnderstoodResponse` model, you must decide what action your application should take. You may simply want to display or use the transcribed text from the user's speech. Alternatively, you could determine an appropriate action based on the detected intents, entities, and traits detected in the user's utterance.

For this application, a simple check of whether or not there were any matched entities and intents in the user's speech is used to determine if the transcription should be passed to Llama 3 as a query. This approach acts as a simple filtering method, and is used to make sure that only questions related to Earth geography, history, and culture were answered.

The function `WitAIFlowService.shouldSendResponseToLlama` includes the core implementation of this filtering. In the function body, the sets of entities, intents, and traits found within the `WitAiUnderstoodResponse` object are first accumulated.

```kotlin
fun shouldSendResponseToLlama(response: WitAiUnderstoodResponse): Boolean {
    // ...

    // accumulate the Wit.ai objects found within our response
    val responseEntities: Set<WitAiEntity> = getNamedEntitiesFromResponse(response)
    val responseIntents: Set<WitAiIntent> = getNamedIntentsFromResponse(response)

    // ...
}
```

Then, a simple intersection of the detected response objects with predetermined sets of entities and traits which would be acceptable for a Llama query is counted.

```kotlin
    // ...

    // determine how many of our response Wit.ai objects intersect with our
    // predetermined list of objects which are acceptable for a Llama query
    val numEntitiesMatch = responseEntities.intersect(allowedEntitiesForLlama).size
    val numIntentsMatch = responseIntents.intersect(allowedIntentsForLLama).size

    // ...
```

Finally, a check of whether the amount of acceptable entities/traits detected in the response meets or exceeds a given threshold is returned. In our case, we just check whether at least one intent and entity are detected.

```kotlin
    // ...

    return numEntitiesMatch > 0 && numIntentsMatch > 0
```

The full function:

```kotlin
fun shouldSendResponseToLlama(response: WitAiUnderstoodResponse): Boolean {
    // accumulate the Wit.ai objects found within our response
    val responseEntities = getNamedEntitiesFromResponse(response)
    val responseIntents = getNamedIntentsFromResponse(response)

    // determine how many of our response Wit.ai objects intersect with our
    // predetermined list of objects which are acceptable for a Llama query
    val numEntitiesMatch = responseEntities.intersect(allowedEntitiesForLlama).size
    val numIntentsMatch = responseIntents.intersect(allowedIntentsForLLama).size

    return numEntitiesMatch > 0 && numIntentsMatch > 0
}
```

## Improved Filtering

While out-of-scope for this project, a more robust solution would be to take different actions depending on which intents or entities matched. For example, in the use case of determining what/if to query Llama, you could pre-define a set of queries with injectable components. You could then select the query which matches the detected intent, and inject any matched entities into the query appropriately. As pseudocode illustration of this:

```
country_fact_query = "What is one interesting fact about the country {{country_name}}"

if responseIntent == "country_fact" && responseEntity.type == "country"
    formatted_query = country_fact_query.replace("{{country_name}}", responseEntity.value)
    queryLlama(formatted_query)
```

This solution would give you more control over the queries sent to Llama, and reduce the possibility of sending queries to Llama which don't match what the user was asking because the speech may have not been transcribed perfectly. Of course, what intents and entities you define in your Wit.ai app, and how well you train them, will determine how viable an option this more fine-tuned approach will be for your application.
