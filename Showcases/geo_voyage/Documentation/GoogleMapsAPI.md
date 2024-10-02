# Google Maps API

This application uses 2 APIs from the [Google Cloud Platform](https://console.cloud.google.com/) to fetch and display data and imagery about real world locations which correspond to points that users select on the globe.

1. **Geocoding API** – used to fetch information about a location on the globe
2. **Map Tiles API** – used to fetch street view panoramic imagery from a location on the globe

![MR to VR](/Documentation/Media/animated/MRtoVR.gif 'MR to VR')

# Table of Contents

- [Google Maps API](#google-maps-api)
- [Table of Contents](#table-of-contents)
- [Core Files](#core-files)
- [Reverse Geocoding](#reverse-geocoding)
  - [Example Usage](#example-usage)
- [Map Tiles Imagery for MR to VR Transition](#map-tiles-imagery-for-mr-to-vr-transition)
  - [Methodology](#methodology)
  - [Getting the Panorama Image's Metadata](#getting-the-panorama-images-metadata)
  - [Determining the Zoom Level and Tiles to Fetch](#determining-the-zoom-level-and-tiles-to-fetch)
  - [Fetching and Combine the Tiles](#fetching-and-combine-the-tiles)
  - [Displaying the Panorama](#displaying-the-panorama)
  - [Balancing Cost \& Quality](#balancing-cost--quality)

# Core Files

The core files for this integration are located within the directory `:app/com.meta.pixelandtexel.geovoyage/services/googlemaps` and its subfolders.

```
/services
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
```

For a more in-depth breakdown of the files within this directory, see the [Code Structure](./CodeStructure.md) documentation.

# Reverse Geocoding

As stated in the official API documentation, reverse geocoding is the process of "...translating a location on the map into a human-readable address...". In this application, that service is used in the Explore play mode when the user drops a pin on the map. The address or location information that is returned back from the API request is used to send a more informed Llama query to our Llama server. See [this page](./Llama3.md) for more info on the templated Llama queries implemented in this application.

The service is also used as a validation or filtering mechanism, wherein locations which have no information or data known to the Google Maps platform (e.g. out in the middle of the Pacific Ocean) are excluded from being sent in a Llama query.

For a more thorough and in-depth documentation on this API usage, see [the Google documentation](https://developers.google.com/maps/documentation/geocoding/requests-reverse-geocoding) on this API usage.

The main entry point to using this reverse geocoding service in this application is the `GoogleMapsService.getPlace` object function. The `getPlace` function accepts 2 arguments, a `GeoCoordinates` instance and `IGeocodeServiceHandler` instance, the later of which is used to accept callbacks from the service and handle the events and results appropriately. This `getPlace` function performs the following:

- build the request URL with the formatted geocoordinates as query parameters
- return early if there are 0 results found
- loop through the results and extract a name for the first place returned

Note that in this application, the reverse geocode results are filtered by only the place types included in the array value `resultTypeFilter` below. As long as one place of at least one of these types is returned from the API, the function succeeds and returns the `formatted_address` or `long_name` of that result.

```kotlin
private val resultTypeFilter =
    listOf("country", "political", "natural_feature", "point_of_interest")
```

> **Also note** that unlike other services used by this application, which include long running operations, this service uses Kotlin coroutines to accomplish asynchronous execution for the quick API call.

## Example Usage

```kotlin
GoogleMapsService.getPlace(coords, object : IGeocodeServiceHandler {
    override fun onFinished(place: String?) {
        if (place.isNullOrEmpty()) {
            // no location found; don't query llama
            return
        }

        // submit templated query to llama, with place name injected into query
    }

    override fun onError(reason: String) {
        // handle request error
    }
})
```

You can also see an example usage in the `startQueryAtCoordinates` function in the `:app/com.meta.pixelandtexel.geovoyage.activities/ExploreActivity.kt` file, where updates to UI and error handling have been implemented.

> **Quick tip**: you should always take care to gracefully handle errors with services like this, and update the user-visible UI appropriately.

# Map Tiles Imagery for MR to VR Transition

In the Explore play mode, the [Google Maps Tiles API](https://developers.google.com/maps/documentation/tile) is used to fetch metadata about panoramic images near the location where the user drops a pin on the globe, as well as display the panorama in the headset using the [Street View Tiles](https://developers.google.com/maps/documentation/tile/streetview) endpoints.

## Methodology

The implementation for displaying a panorama in-headset can be broken down into these steps:

1. Create a new session, which includes a token needed for subsequent requests to the API. More on tokens [here](https://developers.google.com/maps/documentation/tile/session_tokens).
2. Fetch the metadata for a panoramic image closest to a set of geo coordinates, within a specified radius
3. Determine what zoom level and image tiles which compose the full panoramic image should be fetched
4. Fetch the tiles as bitmaps
5. Combine the bitmaps into 1 larger bitmap
6. Assign that bitmap image to a Spatial SDK skybox entity material

## Getting the Panorama Image's Metadata

The main point of entry for using the Street View Tiles API in this application is the `GoogleTilesService.getPanoramaDataAt` function, which accepts a pair of geo coordinates and radius around those coordinates in which to search. The `streetview/metadata` endpoint is then used to fetch the metadata for the panoramic image nearest to those coordinates within the radius, if there is one.

```kotlin
data class PanoMetadata(
    val panoId: String,

    // image and tile dimensions for fetching images to compose the full image

    val imageHeight: Int,
    val imageWidth: Int,
    val tileHeight: Int,
    val tileWidth: Int,

    // data about the image that Google requires you to display

    val copyright: String,
    val date: String?,
    val reportProblemLink: String,
)
```

**Example usage:**

```kotlin
val coords = GeoCoordinates(37.485073f, -122.150856f)
var panoData: PanoMetadata? = null

CoroutineScope(Dispatchers.Main).launch {
    panoData = GoogleTilesService.getPanoramaDataAt(coords, 10000)
}

if (panoData.value != null) {
    // display the metadata, and/or fetch and display the panorama bitmap
}
```

In this application, that API request occurs in the background when the user drops a pin on the globe. If a panoramic image is found, the "VR Mode" button on the Explore panel is enabled. If the user then selects that enabled button, the next step in the implementation is triggered.

> Note that the [usage policy](https://developers.google.com/maps/documentation/tile/policies) of the Map Tiles API requires that you display certain information when using their API in your application. In the PanoMetadata response, the last 3 properties are displayed on the Explore panel, pictured below. The "Report a problem with this image" link opens a web browser view to the linked URL.

![Google Map Tiles API attribution](./Media/google-map-tiles-api-attribution.png 'Google Map Tiles API attribution')

The next step to using this service is to pass the fetched metadata to the `GoogleTilesService.getPanoramaBitmapFor` function, which accepts the metadata object and an object which implements the `IPanoramaServiceHandler` interface to receive callbacks with the resulting Bitmap object or an error message.

```kotlin
interface IPanoramaServiceHandler {
    fun onFinished(bitmap: Bitmap)
    fun onError(reason: String)
}
```

**Example usage:**

```kotlin
GoogleTilesService.getPanoramaBitmapFor(
    panoData,
    object : IPanoramaServiceHandler {
        override fun onFinished(bitmap: Bitmap) {
            // set the skybox entity's material albedo texture to the bitmap
        }

        override fun onError(reason: String) {
            // handle request error
        }
    }
)
```

## Determining the Zoom Level and Tiles to Fetch

The `streetview/tiles` endpoint cannot be used to fetch a full-size street view panoramic image in 1 request. Instead, you must perform a series of requests, specifying a zoom level, and the x/y indices of the tile you are fetching, and then stitch them together to make 1 combined bitmap image. Zoom levels range from 0 to 5, and determine the approximate field of view for the tile image you are fetching. Therefore, a higher zoom level means a larger combined image with greater visible detail, but more tile image network requests. Whereas a lower zoom level means a lower visible quality combined image when viewed in a 360 view, but less network requests. More details on the endpoint can be found in the official documentation [here](https://developers.google.com/maps/documentation/tile/streetview).

In this application, in order to determine the desired zoom level to use, we calculate the largest image we could load while ensuring the number of network requests needed to fetch the entire image stays under a predefined threshold `MAX_TILE_FETCHES_PER_PANO`, defined in `GoogleTilesService`. By using that methodology, it is easy to control the overall size and visible detail of the image, while also controlling the number of network requests the user must wait to complete before they may view the panorama in the headset. The following steps are taken to calculate that zoom level:

1. Calculate the number of tiles needed to fetch the full resolution panorama image using the image and tile width/height values in the metadata.
2. Calculate the zoom level needed to fetch the full resolution panorama image by using the approximate field of view values for each zoom level [here](https://developers.google.com/maps/documentation/tile/streetview#street_view_image_tiles).
3. Working backwards from that full resolution zoom level, decrease our calculated zoom level until the number of network requests required to fetch all tiles which compose the entire image at that zoom level is less than or equal to our threshold value `MAX_TILE_FETCHES_PER_PANO`.

> Note that the panorama images available from this API come from two sources: Google, and public user-generated content. Additionally, the panorama images can vary in size. Care was taken in this application to account for those different sizes by using the size values in the metadata determine how the image tiles are fetched and stitched together.

## Fetching and Combine the Tiles

After the zoom level has been determined, the service concurrently fetches all of the tile images using tile coordinates and the zoom level. This implementation uses [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) and async/await to accomplish asynchronous execution, and the [OkHttp library](https://square.github.io/okhttp/) and `BitmapFactory.decodeStream` function to fetch and create the Bitmap objects.

```kotlin
val numTotalTiles: Int = numTilesX * numTilesY

Log.d(TAG, "Begin fetching $numTotalTiles image tiles at zoom $zoom")

// fetch all of the tiles

val tilesFetches = (0 until numTotalTiles).map { i ->
    val x = i % numTilesX
    val y = i / numTilesX
    async(Dispatchers.IO) {
        getTileImage(metadata.panoId, x, y, zoom)
    }
}
val tiles = tilesFetches.mapNotNull { it.await() }

if (tiles.size < numTilesX * numTilesY) {
    throw Exception("Failed to get all tile images")
}
```

After all of the tiles have been fetched, this implementation iterates through the list of tiles and copies their content to a combined Bitmap object.

```kotlin
val combinedBitmap =
    Bitmap.createBitmap(fullWidth, fullHeight, Bitmap.Config.ARGB_8888)
val canvas = Canvas(combinedBitmap)

var src: Rect
var dst: Rect
for (y in 0 until numTilesY) {
    for (x in 0 until numTilesX) {
        // calculate the right and bottom boundaries for partial tiles
        val rightEdge = minOf((x + 1) * tileWidth, fullWidth)
        val bottomEdge = minOf((y + 1) * tileHeight, fullHeight)

        // skip tiles outside the bounds of the combined bitmap
        if (x * tileWidth >= fullWidth || y * tileHeight >= fullHeight) {
            continue
        }

        val tileIdx = y * numTilesX + x
        val tile = tiles[tileIdx]

        // now calculate the source and destination rects, only drawing the visible portion
        dst = Rect(x * tileWidth, y * tileHeight, rightEdge, bottomEdge)
        src = Rect(0, 0, dst.width(), dst.height())

        Log.d(
            TAG,
            "draw src rect $src from tile[$tileIdx] to combined bitmap at dst rect $dst"
        )

        canvas.drawBitmap(tile, src, dst, null)
    }
}
```

## Displaying the Panorama

With the combined Bitmap object now returned from the `GoogleTilesService` object, we can now display the 360 degree panorama in the heaset by setting the Spatial SDK skybox Entity's mesh material albedo texture to the Bitmap, and set the Entity to be visible using the Visible component.

```kotlin
skyboxEntity.setComponent(Visible(false))

GoogleTilesService.getPanoramaBitmapFor(
    panoData,
    object : IPanoramaServiceHandler {
        override fun onFinished(bitmap: Bitmap) {
            if (!skyboxEntity.hasComponent<Mesh>() ||
                !skyboxEntity.hasComponent<Material>()
            ) {
                // handle skybox entity missing mesh or material
                return
            }

            val sceneMaterial = skyboxSceneObject!!.mesh?.materials?.get(0)
            if (sceneMaterial == null) {
                // handle skybox scene object mesh material not found
                return
            }

            // destroy our old skybox texture
            sceneMaterial.texture?.destroy()

            // set our new texture
            val sceneTexture = SceneTexture(bitmap)
            sceneMaterial.setAlbedoTexture(sceneTexture)

            skyboxEntity.setComponent(Visible(true))
        }

        override fun onError(reason: String) {
            // handle panorama fetch error
        }
    }
)
```

Note that in this application, the skybox entity is defined in the scene.xml file, and starts not visible until the user drops a pin on the globe during the Explore play mode:

```xml
<ac:Entity id="@integer/skybox_id">
    <ac:com.meta.aether.toolkit.Mesh src="mesh://skybox" />
    <ac:com.meta.aether.toolkit.Material unlit="true" />
    <ac:com.meta.aether.toolkit.Transform />
    <ac:com.meta.aether.toolkit.Visible isVisible="false" />
</ac:Entity>
```

## Balancing Cost & Quality

In addition to the number of network requests needed to construct a 360 bitmap of the panorama, there are also cost implications which should be considered when choosing a zoom/quality level to use in your application. Each tile image request [has a price](https://developers.google.com/maps/billing-and-pricing/pricing#streetview), which can quickly add up if you are viewing many high-resolution panoramas. Additionally, each Google project has a [daily quota](https://developers.google.com/maps/documentation/tile/usage-and-billing#other-usage-limits) for the maximum number of image tile requests, after which the API will become inaccessible for the remainder of that day. For this application, a relatively high threshold on the number of network requests was set in order to fetch higher-resolution images, at an increased cost.

Though out-of-scope for this application, a technique of fetching higher zoom level images where the user is facing could be used to reduce costs. The metadata for each panorama includes the heading, tilt, and roll information of the panorama. If your application, such as this one, is built for a headset, you could feasibly fetch higher quality images that are within the virtual camera's field-of-view, and lower quality images outside of the field of view. When the user turns their head, you recalculate the tiles within the field of view, and replace any of the lower resolution tiles with fetched higher resolution ones. You can see this technique in-action if you open a panorama image in street view on [google.com/maps](https://www.google.com/maps), and quickly pan the camera left or right.
