@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package br.edu.ifsp.apy.ui

import br.edu.ifsp.apy.classification.ImageClassification
import br.edu.ifsp.apy.model.entity.History
import br.edu.ifsp.apy.view.HistoryViewModel
import br.edu.ifsp.apy.common.ButtonCustom
import br.edu.ifsp.apy.common.IconCustom
import br.edu.ifsp.apy.common.loadBitmapFromUri
import br.edu.ifsp.apy.common.setDateFromMillis
import br.edu.ifsp.apy.view.ViewModelFactory
import br.edu.ifsp.apy.view.HomeViewModelFactory
import br.edu.ifsp.apy.common.createImageUri
import br.edu.ifsp.apy.view.SettingsViewModel
import br.edu.ifsp.apy.view.HomeViewModel
import br.edu.ifsp.apy.BuildConfig
import br.edu.ifsp.apy.common.getCurrentLocation
import br.edu.ifsp.apy.R
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DoorBack
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.io.File
import androidx.compose.runtime.collectAsState
import android.Manifest
import android.app.Activity
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import java.text.NumberFormat
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModelProvider

import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {

   val context = LocalContext.current
   val apiKey = BuildConfig.GOOGLE_MAPS_API_KEY

    // ViewModels
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(context.applicationContext as Application)
    )
    val historyViewModel: HistoryViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context.applicationContext as Application)
    )

    val places by homeViewModel.places.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()
    val error by homeViewModel.error.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val useUCrop by settingsViewModel.useUCrop.collectAsState()

    var currentImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showResultCard by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("Resultado aparecerá aqui") }

    val toolbarColor = MaterialTheme.colorScheme.primary.toArgb()
    val statusBarColor = MaterialTheme.colorScheme.primary.toArgb()
    val activeControlColor = MaterialTheme.colorScheme.onPrimary.toArgb()

    // Define quais permissões são necessárias
    val locationPermissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.NEARBY_WIFI_DEVICES
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    val multiplePermissionsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                // Obter a localização do usuário e chamar a função do ViewModel
                coroutineScope.launch {
                    val latLng = getCurrentLocation(context)
                    homeViewModel.fetchNearbyDermatologists(
                        apiKey = apiKey,
                        latitude = latLng.latitude,
                        longitude = latLng.longitude
                    )
                }
            } else {
                Toast.makeText(
                    context,
                    "Permissões de localização não concedidas",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val currentLauncher = rememberUpdatedState(multiplePermissionsLauncher)

    fun Context.findActivity(): ComponentActivity? = when (this) {
        is ComponentActivity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

    val cropImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            resultUri?.let { uri ->
                currentImageUri = uri
                selectedBitmap = loadBitmapFromUri(context, uri)
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(result.data!!)
            Log.e("UCrop", "Erro no corte: $cropError")
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            if (useUCrop) {
                val destinationUri = Uri.fromFile(File(context.cacheDir, "${System.currentTimeMillis()}.jpg"))
                val uCrop = UCrop.of(it, destinationUri)
                    .withAspectRatio(16f, 9f)
                    .withMaxResultSize(2000, 2000)
                    .withOptions(UCrop.Options().apply {
                        setToolbarTitle("Recortar imagem")
                        setToolbarColor(toolbarColor)
                        setStatusBarColor(statusBarColor)
                        setActiveControlsWidgetColor(activeControlColor)
                    })

                val activity = context.findActivity()
                activity?.let { act ->
                    cropImageLauncher.launch(uCrop.getIntent(act))
                } ?: Log.e("UCrop", "Erro: Activity é nula, não foi possível iniciar UCrop")

            } else {
                currentImageUri = it
                selectedBitmap = loadBitmapFromUri(context, it)
            }
        }
    }


    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri.value?.let { uri ->
                if (useUCrop) {
                    val destinationUri = Uri.fromFile(File(context.cacheDir, "${System.currentTimeMillis()}.jpg"))
                    val uCrop = UCrop.of(uri, destinationUri)
                        .withAspectRatio(16f, 9f)
                        .withMaxResultSize(2000, 2000)

                    val activity = context as? ComponentActivity
                    activity?.let { act -> cropImageLauncher.launch(uCrop.getIntent(act)) }
                } else {
                    currentImageUri = uri
                    selectedBitmap = loadBitmapFromUri(context, uri)
                }
            }
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
                        IconButton(onClick = { navController.navigate("history_screen") }) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_recent_history),
                                contentDescription = stringResource(id = R.string.salvar)
                            )
                        }

                        IconButton(onClick = {
                            historyViewModel.deleteHistory()
                            navController.navigate("history_screen")
                            Toast.makeText(
                                context,
                                "Histórico Apagado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_notification_clear_all),
                                contentDescription = stringResource(id = R.string.delete)
                            )
                        }

                        IconButton(onClick = {
                            navController.navigate("settings_screen")
                             }) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_preferences),
                                contentDescription = stringResource(id = R.string.config)
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

                // Exibe imagem ou placeholder
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
                            painter = painterResource(id = R.mipmap.ic_apy_v2),
                            contentDescription = "Imagem inicial",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                if (!showResultCard) {
                    // Botões principais
                    Button(
                        onClick = {
                            //homeViewModel.setLoading(true)
                            pickImageLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
                    ) {
                        IconCustom(icon = Icons.Filled.FolderOpen, text = "Selecionar Imagem")
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val uri = createImageUri(context)
                            cameraImageUri.value = uri
                            takePictureLauncher.launch(uri)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
                    ) {
                        IconCustom(icon = Icons.Filled.PhotoCamera, text = "Tirar Foto")
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            homeViewModel.setLoading(true)

                            currentImageUri?.let { uri ->
                                coroutineScope.launch(Dispatchers.IO) {
                                    val classifier = ImageClassification(
                                        context = context,
                                        classifierListener = object :
                                            ImageClassification.ClassifierListener {

                                            override fun onError(error: String) {
                                                Handler(Looper.getMainLooper()).post {
                                                    Toast.makeText(
                                                        context,
                                                        "Erro na classificação",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    homeViewModel.setLoading(false)
                                                }
                                            }

                                            override fun onResults(results: List<Pair<String, Float>>?) {
                                                val text =
                                                    results?.joinToString("\n") { (label, score) ->
                                                        "$label: ${
                                                            NumberFormat.getPercentInstance()
                                                                .format(score).trim()
                                                        }"
                                                    } ?: "Sem resultado"

                                                Handler(Looper.getMainLooper()).post {
                                                    homeViewModel.setLoading(false)
                                                    resultText = text
                                                    showResultCard = true

                                                    val history = History(
                                                        result = resultText,
                                                        imageUri = uri.toString(),
                                                        date = setDateFromMillis(System.currentTimeMillis())
                                                    )
                                                    historyViewModel.insertHistory(history)
                                                }
                                            }
                                        }
                                    )
                                    classifier.classifyStationImage(uri)
                                }
                            } ?: run {
                                Toast.makeText(
                                    context,
                                    "Nenhuma imagem selecionada",
                                    Toast.LENGTH_SHORT
                                ).show()
                                homeViewModel.setLoading(false)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            IconCustom(icon = Icons.Filled.Search, text = "Analisar Imagem")
                        }
                    }
                }
                // Resultado
                if (showResultCard) {

                    if (places.isEmpty()) {

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    "Possível diagnóstico",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(resultText, fontSize = 16.sp, color = Color.Gray)
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        ButtonCustom(
                            text = "Retornar",
                            icon = Icons.Filled.DoorBack,
                            onClick = { showResultCard = false }
                        )

                        Spacer(Modifier.height(6.dp))


                        ButtonCustom(
                            text = "Saiba Mais",
                            icon = Icons.Filled.Book,
                            onClick = {
                                showResultCard = false
                                seeMore(context, resultText)
                            }
                        )

                        Spacer(Modifier.height(6.dp))

                        // Botão "Localizar Centro Médico"
                        ButtonCustom(
                            text = "Localizar Centro Médico",
                            icon = Icons.Filled.HealthAndSafety,
                            onClick = {
                                val allGranted = locationPermissions.all {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        it
                                    ) == PackageManager.PERMISSION_GRANTED
                                }

                                if (!allGranted) {
                                    currentLauncher.value.launch(locationPermissions)
                                } else {
                                    //    homeViewModel.fetchNearbyDermatologists(apiKey)
                                    coroutineScope.launch {
                                        val latLng = getCurrentLocation(context)
                                        homeViewModel.fetchNearbyDermatologists(
                                            apiKey = apiKey,
                                            latitude = latLng.latitude,
                                            longitude = latLng.longitude
                                        )
                                    }

                                }
                            }
                        )
                    }
                    when {
                        isLoading -> CircularProgressIndicator()
                        error != null -> Text(
                            error ?: "",
                            color = MaterialTheme.colorScheme.error
                        )

                        places.isNotEmpty() -> {
                            ButtonCustom(
                                text = "Retornar",
                                icon = Icons.Filled.DoorBack,
                                onClick = {
                                    homeViewModel.clearPlaces()
                                    showResultCard = false
                                }
                            )

                            Spacer(Modifier.height(20.dp))

                            LazyColumn {
                                items(places) { place ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        elevation = CardDefaults.cardElevation(4.dp)
                                    ) {
                                        Text(
                                            text = place,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


