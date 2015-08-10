package ru.android.develop.easybrash.yad.communicator;

/**
 * Created by tagnik'zur on 10.08.2015.
 */
public interface BackendCommunicator {
    boolean postSignIn(String userName, String password) throws InterruptedException;
}
