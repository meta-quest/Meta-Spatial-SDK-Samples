# Llama 3 Querying

This application uses Meta's LLM Llama 3 8B to teach users about our planet's geography, cultures, ecology, and history. Specifically, it was and is used in all 4 play modes in these ways:

- **Ask Earth** – fetch answer to the question the user just asked.
- **Explore** – fetch information about a location on the globe that the user selects, and pre-generate short descriptions for landmarks which appear on the globe.
- **Today in History** – fetch one notable event from history which occurred on the month and day of the user's local time.
- **Daily Quiz** – pre-generate trivia questions of a range of difficulties, with alternate incorrect options and latitude/longitude geocoordinates for the correct answer location.

# Table of Contents

- [Llama 3 Querying](#llama-3-querying)
- [Table of Contents](#table-of-contents)
- [Core Files](#core-files)
- [Server Options](#server-options)
  - [Ollama](#ollama)
  - [AWS Bedrock](#aws-bedrock)
- [Querying](#querying)
  - [Model Parameters](#model-parameters)
  - [Templated Queries](#templated-queries)
  - [Response Streaming](#response-streaming)
- [Example Usage](#example-usage)
- [Pre-generated Data](#pre-generated-data)

# Core Files

The core files for this integration are located within the directory `:app/com.meta.pixelandtexel.geovoyage/services/llama` and its subfolders.

```
/services
    /llama
        QueryLlamaService.kt
        IQueryLlamaServiceHandler.kt

        /models
            BedrockRequest.kt
            BedrockResponse.kt
            OllamaRequest.kt
            OllamaResponse.kt
```

For a more in-depth breakdown of the files within this directory, see the [Code Structure](./CodeStructure.md) documentation.

# Server Options

This application includes out-of-the-box support for two services to run the model and receive results: [Ollama](https://ollama.com/) and [AWS Bedrock](https://aws.amazon.com/bedrock/). More information on those services, how to set them up to run this app, and considerations when choosing to use one over the other, can be found in this repository's main README page [here](/README.md#setup-aws-bedrock-or-ollama-server).

The main entry point to using this querying service in this application is the `QueryLlamaService.submitQuery` object function. Both of these server types are accessible in the app through the single wrapper function viewable below. Which server type is used is determined by the value stored in the application's `SharedPreferences`, which can be changed via a toggle in the in-app Settings menu, and defaults to AWS Bedrock.

```kotlin
fun submitQuery(
    query: String,
    creativity: Float = .6f, // temperature
    diversity: Float = .9f,  // top_p
    handler: IQueryLlamaServiceHandler
) {
    if(queryTemplate.isNullOrEmpty()) {
        throw Exception("Llama query template not created")
    }

    val fullQuery = String.format(queryTemplate!!, query)
    val temperature = creativity.clamp01()
    val top_p = diversity.clamp01()

    val serverType = SettingsService.get(
        KEY_LLAMA_SERVER_TYPE, LlamaServerType.AWS_BEDROCK.value)
    when (serverType) {
        LlamaServerType.OLLAMA.value -> queryOllama(
            fullQuery,
            temperature,
            top_p,
            handler
        )

        LlamaServerType.AWS_BEDROCK.value -> queryAWSBedrock(
            fullQuery,
            temperature,
            top_p,
            handler
        )
    }
}
```

> Note that both of the server type functions `queryOllama` and `queryAWSBedrock` use multithreading because they are long-running operations which stream the response back as the model generates the response. Additionally, both of the implementations only utilize the "generate" or "invoke" functionality, though both APIs support LLM "chat", where you can send follow-up queries which use the previous dialog as a part of the response generation. Choosing which type of querying to use depends on your use-case.

## Ollama

The Ollama model invocation uses a simple, unauthenticated HTTP request – the `/api/generate` endpoint detailed in the [official Ollama documentation here](https://github.com/ollama/ollama/blob/main/docs/api.md#generate-a-completion). The server URL should be configured in your `secrets.properties`, but can be overridden in the in-app Settings menu by selecting Ollama as your server type, and inputting the URL in the text field list item.

If you choose to use this server type for Llama invocation in a production application, it is highly recommended that you add some form of authentication for your requests. Illustrating an implementation of server-side authentication was out-of-scope for this project, which simply serves as a proof-of-concept for integrating this service.

Ollama supports using a number of parameters to configure your querying, listed [here](https://github.com/ollama/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values). For this application, only the parameters `temperature` and `top_p` were used in order to have parity with what parameters the AWS Bedrock model invocation SDK supports, which is by comparison much more limited. How these parameters are configured is explained below in [Model Parameters](#model-parameters).

The Kotlin representation of the Ollama request payload is located in the file `:app.com.meta.pixelandtexel.geovoyage.services.llama.models.OllamaRequest.kt`, and is serialized into JSON by the [gson](https://github.com/google/gson) dependency before being set as the request body.

```kotlin
val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
val nativeRequest = OllamaRequest(query, OllamaRequestParams(temp, top_p))
val requestBody = gson.toJson(nativeRequest).toRequestBody(jsonMediaType)

val request = ollamaRequestBuilder.post(requestBody).build()
```

More information on the query construction can be found in the section below [Templated Queries](#templated-queries).

## AWS Bedrock

The AWS Bedrock model invocation utilizes the [AWS Kotlin SDK](https://sdk.amazonaws.com/kotlin/api/latest/bedrockruntime/index.html), and requires access key and secret key authentication. Additional info and instructions for setting up your app to use those keys can be found in this repo's [main README here](/README.md#adding-your-app-secrets).

The AWS Kotlin SDK supports 3 parameters when invoking Meta's Llama model: `temperature`, `top_p`, and `max_gen_length` – as described [here](https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-meta.html#model-parameters-meta-request-response). How these parameters are configured is explained below in [Model Parameters](#model-parameters).

The Kotlin representation of the AWS Bedrock request payload is located in the file `:app.com.meta.pixelandtexel.geovoyage.services.llama.models.BedrockRequest.kt`, and is also serialized into JSON by the [gson](https://github.com/google/gson) dependency. Constructing the AWS Bedrock request payload is a little more involved than when using Ollama, as it requires using Llama 3's instruction format:

```kotlin
// Embed the prompt in Llama 3's instruction format.
val instruction = """
    <|begin_of_text|>
    <|start_header_id|>user<|end_header_id|>
    {{prompt}} <|eot_id|>
    <|start_header_id|>assistant<|end_header_id|>
""".trimIndent().replace("{{prompt}}", query)

val nativeRequest = BedrockRequest(instruction, temp, top_p)
val requestBody = gson.toJson(nativeRequest)

val request = InvokeModelWithResponseStreamRequest {
    modelId = "meta.llama3-8b-instruct-v1:0"
    contentType = "application/json"
    accept = "application/json"
    body = requestBody.encodeToByteArray()
}
```

More information on the query construction can be found in the section below [Templated Queries](#templated-queries).

# Querying

Both of these Llama server types support a range of functionality and options to configure the nature of the response you receive when invoking models. The three most important techniques considered for this application are outlined below.

1. Model parameters
2. Templated queries
3. Response streaming

## Model Parameters

As noted above, only 2 parameters are being used in the model invocation implementation in this application: `temperature` and `top_p`. Technically, parameters to configure the maximum number of tokens to use in the generated response are also supported, but are left at the respective default values for each server type integration – 128 for Ollama, and 512 for AWS Bedrock. An in-depth explanation of these parameters, and how they affect query responses is out-of-scope for this documentation, but a brief explanation follows:

**temperature** – the creativity level of the model. A low value of 0.1 was chosen for this parameter to have less randomness overall, and higher predictability.

**top_p** – the diversity level of the model. A high default value of 0.9 for this parameter was chosen to have greater diversity in the responses.

Choosing the right model invocation parameters really comes down to your use-case, and the values for this app were chosen after much testing to strike a balance between educational and fun.

## Templated Queries

For this application, a technique called "templated queries" is used, wherein variables or data are injected into pre-defined queries. There are 3 query templates defined below:

```xml
<!-- Llama3 Queries -->
<string name="explore_screen_base_query">
    What is one notable city or landmark near the coordinates %1$s in %2$s?
</string>
<string name="today_in_history_base_query">
    What is one notable event in history that occurred on %1$s?
</string>
<string name="base_query_template">
    In a short response formatted with markdown, answer the following question. %1$s
</string>
```

**explore_screen_base_query** – in the Explore play mode, geocoordinates formatted in common notation (E/W and N/S instead of +/- to denote hemisphere) are injected at token 1, and the place name returned from the Google Geocoding API (if found) is injected at token 2. See the [GoogleMapsAPI.md page](./GoogleMapsAPI.md) for more info regarding the usage of the Google Geocoding API.

> e.g. "What is one notable city or landmark near the coordinates `37.4°N, 139.76°E` in `Japan`?"

**today_in_history_base_query** – in the Today in History play mode, the user's local date is injected at token 1 in the format of `MMMM d`.

> e.g. "What is one notable event in history that occurred on `August 30`?"

**base_query_template** – lastly, all queries are injected into the base query, which is templated in the `QueryLlamaService.submitQuery` function just before being sent to the designated model server. This query accomplishes two tasks: ensuring the result isn't too long (overflowing the allocated panel area and requiring the user to scroll), and transforming the response into Markdown format. All Llama text responses are displayed inside of `MarkdownText` composable function available through the `compose-markdown` dependency. This is a slightly hacky, yet effective way to display a nicely formatted response from Llama in the application.

> e.g. a user question: "In a short response formatted with markdown, answer the following question. `Where are the tallest mountains on Earth?`"
> <br><br>
> e.g. a Today in History query: "In a short response formatted with markdown, answer the following question. `What is one notable event in history that occurred on August 30?`"

The wording of templated queries greatly affects the content of the responses returned from model invocation, and a lot of testing and tweaking was done to find the best wording for this application's purposes. For you own purposes, it is recommended that you dedicate time in your development to test and refine your templated queries if you choose to use this strategy.

## Response Streaming

The integration of Llama model invocation in this application makes use of response streaming in order to provide a better user experience. Both of these Llama server types do support non-streaming requests, but by eliminating the need to wait a longer period of time for the invocation to complete, and by providing progressive visual feedback on the application functions, the user's attention remains engaged by the app. For most situations where you are displaying the text response to the user, it is recommended that you take this approach.

# Example Usage

```kotlin
val fullQuery = String.format(templateQuery, data)

QueryLlamaService.submitQuery(
    query = fullQuery,
    creativity = 1f,
    diversity = .9f,
    handler = object : IQueryLlamaServiceHandler {
        override fun onStreamStart() {
            // (optional) hide loading message/graphic
        }

        override fun onPartial(partial: String) {
            // (optional) update result UI with partial response
        }

        override fun onFinished(answer: String) {
            // update result UI with full, final response
        }

        override fun onError(reason: String) {
            // handle querying error
        }
    }
)
```

# Pre-generated Data

In addition to the queries executed during runtime, Llama 3 was utilized during this application's development to generate educational data for users that is displayed during different play modes:

- **Daily Quiz** – the questions and answers for this play mode were generated with the following prompt:

> Come up with 100 trivia questions related to earth geography and cultures. Rank them from easy to difficult, and include the latitude and longitude coordinates of each location answer. Format the response in XML, and include 2 incorrect answers for each question.

- **Explore** – landmark descriptions for this play mode were generated with the following prompt:

> In short responses, describe each of the following landmarks: the Great Egyptian Pyramids, the Eiffel Tower, Chichén Itzá, the Sydney Opera House, Taj Mahal, the Christ the Redeemer statue, the Colosseum, Mount Vinson, and Victoria Falls. Format the responses in XML, and include the name, description, latitude, and longitude of each landmark.

After receiving responses for these queries and double-checking for their accuracy, minimal work was needed to adjust them for use in this app. This demonstrates the power of Meta's Llama model, and how it can be utilized for education within your own application. Try the online [Meta AI](https://www.meta.ai/) platform for your needs.
