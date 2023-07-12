package com.app.dto;

import java.io.Serializable;
import java.time.LocalTime;

import com.app.model.Timetable;

@SuppressWarnings("serial")
public class TimetableDto implements Serializable {
    private int weekday;
    private LocalTime startTime;
    private LocalTime endTime;

    public TimetableDto() {

    }

    public TimetableDto(Timetable timetable) {
        this.weekday = timetable.getId().getWeekday();
        this.startTime = timetable.getStartTime();
        this.endTime = timetable.getEndTime();
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "TimetableDto [weekday=" + weekday + ", startTime=" + startTime + ", endTime="
                + endTime + "]";
    }
}
