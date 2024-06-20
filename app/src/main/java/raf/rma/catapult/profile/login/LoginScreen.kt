package raf.rma.catapult.profile.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import raf.rma.catapult.R
import raf.rma.catapult.core.theme.LightOrange
import raf.rma.catapult.core.theme.Orange
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    state: LoginState,
    eventPublisher: (uiEvent: LoginEvent) -> Unit,
) {
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.cat_logo),
                        contentDescription = "logo",
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightOrange
                ),
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(top = 60.dp)
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Create Account",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 10.dp)
                        .padding(top = 20.dp),
                    style = TextStyle(
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
                TextField(
                    value = state.name,
                    onValueChange = { eventPublisher(LoginEvent.OnNameChange(it)) },
                    label = { Text("Name") },
                    isError = !state.isNameValid,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Orange,
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Orange,
                    ),
                )
                if (!state.isNameValid) {
                    Text(text = "Name cannot be empty", color = MaterialTheme.colorScheme.error)
                }

                TextField(
                    value = state.nickname,
                    onValueChange = { eventPublisher(LoginEvent.OnNicknameChange(it)) },
                    label = { Text("Nickname") },
                    isError = !state.isNicknameValid,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Orange,
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Orange,
                    ),
                )
                if (!state.isNicknameValid) {
                    Text(
                        text = "Nickname can only contain letters, numbers, and underscores",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                TextField(
                    value = state.email,
                    onValueChange = { eventPublisher(LoginEvent.OnEmailChange(it)) },
                    label = { Text("Email") },
                    isError = !state.isEmailValid,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Orange,
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Orange,
                    ),
                )
                if (!state.isEmailValid) {
                    Text(
                        text = "Enter a valid email address",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { eventPublisher(LoginEvent.OnCreateProfile) },
                    enabled = state.isNameValid && state.isNicknameValid && state.isEmailValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange,
                        contentColor = Color.White,
                    )
                ) {
                    Text(
                        text = "Create",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        ),
                    )
                }
            }
        }
    )
}