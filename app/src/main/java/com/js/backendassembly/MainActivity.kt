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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.js.backendassembly.data.api.ApiManager
import com.js.backendassembly.data.api.EndpointType
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    // KLUCZ DO TESTÓW (w produkcji należy go ukryć!)
    private val apiKey = "7a0cf0cb349b8912480426231b4faf51"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ApiTesterScreen(apiKey)
                }
            }
        }
    }
}

// Tryby pracy UI - zachowane bez zmian
enum class QueryMode(val displayName: String) {
    MOVIE("Film (JSON)"),
    LIST("Lista (JSON)"),
    POSTER("Plakat (Obraz)")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiTesterScreen(apiKey: String) {
    // --- STANY (zachowane bez zmian) ---
    var endpointInput by remember { mutableStateOf("popular") }
    var resultText by remember { mutableStateOf("Wynik pojawi się tutaj") }
    var requestedUrl by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedMode by remember { mutableStateOf(QueryMode.MOVIE) }
    var currentImageUrl by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val state = rememberScrollState() // State dla scrollowania całej kolumny
    val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w200" // Wersja 780 dla lepszej jakości na dużym UI

    // GŁÓWNA KOLUMNA - teraz układamy elementy od GÓRY
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp), // Boczne dopełnienie
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 1. PRZESUNIĘCIE INTERFEJSU W DÓŁ ---
        Spacer(modifier = Modifier.height(64.dp))

        // --- 2. POWIĘKSZONE DROPDOWN MENU ---
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedMode.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Zasób", fontSize = 18.sp) }, // Powiększona etykieta
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                // POWIĘKSZONY TEKST I WYSOKOŚĆ
                textStyle = TextStyle(fontSize = 24.sp),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .height(80.dp), // Zwiększona wysokość pola
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                QueryMode.values().forEach { mode ->
                    DropdownMenuItem(
                        text = {
                            // POWIĘKSZONY TEKST W MENU
                            Text(mode.displayName, fontSize = 22.sp, modifier = Modifier.padding(vertical = 8.dp))
                        },
                        onClick = {
                            selectedMode = mode
                            expanded = false
                            // Logika podpowiedzi - zachowana
                            endpointInput = when (mode) {
                                QueryMode.MOVIE -> "popular"
                                QueryMode.LIST -> "1"
                                QueryMode.POSTER -> "/yUiXA68FfQeA8cRBhd0Ao0jIRZt.jpg"
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- 3. POWIĘKSZONE POLE TEKSTOWE DANYCH ---
        OutlinedTextField(
            value = endpointInput,
            onValueChange = { endpointInput = it },
            label = { Text("ID, path lub parametr", fontSize = 18.sp) },
            // POWIĘKSZONY TEKST I WYSOKOŚĆ
            textStyle = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp), // Zwiększona wysokość
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- 4. POTĘŻNY PRZYCISK ---
        Button(
            onClick = {
                // Logika przycisku - zachowana bez zmian
                requestedUrl = ""
                currentImageUrl = null
                resultText = "⏳ Przetwarzanie..."

                if (selectedMode == QueryMode.POSTER) {
                    val fullImageUrl = "$IMAGE_BASE_URL$endpointInput"
                    requestedUrl = fullImageUrl
                    currentImageUrl = fullImageUrl
                    resultText = "Ładowanie grafiki..."
                } else {
                    val endpointType = if (selectedMode == QueryMode.MOVIE) EndpointType.MOVIE else EndpointType.LIST
                    coroutineScope.launch {
                        val result = ApiManager.fetchRawJson(endpointType, endpointInput, apiKey)
                        result.fold(
                            onSuccess = { apiResult ->
                                requestedUrl = apiResult.fullUrl
                                resultText = apiResult.jsonBody
                            },
                            onFailure = { error ->
                                resultText = "❌ Błąd:\n${error.localizedMessage}"
                            }
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp), // Bardzo wysoki przycisk
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            // POTĘŻNY TEKST NA PRZYCISKU
            Text("PRZETESTUJ", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(16.dp))

        // --- 5. SEKCJA WYNIKÓW Z SCOLLEM ---
        // Używamy .weight(1f), aby ta kolumna zajęła całą resztę ekranu
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()) // WŁĄCZAMY SCROLLOWANIE WYNIKÓW
                .padding(bottom = 24.dp)
        ) {
            // Podgląd URL (jeśli istnieje)
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

            // --- RENDEROWANIE WYNIKU (zachowana logika) ---
            if (currentImageUrl != null) {
                // Widok OBRAZU
                AsyncImage(
                    model = currentImageUrl,
                    contentDescription = "Pobrany plakat",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(), // Obraz zajmuje tyle wysokości, ile potrzebuje
                    contentScale = ContentScale.FillWidth // Skalujemy do szerokości
                )
            } else {
                // Widok JSON (Tekstowy)
                Text(
                    text = resultText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp, // Zwiększony font JSONa dla czytelności
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}