package com.fhtiger.plugins.magic.toolwindow;

import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.util.Calendar;

/**
 * ToolWindow
 *
 * @author LFH
 * @since 2019年08月16日 17:21
 */
public class DefineToolWindow {
	private JButton refreshToolWindowButton;
	private JButton hideToolWindowButton;
	private JLabel currentDate;
	private JLabel currentTime;
	private JLabel timeZone;
	private JPanel defineToolWindowContent;

	public DefineToolWindow(ToolWindow toolWindow) {
		hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
		refreshToolWindowButton.addActionListener(e -> currentDateTime());

		this.currentDateTime();
	}


	public void currentDateTime() {
		// Get current date and time
		Calendar instance = Calendar.getInstance();
		currentDate.setText(instance.get(Calendar.DAY_OF_MONTH) + "/"
				+ instance.get(Calendar.MONTH) + 1 + "/" +
				instance.get(Calendar.YEAR));
		currentDate.setIcon(new ImageIcon(getClass().getResource("/defineToolWindow/Calendar-icon.png")));
		int min = instance.get(Calendar.MINUTE);
		String strMin;
		if (min < 10) {
			strMin = "0" + min;
		} else {
			strMin = String.valueOf(min);
		}
		currentTime.setText(instance.get(Calendar.HOUR_OF_DAY) + ":" + strMin);
		currentTime.setIcon(new ImageIcon(getClass().getResource("/defineToolWindow/Time-icon.png")));
		// Get time zone
		long gmt_Offset = instance.get(Calendar.ZONE_OFFSET); // offset from GMT in milliseconds
		String str_gmt_Offset = String.valueOf(gmt_Offset / 36e5);
		str_gmt_Offset = (gmt_Offset > 0) ? "GMT + " + str_gmt_Offset : "GMT - " + str_gmt_Offset;
		timeZone.setText(str_gmt_Offset);
		timeZone.setIcon(new ImageIcon(getClass().getResource("/defineToolWindow/Time-zone-icon.png")));


	}

	public JPanel getContent() {
		return defineToolWindowContent;
	}
}
