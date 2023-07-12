package com.app.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TimetablePk implements Serializable {
    // default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "user_id", nullable = false)
    private int userId;

    private int weekday;

    public TimetablePk() {

    }

    public TimetablePk(int u, int w) {
        userId = u;
        weekday = w;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getWeekday() {
        return this.weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof TimetablePk))
            return false;

        var otherPk = (TimetablePk) other;
        return (this.userId == otherPk.userId)
                && (this.weekday == otherPk.weekday);
    }

    public int hashCode() {
        final int prime = 31;

        int hash = 17;
        hash = hash * prime + ((int) (this.userId ^ (this.userId >>> 32)));
        hash = hash * prime + ((int) (this.weekday ^ (this.weekday >>> 32)));

        return hash;
    }

    @Override
    public String toString() {
        return "TimetablePK [userId=" + userId + ", weekday=" + weekday + "]";
    }
}
