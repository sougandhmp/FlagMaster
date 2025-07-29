package org.smp.flagmaster.ui.time_schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import org.smp.flagmaster.ui.theme.FlagMasterTheme

@Composable
fun TimeScheduleRoute(
    onBack: () -> Unit,
    viewModel: TimeScheduleViewModel = hiltViewModel()
) {
    TimeScheduleScreen(
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeScheduleScreen(
    onBack: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Time Schedule") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 16.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min) // ensures proper vertical alignment
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            listOf(Color.Black, Color.DarkGray)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = "time",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "FLAGS CHALLENGE",
                                modifier = Modifier.align(Alignment.Center),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFF5722),
                                style = TextStyle(
                                    shadow = Shadow(
                                        color = Color.Black,
                                        offset = Offset(2f, 2f),
                                        blurRadius = 2f
                                    )
                                )
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    // SCHEDULE text
                    Text(
                        text = "SCHEDULE",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Time Input Fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TimeInputBlock(label = "Hour", value = "0")
                        TimeInputBlock(label = "Minute", value = "0")
                        TimeInputBlock(label = "Second", value = "0")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save Button
                    Button(
                        onClick = { /* Save logic */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Save", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}


@Composable
fun TimeInputBlock(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))

        TextField(
            value = value,
            onValueChange = { /* Handle value change */ },
            singleLine = true,
            placeholder = { Text("0") },
            modifier = Modifier.wrapContentSize(),
        )
    }
}

@Composable
@Preview
fun TimeSchedulePreview() {
    FlagMasterTheme {
        TimeScheduleScreen(
            onBack = {}
        )
    }
}