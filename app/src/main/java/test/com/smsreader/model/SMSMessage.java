package test.com.smsreader.model;

/**
 * Created by mmdfarook on 21/02/19.
 */

public class SMSMessage extends Message{

    private final String address;
    private final Long time;

    public SMSMessage(int id, String address, String msg, Long time) {
        super(id, msg, Message.MESSAGE_SMS);
        this.address = address;
        this.time = time;

    }

    public String getAddress() {
        return address;
    }

    public Long getTime() {
        return time;
    }

}
