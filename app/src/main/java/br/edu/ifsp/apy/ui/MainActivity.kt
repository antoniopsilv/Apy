package br.edu.ifsp.apy.ui

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.apy.databinding.ActivityMainBinding
import com.yalantis.ucrop.UCrop
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var currentImageUri: Uri? = null

//    private lateinit var imageClassifierHelper: ImageClassifierHelper
    
//    private lateinit var classifier: SkinCancerClassifier

    private val getImageFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                try {
                    val bitmap: Bitmap = loadBitmapFromUri(uri)
                    binding.imageView.setImageBitmap(bitmap)

//                    val resultIndex = classifier.classify(bitmap)
//                    val resultText = when (resultIndex) {
//                        0 -> "Lesão Benigna"
//                        1 -> "Lesão Suspeita"
//                        else -> "Erro ao classificar"
//                    }

                    // Continuar com o UCrop
                    UCrop.of(uri, Uri.fromFile(cacheDir.resolve("${System.currentTimeMillis()}.jpg")))
                        .withAspectRatio(16f, 9f)
                        .withMaxResultSize(2000, 2000)
                        .start(this)

                    binding.resultText.text = "" //resultText

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Erro ao processar imagem", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Nenhuma imagem selecionada", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        classifier = SkinCancerClassifier(this)

        binding.buttonSelectImage.setOnClickListener {
            getImageFromGallery.launch("image/*")
        }

        binding.buttonAnalyzeImage.setOnClickListener {
            currentImageUri?.let {
                analyzeImage(it)
            }
        }
    }

    private fun analyzeImage(it: Uri) {

//        imageClassifierHelper = ImageClassifierHelper(
//            context = this,
//            classifierListener = object : ImageClassifierHelper.ClassifierListener {
//
//                override fun onResults(results: List<Classifications>?) {
//                    // TODO: Progress Indicator Hilang
//                    runOnUiThread {
//                        val resultText = results?.joinToString("\n") {
//                            it.categories[0].label + ": " + NumberFormat.getPercentInstance()
//                                .format(it.categories[0].score).trim()
//                        }
//                    }
//                }
//            }
//        )
//        imageClassifierHelper.classifyStaticImage(imageUri)
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
    }
}
