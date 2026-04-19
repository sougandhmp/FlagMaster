package org.smp.feature.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.smp.core.ui.TealVibrantTheme
import org.smp.core.ui.VibrantBackground
import org.smp.core.ui.shimmer
import org.smp.feature.auth.AuthAction
import org.smp.feature.auth.AuthState
import org.smp.feature.auth.AuthViewModel

private val avatars = listOf(
    "https://api.dicebear.com/9.x/avataaars/svg?seed=Felix",
    "https://api.dicebear.com/9.x/avataaars/svg?seed=Aneka",
    "https://api.dicebear.com/9.x/avataaars/svg?seed=Erik",
    "https://api.dicebear.com/9.x/avataaars/svg?seed=Sara",
    "https://api.dicebear.com/9.x/avataaars/svg?seed=Nala",
    "https://api.dicebear.com/9.x/avataaars/svg?seed=Zoe",
    "https://api.dicebear.com/9.x/avataaars/svg?seed=Abby",
    "https://api.dicebear.com/9.x/avataaars/svg?seed=Jack",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val user = (uiState.authState as? AuthState.Authenticated)?.user
    var isEditing by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                authViewModel.onAction(AuthAction.AvatarSelected(it.toString()))
            }
        }
    )

    LaunchedEffect(isEditing) {
        if (isEditing) {
            authViewModel.onAction(AuthAction.DisplayNameChanged(user?.displayName ?: ""))
            authViewModel.onAction(AuthAction.AvatarSelected(user?.photoUrl))
        }
    }

    VibrantBackground(config = TealVibrantTheme) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                            )
                        }
                    },
                    actions = {
                        if (isEditing) {
                            IconButton(onClick = { isEditing = false }) {
                                Icon(Icons.Default.Close, "Cancel", tint = Color.White)
                            }
                            IconButton(
                                onClick = {
                                    authViewModel.onAction(AuthAction.UpdateProfile)
                                    isEditing = false
                                },
                                enabled = uiState.displayName.isNotBlank() && !uiState.isLoading
                            ) {
                                if (uiState.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White
                                    )
                                } else {
                                    Icon(Icons.Default.Save, "Save", tint = Color.White)
                                }
                            }
                        } else {
                            IconButton(onClick = { isEditing = true }) {
                                Icon(Icons.Default.Edit, "Edit", tint = Color.White)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(32.dp))

                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .border(
                                width = 2.dp,
                                color = Color.White.copy(alpha = 0.4f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        val currentPhotoUrl = if (isEditing) uiState.selectedAvatar else user?.photoUrl
                        if (currentPhotoUrl != null) {
                            Box(contentAlignment = Alignment.Center) {
                                AsyncImage(
                                    model = currentPhotoUrl,
                                    contentDescription = "Profile photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )
                                if (uiState.isLoading) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.4f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(32.dp),
                                            color = Color.White,
                                            strokeWidth = 3.dp
                                        )
                                    }
                                }
                            }
                        } else {
                            val initials = (if (isEditing) uiState.displayName else user?.displayName)
                                ?.split(" ")
                                ?.mapNotNull { it.firstOrNull()?.uppercaseChar() }
                                ?.take(2)
                                ?.joinToString("")
                                ?: user?.email?.firstOrNull()?.uppercaseChar()?.toString()

                            if (!initials.isNullOrBlank()) {
                                Text(
                                    text = initials,
                                    color = Color.White,
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(60.dp),
                                )
                            }
                        }
                    }

                    if (isEditing) {
                        IconButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Pick Image",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                if (isEditing) {
                    OutlinedTextField(
                        value = uiState.displayName,
                        onValueChange = { authViewModel.onAction(AuthAction.DisplayNameChanged(it)) },
                        label = { Text("Display Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                        )
                    )

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "Or Choose an Avatar",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        contentPadding = PaddingValues(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.height(160.dp)
                    ) {
                        items(avatars) { avatarUrl ->
                            val isSelected = uiState.selectedAvatar == avatarUrl
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 3.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { authViewModel.onAction(AuthAction.AvatarSelected(avatarUrl)) }
                            ) {
                                AsyncImage(
                                    model = avatarUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                } else {
                    val displayName = user?.displayName?.takeIf { it.isNotBlank() }
                        ?: user?.email?.substringBefore("@")
                    if (displayName != null) {
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = if (uiState.isLoading) {
                                Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .shimmer()
                            } else Modifier
                        )
                        Spacer(Modifier.height(4.dp))
                    }

                    user?.email?.let { email ->
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = if (uiState.isLoading) {
                                Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .shimmer()
                            } else Modifier
                        )
                    }
                }

                Spacer(Modifier.height(40.dp))

                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

                Spacer(Modifier.height(40.dp))

                AnimatedVisibility(!isEditing) {
                    Button(
                        onClick = { authViewModel.onAction(AuthAction.SignOut) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.15f),
                            contentColor = Color.White,
                        ),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                            Text("Sign Out", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

