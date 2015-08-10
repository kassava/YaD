package ru.android.develop.easybrash.yad.communicator;

/**
 * Created by tagnik'zur on 10.08.2015.
 */
class BackendCommunicatorStub implements BackendCommunicator {
    private static final String VALID_USERNAME="user1";
    private static final String VALID_PASSWORD="qwerty";

    @Override
    public boolean postSignIn(final String userName, final String password) throws InterruptedException {
        Thread.sleep(8000);
        return VALID_USERNAME.equals(userName) && VALID_PASSWORD.equals(password);
    }
}
