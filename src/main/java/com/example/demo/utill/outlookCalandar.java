package com.example.demo.utill;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class outlookCalandar {

    private String[] selectDate() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return new String[] { yesterday.format(formatter), today.format(formatter) };
    }

    private List<Event> getUserCalendar(String userId, String[] dateRange, String accessToken) throws IOException {
        String startDateTime = dateRange[0];
        String endDateTime = dateRange[1];
        String url = "https://graph.microsoft.com/v1.0/users/" + userId + "/calendarView?" +
                "startDateTime=" + startDateTime + "&endDateTime=" + endDateTime +
                "&$select=organizer,subject,start,end,location" +
                "&$orderby=start/dateTime";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        CalendarResponse calendarResponse = objectMapper.readValue(response, CalendarResponse.class);
        return calendarResponse.getValue();
    }
    
    private void postUserCalendar(String groupName, String userName, List<Event> events) {
        System.out.println(groupName + " : " + userName);
        for (Event event : events) {
            System.out.println("-----------------");
            System.out.println("件名　　 : " + event.getSubject());
            System.out.println("場所　　 : " + event.getLocation().getDisplayName());
            System.out.println("主催者　 : " + event.getOrganizer().getEmailAddress().getName());
            System.out.println("開始時刻 : " + event.getStart().getDateTime().replace('T', ' ').replace(":00.0000000", ""));
            System.out.println("終了時刻 : " + event.getEnd().getDateTime().replace("T", " ").replace(":00.0000000", ""));
            System.out.println("日時 : " + event.getStart().getDateTime().substring(0, 10) + "  " +
                    event.getStart().getDateTime().substring(11, 16) + "-" +
                    event.getEnd().getDateTime().substring(11, 16));
        }
    }

    public void getUserCalendar() throws IOException {
        String accessToken = getAzureAccessToken();
        GroupInfo groupInfo = getUserFromGroup(accessToken);
        List<Group> groups = groupInfo.getGroupIds();
        List<GroupMember> groupMembers = groupInfo.getGroupMembers();
        String[] dateRange = selectDate();
        for (Group group : groups) {
            for (GroupMember member : groupMembers) {
                String userId = member.getId();
                String userName = member.getDisplayName();
                List<Event> events = getUserCalendar(userId, dateRange, accessToken);
                if (!events.isEmpty()) {
                    postUserCalendar(group.getId(), userName, events);
                }
            }
        }
    }

    private String getAzureAccessToken() {
        // 实现获取 Azure Access Token 的方法
        return "sample_access_token";
    }

    private GroupInfo getUserFromGroup(String accessToken) {
        // 实现获取 Group 成员信息的方法
        return new GroupInfo();
    }

}

class GroupInfo {
    private List<Group> groupIds;
    private List<GroupMember> groupMembers;

    // Getter and Setter methods
    public List<Group> getGroupIds() {
        return groupIds;
    }

    public List<GroupMember> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupIds(List<Group> groupIds) {
        this.groupIds = groupIds;
    }

    public void setGroupMembers(List<GroupMember> groupMembers) {
        this.groupMembers = groupMembers;
    }
}

class Group {
    @JsonProperty("group_id")
    private String id;

    // Getter and Setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

class GroupMember {
    private String displayName;
    private String id;

    // Getter and Setter methods
    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }
}

class CalendarResponse {
    private List<Event> value;

    // Getter and Setter methods
    public void setValue(List<Event> value) {
        this.value = value;
    }

    public List<Event> getValue() {
        return value;
    }
}

class Event {
    private String subject;
    private Location location;
    private Organizer organizer;
    private DateTime start;
    private DateTime end;

    // Getter and Setter methods
    public DateTime getEnd() {
        return end;
    }

    public Location getLocation() {
        return location;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public DateTime getStart() {
        return start;
    }

    public String getSubject() {
        return subject;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}

class Location {
    @JsonProperty("display_name")
    private String displayName;

    // Getter and Setter methods
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}

class Organizer {
    @JsonProperty("email_address")
    private EmailAddress emailAddress;

    // Getter and Setter methods
    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(EmailAddress emailAddress) {
        this.emailAddress = emailAddress;
    }
}

class EmailAddress {
    private String name;

    // Getter and Setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class DateTime {
    @JsonProperty("date_time")
    private String dateTime;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    // Getter and Setter methods
}
