package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.domain.Entity;
import com.socialnetwork.socialnetwork.domain.Message;
import com.socialnetwork.socialnetwork.domain.Utilizator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class MessageDbRepository implements PagingRepository<UUID, Message> {
    private String url;
    private String user;
    private String password;

    public MessageDbRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public Optional<Message> findOne(UUID id) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages WHERE UUID=?");) {
            statement.setObject(1, id);
            ResultSet r = statement.executeQuery();
            if (r.next()) {
                String FirstNameU1 = r.getString("FirstNameU1");
                String LastNameU1 = r.getString("LastNameU1");
                Utilizator u1 = new Utilizator(FirstNameU1, LastNameU1);
                String FirstNameU2 = r.getString("FirstNameU2");
                String LastNameU2 = r.getString("LastNameU2");
                Utilizator u2 = new Utilizator(FirstNameU2, LastNameU2);
                String message = r.getString("message");
                Optional<Message> replymessage = findOne((UUID) r.getObject("replyuuid"));
                Message message1 = new Message(u1, u2, message, LocalDateTime.now(), replymessage);
                return Optional.of(message1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Message> findAll() {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages");) {
            ArrayList<Message> list = new ArrayList<>();
            ResultSet r = statement.executeQuery();
            while (r.next()) {
                String FirstNameU1 = r.getString("FirstNameU1");
                String LastNameU1 = r.getString("LastNameU1");
                Utilizator u1 = new Utilizator(FirstNameU1, LastNameU1);
                String FirstNameU2 = r.getString("FirstNameU2");
                String LastNameU2 = r.getString("LastNameU2");
                Utilizator u2 = new Utilizator(FirstNameU2, LastNameU2);
                String message = r.getString("message");
                LocalDateTime date = r.getTimestamp("date").toLocalDateTime();
                UUID replyUuid = (UUID) r.getObject("replyuuid");
                Optional<Message> replyMessage = (replyUuid != null) ? findOne(replyUuid) : Optional.empty();
                Message message1 = new Message(u1, u2, message, date, replyMessage);
                message1.setId((UUID) r.getObject("uuid"));
                list.add(message1);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Message> save(Message entity) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO messages(uuid,firstnameu1,lastnameu1,firstnameu2,lastnameu2,date,message,replyuuid) VALUES (?,?,?,?,?,?,?,?)");) {
            statement.setObject(1, entity.getId());
            statement.setString(2, entity.getUser1().getFirstName());
            statement.setString(3, entity.getUser1().getLastName());
            statement.setString(4, entity.getUser2().getFirstName());
            statement.setString(5, entity.getUser2().getLastName());
            statement.setTimestamp(6, Timestamp.valueOf(entity.getDate()));
            statement.setString(7, entity.getText());
            // Verificăm dacă mesajul are un răspuns și îl salvăm corect
            if (entity.getReply() != null) {
                // Salvăm mesajul de răspuns înainte de mesajul principal
                Optional<Message> savedReply = save(entity.getReply());
                if (savedReply.isEmpty()) {
                    return Optional.empty(); // În cazul în care salvarea răspunsului nu a reușit
                }
                statement.setObject(8, entity.getReply().getId());
            } else {
                statement.setObject(8, null); // Mesajul nu are răspuns
            }

            int affectedRows = statement.executeUpdate();
            return affectedRows != 0 ? Optional.empty() : Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> delete(UUID uuid) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM messages WHERE UUID = ?");) {
            var cv = findOne(uuid);
            statement.setObject(1, uuid);
            int affectedRows = statement.executeUpdate();
            return affectedRows == 0 ? Optional.empty() : cv;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Message> update(Message entity, String newF, String newL) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE messages SET replyuuid = ? WHERE UUID = ?");) {
            statement.setObject(1, entity.getReply().getId());
            statement.setObject(2, entity.getId());
            int affectedRows = statement.executeUpdate();
            return affectedRows != 0 ? Optional.empty() : Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Optional<Message> acceptFriendRequest(UUID uuid) {

        return Optional.empty();
    }

    public Optional<Message> rejectFriendRequest(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Page<Message> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Message> findAllFriends(Pageable pageable, Entity E) {
        return null;
    }
}

