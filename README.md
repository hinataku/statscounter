# Stats Counter App

Compose Multiplatform 形式のプロジェクトとして開ける、スタッツ集計アプリです。

## 機能

- 選手名の入力
- 2P / 3P / A / R / B / S / T の入力
- PTS = 2P × 2 + 3P × 3 を自動計算
- 各項目の + / - ボタン
- 選手追加・削除
- 合計行表示

## 開き方

1. ZIPを解凍
2. Android Studio / IntelliJ でこのプロジェクトフォルダを開く
3. Gradle Sync
4. Android 実行時は `app` を Run

Android シェルは `app`、共通コードは `shared` に配置しています。

## iOS

- Xcode では `iosApp/iosApp.xcodeproj` を開く
- `iosApp` は `shared` の Compose UI を表示します
