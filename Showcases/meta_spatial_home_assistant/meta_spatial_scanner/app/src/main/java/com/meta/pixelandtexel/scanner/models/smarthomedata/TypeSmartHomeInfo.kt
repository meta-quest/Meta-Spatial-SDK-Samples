package com.meta.pixelandtexel.scanner.models.smarthomedata

enum class TypeSmartHomeInfo {
    LIGHT,
    PLUG,
    UNKNOWN
}

fun getEnumFromString(type: String): TypeSmartHomeInfo {
    if (lightStringList.any { it.equals(type, ignoreCase = true) }) {
        return TypeSmartHomeInfo.LIGHT
    }
    if (plugStringList.any { it.equals(type, ignoreCase = true) }) {
        return TypeSmartHomeInfo.PLUG
    }

    return TypeSmartHomeInfo.UNKNOWN
}


val lightStringList = listOf(
    "light",
    "lamp",
    "bulb",
    "ceiling light",
    "floor lamp",
    "table lamp",
    "chandelier",
    "sconce",
    "spotlight",
    "downlight"
)

val plugStringList = listOf(
    "plug",
    "power outlet",
    "socket",
    "electrical outlet",
    "wall socket",
    "power point",
    "extension cord",
    "power strip",
    "switch",
)