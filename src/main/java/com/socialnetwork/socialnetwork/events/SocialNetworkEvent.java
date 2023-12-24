package com.socialnetwork.socialnetwork.events;

public class SocialNetworkEvent implements Event {
private final EventType eventType;

    public SocialNetworkEvent(EventType eventType) {
        this.eventType = eventType;

    }

    public EventType getEventType() {
        return eventType;
    }
}
