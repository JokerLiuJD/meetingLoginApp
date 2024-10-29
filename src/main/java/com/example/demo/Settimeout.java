package com.example.demo;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.*;
import java.util.Base64;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.example.demo.Dto.MeetingResearchResponseDto;
import com.example.demo.Dto.MeetingResearchResponseDto.row;
import com.example.demo.Dto.TokenDto;
import com.example.demo.Dto.outlookCalendarSet_respose;
import com.example.demo.Dto.outlookCalendarSet_respose.OutlookCalendarDto;
import com.example.demo.utill.repeatEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class Settimeout {
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();
    private SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
    private static Calendar oneMonthLater;
    private static TimeZone timeZoneUTC = TimeZone.getTimeZone("UTC");
    private static ArrayList<machineObject> machineList = new ArrayList<>();
    private static String lastmachineListJson = new String();
    private static Date oneMonthLaterDate;
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 5000;
    static {
        //
        try {

            // 机器6-A（竖板）
            machineObject machineObject6_A = new machineObject();
            machineObject6_A.settname("6-A");
            machineObject6_A.setmacId("D4:3D:39:1C:F3:24");
            machineList.add(machineObject6_A);
           // 机器7-A（竖板）
            machineObject machineObject7_A = new machineObject();
            machineObject7_A.settname("7-A");
            machineObject7_A.setmacId("D4:3D:39:1C:E5:9C");
            machineObject7_A.setLogo_img_base64("none");
            machineList.add(machineObject7_A);
            // 机器7-B（竖板）
            machineObject machineObject7_B = new machineObject();
            machineObject7_B.settname("7-B");
            machineObject7_B.setmacId("D4:3D:39:1C:CC:CC");
            machineObject7_B.setLogo_img_base64("none");
            machineList.add(machineObject7_B);

            // 机器8-A（竖板）
            machineObject machineObject8_A = new machineObject();
            machineObject8_A.settname("8-A");
            machineObject8_A.setmacId("D4:3D:39:1C:87:24");
            machineObject8_A.setLogo_img_base64("none");
            machineList.add(machineObject8_A);
            // 机器8-B（竖板）
            machineObject machineObject8_B = new machineObject();
            machineObject8_B.settname("8-B");
            machineObject8_B.setmacId("D4:3D:39:1C:7C:D8");
            machineObject8_B.setLogo_img_base64("none");
            machineList.add(machineObject8_B);

            // 机器8-C（竖板）
            machineObject machineObject8_C = new machineObject();
            machineObject8_C.settname("8-C");
            machineObject8_C.setmacId("D4:3D:39:1C:70:70");
            machineObject8_C.setLogo_img_base64("none");
            machineList.add(machineObject8_C);
            // 机器8_D（竖板）
            machineObject machineObject8_D = new machineObject();
            machineObject8_D.settname("8_D");
            machineObject8_D.setmacId("D4:3D:39:1C:79:E8");
            machineObject8_D.setLogo_img_base64("none");
            machineList.add(machineObject8_D);
            // 机器8_E（竖板）
            machineObject machineObject8_E = new machineObject();
            machineObject8_E.settname("8_E");
            machineObject8_E.setmacId("D4:3D:39:1C:61:DA");
            machineObject8_E.setLogo_img_base64("none");
            machineList.add(machineObject8_E);
            
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Scheduled(fixedRate = 1000 * 60 * 5)
    private void updateImg() {
        try {
            // 获取一个月后的UTC时间和当前UTC时间
            oneMonthLater = Calendar.getInstance(timeZoneUTC);
            oneMonthLater.add(Calendar.MONTH, 1);
            Calendar nowCalendar = Calendar.getInstance(timeZoneUTC);
            nowCalendar.set(Calendar.HOUR_OF_DAY, 0);
            nowCalendar.set(Calendar.MINUTE, 0);
            nowCalendar.set(Calendar.SECOND, 0);
            nowCalendar.set(Calendar.MILLISECOND, 0);
            Date nowtime = nowCalendar.getTime();
            oneMonthLaterDate = oneMonthLater.getTime();
            String oneMonthLaterString = currentDateFormat.format(oneMonthLaterDate);
            String todayStartString = currentDateFormat.format(nowtime);

            // 获取会议列表
            ArrayList<row> list_meetingList = getMeetingList_Formatter(todayStartString,
                    oneMonthLaterString, nowtime);

            getMeetingList_ChangeFlag(list_meetingList);
            changeCheck_update();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private String apiTokenGet() {
        String token = "";

        try {
            Gson gson = new Gson();
            JsonObject jsonBody = new JsonObject();
            jsonBody.addProperty("username", "20231127001");
            jsonBody.addProperty("password", "123456");
            RequestBody Loginbody = RequestBody.create(mediaType, gson.toJson(jsonBody));
            Request loginRequest = new Request.Builder()
                    .url("http://192.144.234.153:4000/user/api/login")
                    .method("POST", Loginbody)
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response loginResponse = okHttpClient.newCall(loginRequest).execute();
            try {

                String loginResponseBody = loginResponse.body().string();
                ObjectMapper mapper = new ObjectMapper();
                TokenDto tokenDto = mapper.readValue(loginResponseBody, TokenDto.class);

                token = tokenDto.getData().getToken();

            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (loginResponse != null && loginResponse.body() != null) {
                    loginResponse.body().close();
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return token;
    }

    private void changeCheck_update() {
        String tokenString = apiTokenGet();
        for (int i = 0; i < machineList.size(); i++) {
            if (machineList.get(i).getchangeFlag()) {
                try {
                    updataImg(tokenString, machineList.get(i));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

    }

    @SuppressWarnings("unchecked")
    private void getMeetingList_ChangeFlag(ArrayList<row> list_meetingList)
            throws JsonProcessingException, ParseException {
        // 遍历设备列表
        ObjectMapper mapper = new ObjectMapper();
        Calendar nowCalendar = Calendar.getInstance(timeZoneUTC);
        Date nowtime = nowCalendar.getTime();
        machineList.forEach(machineObj -> {
            machineObj.getmeetingList().clear();
            String tname = machineObj.gettname();
            list_meetingList.forEach(row -> {
                try {
                    if (tname.equals(row.getEquipmentName()) && row.getEndDate().after(nowtime)) {
                        machineObj.getmeetingList().add(row);

                    }
                } catch (ParseException e) {

                    System.out.println(e);
                }
            });
        });

        // // 根据最后一次的设备列表和当前设备列表判断设备状态是否发生变化

        if (!lastmachineListJson.equals("")) {
            ArrayList<machineObject> machineList_old = mapper.readValue(lastmachineListJson, ArrayList.class);
            for (int i = 0; i < machineList.size(); i++) {
                if (machineList.get(i).getmeetingList().size() > 0) {
                    row firstMeeting = machineList.get(i).getmeetingList().get(0);
                    if (firstMeeting.getEndDate().after(nowtime) && firstMeeting.getStartDate().before(nowtime)
                            && firstMeeting.getEventLength() != 86400) {
                        machineList.get(i).setMeetingStates("利用中");
                    } else {
                        machineList.get(i).setMeetingStates("空室");
                    }
                } else {
                    machineList.get(i).setMeetingStates("空室");
                }

            }

            for (int i = 0; i < machineList.size(); i++) {
                String machineObj_new = mapper.writeValueAsString(machineList.get(i));
                for (int j = 0; j < machineList_old.size(); j++) {
                    String machineObj_old = mapper.writeValueAsString(machineList_old.get(i));
                    if (machineObj_old.equals(machineObj_new)) {
                        machineList.get(i).setchangeFlag(false);
                    } else {
                        machineList.get(i).setchangeFlag(true);
                    }
                }
            }

        } else {
            for (int i = 0; i < machineList.size(); i++) {
                machineList.get(i).setchangeFlag(true);
            }
        }

        try {

            lastmachineListJson = mapper.writeValueAsString(machineList);

        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
    }

    private ArrayList<row> getMeetingList_Formatter(String todayStartString, String oneMonthLaterString,
            Date nowtime) {
        Response meetingRequestresponse = null;
        ArrayList<row> list_meetingList = new ArrayList<>();

        try {
            HttpUrl.Builder baseUrlBuilder = HttpUrl
                    .parse("https://booking.ene-cloud.com/prod-api/ers/reservation/displayed").newBuilder();
            // Params
            baseUrlBuilder.addQueryParameter("params[pageNum]", "1");
            baseUrlBuilder.addQueryParameter("params[pageSize]", "0");
            baseUrlBuilder.addQueryParameter("params[equipmentIds]", "0");
            baseUrlBuilder.addQueryParameter("params[useByIds]", "");
            baseUrlBuilder.addQueryParameter("params[createByIds]", "");
            baseUrlBuilder.addQueryParameter("params[purposeIds]", "");
            baseUrlBuilder.addQueryParameter("params[searchTermRange]", todayStartString
                    + "," + oneMonthLaterString);
            baseUrlBuilder.addQueryParameter("params[tenantId]", "4");
            baseUrlBuilder.addQueryParameter("params[mode]", "timeline");
            String url = baseUrlBuilder.build().toString();
            Request meetingRequest = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            meetingRequestresponse = okHttpClient.newCall(meetingRequest).execute();
            String meetingResearchResponseBody = meetingRequestresponse.body().string();

            if (meetingResearchResponseBody != null && !meetingResearchResponseBody.equals("")) {
                ObjectMapper mapper = new ObjectMapper();
                MeetingResearchResponseDto mResearchReqponse = mapper.readValue(meetingResearchResponseBody,
                        MeetingResearchResponseDto.class);
                if (mResearchReqponse.getCode() != 200) {
                    System.out.println("会议列表请求失败:" + mResearchReqponse.getMsg());
                }
                ArrayList<row> rowsArray = mResearchReqponse.ReturnList();
                // 振り返し会議処理
                for (int i = 0; i < rowsArray.size(); i++) {
                    row row = rowsArray.get(i);
                    Date startDate = row.getStartDate();
                    Date endDate = row.getEndDate();

                    String recType = row.getrecType();

                    if (!recType.equals("")) {
                        if (endDate.after(oneMonthLaterDate)) {
                            endDate = oneMonthLaterDate;
                        }
                        int meetingLength = row.getEventLength();
                        List<String> repeatMeetingTime = repeatEvent.calculateRepeatMeetingTime(recType, startDate,
                                endDate, meetingLength);

                        for (String time : repeatMeetingTime) {
                            String[] startEndTimeArray = time.split(",");
                            row newrow = row;
                            String machineObj_old = mapper.writeValueAsString(newrow);
                            row newrows = mapper.readValue(machineObj_old, row.class);

                            newrows.setStartDate(startEndTimeArray[0]);
                            newrows.setEndDate(startEndTimeArray[1]);

                            list_meetingList.add(newrows);
                        }

                    } else {
                        list_meetingList.add(row);
                    }

                }

                // @RequestBody ArrayList<OutlookCalendarDto> outlookCalendarList
                // meetingList6b_mc.clear();

                HashMap<String, outlookCalendarSet_respose> outlookCalendarList = outlookCalendarSet
                        .outlookCalendarList_get();
                for (String mappingKey : outlookCalendarList.keySet()) {
                    outlookCalendarSet_respose outlookCalendarList_obj = outlookCalendarList.get(mappingKey);
                    if (outlookCalendarList_obj.getTempleteMode() != null
                            || outlookCalendarList_obj.getTempleteMode().isEmpty()) {
                        for (machineObject machineObject : machineList) {
                            if (machineObject.getmacId().equals(outlookCalendarList_obj.getMacID())) {
                                // 展示模版样式 0为支持outlook和booking,1为仅支持outlook。
                                machineObject.setTempleteMode(outlookCalendarList_obj.getTempleteMode());

                                // Logo图片
                                if (!outlookCalendarList_obj.getLogo_img_base64().equals("")
                                        && !outlookCalendarList_obj.getLogo_img_base64().equals("none")) {
                                    machineObject.setLogo_img_base64(outlookCalendarList_obj.getLogo_img_base64());
                                } else {
                                    machineObject.setLogo_img_base64("");
                                }
                            }
                        }
                    }

                    for (int a = 0; a < outlookCalendarList_obj.getMeetingList().size(); a++) {
                        OutlookCalendarDto OutlookCalendarObj = outlookCalendarList_obj.getMeetingList().get(a);
                        row meetingList_obj = new row();
                        meetingList_obj.setStartDate(OutlookCalendarObj.getStart());
                        meetingList_obj.setEndDate(OutlookCalendarObj.getEnd());
                        meetingList_obj.setEventLength(OutlookCalendarObj.getEventLength());
                        meetingList_obj.setEquipmentName(outlookCalendarList_obj.getRoomID());
                        meetingList_obj.setSubject(OutlookCalendarObj.getSubject());
                        meetingList_obj.setText(OutlookCalendarObj.getOrganizer());
                        meetingList_obj.setRecType("");
                        meetingList_obj.setUseById("");
                        list_meetingList.add(meetingList_obj);
                    }
                }

                Collections.sort(list_meetingList, new Comparator<row>() {
                    @Override
                    public int compare(row r1, row r2) {
                        try {
                            // 比较结束日期
                            int endDateComparison = r1.getEndDate().compareTo(r2.getEndDate());
                            if (endDateComparison != 0) {
                                if (r1.getEndDate().before(r2.getEndDate())) {
                                    if (r1.getEndDatehmmString().equals("24:00") && r1.getEventLength() == 86400) {
                                        return 1;
                                    }
                                    return -1;
                                } else {
                                    if (r1.getStartDate().before(r2.getStartDate())) {
                                        return -1;
                                    }
                                    return 1;
                                }
                            }
                            // 结束日期相同时比较开始日期
                            int startDateComparison = r1.getStartDate().compareTo(r2.getStartDate());
                            return startDateComparison;
                        } catch (Exception e) {
                            System.out.println(e);
                            return 0; // 出现异常时返回 0
                        }
                    }
                });

            }

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (meetingRequestresponse != null && meetingRequestresponse.body() != null) {
                meetingRequestresponse.body().close();
            }
        }
        return list_meetingList;
    }

    private String roomString(String roomString) {
        String a = " ";
        if (roomString.length() > 3) {
            a = roomString.replace("-", "階 ") + "室";
        } else {
            a = " " + roomString.replace("-", "階 ") + "室";
        }
        return a;
    }

    private void updataImg(String token, machineObject machineObj) throws Exception {
        String macId = machineObj.getmacId();
        String roomName = roomString(machineObj.gettname());
        String lastDateString = " ";
        Gson gson = new Gson();
        String svgFilePath_blank;
        String svgFilePath_bussy;
        // 展示模板样式 "1"为仅支持outlook预约, "0"为booking和outlook都支持
        if (machineObj.getTempleteMode().equals("1")) {
            svgFilePath_blank = Paths.get("static", "blankOutlookOnly.svg").toString();
            svgFilePath_bussy = Paths.get("static", "bussyOutlookOnly.svg").toString();
        } else {
            svgFilePath_blank = Paths.get("static", "blank.svg").toString();
            svgFilePath_bussy = Paths.get("static", "bussy.svg").toString();
        }

        String pngFilePath = Paths.get("static", "output.png").toString();
        // 读取SVG文件
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);

        ArrayList<row> meetingArray = machineObj.getmeetingList();
        // String tidString = tid;
        Calendar nowCalendar = Calendar.getInstance(timeZoneUTC);
        Date nowtime = nowCalendar.getTime();
        Document document = factory.createDocument("file:" + svgFilePath_blank);

        if (meetingArray.size() > 0) {
            row firstMeeting = machineObj.getmeetingList().get(0);
            try {

                if (firstMeeting.getEndDate().after(nowtime) && firstMeeting.getStartDate().before(nowtime)
                        && firstMeeting.getEventLength() != 86400) {
                    document = factory.createDocument("file:" + svgFilePath_bussy);

                    document.getElementById("roomName").setTextContent(roomName);
                    int baseDy = 118;
                    int maxHeight = 810;
                    if (machineObj.getTempleteMode().equals("1")) {

                    }

                    int ListDy = 0;
                    int itemHeight = 0;

                    Element screen_Element = document.getElementById("screen");
                    for (int index = 0; index < meetingArray.size(); index++) {
                        if (baseDy + ListDy + itemHeight > maxHeight) {
                            break;
                        }
                        String meetingPurposeName_String = meetingArray.get(index).getPurposeName();
                        String meetingTexString = "";
                        String subjecString = meetingArray.get(index).getSubject();
                        String timeOnlyDate = meetingArray.get(index).timeOnlyDate();
                        String timeOnlyWeekDay = meetingArray.get(index).timeOnlyWeekDay();
                        String[] timeOnlyTimeDuring_Array = meetingArray.get(index).timeOnlyTimeDuring().split("~");
                        String meetingListString = "";

                        Element timettext = document.createElementNS("http://www.w3.org/2000/svg", "text");
                        Element timetspan_start = document.createElementNS("http://www.w3.org/2000/svg", "tspan");
                        timetspan_start.setAttribute("x", "28");
                        timetspan_start.setAttribute("dy", "22");
                        timetspan_start.setTextContent(timeOnlyTimeDuring_Array[0]);
                        Element timetspan_end = document.createElementNS("http://www.w3.org/2000/svg", "tspan");
                        timetspan_end.setAttribute("x", "28");
                        timetspan_end.setAttribute("dy", "22");
                        timetspan_end.setTextContent(timeOnlyTimeDuring_Array[1]);
                        timettext.appendChild(timetspan_start);
                        timettext.appendChild(timetspan_end);
                        Element meeting_text = document.createElementNS("http://www.w3.org/2000/svg",
                                "text");
                        meeting_text.setAttribute("x", "192");
                        meeting_text.setAttribute("dy", "26");
                        Element moderator = document.createElementNS("http://www.w3.org/2000/svg", "text");
                        moderator.setAttribute("text-anchor", "end");
                        moderator.setAttribute("x", "600");
                        moderator.setAttribute("dy", "24");

                        // 根据outlook和预约系统的数据结构，对meetingText进行统一处理
                        if (!subjecString.equals("")) {
                            meetingListString = subjecString;
                            String text = meetingArray.get(index).getText();
                            int length = meetingArray.get(index).getText().length();
                            if (meetingArray.get(index).getText().length() > 4) {
                                moderator.setAttribute("dy", "24");
                                moderator.setAttribute("font-size", "12");
                                if (length % 2 == 0) {

                                    Element tspanElement_up = document.createElementNS("http://www.w3.org/2000/svg",
                                            "tspan");
                                    tspanElement_up.setTextContent(text.substring(0, length / 2));
                                    tspanElement_up.setAttribute("dy", "12");
                                    tspanElement_up.setAttribute("x", "600");
                                    Element tspanElement = document.createElementNS("http://www.w3.org/2000/svg",
                                            "tspan");
                                    tspanElement.setAttribute("x", "600");
                                    tspanElement.setAttribute("dy", "12");
                                    tspanElement.setTextContent(text.substring(length / 2, length));
                                    moderator.appendChild(tspanElement_up);
                                    moderator.appendChild(tspanElement);

                                } else {
                                    Element tspanElement_up = document.createElementNS("http://www.w3.org/2000/svg",
                                            "tspan");
                                    tspanElement_up.setAttribute("dy", "12");
                                    tspanElement_up.setAttribute("x", "600");
                                    Element tspanElement = document.createElementNS("http://www.w3.org/2000/svg",
                                            "tspan");
                                    tspanElement.setAttribute("x", "600");
                                    tspanElement.setAttribute("dy", "12");

                                    tspanElement_up.setTextContent(text.substring(0, (length - 1) / 2 + 1));
                                    moderator.appendChild(tspanElement_up);
                                    tspanElement.setTextContent(text.substring((length - 1) / 2 + 1, length));
                                    moderator.appendChild(tspanElement);
                                }

                            } else {
                                moderator.setTextContent(meetingArray.get(index).getText());
                            }
                        } else {
                            meetingTexString = meetingArray.get(index).getText();
                            meetingListString = meetingTexString + "(" + meetingPurposeName_String + ")";
                        }
                        // 不同天的会议要新加一个表头
                        if (lastDateString.equals("") || !lastDateString.equals(timeOnlyDate)) {
                            ListDy += 42;
                            if (itemHeight != 0) {
                                ListDy += itemHeight;
                            }
                            itemHeight = 0;
                            int transformY = baseDy + ListDy;
                            Element group_header = document.createElementNS("http://www.w3.org/2000/svg", "g");
                            group_header.setAttribute("font-size", "16");
                            group_header.setAttribute("transform", "translate(5," + String.valueOf(transformY) + ")");

                            // 创建白色文字Text,内容设置为日期
                            Element rect_text = document.createElementNS("http://www.w3.org/2000/svg", "text");
                            rect_text.setAttribute("dx", "170");
                            rect_text.setAttribute("font-family", "Noto Sans JP");
                            rect_text.setAttribute("font-size", "24");
                            rect_text.setAttribute("dy", "24");
                            rect_text.setAttribute("id", "text-item");
                            rect_text.setAttribute("stroke-width", "0.2");
                            rect_text.setAttribute("stroke", "#000000");
                            rect_text.setAttribute("fill", "#FFFFFF");
                            rect_text.setTextContent(timeOnlyDate);
                            // 创建&lt;tspan&gt;元素，将星期插入到白色文本Text里
                            Element tspanElement = document.createElementNS("http://www.w3.org/2000/svg", "tspan");
                            tspanElement.setAttribute("dx", "50");
                            tspanElement.setTextContent(timeOnlyWeekDay);
                            // 将&lt;tspan&gt;元素添加到&lt;text&gt;元素中
                            rect_text.appendChild(tspanElement);
                            // 创建和设置黑框
                            Element rect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
                            rect.setAttribute("x", "0");
                            rect.setAttribute("fill", "#000000");
                            rect.setAttribute("height", "28");
                            rect.setAttribute("width", "640");
                            group_header.appendChild(rect);
                            group_header.appendChild(rect_text);
                            screen_Element.appendChild(group_header);
                            Element group_meeting_item = document.createElementNS("http://www.w3.org/2000/svg", "g");
                            group_meeting_item.setAttribute("dy", "26");
                            group_meeting_item.setAttribute("text-anchor", "start");
                            group_meeting_item.setAttribute("font-family", "Noto Sans JP");
                            group_meeting_item.setAttribute("id", "meetingList");
                            group_meeting_item.setAttribute("stroke-width", "0.2");
                            group_meeting_item.setAttribute("stroke", "#000000");
                            group_meeting_item.setAttribute("fill", "#000000");

                            group_meeting_item.setAttribute("font-size", "24");
                            group_meeting_item.setAttribute("transform",
                                    "translate(5," + String.valueOf(transformY + 32) + ")");
                            itemHeight += 64;
                            if (meetingListString.length() > 12) {
                                StringBuilder formattedText = new StringBuilder();
                                int length = meetingListString.length();
                                int maxCharactersPerLine = 12;

                                for (int i = 0; i < length; i++) {
                                    char currentChar = meetingListString.charAt(i);

                                    // 将字符转换为全角
                                    if (Character.isLetterOrDigit(currentChar)) {
                                        if (Character.isUpperCase(currentChar)) {
                                            currentChar = (char) (currentChar + ('Ａ' - 'A'));
                                        } else if (Character.isLowerCase(currentChar)) {
                                            currentChar = (char) (currentChar + ('ａ' - 'a'));
                                        } else if (Character.isDigit(currentChar)) {
                                            currentChar = (char) (currentChar + 65248);
                                        }
                                    }

                                    formattedText.append(currentChar);

                                    // 如果达到最大字符数，则创建一个新的 tspan 元素
                                    if ((i + 1) % maxCharactersPerLine == 0 && i < length - 1) {
                                        Element meeting_tspan = document.createElementNS("http://www.w3.org/2000/svg",
                                                "tspan");
                                        meeting_tspan.setAttribute("x", "192");
                                        meeting_tspan.setAttribute("dy", "22");
                                        if (i + 1 == 24) {
                                            meeting_tspan
                                                    .setTextContent(formattedText.toString().substring(0, 11) + "...");
                                            meeting_text.appendChild(meeting_tspan);
                                            formattedText.setLength(0); // 清空 StringBuilder
                                            break;

                                        } else {
                                            meeting_tspan
                                                    .setTextContent(formattedText.toString());
                                            meeting_text.appendChild(meeting_tspan);
                                        }

                                        formattedText.setLength(0); // 清空 StringBuilder
                                    }
                                }

                                // 处理剩余字符
                                if (formattedText.length() > 0) {
                                    Element meeting_tspan = document.createElementNS("http://www.w3.org/2000/svg",
                                            "tspan");
                                    meeting_tspan.setAttribute("x", "192");
                                    meeting_tspan.setAttribute("dy", "22");
                                    meeting_tspan.setTextContent(formattedText.toString());
                                    meeting_text.appendChild(meeting_tspan);
                                }

                                // 更新 meetingListString
                                meetingListString = formattedText.toString();
                            } else {
                                // 如果长度小于或等于12，直接转换并添加
                                StringBuilder formattedText = new StringBuilder();
                                for (int i = 0; i < meetingListString.length(); i++) {
                                    char currentChar = meetingListString.charAt(i);
                                    if (Character.isLetterOrDigit(currentChar)) {
                                        if (Character.isUpperCase(currentChar)) {
                                            currentChar = (char) (currentChar + ('Ａ' - 'A'));
                                        } else if (Character.isLowerCase(currentChar)) {
                                            currentChar = (char) (currentChar + ('ａ' - 'a'));
                                        } else if (Character.isDigit(currentChar)) {
                                            currentChar = (char) (currentChar + 65248);
                                        }
                                    }

                                    formattedText.append(currentChar);
                                }
                                Element meeting_tspan = document.createElementNS("http://www.w3.org/2000/svg", "tspan");
                                meeting_tspan.setAttribute("x", "192");
                                meeting_tspan.setAttribute("dy", "22");
                                meeting_tspan.setTextContent(formattedText.toString());
                                meeting_text.appendChild(meeting_tspan);
                            }

                            if (index == 0) {
                                Element rect_red = document.createElementNS("http://www.w3.org/2000/svg", "rect");
                                rect_red.setAttribute("x", "0");
                                rect_red.setAttribute("fill", "red");
                                rect_red.setAttribute("height", String.valueOf(itemHeight + 4));
                                rect_red.setAttribute("width", "640");
                                rect_red.setAttribute("transform", "translate(0,-5)");
                                group_meeting_item.appendChild(rect_red);
                                timettext.setAttribute("stroke", "white");
                                moderator.setAttribute("stroke", "white");
                                meeting_text.setAttribute("stroke", "white");
                                timettext.setAttribute("fill", "white");
                                moderator.setAttribute("fill", "white");
                                meeting_text.setAttribute("fill", "white");

                                meeting_text.setAttribute("dy", "26");
                                timetspan_end.setAttribute("dy", "26");
                            }

                            group_meeting_item.appendChild(timettext);
                            group_meeting_item.appendChild(moderator);
                            group_meeting_item.appendChild(meeting_text);
                            screen_Element.appendChild(group_meeting_item);
                            itemHeight += 36;
                        } else {
                            int transformY = baseDy + ListDy;
                            Element group_meeting_item = document.createElementNS("http://www.w3.org/2000/svg", "g");
                            group_meeting_item.setAttribute("text-anchor", "start");
                            group_meeting_item.setAttribute("font-family", "Noto Sans JP");
                            group_meeting_item.setAttribute("id", "meetingList");
                            group_meeting_item.setAttribute("stroke-width", "0.2");
                            group_meeting_item.setAttribute("stroke", "#000000");
                            group_meeting_item.setAttribute("fill", "#000000");

                            group_meeting_item.setAttribute("font-size", "24");
                            group_meeting_item.setAttribute("transform",
                                    "translate(5," + String.valueOf(transformY + itemHeight) + ")");
                            itemHeight += 64;
                            if (meetingListString.length() > 12) {
                                StringBuilder formattedText = new StringBuilder();
                                int length = meetingListString.length();
                                int maxCharactersPerLine = 12;

                                for (int i = 0; i < length; i++) {
                                    char currentChar = meetingListString.charAt(i);

                                    // 将字符转换为全角
                                    if (Character.isLetterOrDigit(currentChar)) {
                                        if (Character.isUpperCase(currentChar)) {
                                            currentChar = (char) (currentChar + ('Ａ' - 'A'));
                                        } else if (Character.isLowerCase(currentChar)) {
                                            currentChar = (char) (currentChar + ('ａ' - 'a'));
                                        } else if (Character.isDigit(currentChar)) {
                                            currentChar = (char) (currentChar + 65248);
                                        }
                                    }

                                    formattedText.append(currentChar);

                                    // 如果达到最大字符数，则创建一个新的 tspan 元素
                                    if ((i + 1) % maxCharactersPerLine == 0 && i < length - 1) {
                                        Element meeting_tspan = document.createElementNS("http://www.w3.org/2000/svg",
                                                "tspan");
                                        meeting_tspan.setAttribute("x", "192");
                                        meeting_tspan.setAttribute("dy", "22");
                                        if (i + 1 == 24) {
                                            meeting_tspan
                                                    .setTextContent(formattedText.toString().substring(0, 11) + "...");
                                            meeting_text.appendChild(meeting_tspan);
                                            formattedText.setLength(0);
                                            break;

                                        } else {
                                            meeting_tspan
                                                    .setTextContent(formattedText.toString());
                                            meeting_text.appendChild(meeting_tspan);
                                            formattedText.setLength(0);
                                        }

                                    }
                                }

                                // 处理剩余字符
                                if (formattedText.length() > 0) {
                                    Element meeting_tspan = document.createElementNS("http://www.w3.org/2000/svg",
                                            "tspan");
                                    meeting_tspan.setAttribute("x", "192");
                                    meeting_tspan.setAttribute("dy", "22");
                                    meeting_tspan.setTextContent(formattedText.toString());
                                    meeting_text.appendChild(meeting_tspan);
                                }

                                // 更新 meetingListString
                                meetingListString = formattedText.toString();
                            } else {
                                // 如果长度小于或等于12，直接转换并添加
                                StringBuilder formattedText = new StringBuilder();
                                for (int i = 0; i < meetingListString.length(); i++) {
                                    char currentChar = meetingListString.charAt(i);
                                    if (Character.isLetterOrDigit(currentChar)) {
                                        if (Character.isUpperCase(currentChar)) {
                                            currentChar = (char) (currentChar + ('Ａ' - 'A'));
                                        } else if (Character.isLowerCase(currentChar)) {
                                            currentChar = (char) (currentChar + ('ａ' - 'a'));
                                        } else if (Character.isDigit(currentChar)) {
                                            currentChar = (char) (currentChar + 65248);
                                        }
                                    }
                                    formattedText.append(currentChar);
                                }
                                Element meeting_tspan = document.createElementNS("http://www.w3.org/2000/svg", "tspan");
                                meeting_tspan.setAttribute("x", "192");
                                meeting_tspan.setAttribute("dy", "22");
                                meeting_tspan.setTextContent(formattedText.toString());
                                meeting_text.appendChild(meeting_tspan);
                            }

                            group_meeting_item.appendChild(moderator);
                            group_meeting_item.appendChild(timettext);
                            group_meeting_item.appendChild(meeting_text);
                            screen_Element.appendChild(group_meeting_item);

                        }
                        lastDateString = timeOnlyDate;
                    }

                } else {

                    int baseDy = 118;
                    int maxHeight = 810;
                    if (machineObj.getTempleteMode().equals("1")) {

                    }
                    int ListDy = 0;
                    int itemHeight = 0;

                    Element screen_Element = document.getElementById("screen");
                    for (int index = 0; index < meetingArray.size(); index++) {
                        if (baseDy + ListDy + itemHeight > maxHeight) {
                            break;
                        }
                        String meetingPurposeName_String = meetingArray.get(index).getPurposeName();
                        String meetingTexString = "";
                        String subjecString = meetingArray.get(index).getSubject();
                        String timeOnlyDate = meetingArray.get(index).timeOnlyDate();
                        String timeOnlyWeekDay = meetingArray.get(index).timeOnlyWeekDay();
                        String[] timeOnlyTimeDuring_Array = meetingArray.get(index).timeOnlyTimeDuring().split("~");
                        String meetingListString = "";

                        Element timettext = document.createElementNS("http://www.w3.org/2000/svg", "text");
                        Element timetspan_start = document.createElementNS("http://www.w3.org/2000/svg", "tspan");
                        timetspan_start.setAttribute("x", "28");
                        timetspan_start.setAttribute("dy", "22");
                        timetspan_start.setTextContent(timeOnlyTimeDuring_Array[0]);
                        Element timetspan_end = document.createElementNS("http://www.w3.org/2000/svg", "tspan");
                        timetspan_end.setAttribute("x", "28");
                        timetspan_end.setAttribute("dy", "22");
                        timetspan_end.setTextContent(timeOnlyTimeDuring_Array[1]);
                        timettext.appendChild(timetspan_start);
                        timettext.appendChild(timetspan_end);
                        Element meeting_text = document.createElementNS("http://www.w3.org/2000/svg",
                                "text");
                        meeting_text.setAttribute("x", "192");
                        meeting_text.setAttribute("dy", "26");
                        Element moderator = document.createElementNS("http://www.w3.org/2000/svg", "text");
                        moderator.setAttribute("text-anchor", "end");
                        moderator.setAttribute("x", "600");
                        moderator.setAttribute("dy", "24");

                        // 根据outlook和预约系统的数据结构，对meetingText进行统一处理
                        if (!subjecString.equals("")) {
                            meetingListString = subjecString;
                            String text = meetingArray.get(index).getText();
                            int length = meetingArray.get(index).getText().length();
                            if (meetingArray.get(index).getText().length() > 4) {
                                moderator.setAttribute("dy", "24");
                                moderator.setAttribute("font-size", "12");
                                if (length % 2 == 0) {

                                    Element tspanElement_up = document.createElementNS("http://www.w3.org/2000/svg",
                                            "tspan");
                                    tspanElement_up.setTextContent(text.substring(0, length / 2));
                                    tspanElement_up.setAttribute("dy", "12");
                                    tspanElement_up.setAttribute("x", "600");
                                    Element tspanElement = document.createElementNS("http://www.w3.org/2000/svg",
                                            "tspan");
                                    tspanElement.setAttribute("x", "600");
                                    tspanElement.setAttribute("dy", "12");
                                    tspanElement.setTextContent(text.substring(length / 2, length));
                                    moderator.appendChild(tspanElement_up);
                                    moderator.appendChild(tspanElement);

                                } else {
                                    Element tspanElement_up = document.createElementNS("http://www.w3.org/2000/svg",
                                            "tspan");
                                    tspanElement_up.setAttribute("dy", "12");
                                    tspanElement_up.setAttribute("x", "600");
                                    Element tspanElement = document.createElementNS("http://www.w3.org/2000/svg",
                                            "tspan");
                                    tspanElement.setAttribute("x", "600");
                                    tspanElement.setAttribute("dy", "12");

                                    tspanElement_up.setTextContent(text.substring(0, (length - 1) / 2 + 1));
                                    moderator.appendChild(tspanElement_up);
                                    tspanElement.setTextContent(text.substring((length - 1) / 2 + 1, length));
                                    moderator.appendChild(tspanElement);
                                }

                            } else {
                                moderator.setTextContent(meetingArray.get(index).getText());
                            }
                        } else {
                            meetingTexString = meetingArray.get(index).getText();
                            meetingListString = meetingTexString + "(" + meetingPurposeName_String + ")";
                        }

                        // 不同天的会议要新加一个表头
                        if (lastDateString.equals("") || !lastDateString.equals(timeOnlyDate)) {
                            ListDy += 42;
                            if (itemHeight != 0) {
                                ListDy += itemHeight;
                            }
                            itemHeight = 0;
                            int transformY = baseDy + ListDy;
                            Element group_header = document.createElementNS("http://www.w3.org/2000/svg", "g");
                            group_header.setAttribute("font-size", "16");
                            group_header.setAttribute("transform", "translate(5," + String.valueOf(transformY) + ")");

                            // 创建白色文字Text,内容设置为日期
                            Element rect_text = document.createElementNS("http://www.w3.org/2000/svg", "text");
                            rect_text.setAttribute("dx", "170");
                            rect_text.setAttribute("font-family", "Noto Sans JP");
                            rect_text.setAttribute("font-size", "24");
                            rect_text.setAttribute("dy", "24");
                            rect_text.setAttribute("id", "text-item");
                            rect_text.setAttribute("stroke-width", "0.2");
                            rect_text.setAttribute("stroke", "#000000");
                            rect_text.setAttribute("fill", "#FFFFFF");
                            rect_text.setTextContent(timeOnlyDate);
                            // 创建&lt;tspan&gt;元素，将星期插入到白色文本Text里
                            Element tspanElement = document.createElementNS("http://www.w3.org/2000/svg", "tspan");
                            tspanElement.setAttribute("dx", "50");
                            tspanElement.setTextContent(timeOnlyWeekDay);
                            // 将&lt;tspan&gt;元素添加到&lt;text&gt;元素中
                            rect_text.appendChild(tspanElement);
                            // 创建和设置黑框
                            Element rect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
                            rect.setAttribute("x", "0");
                            rect.setAttribute("fill", "#000000");
                            rect.setAttribute("height", "28");
                            rect.setAttribute("width", "640");
                            group_header.appendChild(rect);
                            group_header.appendChild(rect_text);
                            screen_Element.appendChild(group_header);
                            Element group_meeting_item = document.createElementNS("http://www.w3.org/2000/svg", "g");
                            group_meeting_item.setAttribute("dy", "26");
                            group_meeting_item.setAttribute("text-anchor", "start");
                            group_meeting_item.setAttribute("font-family", "Noto Sans JP");
                            group_meeting_item.setAttribute("id", "meetingList");
                            group_meeting_item.setAttribute("stroke-width", "0.2");
                            group_meeting_item.setAttribute("stroke", "#000000");
                            group_meeting_item.setAttribute("fill", "#000000");

                            group_meeting_item.setAttribute("font-size", "24");
                            group_meeting_item.setAttribute("transform",
                                    "translate(5," + String.valueOf(transformY + 32) + ")");
                            itemHeight += 64;
                            if (meetingListString.length() > 12) {
                                StringBuilder formattedText = new StringBuilder();
                                int length = meetingListString.length();
                                int maxCharactersPerLine = 12;

                                for (int i = 0; i < length; i++) {
                                    char currentChar = meetingListString.charAt(i);

                                    // 将字符转换为全角
                                    if (Character.isLetterOrDigit(currentChar)) {
                                        if (Character.isUpperCase(currentChar)) {
                                            currentChar = (char) (currentChar + ('Ａ' - 'A'));
                                        } else if (Character.isLowerCase(currentChar)) {
                                            currentChar = (char) (currentChar + ('ａ' - 'a'));
                                        } else if (Character.isDigit(currentChar)) {
                                            currentChar = (char) (currentChar + 65248);
                                        }
                                    }

                                    formattedText.append(currentChar);

                                    // 如果达到最大字符数，则创建一个新的 tspan 元素
                                    if ((i + 1) % maxCharactersPerLine == 0 && i < length - 1) {
                                        Element meeting_tspan = document.createElementNS("http://www.w3.org/2000/svg",
                                                "tspan");
                                        meeting_tspan.setAttribute("x", "192");
                                        meeting_tspan.setAttribute("dy", "22");
                                        if (i + 1 == 24) {
                                            meeting_tspan
                                                    .setTextContent(formattedText.toString().substring(0, 11) + "...");
                                            meeting_text.appendChild(meeting_tspan);
                                            formattedText.setLength(0); // 清空 StringBuilder
                                            break;

                                        } else {
                                            meeting_tspan
                                                    .setTextContent(formattedText.toString());
                                            meeting_text.appendChild(meeting_tspan);
                                        }

                                        formattedText.setLength(0); // 清空 StringBuilder
                                    }
                                }

                                // 处理剩余字符
                                if (formattedText.length() > 0) {
                                    Element meeting_tspan = document.createElementNS("http://www.w3.org/2000/svg",
                                            "tspan");
                                    meeting_tspan.setAttribute("x", "192");
                                    meeting_tspan.setAttribute("dy", "22");
                                    meeting_tspan.setTextContent(formattedText.toString());
                                    meeting_text.appendChild(meeting_tspan);
                                }

                                // 更新 meetingListString
                                meetingListString = formattedText.toString();
                            } else {
                                // 如果长度小于或等于12，直接转换并添加
                                StringBuilder formattedText = new StringBuilder();
                                for (int i = 0; i < meetingListString.length(); i++) {
                                    char currentChar = meetingListString.charAt(i);
                                    if (Character.isLetterOrDigit(currentChar)) {
                                        if (Character.isUpperCase(currentChar)) {
                                            currentChar = (char) (currentChar + ('Ａ' - 'A'));
                                        } else if (Character.isLowerCase(currentChar)) {
                                            currentChar = (char) (currentChar + ('ａ' - 'a'));
                                        } else if (Character.isDigit(currentChar)) {
                                            currentChar = (char) (currentChar + 65248);
                                        }
                                    }

                                    formattedText.append(currentChar);
                                }
                                Element meeting_tspan = document.createElementNS("http://www.w3.org/2000/svg", "tspan");
                                meeting_tspan.setAttribute("x", "192");
                                meeting_tspan.setAttribute("dy", "22");
                                meeting_tspan.setTextContent(formattedText.toString());
                                meeting_text.appendChild(meeting_tspan);
                            }

                            group_meeting_item.appendChild(timettext);
                            group_meeting_item.appendChild(moderator);
                            group_meeting_item.appendChild(meeting_text);
                            screen_Element.appendChild(group_meeting_item);
                            itemHeight += 36;
                        } else {
                            int transformY = baseDy + ListDy;
                            Element group_meeting_item = document.createElementNS("http://www.w3.org/2000/svg", "g");
                            group_meeting_item.setAttribute("text-anchor", "start");
                            group_meeting_item.setAttribute("font-family", "Noto Sans JP");
                            group_meeting_item.setAttribute("id", "meetingList");
                            group_meeting_item.setAttribute("stroke-width", "0.2");
                            group_meeting_item.setAttribute("stroke", "#000000");
                            group_meeting_item.setAttribute("fill", "#000000");

                            group_meeting_item.setAttribute("font-size", "24");
                            group_meeting_item.setAttribute("transform",
                                    "translate(5," + String.valueOf(transformY + itemHeight) + ")");
                            itemHeight += 64;
                            if (meetingListString.length() > 12) {
                                StringBuilder formattedText = new StringBuilder();
                                int length = meetingListString.length();
                                int maxCharactersPerLine = 12;

                                for (int i = 0; i < length; i++) {
                                    char currentChar = meetingListString.charAt(i);

                                    // 将字符转换为全角
                                    if (Character.isLetterOrDigit(currentChar)) {
                                        if (Character.isUpperCase(currentChar)) {
                                            currentChar = (char) (currentChar + ('Ａ' - 'A'));
                                        } else if (Character.isLowerCase(currentChar)) {
                                            currentChar = (char) (currentChar + ('ａ' - 'a'));
                                        } else if (Character.isDigit(currentChar)) {
                                            currentChar = (char) (currentChar + 65248);
                                        }
                                    }

                                    formattedText.append(currentChar);

                                    // 如果达到最大字符数，则创建一个新的 tspan 元素
                                    if ((i + 1) % maxCharactersPerLine == 0 && i < length - 1) {
                                        Element meeting_tspan = document.createElementNS("http://www.w3.org/2000/svg",
                                                "tspan");
                                        meeting_tspan.setAttribute("x", "192");
                                        meeting_tspan.setAttribute("dy", "22");
                                        if (i + 1 == 24) {
                                            meeting_tspan
                                                    .setTextContent(formattedText.toString().substring(0, 11) + "...");
                                            meeting_text.appendChild(meeting_tspan);
                                            formattedText.setLength(0); // 清空 StringBuilder
                                            break;

                                        } else {
                                            meeting_tspan
                                                    .setTextContent(formattedText.toString());
                                            meeting_text.appendChild(meeting_tspan);
                                        }

                                        formattedText.setLength(0); // 清空 StringBuilder
                                    }
                                }

                                // 处理剩余字符
                                if (formattedText.length() > 0) {
                                    Element meeting_tspan = document.createElementNS("http://www.w3.org/2000/svg",
                                            "tspan");
                                    meeting_tspan.setAttribute("x", "192");
                                    meeting_tspan.setAttribute("dy", "22");
                                    meeting_tspan.setTextContent(formattedText.toString());
                                    meeting_text.appendChild(meeting_tspan);
                                }

                                // 更新 meetingListString
                                meetingListString = formattedText.toString();
                            } else {
                                // 如果长度小于或等于12，直接转换并添加
                                StringBuilder formattedText = new StringBuilder();
                                for (int i = 0; i < meetingListString.length(); i++) {
                                    char currentChar = meetingListString.charAt(i);
                                    if (Character.isLetterOrDigit(currentChar)) {
                                        if (Character.isUpperCase(currentChar)) {
                                            currentChar = (char) (currentChar + ('Ａ' - 'A'));
                                        } else if (Character.isLowerCase(currentChar)) {
                                            currentChar = (char) (currentChar + ('ａ' - 'a'));
                                        } else if (Character.isDigit(currentChar)) {
                                            currentChar = (char) (currentChar + 65248);
                                        }
                                    }
                                    formattedText.append(currentChar);
                                }
                                Element meeting_tspan = document.createElementNS("http://www.w3.org/2000/svg", "tspan");
                                meeting_tspan.setAttribute("x", "192");
                                meeting_tspan.setAttribute("dy", "22");
                                meeting_tspan.setTextContent(formattedText.toString());
                                meeting_text.appendChild(meeting_tspan);
                            }

                            group_meeting_item.appendChild(moderator);
                            group_meeting_item.appendChild(timettext);
                            group_meeting_item.appendChild(meeting_text);
                            screen_Element.appendChild(group_meeting_item);

                        }
                        lastDateString = timeOnlyDate;
                    }
                }
            } catch (ParseException e) {
                System.out.println(e);
            }
        } else {
            document = factory.createDocument("file:" + svgFilePath_blank);
            document.getElementById("roomName").setTextContent(roomName);

        }

        try {
            String Logo_img_base64 = machineObj.getLogo_img_base64();
            try {
                if (!Logo_img_base64.equals("") && !Logo_img_base64.equals("none")) {
                    document.getElementById("companyLogo").setAttribute("xlink:href", Logo_img_base64);
                }
                if (Logo_img_base64.equals("none")) {
                    document.getElementById("companyLogo").setAttribute("xlink:href", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAARgAAADKCAYAAAB+D18cAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAJOgAACToAYJjBRwAAAKWSURBVHhe7dQxAcAgEMDAp/49A0MlkO1uiYOsfQ1A4PsL8JzBABmDATIGA2QMBsgYDJAxGCBjMEDGYICMwQAZgwEyBgNkDAbIGAyQMRggYzBAxmCAjMEAGYMBMgYDZAwGyBgMkDEYIGMwQMZggIzBABmDATIGA2QMBsgYDJAxGCBjMEDGYICMwQAZgwEyBgNkDAbIGAyQMRggYzBAxmCAjMEAGYMBMgYDZAwGyBgMkDEYIGMwQMZggIzBABmDATIGA2QMBsgYDJAxGCBjMEDGYICMwQAZgwEyBgNkDAbIGAyQMRggYzBAxmCAjMEAGYMBMgYDZAwGyBgMkDEYIGMwQMZggIzBABmDATIGA2QMBsgYDJAxGCBjMEDGYICMwQAZgwEyBgNkDAbIGAyQMRggYzBAxmCAjMEAGYMBMgYDZAwGyBgMkDEYIGMwQMZggIzBABmDATIGA2QMBsgYDJAxGCBjMEDGYICMwQAZgwEyBgNkDAbIGAyQMRggYzBAxmCAjMEAGYMBMgYDZAwGyBgMkDEYIGMwQMZggIzBABmDATIGA2QMBsgYDJAxGCBjMEDGYICMwQAZgwEyBgNkDAbIGAyQMRggYzBAxmCAjMEAGYMBMgYDZAwGyBgMkDEYIGMwQMZggIzBABmDATIGA2QMBsgYDJAxGCBjMEDGYICMwQAZgwEyBgNkDAbIGAyQMRggYzBAxmCAjMEAGYMBMgYDZAwGyBgMkDEYIGMwQMZggIzBABmDATIGA2QMBsgYDJAxGCBjMEDGYICMwQAZgwEyBgNkDAbIGAyQMRggYzBAxmCAjMEAGYMBMgYDZAwGyBgMkDEYIGMwQMZggIzBABmDATIGA2QMBsgYDJAxGCBjMEDGYIDIzAGs7gWQS6TkrwAAAABJRU5ErkJggg==");
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            if (machineObj.getTempleteMode().equals("0") || machineObj.getTempleteMode().equals("2")) {
                document.getElementById("roomName").setTextContent(
                        machineObj.gettname().split("-")[0] + "階 " + machineObj.gettname().split("-")[1] + "室");
            } else {
                document.getElementById("roomName").setTextContent(machineObj.gettname());
            }
            // textElement.setTextContent(meetingString);
            // 将修改后的Document转换为字符串
            StringWriter stringWriter = new StringWriter();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            String modifiedSvgContent = stringWriter.toString();

            // 创建PNGTranscoder实例
            PNGTranscoder transcoder = new PNGTranscoder();

            // 创建TranscoderInput对象，传入SVG文件的输入流
            try (StringReader stringReader = new StringReader(modifiedSvgContent);
                    FileOutputStream pngOutputStream = new FileOutputStream(pngFilePath)) {
                TranscoderInput input = new TranscoderInput(stringReader);
                TranscoderOutput output = new TranscoderOutput(pngOutputStream);

                // 执行转换
                transcoder.transcode(input, output);
            }

            String base64String = bufferedsvgToBase64(
                    pngFilePath);
            if (base64String.equals("")) {
                System.out.println("获取图片失败");
                return;
            }
            JsonObject jsonBody = new JsonObject();
            jsonBody.addProperty("imgsrc", "data:image/svg;base64," + base64String);
            RequestBody body = RequestBody.create(mediaType, gson.toJson(jsonBody));
            Request request = new Request.Builder()
                    .url("http://192.144.234.153:8000/user/api/mqtt/publish/" + macId +
                            "/display")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .build();

            boolean success = false;
            int attempt = 0;

            while (attempt < MAX_RETRIES && !success) {
                attempt++;
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        System.out.println(nowtime + " 刷新成功: " + response.toString());
                        success = true;
                    } else {
                        System.out.println(nowtime + " 请求失败: " + response.toString());
                    }

                    response.body().close();
                } catch (IOException e) {
                    System.out.println(nowtime + " 请求异常: " + e.getMessage());
                }

                if (!success && attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException e) {
                        System.out.println("重试延迟时发生中断: " + e.getMessage());
                    }
                }

            }

            if (!success) {
                System.out.println(nowtime + " 多次重试后仍然失败");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // @SuppressWarnings("unchecked")
    // private void updataImg(String token, machineObject machineObj) {
    // String macId = machineObj.getmacId();
    // String tid = machineObj.gettid();
    // String tid2 = machineObj.gettid2();
    // String tname = machineObj.gettname();
    // String roomName = " ";
    // String meetingListString = " ";
    // String meetingState = " ";
    // String lastDateString = "";
    // ArrayList<row> meetingArray = machineObj.getmeetingList();
    // String tidString = tid;
    // Calendar nowCalendar = Calendar.getInstance(timeZoneUTC);
    // Date nowtime = nowCalendar.getTime();
    // roomName = roomString(machineObj.gettname());
    // if (meetingArray.size() > 0) {

    // row firstMeeting = meetingArray.get(0);

    // try {
    // if (firstMeeting.getEndDate().after(nowtime) &&
    // firstMeeting.getStartDate().before(nowtime)
    // && firstMeeting.getEventLength() != 86400) {
    // meetingState = "利用中";
    // for (int index = 0; index < meetingArray.size(); index++) {
    // String meetingTime_String = meetingArray.get(index).timeDuringWithWeekDay();
    // String userById_String = meetingArray.get(index).getUserString();
    // String meetingPurposeName_String = meetingArray.get(index).getPurposeName();
    // String meetingTexString = meetingArray.get(index).getText();
    // String meetingTime_StringLarge = meetingTime_String.substring(0, 12);
    // String meetingTime_String_time = meetingTime_String.substring(13, 23);
    // if (meetingTexString.equals(" ") || meetingTexString.equals("")) {
    // meetingListString = meetingListString + "\\n" + meetingTime_String + " " +
    // userById_String + "\\n" + "(" + meetingPurposeName_String + ")" +
    // "\\n";
    // } else {
    // if (meetingTexString.length() > 18) {
    // StringBuilder formattedText = new StringBuilder();
    // int length = meetingTexString.length();
    // int maxCharactersPerLine = 18;
    // for (int i = 0; i < length; i++) {
    // char currentChar = meetingTexString.charAt(i);
    // if (Character.isLetterOrDigit(currentChar)) {
    // if (Character.isUpperCase(currentChar)) {
    // currentChar = (char) (currentChar + ('Ａ' - 'A'));
    // } else if (Character.isLowerCase(currentChar)) {
    // currentChar = (char) (currentChar + ('ａ' - 'a'));
    // } else if (Character.isDigit(currentChar)) {
    // currentChar = (char) (currentChar + 65248);
    // }
    // }
    // formattedText.append(currentChar);
    // if ((i + 1) % maxCharactersPerLine == 0 && i < meetingTexString.length() -
    // 1) {
    // formattedText.append("\\n");
    // }
    // }
    // meetingTexString = formattedText.toString();
    // }
    // if (lastDateString.equals("") ||
    // !lastDateString.equals(meetingTime_StringLarge)) {
    // meetingListString = meetingListString
    // + "\\n"

    // + meetingTime_StringLarge
    // + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"
    // + "\\n"
    // + meetingTime_String_time
    // + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"

    // + userById_String
    // + "\\n"
    // + meetingTexString + "\\n" +
    // "(" + meetingPurposeName_String + ")" + "\\n";

    // lastDateString = meetingTime_StringLarge;
    // } else {
    // meetingListString = meetingListString
    // + "\\n"

    // + meetingTime_String_time
    // + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"
    // + userById_String
    // + "\\n"
    // + meetingTexString + "\\n" +
    // "(" + meetingPurposeName_String + ")" + "\\n";

    // }

    // }
    // }
    // } else {
    // meetingState = "空室";
    // tidString = tid2;
    // for (int index = 0; index < meetingArray.size(); index++) {
    // String meetingTime_String = meetingArray.get(index).timeDuringWithWeekDay();
    // String userById_String = meetingArray.get(index).getUserString();
    // String meetingPurposeName_String = meetingArray.get(index).getPurposeName();
    // String meetingTexString = meetingArray.get(index).getText();
    // String meetingTime_StringLarge = meetingTime_String.substring(0, 14);
    // String meetingTime_String_time = meetingTime_String.substring(15, 26);
    // if (meetingTexString.equals(" ") || meetingTexString.equals("")) {
    // meetingListString = meetingListString + "\\n" + meetingTime_String + "\\n" +
    // "\\n" + "(" + meetingPurposeName_String + ")" +
    // "\\n";
    // } else {
    // if (meetingTexString.length() > 18) {
    // StringBuilder formattedText = new StringBuilder();
    // int length = meetingTexString.length();
    // int maxCharactersPerLine = 18;
    // for (int i = 0; i < length; i++) {
    // char currentChar = meetingTexString.charAt(i);
    // if (Character.isLetterOrDigit(currentChar)) {
    // if (Character.isUpperCase(currentChar)) {
    // currentChar = (char) (currentChar + ('Ａ' - 'A'));
    // } else if (Character.isLowerCase(currentChar)) {
    // currentChar = (char) (currentChar + ('ａ' - 'a'));
    // } else if (Character.isDigit(currentChar)) {
    // currentChar = (char) (currentChar + 65248);
    // }
    // }
    // formattedText.append(currentChar);
    // if ((i + 1) % maxCharactersPerLine == 0 && i < meetingTexString.length() -
    // 1) {
    // formattedText.append("\\n");
    // }
    // }
    // meetingTexString = formattedText.toString();
    // }
    // if (lastDateString.equals("") ||
    // !lastDateString.equals(meetingTime_StringLarge)) {
    // meetingListString = meetingListString
    // + "\\n"

    // + meetingTime_StringLarge
    // + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"
    // + "\\n"
    // + meetingTime_String_time
    // + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"

    // + userById_String
    // + "\\n"
    // + meetingTexString + "\\n" +
    // "(" + meetingPurposeName_String + ")" + "\\n";

    // lastDateString = meetingTime_StringLarge;
    // } else {
    // meetingListString = meetingListString
    // + "\\n"

    // + meetingTime_String_time
    // + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"
    // + userById_String
    // + "\\n"
    // + meetingTexString + "\\n" +
    // "(" + meetingPurposeName_String + ")" + "\\n";

    // }
    // }

    // }
    // }
    // } catch (ParseException e) {
    // System.out.println(e);
    // }
    // } else {
    // meetingState = "空室";
    // tidString = tid2;
    // }

    // try {

    // @SuppressWarnings("rawtypes")
    // Map map = new HashMap();
    // map.put("tid", tidString);
    // map.put("tname", tname);
    // map.put("data", Map.of(
    // "meetingState", meetingState,
    // "roomName", roomName,
    // "meetingListString", meetingListString));
    // ObjectMapper mapper = new ObjectMapper();
    // String content = mapper.writeValueAsString(map);

    // Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    // content = gson.toJson(map);
    // content = content.replace("\\\\n", "\\n");

    // RequestBody body = RequestBody.create(MediaType.parse("application/json"),
    // content);
    // Request request = new Request.Builder()
    // .url("http://192.144.234.153:4000/user/api/mqtt/publish/" + macId +
    // "/template/" + tidString)
    // .post(body)
    // .addHeader("Authorization", "Bearer " + token)
    // .addHeader("Content-Type", "application/json")
    // .build();

    // Response response = okHttpClient.newCall(request).execute();
    // System.out.println(response.toString());
    // response.body().close();

    // } catch (Exception e) {
    // System.out.println(e);
    // }
    // }

    // 横板
    // @SuppressWarnings("unchecked")
    // private void updataImg(String token, machineObject machineObj) {
    // String macId = machineObj.getmacId();
    // String tid = machineObj.gettid();
    // String tid2 = machineObj.gettid2();
    // String tname = machineObj.gettname();
    // String meetingPurposeName = " ";
    // String meetingTime = " ";
    // String roomName = " ";
    // String meetingListString = " ";
    // String meetingState = " ";
    // String meetingText = " ";
    // String userByString = " ";
    // ArrayList<row> meetingArray = machineObj.getmeetingList();
    // String tidString = tid;
    // Calendar nowCalendar = Calendar.getInstance(timeZoneUTC);
    // Date nowtime = nowCalendar.getTime();
    // roomName = roomString(machineObj.gettname());
    // if (meetingArray.size() > 0) {

    // row firstMeeting = meetingArray.get(0);

    // try {
    // if (firstMeeting.getEndDate().after(nowtime) &&
    // firstMeeting.getStartDate().before(nowtime)
    // && firstMeeting.getEventLength() != 86400) {
    // meetingState = "利用中";
    // meetingTime = firstMeeting.timeDuringDay();

    // meetingPurposeName = "(" +
    // firstMeeting.getPurposeName() + ")";
    // meetingText = firstMeeting.getText();
    // userByString = firstMeeting.getUserString();
    // if (meetingText.length() > 12) {
    // StringBuilder formattedText = new StringBuilder();
    // int length = meetingText.length();
    // int maxCharactersPerLine = 12;
    // for (int i = 0; i < length; i++) {
    // char currentChar = meetingText.charAt(i);
    // if (Character.isLetterOrDigit(currentChar)) {
    // if (Character.isUpperCase(currentChar)) {
    // currentChar = (char) (currentChar + ('Ａ' - 'A'));
    // } else if (Character.isLowerCase(currentChar)) {
    // currentChar = (char) (currentChar + ('ａ' - 'a'));
    // } else if (Character.isDigit(currentChar)) {
    // currentChar = (char) (currentChar + 65248);
    // }
    // }
    // formattedText.append(currentChar);
    // if ((i + 1) % maxCharactersPerLine == 0 && i < meetingText.length() - 1) {
    // formattedText.append("\\n");
    // }
    // }
    // meetingText = formattedText.toString();
    // }
    // for (int index = 1; index < meetingArray.size(); index++) {
    // String meetingTime_String = meetingArray.get(index).timeDuringWithWeekDay();
    // String userById_String = meetingArray.get(index).getUserString();
    // String meetingPurposeName_String = meetingArray.get(index).getPurposeName();
    // String meetingTexString = meetingArray.get(index).getText();

    // if (meetingTexString.equals(" ") || meetingTexString.equals("")) {
    // meetingListString = meetingListString + "\\n" + meetingTime_String + " " +
    // userById_String + "\\n" + "(" + meetingPurposeName_String + ")" +
    // "\\n";
    // } else {
    // if (meetingTexString.length() > 18) {
    // StringBuilder formattedText = new StringBuilder();
    // int length = meetingTexString.length();
    // int maxCharactersPerLine = 18;
    // for (int i = 0; i < length; i++) {
    // char currentChar = meetingTexString.charAt(i);
    // if (Character.isLetterOrDigit(currentChar)) {
    // if (Character.isUpperCase(currentChar)) {
    // currentChar = (char) (currentChar + ('Ａ' - 'A'));
    // } else if (Character.isLowerCase(currentChar)) {
    // currentChar = (char) (currentChar + ('ａ' - 'a'));
    // } else if (Character.isDigit(currentChar)) {
    // currentChar = (char) (currentChar + 65248);
    // }
    // }
    // formattedText.append(currentChar);
    // if ((i + 1) % maxCharactersPerLine == 0 && i < meetingTexString.length() -
    // 1) {
    // formattedText.append("\\n");
    // }
    // }
    // meetingTexString = formattedText.toString();
    // }
    // meetingListString = meetingListString + "\\n" + meetingTime_String + " " +
    // userById_String + "\\n"
    // + meetingTexString + "\\n" + "(" + meetingPurposeName_String + ")" +
    // "\\n";
    // }
    // }
    // } else {
    // meetingState = "空室";
    // tidString = tid2;
    // for (int index = 0; index < meetingArray.size(); index++) {
    // String meetingTime_String = meetingArray.get(index).timeDuringWithWeekDay();
    // String userById_String = meetingArray.get(index).getUserString();
    // String meetingPurposeName_String = meetingArray.get(index).getPurposeName();
    // String meetingTexString = meetingArray.get(index).getText();
    // if (meetingTexString.equals(" ") || meetingTexString.equals("")) {
    // meetingListString = meetingListString + "\\n" + meetingTime_String + " " +
    // userById_String + "\\n" + "(" + meetingPurposeName_String + ")" +
    // "\\n";
    // } else {
    // if (meetingTexString.length() > 18) {
    // StringBuilder formattedText = new StringBuilder();
    // int length = meetingTexString.length();
    // int maxCharactersPerLine = 18;
    // for (int i = 0; i < length; i++) {
    // char currentChar = meetingTexString.charAt(i);
    // if (Character.isLetterOrDigit(currentChar)) {
    // if (Character.isUpperCase(currentChar)) {
    // currentChar = (char) (currentChar + ('Ａ' - 'A'));
    // } else if (Character.isLowerCase(currentChar)) {
    // currentChar = (char) (currentChar + ('ａ' - 'a'));
    // } else if (Character.isDigit(currentChar)) {
    // currentChar = (char) (currentChar + 65248);
    // }
    // }
    // formattedText.append(currentChar);
    // if ((i + 1) % maxCharactersPerLine == 0 && i < meetingTexString.length() -
    // 1) {
    // formattedText.append("\\n");
    // }
    // }
    // meetingTexString = formattedText.toString();
    // }
    // meetingListString = meetingListString + "\\n" + meetingTime_String + " " +
    // userById_String + "\\n"
    // + meetingTexString + "\\n" + "(" + meetingPurposeName_String + ")" +
    // "\\n";
    // }

    // }
    // }
    // } catch (ParseException e) {
    // System.out.println(e);
    // }
    // } else {
    // meetingState = "空室";
    // tidString = tid2;
    // }

    // try {

    // @SuppressWarnings("rawtypes")
    // Map map = new HashMap();
    // map.put("tid", tidString);
    // map.put("tname", tname);
    // map.put("data", Map.of(
    // "meetingPurposeName", meetingPurposeName,
    // "meetingText", meetingText,
    // "meetingState", meetingState,
    // "meetingTime", meetingTime,
    // "userByString", userByString,
    // "roomName", roomName,
    // "meetingListString", meetingListString));
    // ObjectMapper mapper = new ObjectMapper();
    // String content = mapper.writeValueAsString(map);

    // Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    // content = gson.toJson(map);
    // content = content.replace("\\\\n", "\\n");

    // RequestBody body = RequestBody.create(MediaType.parse("application/json"),
    // content);
    // Request request = new Request.Builder()
    // .url("http://192.144.234.153:4000/user/api/mqtt/publish/" + macId +
    // "/template/" + tidString)
    // .post(body)
    // .addHeader("Authorization", "Bearer " + token)
    // .addHeader("Content-Type", "application/json")
    // .build();

    // Response response = okHttpClient.newCall(request).execute();

    // response.body().close();

    // } catch (Exception e) {
    // System.out.println(e);
    // }
    // }
    // public String convertHtmlToImage(String htmlFilePath) throws Exception {
    // String base64String = "";

    // try {
    // String inputHtmlPath = "path/to/your_file.html";
    // String outputPngPath = "output.png";

    // // Parse the HTML file into a Document
    // DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    // DocumentBuilder builder = factory.newDocumentBuilder();
    // Document document = builder.parse(new File(inputHtmlPath));

    // // Create an ITextRenderer to render the document to an image
    // ITextRenderer renderer = new ITextRenderer();
    // renderer.setDocument(document, null);
    // renderer.layout();

    // // Create a ByteArrayOutputStream to hold the rendered PDF content
    // ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
    // renderer.createPDF(pdfOutputStream);

    // // 将PDF内容转换为PNG
    // byte[] pdfBytes = pdfOutputStream.toByteArray();
    // BufferedImage image = convertPdfToPng(pdfBytes, outputPngPath);

    // // 将图像转换为Base64
    // ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // ImageIO.write(image, "png", baos);
    // String base64String =
    // java.util.Base64.getEncoder().encodeToString(baos.toByteArray());

    // System.out.println("Base64 image string: " + base64String);
    // } catch (MalformedURLException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return base64String;
    // }

    // 转图片备案(Java2DRenderer)
    // public static String convertHtmlToBase64(String inputHtmlPath) throws
    // Exception {
    // File inputFile = new File(inputHtmlPath);
    // // 将HTML文档的URL传递给Java2DRenderer
    // URL inputUrl = inputFile.toURI().toURL();
    // Java2DRenderer renderer = new Java2DRenderer(inputUrl.toString(), 800, 600);
    // BufferedImage image = renderer.getImage();
    // File outputFile = new
    // File("C:\\Users\\t2294\\Downloads\\first-app-lesson-00\\meetingLoginApp\\static\\output.jpeg");
    // ImageIO.write(image, "jpeg", outputFile);
    // // 将图像转换为Base64字符串
    // ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // ImageIO.write(image, "jpeg", baos);

    // return Base64.getEncoder().encodeToString(baos.toByteArray());
    // }
    public static String bufferedsvgToBase64(String filePath) throws IOException {
        byte[] svgBytes = Files.readAllBytes(Paths.get(filePath));

        // Encode SVG content to Base64
        String base64String = Base64.getEncoder().encodeToString(svgBytes);

        return base64String;
    }

    // // \n替换为换行标签（svg用）
    // public static void replaceNewlinesWithTspan(Document document, String
    // elementId) {
    // Element textElement = document.getElementById(elementId);

    // if (textElement != null) {
    // String textContent = textElement.getTextContent();

    // // 清空原来的内容
    // textElement.setTextContent("");

    // String[] lines = textContent.split("\\r?\\n");
    // for (int i = 0; i < lines.length; i++) {
    // Element tspan = document.createElementNS("http://www.w3.org/2000/svg",
    // "tspan");
    // tspan.setTextContent(lines[i]);
    // // 设置位置属性，第一行 dy=0，后续每行 dy=20
    // tspan.setAttribute("x", textElement.getAttribute("x"));
    // tspan.setAttribute("dy","20");

    // // 将 tspan 元素添加到 textElement 中
    // textElement.appendChild(tspan);
    // }
    // } else {
    // System.out.println("Element with ID " + elementId + " not found.");
    // }
    // }
    public static String bufferedImageToBase64(BufferedImage image) throws IOException {
        String base64String = "";
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Write the image to the ByteArrayOutputStream
            ImageIO.write(image, "jpg", baos);

            // Convert the image to byte array
            byte[] imageBytes = baos.toByteArray();

            // Encode the byte array to Base64
            base64String = Base64.getEncoder().encodeToString(imageBytes);
        }
        return base64String;
    }
    // public static String convertHtmlToBase64(String inputHtmlPath) throws
    // Exception {
    // File inputFile = new File(inputHtmlPath);
    // // 将HTML文档的URL传递给Java2DRenderer
    // URL inputUrl = inputFile.toURI().toURL();
    // Java2DRenderer renderer = new Java2DRenderer(inputUrl.toString(), 800, 600);
    // BufferedImage image = renderer.getImage();
    // File outputFile = new File(
    // "C:\\Users\\t2294\\Downloads\\first-app-lesson-00\\meetingLoginApp\\static\\output.jpeg");
    // ImageIO.write(image, "jpeg", outputFile);
    // // 将图像转换为Base64字符串
    // ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // ImageIO.write(image, "jpeg", baos);

    // return Base64.getEncoder().encodeToString(baos.toByteArray());
    // }

    public static class machineObject {
        private String templeteMode = "0";
        private String tname = "";
        private String macId = "";
        private String meetingStates = "";
        private String logo_img_base64 = "";
        private Boolean changeFlag = false;
        private ArrayList<row> meetingList = new ArrayList<>();

        public void setTempleteMode(String templeteMode) {
            this.templeteMode = templeteMode;
        }

        public void setMeetingStates(String meetingStates) {
            this.meetingStates = meetingStates;
        }

        public String getMeetingStates() {
            return meetingStates;
        }

        public String getTempleteMode() {
            return templeteMode;
        }

        public String getLogo_img_base64() {
            return logo_img_base64;
        }

        public void setLogo_img_base64(String logo_img_base64) {
            this.logo_img_base64 = logo_img_base64;
        }

        public String gettname() {
            return this.tname;
        }

        public void settname(String tname) {
            this.tname = tname;
        }

        public String getmacId() {
            return this.macId;
        }

        public void setmacId(String macId) {
            this.macId = macId;
        }

        public Boolean getchangeFlag() {
            return this.changeFlag;
        }

        public void setchangeFlag(Boolean changeFlag) {
            this.changeFlag = changeFlag;
        }

        public ArrayList<row> getmeetingList() {
            return this.meetingList;
        }

        public void addmeetingList(row row) {
            this.meetingList.add(row);
        }

        public void clearmeetingList(row row) {
            this.meetingList.clear();
        }
    }

    public static ArrayList<machineObject> getMachineList() {
        return machineList;
    }
}
