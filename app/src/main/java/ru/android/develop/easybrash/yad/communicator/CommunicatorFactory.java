package ru.android.develop.easybrash.yad.communicator;

/**
 * Created by tagnik'zur on 10.08.2015.
 */
public class CommunicatorFactory {
    public static BackendCommunicator createBackendCommunicator() {
        return new BackendCommunicatorStub();
    }
}
