package com.example.piediabeticov1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.piediabeticov1.ui.theme.PiediabeticoV1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PiediabeticoV1Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var score by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()

    // Variables para los rangos y checkboxes
    val ageState = remember { mutableStateOf(-1) }
    val hba1cState = remember { mutableStateOf(-1) }
    val ldlState = remember { mutableStateOf(-1) }
    val cholesterolState = remember { mutableStateOf(-1) }
    val smokerState = remember { mutableStateOf(-1) }
    val alcoholState = remember { mutableStateOf(-1) }

    var calculating by remember { mutableStateOf(false) }
    var finalScore by remember { mutableStateOf<Int?>(null) }
    var riskCategory by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "PREDICCIÓN PIE DIABETICO",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Sección de Edad
        Section(title = "¿En cuál de los siguientes rangos se encuentra la edad del paciente?") {
            DropdownMenuItem(text = "Menos de 40", value = 0, selectedValue = ageState)
            DropdownMenuItem(text = "Entre 40 y 45", value = 1, selectedValue = ageState)
            DropdownMenuItem(text = "46-50", value = 2, selectedValue = ageState)
            DropdownMenuItem(text = "Mayor igual a 51", value = 3, selectedValue = ageState)
        }

        // Sección de HbA1c
        Section(title = "¿En cuál de los siguientes rangos se encuentra la HbA1c del paciente?") {
            DropdownMenuItem(text = "Menos de 7.5", value = 0, selectedValue = hba1cState)
            DropdownMenuItem(text = "Entre 7.5 y 9", value = 1, selectedValue = hba1cState)
            DropdownMenuItem(text = "Entre 9.5 y 11", value = 2, selectedValue = hba1cState)
            DropdownMenuItem(text = "Mayor igual a 11.5", value = 3, selectedValue = hba1cState)
        }

        // Sección de LDL
        Section(title = "¿En cuál de los siguientes rangos se encuentra la LDL del paciente?") {
            DropdownMenuItem(text = "Menos de 1", value = 0, selectedValue = ldlState)
            DropdownMenuItem(text = "Entre 1 y 2.5", value = 1, selectedValue = ldlState)
            DropdownMenuItem(text = "Entre 3 y 4.5", value = 2, selectedValue = ldlState)
            DropdownMenuItem(text = "Mayor igual a 5", value = 3, selectedValue = ldlState)
        }

        // Sección de Colesterol Total
        Section(title = "¿En cuál de los siguientes rangos se encuentra el colesterol total del paciente?") {
            DropdownMenuItem(text = "Menos de 3", value = 0, selectedValue = cholesterolState)
            DropdownMenuItem(text = "Entre 3 y 4.5", value = 1, selectedValue = cholesterolState)
            DropdownMenuItem(text = "Entre 5 y 6.5", value = 2, selectedValue = cholesterolState)
            DropdownMenuItem(text = "Mayor igual a 7", value = 3, selectedValue = cholesterolState)
        }

        // Sección de Fumador
        Section(title = "¿Fumador activo?") {
            RadioButtonGroup(
                options = listOf("Sí", "No"),
                selectedValue = smokerState,
                onValueChange = { selectedValue -> smokerState.value = selectedValue }
            )
        }

        // Sección de Consumo de Alcohol
        Section(title = "¿Bebes alcohol?") {
            RadioButtonGroup(
                options = listOf("Sí", "No"),
                selectedValue = alcoholState,
                onValueChange = { selectedValue -> alcoholState.value = selectedValue }
            )
        }

        Button(
            onClick = {
                // Calcular el puntaje y la categoría de riesgo
                finalScore = calculateScore(
                    ageState.value,
                    hba1cState.value,
                    ldlState.value,
                    cholesterolState.value,
                    smokerState.value,
                    alcoholState.value
                )
                riskCategory = calculateRiskCategory(finalScore ?: 0)

                // Resetear el estado de los botones
                ageState.value = -1
                hba1cState.value = -1
                ldlState.value = -1
                cholesterolState.value = -1
                smokerState.value = -1
                alcoholState.value = -1

                // Actualizar el estado de calculando
                calculating = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Calcular")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (calculating) {
            finalScore?.let { score ->
                Text(
                    text = "Puntaje total: $score",
                    fontSize = 18.sp
                )
            }
            riskCategory?.let { risk ->
                Text(
                    text = "Categoría de riesgo: $risk",
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun Section(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
fun DropdownMenuItem(text: String, value: Int, selectedValue: MutableState<Int>) {
    val isSelected = selectedValue.value == value
    Button(
        onClick = {
            selectedValue.value = if (isSelected) -1 else value
        },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) Color.Cyan else Color.LightGray,
            contentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text)
    }
}

@Composable
fun RadioButtonGroup(
    options: List<String>,
    selectedValue: MutableState<Int>,
    onValueChange: (Int) -> Unit
) {
    Column {
        options.forEachIndexed { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onValueChange(index) } // Añadido aquí
            ) {
                RadioButton(
                    selected = selectedValue.value == index,
                    onClick = { onValueChange(index) }
                )
                Text(
                    text = option,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

fun calculateScore(
    age: Int,
    hba1c: Int,
    ldl: Int,
    cholesterol: Int,
    smoker: Int,
    alcohol: Int
): Int {
    var score = 0
    score += age
    score += hba1c
    score += ldl
    score += cholesterol
    score += if (smoker == 0) 3 else 0 // Sí = 0
    score += if (alcohol == 0) 3 else 0 // Sí = 0
    return score
}

fun calculateRiskCategory(score: Int): String {
    return when (score) {
        in 0..6 -> "Riesgo Bajo"
        in 7..12 -> "Riesgo Medio"
        in 13..18 -> "Riesgo Alto"
        else -> "Riesgo Muy Alto"
    }
}
