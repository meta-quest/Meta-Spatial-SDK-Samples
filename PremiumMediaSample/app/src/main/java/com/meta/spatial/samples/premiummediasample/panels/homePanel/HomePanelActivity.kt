// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.panels.homePanel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.meta.spatial.samples.premiummediasample.data.HomeItem
import com.meta.spatial.samples.premiummediasample.immersive.ImmersiveActivity.Companion.ImmersiveActivityCodes
import com.meta.spatial.samples.premiummediasample.service.IPCService
import com.meta.spatial.samples.premiummediasample.service.IPCServiceConnection

class HomePanelActivity : ComponentActivity() {
  val homePanelViewModel = HomePanelViewModel()

  // Home Panel doesn't receive any IPC messages
  private val ipcServiceConnection: IPCServiceConnection = IPCServiceConnection(this, null, null)

  override fun onCreate(savedInstanceBundle: Bundle?) {
    super.onCreate(savedInstanceBundle)
    ipcServiceConnection.bindService()

    homePanelViewModel.onItemSelectedHandler = { homeItem: HomeItem ->
      ipcServiceConnection.messageProcess(
          IPCService.IMMERSIVE_CHANNEL,
          ImmersiveActivityCodes.HOME_PANEL_SELECT_ITEM.ordinal,
          Bundle().apply { putSerializable("homeItem", homeItem) })
    }

    setContent { HomeView(homePanelViewModel) }
  }

  override fun reportFullyDrawn() {
    super.reportFullyDrawn()
    ipcServiceConnection.messageService(IPCService.NOTIFY_HOME_PANEL_DRAWN)
  }

  override fun onDestroy() {
    ipcServiceConnection.unbindService()
    super.onDestroy()
  }
}
