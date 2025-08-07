// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus.panels

import android.content.res.Configuration.UI_MODE_TYPE_VR_HEADSET
import android.util.Patterns
import android.webkit.WebResourceRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.meta.spatial.uiset.theme.SpatialTheme
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import com.meta.spatial.core.Entity
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.uiset.button.SecondaryCircleButton
import com.meta.theelectricfactory.focus.ui.FocusColors
import com.meta.theelectricfactory.focus.ui.FocusTheme
import com.meta.theelectricfactory.focus.ImmersiveActivity
import com.meta.theelectricfactory.focus.R
import com.meta.theelectricfactory.focus.ui.focusFont
import com.meta.theelectricfactory.focus.utils.FOCUS_DP

@Composable
fun WebViewPanel(
    url: String = "https://www.google.com/",
    uuid: Int = -1,
    entity: Entity
) {

    var immA = ImmersiveActivity.getInstance()
    var urlInput = remember { mutableStateOf(url) }
    val webViewRef = remember { mutableStateOf<WebView?>(null) }

    return FocusTheme {

        Box {
            Column() {
                Row (modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SecondaryCircleButton(
                        onClick = {
                            webViewRef.value?.let { webView ->
                                if (webView.canGoBack()) {
                                    webView.goBack()
                                    immA?.DB?.updateWebViewURL(uuid, webView.originalUrl.toString())
                                    urlInput.value = webView.originalUrl.toString()
                                } else if (urlInput.value != webView.url) {
                                    webView.loadUrl(urlInput.value)
                                    immA?.DB?.updateWebViewURL(uuid, urlInput.value)
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

                    Box(modifier = Modifier
                        .fillMaxWidth(0.945f)
                    ) {
                        BasicTextField(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White, shape = SpatialTheme.shapes.large),
                            value = urlInput.value,
                            onValueChange = {
                                urlInput.value = it
                            },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                fontFamily = focusFont
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    onNewURL(urlInput.value, uuid, webViewRef)
                                }
                            ),

                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 20.dp, vertical = 0.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    innerTextField()
                                }
                            }
                        )
                    }

                    SecondaryCircleButton(
                        onClick = {
                            immA?.DB?.deleteToolAsset(uuid)
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

                Spacer(modifier = Modifier.size(20.dp))

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
                                        immA?.DB?.updateWebViewURL(uuid, newUrl)
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

@Preview(
    widthDp = (0.56f * FOCUS_DP).toInt(),
    heightDp = (0.4f * FOCUS_DP).toInt(),
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