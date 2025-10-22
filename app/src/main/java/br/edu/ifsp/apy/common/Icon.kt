package br.edu.ifsp.apy.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconCustom (
    icon: ImageVector,
    text: String
   //spacerWidth: Dp = 8.dp
) {
        Icon(
            icon,
            contentDescription = text,
            tint = Color.White
        )
 //   Spacer(modifier = Modifier.width(spacerWidth))
    Text(
            text = "    $text",
            color = Color.White
        )

    }

