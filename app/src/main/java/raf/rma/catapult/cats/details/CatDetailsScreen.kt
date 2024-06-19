package raf.rma.catapult.cats.details

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.SubcomposeAsyncImage
import raf.rma.catapult.R
import raf.rma.catapult.cats.details.CatDetailsContract.CatDetailsState
import raf.rma.catapult.cats.model.CatUiModel
import raf.rma.catapult.core.compose.AppIconButton
import raf.rma.catapult.core.theme.LightOrange
import raf.rma.catapult.core.theme.Orange
import raf.rma.catapult.photos.model.PhotoUiModel

fun NavGraphBuilder.catDetails(
    route: String,
    arguments: List<NamedNavArgument>,
    onGalleryClick: (String) -> Unit,
    onClose: () -> Unit
) = composable(
    route = route,
    arguments = arguments
) { navBackStackEntry ->

    val catDetailsViewModel: CatDetailsViewModel = hiltViewModel(navBackStackEntry)

    val state = catDetailsViewModel.state.collectAsState()

    CatDetailsScreen(
        state = state.value,
        onGalleryClick = onGalleryClick,
        onClose = onClose
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatDetailsScreen(
    state: CatDetailsState,
    onGalleryClick: (String) -> Unit,
    onClose: () -> Unit
) {
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    state.cat?.let {
                        Text(
                            text = it.name,
                            style = TextStyle(
                                fontSize = 27.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                navigationIcon = {
                    AppIconButton(
                        imageVector = Icons.Default.ArrowBack,
                        onClick = onClose,
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
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (state.loading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (state.error) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "Something went wrong while fetching the data")
                    }
                } else if (state.cat != null) {
                    state.photo?.let {
                        BreedsDataColumn(
                            data = state.cat,
                            onGalleryClick = onGalleryClick,
                            photo = it
                        )
                    }
                } else {
//                    NoDataContent(id = state.breedId)
                }
            }
        }
    )
}

@Composable
private fun BreedsDataColumn(
    data: CatUiModel,
    onGalleryClick: (String) -> Unit,
    photo: PhotoUiModel
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(scrollState),
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .align(Alignment.CenterHorizontally),
            model = photo.url,
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
        HorizontalDivider(thickness = 1.dp, color = Orange)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Description",
            style = TextStyle(
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = data.description,
            style = TextStyle(
                fontSize = 18.sp,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))
        Divider(thickness = 1.dp, color = Orange)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
            text = "Origin: " + data.origin,
            style = TextStyle(
                fontSize = 18.sp
            )
        )

        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
            style = TextStyle(
                fontSize = 18.sp
            ),
            text = "Temperament: " + data.temperament,
        )

        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
            style = TextStyle(
                fontSize = 18.sp
            ),
            text = "Life Span: " + data.lifeSpan,
        )

        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
            style = TextStyle(
                fontSize = 18.sp
            ),
            text = "Weight(Metrics): " + data.weight,
        )

        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
            style = TextStyle(
                fontSize = 18.sp
            ),
            text = if (data.rare == 1) "Rare Breed" else "Not Rare Breed",
        )

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 1.dp, color = Orange)
        Spacer(modifier = Modifier.height(15.dp))

//        Row(
//            Modifier.fillMaxSize(),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            DetailsWidget(
//                number = data.adaptability,
//                trait = "Adaptability"
//            )
//            DetailsWidget(
//                number = data.affectionLevel,
//                trait = "Affection"
//            )
//            DetailsWidget(
//                number = data.intelligence,
//                trait = "Intelligence"
//            )
//        }

//        Spacer(modifier = Modifier.height(15.dp))

//        Row(
//            Modifier.fillMaxSize(),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            DetailsWidget(
//                number = data.childFriendly,
//                trait = "Child Friendly"
//            )
//            DetailsWidget(
//                number = data.socialNeeds,
//                trait = "Social Needs"
//            )
//        }


        FilledIconButton(
            onClick = {
                onGalleryClick(data.id)
            },
            modifier = Modifier
                .height(55.dp)
                .width(250.dp)
                .align(Alignment.CenterHorizontally),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Orange,
                contentColor = Color.White,
            )
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                ),
                text = "Gallery",
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
    }
}