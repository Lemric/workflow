package com.labudzinski.workflow.tools;

public class StringUtils {
    public static String ucfirst(String subject) {
        return Character.toUpperCase(subject.charAt(0)) + subject.substring(1);
    }

}
