package com.swiftygif

import android.graphics.Color
import android.graphics.drawable.Animatable
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.ReactStylesDiffMap
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewProps
import com.facebook.react.uimanager.annotations.ReactProp

class SwiftyGifViewManager : SimpleViewManager<FrameLayout>() {
  override fun getName() = "SwiftyGifView"

  override fun createViewInstance(reactContext: ThemedReactContext): FrameLayout {
    Fresco.initialize(reactContext)
    val frameLayout = FrameLayout(reactContext)
    val draweeView = SimpleDraweeView(reactContext)
    val progressBar = ProgressBar(reactContext)
    progressBar.visibility = View.GONE
    frameLayout.addView(draweeView)
    frameLayout.addView(progressBar)
    return frameLayout
  }

  @ReactProp(name = "style")
  fun setStyle(view: FrameLayout, style: ReadableMap) {
    applyStyles(view, ReactStylesDiffMap(style))
  }

  @ReactProp(name = "source")
  fun setGifUrl(view: FrameLayout, url: String) {
    val draweeView = view.getChildAt(0) as SimpleDraweeView
    val progressBar = view.getChildAt(1) as ProgressBar
    progressBar.visibility = View.VISIBLE
    val controller: DraweeController = Fresco.newDraweeControllerBuilder()
            .setUri(url)
            .setControllerListener(object : BaseControllerListener<ImageInfo>() {
              override fun onFinalImageSet(
                      id: String?,
                      imageInfo: ImageInfo?,
                      animatable: Animatable?
              ) {
                Log.d("SwiftyGifViewManager", "GIF loaded successfully")
                progressBar.visibility = View.GONE
                if (view.getTag(R.id.paused) != true) {
                  animatable?.start()
                }
              }

              override fun onFailure(id: String?, throwable: Throwable?) {
                progressBar.visibility = View.GONE
                // Handle failure
              }
            })
            .build()
    draweeView.controller = controller
  }

  @ReactProp(name = "paused")
  fun setPaused(view: FrameLayout, paused: Boolean) {
    view.setTag(R.id.paused, paused)
    val draweeView = view.getChildAt(0) as SimpleDraweeView
    val animatable = draweeView.controller?.animatable
    if (paused) {
      animatable?.stop()
    } else {
      animatable?.start()
    }
  }

  @ReactProp(name = "loadingView")
  fun setLoadingView(view: FrameLayout, loadingView: Boolean) {
    val progressBar = view.getChildAt(1) as ProgressBar
    progressBar.visibility = if (loadingView) View.VISIBLE else View.GONE
  }

  @ReactProp(name = "resizeMode")
  fun setResizeMode(view: FrameLayout, resizeMode: String) {
    val draweeView = view.getChildAt(0) as SimpleDraweeView
    val scaleType = when (resizeMode) {
      "contain" -> ScalingUtils.ScaleType.FIT_CENTER
      "cover" -> ScalingUtils.ScaleType.CENTER_CROP
      "stretch" -> ScalingUtils.ScaleType.FIT_XY
      else -> ScalingUtils.ScaleType.CENTER_INSIDE
    }
    draweeView.hierarchy.actualImageScaleType = scaleType
  }

  private fun applyStyles(view: FrameLayout, props: ReactStylesDiffMap) {

    // Handle other style properties as needed
  }
}