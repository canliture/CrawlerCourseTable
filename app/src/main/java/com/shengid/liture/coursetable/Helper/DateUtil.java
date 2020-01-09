package com.shengid.liture.coursetable.Helper;

import android.content.Context;
import android.widget.Toast;

import java.util.Calendar;

public class DateUtil {
    private static Calendar carlendar;
    private static long startMills;
    private static long nowMills;

    public static long weekThFromStart;
    public static long dayThFromStart;
    public static int dayOfWeekNow;

    static {
        carlendar =  Calendar.getInstance();
        // TODO change date
        carlendar.set(2019, 9-1, 2);

        startMills = carlendar.getTimeInMillis();

        carlendar = Calendar.getInstance();

        nowMills = carlendar.getTimeInMillis();

        long gapDays = ( nowMills - startMills) / (1000*60*60*24) + 1;
        dayThFromStart = gapDays;
        weekThFromStart = gapDays/7 + (gapDays%7==0?0:1);
        dayOfWeekNow = carlendar.get( Calendar.DAY_OF_WEEK ) - 1;           // carlendar.get( Calendar.DAY_OF_WEEK )      // -1
                                                                              //1,    2,   3,  4...                          //0  ,  1,   2,  3,  4,  5,  6
                                                                             //Sun   Mon  ...                               // Sun   Mon
    }
    // String format : "year,month,day"
    public static String firstDayOfSomeWeek(int weekTh){
        carlendar = Calendar.getInstance();
        // TODO change date
        carlendar.set(2019, 9-1, 2);        //month starts from 0

        carlendar.add(Calendar.DATE, (weekTh-1)*7);

        return carlendar.get(Calendar.YEAR) + "," + (carlendar.get(Calendar.MONTH)+1) + "," + carlendar.get(Calendar.DATE);
    }

    //for test ,  change private to public
    private static void showData(Context context){

        String nowYMD = "" +
                carlendar.get( Calendar.YEAR ) + "年" +
                (carlendar.get( Calendar.MONTH ) + 1) + "月" +
                carlendar.get( Calendar.DAY_OF_MONTH ) + "日" +
                carlendar.get( Calendar.HOUR_OF_DAY ) + "时" +
                carlendar.get( Calendar.MINUTE ) + "分" +
                carlendar.get( Calendar.SECOND) + "秒";

        long gapDays = ( nowMills - startMills) / (1000*60*60*24) + 1;
        Toast.makeText(context,
                                nowYMD + "\n" +
                                      "周?: " + (carlendar.get( Calendar.DAY_OF_WEEK)-1) +
                                      "开学第?天: " + gapDays +
                                      "开学第?周: " + weekThFromStart

                              , Toast.LENGTH_SHORT).show();
    }

    //used to test
    private static void test() {

        long gapDays = ( nowMills - startMills) / (1000*60*60*24) + 1;
        long weekThFromStart = gapDays/7 + (gapDays%7==0?0:1);

        String nowYMD = "" +   carlendar.get( Calendar.YEAR ) + "年" +
                (carlendar.get( Calendar.MONTH ) + 1) + "月" +
                carlendar.get( Calendar.DAY_OF_MONTH ) + "日" ;

        System.out.println( nowYMD );
        System.out.println( "周?: " + (carlendar.get( Calendar.DAY_OF_WEEK ) - 1)  );
        System.out.println( "开学第?天: " + gapDays );
        System.out.println( "开学第?周: " + weekThFromStart );
    }
}


/*
    Call requires API level 26 (current min is 15): java.time.LocalDate#of...

    private static LocalDate startDate = LocalDate.of(2018, 9, 3);
    private static LocalDate nowDate = LocalDate.now();

    private void test() {

        System.out.println("startDate: " + startDate + ", nowDate: " + nowDate);

        int todayTh = (int) (nowDate.toEpochDay() - startDate.toEpochDay()) + 1;

        String month = nowDate.getMonthValue() + "月";
        String day = nowDate.getDayOfMonth() + "日";
        String dayOfWeek = "周" + (nowDate.getDayOfWeek().ordinal() + 1);
        String dayThFromStart = "开学第" + ((int) (nowDate.toEpochDay() - startDate.toEpochDay()) + 1) + "天";
        String weekThFromStart = "第" + (todayTh / 7 + (todayTh % 7 == 0 ? 0 : 1)) + "周";

        System.out.println(month + day + "," + dayThFromStart + "," + dayOfWeek + "," + weekThFromStart);
    }


*/