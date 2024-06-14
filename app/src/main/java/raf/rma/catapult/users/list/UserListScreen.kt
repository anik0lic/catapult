package raf.rma.catapult.users.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import raf.rma.catapult.LocalAnalytics
import raf.rma.catapult.core.compose.AppIconButton
import raf.rma.catapult.core.theme.CatapultTheme
import raf.rma.catapult.users.list.UserListContract.UserListState
import raf.rma.catapult.users.repository.SampleData

fun NavGraphBuilder.users(
    route: String,
    onUserClick: (Int) -> Unit,
    onProfileClick: () -> Unit,
    onPasswordsClick: () -> Unit
) = composable(
    route = route
){
    val userListViewModel = hiltViewModel<UserListViewModel>()
    val state = userListViewModel.state.collectAsState()
//    EnableEdgeToEdge(isDarkTheme = false)
    UserListScreen(
        state = state.value,
        onUserClick = onUserClick,
        onProfileClick = onProfileClick,
        onPasswordsClick = onPasswordsClick
    )
}

@Composable
fun UserListScreen(
    state: UserListState,
    onUserClick: (Int) -> Unit,
    onProfileClick: () -> Unit,
    onPasswordsClick: () -> Unit
) {
    val uiScope = rememberCoroutineScope()
    val drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed)

    val analytics = LocalAnalytics.current
    SideEffect {
        analytics.log("Neka poruka")
    }

    BackHandler(enabled = drawerState.isOpen){
        uiScope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        modifier = Modifier,
        drawerState = drawerState,
        drawerContent = {
            UserListDrawer(
                onProfileClick = {
                    uiScope.launch {
                        drawerState.close()
                    }
                    onProfileClick()
                },
                onPasswordsClick = {
                    uiScope.launch {
                        drawerState.close()
                    }
                    onPasswordsClick()
                },
                onSettingsClick = { /*TODO*/ }
            )
        },
        content = {
            UserListScaffold(
                state = state,
                onUserClick = onUserClick,
                onDrawerMenuClick = {
                    uiScope.launch {
                        drawerState.open()
                    }
                }
            )
        }
    )
}

@Composable
private fun AppDrawerMenuItem(
    icon: ImageVector,
    text: String,
){
    Row{
        Icon(imageVector = icon, contentDescription = null)
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = text
        )
    }
}

@Composable
private fun AppDrawerActionItem(
    icon: ImageVector,
    text: String,
    onClick: (() -> Unit)? = null
){
    ListItem(
        modifier = Modifier.clickable(
            enabled = onClick != null,
            onClick = { onClick?.invoke() }
        ),
        leadingContent = {
            Icon(imageVector = icon, contentDescription = null)
        },
        headlineContent = {
            Text(text = text)
        }
    )
}

@Composable
private fun UserListDrawer(
    onProfileClick: () -> Unit,
    onPasswordsClick: () -> Unit,
    onSettingsClick: () -> Unit,
){
    BoxWithConstraints {
        // We can use ModalDrawerSheet as a convenience or
        // built our own drawer as AppDrawer example
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
                        text = "Admin Name",
//                        style = CatapultTheme.typography.headlineSmall,
                    )
                }

                Column(modifier = Modifier.weight(1f)){

                    AppDrawerActionItem(
                        icon = Icons.Default.Person,
                        text = "Profile",
                        onClick = onProfileClick
                    )

                    AppDrawerActionItem(
                        icon = Icons.Default.Lock,
                        text = "Passwords",
                        onClick = onPasswordsClick
                    )

                    NavigationDrawerItem(
                        label = {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = "Users"
                            )
                        },
                        icon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null)
                        },
                        badge = {
                            Badge{
                                Text(text = "20")
                            }
                        },
                        selected = true,
                        onClick = {}
                    )

                    NavigationDrawerItem(
                        label = {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = "Passwords"
                            )
                        },
                        icon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                        },
                        selected = false,
                        onClick = onSettingsClick
                    )

                    HorizontalDivider(modifier = Modifier.fillMaxWidth())

                    AppDrawerActionItem(
                        icon = Icons.Default.Settings,
                        text = "Settings",
                        onClick = onSettingsClick
                    )
                }
            }

        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun UserListScaffold(
    state: UserListState,
    onUserClick: (Int) -> Unit,
    onDrawerMenuClick: () -> Unit
){
    val uiScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val showScrollToTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold (
        topBar = {
            LargeTopAppBar(
                navigationIcon = {
                    AppIconButton(
                        imageVector = Icons.Default.Menu,
                        onClick = onDrawerMenuClick
                    )
                },
                title = { Text(text = "Users") },
                scrollBehavior = scrollBehavior
            )
        },
        content = { paddingValues ->
            LazyColumn (
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .fillMaxSize(),
                state = listState,
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = state.users,
                    key = { it.id }
                ){
                    Card (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .clickable { onUserClick(it.id) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(vertical = 16.dp)
                                    .weight(1f),
                                text = "@${it.username}\n${it.name}",
//                                style
                            )

                            if(it.albumsCount != null){
                                Text(
                                    modifier = Modifier.padding(all = 16.dp),
                                    text = "Albums: ${it.albumsCount}",
                                    textAlign = TextAlign.Center,
//                                    style
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (showScrollToTop){
                FloatingActionButton(
                    onClick = {
                        uiScope.launch { listState.animateScrollToItem(index = 0)}
                    }
                ) {
                    Image(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = null)
                }
            }
        }
    )
}

@Preview
@Composable
fun PreviewUserListScreen() {
    CatapultTheme {
        UserListScreen(state = UserListState(users = SampleData), onUserClick = {}, onProfileClick = { /*TODO*/ }) {

        }
    }
}








