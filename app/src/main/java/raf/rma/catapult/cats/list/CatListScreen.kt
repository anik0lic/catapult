package raf.rma.catapult.cats.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import raf.rma.catapult.cats.list.CatListContract.CatListState
import raf.rma.catapult.cats.list.CatListContract.CatListUiEvent
import raf.rma.catapult.core.compose.AppDrawerActionItem
import raf.rma.catapult.core.compose.AppIconButton
import raf.rma.catapult.core.compose.SearchBar
import raf.rma.catapult.core.theme.LightOrange
import raf.rma.catapult.core.theme.Orange

fun NavGraphBuilder.cats(
    route: String,
    onCatClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onQuizClick: () -> Unit,
    onLeaderboardClick: () -> Unit
) = composable(
    route = route
){
    val catListViewModel = hiltViewModel<CatListViewModel>()
    val state = catListViewModel.state.collectAsState()
//    EnableEdgeToEdge(isDarkTheme = false)
    CatListScreen(
        state = state.value,
        eventPublisher = {
            catListViewModel.setEvent(it)
        },
        onCatClick = onCatClick,
        onProfileClick = onProfileClick,
        onQuizClick = onQuizClick,
        onLeaderboardClick = onLeaderboardClick
    )
}

@Composable
fun CatListScreen(
    state: CatListState,
    eventPublisher: (uiEvent: CatListUiEvent) -> Unit,
    onCatClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onQuizClick: () -> Unit,
    onLeaderboardClick: () -> Unit
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
            CatListDrawer(
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
                onLeaderboardClick = {
                    uiScope.launch {
                        drawerState.close()
                    }
                    onLeaderboardClick()
                }
            )
        },
        content = {
            CatListScaffold(
                state = state,
                onCatClick = onCatClick,
                onDrawerMenuClick = {
                    uiScope.launch {
                        drawerState.open()
                    }
                },
                eventPublisher = eventPublisher
            )

            if (state.cats.isEmpty()) {
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
                                Text(text = "No cats.")
                            }
                        }
                    }
                }
            }
            if(state.isSearchMode && state.filteredCats.isEmpty()){
                if (!state.loading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "No cats with that name.")
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
private fun CatListDrawer(
    onProfileClick: () -> Unit,
    onQuizClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
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
                        icon = Icons.Default.Quiz,
                        text = "Quiz",
                        onClick = onQuizClick
                    )

                    AppDrawerActionItem(
                        icon = Icons.Default.Leaderboard,
                        text = "Leaderboard",
                        onClick = onLeaderboardClick
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

//                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
//
//                    AppDrawerActionItem(
//                        icon = Icons.Default.Settings,
//                        text = "Settings",
//                        onClick = onSettingsClick
//                    )
                }
            }

        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CatListScaffold(
    state: CatListState,
    onCatClick: (String) -> Unit,
    onDrawerMenuClick: () -> Unit,
    eventPublisher: (uiEvent: CatListUiEvent) -> Unit,
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
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Cat Breeds",
                            style = TextStyle(
                                fontSize = 27.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    navigationIcon = {
//                        Image(
//                            painter = painterResource(id = R.drawable.cat_logo),
//                            contentDescription = "logo",
//                            modifier = Modifier
//                        ),
                        AppIconButton(
                            imageVector = Icons.Default.Menu,
                            onClick = onDrawerMenuClick,
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = LightOrange
                    ),
                    scrollBehavior = scrollBehavior
                )
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightOrange)
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(30.dp)),
                    onQueryChange = { query ->
                        eventPublisher(
                            CatListUiEvent.SearchQueryChanged(
                                query = query
                            )
                        )
                    },
                    onCloseClicked = { eventPublisher(CatListUiEvent.CloseSearchMode) },
                    query = state.query,
                    activated = state.isSearchMode
                )
            }
        },
        content = {paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .fillMaxSize(),
                state = listState,
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(
                    items = if (state.isSearchMode) state.filteredCats else state.cats,
                    key = { it.id },
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Orange),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .clickable { onCatClick(it.id) },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(vertical = 16.dp)
                                    .weight(1f),
                                text = it.name,
                                style = TextStyle(
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Bold
                                )
//                                style = MaterialTheme.typography.headlineSmall,
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = it.description.take(250).plus("..."),
                                style = TextStyle(
                                    fontSize = 18.sp,
                                )
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (showScrollToTop) {
                FloatingActionButton(
                    onClick = {
                        uiScope.launch { listState.animateScrollToItem(index = 0) }
                    },
                ) {
                    Image(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = null)
                }
            }
        },
    )
}

//@Preview
//@Composable
//fun PreviewUserListScreen() {
//    CatapultTheme {
//        CatListScreen(state = CatListState(cats = SampleData), onUserClick = {}, onProfileClick = { /*TODO*/ }) {
//
//        }
//    }
//}








