package com.hinataku.statscounter.ui.stats

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import android.widget.FrameLayout
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.FileProvider
import com.hinataku.statscounter.App
import com.hinataku.statscounter.ui.stats.blocks.StatsShareCard
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object StatsShareManager {
  private const val ShareImageWidthDp = 800

  fun shareStatsImage(
    activity: Activity,
    gameName: String,
    uiState: StatsUiState,
  ) {
    val bitmap = createBitmap(activity, gameName, uiState) ?: return
    val imageUri = saveBitmap(activity, bitmap) ?: return
    shareImage(activity, imageUri, gameName)
  }

  fun saveStatsImage(
    activity: Activity,
    gameName: String,
    uiState: StatsUiState,
  ) {
    val bitmap = createBitmap(activity, gameName, uiState) ?: return
    val savedUri = saveBitmapToGallery(activity, bitmap, gameName)
    Toast.makeText(
      activity,
      if (savedUri != null) "画像を保存しました" else "画像の保存に失敗しました",
      Toast.LENGTH_SHORT,
    ).show()
  }

  private fun createBitmap(
    activity: Activity,
    gameName: String,
    uiState: StatsUiState,
  ): Bitmap? {
    val contentRoot = activity.findViewById<ViewGroup>(android.R.id.content) ?: return null
    val density = activity.resources.displayMetrics.density
    val widthPx = (ShareImageWidthDp * density).toInt()
    val composeView = ComposeView(activity).apply {
      layoutParams = FrameLayout.LayoutParams(
        widthPx,
        ViewGroup.LayoutParams.WRAP_CONTENT,
      )
      visibility = View.INVISIBLE
      setContent {
        App {
          StatsShareCard(gameName = gameName, uiState = uiState)
        }
      }
    }

    contentRoot.addView(composeView)
    try {
      val widthSpec = View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY)
      val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
      composeView.measure(widthSpec, heightSpec)
      val measuredWidth = composeView.measuredWidth
      val measuredHeight = composeView.measuredHeight
      if (measuredWidth <= 0 || measuredHeight <= 0) return null
      composeView.layout(0, 0, measuredWidth, measuredHeight)

      return Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888).also { bitmap ->
        composeView.draw(Canvas(bitmap))
      }
    } finally {
      contentRoot.removeView(composeView)
    }
  }

  private fun saveBitmap(context: Context, bitmap: Bitmap): Uri? {
    val shareDir = File(context.cacheDir, "shared_stats").apply { mkdirs() }
    val imageFile = File(shareDir, "stats_share.png")
    FileOutputStream(imageFile).use { output ->
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
    }
    return FileProvider.getUriForFile(
      context,
      "${context.packageName}.fileprovider",
      imageFile,
    )
  }

  private fun shareImage(context: Context, imageUri: Uri, gameName: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
      type = "image/png"
      putExtra(Intent.EXTRA_STREAM, imageUri)
      putExtra(Intent.EXTRA_TEXT, gameName)
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(sendIntent, "スタッツを共有"))
  }

  private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, gameName: String): Uri? {
    val resolver = context.contentResolver
    val safeName = gameName
      .trim()
      .ifBlank { "stats" }
      .replace(Regex("[\\\\/:*?\"<>|]"), "_")
    val fileName = "${safeName}_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}.png"
    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
      MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }
    val values = ContentValues().apply {
      put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
      put(MediaStore.Images.Media.MIME_TYPE, "image/png")
      put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
      put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/StatsCounter")
        put(MediaStore.Images.Media.IS_PENDING, 1)
      }
    }
    val uri = resolver.insert(collection, values) ?: return null
    return runCatching {
      resolver.openOutputStream(uri)?.use { output ->
        check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output))
      } ?: error("Output stream unavailable")
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val publishValues = ContentValues().apply {
          put(MediaStore.Images.Media.IS_PENDING, 0)
        }
        resolver.update(uri, publishValues, null, null)
      }
      uri
    }.getOrElse {
      resolver.delete(uri, null, null)
      null
    }
  }
}
