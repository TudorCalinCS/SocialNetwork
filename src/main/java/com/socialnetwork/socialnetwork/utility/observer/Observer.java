package com.socialnetwork.socialnetwork.utility.observer;
import com.socialnetwork.socialnetwork.events.Event;
public interface Observer<E extends Event> {
    void update(E event);
}
