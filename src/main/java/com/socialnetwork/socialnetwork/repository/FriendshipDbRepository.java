package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.domain.Entity;
import com.socialnetwork.socialnetwork.domain.FriendshipStatus;
import com.socialnetwork.socialnetwork.domain.Utilizator;
import com.socialnetwork.socialnetwork.domain.Prietenie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FriendshipDbRepository implements PagingRepository<UUID, Prietenie> {
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
            statement.setObject(1, id);
            ResultSet r = statement.executeQuery();
            if (r.next()) {
                String FirstNameU1 = r.getString("FirstNameU1");
                String LastNameU1 = r.getString("LastNameU1");
                Utilizator u1 = new Utilizator(FirstNameU1, LastNameU1);
                String FirstNameU2 = r.getString("FirstNameU2");
                String LastNameU2 = r.getString("LastNameU2");
                Utilizator u2 = new Utilizator(FirstNameU2, LastNameU2);
                FriendshipStatus status = FriendshipStatus.valueOf(r.getString(7));
                Prietenie p1 = new Prietenie(u1, u2, status);
                p1.setId(id);
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
                FriendshipStatus status = FriendshipStatus.valueOf(r.getString(7));
                Prietenie p1 = new Prietenie(u1, u2, status);
                //Prietenie p1 = new Prietenie(u1, u2);
                p1.setId((UUID) r.getObject("UUID"));
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
             PreparedStatement statement = connection.prepareStatement("INSERT INTO Prietenii(FirstNameU1,LastNameU1,FirstNameU2,LastNameU2,UUID,friendsFrom,friendshipStatus) VALUES (?,?,?,?,?,?,?)");) {
            statement.setString(1, entity.getUser1().getFirstName());
            statement.setString(2, entity.getUser1().getLastName());
            statement.setString(3, entity.getUser2().getFirstName());
            statement.setString(4, entity.getUser2().getLastName());
            statement.setObject(5, UUID.randomUUID());
            statement.setTimestamp(6, Timestamp.valueOf(entity.getDate()));
            statement.setString(7, entity.getFriendshipStatus().name());
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
    public Optional<Prietenie> update(Prietenie entity, String newF, String newL) {
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


    public Optional<Prietenie> acceptFriendRequest(UUID uuid) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE Prietenii SET friendshipStatus = 'CONFIRMED' WHERE UUID = ?");) {
            statement.setObject(1, uuid);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 0) {
                System.out.println("status changed");
                return findOne(uuid);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<Prietenie> rejectFriendRequest(UUID uuid) {
        delete(uuid);
        return Optional.empty();
    }

    @Override
    public Page<Prietenie> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Prietenie> findAllFriends(Pageable pageable, Entity E) {
        List<Prietenie> prietenieList = new ArrayList<>();
        int totalCount = 0;

        try (Connection connection = DriverManager.getConnection(this.url, this.user, this.password);
             PreparedStatement pagePreparedStatement = connection.prepareStatement("SELECT * FROM prietenii LIMIT ? OFFSET ?");
             PreparedStatement countPreparedStatement = connection.prepareStatement("SELECT COUNT(*) AS count FROM prietenii")) {

            // Setarea parametrilor pentru interogarea de paginare
            pagePreparedStatement.setInt(1, pageable.getPageSize());
            pagePreparedStatement.setInt(2, pageable.getPageSize() * (pageable.getPageNumber()));

            // Executarea interogării pentru paginare și preluarea rezultatelor
            try (ResultSet r = pagePreparedStatement.executeQuery()) {
                while (r.next()) {
                    String FirstNameU1 = r.getString("FirstNameU1");
                    String LastNameU1 = r.getString("LastNameU1");
                    Utilizator u1 = new Utilizator(FirstNameU1, LastNameU1);
                    String FirstNameU2 = r.getString("FirstNameU2");
                    String LastNameU2 = r.getString("LastNameU2");
                    Utilizator u2 = new Utilizator(FirstNameU2, LastNameU2);
                    FriendshipStatus status = FriendshipStatus.valueOf(r.getString(7));
                    Prietenie p1 = new Prietenie(u1, u2, status);
                    p1.setId((UUID) r.getObject("UUID"));
                    if(p1.getUser1().equals(E)||p1.getUser2().equals(E))
                        prietenieList.add(p1);
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

        return new Page<>(prietenieList, totalCount);
    }

}


