package com.example.proyecto_catedra.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditProfileScreen(
    currentName: String,
    currentEmail: String,
    currentUniversidad: String?,
    currentCarrera: String?,
    currentSemestre: String?,
    onBack: () -> Unit = {},
    onSave: (name: String, universidad: String?, carrera: String?, semestre: String?) -> Unit = { _,_,_,_ -> }
) {
    var name by remember { mutableStateOf(currentName) }
    val email = currentEmail // normalmente no editable
    var universidad by remember { mutableStateOf(currentUniversidad ?: "") }
    var carrera by remember { mutableStateOf(currentCarrera ?: "") }
    var semestre by remember { mutableStateOf(currentSemestre ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
            }
            Text("Editar datos", fontSize = 20.sp, modifier = Modifier.padding(start = 8.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = {}, enabled = false, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = universidad, onValueChange = { universidad = it }, label = { Text("Universidad") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = carrera, onValueChange = { carrera = it }, label = { Text("Carrera") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = semestre, onValueChange = { semestre = it }, label = { Text("Semestre") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = { onSave(name, universidad.ifBlank { null }, carrera.ifBlank { null }, semestre.ifBlank { null }) },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar cambios") }
        }
    }
}


