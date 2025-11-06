package br.edu.ifsp.apy.common

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun IconCustom (
    icon: ImageVector,
    text: String
) {
        Icon(
            icon,
            contentDescription = text,
            tint = Color.White
        )
    Text(
            text = "    $text",
            color = Color.White
        )

    }

