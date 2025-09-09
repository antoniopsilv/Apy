package br.edu.ifsp.apy.classification

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.util.Log
import androidx.camera.core.ImageProcessor
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import kotlin.io.encoding.Base64

class ImageClassification (
    private var threshold: Float = 0.1f,
    private var maxResults: Int = 3,
    private val modelName: String = "cancer_classification.tflite",
    val context: Context,
    val classifierListener: ClassifierListener?,

) {
    companion object {
        private const val TAG = "ImageClassification"
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>?
        )
    }

    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {

        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)

        val baseOptionsBuilder = BaseOptions.builder()
            .setNumThreads(4)

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            classifierListener?.onError("Falha em inicializar a classificação de imagem. Verifique os logs ...")
            Log.e(TAG, e.message.toString())
        }
    }

    fun classifyStationImage(imageUri: Uri) {

        if(imageClassifier == null) {
            setupImageClassifier()
        }

        val imageProcessor = org.tensorflow.lite.support.image.ImageProcessor.Builder()
            .add(ResizeOp(224,224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(CastOp(DataType.UINT8))
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        }.copy(Bitmap.Config.ARGB_8888, true)?.let {
            val tensorImage = imageProcessor.process(TensorImage.fromBitmap(it))

            val imageProcessingOptions =  ImageProcessingOptions.builder()
                .build()

            val results = imageClassifier?.classify(tensorImage, imageProcessingOptions)

            classifierListener?.onResults(results)
        }

    }

}