package test.com.smsreader.model;

import android.text.TextUtils;

/**
 * Created by mmdfarook on 21/02/19.
 */

public class HeaderMessage extends Message {
    public HeaderMessage(String msg) {
        super(-1, msg, MESSAGE_HEDAER);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof  HeaderMessage && TextUtils.equals(getMessage(), ((HeaderMessage) obj).getMessage());
    }
}
