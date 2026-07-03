package pt.ipbeja.gestaocompras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import pt.ipbeja.gestaocompras.ui.nav.AppNavigation
import pt.ipbeja.gestaocompras.ui.theme.GestaoComprasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Opticart() }
    }
}

@Composable
private fun Opticart() {
    GestaoComprasTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavigation()
        }
    }
}
