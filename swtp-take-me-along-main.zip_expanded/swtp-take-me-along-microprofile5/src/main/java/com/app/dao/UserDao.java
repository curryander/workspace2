package com.app.dao;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import com.app.geocode.GeoCoder;
import com.app.model.Position;
import com.app.model.TimetablePk;
import com.app.model.User;
import com.app.model.converter.PositionConverter;
import com.app.util.PasswordHelper;

@Singleton
public class UserDao {
    private static final String searchNearbyUserQuery = "SELECT "
            + "user_id, username, firstname, lastname, email, street, street_number, zip, city, position "
            + "FROM user  " + "WHERE cast((ST_Distance_Sphere(position,point(?, ?))/1000) as UNSIGNED) < ?";

    @PersistenceContext(name = "jpa-unit")
    private EntityManager _em;

    @Inject
    private TimetableDao _timetableDao;

    @Inject
    private GeoCoder _geoCoder;

    public boolean checkUserPassword(String username, String password) {
        try {
            Query query = _em.createNamedQuery("User.findByUserName", User.class);
            query.setParameter("username", username);
            User user = (User) query.getSingleResult();

            byte[] passwordSalt = user.getPasswordSalt();
            byte[] passwordHash = user.getPasswordHash();

            byte[] passwordToCheck = PasswordHelper.generatePasswordHash(password, passwordSalt);
            if (Arrays.equals(passwordHash, passwordToCheck)) {
                return true;
            } else {
                return false;
            }
        } catch (Throwable thr) {
            throw new RuntimeException("ERROR checkUserPassword");
        }

    }

    public boolean deleteUser(String username) {
        try {
            Query query = _em.createNamedQuery("User.findByUserName", User.class);
            query.setParameter("username", username);
            User user = (User) query.getSingleResult();
            _em.remove(user);

            return true;
        } catch (Throwable thr) {
            throw new RuntimeException("ERROR checkUserPassword");
        }
    }

    public User createUser(String username, String password, String firstname, String lastname, String email,
            String street, String streetNumber, String zip, String city) {

        try {
            User user = new User();
            user.setUsername(username);
            user.setFirstname(firstname);
            user.setLastname(lastname);
            user.setEmail(email);
            user.setStreet(street);
            user.setStreetNumber(streetNumber);
            user.setZip(zip);
            user.setCity(city);
            user.setImageId(1);

            Optional<Position> position = _geoCoder.geocode(street, streetNumber, zip, city, "Germany");

            if (position.isEmpty()) {
                System.out.println("Position not found");
                throw new RuntimeException();
            }

            user.setPosition(position.get());

            byte[] passwordSalt = PasswordHelper.generateSalt();
            byte[] passwordHash = PasswordHelper.generatePasswordHash(password, passwordSalt);

            user.setPasswordSalt(passwordSalt);
            user.setPasswordHash(passwordHash);

            _em.persist(user);
            _em.flush();
            _em.refresh(user);

            return user;
        } catch (Throwable thr) {
            thr.printStackTrace();
            throw new RuntimeException("ERROR createUser");
        }

    }

    public Optional<User> findUser(int user_id) {

        User user = _em.find(User.class, user_id);

        if (user != null) {
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    public Optional<User> findUser(String username) {
        User user;

        try {
            Query query = _em.createNamedQuery("User.findByUserName", User.class);
            query.setParameter("username", username);
            user = (User) query.getSingleResult();
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Throwable thr) {
            throw new RuntimeException("ERROR findUser");
        }

        if (user != null)
            return Optional.of(user);
        else
            return Optional.empty();
    }
    /*
     * @SuppressWarnings("unchecked")
     * public List<User> findUserNearBy(Position position, int radiusInKm) {
     * try {
     * 
     * Query sqlQuery = _em.createNativeQuery(searchNearbyUserQuery);
     * sqlQuery.setParameter(1, position.getLongitude());
     * sqlQuery.setParameter(2, position.getLatitude());
     * sqlQuery.setParameter(3, radiusInKm);
     * 
     * final List<Object[]> results = (List<Object[]>) sqlQuery.getResultList();
     * 
     * List<User> result = new ArrayList<User>();
     * for (Object[] row : results) {
     * User user = new User();
     * user.setUserId((int) row[0]);
     * user.setUsername((String) row[1]);
     * user.setFirstname((String) row[2]);
     * user.setLastname((String) row[3]);
     * user.setEmail((String) row[4]);
     * user.setStreet((String) row[5]);
     * user.setStreetNumber((String) row[6]);
     * user.setZip((String) row[7]);
     * user.setCity((String) row[8]);
     * Position userPosition = new
     * PositionConverter().convertToEntityAttribute((byte[]) row[9]);
     * user.setPosition(userPosition);
     * user.setImageId((int) row[10]);
     * 
     * result.add(user);
     * }
     * 
     * return result;
     * } catch (Exception exce) {
     * exce.printStackTrace();
     * return List.of();
     * }
     * }
     */

    @SuppressWarnings("unchecked")
    public List<User> findUserNearBy(Position position, int radiusInKm, int journeyType, String starttime,
            String endtime, int weekday, int userId) {
        try {
            Query sqlQuery = _em.createNativeQuery(searchNearbyUserQuery);
            sqlQuery.setParameter(1, position.getLongitude());
            sqlQuery.setParameter(2, position.getLatitude());
            sqlQuery.setParameter(3, radiusInKm);

            final List<Object[]> results = (List<Object[]>) sqlQuery.getResultList();
            // System.out.println(Arrays.toString(results.toArray()));
            List<User> result = new ArrayList<User>();

            for (Object[] row : results) {
                System.out.println(Arrays.toString(row));
                User user = new User();
                user.setUserId((int) row[0]);
                user.setUsername((String) row[1]);
                user.setFirstname((String) row[2]);
                user.setLastname((String) row[3]);
                user.setEmail((String) row[4]);
                user.setStreet((String) row[5]);
                user.setStreetNumber((String) row[6]);
                user.setZip((String) row[7]);
                user.setCity((String) row[8]);
                Position userPosition = new PositionConverter().convertToEntityAttribute((byte[]) row[9]);
                user.setPosition(userPosition);

                result.add(user);
            }

            List<User> drivers = new ArrayList<User>();
            for (User u : result) {
                var ttPk = new TimetablePk(u.getUserId(), weekday);

                if (u.getUserId() != userId) {
                    List<Integer> weekdays = _timetableDao.getWeekdaysForUserId(u.getUserId());
                    var timetable = _timetableDao.getById(ttPk);

                    if (weekdays.contains(weekday)) {
                        switch (journeyType) {
                            case 0:
                                if (timetable.get().getStartTime().isBefore(LocalTime.parse(starttime).plusMinutes(1)))
                                    drivers.add(u);
                                break;
                            case 1:
                                if (timetable.get().getEndTime().isAfter(LocalTime.parse(endtime).minusMinutes(1)))
                                    drivers.add(u);
                                break;
                            case 2:
                                if (timetable.get().getStartTime().isBefore(LocalTime.parse(starttime).plusMinutes(1))
                                        && timetable.get().getEndTime()
                                                .isAfter(LocalTime.parse(endtime).minusMinutes(1))) {
                                    drivers.add(u);
                                }
                                break;
                        }
                    }
                }
            }

            return drivers;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void update(User u) {
        try {
            _em.merge(u);
            _em.flush();
        } catch (Throwable thr) {
            throw new RuntimeException("ERROR updating user" + thr.getMessage());
        }
    }
}
