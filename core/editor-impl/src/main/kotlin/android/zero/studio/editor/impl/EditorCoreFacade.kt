package android.zero.studio.editor.impl

import android.zero.studio.editor.api.*

/**
 * App 层装配入口：聚合常用 editor 能力，减少 Activity/ViewModel 手工拼装复杂度。
 */
class EditorCoreFacade(
    val workspace: PersistentEditorWorkspace,
    val coordinator: EditorCoordinator,
    val openFilesService: EditorOpenFilesService,
    val commandPaletteUseCase: CommandPaletteUseCase,
    val searchInWorkspaceUseCase: SearchInWorkspaceUseCase,
    val bulkReplaceInWorkspaceUseCase: BulkReplaceInWorkspaceUseCase,
    val metricsUseCase: EditorMetricsUseCase,
    val exportEditorStateUseCase: ExportEditorStateUseCase,
)

object EditorCoreFacadeFactory {
    fun createDefault(): EditorCoreFacade {
        val workspace = PersistentEditorWorkspace(PersistentEditorSessionFactory())
        val workspaceManager = EditorWorkspaceManagerImpl(workspace)

        val openUseCase = OpenEditorUseCaseImpl(workspaceManager)
        val saveUseCase = SaveEditorUseCaseImpl(workspaceManager)
        val openManyUseCase = OpenManyEditorsUseCaseImpl(openUseCase)

        val coordinator = EditorCoordinatorImpl(workspace, openUseCase, saveUseCase)

        val openFilesService = EditorOpenFilesServiceImpl(
            openEditorUseCase = openUseCase,
            openManyEditorsUseCase = openManyUseCase,
        )

        val autosave = EditorAutosaveControllerImpl(workspace)
        val saveDirty = SaveDirtyEditorsUseCaseImpl(autosave)
        val closeAll = CloseAllEditorsUseCaseImpl(workspace)

        val commandPalette = CommandPaletteUseCaseImpl(saveDirty, closeAll)
        val search = WorkspaceSearchUseCaseImpl(workspace)
        val bulkReplace = BulkReplaceInWorkspaceUseCaseImpl(workspace)

        val collector = InMemoryEditorMetricsCollector()
        val metrics = MetricsEditorMetricsUseCase(collector)

        val export = EditorSnapshotExportUseCaseImpl(workspace)

        return EditorCoreFacade(
            workspace = workspace,
            coordinator = coordinator,
            openFilesService = openFilesService,
            commandPaletteUseCase = commandPalette,
            searchInWorkspaceUseCase = search,
            bulkReplaceInWorkspaceUseCase = bulkReplace,
            metricsUseCase = metrics,
            exportEditorStateUseCase = export,
        )
    }
}
