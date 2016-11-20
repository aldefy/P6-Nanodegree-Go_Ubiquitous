package techgravy.sunshine.utils;

import android.util.Log;

import techgravy.sunshine.utils.logger.LogLevel;
import techgravy.sunshine.utils.logger.Logger;
import timber.log.Timber;

/**
 * Created by aditlal on 14/04/16.
 */
public class LoggerTree extends Timber.Tree {

    public LoggerTree() {
        Logger.init("Sunshine")
                .methodCount(4)
                .hideThreadInfo()
                .logLevel(LogLevel.FULL);

    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        Logger.t(tag);
        switch (priority) {
            case Log.VERBOSE:
                Logger.v(message);
                break;
            case Log.DEBUG:
                Logger.d(message);
                break;
            case Log.INFO:
                Logger.i(message);
                break;
            case Log.WARN:
                Logger.w(message);
                break;
            case Log.ERROR:
                Logger.e(t, message);
                break;
            case Log.ASSERT:
                Logger.wtf(message);
                break;
        }

    }
}