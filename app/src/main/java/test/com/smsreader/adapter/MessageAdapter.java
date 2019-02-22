package test.com.smsreader.adapter;

import android.arch.paging.PagedListAdapter;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import test.com.smsreader.R;
import test.com.smsreader.model.HeaderMessage;
import test.com.smsreader.model.Message;
import test.com.smsreader.model.SMSMessage;

/**
 * Created by mmdfarook on 21/02/19.
 */

public class MessageAdapter extends PagedListAdapter<Message, MessageAdapter.MessageViewHolder> {

    public MessageAdapter(DiffUtil.ItemCallback<Message> callback) {
        super(callback);
    }

    abstract class MessageViewHolder extends RecyclerView.ViewHolder {
        public MessageViewHolder(View itemView) {
            super(itemView);
        }

        abstract void bindTo(MessageViewHolder messageViewHolder,Message message);
    }

    class HeaderMessageViewHolder extends MessageViewHolder {
        private TextView headerView;

        public HeaderMessageViewHolder(View itemView) {
            super(itemView);
            headerView = itemView.findViewById(R.id.header);
        }

        @Override
        void bindTo(MessageViewHolder messageViewHolder, Message message) {
            HeaderMessage headerMessage = (HeaderMessage) message;
            HeaderMessageViewHolder holder = (HeaderMessageViewHolder) messageViewHolder;
            holder.headerView.setText(headerMessage.getMessage());
        }
    }

    class SMSMessageViewHolder extends MessageViewHolder {
        private View messageContainer;
        private TextView addressView;
        private TextView messageView;

        public SMSMessageViewHolder(View itemView) {
            super(itemView);
            this.messageContainer = itemView.findViewById(R.id.message_container);
            addressView = itemView.findViewById(R.id.address);
            messageView = itemView.findViewById(R.id.message);
        }

        @Override
        void bindTo(MessageViewHolder messageViewHolder, Message message) {
            SMSMessage smsMessage = (SMSMessage) message;
            SMSMessageViewHolder holder = (SMSMessageViewHolder) messageViewHolder;
            holder.addressView.setText(smsMessage.getAddress());
            holder.messageView.setText(smsMessage.getMessage());
            if (smsMessage.isHighlight()) {
                holder.addressView.setTextColor(Color.WHITE);
                holder.messageView.setTextColor(Color.WHITE);
                messageContainer.setBackgroundColor(Color.BLUE);
            } else {
                holder.addressView.setTextColor(Color.BLACK);
                holder.messageView.setTextColor(Color.BLACK);
                messageContainer.setBackgroundColor(Color.WHITE);

            }
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int viewId = viewType == Message.MESSAGE_HEDAER ? R.layout.header_view : R.layout.message_view;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(viewId, parent, false);
        if (viewType == Message.MESSAGE_SMS) {
            return new SMSMessageViewHolder(itemView);
        }
        return new HeaderMessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder mholder, int position) {
        Message message = getItem(position);
        mholder.bindTo(mholder, message);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = getItem(position);
        return message instanceof HeaderMessage ? Message.MESSAGE_HEDAER : Message.MESSAGE_SMS;
    }
}
