package shashank.mysms.android;

import android.Manifest.permission;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import shashank.mysms.R;
import shashank.mysms.model.Sms;
import shashank.mysms.adapter.SmsAdapter;
import shashank.mysms.database.SmsDBHelper;
import shashank.mysms.util.Constants;
import shashank.mysms.util.Contacts;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private List<Sms> smsDataList, smsListWithoutSpam;
    private RecyclerView messageList;
    private SmsDBHelper smsDBHelper;
    private SmsAdapter mAdapter;
    private FloatingActionButton fab;
    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageList = (RecyclerView) findViewById(R.id.message_list);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        messageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        messageList.setLayoutManager(linearLayoutManager);
        if (smsDBHelper == null)
            smsDBHelper = new SmsDBHelper(this);
        fab.setOnClickListener(this);
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            checkpermission();
        } else {
            if (smsDBHelper.getCount() == 0) {
                Contacts contacts = new Contacts();
                contacts.getContacts(MainActivity.this);
                getMessages();
            } else {
                getMessagesFromDb();
            }
        }
    }


    //For Marsh mellow
    private void checkpermission() {
        int smsPermission = ContextCompat.checkSelfPermission(this,
                permission.READ_SMS);
        int contactsPermission = ContextCompat.checkSelfPermission(this,
                permission.READ_CONTACTS);
        if (smsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission.READ_SMS},
                    Constants.MY_PERMISSION_READ_SMS);
        } else if (contactsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission.READ_CONTACTS},
                    Constants.MY_PERMISSION_READ_CONTACTS);
        } else {
            if (smsDBHelper.getCount() == 0) {
                Contacts contacts = new Contacts();
                contacts.getContacts(MainActivity.this);
                getMessages();
            } else
                getMessagesFromDb();
        }
    }

    //Receiving permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSION_READ_SMS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkpermission();
                } else {
                    fab.setEnabled(false);
                    new AlertDialog.Builder(this)
                            .setTitle("My SMS")
                            .setMessage("Please enable read SMS permission to use this app")
                            .setPositiveButton(android.R.string.yes, new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();
                }
                break;

            case Constants.MY_PERMISSION_READ_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkpermission();
                } else {
                    fab.setEnabled(false);
                    new AlertDialog.Builder(this)
                            .setTitle("My SMS")
                            .setMessage("Please enable read contacts permission to use this app")
                            .setPositiveButton(android.R.string.yes, new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();
                }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void getMessagesFromDb() {
        smsDataList = new ArrayList<>(smsDBHelper.getAllSms());
        mAdapter = new SmsAdapter(smsDataList);
        messageList.setAdapter(mAdapter);
    }

    //Called the first time app starts to fetch sms from phone
    private void getMessages() {
        smsDataList = new ArrayList<>();
        Uri uri = Uri.parse(Constants.FETCH_SMS_URL);
        String[] reqCols = new String[]{"_id", "address", "body", "date"};
        Cursor c = getContentResolver().query(uri, reqCols, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    Sms sms = new Sms();
                    Date df = new java.util.Date(c.getLong(3));
                    int date = Integer.parseInt(new SimpleDateFormat("dd").format(df));
                    int month = Integer.parseInt(new SimpleDateFormat("MM").format(df));
                    String time = new SimpleDateFormat("h:mma").format(df);
                    sms.setId(c.getLong(0));
                    sms.setAddress(c.getString(1));
                    sms.setBody(c.getString(2));
                    sms.setTime(time);
                    sms.setMonth(month);
                    sms.setDate(date);
                    sms.setIsSpam(checkIfSpam(sms.getAddress()));
                    smsDataList.add(sms);
                } while (c.moveToNext());
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    smsDBHelper.insertValues(smsDataList);
                }
            }).start();
            c.close();
            mAdapter = new SmsAdapter(smsDataList);
            messageList.setAdapter(mAdapter);
        }
    }

    private boolean checkIfSpam(String address) {
        boolean isNotSpam;
        if (Pattern.matches("[a-zA-Z-]+", address) == false) {
            Log.e("Sms adapter", "Is this ever entering?");
            isNotSpam = Contacts.contactNames.contains(address);
            if (isNotSpam)
                return false;
            else
                return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                new AlertDialog.Builder(this)
                        .setTitle("My SMS")
                        .setMessage("Are you sure you want to delete all spam?")
                        .setPositiveButton(android.R.string.yes, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new DeleteSpam().execute();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
                break;
        }
    }

    private class DeleteSpam extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Deleting");
            dialog.setInverseBackgroundForced(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        /**
         * This method deletes all spam messages in background
         *
         * @param params
         * @return null
         */
        @Override
        protected Void doInBackground(Void... params) {
            smsListWithoutSpam = new ArrayList<>();
            for (Sms sms : smsDataList) {
                if (!sms.getisSpam()) {
                    smsListWithoutSpam.add(sms);
                } else {
                    smsDBHelper.deleteTask(sms.getId());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            mAdapter = new SmsAdapter(smsListWithoutSpam);
            messageList.swapAdapter(mAdapter, false);
        }
    }
}
