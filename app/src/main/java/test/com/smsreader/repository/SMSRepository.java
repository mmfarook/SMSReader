package test.com.smsreader.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.arch.paging.PositionalDataSource;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import test.com.smsreader.R;
import test.com.smsreader.model.HeaderMessage;
import test.com.smsreader.model.Message;
import test.com.smsreader.model.SMSMessage;
import test.com.smsreader.utils.Utils;

/**
 * Created by mmdfarook on 21/02/19.
 */

public class SMSRepository {

    private static final String TAG = "SMSRepository";
    private static String SMS_URI_INBOX = "content://sms/inbox";
    private static final int PAGE_SIZE = 20;
    private Application mApplication;
    private ContentResolver mContentResolver;
    private LiveData<PagedList<Message>> smsMessages;

    public SMSRepository(Application application, ContentResolver contentResolver) {
        this.mApplication = application;
        this.mContentResolver = contentResolver;
    }

    class MessageDataSourceFactory extends DataSource.Factory<Integer, Message> {
        MessageDataSourceFactory() {

        }

        @Override
        public DataSource<Integer, Message> create() {
            return new MessageDataSource();
        }
    }

    class MessageDataSource extends PositionalDataSource<Message> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Message> callback) {
            callback.onResult(fetchSMS(params.requestedLoadSize, params.requestedStartPosition), 0);
        }

        @Override
        public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Message> callback) {
            callback.onResult(fetchSMS(params.loadSize, params.startPosition));
        }
    }

    public void readSMS() {
        if (smsMessages == null) {
            PagedList.Config config = new PagedList.Config.Builder().setPageSize(PAGE_SIZE).setEnablePlaceholders(false).build();
            smsMessages = new LivePagedListBuilder<>(new MessageDataSourceFactory(), config).build();
        }
    }

    public List<Message> fetchSMS(int limit, int offset) {
        Uri uri = Uri.parse(SMS_URI_INBOX);
        String filter = Telephony.Sms.DATE + ">=" + Utils.getYesterday().getTime();
        Cursor cur = null;
        try {
            cur = mContentResolver.query(uri, new String[]{Telephony.Sms._ID, Telephony.Sms.ADDRESS,
                    Telephony.Sms.BODY, Telephony.Sms.DATE}, filter, null,
                    Telephony.Sms.DATE + " desc limit " + limit + " offset " + offset);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    int idIndex = cur.getColumnIndex(Telephony.Sms._ID);
                    int addressIndex = cur.getColumnIndex(Telephony.Sms.ADDRESS);
                    int bodyIndex = cur.getColumnIndex(Telephony.Sms.BODY);
                    int dateIndex = cur.getColumnIndex(Telephony.Sms.DATE);
                    String[] headers = mApplication.getResources().getStringArray(R.array.headers_array);
                    int headersLength = headers.length;
                    boolean[] isHeaderAdded = new boolean[headersLength];
                    List messageList = new ArrayList();
                    do {
                        int id = cur.getInt(idIndex);
                        String address = cur.getString(addressIndex);
                        String body = cur.getString(bodyIndex);
                        long date = cur.getLong(dateIndex);
                        HeaderMessage headerMessage = getHeaderMessage(date, isHeaderAdded, headers);
                        SMSMessage message = new SMSMessage(id, address, body, date);
                        if (headerMessage != null) {
                            messageList.add(headerMessage);
                        }
                        messageList.add(message);
                    } while (cur.moveToNext());
                    return messageList;
               }
            }
        } catch (Exception ex) {
            Log.d(TAG, "unable to read sms messages:" + ex);
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }
        return Collections.EMPTY_LIST;
    }

    private HeaderMessage getHeaderMessage(long date, boolean[] isHeaderAdded, String[] headers) {
        int[] times = Utils.splitSeconds((System.currentTimeMillis() - date) / 1000);
        int headersLength = isHeaderAdded.length;
        HeaderMessage headerMessage = null;
        if (times[0] >= 1 && !isHeaderAdded[headersLength - 1]) {
            headerMessage = new HeaderMessage(headers[headersLength - 1]);
            isHeaderAdded[headersLength - 1] = true;
        } else if (!isHeaderAdded[0] && times[1] <= 1) {
            headerMessage = new HeaderMessage(headers[0]);
            isHeaderAdded[0] = true;
        } else if (!isHeaderAdded[1] && times[1] > 1 && times[1] <= 2) {
            headerMessage = new HeaderMessage(headers[1]);
            isHeaderAdded[1] = true;
        } else if (!isHeaderAdded[2] && times[1] > 2 && times[1] <= 3) {
            headerMessage = new HeaderMessage(headers[2]);
            isHeaderAdded[2] = true;
        } else if (!isHeaderAdded[3] && times[1] > 3 && times[1] <= 6) {
            headerMessage = new HeaderMessage(headers[3]);
            isHeaderAdded[3] = true;
        } else if (!isHeaderAdded[4] && times[1] > 6 && times[1] <= 12) {
            headerMessage = new HeaderMessage(headers[4]);
            isHeaderAdded[4] = true;
        }
        return headerMessage;
    }

    public LiveData<PagedList<Message>> getMessages() {
        return smsMessages;
    }
}
