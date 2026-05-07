package com.hinataku.statscounter.ui.stats

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGContextFillRect
import platform.CoreGraphics.CGContextRestoreGState
import platform.CoreGraphics.CGContextSaveGState
import platform.CoreGraphics.CGContextSetLineWidth
import platform.CoreGraphics.CGContextStrokeRect
import platform.CoreGraphics.CGContextTranslateCTM
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Photos.PHAssetChangeRequest
import platform.Photos.PHPhotoLibrary
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIImage
import platform.UIKit.UILabel
import platform.UIKit.UIViewController

@OptIn(ExperimentalForeignApi::class)
object StatsShareManager {
    private const val ImageWidth = 800.0
    private const val Padding = 20.0
    private const val NameW = 120.0
    private const val CellW = 80.0
    private const val TitleH = 36.0
    private const val GapH = 16.0
    private const val HeaderH = 44.0
    private const val RowH = 60.0
    private const val TotalH = 52.0

    fun shareStatsImage(gameName: String, uiState: StatsUiState) {
        val image = buildImage(gameName, uiState)
        val activityVC = UIActivityViewController(
            activityItems = listOf(image),
            applicationActivities = null,
        )
        rootViewController()?.presentViewController(activityVC, animated = true, completion = null)
    }

    fun saveStatsImage(gameName: String, uiState: StatsUiState) {
        val image = buildImage(gameName, uiState)
        PHPhotoLibrary.sharedPhotoLibrary().performChanges({
            PHAssetChangeRequest.creationRequestForAssetFromImage(image)
        }, completionHandler = null)
    }

    // Compose の Metal レンダリングは UIKit スナップショット API で取得できないため
    // CoreGraphics + UILabel で直接描画する
    private fun buildImage(gameName: String, uiState: StatsUiState): UIImage {
        val imageHeight = Padding + TitleH + GapH + HeaderH +
                (uiState.players.size * RowH) + TotalH + Padding

        val widths = listOf(NameW) + List(8) { CellW }
        val colXs = widths.runningFold(Padding) { acc, w -> acc + w }.dropLast(1)
        val headerY = Padding + TitleH + GapH
        val totalY = headerY + HeaderH + uiState.players.size * RowH
        val headers = listOf("名前", "2P", "3P", "PTS", "AST", "REB", "BLK", "STL", "TO")

        val bg = rgba(0.973, 0.980, 0.988)
        val dark = rgba(0.067, 0.094, 0.153)
        val white = rgba(1.0, 1.0, 1.0)
        val headerBg = rgba(0.898, 0.906, 0.922)
        val headerBorder = rgba(0.612, 0.639, 0.686)
        val cellBorder = rgba(0.820, 0.835, 0.859)
        val ptsBg = rgba(1.0, 0.969, 0.929)
        val ptsText = rgba(0.918, 0.345, 0.047)

        return UIGraphicsImageRenderer(size = CGSizeMake(ImageWidth, imageHeight)).imageWithActions { _ ->
            // 背景
            fillRect(0.0, 0.0, ImageWidth, imageHeight, bg)

            // タイトル
            drawLabel(gameName, Padding, Padding, ImageWidth - Padding * 2, TitleH, 24.0, dark)

            // ヘッダー行
            colXs.forEachIndexed { i, cx ->
                fillRect(cx, headerY, widths[i], HeaderH, headerBg)
                strokeRect(cx, headerY, widths[i], HeaderH, headerBorder)
                drawLabel(headers[i], cx, headerY, widths[i], HeaderH, 13.0, dark)
            }

            // 選手行
            uiState.players.forEachIndexed { pi, player ->
                val rowY = headerY + HeaderH + pi * RowH
                val vals = listOf(
                    player.name,
                    player.twoPointMade.toString(),
                    player.threePointMade.toString(),
                    player.points.toString(),
                    player.assist.toString(),
                    player.rebound.toString(),
                    player.block.toString(),
                    player.steal.toString(),
                    player.turnover.toString(),
                )
                colXs.forEachIndexed { i, cx ->
                    val isPts = i == 3
                    fillRect(cx, rowY, widths[i], RowH, if (isPts) ptsBg else white)
                    strokeRect(cx, rowY, widths[i], RowH, cellBorder)
                    drawLabel(vals[i], cx, rowY, widths[i], RowH,
                        if (i == 0) 16.0 else 28.0,
                        if (isPts) ptsText else dark)
                }
            }

            // 合計行
            val totals = listOf(
                "合計",
                uiState.players.sumOf { it.twoPointMade }.toString(),
                uiState.players.sumOf { it.threePointMade }.toString(),
                uiState.players.sumOf { it.points }.toString(),
                uiState.players.sumOf { it.assist }.toString(),
                uiState.players.sumOf { it.rebound }.toString(),
                uiState.players.sumOf { it.block }.toString(),
                uiState.players.sumOf { it.steal }.toString(),
                uiState.players.sumOf { it.turnover }.toString(),
            )
            colXs.forEachIndexed { i, cx ->
                fillRect(cx, totalY, widths[i], TotalH, dark)
                strokeRect(cx, totalY, widths[i], TotalH, white)
                drawLabel(totals[i], cx, totalY, widths[i], TotalH, 16.0, white)
            }
        }
    }

    private fun rgba(r: Double, g: Double, b: Double) =
        UIColor(red = r, green = g, blue = b, alpha = 1.0)

    private fun fillRect(x: Double, y: Double, w: Double, h: Double, color: UIColor) {
        val ctx = UIGraphicsGetCurrentContext() ?: return
        color.setFill()
        CGContextFillRect(ctx, CGRectMake(x, y, w, h))
    }

    private fun strokeRect(x: Double, y: Double, w: Double, h: Double, color: UIColor) {
        val ctx = UIGraphicsGetCurrentContext() ?: return
        color.setStroke()
        CGContextSetLineWidth(ctx, 0.5)
        CGContextStrokeRect(ctx, CGRectMake(x, y, w, h))
    }

    private fun drawLabel(text: String, x: Double, y: Double, w: Double, h: Double, fontSize: Double, color: UIColor) {
        val ctx = UIGraphicsGetCurrentContext() ?: return
        val label = UILabel()
        label.text = text
        label.font = UIFont.boldSystemFontOfSize(fontSize)
        label.textColor = color
        label.textAlignment = NSTextAlignmentCenter
        label.backgroundColor = UIColor(red = 0.0, green = 0.0, blue = 0.0, alpha = 0.0)
        label.setFrame(CGRectMake(0.0, 0.0, w, h))
        CGContextSaveGState(ctx)
        CGContextTranslateCTM(ctx, x, y)
        label.layer.renderInContext(ctx)
        CGContextRestoreGState(ctx)
    }

    private fun rootViewController(): UIViewController? =
        UIApplication.sharedApplication.keyWindow?.rootViewController
}
