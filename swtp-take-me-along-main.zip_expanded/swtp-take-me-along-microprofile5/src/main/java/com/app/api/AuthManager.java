package com.app.api;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import com.app.dao.UserDao;

@Singleton
public class AuthManager {

    @Inject
    private UserDao _userDao;

    private final Map<String, UUID> _authorizedUsers = new ConcurrentHashMap<>();

    public AuthManager() {
        // TODO Auto-generated constructor stub
    }

    public boolean deauth(String username) {
        if (username == null)
            return false;

        return _authorizedUsers.remove(username) != null ? true : false;
    }

    public boolean deauth(UUID token) {
        if (token == null)
            return false;

        return _authorizedUsers.remove(getUsername(token)) != null ? true : false;
    }

    public UUID auth(String username, String password) {
        if (!_userDao.checkUserPassword(username, password)) {
            throw new RuntimeException("ERROR: username and password do not correspond");
        }

        UUID uuid = UUID.randomUUID();
        if (_authorizedUsers.putIfAbsent(username, uuid) == null) {
            return uuid;
        } else {
            deauth(username);
            throw new RuntimeException("ERROR: Already logged in");
        }
    }

    public UUID getUuid(String username) {
        return _authorizedUsers.get(username);
    }

    public String getUsername(UUID uuid) {
        Optional<String> username = _authorizedUsers.entrySet()
                .stream()
                .filter(entry -> uuid.equals(entry.getValue()))
                .map(Map.Entry::getKey).findFirst();

        return username.isPresent() ? username.get() : null;
    }

    public boolean isAuthorized(UUID uuid) {
        return _authorizedUsers.containsValue(uuid);
    }
}
