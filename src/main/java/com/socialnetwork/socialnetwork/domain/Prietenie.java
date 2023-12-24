package com.socialnetwork.socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.UUID;


public class Prietenie extends Entity<UUID> {
    private Utilizator user1;
    private Utilizator user2;
    LocalDateTime date;

    private FriendshipStatus friendshipStatus;

    public Prietenie(Utilizator user1, Utilizator user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.setId(UUID.randomUUID());
        this.date = LocalDateTime.now();
        friendshipStatus = FriendshipStatus.PENDING;
    }
    public Prietenie(Utilizator user1, Utilizator user2,FriendshipStatus sfriendshipstatus) {
        this.user1 = user1;
        this.user2 = user2;
        this.setId(UUID.randomUUID());
        this.date = LocalDateTime.now();
        friendshipStatus = sfriendshipstatus;
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

    public FriendshipStatus getFriendshipStatus() {
        return friendshipStatus;
    }
    public void confirmStatus(){
        friendshipStatus=FriendshipStatus.CONFIRMED;
    }

    public Utilizator getOther(Utilizator u){
        if(user1.equals(u))
            return user2;
        if(user2.equals(u))
            return user1;
        return null;
    }
}
