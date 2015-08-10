package ru.android.develop.easybrash.yad;

import android.database.Observable;
import android.os.AsyncTask;
import android.util.Log;

import ru.android.develop.easybrash.yad.communicator.BackendCommunicator;
import ru.android.develop.easybrash.yad.communicator.CommunicatorFactory;

/**
 * Created by tagnik'zur on 10.08.2015.
 */
public class Model {
    private static final String LOG_TAG = "Model";

    private final SignInObservable mObservable = new SignInObservable();
    private SignInTask mSignInTask;
    private boolean mIsWorking;

    public Model() {
        Log.i(LOG_TAG, "new Instance");
    }

    public void signIn(final String userName, final String password) {
        if (mIsWorking) {
            return;
        }

        mObservable.notifyStarted();

        mIsWorking = true;
        mSignInTask = new SignInTask(userName, password);
        mSignInTask.execute();
    }

    public void stopSignIn() {
        if (mIsWorking) {
            mSignInTask.cancel(true);
            mIsWorking = false;
        }
    }

    public void registerObserver(final Observer observer) {
        mObservable.registerObserver(observer);
        if (mIsWorking) {
            observer.onSignInStarted(this);
        }
    }

    public void unregisterObserver(final Observer observer) {
        mObservable.unregisterObserver(observer);
    }

    private class SignInTask extends AsyncTask<Void, Void, Boolean> {
        private String mUserName;
        private String mPassword;

        public SignInTask(final String userName, final String password) {
            mUserName = userName;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(final Void... params) {
            final BackendCommunicator communicator = CommunicatorFactory.createBackendCommunicator();

            try {
                return communicator.postSignIn(mUserName, mPassword);
            } catch (InterruptedException e) {
                Log.i(LOG_TAG, "Sign in interrupted");
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mIsWorking = false;

            if (success) {
                mObservable.notifySucceeded();
            } else {
                mObservable.notifyFailed();
            }
        }
    }

    public interface Observer {
        void onSignInStarted(Model model);

        void onSignInSucceeded(Model model);

        void onSignInFailed(Model model);
    }

    private class SignInObservable extends Observable<Observer> {
        public void notifyStarted() {
            for (final Observer observer : mObservers) {
                observer.onSignInStarted(Model.this);
            }
        }

        public void notifySucceeded() {
            for (final Observer observer : mObservers) {
                observer.onSignInSucceeded(Model.this);
            }
        }

        public void notifyFailed() {
            for (final Observer observer : mObservers) {
                observer.onSignInFailed(Model.this);
            }
        }
    }
}