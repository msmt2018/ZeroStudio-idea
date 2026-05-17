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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import android.zero.studio.editor.editor.EditorHostViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { EditorHostScreen() }
    }
}

@Composable
private fun EditorHostScreen(vm: EditorHostViewModel = viewModel()) {
    val ui by vm.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = ui.status, style = MaterialTheme.typography.titleMedium)
        Text(text = "Open: ${ui.openCount}")
        Text(text = "Active: ${ui.activeFile ?: "<none>"}")
        Text(text = "Report: ${ui.lastReportPreview}")

        Button(onClick = { vm.open("/tmp/demo.txt") }) { Text("Open Demo") }
        Button(onClick = { vm.search("hello") }) { Text("Search hello") }
        Button(onClick = { vm.replaceHelloToHi() }) { Text("Replace hello->hi") }
        Button(onClick = { vm.saveDirty() }) { Text("Save Dirty") }
        Button(onClick = { vm.exportReport() }) { Text("Export Report") }
    }
}
