package ro.ubbcluj.map.domain;

import java.time.LocalDateTime;
import java.util.UUID;


public class Prietenie extends Entity<UUID> {
    private Utilizator user1;
    private Utilizator user2;
    LocalDateTime date;

    public Prietenie(Utilizator user1, Utilizator user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.setId(UUID.randomUUID());
        this.date = LocalDateTime.now();
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Prietenie{" +
                "user1=" + user1.toString() +
                ", user2=" + user2.toString() +
                '}';
    }

    public Utilizator getUser1() {
        return user1;
    }

    public Utilizator getUser2() {
        return user2;
    }
}
