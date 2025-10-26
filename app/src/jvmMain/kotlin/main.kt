import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kyant.capsule.demo.MainContent

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "MyApplication",
    ) {
        MainContent()
    }
}