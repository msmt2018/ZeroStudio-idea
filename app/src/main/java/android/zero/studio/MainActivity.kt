package android.zero.studio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.zero.studio.editor.impl.InMemoryEditorWorkspace

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { EditorCoreHomeScreen() }
    }
}

@Composable
private fun EditorCoreHomeScreen() {
    var status by remember { mutableStateOf("Editor Core Ready") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = status, style = MaterialTheme.typography.titleMedium)
        Button(onClick = {
            status = runCatching {
                // 仅验证 editor-core 能被 app 层装配调用
                InMemoryEditorWorkspace().hashCode()
                "Editor Workspace Bootstrapped"
            }.getOrElse { "Bootstrap Failed: ${it.message}" }
        }) {
            Text("Bootstrap Editor Core")
        }
    }
}
