package br.edu.ifsp.apy.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifsp.apy.R
import br.edu.ifsp.apy.classification.ImageClassification
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.text.NumberFormat


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen() {

    val context = LocalContext.current
    var currentImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var resultText by remember { mutableStateOf("Resultado aparecerá aqui") }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            currentImageUri = it
            selectedBitmap = loadBitmapFromUri(context, it)
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Apy") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF673AB7),
                    titleContentColor = Color.White
                ),
                actions = {

                    IconButton(onClick = {

                    } ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_recent_history),
                            contentDescription = stringResource(id = R.string.salvar)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Exibe imagem
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFEDE7F6), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (selectedBitmap != null) {
                    Image(
                        bitmap = selectedBitmap!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.mipmap.ic_apy), // use seu drawable/mipmap
                        contentDescription = "Imagem inicial",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)                  // altura igual ao Box
                            .clip(RoundedCornerShape(16.dp)) // arredondar cantos
                    )
                    //Text("Imagem selecionada aparecerá aqui", color = Color.Gray)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Botão Selecionar Imagem
            Button(
                onClick = { pickImageLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
            ) {
                Icon(
                    Icons.Filled.FolderOpen,
                    contentDescription = "Selecionar Imagem",
                    tint = Color.White
                )
                Text(
                    text = "    Selecionar Imagem",
                    color = Color.White
                )
            }

            Spacer(Modifier.height(16.dp))

            // Botão Analisar Imagem
            Button(
                onClick = {
                    currentImageUri?.let { uri ->
                        val classifier = ImageClassification(
                            context = context,
                            classifierListener = object : ImageClassification.ClassifierListener {
                                override fun onError(error: String) {
                                    Toast.makeText(context, "Erro na classificação", Toast.LENGTH_SHORT).show()
                                }

                                override fun onResults(results: List<Classifications>?) {
                                    val text = results?.joinToString("\n") {
                                        it.categories[0].label + ": " +
                                                NumberFormat.getPercentInstance()
                                                    .format(it.categories[0].score).trim()
                                    } ?: "Sem resultado"
                                    resultText = text;
                                }
                            }
                        )
                        classifier.classifyStationImage(uri)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
            ) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Analisar Imagem",
                    tint = Color.White
                )
                Text(
                    text = "    Analisar Imagem",
                    color = Color.White
                )
            }
            Spacer(Modifier.height(32.dp))

            // Resultado
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Resultado", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(resultText, fontSize = 16.sp, color = Color.Gray)
                }
            }
        }
    }

}


fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}

