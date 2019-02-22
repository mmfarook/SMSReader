package test.com.smsreader;


import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import test.com.smsreader.adapter.MessageAdapter;
import test.com.smsreader.model.HeaderMessage;
import test.com.smsreader.model.Message;
import test.com.smsreader.model.SMSMessage;
import test.com.smsreader.receiver.SMSReceiver;
import test.com.smsreader.viewmodel.SMSViewModel;
import test.com.smsreader.viewmodel.SMSViewModelFactory;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 1000;
    private SMSViewModel mSmsViewModel;
    private MessageAdapter mMessageAdapter;
    private Intent mStartupIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartupIntent = getIntent();
        final DiffUtil.ItemCallback<Message> diffCallback = new DiffUtil.ItemCallback<Message>() {
            @Override
            public boolean areItemsTheSame(Message oldItem, Message newItem) {
                if (oldItem instanceof HeaderMessage && newItem instanceof HeaderMessage) {
                    return TextUtils.equals(oldItem.getMessage(), newItem.getMessage());
                }
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(Message oldItem, Message newItem) {
                return areItemsTheSame(oldItem, newItem);

            }
        };

        mMessageAdapter = new MessageAdapter(diffCallback);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mMessageAdapter);
        mSmsViewModel = ViewModelProviders.of(this, new SMSViewModelFactory(getApplication(), getContentResolver())).get(SMSViewModel.class);
        if (isSmsPermissionGranted()) {
            readSMS();
        } else {
            showDiaglogOrrequestPermission();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mStartupIntent = intent;
        if (isSmsPermissionGranted()) {
            readSMS();
        } else {
            showDiaglogOrrequestPermission();
        }
    }


    private void readSMS() {
        mSmsViewModel.readSMS();
        mSmsViewModel.getMessages().observe(this, new Observer<PagedList<Message>>() {
            @Override
            public void onChanged(@Nullable PagedList<Message> messages) {
                if (mStartupIntent != null && !messages.isEmpty()) {
                    Intent intent = mStartupIntent;
                    mStartupIntent = null;
                    Uri uri = intent.getData();
                    if (uri != null) {
                        if (SMSReceiver.NOTIFY_SCHEME.equals(uri.getScheme()) && SMSReceiver.HOST.equals(uri.getHost())) {
                            String sender = uri.getQueryParameter("sender");
                            for (Message message : messages) {
                                if (message instanceof SMSMessage) {
                                    String address = ((SMSMessage) message).getAddress();
                                    if (!TextUtils.isEmpty(sender) && !TextUtils.isEmpty(address)) {
                                        sender = sender.trim();
                                        address = address.trim();
                                        if (address.indexOf(sender) != -1 || sender.indexOf(address) != -1) {
                                            message.setHighlight(true);
                                            break;
                                        } else {
                                            message.setHighlight(false);
                                        }
                                    } else {
                                        message.setHighlight(false);
                                    }
                                }
                            }
                        }
                    }
                }
                mMessageAdapter.submitList(messages);
            }
        });
    }

    public boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void showDiaglogOrrequestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
            showRequestPermissionsInfoAlertDialog();
        } else {
            requestSmsPermission();
        }
    }

    private void requestSmsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, SMS_PERMISSION_CODE);
    }

    public void showRequestPermissionsInfoAlertDialog() {
        showRequestPermissionsInfoAlertDialog(true);
    }

    public void showRequestPermissionsInfoAlertDialog(final boolean makeSystemRequest) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_alert_dialog_title); // Your own title
        builder.setMessage(R.string.permission_dialog_message);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (makeSystemRequest) {
                    requestSmsPermission();
                }
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SMS_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    readSMS();
                } else {
                    finish();
                }
            }
            return;
        }

    }
}
