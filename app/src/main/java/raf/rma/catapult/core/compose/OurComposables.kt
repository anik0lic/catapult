package raf.rma.catapult.core.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    activated: Boolean
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
            unfocusedContainerColor = Color.White,
            unfocusedIndicatorColor = Orange,
            focusedContainerColor = Color.White,
            focusedIndicatorColor = Orange,
        ),
    )
}

@Composable
fun AppDrawerActionItem(
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
fun NoDataContent(
    id: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "There is no data for id '$id'.",
            fontSize = 18.sp,
        )
    }
}

@Composable
fun AppDropdownMenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        leadingIcon = { Icon(imageVector = icon, contentDescription = text) },
        text = { Text(text = text) },
        onClick = onClick,
    )
}

@Composable
fun PasswordOutlinedTextField(
    modifier: Modifier,
    password: String,
    onPasswordChange: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        modifier = modifier,
        value = password,
        onValueChange = onPasswordChange,
        trailingIcon = {
            AppIconButton(imageVector = if (passwordVisible) {
                Icons.Outlined.Warning
            } else {
                Icons.Outlined.Lock
            }, onClick = {
                passwordVisible = !passwordVisible
            })
        },
        placeholder = { Text(text = "Password") },
        label = { Text(text = "Password") },
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        }
    )

}