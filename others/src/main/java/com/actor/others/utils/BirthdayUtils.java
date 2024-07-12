package com.actor.others.utils;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.bigkoo.pickerview.utils.LunarCalendar;
import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.TimeUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * description: 计算生日相关 <br />
 * 其中使用到了 {@link LunarCalendar}, 需要自己添加依赖:
 * <pre>
 *     //https://github.com/Bigkoo/Android-PickerView 时间选择器等等等
 *     implementation 'com.contrarywind:Android-PickerView:4.1.9'
 * </pre>
 *
 * @author : ldf
 * date       : 2022/12/31/0031 on 22
 * @version 1.0
 */
public class BirthdayUtils {

    /**
     * 计算生日还有多少天
     * @param date 阳历
     * @return 生日倒数
     */
    public static long getBirthSpanByNow(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        //今年
        int year = calendar.get(Calendar.YEAR);
        //现在月份
        int month = calendar.get(Calendar.MONTH);
        //今天
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTime(date);
        //出生年份
        int birthYear = calendar.get(Calendar.YEAR);
        //出生月份
        int birthMonth = calendar.get(Calendar.MONTH);
        //出生日
        int birthDay = calendar.get(Calendar.DAY_OF_MONTH);
        //如果农历所对应的date是以前/今年
        if (birthYear <= year) {
            //如果就是今天
            if (birthMonth == month && birthDay == day) {
                return 0;
            }
            //如果生日已过
            if (birthMonth < month || (birthMonth == month && birthDay < day)) {
                year ++;
            }
            //设置成00:00:00, 否则计算可能出错
            calendar.set(year, birthMonth, birthDay, 0, 0, 0);
        } else {
            //设置成00:00:00, 否则计算可能出错
            calendar.set(birthYear, birthMonth, birthDay, 0, 0, 0);
        }
        /**
         * +1天
         * ∵不是整除, 会有余数.
         * ∴不足1天的也要算进去
         */
        return TimeUtils.getTimeSpanByNow(calendar.getTime(), TimeConstants.DAY) + 1;
    }

    /**
     * 计算下一个 农历 生日还有多少天 <br />
     * ∵农历的2月有30号, 而Date的2月没有30号, 甚至没有29号. 如果遇到这种情况, 会转换成3月x号, 造成错误. <br />
     * ∴直接传农历的年月日, 而不传Date
     * @param lunarMonth (农历生日)月, 1-12
     * @param lunarDay (农历生日)日, 1-30(农历没有31)
     * @param isLeapMonth 生日的这个月是不是闰月? 比如阳历 2001-05-04 和 2001-06-03 的农历都是 2001-04-12,
     *                    但第2个阳历是"闰四月十二", 如果你是第2个就传true, 第1个就传false
     * @return 生日倒数
     */
    public static long getBirthSpanByLunar(@IntRange(from = 1, to = 12) int lunarMonth,
                                           @IntRange(from = 1, to = 30) int lunarDay,
                                           boolean isLeapMonth) {
        //获取农历生日对应的 下一个还未到的 阳历生日
        Date date = getNextSolarBirthdayByLunar(lunarMonth, lunarDay, isLeapMonth);
        return getBirthSpanByNow(date);
    }

    /**
     * 获取阳历生日对应的 下一个还未到的 阳历生日
     * @param solarBirthday 阳历生日
     * @return 下一个还未到的 阳历生日
     */
    @NonNull
    public static Date getNextSolarBirthdayBySolar(@NonNull Date solarBirthday) {
        Calendar calendar = Calendar.getInstance();
        //今天年月日
        int year = calendar.get(Calendar.YEAR);
        int monty = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //阳历生日的 年月日
        calendar.setTime(solarBirthday);
        int birthYear = calendar.get(Calendar.YEAR);
        int birtyMonty = calendar.get(Calendar.MONTH);
        int birthDay = calendar.get(Calendar.DAY_OF_MONTH);

        //如果生日已过
        if (birtyMonty < monty || (birtyMonty == monty && birthDay < day)) {
            year ++;
        }
        calendar.set(year, birtyMonty, birthDay, 0, 0, 0);
        return calendar.getTime();
    }

    /**
     * 获取农历生日对应的 下一个还未到的 阳历生日. <br />
     * ∵农历的2月有30号, 而Date的2月没有30号, 甚至没有29号. 如果遇到这种情况, 会转换成3月x号, 造成错误. <br />
     * ∴传入的农历参数使用2个int类型
     * @param lunarMonth (农历生日)月, 1-12
     * @param lunarDay (农历生日)日, 1-30(农历没有31)
     * @param isLeapMonth 出生那年的出生月份是不是闰月? 比如阳历 2001-05-04 和 2001-06-03 的农历都是 2001-04-12,
     *                    但第 2001-06-03 是"闰四月十二", 如果你是第2个就传true, 第1个就传false
     * @return 下一个还未到的阳历生日
     */
    public static Date getNextSolarBirthdayByLunar(@IntRange(from = 1, to = 12) int lunarMonth,
                                                   @IntRange(from = 1, to = 30) int lunarDay,
                                                   boolean isLeapMonth) {
        //今天所对应的农历
        int[] lunarToday = today2Lunar();
        int lYear = lunarToday[0];
        int lMonth = lunarToday[1];
        int lDay = lunarToday[2];
        boolean isLeamM = lunarToday[3] == 1;
//        LogUtils.formatError("今天所对应的农历: %02d-%02d-%02d, isLeapMonth=%b", lYear, lMonth, lDay, isLeamM);
        //如果生日已过
        if (lunarMonth < lMonth || (lunarMonth == lMonth && lunarDay < lDay)) {
            lYear ++;
        }
        //将农历日期转换为阳历日期
        int[] ints = LunarCalendar.lunarToSolar(lYear, lunarMonth, lunarDay, isLeapMonth);
        Calendar calendar = Calendar.getInstance();
        calendar.set(ints[0], ints[1] - 1, ints[2]);
        return calendar.getTime();
    }

    /**
     * 根据阳历生日, 返回岁数
     * @param solarDate 阳历生日
     * @return 岁数
     */
    public static int getAgeBySolar(@NonNull Date solarDate) {
        Calendar calendar = Calendar.getInstance();
        //当前年月日
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //生日时间
        calendar.setTime(solarDate);
        int age = year - calendar.get(Calendar.YEAR);
        int birthMmonth = month - calendar.get(Calendar.MONTH);
        int birthDay = day - calendar.get(Calendar.DAY_OF_MONTH);
        //如果现在还没到出生月份 or 就是本月,但是天数没到
        if (birthMmonth < 0 || (birthMmonth == 0 && birthDay < 0)) {
            age --;
        }
        return age;
    }

    /**
     * 根据农历生日, 返回岁数. <br />
     * ∵农历的2月有30号, 而Date的2月没有30号, 甚至没有29号. 如果遇到这种情况, 会转换成3月x号, 造成错误. <br />
     * ∴直接传农历的年月日
     * @param lunarYear (农历生日)年, 1900-2099
     * @param lunarMonth (农历生日)月, 1-12
     * @param lunarDay (农历生日)日, 1-30(农历没有31)
     * @param isLeapMonth 出生那年的出生月份是不是闰月? 比如阳历 2001-05-04 和 2001-06-03 的农历都是 2001-04-12,
     *                    但第 2001-06-03 是"闰四月十二", 如果你是第2个就传true, 第1个就传false
     * @return 岁数
     */
    public static int getAgeByLunar(@IntRange(from = LunarCalendar.MIN_YEAR, to = LunarCalendar.MAX_YEAR) int lunarYear,
                                    @IntRange(from = 1, to = 12) int lunarMonth,
                                    @IntRange(from = 1, to = 30) int lunarDay,
                                    boolean isLeapMonth) {
        //今天所对应的农历
        int[] lunarToday = today2Lunar();
        int lunarYeayToday = lunarToday[0];
        int lunarMonthToday = lunarToday[1];
        int lunarDayToday = lunarToday[2];
        boolean isLeapMonthToday = lunarToday[3] == 1;
        int age = lunarYeayToday - lunarYear;
        int birthMmonth = lunarMonthToday - lunarMonth;
        int birthDay = lunarDayToday - lunarDay;
        //如果现在还没到出生月份 or 就是本月,但是天数没到
        if (birthMmonth < 0 || (birthMmonth == 0 && birthDay < 0)) {
            age --;
        } else if (birthMmonth == 0 && birthDay == 0 && isLeapMonth && !isLeapMonthToday) {
            //如果年月日都对上了, 但是出生是闰月, 而今天不是闰月
            //今年的闰月
            int leapMonth = LunarCalendar.leapMonth(lunarYeayToday);
            //如果这个月还有1个闰月
            if (leapMonth == lunarMonthToday) {
                age --;
            }
        }
        return age;
    }

    /**
     * 返回今天所对应的农历
     * @return int[0] 农历年 <br />
     *         int[1] 农历月(1-12) <br />
     *         int[2] 农历日(1-30, 农历没有31) <br />
     *         int[3] 今天所对应的农历月份是不是闰月, 1是, 0否
     */
    public static int[] today2Lunar() {
        Calendar calendar = Calendar.getInstance();
        //当前年月日
        int year = calendar.get(Calendar.YEAR);
        int monty = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //今天所对应的农历
        return LunarCalendar.solarToLunar(year, monty + 1, day);
    }
}
