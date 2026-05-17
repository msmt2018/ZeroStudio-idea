package android.zero.studio.editor.api

data class OpenManyRequest(
    val paths: List<String>,
    val languageId: String = "plaintext",
)

data class OpenManyResult(
    val openedCount: Int,
    val failedPaths: List<String>,
)

interface OpenManyEditorsUseCase {
    suspend operator fun invoke(request: OpenManyRequest): OpenManyResult
}

interface SaveDirtyEditorsUseCase {
    suspend operator fun invoke(): Int
}

interface CloseAllEditorsUseCase {
    suspend operator fun invoke(): Int
}
