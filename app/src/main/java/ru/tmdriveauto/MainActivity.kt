package ru.tmdriveauto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "filter_settings")
private val KEYWORDS = stringPreferencesKey("keywords")
private val MIN_PRICE = floatPreferencesKey("min_price")
private val FILTER_ENABLED = booleanPreferencesKey("filter_enabled")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { FilterSettingsScreen() } }
    }
}

@Composable
private fun FilterSettingsScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var keywordsText by remember { mutableStateOf("") }
    var minPriceText by remember { mutableStateOf("300") }
    var enabled by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val prefs = context.dataStore.data.first()
        keywordsText = prefs[KEYWORDS] ?: ""
        minPriceText = (prefs[MIN_PRICE] ?: 300f).toString().removeSuffix(".0")
        enabled = prefs[FILTER_ENABLED] ?: false
    }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("TMDrive Auto", style = MaterialTheme.typography.headlineMedium)
                Text("Настрой фильтр заказов. Пока это экран сохранения параметров; подключение к TMDrive ещё не реализовано.",
                    style = MaterialTheme.typography.bodyMedium)
            }
            item {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f)) {
                        Text("Фильтр включён", style = MaterialTheme.typography.titleMedium)
                        Text("Включение пока сохраняет настройку, но не принимает заказы автоматически.")
                    }
                    Switch(checked = enabled, onCheckedChange = { enabled = it; saved = false })
                }
            }
            item {
                OutlinedTextField(
                    value = minPriceText,
                    onValueChange = { value -> if (value.matches(Regex("^\\d{0,7}([.,]\\d{0,2})?$"))) { minPriceText = value; saved = false } },
                    label = { Text("Минимальная стоимость заказа, ₽") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Text("Ключевые слова", style = MaterialTheme.typography.titleMedium)
                Text("Вводи слова через запятую. Совпадение хотя бы одного слова в адресе подачи или назначения будет считаться подходящим.")
                OutlinedTextField(
                    value = keywordsText,
                    onValueChange = { keywordsText = it; saved = false },
                    label = { Text("Например: аэропорт, центр, вокзал") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
            item {
                Button(onClick = {
                    val price = minPriceText.replace(',', '.').toFloatOrNull() ?: 0f
                    scope.launch {
                        context.dataStore.edit { prefs ->
                            prefs[KEYWORDS] = keywordsText
                            prefs[MIN_PRICE] = price
                            prefs[FILTER_ENABLED] = enabled
                        }
                        saved = true
                    }
                }, modifier = Modifier.fillMaxWidth()) { Text("Сохранить настройки") }
                if (saved) Text("Настройки сохранены на устройстве.", color = MaterialTheme.colorScheme.primary)
            }
            item {
                Text("Следующий этап: безопасное получение данных о заказе и проверка условий. Приложение не нажимает кнопки в TMDrive и не обходит ограничения сервиса.",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
