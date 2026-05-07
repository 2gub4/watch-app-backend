package com.js.backendassembly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.js.backendassembly.data.api.ApiManager
import com.js.backendassembly.data.api.EndpointType
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ApiTesterScreen()
                }
            }
        }
    }
}

enum class QueryMode(val displayName: String) {
    MOVIE("Film (JSON)"),
    LIST("Lista (JSON)"),
    POSTER("Plakat (Obraz)"),
    HOMEPAGE_LIST("Filmy na strone glowna (JSON)")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiTesterScreen() {
    var endpointInput by remember { mutableStateOf("popular") }
    var resultText by remember { mutableStateOf("Wynik pojawi się tutaj") }
    var requestedUrl by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedMode by remember { mutableStateOf(QueryMode.MOVIE) }
    var currentImageUrl by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val pageNumber = "1"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedMode.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Zasób", fontSize = 18.sp) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                textStyle = TextStyle(fontSize = 24.sp),
                modifier = Modifier
                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                    .fillMaxWidth()
                    .height(80.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                QueryMode.entries.forEach { mode ->
                    DropdownMenuItem(
                        text = {
                            Text(mode.displayName, fontSize = 22.sp, modifier = Modifier.padding(vertical = 8.dp))
                        },
                        onClick = {
                            selectedMode = mode
                            expanded = false
                            endpointInput = when (mode) {
                                QueryMode.MOVIE -> "11"
                                QueryMode.LIST -> "1"
                                QueryMode.POSTER -> "/pWVLFh4OuejTpUaDQbB1C4zoS2p.jpg"
                                QueryMode.HOMEPAGE_LIST -> "now_playing?page=$pageNumber"
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = endpointInput,
            onValueChange = { endpointInput = it },
            label = { Text("ID, path lub parametr", fontSize = 18.sp) },
            textStyle = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                requestedUrl = ""
                currentImageUrl = null
                resultText = "⏳ Przetwarzanie..."

                // NAPRAWIONO LOGIKĘ: Obsługa wszystkich 3 trybów poprawnie
                val endpointType = when (selectedMode) {
                    QueryMode.MOVIE -> EndpointType.MOVIE
                    QueryMode.LIST -> EndpointType.LIST
                    QueryMode.POSTER -> EndpointType.POSTER
                    QueryMode.HOMEPAGE_LIST -> EndpointType.HOMEPAGE_LIST
                }

                coroutineScope.launch {
                    val result = ApiManager.fetchApiData(endpointType, endpointInput)
                    result.fold(
                        onSuccess = { apiResult ->
                            requestedUrl = apiResult.fullUrl

                            // NOWA LOGIKA: React on info from backend
                            if (apiResult.isImage) {
                                currentImageUrl = apiResult.fullUrl // Przekazujemy url obrazu do Coila
                                resultText = ""
                            } else {
                                currentImageUrl = null
                                resultText = apiResult.responseText // Wyświetlamy tekst JSON
                            }
                        },
                        onFailure = { error ->
                            resultText = "❌ Błąd:\n${error.localizedMessage}"
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Text("PRZETESTUJ", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            if (requestedUrl.isNotEmpty()) {
                Text(
                    text = "WYSŁANO NA:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = requestedUrl,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (currentImageUrl != null) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = currentImageUrl,
                        contentDescription = "Pobrany plakat",
                        modifier = Modifier.wrapContentHeight()
                    )
                    AsyncImage(
                        model = currentImageUrl,
                        contentDescription = "Pobrany plakat",
                        modifier = Modifier.wrapContentHeight()
                    )
                    AsyncImage(
                        model = currentImageUrl,
                        contentDescription = "Pobrany plakat",
                        modifier = Modifier.wrapContentHeight()
                    )
                }
            } else {
                Text(
                    text = resultText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}