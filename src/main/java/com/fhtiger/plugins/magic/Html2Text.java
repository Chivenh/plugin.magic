package com.fhtiger.plugins.magic;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * test
 *
 * @author LFH
 * @since 2019年08月16日 14:38
 */
public class Html2Text extends AnAction {
	@Override
	public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
		FileEditor fileEditor =  anActionEvent.getData(DataKeys.FILE_EDITOR);

		Project theProject = anActionEvent.getProject();
		if(fileEditor!=null){
			VirtualFile file = fileEditor.getFile();
				if(file!=null){
					try (InputStream stream = file.getInputStream()){

						byte [] buffer = new byte[1024];

						List<String> content = new ArrayList<>();

						while (stream.available()>0) {
							stream.read(buffer);
							content.add(new String(buffer, StandardCharsets.UTF_8));
						}

						Document document = Jsoup.parse(String.join("\n", content));

						if(document!=null){
							ApplicationManager.getApplication().runWriteAction(()->{
								CommandProcessor.getInstance().executeCommand(theProject, ()->{
									try {
										FileUtils.writeStringToFile(new File(file.getParent().getPath(),file.getName()+".txt"),document.body().text(),StandardCharsets.UTF_8.name());
									}catch (IOException xe){
										xe.printStackTrace();
									}

								}, "html2text", ActionGroup.ACTIONS_KEY);
							});
						}

					}catch (IOException ie){
						ie.printStackTrace();
					}
				}

		}
	}
}
