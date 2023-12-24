package com.socialnetwork.socialnetwork.events;

import com.socialnetwork.socialnetwork.domain.Utilizator;

public class UserChangeEvent extends SocialNetworkEvent {
    private final Utilizator newu1;
    private final Utilizator oldu2;

    public UserChangeEvent(EventType eventType, Utilizator newu1, Utilizator oldu2) {
        super(eventType);
        this.newu1 = newu1;
        this.oldu2 = oldu2;
    }

    public Utilizator getU1() {
        return newu1;
    }

    public Utilizator getU2() {
        return oldu2;
    }
}


