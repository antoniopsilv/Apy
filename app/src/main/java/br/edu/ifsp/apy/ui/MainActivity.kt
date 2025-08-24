package br.edu.ifsp.apy.ui

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.ifsp.apy.R
import br.edu.ifsp.apy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

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

        binding.button.setOnClickListener {
            getImageFromGallery.launch("image/*")
        }
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
