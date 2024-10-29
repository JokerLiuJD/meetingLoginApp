package com.example.demo.Dto;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class outlookCalendarSet_respose {
    @JsonProperty("meetingList")
    ArrayList<OutlookCalendarDto> meetingList = new ArrayList<>();
    @JsonProperty("company")
    String company = "";
    @JsonProperty("roomID")
    String roomID = "";
    @JsonProperty("templeteMode")
    String templeteMode = "";
    @JsonProperty("logo-img-base64")
    String logo_img_base64 = "";

    @JsonProperty("macID")
    String macID = "";

    public String getMacID() {
        return macID;
    }

    public String getCompany() {
        return company;
    }

    public String getLogo_img_base64() {
        return logo_img_base64;
    }

    public ArrayList<OutlookCalendarDto> getMeetingList() {
        return meetingList;
    }

    public String getRoomID() {
        return roomID;
    }

    public String getTempleteMode() {
        return templeteMode;
    }

    public static class OutlookCalendarDto {
        private static SimpleDateFormat timeDurringFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        @JsonProperty("subject")
        String subject;
        @JsonProperty("start")
        String start;
        @JsonProperty("end")
        String end;
        @JsonProperty("organizer")
        String organizer;

        public void setEnd(String end) {
            this.end = end;
        }

        public String getOrganizer() {
            return organizer;
        }

        public String getEnd() {
            LocalDateTime utcDateTime = LocalDateTime.parse(this.end, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Date endDate = Date.from(utcDateTime.atZone(ZoneId.systemDefault()).toInstant());
            String endDateString = timeDurringFormat.format(endDate);

            return endDateString;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getStart() {
            LocalDateTime utcDateTime = LocalDateTime.parse(this.start, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Date startDate = Date.from(utcDateTime.atZone(ZoneId.systemDefault()).toInstant());
            String startDateString = timeDurringFormat.format(startDate);
            return startDateString;
        }

        public void setSubject(String subject) {

            this.subject = subject;
        }

        public String getSubject() {
            return subject;
        }

        public int getEventLength() {
            LocalDateTime utcStartDateTime = LocalDateTime.parse(this.start, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Date startDate = Date.from(utcStartDateTime.atZone(ZoneId.systemDefault()).toInstant());
            LocalDateTime utcEndDateTime = LocalDateTime.parse(this.end, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Date endDate = Date.from(utcEndDateTime.atZone(ZoneId.systemDefault()).toInstant());
            long millisecondsDiff = endDate.getTime() - startDate.getTime();
            int secondsDiff = (int) millisecondsDiff / 1000;
            return secondsDiff;
        }
    }

}