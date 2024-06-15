package raf.rma.catapult.photos.albums.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.SubcomposeAsyncImage
import raf.rma.catapult.R
import raf.rma.catapult.core.compose.AppIconButton
import raf.rma.catapult.photos.albums.list.AlbumListContract.AlbumListUiEvent
import raf.rma.catapult.photos.albums.list.AlbumListContract.AlbumListUiState

@Deprecated(message = "Please use userAlbumsGrid")
fun NavGraphBuilder.userAlbumsList(
    route: String,
    arguments: List<NamedNavArgument>,
    onClose: () -> Unit,
) = composable(
    route = route,
    arguments = arguments,
) { navBackStackEntry ->

    val albumListViewModel = hiltViewModel<AlbumListViewModel>(navBackStackEntry)

    val state = albumListViewModel.state.collectAsState()

    AlbumListScreen(
        state = state.value,
        eventPublisher = {
            albumListViewModel.setEvent(it)
        },
        onClose = onClose,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumListScreen(
    state: AlbumListUiState,
    eventPublisher: (AlbumListUiEvent) -> Unit,
    onClose: () -> Unit
) {
    Scaffold (
        topBar = {
            MediumTopAppBar(
                title = { Text(text = "Albums") },
                navigationIcon = {
                    AppIconButton(
                        imageVector = Icons.Default.ArrowBack,
                        onClick = onClose
                    )
                }
            )
        },
        content = { paddingValues ->
            LazyColumn (
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues
            ) {
                items(
                    items = state.albums,
                    key = { albumModel -> albumModel.id }
                ) { album ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SubcomposeAsyncImage(
                            modifier = Modifier.size(100.dp),
                            model = album.coverPhotoUrl,
                            loading = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            },
                            contentDescription = null
                        )

                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .weight(1f),
                            text = album.title,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
//                            style
                        )

                        DeleteAppIconButton(
                            modifier = Modifier.padding(end = 8.dp),
                            onDeleteConfirmed = {
                                eventPublisher(AlbumListUiEvent.DeleteAlbum(albumId = album.id))
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    )
}

@Composable
private fun DeleteAppIconButton(
    modifier: Modifier = Modifier,
    onDeleteConfirmed: () -> Unit,
) {
    var confirmationDeleteShown by remember { mutableStateOf(false) }
    if (confirmationDeleteShown) {
        AlertDialog(
            onDismissRequest = { confirmationDeleteShown = false },
            title = { Text(text = stringResource(id = R.string.editor_delete_confirmation_title)) },
            text = {
                Text(
                    text = stringResource(R.string.albums_delete_album_confirmation_text)
                )
            },
            dismissButton = {
                TextButton(onClick = { confirmationDeleteShown = false }) {
                    Text(text = stringResource(id = R.string.app_delete_confirmation_dismiss))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmationDeleteShown = false
                    onDeleteConfirmed()
                }) {
                    Text(text = stringResource(id = R.string.app_delete_confirmation_yes))
                }
            },
        )
    }

    AppIconButton(
        modifier = modifier,
        imageVector = Icons.Default.Delete,
        onClick = { confirmationDeleteShown = true },
    )
}