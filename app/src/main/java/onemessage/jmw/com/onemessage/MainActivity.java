package onemessage.jmw.com.onemessage;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends Activity implements IDataReceive {

    public static final int INVISIBLE_THEME = android.R.style.Theme_Translucent_NoTitleBar;
    public static final int VISIBLE_THEME = android.R.style.Theme_Black;
    public static final int REQUEST_SMS = 333;
    public static final int REQUEST_CONTACTS = 334;
    public static final int REQUEST_PHONE_STATE = 335;
    public static final int REQUEST_GPS = 336;
    public static final int PICK_CONTACT = 99;
    public static final String NUMBER = "number";
    public static final String MESSAGE = "message";
    public static final String ICON = "icon";
    public static final String GPS_TRIGGER = "%";
    public static final String LOCATION_MAP = "%location%";
    public static final String LATITUDE = "%latitude%";
    public static final String LONGITUDE = "%longitude%";
    public static final String BEARING = "%bearing%";
    public static final String CARDINAL = "%cardinal%";
    public static final String SPEED_MPH = "%speed_mph%";
    public static final String SPEED_FPS = "%speed_fps%";
    public static final String SPEED_MPS = "%speed_mps%";
    public static final String SPEED_KPH = "%speed_kph%";
    public static final String ALTITUDE_M = "%altitude_m%";
    public static final String ALTITUDE_F = "%altitude_f%";
    public static final String GPS_DUMP = "%gps_dump%";

    private EditText shortcutText;
    private EditText phoneNumberText;
    private EditText messageText;
    private Button addMessageButton;
    private Button addInsertButton;

    private String numberToSend;
    private String messageToSend;
    private int iconId;

    private Integer selectedIcon;
    private int selectedPosition = 0;

    // references to our images
    private static final int[] iconIds = {
            R.mipmap.black_icon,
            R.mipmap.white_icon,
            R.mipmap.red_icon,
            R.mipmap.orange_icon,
            R.mipmap.yellow_icon,
            R.mipmap.green_icon,
            R.mipmap.blue_icon,
            R.mipmap.purple_icon,
            R.mipmap.pink_icon,
            R.mipmap.brown_icon,
            R.mipmap.grey_icon,
            R.mipmap.navy_icon,
            R.mipmap.forest_icon,
            android.R.drawable.sym_action_call,
            android.R.drawable.sym_action_chat,
            android.R.drawable.sym_def_app_icon,
            android.R.drawable.sym_contact_card,
            android.R.drawable.sym_call_missed,
            android.R.drawable.sym_call_outgoing,
            android.R.drawable.sym_action_email,
            android.R.drawable.sym_call_incoming,
            android.R.drawable.stat_sys_headset,
            android.R.drawable.stat_sys_warning,
            android.R.drawable.stat_notify_sync_noanim,
            android.R.drawable.stat_sys_speakerphone,
            android.R.drawable.radiobutton_on_background,
            android.R.drawable.star_big_on,
            android.R.drawable.stat_notify_chat,
            android.R.drawable.ic_media_ff,
            android.R.drawable.ic_dialog_email
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        String number = intent.getStringExtra(NUMBER);
        String message = intent.getStringExtra(MESSAGE);
        int icon = intent.getIntExtra(ICON, iconIds[0]);

        if (number != null) {
            super.onCreate(savedInstanceState);
            send(number, message, icon);
        } else {
            beVisible(savedInstanceState);
        }
        if (ContextCompat.checkSelfPermission(this,  Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_STATE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String number = intent.getStringExtra(NUMBER);
        if (number != null) {
            setTheme(INVISIBLE_THEME);
            setVisible(false);
        } else {
            setTheme(VISIBLE_THEME);
            setVisible(true);
            checkIfAddMessageButtonEnabled();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        setTheme(INVISIBLE_THEME);
        setVisible(false);

    }

    private class ImageAdapter extends BaseAdapter {
        private Context mContext;

        ImageAdapter() {
            mContext = MainActivity.this;
        }

        public int getCount() {
            return iconIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return iconIds[position];
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }
            if (position == selectedPosition) {
                imageView.setBackgroundResource(R.color.selected);
            } else {
                imageView.setBackgroundResource(R.color.transparent);
            }
            imageView.setImageResource(iconIds[position]);
            return imageView;
        }
    }


    private void beVisible(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shortcutText = (EditText) findViewById(R.id.shortcutNameText);
        phoneNumberText = (EditText) findViewById(R.id.phoneNumberText);
        messageText = (EditText) findViewById(R.id.messageText);
        AllTextWatcher textWatcher = new AllTextWatcher();
        shortcutText.addTextChangedListener(textWatcher);
        phoneNumberText.addTextChangedListener(textWatcher);
        messageText.addTextChangedListener(textWatcher);

        addMessageButton = (Button) findViewById(R.id.newMessageButton);
        Button contactsButton = (Button) findViewById(R.id.contactsButton);
        GridView iconGrid = (GridView) findViewById(R.id.iconGrid);
        addMessageButton.setOnTouchListener(new AddMessageListener());
        contactsButton.setOnTouchListener(new ContactsAskListener());

        addInsertButton = (Button) findViewById(R.id.insertTemplateButton);
        addInsertButton.setOnTouchListener(new AddInsertListener());

        final ImageAdapter imageAdapter = new ImageAdapter();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        iconGrid.setColumnWidth(displaymetrics.widthPixels / 5);
        iconGrid.setAdapter(imageAdapter);
        iconGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                selectedPosition = position;
                selectedIcon = iconIds[position];
                imageAdapter.notifyDataSetChanged();
            }
        });
        selectedIcon = iconIds[selectedPosition];
    }


    private void checkIfAddMessageButtonEnabled() {
        boolean enabled =  (shortcutText.getText().length() > 0 &&
                phoneNumberText.getText().length() > 0 &&
                messageText.getText().length() > 0);
        addMessageButton.setEnabled(enabled);
    }

    private class AllTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkIfAddMessageButtonEnabled();
        }
    }

    private void addShortcut(String name, String number, String message, int iconId) {
        Intent shortcutIntent = new Intent(getApplicationContext(), MainActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);
        shortcutIntent.putExtra(NUMBER, number);
        shortcutIntent.putExtra(MESSAGE, message);
        shortcutIntent.putExtra(ICON, iconId);
        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                        iconId));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);

    }

    private class AddMessageListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                String name = shortcutText.getText().toString();
                String number = phoneNumberText.getText().toString();
                String message = messageText.getText().toString();
                if (message.contains(GPS_TRIGGER) &&
                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS}, REQUEST_GPS);
                } else {
                    addShortcut(name, number, message, selectedIcon);
                }
            }
            finishAffinity();
            setTheme(INVISIBLE_THEME);
            return false;
        }
    }

    private class AddInsertListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ChooseInsertDialog dialog = ChooseInsertDialog.newInstance(messageText);
                FragmentTransaction transaction = MainActivity.this.getFragmentManager().beginTransaction();
                dialog.show(transaction, "dialog");
            }

            return false;
        }
    }

    private class ContactsAskListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,  Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);
                }
            }
            return false;
        }
    }

    public void setDataString(String number) {
        if (number != null) {
            phoneNumberText.setText(number);
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
            if (reqCode == PICK_CONTACT) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    final ArrayList<PhoneEntry> numbers = retrieveContactNumber(contactData);
                    if (numbers == null || numbers.size() == 0) {
                        showToast(getString(R.string.noPhoneNumbersRetrieved), android.R.drawable.stat_notify_error);
                    } else if (numbers.size() == 1) {
                        phoneNumberText.setText(numbers.get(0).getNumber());
                    } else {
                        ChooseNumberTypeDialog chooseNumberPopup = ChooseNumberTypeDialog.newInstance(numbers);
                        FragmentTransaction transaction = MainActivity.this.getFragmentManager().beginTransaction();
                        chooseNumberPopup.show(transaction, "dialog");
                    }
                }
            }
    }

    private String getPhoneType(int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                return getString(R.string.workMobile);
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                return getString(R.string.mobile);
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                return getString(R.string.home);
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return getString(R.string.work);
            case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
                return getString(R.string.car);
            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                return getString(R.string.main);
            case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
                return getString(R.string.assistant);
            case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
                return getString(R.string.callback);
            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                return getString(R.string.other);
            case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
                return getString(R.string.custom);
            default:
                return "";
        }
    }

    private ArrayList<PhoneEntry> retrieveContactNumber(Uri uriContact) {
        String contactID;
        Cursor cursorID = null;
        try {
            cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID}, null, null, null);
            if (cursorID == null) {
                return null;
            }
            if (cursorID.moveToFirst()) {
                contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            } else {
                return null;
            }
        } finally {
            if (cursorID != null) {
                cursorID.close();
            }
        }
        Cursor cursorPhone = null;
        try {
            cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{contactID},
                null);
            if (cursorPhone == null) {
                return null;
            }
            ArrayList<PhoneEntry> numbers = new ArrayList<>();
            if (cursorPhone.moveToFirst()) {
                while (!cursorPhone.isAfterLast()) {
                    String contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
                    int type = cursorPhone.getInt(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    numbers.add(new PhoneEntry(contactNumber, getPhoneType(type)));
                    cursorPhone.moveToNext();
                }
            }
            return numbers;
        } finally {
            if (cursorPhone != null) {
                cursorPhone.close();
            }
        }
    }

    private void send(String number, String message, int iconId) {
        if (ContextCompat.checkSelfPermission(this,  Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS}, REQUEST_SMS);
            this.numberToSend = number;
            this.messageToSend = message;
            this.iconId = iconId;
        } else {
            sendSMS(number, message, iconId);
        }
    }

    private static boolean permissionGranted(int[] grantResults) {
        return grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    private void showToast(String message, int iconId) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.sent_toast,
                (ViewGroup) findViewById(R.id.custom_toast_layout));

        TextView text = (TextView) layout.findViewById(R.id.textToShow);
        text.setText(message);
        ImageView icon = (ImageView) layout.findViewById(R.id.toastImage);
        icon.setImageResource(iconId);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_SMS: {
                if (permissionGranted(grantResults)) {
                    sendSMS(numberToSend, messageToSend, iconId);
                } else {
                    showToast(getString(R.string.noSMSPermission), android.R.drawable.stat_notify_error);
                }
                break;
            }
            case REQUEST_CONTACTS:
                if (permissionGranted(grantResults)) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);
                } else {
                    showToast(getString(R.string.noContactPermission), android.R.drawable.stat_notify_error);
                }
                break;
            case REQUEST_PHONE_STATE:
                if (!permissionGranted(grantResults)) {
                    showToast(getString(R.string.noPermissionForPhoneState), android.R.drawable.stat_notify_error);
                }
                break;
            case REQUEST_GPS:
                if (permissionGranted(grantResults)) {
                    String name = shortcutText.getText().toString();
                    String number = phoneNumberText.getText().toString();
                    String message = messageText.getText().toString();
                    addShortcut(name, number, message, selectedIcon);
                } else {
                    showToast("Cannot create this shortcut because GPS permission was not granted when text send GPS position", android.R.drawable.stat_notify_error);
                }
                break;
        }
    }

    private static String bearingToString(float bearing) {
        if (bearing < 22.5) {
            return "north";
        } else if (bearing < 67.5) {
            return "north-east";
        } else if (bearing < 112.5) {
            return "east";
        } else if (bearing < 157.5) {
            return "south-east";
        } else if (bearing < 202.5) {
            return "south";
        } else if (bearing < 247.6) {
            return "south-west";
        } else if (bearing < 292.5) {
            return "west";
        } else if (bearing < 337.5) {
            return "north-west";
        } else {
            return "north";
        }
    }


    private class CurrentLocationListener implements LocationListener {
        private String phoneNo;
        private String msg;
        private int id;
        public CurrentLocationListener(String phoneNo, String msg, int id){
            this.phoneNo = phoneNo;
            this.msg = msg;
            this.id = id;
        }

        public void onLocationChanged(Location location){
            String latitude = Double.toString(location.getLatitude());
            String longitude = Double.toString(location.getLongitude());

            float speed_meterspersecond = location.getSpeed();
            String speed_mph = Float.toString(speed_meterspersecond * 2.237f);
            String speed_fps = Float.toString(speed_meterspersecond * 3.28084f);
            String speed_mps = Float.toString(speed_meterspersecond);
            String speed_kph = Float.toString(speed_meterspersecond * 3.6f);
            float bearing = location.getBearing();
            String bearingString = Float.toString(location.getBearing());
            String cardinal = bearingToString(bearing);
            Double altitude = location.getAltitude();
            String altitude_m = Double.toString(altitude);
            String altitude_f = Double.toString(altitude * 3.28084);
            String accuracy_f = Double.toString(location.getAccuracy() * 3.28084);
            msg = msg.replaceAll(LATITUDE, latitude);
            msg = msg.replaceAll(LONGITUDE, longitude);
            msg = msg.replaceAll(LOCATION_MAP, "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude);
            msg = msg.replaceAll(SPEED_MPH, speed_mph);
            msg = msg.replaceAll(SPEED_FPS, speed_fps);
            msg = msg.replaceAll(SPEED_MPS, speed_mps);
            msg = msg.replaceAll(SPEED_KPH, speed_kph);
            msg = msg.replaceAll(BEARING, bearingString);
            msg = msg.replaceAll(CARDINAL, cardinal);
            msg = msg.replaceAll(ALTITUDE_M, altitude_m);
            msg = msg.replaceAll(ALTITUDE_F, altitude_f);
            if (msg.contains(GPS_DUMP)) {
                StringBuilder sb = new StringBuilder("http://maps.google.com/maps?q=loc:");
                sb.append(latitude);
                sb.append(longitude);
                sb.append("\n");
                if (location.hasBearing()) {
                    sb.append("Bearing: ");
                    sb.append(bearingString);
                    sb.append("(");
                    sb.append(cardinal);
                    sb.append(")\n");
                }
                if (location.hasSpeed()) {
                    sb.append("Speed: ");
                    sb.append(speed_mph);
                    sb.append(" mph\n");
                }
                if (location.hasAltitude()) {
                    sb.append("Altitude: ");
                    sb.append(altitude_f);
                    sb.append(" ft.\n");
                }
                if (location.hasAccuracy()) {
                    sb.append("Accuracy: ");
                    sb.append(accuracy_f);
                    sb.append(" ft.\n");
                }
                msg = msg.replaceAll(GPS_DUMP, sb.toString());
            }
            actualSend(phoneNo, msg, id);

        }

        public void onStatusChanged(String var1, int var2, Bundle var3){

        }

        public void onProviderEnabled(String var1){

        }

        public void onProviderDisabled(String var1){

        }
    }

    // not registering delivery intent because not all carriers support it
    public void sendSMS(String phoneNo, String msg, int iconId) {
        try {
            if (msg.contains(GPS_TRIGGER)) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null) {
                    showError("Could not retrieve location.  Is GPS turned on?");
                    return;
                } else {
                    showToast("Will send text as soon as updated info received from GPS", iconId);
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new CurrentLocationListener(phoneNo, msg, iconId), Looper.getMainLooper());
                }
            } else {
                actualSend(phoneNo, msg, iconId);
            }
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void actualSend(String phoneNo, String msg, int iconId) {
        String id = UUID.randomUUID().toString();
        Intent sendIntent = new Intent(id);
        sendIntent.putExtra(NUMBER, phoneNo);
        sendIntent.putExtra(MESSAGE, msg);
        sendIntent.putExtra(ICON, iconId);
        PendingIntent sentIntent = PendingIntent.getBroadcast(MainActivity.this, 0, sendIntent, 0);
        registerSentReceivers(id);
        SmsManager smsManager = SmsManager.getDefault();
        showToast(String.format(getString(R.string.sentMessage), phoneNo, msg), iconId);
        smsManager.sendTextMessage(phoneNo, null, msg, sentIntent, null);
    }

    private class ErrorAcknowledgeListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            finishAffinity();
            setTheme(INVISIBLE_THEME);
        }
    }

    private void showError(String errorString) {
        Dialog.showOK(MainActivity.this, errorString, new ErrorAcknowledgeListener());
    }

    private void registerSentReceivers(String sentString) {
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context  context, Intent intent) {
                String number = intent.getStringExtra(NUMBER);
                String msg = intent.getStringExtra(MESSAGE);
                int iconId = intent.getIntExtra(ICON, iconIds[0]);
                unregisterReceiver(this);
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        finishAffinity();
                        setTheme(INVISIBLE_THEME);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        showError(String.format(getString(R.string.errorSendGeneric),
                                number, msg));
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        showError(String.format(getString(R.string.errorSendNoService),
                                number, msg));
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        showError(String.format(getString(R.string.errorSendNullPDU),
                                number, msg));
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        showError(String.format(getString(R.string.errorSendRadioOff),
                                number, msg));
                        break;
                }
            }
        }, new IntentFilter(sentString));
    }

}
