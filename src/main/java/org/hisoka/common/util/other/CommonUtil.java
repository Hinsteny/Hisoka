package org.hisoka.common.util.other;

import org.hisoka.common.exception.BusinessException;
import org.hisoka.common.util.string.StringUtil;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @author Hinsteny
 * @date 2016/10/19
 * @copyright: 2016 All rights reserved.
 */
public class CommonUtil {

    public static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isTrue(Boolean o) {
        if (o == null) {
            return false;
        } else {
            return o;
        }
    }

    /**
     * List是否为空
     *
     * @param list
     * @return
     */
    public static <E> boolean isListEmpty(List<E> list) {
        return list == null || list.isEmpty();
    }

    public static void assertNotBlank(String text, String message) {
        if (text == null || text.trim().isEmpty())
            throw new BusinessException(message);
    }

    public static <T> void assertNotNull(T t, String message) {
        if (t == null)
            throw new BusinessException(message);
    }

    public static <T> void assertListNotNull(List<T> list, String message) {
        if (isListEmpty(list))
            throw new BusinessException(message);
    }

    public static String formatNum(Object num) {
        DecimalFormat df = new DecimalFormat("#0.##########");
        return df.format(num);
    }

    public static <T> void assertNotEq(String arg1, String arg2, String message) {
        if (!arg1.equalsIgnoreCase(arg2)) {
            throw new BusinessException(message);
        }
    }

    /**
     * List内部去重后还保持顺序
     *
     * @param list
     */
    public static void removeDuplicateWithOrder(List<String> list) {
        Set<String> set = new HashSet<String>();
        List<String> newList = new ArrayList<String>();
        for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
            String element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
        list.clear();
        list.addAll(newList);
    }

    /**
     * 判断当前操作系统是否是windows
     *
     * @return
     */
    public static boolean isWindows() {
        return (OS.indexOf("win") != -1);
    }

    /**
     * 获取list对应的字符串(中间以指定分隔符链接)
     *
     * @param list 需要转换的列表
     * @param splitSign 指定分隔符
     * @return list对应的字符串
     */
    public static String getListStr(List<String> list, String splitSign) {
        StringBuffer listSb = new StringBuffer();

        if (list != null && !list.isEmpty()) {
            int size = list.size();

            for (int i = 0; i < size; i++) {
                listSb.append(list.get(i));
                int end = size - 1;

                if (i != end) {
                    listSb.append(splitSign);
                }
            }
        }

        return listSb.toString();
    }

    /**
     * 获取stringArray对应的字符串(中间以指定分隔符链接)
     *
     * @param strArray 需要转换的列表
     * @param splitSign 指定分隔符
     * @return list对应的字符串
     */
    public static String getStringArrayStr(String[] strArray, String splitSign) {
        StringBuffer stringArraySb = new StringBuffer();

        if (strArray != null && strArray.length > 0) {
            int size = strArray.length;

            for (int i = 0; i < size; i++) {
                stringArraySb.append(strArray[i]);
                int end = size - 1;

                if (i != end) {
                    stringArraySb.append(splitSign);
                }
            }
        }

        return stringArraySb.toString();
    }

    /**
     * 获取字符串对应的list
     *
     * @param listStr 需要转换的列表
     * @param splitSign 指定分隔符
     * @return list对应的字符串
     */
    public static List<String> getList(String listStr, String splitSign) {
        List<String> list = new ArrayList<String>();

        if (StringUtil.isNotBlank(listStr)) {
            String[] array = listStr.split(splitSign);

            for (String e : array) {
                list.add(e);
            }
        }

        return list;
    }

}
