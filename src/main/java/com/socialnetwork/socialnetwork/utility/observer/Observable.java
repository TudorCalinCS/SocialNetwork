package com.socialnetwork.socialnetwork.utility.observer;

import com.socialnetwork.socialnetwork.events.Event;

public interface Observable <E extends Event> {
    void addObserver(Observer<E> observer);
    void removeObserver(Observer<E> observer);
    void notify(E event);
}
