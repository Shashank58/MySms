package shashank.mysms.util;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shashankm on 15/01/16.
 */
public class Contacts {
    public static List<String> contactNames;

    /**
     * This class is used to get the contact list of user to
     * check if the address number of message exists in the user's contact
     * list.
     * @param activity
     */

    public void getContacts(Activity activity){
        contactNames = new ArrayList<>();
        String phoneNumber;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Cursor cursor = activity.getContentResolver().query(CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0){
                while (cursor.moveToNext()){
                    String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                    int hasPhoneNumber = Integer.parseInt
                            (cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
                    if (hasPhoneNumber > 0) {
                        Cursor phoneCursor = activity.getContentResolver()
                                .query(PhoneCONTENT_URI, null, Phone_CONTACT_ID +
                                        " = ?", new String[]{contact_id}, null);
                        if (phoneCursor != null) {
                            while (phoneCursor.moveToNext()) {
                                phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                                phoneNumber = phoneNumber.replace(" ", "");
                                contactNames.add(phoneNumber);
                            }
                            phoneCursor.close();
                        }
                    }
                }
            }
            cursor.close();
        }
    }
}
