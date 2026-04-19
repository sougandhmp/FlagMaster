package org.smp.feature.flags.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest

@Composable
fun CountryFlag(countryCode: String) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data("file:///android_asset/flags/${countryCode.lowercase()}.svg")
            .build(),
        contentDescription = "Flag of ${countryCode.uppercase()}",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
    )
}
