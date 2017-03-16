package org.hisoka.common.util.string;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hinsteny
 * @date 2016/10/19
 * @copyright: 2016 All rights reserved.
 */
public class StringUtil extends StringUtils {

    /**
     * 判断字符创是否是null
     *
     * @param s
     * @return
     */
    public static boolean isNull(String s) {
        return s == null;
    }


    /**
     * 判断是否为整数
     *
     * @param s 传入的字符串
     * @return 是整数返回true,否则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 替换字符串的空白、换行符、制表符、回车为指定符号
     *
     * @param str
     * @return 结果字符串
     */
    public static String replaceBlankToCustom(String str, String symbol) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s+|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll(symbol);
        }
        if (String.valueOf(dest.charAt(0)).equals(symbol)) {
            // 检查首是否有逗号,如果有逗号就去掉
            dest = dest.substring(1, dest.length());
        }
        if (String.valueOf(dest.charAt(dest.length() - 1)).equals(symbol)) {
            // 检查首是否有逗号,如果有逗号就去掉
            dest = dest.substring(0, dest.length() - 1);
        }
        return dest;
    }

    public static String replaceBlank1(String str) {
        if(StringUtils.isBlank(str)){
            return str;
        }
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 去掉字符串首位的换行符、制表符、回车
     *
     * @param str
     * @return
     */
    public static String replaceTrnToBlank(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll(" ");
        }
        return dest;
    }

    /**
     * 去掉字符串前后的指定字符
     *
     * @param str 原字符串
     * @param trimStr 需要被去掉的字符
     * @return
     */
    public static String trimString(String str, String trimStr) {
        if (str == null || str.length() == 0 || trimStr == null || trimStr.length() == 0) {
            return str;
        }

        // 结束位置
        int epos = 0;

        // 正规表达式
        String regpattern = "[" + trimStr + "]*+";
        Pattern pattern = Pattern.compile(regpattern, Pattern.CASE_INSENSITIVE);

        // 去掉结尾的指定字符
        StringBuffer buffer = new StringBuffer(str).reverse();
        Matcher matcher = pattern.matcher(buffer);
        if (matcher.lookingAt()) {
            epos = matcher.end();
            str = new StringBuffer(buffer.substring(epos)).reverse().toString();
        }

        // 去掉开头的指定字符
        matcher = pattern.matcher(str);
        if (matcher.lookingAt()) {
            epos = matcher.end();
            str = str.substring(epos);
        }

        // 返回处理后的字符串
        return str;
    }

    /**
     * 字符串首字母大写
     *
     * @param str
     * @return
     */
    public static String toUpperCaseFirstChar(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }

        if (Character.isUpperCase(str.charAt(0))) {
            return str;
        } else {
            return (new StringBuilder()).append(Character.toUpperCase(str.charAt(0))).append(str.substring(1)).toString();
        }
    }

    /**
     * 字符串首字母小写
     *
     * @param str
     * @return
     */
    public static String toLowerCaseFirstChar(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }

        if (Character.isLowerCase(str.charAt(0))) {
            return str;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
        }
    }


}
