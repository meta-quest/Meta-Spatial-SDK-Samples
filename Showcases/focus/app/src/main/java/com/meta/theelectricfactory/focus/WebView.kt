// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.SpatialContext
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.Scene
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.createPanelEntity

// Class to create a Web View panel tool
class WebView(
    scene: Scene,
    ctx: SpatialContext,
    url: String = "https://www.google.com/",
    var uuid: Int = -1,
    pose: Pose = Pose()
) {

  var id = getDisposableID()

  // Create a grabbable entity with a Panel
  init {

    val _width = 0.56f
    val _height = 0.4f
    val _widthInPx = 1024
    val _dpi = 128

    ImmersiveActivity.instance
        .get()
        ?.registerPanel(
            PanelRegistration(id) {
              layoutResourceId = R.layout.web_view_layout
              config {
                themeResourceId = R.style.Theme_Focus_Transparent
                width = _width
                height = _height
                layoutWidthInPx = _widthInPx
                layoutHeightInPx = (_widthInPx * (_height / _width)).toInt()
                layoutDpi = _dpi
                includeGlass = false
                enableLayer = true
                enableTransparent = true
              }
              panel {
                val webView: android.webkit.WebView? =
                    rootView?.findViewById<android.webkit.WebView>(R.id.webView)

                // Trick to use browser like desktop and not mobile
                webView?.settings?.userAgentString =
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0"

                // This will enable the javascript settings, it can also allow xss vulnerabilities
                webView?.settings?.javaScriptEnabled = true
                webView?.settings?.setLoadWithOverviewMode(true)
                webView?.settings?.setUseWideViewPort(true)
                // if you want to enable zoom feature
                webView?.settings?.setSupportZoom(true)

                // Update address bar when web view created
                val addressBar: EditText? = rootView?.findViewById<EditText>(R.id.addressBar)
                addressBar?.setText(url)

                // Listener added to url bar
                fun onNewURL() {
                  var adress = addressBar?.text?.toString()!!
                  val correctedUrl =
                      when {
                        adress.startsWith("www.") -> "https://$adress"
                        adress.startsWith("http://") || adress.startsWith("https://") -> adress
                        else -> "https://$adress"
                      }
                  ImmersiveActivity.instance.get()?.DB?.updateWebViewURL(uuid, correctedUrl)
                  webView?.loadUrl(correctedUrl)
                  addressBar.setText(correctedUrl)
                }
                addEditTextListeners(addressBar, ::onNewURL)

                webView?.webViewClient =
                    object : WebViewClient() {
                      override fun shouldOverrideUrlLoading(
                          view: WebView?,
                          request: WebResourceRequest?
                      ): Boolean {
                        // Handle internal URL changes here
                        val _url = request?.url.toString()
                        ImmersiveActivity.instance.get()?.DB?.updateWebViewURL(uuid, _url)
                        addressBar?.setText(_url)
                        return false // Allow the WebView to load the URL
                      }
                    }

                // This will load the url in the web view
                webView?.loadUrl(url)

                // Handles the previous urls
                val backButton: ImageButton? =
                    rootView?.findViewById<ImageButton>(R.id.backWebViewButton)
                backButton?.setOnClickListener {
                  if (webView?.canGoBack() == true) {
                    webView.goBack()
                    ImmersiveActivity.instance
                        .get()
                        ?.DB
                        ?.updateWebViewURL(uuid, webView.originalUrl.toString())
                    addressBar?.setText(webView.originalUrl)
                  } else if (url != webView?.url) {
                    webView?.loadUrl(url)
                    ImmersiveActivity.instance.get()?.DB?.updateWebViewURL(uuid, url)
                    addressBar?.setText(url)
                  }
                }

                val refreshButton: ImageButton? =
                    rootView?.findViewById<ImageButton>(R.id.refreshWebViewButton)
                refreshButton?.setOnClickListener {
                  webView?.loadUrl(webView.url.toString())
                  addressBar?.setText(webView?.url.toString())
                }

                val deleteButton: ImageButton? =
                    rootView?.findViewById<ImageButton>(R.id.deleteWebViewButton)
                deleteButton?.setOnClickListener {
                  ImmersiveActivity.instance.get()?.DB?.deleteToolAsset(uuid)
                  this.entity?.destroy()
                }
              }
            })

    val ent: Entity =
        Entity.createPanelEntity(id, Transform(pose), Grabbable(true, GrabbableType.FACE))

    // If this is a new Web View, we create it in the database as well and place it in front of user
    if (uuid == -1) {
      if (pose == Pose()) placeInFront(ent, bigPanel = true)
      uuid = getNewUUID()
      ImmersiveActivity.instance
          .get()
          ?.DB
          ?.createToolAsset(
              uuid,
              ImmersiveActivity.instance.get()?.currentProject?.uuid,
              AssetType.WEB_VIEW,
              url,
              0f, // not relevant
              0f, // not relevant
              ent.getComponent<Transform>().transform)
    }

    // ToolComponent is added to web view to save properties and identify it
    ent.setComponent(ToolComponent(uuid, AssetType.WEB_VIEW, Vector3(0f, 0.3f, -0.005f)))
  }
}
