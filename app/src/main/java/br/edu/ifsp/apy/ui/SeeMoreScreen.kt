package br.edu.ifsp.apy.ui

import br.edu.ifsp.apy.data.DiseaseInfo
import br.edu.ifsp.apy.data.DiseaseRepository
import android.content.Context
import androidx.appcompat.app.AlertDialog

fun DiseaseDetailDialog(context: Context, disease: DiseaseInfo) {
    AlertDialog.Builder(context)
        .setTitle(disease.title)
        .setMessage(
            """
            O que é:
            ${disease.description}
            
            Possível tratamento:
            ${disease.treatment}
            """.trimIndent()
        )
        .setPositiveButton("Fechar", null)
        .show()
}

fun seeMore(context: Context, resultText: String) {
    // Extrai apenas o nome da doença (sem porcentagem)
    val label = resultText
        .substringBefore(":") // pega o texto antes do primeiro “:”
        .trim()

    val disease = DiseaseRepository.diseaseMap[label]

    disease?.let {
        DiseaseDetailDialog(context, it)
    } ?: run {
        android.widget.Toast
            .makeText(context, "Informações não encontradas.", android.widget.Toast.LENGTH_SHORT)
            .show()
    }
}
