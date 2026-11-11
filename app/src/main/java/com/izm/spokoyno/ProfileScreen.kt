package com.izm.spokoyno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.izm.spokoyno.data.local.AppDatabase
import com.izm.spokoyno.data.local.UserEntity
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(onProfileSaved: () -> Unit) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()

    val currentUser by db.userDao().getUser().collectAsState(initial = null)

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Мужской") }

    // Заполняем поля, если пользователь уже есть (режим редактирования)
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            name = currentUser?.name ?: ""
            age = currentUser?.age?.toString() ?: ""
            gender = currentUser?.gender ?: "Мужской"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (currentUser == null) "Давай познакомимся" else "Редактирование профиля",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Ваше имя") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Возраст") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Пол", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = gender == "Мужской",
                onClick = { gender = "Мужской" },
                modifier = Modifier.selectable(selected = gender == "Мужской", onClick = { gender = "Мужской" })
            )
            Text("Мужской", modifier = Modifier.padding(end = 16.dp))

            RadioButton(
                selected = gender == "Женский",
                onClick = { gender = "Женский" },
                modifier = Modifier.selectable(selected = gender == "Женский", onClick = { gender = "Женский" })
            )
            Text("Женский")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (name.isNotBlank() && age.isNotBlank()) {
                    scope.launch {
                        val user = UserEntity(
                            id = currentUser?.id ?: 0,
                            name = name,
                            age = age.toIntOrNull() ?: 0,
                            gender = gender
                        )
                        db.userDao().insertUser(user)
                        onProfileSaved()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить")
        }
    }
}