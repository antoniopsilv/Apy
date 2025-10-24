package br.edu.ifsp.apy.classification

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class ImageClassification(
    private var threshold: Float = 0.1f,
    private var maxResults: Int = 3,
    private val modelName: String = "model.tflite",
    val context: Context,
    val classifierListener: ClassifierListener?,
) {
    companion object {
        private const val TAG = "ImageClassification"
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(results: List<Pair<String, Float>>?)
    }

    private var interpreter: Interpreter? = null
    private var labels: List<String> = emptyList()
    private var inputImageWidth = 224
    private var inputImageHeight = 224
    private var inputImageChannels = 3

    init {
        setupInterpreter()
    }

    /** Inicializa o Interpreter com MappedByteBuffer e configurações otimizadas */
    private fun setupInterpreter() {
        try {
            // 🔹 Carrega o modelo de forma eficiente
            val modelBuffer = loadModelFile()

            // 🔹 Configura opções do Interpreter
            val options = Interpreter.Options().apply {
                setNumThreads(4) // use 4 threads (ajuste conforme o dispositivo)
                // setUseNNAPI(true) // opcional: aceleração via NNAPI
                // addDelegate(GpuDelegate()) // opcional: aceleração via GPU
            }

            interpreter = Interpreter(modelBuffer, options)
            labels = context.assets.open("labels.txt").bufferedReader().readLines()

            Log.i(TAG, "Modelo e labels carregados com sucesso!")
        } catch (e: Exception) {
            classifierListener?.onError("Falha ao inicializar o modelo: ${e.message}")
            Log.e(TAG, "Erro ao carregar modelo", e)
        }
    }

    /** Carrega o modelo com memória mapeada  */
    private fun loadModelFile(): ByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun classifyStationImage(imageUri: Uri) {
        if (interpreter == null) {
            setupInterpreter()
        }

        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            }.copy(Bitmap.Config.ARGB_8888, true)

            val resized = Bitmap.createScaledBitmap(bitmap, inputImageWidth, inputImageHeight, true)
            val inputBuffer = convertBitmapToByteBuffer(resized)

            val output = Array(1) { FloatArray(labels.size) }

            interpreter?.run(inputBuffer, output)

            val results = output[0]
                .mapIndexed { index, value -> labels[index] to value }
                .filter { it.second >= threshold }
                .sortedByDescending { it.second }
                .take(maxResults)

            classifierListener?.onResults(results)
        } catch (e: Exception) {
            classifierListener?.onError("Erro ao classificar imagem: ${e.message}")
            Log.e(TAG, "Erro ao classificar imagem", e)
        }
    }

    /** Converte Bitmap em ByteBuffer Float32 normalizado [0,1] */
    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val inputBuffer = ByteBuffer.allocateDirect(4 * inputImageWidth * inputImageHeight * inputImageChannels)
        inputBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputImageWidth * inputImageHeight)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var pixelIndex = 0
        for (y in 0 until inputImageHeight) {
            for (x in 0 until inputImageWidth) {
                val pixelValue = intValues[pixelIndex++]
                val r = (pixelValue shr 16 and 0xFF) / 255.0f
                val g = (pixelValue shr 8 and 0xFF) / 255.0f
                val b = (pixelValue and 0xFF) / 255.0f

                inputBuffer.putFloat(r)
                inputBuffer.putFloat(g)
                inputBuffer.putFloat(b)
            }
        }

        return inputBuffer
    }
}
