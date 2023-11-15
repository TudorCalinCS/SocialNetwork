package ro.ubbcluj.map.repository;

import ro.ubbcluj.map.domain.Prietenie;
import ro.ubbcluj.map.domain.Utilizator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class FriendshipDbRepository implements Repository<UUID, Prietenie> {
    private String url;
    private String user;
    private String password;

    public FriendshipDbRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public Optional<Prietenie> findOne(UUID id) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Prietenii WHERE UUID=?");) {
            //statement.setLong(1,id);
            statement.setObject(1, id);
            ResultSet r = statement.executeQuery();
            if (r.next()) {
                String FirstNameU1 = r.getString("FirstNameU1");
                String LastNameU1 = r.getString("LastNameU1");
                Utilizator u1 = new Utilizator(FirstNameU1, LastNameU1);
                String FirstNameU2 = r.getString("FirstNameU2");
                String LastNameU2 = r.getString("LastNameU2");
                Utilizator u2 = new Utilizator(FirstNameU1, LastNameU1);
                Prietenie p1 = new Prietenie(u1, u2);
                p1.setId(UUID.randomUUID());
                return Optional.of(p1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Prietenie> findAll() {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Prietenii");) {
            ArrayList<Prietenie> list = new ArrayList<>();
            ResultSet r = statement.executeQuery();
            while (r.next()) {
                String FirstNameU1 = r.getString("FirstNameU1");
                String LastNameU1 = r.getString("LastNameU1");
                Utilizator u1 = new Utilizator(FirstNameU1, LastNameU1);
                String FirstNameU2 = r.getString("FirstNameU2");
                String LastNameU2 = r.getString("LastNameU2");
                Utilizator u2 = new Utilizator(FirstNameU2, LastNameU2);
                Prietenie p1 = new Prietenie(u1, u2);
                p1.setId(UUID.randomUUID());
                list.add(p1);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Prietenie> save(Prietenie entity) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO Prietenii(FirstNameU1,LastNameU1,FirstNameU2,LastNameU2,UUID,friendsFrom) VALUES (?,?,?,?,?,?)");) {
            statement.setString(1, entity.getUser1().getFirstName());
            statement.setString(2, entity.getUser1().getLastName());
            statement.setString(3, entity.getUser2().getFirstName());
            statement.setString(4, entity.getUser2().getLastName());
            statement.setObject(5, UUID.randomUUID());
            statement.setTimestamp(6, Timestamp.valueOf(entity.getDate()));
            int affectedRows = statement.executeUpdate();
            return affectedRows != 0 ? Optional.empty() : Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Prietenie> delete(UUID uuid) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM Prietenii WHERE UUID = ?");) {
            var cv = findOne(uuid);
            statement.setObject(1, uuid);
            int affectedRows = statement.executeUpdate();
            return affectedRows == 0 ? Optional.empty() : cv;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Prietenie> update(Prietenie entity) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE Prietenii SET FirstNameU1 = ?, LastNameU1 = ?,FirstNameU2 = ?,LastNameU2 =? WHERE UUID = ?");) {
            statement.setString(1, entity.getUser1().getFirstName());
            statement.setString(2, entity.getUser1().getLastName());
            statement.setString(3, entity.getUser2().getFirstName());
            statement.setString(4, entity.getUser2().getLastName());
            statement.setObject(5, entity.getId());
            int affectedRows = statement.executeUpdate();
            return affectedRows != 0 ? Optional.empty() : Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

