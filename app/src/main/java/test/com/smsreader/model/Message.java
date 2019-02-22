package test.com.smsreader.model;

/**
 * Created by mmdfarook on 21/02/19.
 */

public abstract class Message {
    public static final int MESSAGE_HEDAER = 1000;
    public static final int MESSAGE_SMS = 1001;
    protected int id;
    protected int type;
    protected String message;
    protected boolean isHighlight;

    Message(int id, String message, int type) {
        this.message = message;
        this.type = type;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    public boolean isHighlight() {
        return isHighlight;
    }

    public void setHighlight(boolean highlight) {
        isHighlight = highlight;
    }
}
