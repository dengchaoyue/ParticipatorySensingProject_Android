/**
 * class name:
 * class description:
 * author: dengchaoyue
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.taskpage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskItem {
    private int lat;
    private int lon;
    private String taskID;
    private int taskType;
    private int incentiveType;
    private int radius;
    private int dataNumber;
    private int earn;
    private int remainsNumber;
    private int thisRound;
    private int totalRound;
    private String diffTime;
    private String taskDescription;
    private String biddingDeadline;
    private String endTime;
    private String beginTime;
    private JSONArray bidInfo, participants;


    public TaskItem(JSONObject taskItem) {
        // TODO Auto-generated constructor stub
        try {
            this.incentiveType = taskItem.getInt("incentiveType");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getTaskInfomation(incentiveType, taskItem);
    }

    private void getTaskInfomation(int incentiveType, JSONObject taskItem) {
        if (incentiveType == 1) {
            try {
                lat = taskItem.getInt("lat");
                lon = taskItem.getInt("lon");
                taskID = taskItem.getString("taskID");
                taskType = taskItem.getInt("taskType");
                taskDescription = taskItem.getString("taskDescription");
                biddingDeadline = taskItem.getString("biddingDeadline");
                endTime = taskItem.getString("endTime");
                radius = taskItem.getInt("radius");
                beginTime = taskItem.getString("beginTime");
                earn = taskItem.getInt("earn");
                dataNumber = taskItem.getInt("dataNumber");
                remainsNumber = taskItem.getInt("remainsNumber");
                participants = taskItem.getJSONArray("participants");
                diffTime = calTimeDeadline(endTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (incentiveType == 2) {
            try {
                lat = taskItem.getInt("lat");
                lon = taskItem.getInt("lon");
                taskID = taskItem.getString("taskID");
                taskType = taskItem.getInt("taskType");
                taskDescription = taskItem.getString("taskDescription");
                biddingDeadline = taskItem.getString("biddingDeadline");
                endTime = taskItem.getString("endTime");
                radius = taskItem.getInt("radius");
                beginTime = taskItem.getString("beginTime");
                dataNumber = taskItem.getInt("dataNumber");
                bidInfo = taskItem.getJSONArray("bidInfo");
                diffTime = calTimeDeadline(endTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (incentiveType == 3) {
            try {
                lat = taskItem.getInt("lat");
                lon = taskItem.getInt("lon");
                taskID = taskItem.getString("taskID");
                taskType = taskItem.getInt("taskType");
                taskDescription = taskItem.getString("taskDescription");
                biddingDeadline = taskItem.getString("biddingDeadline");
                endTime = taskItem.getString("endTime");
                radius = taskItem.getInt("radius");
                beginTime = taskItem.getString("beginTime");
                earn = taskItem.getInt("earn");
                thisRound = taskItem.getInt("thisRound");
                totalRound = taskItem.getInt("totalRound");
                dataNumber = taskItem.getInt("dataNumber");
                remainsNumber = taskItem.getInt("remainsNumber");
                participants = taskItem.getJSONArray("participants");
                diffTime = calTimeDeadline(endTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String calTimeDeadline(String endTime) {
        long days, hours, minutes, diff;
        String returnDiffTime = "";
        Date d1, d2, today;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            d1 = df.parse(endTime);
            today = new Date();
            d2 = df.parse(df.format(today));
            diff = d2.getTime() - d1.getTime();//为了按时间排序
            days = diff / (1000 * 60 * 60 * 24);
            hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            returnDiffTime = "据截止日还有：" + days + "天" + hours + "小时" + minutes + "分";
            System.out.println("" + days + "天" + hours + "小时" + minutes + "分");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnDiffTime;
    }

    public int getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public int getLon() {
        return lon;
    }

    public void setLon(int lon) {
        this.lon = lon;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public int getIncentiveType() {
        return incentiveType;
    }

    public void setIncentiveType(int incentiveType) {
        this.incentiveType = incentiveType;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getDataNumber() {
        return dataNumber;
    }

    public void setDataNumber(int dataNumber) {
        this.dataNumber = dataNumber;
    }

    public int getEarn() {
        return earn;
    }

    public void setEarn(int earn) {
        this.earn = earn;
    }

    public int getRemainsNumber() {
        return remainsNumber;
    }

    public void setRemainsNumber(int remainsNumber) {
        this.remainsNumber = remainsNumber;
    }

    public int getThisRound() {
        return thisRound;
    }

    public void setThisRound(int thisRound) {
        this.thisRound = thisRound;
    }

    public int getTotalRound() {
        return totalRound;
    }

    public void setTotalRound(int totalRound) {
        this.totalRound = totalRound;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getBiddingDeadline() {
        return biddingDeadline;
    }

    public void setBiddingDeadline(String biddingDeadline) {
        this.biddingDeadline = biddingDeadline;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public JSONArray getBidInfo() {
        return bidInfo;
    }

    public void setBidInfo(JSONArray bidInfo) {
        this.bidInfo = bidInfo;
    }

    public JSONArray getParticipants() {
        return participants;
    }

    public void setParticipants(JSONArray participants) {
        this.participants = participants;
    }

    public String getDiffTime() {
        return diffTime;
    }

    public void setDiffTime(String diffTime) {
        this.diffTime = diffTime;
    }
}
