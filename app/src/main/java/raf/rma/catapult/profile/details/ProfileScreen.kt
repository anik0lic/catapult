package raf.rma.catapult.profile.details

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import raf.rma.catapult.R
import raf.rma.catapult.core.compose.AppDrawerActionItem
import raf.rma.catapult.core.compose.AppIconButton
import raf.rma.catapult.core.theme.Orange
import raf.rma.catapult.profile.details.ProfileContract.ProfileEvent
import raf.rma.catapult.profile.details.ProfileContract.ProfileState

fun NavGraphBuilder.profile(
    route: String,
    onCatalogClick: () -> Unit,
    onQuizClick: () -> Unit,
    onLeaderboardClick: () -> Unit
) = composable(
    route = route
) {
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val state = profileViewModel.state.collectAsState()

    ProfileScreen(
        state = state.value,
        onCatalogClick = onCatalogClick,
        onQuizClick = onQuizClick,
        onLeaderboardClick = onLeaderboardClick,
        eventPublisher = {
            profileViewModel.setEvent(it)
        }
    )
}

@Composable
fun ProfileScreen(
    state: ProfileState,
    onCatalogClick: () -> Unit,
    onQuizClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    eventPublisher: (uiEvent: ProfileEvent) -> Unit,
) {
    val uiScope = rememberCoroutineScope()
    val drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed)

    BackHandler (enabled = drawerState.isOpen) {
        uiScope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        modifier = Modifier,
        drawerState = drawerState,
        drawerContent = {
            ProfileDrawer(
                onQuizClick = {
                    uiScope.launch {
                        drawerState.close()
                    }
                    onQuizClick()
                },
                onCatalogClick = {
                    uiScope.launch {
                        drawerState.close()
                    }
                    onCatalogClick()
                },
                onLeaderboardClick = {
                    uiScope.launch {
                        drawerState.close()
                    }
                    onLeaderboardClick()
                }
            )
        },
        content = {
            ProfileScaffold(
                state = state,
                onDrawerMenuClick = {
                    uiScope.launch {
                        drawerState.open()
                    }
                },
                eventPublisher = eventPublisher
            )

        }
    )
}

@Composable
private fun ProfileDrawer(
    onCatalogClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onQuizClick: () -> Unit,
){
    BoxWithConstraints {
        ModalDrawerSheet (
            modifier = Modifier.width(maxWidth * 3 / 4),
            drawerContainerColor = MaterialTheme.colorScheme.background,
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.BottomStart
                ){
                    Text(
                        modifier = Modifier
                            .padding(top = 90.dp)
                            .align(Alignment.Center)
                            .padding(horizontal = 16.dp),
                        text = "Catapult",
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                    Image(
                        painter = painterResource(id = R.drawable.cat_logo),
                        contentDescription = "logo",
                        modifier = Modifier.align(Alignment.Center)
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .fillMaxWidth(),
                        color = Orange
                    )
                }

                Column(modifier = Modifier.weight(1f)){

                    NavigationDrawerItem(
                        label = {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = "Profile"
                            )
                        },
                        icon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null)
                        },
                        selected = true,
                        onClick = {

                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Orange,
                            selectedTextColor = Color.White,
                            selectedIconColor = Color.White
                        )
                    )

                    AppDrawerActionItem(
                        icon = Icons.Default.MenuBook,
                        text = "Catalog",
                        onClick = onCatalogClick,
                        color = MaterialTheme.colorScheme.background
                    )

                    AppDrawerActionItem(
                        icon = Icons.Default.Quiz,
                        text = "Quiz",
                        onClick = onQuizClick,
                        color = MaterialTheme.colorScheme.background
                    )
                    AppDrawerActionItem(
                        icon = Icons.Default.Leaderboard,
                        text = "Leaderboard",
                        onClick = onLeaderboardClick,
                        color = MaterialTheme.colorScheme.background
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScaffold(
    state: ProfileState,
    onDrawerMenuClick: () -> Unit,
    eventPublisher: (uiEvent: ProfileEvent) -> Unit,
) {
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = TextStyle(
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    AppIconButton(
                        imageVector = Icons.Default.Menu,
                        onClick = onDrawerMenuClick,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.cat_logo),
                        contentDescription = "logo",
                        modifier = Modifier
                    )
                },
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(horizontal = 15.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Orange,
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Orange,
                    ),
                    value = state.name,
                    onValueChange = {  },
                    label = { Text("Name") },
                    readOnly = true
                )
                TextField(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Orange,
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Orange,
                    ),
                    value = state.nickname,
                    onValueChange = {  },
                    label = { Text("Nickname") },
                    readOnly = true
                )
                TextField(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Orange,
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Orange,
                    ),
                    value = state.email,
                    onValueChange = {  },
                    label = { Text("Email") },
                    readOnly = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                var showDialog by remember { mutableStateOf(false) }
                var newName by remember(state.name) { mutableStateOf(state.name) }
                var newNickname by remember(state.nickname) { mutableStateOf(state.nickname) }
                var newEmail by remember(state.email) { mutableStateOf(state.email) }

                Button(
                    onClick = {
                        showDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange,
                        contentColor = Color.White,
                    )
                ) {
                    Text(
                        text = "Edit Profile",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                        ),
                    )
                }

                if (showDialog) {
                    Dialog(onDismissRequest = { showDialog = false }) {
                        Column {
                            TextField(
                                value = newName,
                                onValueChange = {
                                    eventPublisher(ProfileEvent.OnNameChange(it))
                                    newName = it
                                },
                                isError = !state.isNameValid,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("New Name") },
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedIndicatorColor = Orange,
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    focusedIndicatorColor = Orange,
                                ),
                            )
                            if (!state.isNameValid) {
                                Text(
                                    text = "Name cannot be empty",
                                    color = Color.White
                                )
                            }
                            TextField(
                                value = newNickname,
                                onValueChange = {
                                    eventPublisher(ProfileEvent.OnNicknameChange(it))
                                    newNickname = it
                                },
                                modifier = Modifier.fillMaxWidth(),
                                isError = !state.isNicknameValid,
                                label = { Text("New Nickname") },
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedIndicatorColor = Orange,
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    focusedIndicatorColor = Orange,
                                ),
                            )
                            if (!state.isNicknameValid) {
                                Text(
                                    text = "Nickname can only contain letters, numbers, and underscores",
                                    color = Color.White
                                )
                            }
                            TextField(
                                value = newEmail,
                                onValueChange = {
                                    eventPublisher(ProfileEvent.OnEmailChange(it))
                                    newEmail = it
                                },
                                modifier = Modifier.fillMaxWidth(),
                                isError = !state.isEmailValid,
                                label = { Text("New Email") },
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedIndicatorColor = Orange,
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    focusedIndicatorColor = Orange,
                                ),
                            )
                            if (!state.isEmailValid) {
                                Text(
                                    text = "Enter a valid email address",
                                    color = Color.White
                                )
                            }
                            Button(
                                onClick = {
                                    eventPublisher(ProfileEvent.EditProfile(newName, newNickname, newEmail))
                                    showDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Orange,
                                    contentColor = Color.White,
                                    disabledContainerColor = Color.Gray,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                                    .padding(horizontal = 20.dp),
                                enabled = state.isNameValid && state.isNicknameValid && state.isEmailValid
                            ) {
                                Text("Save")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Best Score: ${state.bestScore}",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    ),
                )
                Text(
                    text = "Best Position: ${state.bestPosition}",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (state.quizResults.isEmpty()) {
                    Text(
                        text = "No quiz results",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        ),
                    )
                } else {
                    Column {
                        Text(
                            text = "Quiz Results:",
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .padding(bottom = 5.dp),
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                            ),
                        )
                        state.quizResults.forEach { result ->
                            val date = result.date.split(" ").take(4).joinToString(" ")
                            val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)
                            val annotatedText = buildAnnotatedString {
                                withStyle(style = boldStyle){
                                    append("Score: ")
                                }
                                append(result.score.toString())
                                append(", ")
                                withStyle(style = boldStyle){
                                    append("Date: ")
                                }
                                append(date)
                            }
                            Text(
                                text = annotatedText,
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .padding(horizontal = 10.dp),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                ),
                            )
                        }
                    }
                }
            }
        }
    )
}
