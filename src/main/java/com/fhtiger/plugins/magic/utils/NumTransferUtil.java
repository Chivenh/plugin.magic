package com.fhtiger.plugins.magic.utils;


/**
 * 数字转换成英文工具
 * 
 * @author LFH
 * @version 1.0.0
 * @since  2018年3月6日
 */
public final class NumTransferUtil {
	private static final String[] EN_NUM = { // 基本数词表
			"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve",
			"thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen", "twenty", "", "", "", "",
			"", "", "", "", "", "thirty", "", "", "", "", "", "", "", "", "", "forty", "", "", "", "", "", "", "", "",
			"", "fifty", "", "", "", "", "", "", "", "", "", "sixty", "", "", "", "", "", "", "", "", "", "seventy", "",
			"", "", "", "", "", "", "", "", "eighty", "", "", "", "", "", "", "", "", "", "ninety" };
	private static final String[] EN_UNIT = { "hundred", "thousand", "million",
			"billion", "trillion", "quintillion" }; // 单位表

	public static String transferCapital(long num) {
		return transfer(num).toUpperCase();
	}

	public static String transferCapital(String num) {
		return transfer(num).toUpperCase();
	}

	public static String transfer(long num) {
		// long型参数，
		return transfer(String.valueOf(num));
		// 因为long型有极限，所以以字符串参数方法为主
	}

	public static String transfer(String num) {
		// 数字字符串参数
		// 判断字符串是否为数字
		if (!num.matches("\\d+")) {
			return String.format("E:'%s' is not a normal number", num.substring(0,10)+"...");
		}
		num = num.replaceAll("^[0]*([1-9]*)", "$1");
		// 把字符串前面的0去掉
		if (num.length() == 0) {
			// 如果长度为0，则原串都是0
			return EN_NUM[0];
		} else if (num.length() > 9) {
			// 如果大于9，即大于999999999，限制条件
			return "E:too big!";
		}
		// 按3位分割分组
		int count = (num.length() % 3 == 0) ? num.length() / 3 : num.length() / 3 + 1;
		/*if (count > EN_UNIT.length) {
			return "too big!";
		} // 判断组单位是否超过，*/
			// 可以根据需求适当追加enUnit
		String[] group = new String[count];
		for (int i = num.length(), j = group.length - 1; i > 0; i -= 3) {
			group[j--] = num.substring(Math.max(i - 3, 0), i);
		}
		StringBuilder buf = new StringBuilder(); // 结果保存
		for (int i = 0; i < count; i++) { // 遍历分割的组
			int v = Integer.valueOf(group[i]);
			if (v >= 100) { // 因为按3位分割，所以这里不会有超过999的数
				buf.append(EN_NUM[v / 100]).append(" ").append(EN_UNIT[0]).append(" ");
				v = v % 100; // 获取百位，并得到百位以后的数
				if (v != 0) {
					buf.append("and ");
				} // 如果百位后的数不为0，则追加and
			}
			if (v != 0) { // 前提是v不为0才作解析
				if (v < 20 || v % 10 == 0) {
					// 如果小于20或10的整数倍，直接取基本数词表的单词
					buf.append(EN_NUM[v]).append(" ");
				} else { // 否则取10位数词，再取个位数词
					buf.append(EN_NUM[v - v % 10]).append(" ");
					buf.append(EN_NUM[v % 10]).append(" ");
				}
				if (i != count - 1) { // 百位以上的组追加相应的单位
					buf.append(EN_UNIT[count - 1 - i]).append(" ");
				}
			}
		}
		return buf.toString().trim(); // 返回值
	}

}
