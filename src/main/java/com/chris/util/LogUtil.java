package com.chris.util;

public class LogUtil {
    public static String getStackMsg(Exception e) {

        StringBuffer sb = new StringBuffer();
        sb.append("[ERROR]"+e.getMessage());
        StackTraceElement[] stackArray = e.getStackTrace();
        for (int i = 0; i < stackArray.length; i++) {
            StackTraceElement element = stackArray[i];
            sb.append(element.toString() + "\n");
        }
        return sb.toString();
    }

    public static String getStackMsg(Throwable e) {

        StringBuffer sb = new StringBuffer();
        sb.append("[ERROR]"+e.getMessage());
        StackTraceElement[] stackArray = e.getStackTrace();
        for (int i = 0; i < stackArray.length; i++) {
            StackTraceElement element = stackArray[i];
            sb.append(element.toString() + "\n");
        }
        return sb.toString();
    }
}
