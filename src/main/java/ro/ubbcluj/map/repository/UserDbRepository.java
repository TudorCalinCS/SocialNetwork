package ro.ubbcluj.map.repository;
import ro.ubbcluj.map.domain.Utilizator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class UserDbRepository implements Repository<UUID,Utilizator> {
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
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("SELECT * FROM Utilizatori WHERE id=?");)
        {
            //statement.setLong(1,id);
            statement.setObject(1,id);
            ResultSet r = statement.executeQuery();
            if (r.next()){
                String FirstName = r.getString("FirstName");
                String LastName = r.getString("LastName");
                Utilizator u1 = new Utilizator(FirstName, LastName);
                u1.setId(UUID.randomUUID());
                return Optional.of(u1);
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Utilizator> findAll() {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("SELECT * FROM Utilizatori");)
        {
            ArrayList<Utilizator> list = new ArrayList<>();
            ResultSet r = statement.executeQuery();
            while (r.next()){
                String FirstName = r.getString("FirstName");
                String LastName = r.getString("LastName");
                Utilizator u1 = new Utilizator(FirstName, LastName);
                u1.setId(UUID.randomUUID());
                list.add(u1);
            }
            return list;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("INSERT INTO Utilizatori(FirstName,LastName,UUID) VALUES (?,?,?)");)
        {
            statement.setString(1,entity.getFirstName());
            statement.setString(2,entity.getLastName());
            statement.setObject(3,UUID.randomUUID());
            //statement.setInt(3,entity.getYear());
            int affectedRows = statement.executeUpdate();
            return affectedRows!=0? Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }    }

    @Override
    public Optional<Utilizator> delete(UUID uuid) {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("DELETE FROM Utilizatori WHERE UUID = ?");)
        {
            var cv = findOne(uuid);
            //statement.setLong(1,uuid);
            statement.setObject(1,uuid);
            int affectedRows = statement.executeUpdate();
            return affectedRows==0? Optional.empty():cv;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> update(Utilizator entity) {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("UPDATE Utilizatori SET FirstName = ?, LastName = ? WHERE id = ?");)
        {
            statement.setString(1,entity.getFirstName());
            statement.setString(2,entity.getLastName());
            statement.setObject(3,entity.getId());
            int affectedRows = statement.executeUpdate();
            return affectedRows!=0? Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
