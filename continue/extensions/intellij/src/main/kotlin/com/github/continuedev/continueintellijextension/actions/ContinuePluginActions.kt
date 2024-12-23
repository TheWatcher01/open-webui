package com.github.continuedev.continueintellijextension.actions

import com.github.continuedev.continueintellijextension.editor.DiffStreamService
import com.github.continuedev.continueintellijextension.services.ContinuePluginService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager

fun getPluginService(project: Project?): ContinuePluginService? {
    if (project == null) {
        return null
    }
    return ServiceManager.getService(
        project,
        ContinuePluginService::class.java
    )
}

class AcceptDiffAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        acceptHorizontalDiff(e)
        acceptVerticalDiff(e)
    }

    private fun acceptHorizontalDiff(e: AnActionEvent) {
        val continuePluginService = getPluginService(e.project) ?: return
        continuePluginService?.diffManager?.acceptDiff(null)
    }

    private fun acceptVerticalDiff(e: AnActionEvent) {
        val project = e.project ?: return
        val editor =
            e.getData(PlatformDataKeys.EDITOR) ?: FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val diffStreamService = project.service<DiffStreamService>()
        diffStreamService.accept(editor)
    }
}

class RejectDiffAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        rejectHorizontalDiff(e)
        rejectVerticalDiff(e)
    }

    private fun rejectHorizontalDiff(e: AnActionEvent) {
        val continuePluginService = getPluginService(e.project) ?: return
        continuePluginService.diffManager?.rejectDiff(null)
    }

    private fun rejectVerticalDiff(e: AnActionEvent) {
        val project = e.project ?: return
        val editor =
            e.getData(PlatformDataKeys.EDITOR) ?: FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val diffStreamService = project.service<DiffStreamService>()
        diffStreamService.reject(editor)
    }
}

fun getContinuePluginService(project: Project?): ContinuePluginService? {
    if (project != null) {
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow("Continue")

        if (toolWindow != null) {
            if (!toolWindow.isVisible) {
                toolWindow.activate(null)
            }
        }
    }

    return getPluginService(project)
}

fun focusContinueInput(project: Project?) {
    val continuePluginService = getContinuePluginService(project) ?: return
    continuePluginService.continuePluginWindow?.content?.components?.get(0)?.requestFocus()
    continuePluginService.sendToWebview("focusContinueInputWithNewSession", null)

    continuePluginService.ideProtocolClient?.sendHighlightedCode()
}

class FocusContinueInputWithoutClearAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        focusContinueInput(project)
    }
}

class FocusContinueInputAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val continuePluginService = getContinuePluginService(e.project) ?: return

        continuePluginService.continuePluginWindow?.content?.components?.get(0)?.requestFocus()
        continuePluginService.sendToWebview("focusContinueInputWithNewSession", null)

        continuePluginService.ideProtocolClient?.sendHighlightedCode()
    }
}

class NewContinueSessionAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val continuePluginService = getContinuePluginService(e.project) ?: return
        continuePluginService.continuePluginWindow?.content?.components?.get(0)?.requestFocus()
        continuePluginService.sendToWebview("focusContinueInputWithNewSession", null)
    }
}

class ViewHistoryAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val continuePluginService = getContinuePluginService(e.project) ?: return
        continuePluginService.sendToWebview("viewHistory", null)
    }
}

class OpenConfigAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val continuePluginService = getContinuePluginService(e.project) ?: return
        val params = mapOf("profileId" to null)
        continuePluginService.coreMessenger?.request("config/openProfile", params, null) { _ -> }
    }
}

class OpenMorePageAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val continuePluginService = getContinuePluginService(e.project) ?: return
        continuePluginService.continuePluginWindow?.content?.components?.get(0)?.requestFocus()
        val params = mapOf("path" to "/more", "toggle" to true)
        continuePluginService.sendToWebview("navigateTo", params)
    }
}

class OpenAccountDialogAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val continuePluginService = getContinuePluginService(e.project) ?: return
        continuePluginService.sendToWebview("openDialogMessage", "account")
    }
}