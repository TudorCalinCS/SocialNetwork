package com.socialnetwork.socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class Message extends Entity<UUID> {
    private Utilizator from;
    private Utilizator to;
    private String message;
    private LocalDateTime data;
    private Message reply = null;

    public Message(Utilizator from, Utilizator to, String message, LocalDateTime data, Message reply) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
        this.reply = reply;
        if (this.getId() == null)
            this.setId(UUID.randomUUID());
    }

    public Message(Utilizator from, Utilizator to, String message, LocalDateTime data) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
        this.reply = null;
        if (this.getId() == null)
            this.setId(UUID.randomUUID());

    }

    public Message(Utilizator from, Utilizator to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = LocalDateTime.now();
        this.reply = null;
        if (this.getId() == null)
            this.setId(UUID.randomUUID());
    }

    public Message(Utilizator u1, Utilizator u2, String message, LocalDateTime now, Optional<Message> replymessage) {
        super();
        this.from = u1;
        this.to = u2;
        this.message = message;
        this.data = now;
        if (this.getId() == null)
            this.setId(UUID.randomUUID());
        //this.reply=replymessage;
    }

    public Utilizator getUser1() {
        return from;
    }

    public LocalDateTime getDate() {
        return data;
    }

    public Utilizator getUser2() {
        return to;
    }

    public String getText() {
        return message;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    public Message getReply() {
        return reply;
    }

    public Utilizator getOtherUser(Utilizator utilizator) {
        if (from.equals(utilizator))
            return to;
        else if (to.equals(utilizator))
            return from;
        return null;
    }
}
