package ro.ubbcluj.map;

import ro.ubbcluj.map.domain.Utilizator;
import ro.ubbcluj.map.repository.FriendshipDbRepository;
import ro.ubbcluj.map.repository.Repository;
import ro.ubbcluj.map.repository.UserDbRepository;
import ro.ubbcluj.map.service.Service;
import ro.ubbcluj.map.domain.Prietenie;

import java.util.UUID;

public class Main {

    public static void main(String[] args) {

        Repository<UUID, Utilizator> userRepo = new UserDbRepository("jdbc:postgresql://localhost:5432/SocialNetworkDB", "postgres", "password");
        Repository<UUID, Prietenie> friendshipRepo = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/SocialNetworkDB", "postgres", "password");

        Service service = new Service(userRepo, friendshipRepo);

        Ui ui = new Ui(service);
        ui.start();

        System.out.println("Done");
    }
}
