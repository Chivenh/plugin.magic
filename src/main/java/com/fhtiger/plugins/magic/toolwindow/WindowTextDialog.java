package com.fhtiger.plugins.magic.toolwindow;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;
import com.intellij.ui.awt.RelativePoint;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WindowTextDialog extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JPanel textAreaPanel;
	private JTextArea textArea;
	private JTextArea resultTextArea;
	private JButton SqlInParams;
	private JButton Html2Text;
	private final static String EMPTY="";

	public WindowTextDialog(ToolWindow toolWindow) {

		setContentPane(this.contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		this.buttonOK.addActionListener(e -> onOK());

		this.buttonCancel.addActionListener(e -> {
			toolWindow.hide(null);
			onCancel();
		});

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		this.contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		this.SqlInParams.addActionListener(this::execSqlInParamsAction);
		this.Html2Text.addActionListener(this::execHtml2Text);

		/*在点击此按钮时执行复制操作*/
		this.buttonOK.addActionListener(e->{
			this.resultTextArea.requestFocus();
			this.resultTextArea.selectAll();
			this.resultTextArea.copy();
			actionDown(this.buttonOK,"Copy Successful!");
		});

		this.buttonOK.addMouseListener(getMouseEnterAdapter(this.buttonOK));
		this.buttonCancel.addMouseListener(getMouseEnterAdapter(this.buttonCancel));
		this.SqlInParams.addMouseListener(getMouseEnterAdapter(SqlInParams));
		this.Html2Text.addMouseListener(getMouseEnterAdapter(this.Html2Text));

		//自动滚动到末尾
		this.resultTextArea.setAutoscrolls(true);
		this.textArea.setAutoscrolls(true);

		this.textArea.setBorder(BorderFactory.createBevelBorder(1));
		this.resultTextArea.setBorder(BorderFactory.createBevelBorder(1));
	}


	private void execSqlInParamsAction(ActionEvent action){
		String text = this.textArea.getText();

		if(StringUtils.isNotEmpty(text)){
			String replace = "(\\r|\\n|\\r\\n)";

			String result = text.replaceAll(replace,"','").replaceFirst(",'$","");

			if(!result.startsWith("'")){
				result = '\''+result;
			}

			this.resultTextArea.setText(result);
		}
		actionDown(this.SqlInParams,"Transfer Successful!");
	}

	/**
	 * @param action 转换html内容为纯文本内容
	 */
	private void execHtml2Text(ActionEvent action){
		String text = this.textArea.getText();

		Document doc = Jsoup.parse(text);
		String htmlContent ="";
		if(doc!=null){
			Element body = doc.body();

			List<String> contents = getNodesText(body);

			htmlContent = String.join("",contents);
		}

		this.resultTextArea.setText(htmlContent);
	}

	/**
	 * @param node 元素节点
	 * @return 元素文本集
	 */
	private List<String> getNodesText(Element node){
		List<String> texts = new ArrayList<>(0);
		Elements children = node.children();

		if(children.isEmpty()){
			texts.add(node.text());
			if(node.isBlock()){
				texts.add("\n");
			}
			return texts;
		}

		for (Element child : children) {
			texts.addAll( getNodesText(child));
		}
		if(node.isBlock()){
			texts.add("\n");
		}
		return texts;
	}

	private void onOK() {
		// add your code here
		dispose();
	}

	private void onCancel() {
		// add your code here if necessary
		dispose();
		this.reset();
	}

	@Override
	public JPanel getContentPane() {
		return contentPane;
	}


	/**
	 * @param button 按钮
	 * @param result 提示信息
	 */
	private static void actionDown(JButton button,String result) {
		ApplicationManager.getApplication().invokeLater(() -> {
			JBPopupFactory factory = JBPopupFactory.getInstance();
			factory.createHtmlTextBalloonBuilder(result, null, new JBColor(new Color(186, 238, 186), new Color(73, 117, 73)), null)
					.setFadeoutTime(3000)
					.createBalloon()
					.show(RelativePoint.fromScreen(button.getLocationOnScreen()), Balloon.Position.below);
		});
	}

	/**
	 * @param button 按钮
	 * @return 鼠标事件
	 */
	@NotNull private MouseAdapter getMouseEnterAdapter(JButton button) {
		return new MouseAdapter() {
			@Override public void mouseEntered(MouseEvent e) {
				button.setBackground(button.getBackground().darker());
				button.updateUI();
//				super.mouseEntered(e);
			}

			@Override public void mouseMoved(MouseEvent e) {
				button.setBackground(button.getBackground().brighter());
				button.updateUI();
//				super.mouseMoved(e);
			}

			@Override public void mouseExited(MouseEvent e) {
				button.setBackground(button.getBackground().brighter());
				button.updateUI();
			}
		};
	}

	/**
	 * @param button 按钮
	 * @return 聚焦事件
	 */
	@NotNull
	private FocusAdapter getFocusAdapter(JButton button) {
		return new FocusAdapter() {
			@Override public void focusGained(FocusEvent e) {
				button.setBackground(button.getBackground().darker());
				button.updateUI();
//				super.focusGained(e);
			}

			@Override public void focusLost(FocusEvent e) {
				button.setBackground(button.getBackground().brighter());
				button.updateUI();
//				super.focusLost(e);
			}
		};
	}

	/**
	 * 重置输入/输出文本框
	 */
	private void reset(){
		this.textArea.setText(EMPTY);
		this.resultTextArea.setText(EMPTY);
	}

}
