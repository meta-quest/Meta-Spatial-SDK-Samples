// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.upload

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Message
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toolbar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.meta.levinriegner.mediaview.BuildConfig
import com.meta.levinriegner.mediaview.R
import com.meta.levinriegner.mediaview.app.upload.js.DriveConfig
import com.meta.levinriegner.mediaview.app.upload.js.DriveConfigJavaScriptInterface
import com.meta.levinriegner.mediaview.app.upload.js.DriveJavaScriptInterface
import com.meta.levinriegner.mediaview.app.upload.js.DriveMedia
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class UploadActivity : AppCompatActivity() {

    private val viewModel: UploadViewModel by viewModels()

    private lateinit var toolbar: Toolbar
    private lateinit var webView: WebView
    private lateinit var webViewContainer: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        initObservers()
        initViews()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.state.collect {
                    when (it) {
                        UploadState.Completed -> {
                            finish()
                        }

                        is UploadState.Error -> {
                            setDownloadProgress(null, null)
                            finish()
                        }

                        UploadState.Idle -> {
                            setDownloadProgress(null, null)
                        }

                        is UploadState.Uploading -> {
                            setDownloadProgress(it.fileName, Pair(it.progress, it.total))
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { viewModel.onDismiss() }
        webViewContainer = findViewById(R.id.webViewContainer)
        // Main WebView
        webView = findViewById(R.id.webView)
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                setSupportMultipleWindows(true)
                javaScriptCanOpenWindowsAutomatically = true
                settings.userAgentString = USER_AGENT
            }
            addJavascriptInterface(
                DriveJavaScriptInterface(
                    onAuthCompleted = ::onDriveAuthCompleted,
                    onMediaDownloaded = ::onMediaDownloaded,
                    onDownloadFailed = ::onMediaDownloadFailed,
                    onDownloadCanceled = ::onMediaDownloadCanceled,
                ),
                DRIVE_JAVASCRIPT_INTERFACE_NAME
            )
            addJavascriptInterface(
                DriveConfigJavaScriptInterface(
                    config =
                    DriveConfig(
                        scopes =
                        "https://www.googleapis.com/auth/drive.file https://www.googleapis.com/auth/drive.appdata https://www.googleapis.com/auth/drive.appfolder",
                        clientId = BuildConfig.DRIVE_CLIENT_ID,
                        apiKey = BuildConfig.DRIVE_API_KEY,
                        appId = BuildConfig.DRIVE_APP_ID,
                    )
                ),
                DRIVE_CONFIG_JAVASCRIPT_INTERFACE_NAME
            )
        }
        webView.webViewClient = WebViewClient()
        webView.webChromeClient =
            object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    Timber.d("WebView: ${consoleMessage?.message()}")
                    return super.onConsoleMessage(consoleMessage)
                }

                override fun onCreateWindow(
                    view: WebView?,
                    isDialog: Boolean,
                    isUserGesture: Boolean,
                    resultMsg: Message?
                ): Boolean {
                    handleCreateWebWindowRequest(resultMsg)
                    return true
                }
            }
        // Load File Picker HTML
        loadHTML()
    }

    // Multiple windows support
    @SuppressLint("SetJavaScriptEnabled")
    private fun handleCreateWebWindowRequest(resultMsg: Message?) {
        if (resultMsg == null) return
        if (resultMsg.obj != null && resultMsg.obj is WebView.WebViewTransport) {
            val transport = resultMsg.obj as WebView.WebViewTransport
            // Create window WebView
            val windowWebView = WebView(this)
            windowWebView.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
            windowWebView.apply {
                settings.apply {
                    javaScriptEnabled = true
                    javaScriptCanOpenWindowsAutomatically = true
                    setSupportMultipleWindows(true)
                    settings.userAgentString = USER_AGENT
                }
                webViewClient = WebViewClient()
                webChromeClient =
                    object : WebChromeClient() {
                        override fun onCloseWindow(window: WebView?) {
                            super.onCloseWindow(window)
                            webViewContainer.removeView(window)
                        }
                    }
            }
            webViewContainer.addView(windowWebView)
            transport.webView = windowWebView
            // Continue with the new WebView
            resultMsg.sendToTarget()
        }
    }

    private fun loadHTML() {
        application
            ?.assets
            ?.open("html/google_drive_picker.html")
            ?.let { it.bufferedReader().use { it.readText() } }
            ?.let {
                webView.loadDataWithBaseURL(
                    "https://levinriegner.com",
                    it,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
    }

    private fun onDriveAuthCompleted(token: String?) {
        Timber.i("onDriveAuthCompleted: ${if (token != null) "Success" else "Failed"}")
        // Consider storing the token in the KeyStore to avoid asking for it every time
    }

    private fun onMediaDownloaded(driveMedia: DriveMedia) {
        Timber.i(
            "onMediaDownloaded: ${driveMedia.fileName} (${driveMedia.progress.first}/${driveMedia.progress.second})"
        )
        viewModel.onDownload(driveMedia)
    }

    private fun onMediaDownloadFailed(reason: String) {
        Timber.w("onMediaDownloadFailed: $reason")
        viewModel.onMediaDownloadFailed(reason)
    }

    private fun onMediaDownloadCanceled() {
        Timber.w("onMediaDownloadCanceled")
        viewModel.onMediaDownloadFailed("Download Cancelled")
    }

    private fun setDownloadProgress(fileName: String?, progress: Pair<Int, Int>?) = runOnUiThread {
        if (progress != null) {
            val escapedFileName = JSONObject.quote(fileName ?: "")

            webView.evaluateJavascript(
                """
            (function(fileName, progressValue, progressMax) {
                document.getElementById("app-text").innerText = fileName ? "Downloading " + fileName + "..." : "Downloading...";
                document.getElementById("progress-bar").value = progressValue;
                document.getElementById("progress-bar").max = progressMax;
            })($escapedFileName, ${progress.first}, ${progress.second})
        """
                    .trimIndent()
            ) {}
        } else {
            webView.evaluateJavascript(
                """
            (function() {
                document.getElementById("app-text").innerText = "Loading, please wait...";
                document.getElementById("progress-bar").removeAttribute("value");
                document.getElementById("progress-bar").removeAttribute("max");
            })()
        """
                    .trimIndent()
            ) {}
        }
    }

    companion object {
        private const val USER_AGENT =
            "Mozilla/5.0 (Android 14; Mobile; rv:129.0) Gecko/129.0 Firefox/129.0"
        private const val DRIVE_JAVASCRIPT_INTERFACE_NAME = "Android"
        private const val DRIVE_CONFIG_JAVASCRIPT_INTERFACE_NAME = "AndroidConfig"
    }
}
