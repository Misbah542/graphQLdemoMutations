package com.misbahminiproject.graphqldemomutations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apollographql.apollo3.api.Optional
import com.misbahminiproject.graphqldemomutations.type.PersonInput
import com.misbahminiproject.graphqldemomutations.ui.theme.GraphQLdemoMutationsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GraphQLdemoMutationsTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    GreetingScreen()
                }
            }
        }
    }
}

@Composable
fun GreetingScreen() {
    val apolloClient = NetworkModule.apolloClient
    var greeting by remember { mutableStateOf("") }
    var personId by remember { mutableStateOf("") }
    var personName by remember { mutableStateOf("") }
    var personAge by remember { mutableStateOf("") }

    var inputName by remember { mutableStateOf("") }
    var inputAge by remember { mutableStateOf("0") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = inputName,
            onValueChange = { inputName = it },
            label = { Text("Name") },
            isError = inputName.isEmpty()
        )
        if (inputName.isEmpty()) {
            Text("Name is required.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = inputAge,
            onValueChange = { inputAge = it },
            label = { Text("Age (Optional)") },
            isError = inputAge.isNotEmpty() && inputAge.toIntOrNull() == null
        )
        if (inputAge.isNotEmpty() && inputAge.toIntOrNull() == null) {
            Text("Invalid age. Please enter a valid number.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            if (inputName.isNotEmpty() && (inputAge.isEmpty() || inputAge.toIntOrNull() != null)) {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = apolloClient.query(
                        HelloQuery(person = Optional.present(PersonInput(inputName, Optional.present(inputAge.toIntOrNull()))))
                    ).execute()
                    greeting = response.data?.hello ?: "No greeting received"
                }
            }
        }) {
            Text("Get Greeting")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Greeting: $greeting", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = personName,
            onValueChange = { personName = it },
            label = { Text("Person Name") },
            isError = personName.isEmpty()
        )
        if (personName.isEmpty()) {
            Text("Name is required.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = personAge,
            onValueChange = { personAge = it },
            label = { Text("Person Age") },
            isError = personAge.isNotEmpty() && personAge.toIntOrNull() == null
        )
        if (personAge.isNotEmpty() && personAge.toIntOrNull() == null) {
            Text("Invalid age. Please enter a valid number.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            if (personName.isNotEmpty() && (personAge.isEmpty() || personAge.toIntOrNull() != null)) {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = apolloClient.mutation(
                        CreatePersonMutation(
                            person = PersonInput(personName, Optional.present(personAge.toIntOrNull()))
                        )
                    ).execute()
                    val createdPerson = response.data?.createPerson
                    personId = createdPerson?.id ?: ""
                    personName = createdPerson?.name ?: ""
                    personAge = createdPerson?.age?.toString() ?: ""
                }
            }
        }) {
            Text("Create Person")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (personId.isNotEmpty()) {
            Text("Created Person: $personName (ID: $personId, Age: $personAge)")
        }
    }
}
