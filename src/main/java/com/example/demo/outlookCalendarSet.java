package com.example.demo;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.Dto.outlookCalendarSet_respose;
import com.example.demo.Settimeout;
import com.example.demo.Settimeout.machineObject;

@RestController
public class outlookCalendarSet {
    private static HashMap<String, outlookCalendarSet_respose> meetingList_All = new HashMap<>();

    @PostMapping("/setCalendar")
    public String setCalendar(@RequestBody outlookCalendarSet_respose outlookCalendarList) {
        try {
            String roomID = outlookCalendarList.getRoomID();
            String mapID = outlookCalendarList.getCompany().concat(roomID);
            ArrayList<machineObject> machineList = Settimeout.getMachineList();
            if (!roomID.isEmpty()) {
                for (int i = 0; i < machineList.size(); i++) {
                    if (machineList.get(i).getmacId().equals(outlookCalendarList.getMacID())) {
                        machineList.get(i).settname(roomID);
                        break;
                    }
                }
            }
            meetingList_All.put(mapID, outlookCalendarList);

            return "成功";
        } catch (Exception e) {

            System.out.println(e);
            return "失败";
        }

    }

    @GetMapping("/getCalendar")
    public HashMap<String, outlookCalendarSet_respose> outlookCalendarList() {
        return meetingList_All;
    }

    public static HashMap<String, outlookCalendarSet_respose> outlookCalendarList_get() {
        return meetingList_All;
    }

}
