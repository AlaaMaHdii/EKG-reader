package com.hmaar.sundhed.model;

import com.hmaar.sundhed.controller.Observer;


public interface Subject {
    void registerObserver(Observer o);
    void unregisterObserver(Observer o);
    void notifyObservers();
}
