package org.smp.flagmaster.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.smp.flagmaster.ui.components.VibrantBackground
import org.smp.flagmaster.ui.theme.BlueVibrantTheme

private val tabTitles = listOf("Sign In", "Create Account")

@Composable
fun LoginScreen(authViewModel: AuthViewModel) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            authViewModel.onAction(AuthAction.ClearError)
        }
    }

    VibrantBackground(config = BlueVibrantTheme) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🌍",
                    style = MaterialTheme.typography.displayLarge,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Flags Challenge",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = "Sign in to track your progress",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(36.dp))

                AuthForm(
                    uiState = uiState,
                    onAction = authViewModel::onAction,
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthForm(
    uiState: AuthUiState,
    onAction: (AuthAction) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        cursorColor = Color.White,
        focusedLeadingIconColor = Color.White,
        unfocusedLeadingIconColor = Color.White.copy(alpha = 0.6f),
        focusedTrailingIconColor = Color.White,
        unfocusedTrailingIconColor = Color.White.copy(alpha = 0.6f),
    )

    fun submit() {
        focusManager.clearFocus()
        onAction(AuthAction.Submit)
    }

    SecondaryTabRow(
        selectedTabIndex = uiState.selectedTab,
        containerColor = Color.Transparent,
        contentColor = Color.White,
        divider = { HorizontalDivider(color = Color.White.copy(alpha = 0.2f)) }
    ) {
        tabTitles.forEachIndexed { index, title ->
            Tab(
                selected = uiState.selectedTab == index,
                onClick = { onAction(AuthAction.TabSelected(index)) },
                text = {
                    Text(
                        text = title,
                        fontWeight = if (uiState.selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (uiState.selectedTab == index) Color.White else Color.White.copy(alpha = 0.5f),
                    )
                }
            )
        }
    }

    Spacer(Modifier.height(24.dp))

    OutlinedTextField(
        value = uiState.email,
        onValueChange = { onAction(AuthAction.EmailChanged(it)) },
        label = { Text("Email") },
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
        ),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = fieldColors,
    )

    Spacer(Modifier.height(12.dp))

    OutlinedTextField(
        value = uiState.password,
        onValueChange = { onAction(AuthAction.PasswordChanged(it)) },
        label = { Text("Password") },
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { onAction(AuthAction.TogglePasswordVisibility) }) {
                Icon(
                    imageVector = if (uiState.passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { submit() }),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = fieldColors,
    )

    Spacer(Modifier.height(24.dp))

    Button(
        onClick = { submit() },
        enabled = !uiState.isLoading && uiState.email.isNotBlank() && uiState.password.isNotBlank(),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF001F26),
            disabledContainerColor = Color.White.copy(alpha = 0.3f),
            disabledContentColor = Color.White.copy(alpha = 0.5f),
        )
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF001F26), strokeWidth = 2.dp)
        } else {
            Text(
                text = if (uiState.selectedTab == 0) "Sign In" else "Create Account",
                fontWeight = FontWeight.Bold,
            )
        }
    }

    Spacer(Modifier.height(20.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.3f))
        Text("or", color = Color.White.copy(alpha = 0.6f), style = MaterialTheme.typography.bodySmall)
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.3f))
    }

    Spacer(Modifier.height(20.dp))

    Button(
        onClick = { onAction(AuthAction.GoogleSignIn) },
        enabled = !uiState.isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.15f),
            contentColor = Color.White,
            disabledContainerColor = Color.White.copy(alpha = 0.05f),
            disabledContentColor = Color.White.copy(alpha = 0.3f),
        )
    ) {
        Text("G  Continue with Google", fontWeight = FontWeight.SemiBold)
    }
}
