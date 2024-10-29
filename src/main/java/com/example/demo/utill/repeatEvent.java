package com.example.demo.utill;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class repeatEvent {

    public static List<String> calculateRepeatMeetingTime(String recType, Date startDate, Date endDate, int eventLength)
            throws ParseException {
        SimpleDateFormat timeDurringFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        timeDurringFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar startCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        startCalendar.setTime(startDate);
        Calendar endCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        endCalendar.setTime(endDate);
        List<String> repeatMeetingTime = new ArrayList<>();

        String[] ruleAllStrings = recType.split("#")[0].split("_");
        String ruleType = ruleAllStrings[0];
        int count = Integer.parseInt(ruleAllStrings[1]);
        for (; startCalendar.before(endCalendar);) {
            switch (ruleType) {
                case "week":
                    weeklySchedule(startCalendar, endCalendar, count, ruleAllStrings[4],
                            eventLength, repeatMeetingTime);
                    startCalendar.add(Calendar.WEEK_OF_YEAR, count);
                    break;
                case "month":
                    if (ruleAllStrings.length > 3) {
                        monthlySchedule(startCalendar, endCalendar, count, ruleAllStrings[2], ruleAllStrings[3],
                                eventLength, repeatMeetingTime);
                    } else {
                        monthlySchedule(startCalendar, endCalendar, count, "", "",
                                eventLength, repeatMeetingTime);
                    }

                    startCalendar.add(Calendar.MONTH, count);
                    break;
                case "year":
                    timeDurringFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    String startime = timeDurringFormat.format(startCalendar.getTime());
                    String endtime = timeDurringFormat.format(endCalendar.getTime());
                    repeatMeetingTime.add(startime + "," + endtime);
                    startCalendar.add(Calendar.YEAR, count);
                    break;
                default:
                System.out.println("1");
                    throw new IllegalArgumentException(ruleType);
            }
        }

        return repeatMeetingTime;
    }

    public static List<String> weeklySchedule(Calendar startDate, Calendar endDate,
            int count, String weekcountString, int eventLength, List<String> repeatMeetingTime) {
        String[] weekcount = weekcountString.split(",");
        Calendar nowtime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat timeDurringFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        timeDurringFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        for (String element : weekcount) {
            int dayOfWeek = Integer.parseInt(element);

            startDate.set(Calendar.DAY_OF_WEEK, dayOfWeek + 1);

            Calendar endTime = (Calendar) startDate.clone();
            endTime.add(Calendar.SECOND, eventLength);
            if (startDate.before(nowtime)) {
                if (nowtime.after(endTime) && nowtime.after(startDate)) {
                    continue;
                } else {
                    String startDateString = timeDurringFormat.format(startDate.getTime());
                    String endDateString = timeDurringFormat.format(endTime.getTime());
                    repeatMeetingTime.add(startDateString + "," + endDateString);
                    continue;
                }
            }

            String startDateString = timeDurringFormat.format(startDate.getTime());
            String endDateString = timeDurringFormat.format(endTime.getTime());
            repeatMeetingTime.add(startDateString + "," + endDateString);
        }
        return repeatMeetingTime;
    }

    public static List<String> monthlySchedule(Calendar startDate, Calendar endDate,
            int count, String weekDayString, String weekDayDuringIntString, int eventLength,
            List<String> repeatMeetingTime) {
        String[] weekcount = weekDayString.split(",");
        SimpleDateFormat timeDurringFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        timeDurringFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar nowtime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        if (weekDayString.equals("") && weekDayDuringIntString.equals("")) {
            if (startDate.before(nowtime)) {
                return repeatMeetingTime;
            }
            Calendar endTime = (Calendar) startDate.clone();
            String startDateString = timeDurringFormat.format(startDate.getTime());
            endTime.add(Calendar.SECOND, eventLength);
            String endDateString = timeDurringFormat.format(endTime.getTime());
            repeatMeetingTime.add(startDateString + "," + endDateString);

        } else {
            int weekDayDuringInt = Integer.parseInt(weekDayDuringIntString);
            startDate.set(Calendar.WEEK_OF_MONTH, weekDayDuringInt);

            for (String element : weekcount) {
                int dayOfWeek = Integer.parseInt(element);

                startDate.set(Calendar.DAY_OF_WEEK, dayOfWeek + 1);

                Calendar endTime = (Calendar) startDate.clone();
                endTime.add(Calendar.SECOND, eventLength);
                if (startDate.before(nowtime)) {
                    if (nowtime.after(endTime) && nowtime.after(startDate)) {
                        continue;
                    } else {
                        String startDateString = timeDurringFormat.format(startDate.getTime());
                        String endDateString = timeDurringFormat.format(endTime.getTime());
                        repeatMeetingTime.add(startDateString + "," + endDateString);
                        continue;
                    }
                }

                String startDateString = timeDurringFormat.format(startDate.getTime());
                String endDateString = timeDurringFormat.format(endTime.getTime());
                repeatMeetingTime.add(startDateString + "," + endDateString);
            }
        }
        return repeatMeetingTime;
    }
}
