package org.erhun.framework.basic.utils;

import java.util.Date;

public class LunarDateTest {
    public static void main(String[] args) {
        LunarDate ld = new LunarDate(new Date());
        System.out.println(ld.toString());
    }
}
