package com.example.demo.Dto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MeetingResearchResponseDto {
    @JsonProperty("rows")
    public ArrayList<row> meetingList;
    @JsonProperty("code")
    private int code;
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("total")
    private int total;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    private static TimeZone timeZoneUTC = TimeZone.getTimeZone("UTC");
    private static TimeZone tokyoTimeZone = TimeZone.getTimeZone("Asia/Tokyo");
    private static SimpleDateFormat timeDurringFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static SimpleDateFormat LargeFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    static {
        timeFormat.setTimeZone(timeZoneUTC);
    }

    public ArrayList<row> ReturnList() {
        return this.meetingList;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class row implements Cloneable {

        // String searchValue;
        // String createBy;
        // String createTime;
        // private String updateBy;
        // private String updateTime;
        // String remark;
        // Map params;
        // private String reservationId;
        // String equipmentId;
        // String purposeId;
        @JsonProperty("eventLength")
        int eventLength;
        // String eventPid;
        // String delFlag;
        // String sortedEquipmentId;
        // String parentReservationId;
        private String equipmentName;
        @JsonProperty("startDate")
        private String startDate;
        @JsonProperty("endDate")
        private String endDate;
        @JsonProperty("useById")
        private String useById;
        // String createById;
        @JsonProperty("text")
        private String text = "";

        // String reservationGroupId;
        @JsonProperty("recType")
        private String recType;
        @JsonProperty("purposeName")
        private String purposeName = "";

        private String subject = "";

        @Override
        public Object clone() throws CloneNotSupportedException {
            row clonedRow = (row) super.clone(); // 浅拷贝
            clonedRow.startDate = new String(this.startDate); // 深拷贝
            clonedRow.endDate = new String(this.endDate); // 深拷贝
            // 其他属性如果有需要也进行深拷贝
            return clonedRow;
        }

        public void setRecType(String recType) {
            this.recType = recType;
        }

        public String getrecType() {
            return this.recType;
        }

        public int getEventLength() {
            return this.eventLength;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public void setEventLength(int eventLength) {
            this.eventLength = eventLength;
        }

        public String getUserString() {
            String a = " ";
            switch (this.useById) {
                case "4":
                    a = "Napir";
                    break;
                case "3":
                    a = "EG   ";
                    break;
                case "5":
                    a = "meteo";
                    break;
                case "6":
                    a = "CES  ";
                    break;
                case "7":
                    a = "TES  ";
                    break;
                default:
                    a = "     ";
                    break;
            }
            return "利用者: " + a;
        }

        public String getUserId() {
            return this.useById;
        }

        public void setUseById(String useById) {
            this.useById = useById;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public String getPurposeName() {
            return this.purposeName;
        }

        public void setPurposeName(String purposeName) {
            this.purposeName = purposeName;
        }

        public String getEquipmentName() {
            return this.equipmentName;
        }

        public void setEquipmentName(String equipmentName) {
            this.equipmentName = equipmentName;
        }

        public String timeDuringDay() {
            String timeString = "";

            if (hmFormatter(this.endDate).equals("00:00")) {
                timeString = hmFormatter(this.startDate) + "~" + "24:00";
            } else {
                timeString = hmFormatter(this.startDate) + "~" + hmFormatter(this.endDate);
            }
            return timeString;
        }

        public String getEndDatehmmString() {
            String timeString = "";

            if (hmFormatter(this.endDate).equals("00:00")) {
                timeString = "24:00";
            } else {
                timeString = hmFormatter(this.endDate);
            }
            return timeString;
        }

        public String getStartDatehmmString() {
            String timeString = "";

            if (hmFormatter(this.startDate).equals("00:00")) {
                timeString = "24:00";
            } else {
                timeString = hmFormatter(this.startDate);
            }
            return timeString;
        }

        public String timeDuringWithWeekDay() {
            String timeString = "";
            if (hmFormatter(this.endDate).equals("00:00")) {
                timeString = yMDhmWeekDayFormatter(this.startDate) + "~" + "24:00";
            } else {
                timeString = yMDhmWeekDayFormatter(this.startDate) + "~" + hmFormatter(this.endDate);
            }

            return timeString;
        }

        public String timeOnlyTimeDuring() {
            String timeString = "";
            if (hmFormatter(this.endDate).equals("00:00")) {
                timeString = hmFormatter(this.startDate) + "~" + "24:00";
            } else {
                timeString = hmFormatter(this.startDate) + "~" + hmFormatter(this.endDate);
            }
            return timeString;
        }

        public String timeOnlyWeekDay() {
            return WeekDayFormatter(this.startDate);
        }

        public String timeOnlyDate() throws Exception {
            Date a = timeDurringFormat.parse(this.startDate);
            return LargeFormat.format(a);
        }

        public void setStartDate(String startDate) {
            try {
                this.startDate = startDate;
            } catch (Exception e) {
                throw e;
            }
        }

        public void setEndDate(String endDate) {
            try {
                this.endDate = endDate;
            } catch (Exception e) {
                throw e;
            }
        }

        public Date getStartDate() throws ParseException {

            timeDurringFormat.setTimeZone(timeZoneUTC);
            Date time = timeDurringFormat.parse(this.startDate);
            return time;

        }

        public Date getEndDate() throws ParseException {
            timeDurringFormat.setTimeZone(timeZoneUTC);
            Date time = timeDurringFormat.parse(this.endDate);
            return time;

        }
    }

    private static String hmFormatter(String meetingTimeString) {
        String formatterString = "";
        try {
            timeDurringFormat.setTimeZone(timeZoneUTC);
            Date time = timeDurringFormat.parse(meetingTimeString);
            timeFormat.setTimeZone(tokyoTimeZone);
            formatterString = timeFormat.format(time);
        } catch (Exception e) {
            System.out.println(e);
        }
        return formatterString;
    }

    private static String yMDhmWeekDayFormatter(String meetingTimeString) {
        String formatterString = "";
        try {
            timeDurringFormat.setTimeZone(timeZoneUTC);
            Date a = timeDurringFormat.parse(meetingTimeString);
            Calendar cal = Calendar.getInstance(timeZoneUTC);
            cal.setTime(a);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            String dayOfWeekString = "";
            switch (dayOfWeek) {
                case 2:
                    dayOfWeekString = "(月)";
                    break;
                case 3:
                    dayOfWeekString = "(火)";
                    break;
                case 4:
                    dayOfWeekString = "(水)";
                    break;
                case 5:
                    dayOfWeekString = "(木)";
                    break;
                case 6:
                    dayOfWeekString = "(金)";
                    break;
                case 7:
                    dayOfWeekString = "(土)";
                    break;
                case 1:
                    dayOfWeekString = "(日)";
                    break;
                default:
                    break;
            }
            LargeFormat.setTimeZone(tokyoTimeZone);
            timeFormat.setTimeZone(tokyoTimeZone);
            formatterString = LargeFormat.format(a) + " " + dayOfWeekString + " " +
                    timeFormat.format(a);
        } catch (Exception e) {
            System.out.println(e);
        }
        return formatterString;
    }

    private static String WeekDayFormatter(String meetingTimeString) {
        String formatterString = "";
        try {
            timeDurringFormat.setTimeZone(timeZoneUTC);
            Date a = timeDurringFormat.parse(meetingTimeString);
            Calendar cal = Calendar.getInstance(timeZoneUTC);
            cal.setTime(a);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            String dayOfWeekString = "";
            switch (dayOfWeek) {
                case 2:
                    dayOfWeekString = "月曜日";
                    break;
                case 3:
                    dayOfWeekString = "火曜日";
                    break;
                case 4:
                    dayOfWeekString = "水曜日";
                    break;
                case 5:
                    dayOfWeekString = "木曜日";
                    break;
                case 6:
                    dayOfWeekString = "金曜日";
                    break;
                case 7:
                    dayOfWeekString = "土曜日";
                    break;
                case 1:
                    dayOfWeekString = "日曜日";
                    break;
                default:
                    break;
            }
            LargeFormat.setTimeZone(tokyoTimeZone);
            timeFormat.setTimeZone(tokyoTimeZone);
            formatterString = dayOfWeekString;
        } catch (Exception e) {
            System.out.println(e);
        }
        return formatterString;
    }

}
