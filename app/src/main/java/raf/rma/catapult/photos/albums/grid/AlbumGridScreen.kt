package raf.rma.catapult.photos.albums.grid

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import raf.rma.catapult.core.compose.AppIconButton
import raf.rma.catapult.core.compose.PhotoPreview
import raf.rma.catapult.photos.albums.grid.AlbumGridContract.AlbumGridUiState
import raf.rma.catapult.photos.albums.model.AlbumUiModel

fun NavGraphBuilder.userAlbumsGrid(
    route: String,
    arguments: List<NamedNavArgument>,
    onAlbumClick: (Int) -> Unit,
    onClose: () -> Unit,
) = composable(
    route = route,
    arguments = arguments
) { navBackStackEntry ->

    val albumGridViewModel: AlbumGridViewModel = hiltViewModel(navBackStackEntry)

    val state = albumGridViewModel.state.collectAsState()

    AlbumGridScreen(
        state = state.value,
        onAlbumClick = onAlbumClick,
        onClose = onClose,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumGridScreen(
    state: AlbumGridUiState,
    onAlbumClick: (albumId: Int) -> Unit,
    onClose: () -> Unit,
) {
    Scaffold (
        topBar = {
            MediumTopAppBar(
                title = { Text(text = "Albums") },
                navigationIcon = {
                    AppIconButton(
                        imageVector = Icons.Default.ArrowBack,
                        onClick = onClose,
                    )
                }
            )
        },
        content = { paddingValues ->
            BoxWithConstraints (
                modifier = Modifier,
                contentAlignment = Alignment.BottomCenter,
            ) {
                val screenWidth = this.maxWidth
                val cellSize = (screenWidth / 2) - 4.dp

                if (state.albums.isEmpty() && state.updating) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(36.dp)
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp),
                        columns = GridCells.Fixed(2),
                        contentPadding = paddingValues,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        itemsIndexed(
                            items = state.albums,
                            key = { index: Int, album: AlbumUiModel -> album.id },
                        ) { index: Int, album: AlbumUiModel ->
                            Card (
                                modifier = Modifier
                                    .size(cellSize)
                                    .clickable {
                                        onAlbumClick(album.id)
                                    }
                            ) {
                                PhotoPreview(
                                    modifier = Modifier.fillMaxWidth(),
                                    url = album.coverPhotoUrl,
                                    title = album.title,
                                )
                            }
                        }

                        if(!state.updating){
                            item(
                                span = {
                                    GridItemSpan(2)
                                }
                            ) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(all = 32.dp),
                                    text = "Made with Kotlin & Compose" +
                                            "\n\uD83D\uDC9A",
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}