## Media View Media Handling

Media View provides robust media handling capabilities, leveraging the Android MediaStore for accessing, managing, and storing media files on the device. This document dives into the details of how Media View interacts with the MediaStore, detects different media types, and manages sample media assets.

### MediaStore Interaction

Media View uses the Android MediaStore to access and manage media files on the user's device. The `data/gallery` module encapsulates the logic for MediaStore interaction, providing a clean separation of concerns.

- **Querying Media:**
    - The `MediaStoreQueryBuilder` class constructs queries for retrieving media files based on specified filters (`MediaFilter`) and sort options (`MediaSortBy`).
    - The `buildSelection` method dynamically generates selection clauses for the MediaStore query, allowing for flexible filtering based on media types and other criteria.
    - The `buildSortOrder` method constructs the sort order clause based on the selected `MediaSortBy` option, allowing users to organize their media by date, size, or name.

- **Media Type Detection:**
    - The `MediaStoreFileDto` class represents a media file retrieved from the MediaStore. It includes logic to determine the type of media based on file properties like MIME type, aspect ratio, dimensions, and bitrate.
    - Methods like `isPanorama`, `isRayban`, `is360`, and `isSpatial` use heuristics to categorize media files. For example, panoramas are identified based on their aspect ratio exceeding a certain threshold, while 360-degree media are detected based on a 2:1 aspect ratio.
    - Based on these detection methods, the `mediaType` and `mediaFilter` properties of `MediaStoreFileDto` are set, facilitating filtering and presentation in the UI.

- **Saving Media:**
    - The `DeviceGalleryService` class handles the process of saving new media files to the device. The `saveMediaFile` method utilizes `ContentValues` to create a new entry in the MediaStore, specifying properties like display name, MIME type, and relative path.  It takes a `storageType` (`StorageType` enum) to specify the type of media being saved.
    - The `saveMediaFile` method utilizes a lambda function (`onWrite`) to handle the actual writing of data to the file using a `FileOutputStream`. The `IS_PENDING` flag is used to indicate that the file is still being written. Once the write is complete, the flag is cleared, signifying that the file is available.

### Sample Media Management

Media View includes sample media assets that can be saved to the user's device, providing an initial set of content to explore. The `data/user` module is responsible for managing user preferences, including the tracking of whether sample media has been saved.

- **User Preferences:**
    - The `UserRepository` class provides a centralized way to access and modify user preferences.
    - The `UserPreferencesService` interacts with `SharedPreferences` to store and retrieve preferences. The `isSampleMediaSaved` method checks whether the user has previously saved the sample media.

- **Loading Sample Assets:**
    - The `PermissionViewModel` handles the process of loading sample assets from the `assets` folder. This occurs after the user grants storage permissions.
    - Before saving assets, the code checks if a sample media folder exists from a previous installation. If found, it is deleted to avoid conflicts or duplicates.
    - The `saveAssetDirectory` and `saveAssetFile` methods recursively iterate through the sample assets in the `assets` folder, saving each file to the device using the `DeviceGalleryService`.  The `setSampleMediaSaved` method in `UserRepository` is called after successful asset saving to update the user's preference.

### Code Examples

**MediaStore Query Builder (`MediaStoreQueryBuilder`):**

```kotlin
fun buildSelection(filter: MediaFilter?): Pair<String?, Array<String>?> {
    return Pair(
        "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?",
        arrayOf(
            "${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE}",
            "${MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO}"
        ),
    ) // ... Add support for other MediaFilters
}
```

**Media Type Detection (`MediaStoreFileDto`):**

```kotlin
private fun isPanorama(): Boolean {
    return aspectRatio?.let { it > MediaType.panoramaAspectRatioMin } ?: false
}
```

**Saving Media (`DeviceGalleryService`):**

```kotlin
suspend fun saveMediaFile(
    displayFileName: String?,
    mimeType: String?,
    relativeSubPath: String?,
    storageType: StorageType,
    onWrite: (FileOutputStream) -> Unit
): Boolean {
    // ... ContentValues setup and MediaStore insertion
    contentResolver.openFileDescriptor(mediaUri, "w", null).use { file ->
        file?.let {
            val fos = FileOutputStream(it.fileDescriptor)
            fos.use {
                onWrite(fos) // Write data using the provided lambda
            }
        }
    }
    // ... Update MediaStore entry to clear IS_PENDING flag
}
```

**Checking for Sample Media (`UserRepository`):**

```kotlin
fun isSampleMediaSaved(): Boolean {
    return userPreferencesService.isSampleMediaSaved()
}
```

By combining the robust capabilities of the Android MediaStore with custom logic for media type detection and sample asset management, Media View delivers a comprehensive media handling experience. This approach ensures a seamless workflow for accessing, managing, and presenting diverse media content within the immersive mixed reality environment.
