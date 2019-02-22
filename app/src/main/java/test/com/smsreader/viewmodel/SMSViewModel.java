package test.com.smsreader.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.content.ContentResolver;
import android.support.annotation.NonNull;

import test.com.smsreader.model.Message;
import test.com.smsreader.repository.SMSRepository;

/**
 * Created by mmdfarook on 21/02/19.
 */

public class SMSViewModel extends ViewModel {

    private SMSRepository smsRepository;
    public SMSViewModel(@NonNull Application application, @NonNull ContentResolver contentResolver) {
        smsRepository = new SMSRepository(application, contentResolver);
    }

    public void readSMS() {
        smsRepository.readSMS();
    }

    public LiveData<PagedList<Message>> getMessages() {
        return smsRepository.getMessages();
    }
}
