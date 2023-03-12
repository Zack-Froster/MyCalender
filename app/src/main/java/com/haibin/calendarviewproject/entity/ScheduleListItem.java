package com.haibin.calendarviewproject.entity;

public class ScheduleListItem {
    private Schedule schedule;
    private boolean isShow;
    private boolean isChecked;

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public ScheduleListItem(Schedule schedule, boolean isShow, boolean isChecked) {
        this.schedule = schedule;
        this.isShow = isShow;
        this.isChecked = isChecked;
    }
}
