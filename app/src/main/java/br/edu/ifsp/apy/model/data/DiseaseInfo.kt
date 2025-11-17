package br.edu.ifsp.apy.data

data class DiseaseInfo(
    val title: String,
    val description: String,
    val treatment: String
)

object DiseaseRepository {
    val diseaseMap = mapOf(
        "Queratoses actínicas / Doença de Bowen" to DiseaseInfo(
            title = "Queratoses actínicas / Doença de Bowen",
            description = "Lesões de pele causadas pelo sol que podem virar câncer se não tratadas.",
            treatment = """
                Lesões cutâneas pré-malignas causadas pela exposição crônica ao sol. 
                O tratamento visa eliminar as lesões e prevenir evolução para carcinoma espinocelular. 
                As opções incluem crioterapia, curetagem, laser, terapias tópicas 
                (5-fluorouracil, imiquimode, diclofenaco, mebutato de ingenol) e terapia fotodinâmica.
            """.trimIndent()
        ),
        "Carcinoma basocelular" to DiseaseInfo(
            title = "Carcinoma basocelular",
            description = "Câncer de pele mais comum e menos agressivo.",
            treatment = "Tratado com cirurgia, curetagem ou terapias tópicas."
        ),
        "Lesões benignas semelhantes à queratose" to DiseaseInfo(
            title = "Lesões benignas semelhantes à queratose",
            description = "Lesões benignas parecidas com queratoses actínicas.",
            treatment = "Normalmente não exigem tratamento, a menos que haja desconforto estético."
        ),
        "Dermatofibroma (nódulo benigno)" to DiseaseInfo(
            title = "Dermatofibroma (nódulo benigno)",
            description = "Nódulo firme e benigno da pele, geralmente inofensivo.",
            treatment = "Pode ser removido cirurgicamente por motivos estéticos."
        ),
        "Melanoma (câncer de pele grave)" to DiseaseInfo(
            title = "Melanoma (câncer de pele grave)",
            description = "Câncer de pele agressivo e potencialmente mortal.",
            treatment = "Tratamento cirúrgico, podendo incluir imunoterapia ou quimioterapia."
        ),
        "Nevo (pinta ou sinal benigno)" to DiseaseInfo(
            title = "Nevo (pinta ou sinal benigno)",
            description = "Mancha pigmentada comum na pele.",
            treatment = "Não requer tratamento, mas deve ser monitorado regularmente."
        ),
        "Nenhuma doença detectada" to DiseaseInfo(
            title = "Nenhuma doença detectada",
            description = "Nenhuma alteração suspeita foi identificada.",
            treatment = "Continue com proteção solar e acompanhamento dermatológico. Aproveite seu dia !"
        ),
        "Lesões vasculares" to DiseaseInfo(
            title = "Lesões vasculares",
            description = "Alterações nos vasos sanguíneos da pele.",
            treatment = "Podem ser tratadas com laser ou cirurgia, dependendo do caso."
        )
    )
}
