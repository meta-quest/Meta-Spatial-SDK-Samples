package com.meta.pixelandtexel.scanner.utils

import com.meta.spatial.core.Entity
import com.meta.spatial.core.SystemManager
import com.meta.spatial.toolkit.PlayerBodyAttachmentSystem

fun getRightController(systemManager: SystemManager): Entity? {
    return systemManager
        .tryFindSystem<PlayerBodyAttachmentSystem>()
        ?.tryGetLocalPlayerAvatarBody()
        ?.rightHand
}