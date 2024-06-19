package raf.rma.catapult.profile.details

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import raf.rma.catapult.cats.list.CatListContract.CatListUiEvent
import raf.rma.catapult.core.compose.AppDrawerActionItem
import raf.rma.catapult.core.compose.AppIconButton
import raf.rma.catapult.core.theme.LightOrange
import raf.rma.catapult.profile.details.ProfileContract.ProfileEvent
import raf.rma.catapult.profile.details.ProfileContract.ProfileState

fun NavGraphBuilder.profile(
    route: String,
    onEditClick: () -> Unit,
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
        onEditClick = onEditClick,
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
    onEditClick: () -> Unit,
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
            modifier = Modifier.width(maxWidth * 3 / 4)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.BottomStart
                ){
                    Text(
                        modifier = Modifier.padding(all = 16.dp),
                        text = "Menu",
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                        )
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

                        }
                    )

                    AppDrawerActionItem(
                        icon = Icons.Default.MenuBook,
                        text = "Catalog",
                        onClick = onCatalogClick
                    )

                    AppDrawerActionItem(
                        icon = Icons.Default.Quiz,
                        text = "Quiz",
                        onClick = onQuizClick
                    )
                    AppDrawerActionItem(
                        icon = Icons.Default.Leaderboard,
                        text = "Leaderboard",
                        onClick = onLeaderboardClick
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
                    containerColor = LightOrange
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = state.name,
                    onValueChange = {  },
                    label = { Text("Name") },
                    readOnly = true
                )
                TextField(
                    value = state.nickname,
                    onValueChange = {  },
                    label = { Text("Nickname") },
                    readOnly = true
                )
                TextField(
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

                Button(onClick = {
                    showDialog = true
                }) {
                    Text("Edit Profile")
                }

                if (showDialog) {
                    Dialog(onDismissRequest = { showDialog = false }) {
                        Column {
                            TextField(
                                value = newName,
                                onValueChange = { newName = it },
                                label = { Text("New Name") }
                            )
                            TextField(
                                value = newNickname,
                                onValueChange = { newNickname = it },
                                label = { Text("New Nickname") }
                            )
                            TextField(
                                value = newEmail,
                                onValueChange = { newEmail = it },
                                label = { Text("New Email") }
                            )
                            Button(onClick = {
                                eventPublisher(ProfileEvent.EditProfile(newName, newNickname, newEmail))
                                showDialog = false
                            }) {
                                Text("Save")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Best Score: ${state.bestScore}")
                Text("Best Position: ${state.bestPosition}")

                Spacer(modifier = Modifier.height(16.dp))

                if (state.quizResults.isEmpty()) {
                    Text("No quiz results")
                } else {
                    Column {
                        Text("Quiz Results:")
                        state.quizResults.forEach { result ->
                            Text("Score: ${result.score}, Date: ${result.date}")
                        }
                    }
                }
            }
        }
    )
}
