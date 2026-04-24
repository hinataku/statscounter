# Android Architecture

新規Androidプロジェクトのアーキテクチャ参考資料。MashCutプロジェクトで実証済みの構成。

---

## 技術スタック

| ライブラリ | バージョン | 用途 |
|-----------|-----------|------|
| AGP | 8.7.3 | Android Gradle Plugin |
| Kotlin | 2.1.0 | 言語 |
| Compose BOM | 2025.05.01 | Jetpack Compose UI |
| Navigation Compose | 2.8.9 | 型安全ナビゲーション |
| Koin | 4.1.1 | DI |
| kotlinx-serialization | kotlin同梱 | @Serializable（Navigation用） |
| kotlinx-coroutines-test | 1.9.0 | ユニットテスト |

### gradle.properties

```properties
android.useAndroidX=true
android.enableJetifier=true
```

### themes.xml

```xml
<style name="Theme.YourApp" parent="Theme.AppCompat.NoActionBar" />
```

`Theme.MaterialComponents` は依存が必要なので、不要なら `Theme.AppCompat.NoActionBar` を使う。

---

## Page / Template / Block アーキテクチャ

```
Destination  画面への入口。NavGraphへのノード登録とPageの呼び出しのみ
Page         ViewModelを知る唯一の層。BackHandler・LaunchedEffect・navigation処理を担当
Template     純粋なUI関数。ViewModelを知らない。@Preview付き
Block        Template内で使う再利用可能なUI部品。blocks/ディレクトリ。internal修飾子。@Preview付き
ViewModel    ビジネスロジックと状態管理。UI層（Compose）を知らない
```

### 層間の依存関係

```
Destination → Page → Template → Block
                ↕
            ViewModel
```

### データフロー

```
ViewModel ──[StateFlow<UiState>]──→ Page ──[引数]──→ Template ──[引数]──→ Block
   ↑                                  |
   └──────[イベント関数呼び出し]────────┘
          (onClickXxx, onChangeXxx)
```

---

## 各層の責務と禁止事項

### Destination
```kotlin
@Serializable
data class XxxDestination(val id: String) : SafeArgDestination() {
  override fun createNode(builder: NavGraphBuilder) {
    builder.composable<XxxDestination> {
      XxxPage(it.toRoute<XxxDestination>().id)
    }
  }
}
```
- **禁止**: UIロジック、ViewModelの直接参照

### Page
```kotlin
@Composable
fun XxxPage(id: String) {
  val viewModel: XxxViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val navController = LocalNavController.current

  val navigateTo by viewModel.navigateToStateFlow.collectAsStateWithLifecycle()
  LaunchedEffect(navigateTo) {
    navigateTo?.let { it.navigate(navController); viewModel.completeNavigation() }
  }

  BackHandler { viewModel.onPopBack() }

  XxxTemplate(
    uiState = uiState,
    onClickSomething = viewModel::onClickSomething,
  )
}
```
- **禁止**: UIの直接描画、ビジネスロジックの記述

### Template
```kotlin
@Composable
fun XxxTemplate(
  uiState: XxxUiState,
  onClickSomething: () -> Unit,
) {
  // UIのみ。ViewModelを参照しない
}

@Preview(showBackground = true)
@Composable
private fun XxxTemplatePreview() {
  YourAppTheme {
    XxxTemplate(uiState = XxxUiState(), onClickSomething = {})
  }
}
```
- **禁止**: ViewModelの参照、NavControllerの参照、副作用（LaunchedEffect等）

### Block
```kotlin
// blocks/ ディレクトリに配置
@Composable
internal fun SomeBlock(
  value: String,
  onClick: () -> Unit,
) {
  // Modifierパラメータを持たない
  // サイズ・配置は自身で制御
}
```
- **禁止**: ViewModelの参照、Modifierパラメータの受け取り
- 呼び出し側でweightが必要な場合: `Box(Modifier.weight(1f)) { SomeBlock(...) }`

### ViewModel
```kotlin
class XxxViewModel(
  private val repository: XxxRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(XxxUiState())
  val uiState: StateFlow<XxxUiState> = _uiState.asStateFlow()

  private val _navigateToStateFlow = MutableStateFlow<Destination?>(null)
  val navigateToStateFlow: StateFlow<Destination?> = _navigateToStateFlow.asStateFlow()

  fun completeNavigation() { _navigateToStateFlow.value = null }
  fun onPopBack() { _navigateToStateFlow.value = PopBackDestination }

  fun onClickSomething() {
    _uiState.update { it.copy(...) }
  }
}
```
- **禁止**: Composable呼び出し、Context/NavControllerの保持（ApplicationContextは除く）

---

## UiState 設計

```kotlin
// 基本形（data class）
data class XxxUiState(
  val items: List<Item> = emptyList(),
  val isLoading: Boolean = false,
  val errorText: String? = null,
)

// フェーズが必要な場合のみ sealed interface
sealed interface XxxUiState {
  data object Loading : XxxUiState
  data class Success(val items: List<Item>) : XxxUiState
  data class Error(val message: String) : XxxUiState
}
```

---

## ナビゲーション基盤

### Destination.kt
```kotlin
interface Destination {
  fun navigate(navController: NavController)
}

@Serializable
abstract class SafeArgDestination : Destination {
  abstract fun createNode(builder: NavGraphBuilder)
  override fun navigate(navController: NavController) {
    navController.navigate(this)
  }
}
```

### PopBackDestination.kt
```kotlin
object PopBackDestination : Destination {
  override fun navigate(navController: NavController) {
    navController.popBackStack()
  }
}
```

### LocalNavController.kt
```kotlin
val LocalNavController = compositionLocalOf<NavController> {
  error("NavController not provided")
}
```

### AppNavigation.kt
```kotlin
@Composable
fun AppNavigation() {
  val navController = rememberNavController()
  CompositionLocalProvider(LocalNavController provides navController) {
    NavHost(navController = navController, startDestination = XxxDestination) {
      XxxDestination.createNode(this)
      YyyDestination.createNode(this)
    }
  }
}
```

---

## StateFlow 命名規則

| 用途 | 命名 |
|------|------|
| UI状態 | `_uiState` / `uiState` |
| ナビゲーション先 | `_navigateToStateFlow` / `navigateToStateFlow` |
| ナビゲーション完了 | `completeNavigation()` |
| 戻る操作 | `onPopBack()` |
| ユーザーイベント | `onXxx()` （例: `onClickReset()`, `onChangeText()`）|

---

## ファイル構成

```
app/src/main/java/com/example/yourapp/
├── YourApplication.kt               Koin 初期化
├── MainActivity.kt
├── data/
│   └── YourRepository.kt
├── di/
│   └── AppModule.kt
└── ui/
    ├── navigation/
    │   ├── AppNavigation.kt
    │   ├── Destination.kt
    │   ├── LocalNavController.kt
    │   └── PopBackDestination.kt
    └── featurename/
        ├── FeatureDestination.kt
        ├── FeaturePage.kt
        ├── FeatureTemplate.kt
        ├── FeatureViewModel.kt
        └── blocks/
            └── SomeBlock.kt
```
