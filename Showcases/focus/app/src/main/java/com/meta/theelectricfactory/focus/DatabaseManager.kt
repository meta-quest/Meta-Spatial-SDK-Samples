// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.meta.spatial.core.Pose

// Class to create and update Focus database
class DatabaseManager(ctx: Context) : SQLiteOpenHelper(ctx, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        public const val DATABASE_NAME = "focusapp.db"
        // Database version needs to be incremented each time you make a change in the database
        // structure
        public const val DATABASE_VERSION = 10

        // Projects table
        public const val PROJECTS_TABLE = "project"
        public const val PROJECT_ID = "id"
        public const val PROJECT_UUID = "uuid"
        public const val PROJECT_NAME = "name"
        public const val PROJECT_MR = "mr"
        public const val PROJECT_ENVIRONMENT = "environment"
        public const val PROJECT_LAST_OPENING = "last_opening"

        // Unique assets table (TasksPanel, AIExchangePanel, Clock, Speaker)
        public const val UNIQUE_ASSETS_TABLE = "unique_asset"
        public const val UNIQUE_ASSET_ID = "id"
        public const val UNIQUE_ASSET_UUID = "uuid"
        public const val UNIQUE_ASSET_PROJECT_UUID = "project_uuid"
        public const val UNIQUE_ASSET_TYPE = "type"
        public const val UNIQUE_ASSET_STATE = "state"
        public const val UNIQUE_ASSET_POSITION_X = "position_x"
        public const val UNIQUE_ASSET_POSITION_Y = "position_y"
        public const val UNIQUE_ASSET_POSITION_Z = "position_z"
        public const val UNIQUE_ASSET_ROTATION_W = "rotation_w"
        public const val UNIQUE_ASSET_ROTATION_X = "rotation_x"
        public const val UNIQUE_ASSET_ROTATION_Y = "rotation_y"
        public const val UNIQUE_ASSET_ROTATION_Z = "rotation_z"

        // Tool assets table (Stickers, Labels, Boards, WebView, Arrows, Shapes)
        public const val TOOLS_TABLE = "tool"
        public const val TOOL_ID = "id"
        public const val TOOL_UUID = "uuid"
        public const val TOOL_PROJECT_UUID = "project_uuid"
        public const val TOOL_TYPE = "type"
        public const val TOOL_SOURCE = "source"
        public const val TOOL_SIZE = "size"
        public const val TOOL_DELETE_HEIGHT = "delete_height"
        public const val TOOL_POSITION_X = "position_x"
        public const val TOOL_POSITION_Y = "position_y"
        public const val TOOL_POSITION_Z = "position_z"
        public const val TOOL_ROTATION_W = "rotation_w"
        public const val TOOL_ROTATION_X = "rotation_x"
        public const val TOOL_ROTATION_Y = "rotation_y"
        public const val TOOL_ROTATION_Z = "rotation_z"

        // Sticky Notes table
        public const val STICKIES_TABLE = "sticky"
        public const val STICKY_ID = "id"
        public const val STICKY_UUID = "uuid"
        public const val STICKY_PROJECT_UUID = "project_uuid"
        public const val STICKY_MESSAGE = "message"
        public const val STICKY_COLOR = "color"
        public const val STICKY_POSITION_X = "position_x"
        public const val STICKY_POSITION_Y = "position_y"
        public const val STICKY_POSITION_Z = "position_z"
        public const val STICKY_ROTATION_W = "rotation_w"
        public const val STICKY_ROTATION_X = "rotation_x"
        public const val STICKY_ROTATION_Y = "rotation_y"
        public const val STICKY_ROTATION_Z = "rotation_z"

        // Tasks table
        public const val TASKS_TABLE = "task"
        public const val TASK_ID = "id"
        public const val TASK_UUID = "uuid"
        public const val TASK_PROJECT_UUID = "project_uuid"
        public const val TASK_TITLE = "title"
        public const val TASK_BODY = "body"
        public const val TASK_STATE = "state"
        public const val TASK_PRIORITY = "priority"
        public const val TASK_DETACH = "detach"
        public const val TASK_POSITION_X = "position_x"
        public const val TASK_POSITION_Y = "position_y"
        public const val TASK_POSITION_Z = "position_z"
        public const val TASK_ROTATION_W = "rotation_w"
        public const val TASK_ROTATION_X = "rotation_x"
        public const val TASK_ROTATION_Y = "rotation_y"
        public const val TASK_ROTATION_Z = "rotation_z"
    }

    // Creation of database
    override fun onCreate(db: SQLiteDatabase?) {
        Log.i("Focus", "Focus> onCreate DB")

        db?.execSQL(
            "CREATE TABLE $PROJECTS_TABLE (" +
                    "$PROJECT_ID INTEGER PRIMARY KEY, " +
                    "$PROJECT_UUID INTEGER, " +
                    "$PROJECT_NAME TEXT, " +
                    "$PROJECT_MR INTEGER, " +
                    "$PROJECT_ENVIRONMENT INTEGER, " +
                    "$PROJECT_LAST_OPENING TEXT)")

        db?.execSQL(
            "CREATE TABLE $UNIQUE_ASSETS_TABLE (" +
                    "$UNIQUE_ASSET_ID INTEGER PRIMARY KEY, " +
                    "$UNIQUE_ASSET_UUID INTEGER, " +
                    "$UNIQUE_ASSET_PROJECT_UUID INTEGER, " +
                    "$UNIQUE_ASSET_TYPE TEXT, " +
                    "$UNIQUE_ASSET_STATE INTEGER, " +
                    "$UNIQUE_ASSET_POSITION_X FLOAT, " +
                    "$UNIQUE_ASSET_POSITION_Y FLOAT, " +
                    "$UNIQUE_ASSET_POSITION_Z FLOAT, " +
                    "$UNIQUE_ASSET_ROTATION_W FLOAT," +
                    "$UNIQUE_ASSET_ROTATION_X FLOAT," +
                    "$UNIQUE_ASSET_ROTATION_Y FLOAT," +
                    "$UNIQUE_ASSET_ROTATION_Z FLOAT)")

        db?.execSQL(
            "CREATE TABLE $TOOLS_TABLE (" +
                    "$TOOL_ID INTEGER PRIMARY KEY, " +
                    "$TOOL_UUID INTEGER, " +
                    "$TOOL_PROJECT_UUID INTEGER, " +
                    "$TOOL_TYPE TEXT, " +
                    "$TOOL_SOURCE TEXT, " +
                    "$TOOL_SIZE FLOAT, " +
                    "$TOOL_DELETE_HEIGHT FLOAT, " +
                    "$TOOL_POSITION_X FLOAT, " +
                    "$TOOL_POSITION_Y FLOAT, " +
                    "$TOOL_POSITION_Z FLOAT, " +
                    "$TOOL_ROTATION_W FLOAT," +
                    "$TOOL_ROTATION_X FLOAT," +
                    "$TOOL_ROTATION_Y FLOAT," +
                    "$TOOL_ROTATION_Z FLOAT)")

        db?.execSQL(
            "CREATE TABLE $STICKIES_TABLE (" +
                    "$STICKY_ID INTEGER PRIMARY KEY, " +
                    "$STICKY_UUID INTEGER, " +
                    "$STICKY_PROJECT_UUID INTEGER, " +
                    "$STICKY_MESSAGE TEXT, " +
                    "$STICKY_COLOR TEXT, " +
                    "$STICKY_POSITION_X FLOAT, " +
                    "$STICKY_POSITION_Y FLOAT, " +
                    "$STICKY_POSITION_Z FLOAT, " +
                    "$STICKY_ROTATION_W FLOAT," +
                    "$STICKY_ROTATION_X FLOAT," +
                    "$STICKY_ROTATION_Y FLOAT," +
                    "$STICKY_ROTATION_Z FLOAT)")

        db?.execSQL(
            "CREATE TABLE $TASKS_TABLE (" +
                    "$TASK_ID INTEGER PRIMARY KEY, " +
                    "$TASK_UUID INTEGER, " +
                    "$TASK_PROJECT_UUID INTEGER, " +
                    "$TASK_TITLE TEXT, " +
                    "$TASK_BODY TEXT, " +
                    "$TASK_STATE INTEGER, " +
                    "$TASK_PRIORITY INTEGER, " +
                    "$TASK_DETACH INTEGER, " +
                    "$TASK_POSITION_X FLOAT, " +
                    "$TASK_POSITION_Y FLOAT, " +
                    "$TASK_POSITION_Z FLOAT, " +
                    "$TASK_ROTATION_W FLOAT," +
                    "$TASK_ROTATION_X FLOAT," +
                    "$TASK_ROTATION_Y FLOAT," +
                    "$TASK_ROTATION_Z FLOAT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.i("Focus", "Focus> onUpgrade DB")
        db?.execSQL("DROP TABLE IF EXISTS $PROJECTS_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $UNIQUE_ASSETS_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $TOOLS_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $STICKIES_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $TASKS_TABLE")
        onCreate(db)
    }

    // Manage Projects data
    fun createProject(project: Project) {
        val db = writableDatabase
        val values =
            ContentValues().apply {
                put(PROJECT_UUID, project.uuid)
                put(PROJECT_NAME, project.name)
                put(PROJECT_MR, if (project.MR) 1 else 0)
                put(PROJECT_ENVIRONMENT, project.environment)
            }
        db.insert(PROJECTS_TABLE, null, values)
        db.close()
    }

    fun getProjects(): Cursor {
        val db = readableDatabase
        val readDataQuery = "Select * from $PROJECTS_TABLE"
        return db.rawQuery(readDataQuery, null)
    }

    fun getProject(uuid: Int): Cursor {
        val db = readableDatabase
        val readDataQuery = "Select * from $PROJECTS_TABLE where $PROJECT_UUID is $uuid"
        return db.rawQuery(readDataQuery, null)
    }

    fun updateProject(project: Project?) {
        val db = writableDatabase
        val values =
            ContentValues().apply {
                put(PROJECT_NAME, project?.name)
                put(PROJECT_MR, project?.MR)
                put(PROJECT_ENVIRONMENT, project?.environment)
            }
        db.update(PROJECTS_TABLE, values, "$PROJECT_UUID=?", arrayOf(project?.uuid.toString()))
        db.close()
        updateLastTimeOpen()
    }

    fun updateLastTimeOpen() {
        // We save last time the user interacted with elements in the project to show this info in the
        // Home Panel
        if (ImmersiveActivity.instance.get()?.currentProject == null) return
        val db = writableDatabase
        val values =
            ContentValues().apply { put(PROJECT_LAST_OPENING, System.currentTimeMillis().toString()) }
        db.update(
            PROJECTS_TABLE,
            values,
            "$PROJECT_UUID=?",
            arrayOf(ImmersiveActivity.instance.get()?.currentProject?.uuid.toString()))
        db.close()
    }

    fun deleteProject(uuid: Int?) {
        val db = writableDatabase
        db.delete(PROJECTS_TABLE, "$PROJECT_UUID=?", arrayOf(uuid.toString()))
        db.delete(UNIQUE_ASSETS_TABLE, "$UNIQUE_ASSET_PROJECT_UUID=?", arrayOf(uuid.toString()))
        db.delete(TOOLS_TABLE, "$TOOL_PROJECT_UUID=?", arrayOf(uuid.toString()))
        db.delete(STICKIES_TABLE, "$STICKY_PROJECT_UUID=?", arrayOf(uuid.toString()))
        db.delete(TASKS_TABLE, "$TASK_PROJECT_UUID=?", arrayOf(uuid.toString()))
        db.close()
    }

    // Manage Unique Assets data
    fun createUniqueAsset(
        uuid: Int?,
        projectUUID: Int?,
        type: AssetType,
        state: Boolean,
        position: Pose
    ) {
        val db = writableDatabase
        val values =
            ContentValues().apply {
                put(UNIQUE_ASSET_UUID, uuid)
                put(UNIQUE_ASSET_PROJECT_UUID, projectUUID)
                put(UNIQUE_ASSET_TYPE, type.name)
                put(UNIQUE_ASSET_STATE, if (state) 1 else 0)
                put(UNIQUE_ASSET_POSITION_X, position.t.x)
                put(UNIQUE_ASSET_POSITION_Y, position.t.y)
                put(UNIQUE_ASSET_POSITION_Z, position.t.z)
                put(UNIQUE_ASSET_ROTATION_W, position.q.w)
                put(UNIQUE_ASSET_ROTATION_X, position.q.x)
                put(UNIQUE_ASSET_ROTATION_Y, position.q.y)
                put(UNIQUE_ASSET_ROTATION_Z, position.q.z)
            }
        db.insert(UNIQUE_ASSETS_TABLE, null, values)
        db.close()
        updateLastTimeOpen()
    }

    fun getUniqueAssets(uuid: Int?): Cursor {
        val db = readableDatabase
        val readDataQuery =
            "Select * from $UNIQUE_ASSETS_TABLE where $UNIQUE_ASSET_PROJECT_UUID is $uuid"
        return db.rawQuery(readDataQuery, null)
    }

    fun updateUniqueAsset(uuid: Int, pose: Pose? = null, state: Boolean? = null) {
        val db = writableDatabase
        val values =
            ContentValues().apply {
                if (state != null) put(UNIQUE_ASSET_STATE, if (state) 1 else 0)
                if (pose != null) {
                    put(UNIQUE_ASSET_POSITION_X, pose.t.x)
                    put(UNIQUE_ASSET_POSITION_Y, pose.t.y)
                    put(UNIQUE_ASSET_POSITION_Z, pose.t.z)
                    put(UNIQUE_ASSET_ROTATION_W, pose.q.w)
                    put(UNIQUE_ASSET_ROTATION_X, pose.q.x)
                    put(UNIQUE_ASSET_ROTATION_Y, pose.q.y)
                    put(UNIQUE_ASSET_ROTATION_Z, pose.q.z)
                }
            }
        db.update(UNIQUE_ASSETS_TABLE, values, "${UNIQUE_ASSET_UUID}=?", arrayOf(uuid.toString()))
        db.close()
        updateLastTimeOpen()
    }

    // Manage Tool Assets data
    fun createToolAsset(
        uuid: Int?,
        projectUUID: Int?,
        type: AssetType?,
        source: String,
        size: Float,
        deleteHeight: Float,
        position: Pose
    ) {
        val db = writableDatabase
        val values =
            ContentValues().apply {
                put(TOOL_UUID, uuid)
                put(TOOL_PROJECT_UUID, projectUUID)
                put(TOOL_TYPE, type?.name)
                put(TOOL_SOURCE, source)
                put(TOOL_SIZE, size)
                put(TOOL_DELETE_HEIGHT, deleteHeight)
                put(TOOL_POSITION_X, position.t.x)
                put(TOOL_POSITION_Y, position.t.y)
                put(TOOL_POSITION_Z, position.t.z)
                put(TOOL_ROTATION_W, position.q.w)
                put(TOOL_ROTATION_X, position.q.x)
                put(TOOL_ROTATION_Y, position.q.y)
                put(TOOL_ROTATION_Z, position.q.z)
            }
        db.insert(TOOLS_TABLE, null, values)
        db.close()
        updateLastTimeOpen()
    }

    fun getToolAssets(uuid: Int?): Cursor {
        val db = readableDatabase
        val readDataQuery = "Select * from $TOOLS_TABLE where $TOOL_PROJECT_UUID is $uuid"
        return db.rawQuery(readDataQuery, null)
    }

    fun updateAssetPose(uuid: Int, type: AssetType, pose: Pose) {
        val db = writableDatabase
        var values = ContentValues()
        var table = ""
        var _uuid = ""

        when (type) {
            AssetType.STICKY_NOTE -> {
                table = STICKIES_TABLE
                _uuid = STICKY_UUID
                values =
                    ContentValues().apply {
                        put(STICKY_POSITION_X, pose.t.x)
                        put(STICKY_POSITION_Y, pose.t.y)
                        put(STICKY_POSITION_Z, pose.t.z)
                        put(STICKY_ROTATION_W, pose.q.w)
                        put(STICKY_ROTATION_X, pose.q.x)
                        put(STICKY_ROTATION_Y, pose.q.y)
                        put(STICKY_ROTATION_Z, pose.q.z)
                    }
            }
            AssetType.TASK -> {
                table = TASKS_TABLE
                _uuid = TASK_UUID
                values =
                    ContentValues().apply {
                        put(TASK_POSITION_X, pose.t.x)
                        put(TASK_POSITION_Y, pose.t.y)
                        put(TASK_POSITION_Z, pose.t.z)
                        put(TASK_ROTATION_W, pose.q.w)
                        put(TASK_ROTATION_X, pose.q.x)
                        put(TASK_ROTATION_Y, pose.q.y)
                        put(TASK_ROTATION_Z, pose.q.z)
                    }
            }
            else -> {
                table = TOOLS_TABLE
                _uuid = TOOL_UUID
                values =
                    ContentValues().apply {
                        put(TOOL_POSITION_X, pose.t.x)
                        put(TOOL_POSITION_Y, pose.t.y)
                        put(TOOL_POSITION_Z, pose.t.z)
                        put(TOOL_ROTATION_W, pose.q.w)
                        put(TOOL_ROTATION_X, pose.q.x)
                        put(TOOL_ROTATION_Y, pose.q.y)
                        put(TOOL_ROTATION_Z, pose.q.z)
                    }
            }
        }
        db.update(table, values, "${_uuid}=?", arrayOf(uuid.toString()))
        db.close()
        updateLastTimeOpen()
    }

    fun deleteToolAsset(uuid: Int?) {
        val db = writableDatabase
        db.delete(TOOLS_TABLE, "$TOOL_UUID=?", arrayOf(uuid.toString()))
        db.close()
        updateLastTimeOpen()
    }

    // Manage Sticky Notes data
    fun createSticky(
        uuid: Int?,
        projectUUID: Int?,
        color: StickyColor,
        message: String,
        position: Pose
    ) {
        val db = writableDatabase
        val values =
            ContentValues().apply {
                put(STICKY_UUID, uuid)
                put(STICKY_PROJECT_UUID, projectUUID)
                put(STICKY_MESSAGE, message)
                put(STICKY_COLOR, color.name)
                put(STICKY_POSITION_X, position.t.x)
                put(STICKY_POSITION_Y, position.t.y)
                put(STICKY_POSITION_Z, position.t.z)
                put(STICKY_ROTATION_W, position.q.w)
                put(STICKY_ROTATION_X, position.q.x)
                put(STICKY_ROTATION_Y, position.q.y)
                put(STICKY_ROTATION_Z, position.q.z)
            }
        db.insert(STICKIES_TABLE, null, values)
        db.close()
        updateLastTimeOpen()
    }

    fun getStickies(uuid: Int?): Cursor {
        val db = readableDatabase
        val readDataQuery = "Select * from $STICKIES_TABLE where $TOOL_PROJECT_UUID is $uuid"
        return db.rawQuery(readDataQuery, null)
    }

    fun updateStickyMessage(uuid: Int?, message: String) {
        val db = writableDatabase
        val values = ContentValues().apply { put(STICKY_MESSAGE, message) }
        db.update(STICKIES_TABLE, values, "${STICKY_UUID}=?", arrayOf(uuid.toString()))
        db.close()
        updateLastTimeOpen()
    }

    fun deleteSticky(uuid: Int?) {
        val db = writableDatabase
        db.delete(STICKIES_TABLE, "$STICKY_UUID=?", arrayOf(uuid.toString()))
        db.close()
        updateLastTimeOpen()
    }

    // Manage Tasks data
    fun createTask(
        uuid: Int?,
        projectUUID: Int?,
        title: String,
        body: String,
        state: Int,
        priority: Int
    ) {
        val db = writableDatabase
        val values =
            ContentValues().apply {
                put(TASK_UUID, uuid)
                put(TASK_PROJECT_UUID, projectUUID)
                put(TASK_TITLE, title)
                put(TASK_BODY, body)
                put(TASK_STATE, state)
                put(TASK_PRIORITY, priority)
                put(TASK_DETACH, 0)
            }
        db.insert(TASKS_TABLE, null, values)
        db.close()
        updateLastTimeOpen()
    }

    fun getTasks(uuid: Int?): Cursor {
        val db = readableDatabase
        val readDataQuery = "Select * from $TASKS_TABLE where $TASK_PROJECT_UUID is $uuid"
        return db.rawQuery(readDataQuery, null)
    }

    fun getTaskData(uuid: Int?): Cursor {
        val db = readableDatabase
        val readDataQuery = "Select * from $TASKS_TABLE where $TASK_UUID is $uuid"
        return db.rawQuery(readDataQuery, null)
    }

    fun updateTaskData(
        uuid: Int?,
        title: String? = null,
        body: String? = null,
        state: Int? = null,
        priority: Int? = null,
        detach: Int? = null
    ) {
        val db = writableDatabase
        val values =
            ContentValues().apply {
                if (title != null) put(TASK_TITLE, title)
                if (body != null) put(TASK_BODY, body)
                if (state != null) put(TASK_STATE, state)
                if (priority != null) put(TASK_PRIORITY, priority)
                if (detach != null) put(TASK_DETACH, detach)
            }
        db.update(TASKS_TABLE, values, "${TASK_UUID}=?", arrayOf(uuid.toString()))
        db.close()
        updateLastTimeOpen()
    }

    fun deleteTask(uuid: Int?) {
        val db = writableDatabase
        db.delete(TASKS_TABLE, "$TASK_UUID=?", arrayOf(uuid.toString()))
        db.close()
        updateLastTimeOpen()
    }

    fun updateWebViewURL(uuid: Int?, adress: String) {
        val db = writableDatabase
        val values = ContentValues().apply { put(TOOL_SOURCE, adress) }
        db.update(TOOLS_TABLE, values, "${TOOL_UUID}=?", arrayOf(uuid.toString()))
        db.close()
        updateLastTimeOpen()
    }
}
