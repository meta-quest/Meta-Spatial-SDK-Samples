package com.meta.pixelandtexel.scanner.android.views.components.smart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.meta.pixelandtexel.scanner.models.devices.domain.DomainServices
import com.meta.pixelandtexel.scanner.models.devices.domain.MediaPlayerAttributes
import com.meta.pixelandtexel.scanner.models.devices.domain.MediaPlayerDomain
import com.meta.pixelandtexel.scanner.utils.mytheme.MyPaddings

@Composable
fun MediaPlayerComposable(
    modifier: Modifier = Modifier,
    title: String = "Media Player",
    mediaPlayerDomain: MediaPlayerDomain,
    onStartChange: ((Boolean) -> Unit)? = null,
    onMuteChange: ((Boolean) -> Unit)? = null,
    onVolumenChange: ((Float) -> Unit)? = null,
    onPlayChange: ((Boolean) -> Unit)? = null,
) {
    Card(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MyPaddings.S)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(MyPaddings.M)
            )

            EntityRow(
                title = "Actual State",
                isUpdating = false,
                actualValue = mediaPlayerDomain.value,
                onSwitchToggle = onStartChange
            )

            if (mediaPlayerDomain.attributes.volumeLevel != null) {
                EntityRow(
                    title = "Volume Level",
                    isUpdating = false,
                    actualValue = mediaPlayerDomain.attributes.volumeLevel,
                    onSliderChange = onVolumenChange,
                )
            }

            if (mediaPlayerDomain.attributes.isMuted != null) {
                EntityRow(
                    title = "Mute",
                    isUpdating = false,
                    actualValue = mediaPlayerDomain.attributes.isMuted,
                    onSwitchToggle = onMuteChange
                )
            }


            EntityRow(
                title = "Play/Pause",
                isUpdating = false,
                actualValue = 0,
                onButtonToggled = {
                    onPlayChange?.invoke(true)
                }
            )

        }
    }
}

@Preview
@Composable
fun MediaPlayerComposablePreview() {
    val thingEntity = MediaPlayerDomain(
        value = true,
        services = listOf(
            DomainServices.TURN_OFF, DomainServices.TURN_ON, DomainServices.VOLUME_SET,
            DomainServices.VOLUME_MUTE, DomainServices.MEDIA_PLAY
        ),
        attributes = MediaPlayerAttributes(
            volumeLevel = 0.5f,
            isMuted = false,
            source = listOf("TV", "HDMI")
        )
    )
    MediaPlayerComposable(
        mediaPlayerDomain = thingEntity,
        onMuteChange = { isChecked -> /* Handle switch toggle */ },
        onVolumenChange = { volume -> /* Handle slider change */ },
        onStartChange = { isChecked -> /* Handle switch toggle */ },
    )
}