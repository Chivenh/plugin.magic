package com.fhtiger.plugins.magic.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * TextAreaFactory
 *
 * @author LFH
 * @since 2019年08月16日 18:01
 */
public class TextAreaFactory implements ToolWindowFactory {

	@Override
	public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
		WindowTextDialog windowTextDialog = new WindowTextDialog(toolWindow);
		ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
		Content content = contentFactory.createContent(windowTextDialog.getContentPane(), "Text-Area", false);
		toolWindow.getContentManager().addContent(content);
	}
}
