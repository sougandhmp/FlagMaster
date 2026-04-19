package org.smp.feature.flags.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import org.smp.core.ui.BlueVibrantTheme
import org.smp.core.ui.VibrantThemeConfig

@Composable
fun FactPanel(fact: String, config: VibrantThemeConfig) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(config.accentColor.copy(alpha = 0.15f))
            .border(1.dp, config.accentColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = null,
            tint = config.accentColor,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = fact,
            color = Color.White,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Preview
@Composable
fun FactPanelPreview() {
    MaterialTheme {
        FactPanel(
            fact = "North Macedonia declared independence from Yugoslavia in 1991 and is one of the youngest countries in Europe.",
            config = BlueVibrantTheme,
        )
    }
}
