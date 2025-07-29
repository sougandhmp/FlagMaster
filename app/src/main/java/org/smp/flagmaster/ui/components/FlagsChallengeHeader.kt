package org.smp.flagmaster.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smp.flagmaster.R

@Composable
fun FlagsChallengeHeader(
    modifier: Modifier = Modifier,
    time: String? = null,
    pageCount: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        time?.let {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color.Black, Color.DarkGray)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = time,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } ?: Spacer(modifier = Modifier.size(80.dp))

        Text(
            text = stringResource(R.string.flags_challenge),
            modifier = Modifier.weight(1f),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFFF5722),
            textAlign = TextAlign.Center,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(2f, 2f),
                    blurRadius = 2f
                )
            )
        )
        pageCount?.let {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color.Black, Color.DarkGray)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = pageCount,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        } ?: Spacer(modifier = Modifier.size(80.dp))
    }
}

@Composable
@Preview
private fun FlagsChallengeHeaderPreview() {
    FlagsChallengeHeader()
}