package com.fhtiger.plugins.magic.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * WindowFactory
 *
 * @author LFH
 * @since 2019年08月16日 17:20
 */
public class WindowFactory implements ToolWindowFactory {
	// Create the tool window content.
	@Override
	public void createToolWindowContent(@NotNull Project project,@NotNull ToolWindow toolWindow) {
		DefineToolWindow myToolWindow = new DefineToolWindow(toolWindow);
		ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
		Content content = contentFactory.createContent(myToolWindow.getContent(), "", false);
		toolWindow.getContentManager().addContent(content);
	}
}