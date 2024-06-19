package raf.rma.catapult.profile.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import raf.rma.catapult.profile.login.LoginContract.LoginEvent
import raf.rma.catapult.profile.login.LoginContract.LoginState

fun NavGraphBuilder.login(
    route: String
) = composable(route = route) {
    val loginViewModel = hiltViewModel<LoginViewModel>()
    val state = loginViewModel.state.collectAsState()

    LoginScreen(
        state = state.value,
        eventPublisher = {
            loginViewModel.setEvent(it)
        },
    )
}

@Composable
fun LoginScreen(
    state: LoginState,
    eventPublisher: (uiEvent: LoginEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = state.name,
            onValueChange = { eventPublisher(LoginEvent.OnNameChange(it)) },
            label = { Text("Name") },
            isError = !state.isNameValid
        )
        if (!state.isNameValid) {
            Text(text = "Name cannot be empty", color = MaterialTheme.colorScheme.error)
        }

        TextField(
            value = state.nickname,
            onValueChange = { eventPublisher(LoginEvent.OnNicknameChange(it)) },
            label = { Text("Nickname") },
            isError = !state.isNicknameValid
        )
        if (!state.isNicknameValid) {
            Text(text = "Nickname can only contain letters, numbers, and underscores", color = MaterialTheme.colorScheme.error)
        }

        TextField(
            value = state.email,
            onValueChange = { eventPublisher(LoginEvent.OnEmailChange(it)) },
            label = { Text("Email") },
            isError = !state.isEmailValid
        )
        if (!state.isEmailValid) {
            Text(text = "Enter a valid email address", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { eventPublisher(LoginEvent.OnCreateProfile) },
            enabled = state.isNameValid && state.isNicknameValid && state.isEmailValid
        ) {
            Text("Create Profile")
        }
    }
}