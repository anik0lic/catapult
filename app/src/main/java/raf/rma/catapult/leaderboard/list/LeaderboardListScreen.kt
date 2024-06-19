package raf.rma.catapult.leaderboard.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import raf.rma.catapult.LocalAnalytics
import raf.rma.catapult.core.compose.AppDrawerActionItem
import raf.rma.catapult.core.compose.AppIconButton
import raf.rma.catapult.core.theme.LightOrange
import raf.rma.catapult.core.theme.Orange
import raf.rma.catapult.leaderboard.list.LeaderboardListContract.LeaderboardListState

fun NavGraphBuilder.leaderboard(
    route: String,
    onCatalogClick: () -> Unit,
    onProfileClick: () -> Unit,
    onQuizClick: () -> Unit,
) = composable(
    route = route
) {
    val leaderboardListViewModel = hiltViewModel<LeaderboardListViewModel>()
    val state = leaderboardListViewModel.state.collectAsState()

    LeaderboardListScreen(
        state = state.value,
        onCatalogClick = onCatalogClick,
        onProfileClick = onProfileClick,
        onQuizClick = onQuizClick,
    )
}

@Composable
fun LeaderboardListScreen(
    state: LeaderboardListState,
    onCatalogClick: () -> Unit,
    onProfileClick: () -> Unit,
    onQuizClick: () -> Unit,
){
    val uiScope = rememberCoroutineScope()
    val drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed)

//    val analytics = LocalAnalytics.current
//    SideEffect {
//        analytics.log("Neka poruka")
//    }

    BackHandler(enabled = drawerState.isOpen){
        uiScope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        modifier = Modifier,
        drawerState = drawerState,
        drawerContent = {
            LeaderboardListDrawer(
                onProfileClick = {
                    uiScope.launch {
                        drawerState.close()
                    }
                    onProfileClick()
                },
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
                }
            )
        },
        content = {
            LeaderboardListScaffold(
                state = state,
                onDrawerMenuClick = {
                    uiScope.launch {
                        drawerState.open()
                    }
                }
            )

            if (state.results.isEmpty()) {
                when (state.loading) {
                    true -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    false -> {
                        if (state.error) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(text = "Something went wrong while fetching the data")
                            }
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(text = "No results.")
                            }
                        }
                    }
                }
            }

            if(state.loading){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    )
}

@Composable
private fun LeaderboardListDrawer(
    onCatalogClick: () -> Unit,
    onProfileClick: () -> Unit,
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

                    AppDrawerActionItem(
                        icon = Icons.Default.Person,
                        text = "Profile",
                        onClick = onProfileClick
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

                    NavigationDrawerItem(
                        label = {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = "Leaderboard"
                            )
                        },
                        icon = {
                            Icon(imageVector = Icons.Default.Leaderboard, contentDescription = null)
                        },
                        selected = true,
                        onClick = {

                        }
                    )
                }
            }

        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun LeaderboardListScaffold(
    state: LeaderboardListState,
    onDrawerMenuClick: () -> Unit,
) {
    val listState = rememberLazyListState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Leaderboard",
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
                ),
                scrollBehavior = scrollBehavior,
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .fillMaxSize(),
                state = listState,
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(
                    items = state.results,
                    key = { it.id }
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Orange),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(vertical = 16.dp)
                                    .weight(1f),
                                text = it.nickname + it.result,
                                style = TextStyle(
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Bold
                                )
//                                style = MaterialTheme.typography.headlineSmall,
                            )
                        }
                    }
                }
            }
        }
    )
}