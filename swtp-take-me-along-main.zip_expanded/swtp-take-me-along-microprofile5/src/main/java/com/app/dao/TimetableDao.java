package com.app.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.app.model.Timetable;
import com.app.model.TimetablePk;

import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Singleton
public class TimetableDao implements IDao<Timetable, TimetablePk> {

    @PersistenceContext(name = "jpa-unit")
    private EntityManager _em;

    @Override
    public void delete(Timetable t) {
        try {
            _em.remove(t);
        } catch (Throwable thr) {
            throw new RuntimeException("ERROR deleting timetable " + t);
        }
    }

    @Override
    public Collection<Timetable> getAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Timetable> getById(TimetablePk id) {

        Timetable tt = _em.find(Timetable.class, id);

        if (tt != null)
            return Optional.of(tt);
        else
            return Optional.empty();

    }

    @Override
    public TimetablePk insert(Timetable t) {
        try {
            _em.persist(t);
            _em.flush();
            _em.refresh(t);
            return t.getId();
        } catch (Throwable thr) {
            thr.printStackTrace();
            throw new RuntimeException("ERROR inserting Timetable " + thr.getMessage());
        }
    }

    @Override
    public void update(Timetable t) {
        try {
            _em.merge(t);
            _em.flush();
        } catch (Throwable thr) {
            thr.printStackTrace();
            throw new RuntimeException("ERROR updating Timetable " + thr.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getWeekdaysForUserId(Integer userid) {
        Query query = _em.createNamedQuery("Timetable.getWeekdays", Timetable.class);
        query.setParameter("userId", userid);

        return (List<Integer>) query.getResultList();
    }
}
