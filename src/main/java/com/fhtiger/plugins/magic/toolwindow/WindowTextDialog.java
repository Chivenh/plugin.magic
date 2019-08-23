package com.fhtiger.plugins.magic.toolwindow;

import com.fhtiger.plugins.magic.utils.NumTransferUtil;
import com.intellij.openapi.application.ApplicationManager;
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

public class WindowTextDialog extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JPanel textAreaPanel;
	private JTextArea textArea;
	private JTextArea resultTextArea;
	private JButton SqlInParams;
	private JButton Html2Text;
	private JButton NumTransfer;
	private final static String EMPTY="";

	public WindowTextDialog(ToolWindow toolWindow) {

		setContentPane(this.contentPane);
		this.textAreaPanel.setAutoscrolls(true);
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
		this.NumTransfer.addActionListener(this::execNumberTransfer);

		/*在点击此按钮时执行复制操作*/
		this.buttonOK.addActionListener(e->{
			//结果展示区域为空时,不执行复制操作.
			if(StringUtils.isEmpty(this.resultTextArea.getText())){
				return;
			}
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

		this.textArea.setBorder(BorderFactory.createDashedBorder(JBColor.LIGHT_GRAY));
		this.resultTextArea.setBorder(BorderFactory.createDashedBorder(JBColor.LIGHT_GRAY));
	}

	/**
	 * @param action 将列表参数处理成sql中in的条件参数.
	 */
	private void execSqlInParamsAction(ActionEvent action){
		String text = this.textArea.getText();

		if(StringUtils.isNotEmpty(text)){
			String replace = "(\\r|\\n|\\r\\n)";

			String result = text.replaceAll(replace,"','").replaceFirst(",'$","");

			if(!result.startsWith("'")){
				result = '\''+result;
			}
			if(!result.endsWith("'")){
				result+='\'';
			}

			this.resultTextArea.setText(result);
			actionDown(this.SqlInParams,"Transfer Successful!");
		}
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
		actionDown(this.Html2Text,"Text Transfer Successful!");
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

	/**
	 * 将数字转换成英文表述
	 */
	private void execNumberTransfer(ActionEvent actionEvent){
		String text = this.textArea.getText();

		if(StringUtils.isNotEmpty(text)){

			String result= NumTransferUtil.transfer(text);

			if(result.startsWith("E:")){
				actionDownError(this.NumTransfer,result.substring(2).replaceFirst("('.+')","<b>$1</b>"));
			}else{
				this.resultTextArea.setText(result);
				actionDown(this.NumTransfer,"Transfer Number to <i>English Text</i> Successful!");
			}

		}
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
					.show(RelativePoint.fromScreen(button.getLocationOnScreen()), Balloon.Position.atLeft);
		});
	}

	/**
	 * @param button 按钮
	 * @param result 提示信息(错误信息)
	 */
	private static void actionDownError(JButton button,String result) {
		ApplicationManager.getApplication().invokeLater(() -> {
			JBPopupFactory factory = JBPopupFactory.getInstance();
			factory.createHtmlTextBalloonBuilder(result, null,JBColor.YELLOW, new JBColor(new Color(238, 125, 89), new Color(
					222, 88, 25)), null)
					.setFadeoutTime(3000)
					.createBalloon()
					.show(RelativePoint.fromScreen(button.getLocationOnScreen()), Balloon.Position.atLeft);
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
//				button.updateUI();
			}

			@Override public void mouseMoved(MouseEvent e) {
				button.setBackground(button.getBackground().brighter());
//				button.updateUI();
			}

			@Override public void mouseExited(MouseEvent e) {
				button.setBackground(button.getBackground().brighter());
//				button.updateUI();
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
//				button.updateUI();
			}

			@Override public void focusLost(FocusEvent e) {
				button.setBackground(button.getBackground().brighter());
//				button.updateUI();
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
