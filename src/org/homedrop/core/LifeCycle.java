package org.homedrop.core;

/** Defines lifecycle of all managers. For proper resources management*/
public interface LifeCycle {
    void onStart();
    void onPause();
    void onResume();
    void onExit();
}
