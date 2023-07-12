package com.app.model;

import java.io.Serializable;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "timetable_weekday")
@NamedQueries({
        @NamedQuery(name = "Timetable.getById", query = "SELECT i FROM Timetable i WHERE i.id.userId = :userId and i.id.weekday = :weekday"),
        @NamedQuery(name = "Timetable.getWeekdays", query = "SELECT i.id.weekday FROM Timetable i WHERE i.id.userId = :userId"),
        @NamedQuery(name = "Timetable.getAll", query = "SELECT i FROM Timetable i WHERE i.id.userId = :userId"),
        @NamedQuery(name = "Timetable.delete", query = "DELETE FROM Timetable i WHERE i.id.userId = :userId and i.id.weekday = :weekday")
})
@SuppressWarnings("serial")
public class Timetable implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TimetablePk id;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    public Timetable() {

    }

    public Timetable(TimetablePk id, LocalTime startTime2, LocalTime endTime2) {
        this.id = id;
        this.startTime = startTime2;
        this.endTime = endTime2;
    }

    public TimetablePk getId() {
        return this.id;
    }

    public void setId(TimetablePk id) {
        this.id = id;
    }

    public LocalTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "Timetable [id=" + id.toString() + ", endTime=" + endTime + ", startTime=" + startTime + "]";
    }

}
