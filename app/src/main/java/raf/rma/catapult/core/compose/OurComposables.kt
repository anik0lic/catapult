package raf.rma.catapult.core.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import raf.rma.catapult.core.theme.Orange

@Composable
fun AppIconButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    onClick: () -> Unit,
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    query: String,
    activated: Boolean,
    color: Color
) {
    var text by remember { mutableStateOf(query) }
    var active by remember { mutableStateOf(activated) }
    val focusManager = LocalFocusManager.current

    TextField(
        modifier = modifier,
        value = text,
        onValueChange = { newText ->
            text = newText
            active = true
            onQueryChange(newText)
        },
        placeholder = { Text(text = "Search") },
        textStyle = TextStyle(color = Color.Black),
        leadingIcon = { AppIconButton(imageVector = Icons.Default.Search, onClick = { }) },
        trailingIcon = {
            if(active){
                AppIconButton(
                    imageVector = Icons.Default.Clear,
                    onClick = {
                        if(text.isNotEmpty()) {
                            text = ""
                            onQueryChange(text)
                        }
                        else{
                            active = false
                            onCloseClicked()
                            focusManager.clearFocus()
                        }
                    }
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
            }
        ),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = color,
            unfocusedIndicatorColor = Orange,
            focusedContainerColor = color,
            focusedIndicatorColor = Orange,
        ),
    )
}

@Composable
fun AppDrawerActionItem(
    icon: ImageVector,
    text: String,
    color: Color,
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
        },
        colors = ListItemDefaults.colors(
            containerColor = color
        )
    )
}
