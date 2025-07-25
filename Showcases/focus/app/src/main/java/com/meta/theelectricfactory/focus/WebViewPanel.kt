// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.util.Log
import android.webkit.WebResourceRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.meta.spatial.uiset.button.PrimaryCircleButton
import com.meta.spatial.uiset.input.SpatialSearchBar
import com.meta.spatial.uiset.theme.SpatialTheme
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.meta.spatial.core.Entity
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.uiset.button.SecondaryCircleButton

@Composable
fun WebViewPanel(
    url: String = "https://www.google.com/",
    uuid: Int = -1,
    entity: Entity
) {

    var immersiveActivity = ImmersiveActivity.getInstance()
    var urlInput = remember { mutableStateOf(url) }
    val webViewRef = remember { mutableStateOf<WebView?>(null) }

    var lastTextChangeTime = remember { 0L }
    val handler = remember { Handler(Looper.getMainLooper()) }
    val lastRunnable = remember { arrayOf<Runnable?>(null) }

    return FocusTheme {

        Box {
            Column() {
                Row (modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier
                        .aspectRatio(1f)
                    ) {
                        SecondaryCircleButton(
                            onClick = {
                                webViewRef.value?.let { webView ->
                                    if (webView.canGoBack()) {
                                        webView.goBack()
                                        immersiveActivity?.DB?.updateWebViewURL(uuid, webView.originalUrl.toString())
                                        urlInput.value = webView.originalUrl.toString()
                                    } else if (urlInput.value != webView.url) {
                                        webView.loadUrl(urlInput.value)
                                        immersiveActivity?.DB?.updateWebViewURL(uuid, urlInput.value)
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    painterResource(id = R.drawable.back),
                                    contentDescription = "Back",
                                    tint = Color.Unspecified
                                )
                            },
                        )
                    }

                    Box(modifier = Modifier
                        .aspectRatio(1f)
                    ) {
                        SecondaryCircleButton(
                            onClick = {
                                webViewRef.value?.let { webView ->
                                    webView.loadUrl(webView.url ?: "")
                                    urlInput.value = webView.url ?: ""
                                }
                            },
                            icon = {
                                Icon(
                                    painterResource(id = R.drawable.refresh),
                                    contentDescription = "Refresh",
                                    tint = Color.Unspecified
                                )
                            },
                        )
                    }

                    Box(modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .clip(SpatialTheme.shapes.large)
                        .background(Color.White)
                    ) {
                        SpatialSearchBar(
                            enableAudio = false,
                            // Update address bar when web view created
                            query = urlInput.value,
                            onQueryChange = {
                                urlInput.value = it
                                lastTextChangeTime = System.currentTimeMillis()
                                checkIfStillWriting(3 * 1000, lastTextChangeTime, handler, lastRunnable,  {
                                    onNewURL(urlInput.value, uuid, webViewRef)
                                })
                            },
                            onQuerySubmit = {
                                // TODO this is not working, we are hacking this with the checkIfStillWriting function
                                Log.i("Focus", "Focus> onQuerySubmit")
                                onNewURL(urlInput.value, uuid, webViewRef)
                            },
                        )
                    }

                    Box(modifier = Modifier
                        .aspectRatio(1f)
                        .align(Alignment.Top)
                    ) {
                        SecondaryCircleButton(
                            onClick = {
                                immersiveActivity?.DB?.deleteToolAsset(uuid)
                                entity.destroy()
                            },
                            icon = {
                                Icon(
                                    painterResource(id = R.drawable.delete_task),
                                    contentDescription = "Delete",
                                    tint = Color.Unspecified
                                )
                            },
                        )
                    }
                }

                Spacer(modifier = Modifier.size(40.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(SpatialTheme.shapes.large)
                        .background(FocusColors.panel),
                ) {
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                webViewRef.value = this
                                // Trick to use browser like desktop and not mobile
                                settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0"
                                settings.javaScriptEnabled = true
                                settings.setLoadWithOverviewMode(true)
                                settings.setUseWideViewPort(true)
                                // if you want to enable zoom feature
                                settings.setSupportZoom(true)

                                webViewClient = object : WebViewClient() {
                                    override fun shouldOverrideUrlLoading(
                                        view: WebView?,
                                        request: WebResourceRequest?
                                    ): Boolean {

                                        // Handle internal URL changes here
                                        val newUrl = request?.url.toString()
                                        immersiveActivity?.DB?.updateWebViewURL(uuid, newUrl)
                                        urlInput.value = newUrl
                                        return false
                                    }
                                }
                                loadUrl(url)
                            }
                        },
                        update = { webView ->
                        }
                    )
                }
            }
        }
    }
}

fun onNewURL(
    query: String,
    uuid: Int,
    webViewRef: MutableState<WebView?>
) {
    val isValidUrl = Patterns.WEB_URL.matcher(query).matches()

    val newURL = if (isValidUrl) {
        when {
            query.startsWith("http://") || query.startsWith("https://") -> query
            else -> "https://$query"
        }
    } else {
        // If it's not a valid URL, build a Google search URL
        val googleQuery = query.trim().replace(" ", "+")
        "https://www.google.com/search?q=$googleQuery"
    }

    ImmersiveActivity.getInstance()?.DB?.updateWebViewURL(uuid, newURL)
    webViewRef.value?.let { webView ->
        webView.loadUrl(newURL)
    }
}

fun checkIfStillWriting( typingInterval: Long, lastTextChangeTime: Long, handler: Handler, lastRunnable: Array<Runnable?>, onComplete: () -> (Unit)) {

    lastRunnable[0]?.let { handler.removeCallbacks(it) }

    val runnable = Runnable {
        if (System.currentTimeMillis() - lastTextChangeTime >= typingInterval) {
            onComplete()
        }
    }
    lastRunnable[0] = runnable
    handler.postDelayed(runnable, typingInterval)
}

@Preview(
    widthDp = (0.56f * focusDP).toInt(),
    heightDp = (0.4f * focusDP).toInt(),
    uiMode = UI_MODE_TYPE_VR_HEADSET,
)
@Composable
fun WebViewPanelPreview() {
    val url = "https://www.google.com/"
    val uuid = 1
    val ent = Entity.createPanelEntity(1, Transform(), Grabbable(true, GrabbableType.FACE)
    )
    WebViewPanel(url, uuid, ent)
}