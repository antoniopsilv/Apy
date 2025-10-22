package br.edu.ifsp.apy.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.ui.Modifier

@Composable
fun ButtonCustom(
                    text: String,
                    icon: ImageVector,
                    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
    )
    {
        Icon(
            icon, contentDescription = text, tint = Color.White
        )
        Text(
            text = "    $text", color = Color.White
        )
    }
}
