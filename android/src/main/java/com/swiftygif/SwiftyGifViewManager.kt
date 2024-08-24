package com.swiftygif

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
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.ReactStylesDiffMap
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.facebook.react.common.MapBuilder

class SwiftyGifViewManager : SimpleViewManager<FrameLayout>() {
  override fun getName() = "SwiftyGifView"

  override fun createViewInstance(reactContext: ThemedReactContext): FrameLayout {
    Fresco.initialize(reactContext)
    val frameLayout = FrameLayout(reactContext)
    val draweeView = SimpleDraweeView(reactContext)
    sendEvent(frameLayout, EVENT_ON_LOAD_START, null)
    frameLayout.addView(draweeView)
    return frameLayout
  }

  @ReactProp(name = "style")
  fun setStyle(view: FrameLayout, style: ReadableMap) {
    applyStyles(view, ReactStylesDiffMap(style))
  }

  @ReactProp(name = "source")
  fun setGifUrl(view: FrameLayout, url: String) {
    sendEvent(view, EVENT_ON_LOAD_START, null)
    val draweeView = view.getChildAt(0) as SimpleDraweeView
    draweeView.controller = getDraweeController(url, view)
  }

  private fun getDraweeController(url: String, view: FrameLayout): DraweeController {
    val controller: DraweeController = Fresco.newDraweeControllerBuilder()
      .setUri(url)
      .setControllerListener(object : BaseControllerListener<ImageInfo>() {
        override fun onFinalImageSet(
          id: String?,
          imageInfo: ImageInfo?,
          animatable: Animatable?
        ) {
          Log.d("SwiftyGifViewManager", "GIF loaded successfully")
          sendEvent(view, EVENT_ON_LOAD_GIF_END, null)
          if (view.getTag(R.id.paused) != true) {
            animatable?.start()
          }
        }

        override fun onFailure(id: String?, throwable: Throwable?) {
          sendEvent(
            view,
            EVENT_ON_LOAD_GIF_ERROR,
            Arguments.createMap().apply {
              putString("error", throwable?.message)
            }
          )
          // Handle failure
        }
      })
      .build()
    return controller
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

  }

  override fun getExportedCustomDirectEventTypeConstants(): Map<String?, Any?>? {
    return createExportedCustomDirectEventTypeConstants()
  }

  private fun createExportedCustomDirectEventTypeConstants(): Map<String?, Any?>? {
    return MapBuilder.builder<String?, Any?>()
      .put(EVENT_ON_LOAD_GIF_END, MapBuilder.of("registrationName", EVENT_ON_LOAD_GIF_END))
      .put(EVENT_ON_LOAD_GIF_ERROR, MapBuilder.of("registrationName", EVENT_ON_LOAD_GIF_ERROR))
      .put(EVENT_ON_LOAD_START, MapBuilder.of("registrationName", EVENT_ON_LOAD_START))
      .build()
  }

  companion object {
    private const val COMMAND_SET_TEXT = "setText"
    private const val COMMAND_SET_TEXT_ID = 1

    const val EVENT_ON_LOAD_GIF_END = "onLoadGifEnd"
    const val EVENT_ON_LOAD_GIF_ERROR = "onLoadGifError"
    const val EVENT_ON_LOAD_START = "onLoadGifStart"
  }

  private fun sendEvent(view: FrameLayout, eventName: String, params: WritableMap?) {
    val reactContext = view.context as ReactContext
    reactContext.getJSModule(RCTEventEmitter::class.java)
      .receiveEvent(view.id, eventName, params)
  }
}
