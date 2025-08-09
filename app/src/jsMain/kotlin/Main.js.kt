import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.kyant.capsule.demo.MainContent
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onWasmReady {
        ComposeViewport(
            viewportContainerId = "composeApplication"
        ) {
            hideLoading()
            MainContent()
        }
    }
}

external fun hideLoading()
