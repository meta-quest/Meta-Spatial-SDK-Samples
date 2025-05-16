// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.uiset.app.birdseye

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.levinriegner.uiset.app.util.view.PanelScaffold
import com.meta.spatial.uiset.R
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.Add
import com.meta.spatial.uiset.theme.icons.regular.Apps
import com.meta.spatial.uiset.theme.icons.regular.ArrowDown
import com.meta.spatial.uiset.theme.icons.regular.ArrowDownCircle
import com.meta.spatial.uiset.theme.icons.regular.ArrowLeft
import com.meta.spatial.uiset.theme.icons.regular.ArrowLeftCircle
import com.meta.spatial.uiset.theme.icons.regular.ArrowRight
import com.meta.spatial.uiset.theme.icons.regular.ArrowRightCircle
import com.meta.spatial.uiset.theme.icons.regular.ArrowUp
import com.meta.spatial.uiset.theme.icons.regular.ArrowUpCircle
import com.meta.spatial.uiset.theme.icons.regular.ArrowsLeftRight
import com.meta.spatial.uiset.theme.icons.regular.ArrowsUpDown
import com.meta.spatial.uiset.theme.icons.regular.Bluetooth
import com.meta.spatial.uiset.theme.icons.regular.Bookmark
import com.meta.spatial.uiset.theme.icons.regular.BookmarkAdd
import com.meta.spatial.uiset.theme.icons.regular.BrightnessLow
import com.meta.spatial.uiset.theme.icons.regular.BrightnessMid
import com.meta.spatial.uiset.theme.icons.regular.BrightnessOff
import com.meta.spatial.uiset.theme.icons.regular.BrightnessOn
import com.meta.spatial.uiset.theme.icons.regular.BtA
import com.meta.spatial.uiset.theme.icons.regular.BtB
import com.meta.spatial.uiset.theme.icons.regular.BtMenu
import com.meta.spatial.uiset.theme.icons.regular.BtOculus
import com.meta.spatial.uiset.theme.icons.regular.BtX
import com.meta.spatial.uiset.theme.icons.regular.BtY
import com.meta.spatial.uiset.theme.icons.regular.BulletList
import com.meta.spatial.uiset.theme.icons.regular.CameraRoll
import com.meta.spatial.uiset.theme.icons.regular.CategoryAll
import com.meta.spatial.uiset.theme.icons.regular.Chat
import com.meta.spatial.uiset.theme.icons.regular.ChatEllipses
import com.meta.spatial.uiset.theme.icons.regular.ChatOff
import com.meta.spatial.uiset.theme.icons.regular.ChatText
import com.meta.spatial.uiset.theme.icons.regular.CheckAlt
import com.meta.spatial.uiset.theme.icons.regular.CheckCircle
import com.meta.spatial.uiset.theme.icons.regular.CheckboxCircle
import com.meta.spatial.uiset.theme.icons.regular.ChevronDown
import com.meta.spatial.uiset.theme.icons.regular.ChevronLeft
import com.meta.spatial.uiset.theme.icons.regular.ChevronRight
import com.meta.spatial.uiset.theme.icons.regular.ChevronUp
import com.meta.spatial.uiset.theme.icons.regular.Close
import com.meta.spatial.uiset.theme.icons.regular.CloseCircle
import com.meta.spatial.uiset.theme.icons.regular.ClosedCaptioning
import com.meta.spatial.uiset.theme.icons.regular.ClosedCaptioningOff
import com.meta.spatial.uiset.theme.icons.regular.Clothing
import com.meta.spatial.uiset.theme.icons.regular.Cloud
import com.meta.spatial.uiset.theme.icons.regular.Color
import com.meta.spatial.uiset.theme.icons.regular.Comfortable
import com.meta.spatial.uiset.theme.icons.regular.CommandCenter
import com.meta.spatial.uiset.theme.icons.regular.Compass
import com.meta.spatial.uiset.theme.icons.regular.Compose
import com.meta.spatial.uiset.theme.icons.regular.Computer
import com.meta.spatial.uiset.theme.icons.regular.ConceptsDogfooding
import com.meta.spatial.uiset.theme.icons.regular.Couch
import com.meta.spatial.uiset.theme.icons.regular.CreditCard
import com.meta.spatial.uiset.theme.icons.regular.Desktop
import com.meta.spatial.uiset.theme.icons.regular.DesktopOff
import com.meta.spatial.uiset.theme.icons.regular.Distance
import com.meta.spatial.uiset.theme.icons.regular.DoNotDisturb
import com.meta.spatial.uiset.theme.icons.regular.DoNotDisturbOff
import com.meta.spatial.uiset.theme.icons.regular.Download
import com.meta.spatial.uiset.theme.icons.regular.Environment
import com.meta.spatial.uiset.theme.icons.regular.Error
import com.meta.spatial.uiset.theme.icons.regular.ErrorCircle
import com.meta.spatial.uiset.theme.icons.regular.Events
import com.meta.spatial.uiset.theme.icons.regular.EventsAdd
import com.meta.spatial.uiset.theme.icons.regular.File
import com.meta.spatial.uiset.theme.icons.regular.Filter
import com.meta.spatial.uiset.theme.icons.regular.Flip
import com.meta.spatial.uiset.theme.icons.regular.Folder
import com.meta.spatial.uiset.theme.icons.regular.Frequency
import com.meta.spatial.uiset.theme.icons.regular.FriendsAdd
import com.meta.spatial.uiset.theme.icons.regular.FriendsBlock
import com.meta.spatial.uiset.theme.icons.regular.FriendsExcept
import com.meta.spatial.uiset.theme.icons.regular.FriendsKick
import com.meta.spatial.uiset.theme.icons.regular.FriendsRemove
import com.meta.spatial.uiset.theme.icons.regular.FriendsReport
import com.meta.spatial.uiset.theme.icons.regular.FriendsRequestSent
import com.meta.spatial.uiset.theme.icons.regular.FullScreen
import com.meta.spatial.uiset.theme.icons.regular.FullScreenExit
import com.meta.spatial.uiset.theme.icons.regular.GalleryConnect
import com.meta.spatial.uiset.theme.icons.regular.GalleryFolder
import com.meta.spatial.uiset.theme.icons.regular.GalleryPhone
import com.meta.spatial.uiset.theme.icons.regular.GalleryReady
import com.meta.spatial.uiset.theme.icons.regular.GalleryReconnect
import com.meta.spatial.uiset.theme.icons.regular.Gamepad
import com.meta.spatial.uiset.theme.icons.regular.HandCursor
import com.meta.spatial.uiset.theme.icons.regular.HeadsetCasting
import com.meta.spatial.uiset.theme.icons.regular.HeartOff
import com.meta.spatial.uiset.theme.icons.regular.HeartOn
import com.meta.spatial.uiset.theme.icons.regular.History
import com.meta.spatial.uiset.theme.icons.regular.Home
import com.meta.spatial.uiset.theme.icons.regular.HomeEdit
import com.meta.spatial.uiset.theme.icons.regular.Horizon
import com.meta.spatial.uiset.theme.icons.regular.Ibeam
import com.meta.spatial.uiset.theme.icons.regular.Image
import com.meta.spatial.uiset.theme.icons.regular.Images360
import com.meta.spatial.uiset.theme.icons.regular.Info
import com.meta.spatial.uiset.theme.icons.regular.Intense
import com.meta.spatial.uiset.theme.icons.regular.Internet
import com.meta.spatial.uiset.theme.icons.regular.IntrusionDetection
import com.meta.spatial.uiset.theme.icons.regular.Keyboard
import com.meta.spatial.uiset.theme.icons.regular.KeyboardEnter
import com.meta.spatial.uiset.theme.icons.regular.KeyboardOff
import com.meta.spatial.uiset.theme.icons.regular.KeyboardOn
import com.meta.spatial.uiset.theme.icons.regular.KeyboardSpace
import com.meta.spatial.uiset.theme.icons.regular.LeavePlaces
import com.meta.spatial.uiset.theme.icons.regular.LensAdjustment
import com.meta.spatial.uiset.theme.icons.regular.Library
import com.meta.spatial.uiset.theme.icons.regular.Linkedin
import com.meta.spatial.uiset.theme.icons.regular.ListChecked
import com.meta.spatial.uiset.theme.icons.regular.ListLock
import com.meta.spatial.uiset.theme.icons.regular.ListSort
import com.meta.spatial.uiset.theme.icons.regular.ListView
import com.meta.spatial.uiset.theme.icons.regular.LockOff
import com.meta.spatial.uiset.theme.icons.regular.LockOn
import com.meta.spatial.uiset.theme.icons.regular.Media180
import com.meta.spatial.uiset.theme.icons.regular.Media1803d
import com.meta.spatial.uiset.theme.icons.regular.Media2d
import com.meta.spatial.uiset.theme.icons.regular.Media360
import com.meta.spatial.uiset.theme.icons.regular.Media3603d
import com.meta.spatial.uiset.theme.icons.regular.Media3dHoriz
import com.meta.spatial.uiset.theme.icons.regular.Media3dVert
import com.meta.spatial.uiset.theme.icons.regular.MediaImmersivePhoto
import com.meta.spatial.uiset.theme.icons.regular.MediaImmersiveVideo
import com.meta.spatial.uiset.theme.icons.regular.Messenger
import com.meta.spatial.uiset.theme.icons.regular.MicrophoneOff
import com.meta.spatial.uiset.theme.icons.regular.MicrophoneOn
import com.meta.spatial.uiset.theme.icons.regular.MicrophoneUnavailable
import com.meta.spatial.uiset.theme.icons.regular.Minimize
import com.meta.spatial.uiset.theme.icons.regular.Mobile
import com.meta.spatial.uiset.theme.icons.regular.MobileKeyboard
import com.meta.spatial.uiset.theme.icons.regular.Moderate
import com.meta.spatial.uiset.theme.icons.regular.Molokini
import com.meta.spatial.uiset.theme.icons.regular.MoreHorizontal
import com.meta.spatial.uiset.theme.icons.regular.MoreVertical
import com.meta.spatial.uiset.theme.icons.regular.Move
import com.meta.spatial.uiset.theme.icons.regular.MultiBrowser
import com.meta.spatial.uiset.theme.icons.regular.MyMedia
import com.meta.spatial.uiset.theme.icons.regular.NightMode
import com.meta.spatial.uiset.theme.icons.regular.Notifications
import com.meta.spatial.uiset.theme.icons.regular.NotificationsOff
import com.meta.spatial.uiset.theme.icons.regular.OculusRemote
import com.meta.spatial.uiset.theme.icons.regular.OculusVoice
import com.meta.spatial.uiset.theme.icons.regular.OculusVoiceOff
import com.meta.spatial.uiset.theme.icons.regular.OpenCircle
import com.meta.spatial.uiset.theme.icons.regular.OpenPanel
import com.meta.spatial.uiset.theme.icons.regular.OpenTab
import com.meta.spatial.uiset.theme.icons.regular.Parties
import com.meta.spatial.uiset.theme.icons.regular.Password
import com.meta.spatial.uiset.theme.icons.regular.PasswordVisible
import com.meta.spatial.uiset.theme.icons.regular.Pause
import com.meta.spatial.uiset.theme.icons.regular.PauseCircle
import com.meta.spatial.uiset.theme.icons.regular.Phone
import com.meta.spatial.uiset.theme.icons.regular.PhysicalFeatures
import com.meta.spatial.uiset.theme.icons.regular.Play
import com.meta.spatial.uiset.theme.icons.regular.PlayCircle
import com.meta.spatial.uiset.theme.icons.regular.PlayNext
import com.meta.spatial.uiset.theme.icons.regular.PlayNextCircle
import com.meta.spatial.uiset.theme.icons.regular.PlayPrev
import com.meta.spatial.uiset.theme.icons.regular.PlayPrevCircle
import com.meta.spatial.uiset.theme.icons.regular.Power
import com.meta.spatial.uiset.theme.icons.regular.Privacy
import com.meta.spatial.uiset.theme.icons.regular.Profile
import com.meta.spatial.uiset.theme.icons.regular.ProfileCircle
import com.meta.spatial.uiset.theme.icons.regular.Purchase
import com.meta.spatial.uiset.theme.icons.regular.Question
import com.meta.spatial.uiset.theme.icons.regular.RecentlyPlayed
import com.meta.spatial.uiset.theme.icons.regular.Redo
import com.meta.spatial.uiset.theme.icons.regular.Refresh
import com.meta.spatial.uiset.theme.icons.regular.RefreshCircle
import com.meta.spatial.uiset.theme.icons.regular.Refund
import com.meta.spatial.uiset.theme.icons.regular.RemoveCircle
import com.meta.spatial.uiset.theme.icons.regular.Reorient
import com.meta.spatial.uiset.theme.icons.regular.Replay
import com.meta.spatial.uiset.theme.icons.regular.ReplayCircle
import com.meta.spatial.uiset.theme.icons.regular.ResizeHorizontal
import com.meta.spatial.uiset.theme.icons.regular.ResizeHorizontalDown
import com.meta.spatial.uiset.theme.icons.regular.ResizeVerticalDown
import com.meta.spatial.uiset.theme.icons.regular.ResizeVerticalUp
import com.meta.spatial.uiset.theme.icons.regular.RewardPacks
import com.meta.spatial.uiset.theme.icons.regular.RotateLeft
import com.meta.spatial.uiset.theme.icons.regular.RotateRight
import com.meta.spatial.uiset.theme.icons.regular.Scale
import com.meta.spatial.uiset.theme.icons.regular.ScaleDown
import com.meta.spatial.uiset.theme.icons.regular.Screenshot
import com.meta.spatial.uiset.theme.icons.regular.Search
import com.meta.spatial.uiset.theme.icons.regular.Send
import com.meta.spatial.uiset.theme.icons.regular.Settings
import com.meta.spatial.uiset.theme.icons.regular.SidebarPin
import com.meta.spatial.uiset.theme.icons.regular.SidebarPinClose
import com.meta.spatial.uiset.theme.icons.regular.Sitting
import com.meta.spatial.uiset.theme.icons.regular.Standing
import com.meta.spatial.uiset.theme.icons.regular.Star
import com.meta.spatial.uiset.theme.icons.regular.StarFull
import com.meta.spatial.uiset.theme.icons.regular.StarHalf
import com.meta.spatial.uiset.theme.icons.regular.Stationary
import com.meta.spatial.uiset.theme.icons.regular.Stop
import com.meta.spatial.uiset.theme.icons.regular.StopCircle
import com.meta.spatial.uiset.theme.icons.regular.Storage
import com.meta.spatial.uiset.theme.icons.regular.StorageSd
import com.meta.spatial.uiset.theme.icons.regular.Store
import com.meta.spatial.uiset.theme.icons.regular.StoreCart
import com.meta.spatial.uiset.theme.icons.regular.Sync
import com.meta.spatial.uiset.theme.icons.regular.SyncCircle
import com.meta.spatial.uiset.theme.icons.regular.SyncOff
import com.meta.spatial.uiset.theme.icons.regular.Table
import com.meta.spatial.uiset.theme.icons.regular.Tag
import com.meta.spatial.uiset.theme.icons.regular.Television
import com.meta.spatial.uiset.theme.icons.regular.TenSecondsBackward
import com.meta.spatial.uiset.theme.icons.regular.TenSecondsForward
import com.meta.spatial.uiset.theme.icons.regular.ThirteenPlus
import com.meta.spatial.uiset.theme.icons.regular.ThreePeople
import com.meta.spatial.uiset.theme.icons.regular.ThumbsDown
import com.meta.spatial.uiset.theme.icons.regular.ThumbsUp
import com.meta.spatial.uiset.theme.icons.regular.Time
import com.meta.spatial.uiset.theme.icons.regular.Tips
import com.meta.spatial.uiset.theme.icons.regular.ToTop
import com.meta.spatial.uiset.theme.icons.regular.Touch2Left
import com.meta.spatial.uiset.theme.icons.regular.Touch2Right
import com.meta.spatial.uiset.theme.icons.regular.TouchLeft
import com.meta.spatial.uiset.theme.icons.regular.TouchRight
import com.meta.spatial.uiset.theme.icons.regular.Touchpad
import com.meta.spatial.uiset.theme.icons.regular.Trash
import com.meta.spatial.uiset.theme.icons.regular.Trophy
import com.meta.spatial.uiset.theme.icons.regular.Twitter
import com.meta.spatial.uiset.theme.icons.regular.Undo
import com.meta.spatial.uiset.theme.icons.regular.UniversalMenu
import com.meta.spatial.uiset.theme.icons.regular.UnknownController
import com.meta.spatial.uiset.theme.icons.regular.UnknownHeadset
import com.meta.spatial.uiset.theme.icons.regular.UnknownSources
import com.meta.spatial.uiset.theme.icons.regular.UnlockPattern
import com.meta.spatial.uiset.theme.icons.regular.Unrated
import com.meta.spatial.uiset.theme.icons.regular.Update
import com.meta.spatial.uiset.theme.icons.regular.UppercaseArrow
import com.meta.spatial.uiset.theme.icons.regular.UsbStick
import com.meta.spatial.uiset.theme.icons.regular.Vibration
import com.meta.spatial.uiset.theme.icons.regular.VideoCapture
import com.meta.spatial.uiset.theme.icons.regular.ViewGallery
import com.meta.spatial.uiset.theme.icons.regular.VisitHome
import com.meta.spatial.uiset.theme.icons.regular.VoiceCommand
import com.meta.spatial.uiset.theme.icons.regular.VolumeLow
import com.meta.spatial.uiset.theme.icons.regular.VolumeMid
import com.meta.spatial.uiset.theme.icons.regular.VolumeOff
import com.meta.spatial.uiset.theme.icons.regular.VolumeOn
import com.meta.spatial.uiset.theme.icons.regular.VrObject
import com.meta.spatial.uiset.theme.icons.regular.Wallet
import com.meta.spatial.uiset.theme.icons.regular.Warning
import com.meta.spatial.uiset.theme.icons.regular.WidthExtrawide
import com.meta.spatial.uiset.theme.icons.regular.WidthMedium
import com.meta.spatial.uiset.theme.icons.regular.WidthSmall
import com.meta.spatial.uiset.theme.icons.regular.WifiAltLow
import com.meta.spatial.uiset.theme.icons.regular.WifiAltMid
import com.meta.spatial.uiset.theme.icons.regular.WifiAltOff
import com.meta.spatial.uiset.theme.icons.regular.WifiOff
import com.meta.spatial.uiset.theme.icons.regular.WifiOn
import com.meta.spatial.uiset.theme.icons.regular.WifiSecure
import com.meta.spatial.uiset.theme.icons.regular.Workplace
import com.meta.spatial.uiset.theme.icons.regular.World
import com.meta.spatial.uiset.theme.icons.regular.Zoom

@Composable
fun BirdseyeIconLibrary() {

  val iconList: Array<Array<Map<String, Any>>> =
      arrayOf(
          arrayOf(
              mapOf("name" to "undo", "icon" to SpatialIcons.Regular.Undo),
              mapOf("name" to "redo", "icon" to SpatialIcons.Regular.Redo),
              mapOf("name" to "universal menu", "icon" to SpatialIcons.Regular.UniversalMenu),
              mapOf("name" to "unlock-pattern", "icon" to SpatialIcons.Regular.UnlockPattern),
              mapOf("name" to "compose", "icon" to SpatialIcons.Regular.Compose),
              mapOf("name" to "width-small", "icon" to SpatialIcons.Regular.WidthSmall),
              mapOf("name" to "width-medium", "icon" to SpatialIcons.Regular.WidthMedium),
              // mapOf("name" to "width-wide", "icon" to SpatialIcons.Regular.WidthWide),
              mapOf("name" to "width-extrawide", "icon" to SpatialIcons.Regular.WidthExtrawide),
              mapOf("name" to "workplace", "icon" to SpatialIcons.Regular.Workplace),
              mapOf("name" to "ibeam", "icon" to SpatialIcons.Regular.Ibeam),
              mapOf("name" to "horizon", "icon" to SpatialIcons.Regular.Horizon),
              mapOf("name" to "keyboard-space", "icon" to SpatialIcons.Regular.KeyboardSpace),
              mapOf("name" to "physical-features", "icon" to SpatialIcons.Regular.PhysicalFeatures),
              mapOf("name" to "minimize", "icon" to SpatialIcons.Regular.Minimize),
              mapOf("name" to "keyboard-enter", "icon" to SpatialIcons.Regular.KeyboardEnter),
              mapOf("name" to "Molokini", "icon" to SpatialIcons.Regular.Molokini),
              mapOf("name" to "multi-browser", "icon" to SpatialIcons.Regular.MultiBrowser),
              mapOf("name" to "open-circle", "icon" to SpatialIcons.Regular.OpenCircle),
              mapOf("name" to "reward-packs", "icon" to SpatialIcons.Regular.RewardPacks),
              mapOf("name" to "rotate-left", "icon" to SpatialIcons.Regular.RotateLeft),
              mapOf("name" to "rotate-right", "icon" to SpatialIcons.Regular.RotateRight),
              mapOf("name" to "sidebar-pin", "icon" to SpatialIcons.Regular.SidebarPin),
              mapOf("name" to "sidebar-pin-close", "icon" to SpatialIcons.Regular.SidebarPinClose),
              mapOf("name" to "sync", "icon" to SpatialIcons.Regular.Sync),
              mapOf("name" to "sync-off", "icon" to SpatialIcons.Regular.SyncOff),
              mapOf("name" to "sync-circle", "icon" to SpatialIcons.Regular.SyncCircle),
              mapOf("name" to "settings", "icon" to SpatialIcons.Regular.Settings),
              mapOf("name" to "refresh", "icon" to SpatialIcons.Regular.Refresh),
              mapOf("name" to "refresh-circle", "icon" to SpatialIcons.Regular.RefreshCircle),
              mapOf("name" to "refund", "icon" to SpatialIcons.Regular.Refund),
              mapOf("name" to "standing", "icon" to SpatialIcons.Regular.Standing),
              mapOf("name" to "screenshot", "icon" to SpatialIcons.Regular.Screenshot),
              mapOf("name" to "star", "icon" to SpatialIcons.Regular.Star),
              mapOf("name" to "star-full", "icon" to SpatialIcons.Regular.StarFull),
              mapOf("name" to "star-half", "icon" to SpatialIcons.Regular.StarHalf),
              mapOf("name" to "brightness-low", "icon" to SpatialIcons.Regular.BrightnessLow),
              mapOf("name" to "brightness-mid", "icon" to SpatialIcons.Regular.BrightnessMid),
              mapOf("name" to "brightness-on", "icon" to SpatialIcons.Regular.BrightnessOn),
              mapOf("name" to "brightness-off", "icon" to SpatialIcons.Regular.BrightnessOff),
              mapOf("name" to "search", "icon" to SpatialIcons.Regular.Search),
              mapOf("name" to "send", "icon" to SpatialIcons.Regular.Send),
              mapOf("name" to "sitting", "icon" to SpatialIcons.Regular.Sitting),
              mapOf("name" to "profile-circle", "icon" to SpatialIcons.Regular.ProfileCircle),
              mapOf("name" to "profile", "icon" to SpatialIcons.Regular.Profile),
              mapOf("name" to "mobile-keyboard", "icon" to SpatialIcons.Regular.MobileKeyboard),
              mapOf("name" to "mobile", "icon" to SpatialIcons.Regular.Mobile),
              mapOf("name" to "power", "icon" to SpatialIcons.Regular.Power),
              mapOf("name" to "privacy", "icon" to SpatialIcons.Regular.Privacy),
              mapOf("name" to "heart-off", "icon" to SpatialIcons.Regular.HeartOff),
              mapOf("name" to "heart-on", "icon" to SpatialIcons.Regular.HeartOn),
              mapOf(
                  "name" to "friends-request-sent",
                  "icon" to SpatialIcons.Regular.FriendsRequestSent),
              mapOf("name" to "friends-report", "icon" to SpatialIcons.Regular.FriendsReport),
              mapOf("name" to "friends-kick", "icon" to SpatialIcons.Regular.FriendsKick),
              mapOf("name" to "friends-remove", "icon" to SpatialIcons.Regular.FriendsRemove),
              mapOf("name" to "friends-block", "icon" to SpatialIcons.Regular.FriendsBlock),
              mapOf("name" to "friends-add", "icon" to SpatialIcons.Regular.FriendsAdd),
              mapOf("name" to "friends-except", "icon" to SpatialIcons.Regular.FriendsExcept),
              mapOf("name" to "headset-casting", "icon" to SpatialIcons.Regular.HeadsetCasting),
              mapOf("name" to "frequency", "icon" to SpatialIcons.Regular.Frequency),
              mapOf("name" to "lock-off", "icon" to SpatialIcons.Regular.LockOff),
              mapOf("name" to "lock-on", "icon" to SpatialIcons.Regular.LockOn),
              mapOf("name" to "folder", "icon" to SpatialIcons.Regular.Folder),
              mapOf("name" to "filter", "icon" to SpatialIcons.Regular.Filter),
              mapOf("name" to "media-360-3d", "icon" to SpatialIcons.Regular.Media3603d),
              mapOf("name" to "media-180-3d", "icon" to SpatialIcons.Regular.Media1803d),
              mapOf("name" to "media-2d", "icon" to SpatialIcons.Regular.Media2d),
              mapOf("name" to "media-180", "icon" to SpatialIcons.Regular.Media180),
              mapOf("name" to "images-360", "icon" to SpatialIcons.Regular.Images360),
              mapOf("name" to "library", "icon" to SpatialIcons.Regular.Library),
              mapOf("name" to "image", "icon" to SpatialIcons.Regular.Image),
              mapOf("name" to "history", "icon" to SpatialIcons.Regular.History),
              mapOf("name" to "bullet-list", "icon" to SpatialIcons.Regular.BulletList),
              mapOf("name" to "list-lock", "icon" to SpatialIcons.Regular.ListLock),
              mapOf("name" to "list-sort", "icon" to SpatialIcons.Regular.ListSort),
              mapOf("name" to "list-checked", "icon" to SpatialIcons.Regular.ListChecked),
              mapOf("name" to "list-view", "icon" to SpatialIcons.Regular.ListView),
              mapOf("name" to "home", "icon" to SpatialIcons.Regular.Home),
              mapOf("name" to "info", "icon" to SpatialIcons.Regular.Info),
              mapOf("name" to "internet", "icon" to SpatialIcons.Regular.Internet),
              mapOf("name" to "keyboard-on", "icon" to SpatialIcons.Regular.KeyboardOn),
              mapOf("name" to "keyboard-off", "icon" to SpatialIcons.Regular.KeyboardOff),
              mapOf("name" to "keyboard", "icon" to SpatialIcons.Regular.Keyboard),
              mapOf("name" to "do-not-disturb-off", "icon" to SpatialIcons.Regular.DoNotDisturbOff),
              mapOf("name" to "do-not-disturb-on", "icon" to SpatialIcons.Regular.DoNotDisturb),
              mapOf("name" to "download", "icon" to SpatialIcons.Regular.Download),
              // mapOf("name" to "upload", "icon" to SpatialIcons.Regular.Upload),
              mapOf("name" to "desktop-off", "icon" to SpatialIcons.Regular.DesktopOff),
              mapOf("name" to "desktop", "icon" to SpatialIcons.Regular.Desktop),
              mapOf("name" to "distance", "icon" to SpatialIcons.Regular.Distance),
              mapOf("name" to "table", "icon" to SpatialIcons.Regular.Table),
          ),
          arrayOf(
              mapOf("name" to "three-people", "icon" to SpatialIcons.Regular.ThreePeople),
              mapOf("name" to "wallet", "icon" to SpatialIcons.Regular.Wallet),
              mapOf("name" to "warning", "icon" to SpatialIcons.Regular.Warning),
              mapOf("name" to "wifi-on", "icon" to SpatialIcons.Regular.WifiOn),
              mapOf("name" to "wifi-alt-mid", "icon" to SpatialIcons.Regular.WifiAltMid),
              mapOf("name" to "wifi-alt-low", "icon" to SpatialIcons.Regular.WifiAltLow),
              mapOf("name" to "wifi-alt-off", "icon" to SpatialIcons.Regular.WifiAltOff),
              mapOf("name" to "wifi-off", "icon" to SpatialIcons.Regular.WifiOff),
              mapOf("name" to "wifi-secure", "icon" to SpatialIcons.Regular.WifiSecure),
              mapOf("name" to "world", "icon" to SpatialIcons.Regular.World),
              mapOf("name" to "Zoom", "icon" to SpatialIcons.Regular.Zoom),
              mapOf("name" to "10s-backward", "icon" to SpatialIcons.Regular.TenSecondsBackward),
              mapOf("name" to "10s-forward", "icon" to SpatialIcons.Regular.TenSecondsForward),
              mapOf("name" to "check-box-circle", "icon" to SpatialIcons.Regular.CheckboxCircle),
              mapOf("name" to "clothing", "icon" to SpatialIcons.Regular.Clothing),
              mapOf("name" to "color", "icon" to SpatialIcons.Regular.Color),
              mapOf("name" to "couch", "icon" to SpatialIcons.Regular.Couch),
              mapOf("name" to "flip", "icon" to SpatialIcons.Regular.Flip),
              mapOf("name" to "home-edit", "icon" to SpatialIcons.Regular.HomeEdit),
              mapOf(
                  "name" to "intrusion-detection",
                  "icon" to SpatialIcons.Regular.IntrusionDetection),
              mapOf("name" to "hand cursor", "icon" to SpatialIcons.Regular.HandCursor),
              mapOf(
                  "name" to "unknown-controller", "icon" to SpatialIcons.Regular.UnknownController),
              mapOf("name" to "unknown-headset", "icon" to SpatialIcons.Regular.UnknownHeadset),
              // mapOf("name" to "upload", "icon" to SpatialIcons.Regular.Upload),
              mapOf("name" to "unknown-sources", "icon" to SpatialIcons.Regular.UnknownSources),
              mapOf("name" to "twitter", "icon" to SpatialIcons.Regular.Twitter),
              mapOf("name" to "television", "icon" to SpatialIcons.Regular.Television),
              mapOf("name" to "unrated", "icon" to SpatialIcons.Regular.Unrated),
              mapOf("name" to "update", "icon" to SpatialIcons.Regular.Update),
              mapOf("name" to "usb-stick", "icon" to SpatialIcons.Regular.UsbStick),
              mapOf("name" to "phone", "icon" to SpatialIcons.Regular.Phone),
              mapOf("name" to "power", "icon" to SpatialIcons.Regular.Power),
              mapOf("name" to "scale-down", "icon" to SpatialIcons.Regular.ScaleDown),
              mapOf("name" to "scale", "icon" to SpatialIcons.Regular.Scale),
              mapOf("name" to "resize-horizontal", "icon" to SpatialIcons.Regular.ResizeHorizontal),
              mapOf(
                  "name" to "resize-horizontal-down",
                  "icon" to SpatialIcons.Regular.ResizeHorizontalDown),
              mapOf(
                  "name" to "resize-vertical-up", "icon" to SpatialIcons.Regular.ResizeVerticalUp),
              mapOf(
                  "name" to "resize-vertical-down",
                  "icon" to SpatialIcons.Regular.ResizeVerticalDown),
              mapOf("name" to "play-next-circle", "icon" to SpatialIcons.Regular.PlayNextCircle),
              mapOf("name" to "play-prev", "icon" to SpatialIcons.Regular.PlayPrev),
              mapOf("name" to "play-prev-circle", "icon" to SpatialIcons.Regular.PlayPrevCircle),
              mapOf("name" to "play-next", "icon" to SpatialIcons.Regular.PlayNext),
              mapOf("name" to "play", "icon" to SpatialIcons.Regular.Play),
              mapOf("name" to "play-circle", "icon" to SpatialIcons.Regular.PlayCircle),
              mapOf("name" to "pause", "icon" to SpatialIcons.Regular.Pause),
              mapOf("name" to "pause-circle", "icon" to SpatialIcons.Regular.PauseCircle),
              mapOf("name" to "replay", "icon" to SpatialIcons.Regular.Replay),
              mapOf("name" to "replay-circle", "icon" to SpatialIcons.Regular.ReplayCircle),
              mapOf("name" to "remove-circle", "icon" to SpatialIcons.Regular.RemoveCircle),
              mapOf("name" to "password-visible", "icon" to SpatialIcons.Regular.PasswordVisible),
              mapOf("name" to "reorient", "icon" to SpatialIcons.Regular.Reorient),
              mapOf(
                  "name" to "concepts (dogfooding)",
                  "icon" to SpatialIcons.Regular.ConceptsDogfooding),
              mapOf("name" to "environment", "icon" to SpatialIcons.Regular.Environment),
              mapOf("name" to "events-add", "icon" to SpatialIcons.Regular.EventsAdd),
              mapOf("name" to "events", "icon" to SpatialIcons.Regular.Events),
              mapOf("name" to "bluetooth", "icon" to SpatialIcons.Regular.Bluetooth),
              mapOf("name" to "computer", "icon" to SpatialIcons.Regular.Computer),
              mapOf("name" to "command-center", "icon" to SpatialIcons.Regular.CommandCenter),
              mapOf("name" to "more-horizontal", "icon" to SpatialIcons.Regular.MoreHorizontal),
              mapOf("name" to "more-vertical", "icon" to SpatialIcons.Regular.MoreVertical),
              mapOf("name" to "close", "icon" to SpatialIcons.Regular.Close),
              mapOf("name" to "close-circle", "icon" to SpatialIcons.Regular.CloseCircle),
              mapOf("name" to "check-circle", "icon" to SpatialIcons.Regular.CheckCircle),
              mapOf("name" to "check-alt", "icon" to SpatialIcons.Regular.CheckAlt),
              mapOf("name" to "chevron-down", "icon" to SpatialIcons.Regular.ChevronDown),
              mapOf("name" to "chevron-up", "icon" to SpatialIcons.Regular.ChevronUp),
              mapOf("name" to "chevron-left", "icon" to SpatialIcons.Regular.ChevronLeft),
              mapOf("name" to "chevron-right", "icon" to SpatialIcons.Regular.ChevronRight),
              mapOf("name" to "camera-roll", "icon" to SpatialIcons.Regular.CameraRoll),
              mapOf("name" to "comfortable", "icon" to SpatialIcons.Regular.Comfortable),
              mapOf(
                  "name" to "closed-captioning-off",
                  "icon" to SpatialIcons.Regular.ClosedCaptioningOff),
              mapOf("name" to "closed-captioning", "icon" to SpatialIcons.Regular.ClosedCaptioning),
              mapOf("name" to "file", "icon" to SpatialIcons.Regular.File),
              mapOf("name" to "compass", "icon" to SpatialIcons.Regular.Compass),
              mapOf("name" to "category-all", "icon" to SpatialIcons.Regular.CategoryAll),
              mapOf("name" to "apps", "icon" to SpatialIcons.Regular.Apps),
              mapOf("name" to "chat-off", "icon" to SpatialIcons.Regular.ChatOff),
              mapOf("name" to "chat-ellipses", "icon" to SpatialIcons.Regular.ChatEllipses),
              mapOf("name" to "chat-text", "icon" to SpatialIcons.Regular.ChatText),
              mapOf("name" to "chat", "icon" to SpatialIcons.Regular.Chat),
              mapOf("name" to "bookmark", "icon" to SpatialIcons.Regular.Bookmark),
              mapOf("name" to "bookmark-add", "icon" to SpatialIcons.Regular.BookmarkAdd),
              mapOf("name" to "gallery-reconnect", "icon" to SpatialIcons.Regular.GalleryReconnect),
              mapOf("name" to "gallery-ready", "icon" to SpatialIcons.Regular.GalleryReady),
              mapOf("name" to "gallery-phone", "icon" to SpatialIcons.Regular.GalleryPhone),
              mapOf("name" to "gallery-folder", "icon" to SpatialIcons.Regular.GalleryFolder),
              mapOf("name" to "gallery-connect", "icon" to SpatialIcons.Regular.GalleryConnect),
              mapOf("name" to "gamepad", "icon" to SpatialIcons.Regular.Gamepad),
              mapOf("name" to "full-screen", "icon" to SpatialIcons.Regular.FullScreen),
              mapOf("name" to "full-screen-exit", "icon" to SpatialIcons.Regular.FullScreenExit),
              mapOf("name" to "uppercase-arrow", "icon" to SpatialIcons.Regular.UppercaseArrow),
          ),
          arrayOf(
              mapOf("name" to "vibration", "icon" to SpatialIcons.Regular.Vibration),
              mapOf("name" to "video-capture", "icon" to SpatialIcons.Regular.VideoCapture),
              mapOf("name" to "view-gallery", "icon" to SpatialIcons.Regular.ViewGallery),
              mapOf("name" to "visit-home", "icon" to SpatialIcons.Regular.VisitHome),
              mapOf("name" to "voice-command", "icon" to SpatialIcons.Regular.VoiceCommand),
              mapOf("name" to "volume-on", "icon" to SpatialIcons.Regular.VolumeOn),
              mapOf("name" to "volume-off", "icon" to SpatialIcons.Regular.VolumeOff),
              mapOf("name" to "volume-mid", "icon" to SpatialIcons.Regular.VolumeMid),
              mapOf("name" to "volume-low", "icon" to SpatialIcons.Regular.VolumeLow),
              mapOf("name" to "vr-object", "icon" to SpatialIcons.Regular.VrObject),
              mapOf("name" to "stationary", "icon" to SpatialIcons.Regular.Stationary),
              mapOf("name" to "stop", "icon" to SpatialIcons.Regular.Stop),
              mapOf("name" to "stop-circle", "icon" to SpatialIcons.Regular.StopCircle),
              mapOf("name" to "storage", "icon" to SpatialIcons.Regular.Storage),
              mapOf("name" to "storage-sd", "icon" to SpatialIcons.Regular.StorageSd),
              mapOf("name" to "store", "icon" to SpatialIcons.Regular.Store),
              mapOf("name" to "store-cart", "icon" to SpatialIcons.Regular.StoreCart),
              mapOf("name" to "tag", "icon" to SpatialIcons.Regular.Tag),
              mapOf("name" to "thumb down", "icon" to SpatialIcons.Regular.ThumbsDown),
              mapOf("name" to "thumb up", "icon" to SpatialIcons.Regular.ThumbsUp),
              mapOf("name" to "time", "icon" to SpatialIcons.Regular.Time),
              mapOf("name" to "tips", "icon" to SpatialIcons.Regular.Tips),
              mapOf("name" to "to-top", "icon" to SpatialIcons.Regular.ToTop),
              mapOf("name" to "touchpad", "icon" to SpatialIcons.Regular.Touchpad),
              mapOf("name" to "touch-left", "icon" to SpatialIcons.Regular.TouchLeft),
              mapOf("name" to "touch-right", "icon" to SpatialIcons.Regular.TouchRight),
              mapOf("name" to "touch-2-right", "icon" to SpatialIcons.Regular.Touch2Right),
              mapOf("name" to "touch-2-left", "icon" to SpatialIcons.Regular.Touch2Left),
              mapOf("name" to "trophy", "icon" to SpatialIcons.Regular.Trophy),
              mapOf("name" to "trash", "icon" to SpatialIcons.Regular.Trash),
              mapOf("name" to "parties", "icon" to SpatialIcons.Regular.Parties),
              mapOf("name" to "question", "icon" to SpatialIcons.Regular.Question),
              mapOf("name" to "oculus-remote", "icon" to SpatialIcons.Regular.OculusRemote),
              mapOf("name" to "linkedin", "icon" to SpatialIcons.Regular.Linkedin),
              mapOf("name" to "messenger", "icon" to SpatialIcons.Regular.Messenger),
              mapOf("name" to "lens-adjustment", "icon" to SpatialIcons.Regular.LensAdjustment),
              mapOf("name" to "night-mode", "icon" to SpatialIcons.Regular.NightMode),
              mapOf(
                  "name" to "microphone-unavailable",
                  "icon" to SpatialIcons.Regular.MicrophoneUnavailable),
              mapOf("name" to "microphone-on", "icon" to SpatialIcons.Regular.MicrophoneOn),
              mapOf("name" to "microphone-off", "icon" to SpatialIcons.Regular.MicrophoneOff),
              mapOf("name" to "password", "icon" to SpatialIcons.Regular.Password),
              mapOf("name" to "leave-places", "icon" to SpatialIcons.Regular.LeavePlaces),
              mapOf("name" to "open-tab", "icon" to SpatialIcons.Regular.OpenTab),
              mapOf("name" to "open-panel", "icon" to SpatialIcons.Regular.OpenPanel),
              mapOf("name" to "intense", "icon" to SpatialIcons.Regular.Intense),
              mapOf("name" to "moderate", "icon" to SpatialIcons.Regular.Moderate),
              mapOf(
                  "name" to "media-immersive-video",
                  "icon" to SpatialIcons.Regular.MediaImmersiveVideo),
              mapOf(
                  "name" to "media-immersive-photo",
                  "icon" to SpatialIcons.Regular.MediaImmersivePhoto),
              mapOf("name" to "media-3d-horiz", "icon" to SpatialIcons.Regular.Media3dHoriz),
              mapOf("name" to "media-3d-vert", "icon" to SpatialIcons.Regular.Media3dVert),
              mapOf("name" to "media-360", "icon" to SpatialIcons.Regular.Media360),
              mapOf("name" to "oculus-voice-off", "icon" to SpatialIcons.Regular.OculusVoiceOff),
              mapOf("name" to "oculus-voice", "icon" to SpatialIcons.Regular.OculusVoice),
              mapOf("name" to "recently-played", "icon" to SpatialIcons.Regular.RecentlyPlayed),
              mapOf("name" to "my-media", "icon" to SpatialIcons.Regular.MyMedia),
              mapOf("name" to "purchase", "icon" to SpatialIcons.Regular.Purchase),
              mapOf("name" to "notifications", "icon" to SpatialIcons.Regular.Notifications),
              mapOf("name" to "notifications-off", "icon" to SpatialIcons.Regular.NotificationsOff),
              mapOf("name" to "bt-oculus", "icon" to SpatialIcons.Regular.BtOculus),
              mapOf("name" to "move", "icon" to SpatialIcons.Regular.Move),
              mapOf("name" to "bt-menu", "icon" to SpatialIcons.Regular.BtMenu),
              mapOf("name" to "bt-y", "icon" to SpatialIcons.Regular.BtY),
              mapOf("name" to "bt-x", "icon" to SpatialIcons.Regular.BtX),
              mapOf("name" to "bt-b", "icon" to SpatialIcons.Regular.BtB),
              mapOf("name" to "bt-a", "icon" to SpatialIcons.Regular.BtA),
              mapOf("name" to "arrow-down", "icon" to SpatialIcons.Regular.ArrowDown),
              mapOf("name" to "arrow-up", "icon" to SpatialIcons.Regular.ArrowUp),
              mapOf("name" to "arrow-left", "icon" to SpatialIcons.Regular.ArrowLeft),
              mapOf("name" to "arrow-right", "icon" to SpatialIcons.Regular.ArrowRight),
              mapOf("name" to "arrows-up-down", "icon" to SpatialIcons.Regular.ArrowsUpDown),
              mapOf("name" to "arrows-left-right", "icon" to SpatialIcons.Regular.ArrowsLeftRight),
              mapOf(
                  "name" to "arrow-right-circle", "icon" to SpatialIcons.Regular.ArrowRightCircle),
              mapOf("name" to "arrow-left-circle", "icon" to SpatialIcons.Regular.ArrowLeftCircle),
              mapOf("name" to "arrow-up-circle", "icon" to SpatialIcons.Regular.ArrowUpCircle),
              mapOf("name" to "arrow-down-circle", "icon" to SpatialIcons.Regular.ArrowDownCircle),
              mapOf("name" to "13-plus", "icon" to SpatialIcons.Regular.ThirteenPlus),
              mapOf("name" to "add", "icon" to SpatialIcons.Regular.Add),
              // mapOf("name" to "add-circle", "icon" to SpatialIcons.Regular.AddCircle),
              // mapOf("name" to "15s-backward", "icon" to
              // SpatialIcons.Regular.FifteenSecondsBackward),
              // mapOf("name" to "15s-forward", "icon" to
              // SpatialIcons.Regular.FifteenSecondsForward),
              // mapOf("name" to "emoji", "icon" to SpatialIcons.Regular.Emoji),
              // mapOf("name" to "Email", "icon" to SpatialIcons.Regular.Email),
              // mapOf("name" to "edit", "icon" to SpatialIcons.Regular.Edit),
              // mapOf("name" to "destination", "icon" to SpatialIcons.Regular.Destination),
              mapOf("name" to "credit-card", "icon" to SpatialIcons.Regular.CreditCard),
              // mapOf("name" to "category-basic", "icon" to SpatialIcons.Regular.CategoryBasic),
              mapOf("name" to "error-circle", "icon" to SpatialIcons.Regular.ErrorCircle),
              mapOf("name" to "error", "icon" to SpatialIcons.Regular.Error),
              mapOf("name" to "cloud", "icon" to SpatialIcons.Regular.Cloud),
          ),
      )

  PanelScaffold("Icon Library") {
    Column {
      Row {
        Text(
            "For optimal sizing, small icons should not exceed 32px, while large icons can start at size 33px and above.",
            style =
                LocalTypography.current.body1.copy(
                    color = LocalColorScheme.current.primaryAlphaBackground))
      }
      Spacer(modifier = Modifier.height(20.dp))
      Row(modifier = Modifier.background(SpatialTheme.colorScheme.hover).fillMaxWidth()) {
        Column {
          Spacer(modifier = Modifier.height(20.dp))
          Text(
              "  Component",
              style =
                  LocalTypography.current.headline1Strong.copy(
                      color = LocalColorScheme.current.primaryAlphaBackground))
          Spacer(modifier = Modifier.height(20.dp))
          Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = SpatialIcons.Regular.Redo,
                "",
                tint = LocalColorScheme.current.primaryAlphaBackground)
          }
          Spacer(modifier = Modifier.height(20.dp))
        }
      }
      Spacer(modifier = Modifier.height(20.dp))
      Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        Column(modifier = Modifier.width(340.dp).padding(10.dp)) {
          for (icon in iconList[0]) {
            Row(
                modifier = Modifier.height(60.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                  Box(modifier = Modifier.width(200.dp)) {
                    Text(
                        icon["name"] as String,
                        style =
                            LocalTypography.current.body1.copy(
                                color = LocalColorScheme.current.primaryAlphaBackground))
                  }
                  Box(modifier = Modifier.size(dimensionResource(R.dimen.button_icon_size))) {
                    Icon(
                        imageVector = icon["icon"] as ImageVector,
                        "",
                        tint = LocalColorScheme.current.primaryAlphaBackground)
                  }
                  Box(modifier = Modifier.size(dimensionResource(R.dimen.dialog_icon_size))) {
                    Icon(
                        imageVector = icon["icon"] as ImageVector,
                        "",
                        tint = LocalColorScheme.current.primaryAlphaBackground,
                        modifier = Modifier.size(dimensionResource(R.dimen.dialog_icon_size)))
                  }
                }
          }
        }
        VerticalDivider()
        Column(modifier = Modifier.width(340.dp).padding(10.dp)) {
          for (icon in iconList[1]) {
            Row(
                modifier = Modifier.height(60.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                  Box(modifier = Modifier.width(200.dp)) {
                    Text(
                        icon["name"] as String,
                        style =
                            LocalTypography.current.body1.copy(
                                color = LocalColorScheme.current.primaryAlphaBackground))
                  }
                  Box(modifier = Modifier.size(dimensionResource(R.dimen.button_icon_size))) {
                    Icon(
                        imageVector = icon["icon"] as ImageVector,
                        "",
                        tint = LocalColorScheme.current.primaryAlphaBackground)
                  }
                  Box(modifier = Modifier.size(dimensionResource(R.dimen.dialog_icon_size))) {
                    Icon(
                        imageVector = icon["icon"] as ImageVector,
                        "",
                        tint = LocalColorScheme.current.primaryAlphaBackground,
                        modifier = Modifier.size(dimensionResource(R.dimen.dialog_icon_size)))
                  }
                }
          }
        }
        VerticalDivider()
        Column(modifier = Modifier.width(340.dp).padding(10.dp)) {
          for (icon in iconList[2]) {
            Row(
                modifier = Modifier.height(60.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                  Box(modifier = Modifier.width(200.dp)) {
                    Text(
                        icon["name"] as String,
                        style =
                            LocalTypography.current.body1.copy(
                                color = LocalColorScheme.current.primaryAlphaBackground))
                  }
                  Box(modifier = Modifier.size(dimensionResource(R.dimen.button_icon_size))) {
                    Icon(
                        imageVector = icon["icon"] as ImageVector,
                        "",
                        tint = LocalColorScheme.current.primaryAlphaBackground)
                  }
                  Box(modifier = Modifier.size(dimensionResource(R.dimen.dialog_icon_size))) {
                    Icon(
                        imageVector = icon["icon"] as ImageVector,
                        "",
                        tint = LocalColorScheme.current.primaryAlphaBackground,
                        modifier = Modifier.size(dimensionResource(R.dimen.dialog_icon_size)))
                  }
                }
          }
        }
      }
    }
  }
}

@Preview(
    widthDp = 1080,
    heightDp = 1949,
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun BirdseyeIconLibraryPreview() {
  BirdseyeIconLibrary()
}
