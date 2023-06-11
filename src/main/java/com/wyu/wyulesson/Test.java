package com.wyu.wyulesson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author novo
 * @since 2023-05-21
 */
public class Test {
    public static void main(String[] args) throws ParseException {

        System.out.println(Integer.parseInt("12"));
        System.out.println(Integer.parseInt("09"));
        System.out.println(getCurrentTerm());

        System.out.println(getCurrentWeek("2023-2-19"));

        System.out.println(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
    }

    public static String getCurrentTerm() {
        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH) + 1;
        int year = now.get(Calendar.YEAR);
        if (month < 8) {
            return year - 1 + "02";
        } else {
            return year + "01";
        }
    }

    public static int getCurrentWeek(String startStr) {
        int week = 1;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date start = dateFormat.parse(startStr);

            // 获得当前日期与本周日相差的天数
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);
            // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            long daysBetween = (Calendar.getInstance().getTime().getTime() - start.getTime() + 1000000) / (60 * 60 * 24 * 1000);
            week = (int) (daysBetween / 7 + 1);
            if (dayOfWeek + daysBetween % 7 > 7) {
                week += 1;
            }
        } catch (ParseException e) {

        }
        return week;
    }


}
