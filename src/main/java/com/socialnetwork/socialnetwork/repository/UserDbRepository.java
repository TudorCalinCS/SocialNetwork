package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.domain.Entity;
import com.socialnetwork.socialnetwork.domain.Utilizator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.socialnetwork.socialnetwork.utility.observer.BCrypt;

public class UserDbRepository implements PagingRepository<UUID, Utilizator> {
    private String url;
    private String user;
    private String password;

    public UserDbRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public Optional<Utilizator> findOne(UUID id) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Utilizatori WHERE UUID=?");) {
            //statement.setLong(1,id);
            statement.setObject(1, id);
            ResultSet r = statement.executeQuery();
            if (r.next()) {
                String FirstName = r.getString("FirstName");
                String LastName = r.getString("LastName");
                String Password = r.getString("password");
                Utilizator u1 = new Utilizator(FirstName, LastName, Password);
                u1.setId(id);
                return Optional.of(u1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Utilizator> findAll() {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Utilizatori");) {
            ArrayList<Utilizator> list = new ArrayList<>();
            ResultSet r = statement.executeQuery();
            while (r.next()) {
                String FirstName = r.getString("FirstName");
                String LastName = r.getString("LastName");
                String Password = r.getString("password");
                UUID uuid = (UUID) r.getObject("UUID");
                Utilizator u1 = new Utilizator(FirstName, LastName, Password);
                u1.setId(uuid);
                list.add(u1);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO Utilizatori(FirstName,LastName,UUID,password) VALUES (?,?,?,?)");) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setObject(3, UUID.randomUUID());
            String encrypted = BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt());
            statement.setString(4, encrypted);

            //BCrypt.
            int affectedRows = statement.executeUpdate();
            //BCrypt.hashpw
            return affectedRows != 0 ? Optional.empty() : Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> delete(UUID uuid) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM Utilizatori WHERE UUID = ?");) {
            var cv = findOne(uuid);
            statement.setObject(1, uuid);
            int affectedRows = statement.executeUpdate();
            return affectedRows == 0 ? Optional.empty() : cv;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> update(Utilizator entity, String newF, String newL) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE Utilizatori SET FirstName = ?, LastName = ? WHERE UUID = ?");) {
            statement.setString(1, newF);
            statement.setString(2, newL);
            statement.setObject(3, entity.getId());
            int affectedRows = statement.executeUpdate();
            return affectedRows != 0 ? Optional.empty() : Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> acceptFriendRequest(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<Utilizator> rejectFriendRequest(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Page<Utilizator> findAll(Pageable pageable) {
        List<Utilizator> utilizatorList = new ArrayList<>();
        int totalCount = 0;

        try (Connection connection = DriverManager.getConnection(this.url, this.user, this.password);
             PreparedStatement pagePreparedStatement = connection.prepareStatement("SELECT * FROM utilizatori LIMIT ? OFFSET ?");
             PreparedStatement countPreparedStatement = connection.prepareStatement("SELECT COUNT(*) AS count FROM utilizatori")) {

            // Setarea parametrilor pentru interogarea de paginare
            pagePreparedStatement.setInt(1, pageable.getPageSize());
            pagePreparedStatement.setInt(2, pageable.getPageSize() * (pageable.getPageNumber()));

            // Executarea interogării pentru paginare și preluarea rezultatelor
            try (ResultSet pageResultSet = pagePreparedStatement.executeQuery()) {
                while (pageResultSet.next()) {
                    UUID id = (UUID) pageResultSet.getObject("uuid");
                    String firstName = pageResultSet.getString("firstname");
                    String lastName = pageResultSet.getString("lastname");
                    String password = pageResultSet.getString("password");
                    Utilizator utilizator = new Utilizator(firstName, lastName, password);
                    utilizator.setId(id);
                    utilizatorList.add(utilizator);
                }
            }

            // Preluarea numărului total de elemente din tabel
            try (ResultSet countResultSet = countPreparedStatement.executeQuery()) {
                if (countResultSet.next()) {
                    totalCount = countResultSet.getInt("count");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new Page<>(utilizatorList, totalCount);
    }

    @Override
    public Page<Utilizator> findAllFriends(Pageable pageable, Entity E) {
        return null;
    }

}
