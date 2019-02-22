package test.com.smsreader.viewmodel;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.ContentResolver;

/**
 * Created by mmdfarook on 22/02/19.
 */

public class SMSViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private ContentResolver mContentResolver;


    public SMSViewModelFactory(Application application, ContentResolver contentResolver) {
        mApplication = application;
        mContentResolver = contentResolver;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new SMSViewModel(mApplication, mContentResolver);
    }
}

