package com.example.danyal.bluetoothhc05;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class ledControl extends AppCompatActivity {

    Button cancelButton, saveButton, savePasswordButton, go;
    String address = null;
    TextView preventText;
    TextView password, newPasswordText;
    TextView myName, phone1, phone2, phone3;
    TextView[] lbl;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    boolean voilenceDetected = false;
    int confirmCounter = 10;
    void SendSMSs()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        String name = pref.getString("myName", "صاحبة هذا الرقم ");
        String p1 = pref.getString("phone1", "");
        String p2 = pref.getString("phone2", "");
        String p3 = pref.getString("phone3", "");

        String msg = "انا  " + name + " وانا اتعرض للعنف انقظوني! ";
        int counter = 0;
        if (p1.length() > 8)
        {
            if (sendSMS(p1, msg))
            {
                counter++;
            }
        }
        if (p2.length() > 8)
        {
            if (sendSMS(p2, msg))
            {
                counter++;
            }
        }
        if (p3.length() > 8)
        {
            if (sendSMS(p3, msg))
            {
                counter++;
            }
        }


        if (counter == 1)
        {
            msg("تم ارسال الرسالة الى شخص واحد");
        }
        else if (counter == 2)
        {
            msg("تم ارسال الرسالة الى شخصان");
        }
        else if (counter == 3)
        {
            msg("تم ارسال الرسالة الى ٣ اشخاص");
        }
        else
        {
            msg("لم يتم ارسال الرسالة لاي احد");
        }
    }

    boolean checkPassword() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        if (password.getText().toString().equals(pref.getString("password", "0000"))) {
            hideKeyboard();
            return true;
        } else {
            msg("خطأ في كلمة المرور");
            return false;
        }
    }

    public boolean sendSMS(String phone, String message) {

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    void saveSettings()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("myName", myName.getText().toString());
        editor.putString("phone1", phone1.getText().toString()); // Storing string
        editor.putString("phone2", phone2.getText().toString()); // Storing string
        editor.putString("phone3", phone3.getText().toString()); // Storing string

        if (myName.getText().toString().length() < 2)
        {
            msg("تم حفظ الارقام، الرجاء ادخال الاسم");
        }
        else {
            msg("تم الحفظ بنجاح");
        }

        hideKeyboard();
        editor.commit();
    }

    void saveNewPassword()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("password", newPasswordText.getText().toString());

        editor.commit();
        msg("تم التعديل بنجاح");
    }

    void beforeConfirmation()
    {
        password.setText("");
        cancelButton.setText("ايقاف ارسال الرسالة");

        cancelButton.setEnabled(true);
        saveButton.setEnabled(false);
        phone1.setEnabled(false);
        phone2.setEnabled(false);
        phone3.setEnabled(false);
        myName.setEnabled(false);
        newPasswordText.setEnabled(false);
        savePasswordButton.setEnabled(false);

        cancelButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        phone1.setVisibility(View.INVISIBLE);
        phone2.setVisibility(View.INVISIBLE);
        phone3.setVisibility(View.INVISIBLE);
        myName.setVisibility(View.INVISIBLE);
        newPasswordText.setVisibility(View.INVISIBLE);
        savePasswordButton.setVisibility(View.INVISIBLE);

        for (int i = 0; i <lbl.length; ++i)
            lbl[i].setVisibility(View.INVISIBLE);


        msg("الرجاء ادخال كلمة المرور");
    }

    void onTimeout()
    {
        confirmCounter = 10;
        voilenceDetected = false;
        SendSMSs();

        cancelButton.setEnabled(false);
        saveButton.setEnabled(true);
        phone1.setEnabled(true);
        phone2.setEnabled(true);
        phone3.setEnabled(true);
        myName.setEnabled(true);
        newPasswordText.setEnabled(true);
        savePasswordButton.setEnabled(true);


        cancelButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        phone1.setVisibility(View.VISIBLE);
        phone2.setVisibility(View.VISIBLE);
        phone3.setVisibility(View.VISIBLE);
        myName.setVisibility(View.VISIBLE);
        newPasswordText.setVisibility(View.VISIBLE);
        savePasswordButton.setVisibility(View.VISIBLE);

        for (int i = 0; i <lbl.length; ++i)
            lbl[i].setVisibility(View.VISIBLE);

        hideKeyboard();
    }

    void confirmStop()
    {
        voilenceDetected = false;
        confirmCounter = 10;

        cancelButton.setEnabled(false);
        saveButton.setEnabled(true);
        phone1.setEnabled(true);
        phone2.setEnabled(true);
        phone3.setEnabled(true);
        myName.setEnabled(true);
        newPasswordText.setEnabled(true);
        savePasswordButton.setEnabled(true);


        cancelButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        phone1.setVisibility(View.VISIBLE);
        phone2.setVisibility(View.VISIBLE);
        phone3.setVisibility(View.VISIBLE);
        myName.setVisibility(View.VISIBLE);
        newPasswordText.setVisibility(View.VISIBLE);
        savePasswordButton.setVisibility(View.VISIBLE);

        for (int i = 0; i <lbl.length; ++i)
            lbl[i].setVisibility(View.VISIBLE);

        hideKeyboard();
        msg("تم ايقاف الارسال بنجاح");
    }

    public static AppCompatActivity defaultActivity = null;
    public static void hideKeyboard() {

        AppCompatActivity activity = defaultActivity;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);

        setContentView(R.layout.activity_led_control);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        defaultActivity = this;
        go = findViewById(R.id.button2);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.example.studentquestions");
                startActivity(intent);
            }
        });

        lbl = new TextView[5];
        lbl[0] = (TextView) findViewById(R.id.lbl0);
        lbl[1] = (TextView) findViewById(R.id.lbl1);
        lbl[2] = (TextView) findViewById(R.id.lbl2);
        lbl[3] = (TextView) findViewById(R.id.lbl3);
        lbl[4] = (TextView) findViewById(R.id.lbl4);

        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setEnabled(false);
        cancelButton.setVisibility(View.INVISIBLE);
        saveButton = (Button) findViewById(R.id.changePhones);
        savePasswordButton = (Button) findViewById(R.id.savePassword);

        preventText = (TextView) findViewById(R.id.preventText);

        password = (TextView) findViewById(R.id.password);
        newPasswordText = (TextView) findViewById(R.id.newPasswordText);

        myName = (TextView) findViewById(R.id.myName);
        phone1 = (TextView) findViewById(R.id.phone1);
        phone2 = (TextView) findViewById(R.id.phone2);
        phone3 = (TextView) findViewById(R.id.phone3);

        myName.setText(pref.getString("myName", ""));
        phone1.setText(pref.getString("phone1", ""));
        phone2.setText(pref.getString("phone2", ""));
        phone3.setText(pref.getString("phone3", ""));

        new ConnectBT().execute();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (checkPassword()) {
                    confirmStop();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (checkPassword()) {
                    saveSettings();
                }
            }
        });

        savePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPassword()) {
                    saveNewPassword();
                }
            }
        });


        final Handler handler = new Handler();
        final int loopDelay = 30 * 1000; //milliseconds
        final int confirmDelay = 3 * 1000; //milliseconds

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideKeyboard();
            }
        }, 6000);

        handler.postDelayed(new Runnable(){
            public void run() {
                try {
                    if (voilenceDetected) {
                        preventText.setText("لوحظ وجود عنف جاري ارسال الرسالة.." + confirmCounter);

                        if (confirmCounter == 10)
                        {
                            beforeConfirmation();
                        }

                        confirmCounter--;
                        if (confirmCounter < 0) {
                            onTimeout();
                        }
                        handler.postDelayed(this, confirmDelay);
                    } else {
                        cancelButton.setText("");
                        preventText.setText("لا يوجد عنف");


                        byte[] buffer = new byte[10240];
                        int bytes = 0;
                        String inMsg = "";

                        if ( btSocket != null && btSocket.getInputStream() != null && btSocket.getInputStream().available() > 0) {
                            bytes = btSocket.getInputStream().read(buffer);
                            inMsg = new String(buffer, 0, bytes);
                        }

                        String displayMsg = "اشعر بانك في خطر";
                        int x = 0;
                        if (inMsg.contains("f")) {
                            x++;
                            displayMsg = "اشعر بانك وقعتي، هل انت بخير؟";
                        }

                        if (inMsg.contains("b")) {
                            displayMsg = "استمع الى كلام سيء، هل انت بخير؟";
                            x++;
                        }

                        if (x > 0)
                        {
                            if (x == 2) {
                                displayMsg = "اشعر بانك في خطر";
                            }

                            msg(displayMsg);
                        }

                        if (x == 2) {

                            voilenceDetected = true;
                            handler.postDelayed(this, 1000);
                        }
                        else {
                            handler.postDelayed(this, loopDelay);
                        }
                    }
                } catch (Exception ex) {
                    msg("Bluetooth failed to connect" + ex.getMessage());
                }

            }
        }, 3000);

    }

    private void sendSignal ( String number ) {
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(number.toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void Disconnect () {
        if ( btSocket!=null ) {
            try {
                btSocket.close();
            } catch(IOException e) {
                msg("Error");
            }
        }

        finish();
    }

    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected  void onPreExecute () {
            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please Wait!!!");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected");
                isBtConnected = true;
            }

            progress.dismiss();
        }
    }
}
