/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.BluetoothChat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothChat extends Activity {
    // Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Layout Views
    private TextView mTitle;
    // private ListView mConversationView;
    // private EditText mOutEditText;
    // private Button mSendButton;

    // ���Ͱ���
    // private Button button00;
    // private Button button01;
    // private Button button02;

    // ������
    // private sendButtonListener sListener = new sendButtonListener();

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    private int zl = 0;

    class myFingerEven implements OnRockerListener {
        byte[] value = new byte[1];

        public void onRocker(int which) {
            value = new byte[1];
            if(which == 0) {
                value[0] = (byte) 'Z';
            }
            else {
                zl = 0x01 << (which - 1);
                switch(zl) {
                    case 0x01:
                        value[0] = (byte) 'A';//摇杆1
                        break;
                    case 0x02:
                        value[0] = (byte) 'B';//摇杆2
                        break;
                    case 0x04:
                        value[0] = (byte) 'C';//摇杆3
                        break;
                    case 0x08:
                        value[0] = (byte) 'D';//摇杆4
                        break;
                    case 0x10:
                        value[0] = (byte) 'E';//摇杆5
                        break;
                    case 0x20:
                        value[0] = (byte) 'F';//摇杆6
                        break;
                    case 0x40:
                        value[0] = (byte) 'G';//摇杆7
                        break;
                    case 0x80:
                        value[0] = (byte) 'H';//摇杆8
                        break;
                }
            }
            // Check that we're actually connected before trying anything
            if(mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
                //				Toast.makeText(BluetoothChat.this, R.string.not_connected,
                //						Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("ZLog", Arrays.toString(value) + "   " + value[0]);
            mChatService.write(value);
        }

        public void onRocker(MySurfaceView mySurfaceView, int which) {
            // TODO Auto-generated method stub

        }

    }

    /*
     * class myFingerEven implements OnFingerListener {
     *
     * public void onClick(myView view) { // TODO Auto-generated method stub
     *
     * }
     *
     * public void onClick(myView view, int which) { // TODO Auto-generated
     * method stub byte[] value = new byte[1]; switch (which) { case 0:
     * view.setStr("����ԲȦ��"); value[0] = 0x00; break; case 1:
     * view.setStr("����ԲȦ��"); value[0] = 0x01; break; default: break; }
     *
     *
     * // Check that we're actually connected before trying anything if
     * (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
     * Toast.makeText(BluetoothChat.this, R.string.not_connected,
     * Toast.LENGTH_SHORT).show(); return; }
     *
     * mChatService.write(value); } }
     */

    // class sendButtonListener implements OnClickListener {
    // byte[] value = new byte[1];
    //
    // public sendButtonListener() {
    // super();
    // // TODO Auto-generated constructor stub
    // }
    //
    // public void onClick(View v) {
    // // TODO Auto-generated method stub
    // switch (v.getId()) {
    // case R.id.button1:
    // value[0] = 0x00;
    // break;
    // case R.id.button2:
    // value[0] = 0x01;
    // break;
    // case R.id.button3:
    // value[0] = 0x03;
    // break;
    // default:
    // break;
    // }
    //
    // // Check that we're actually connected before trying anything
    // if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
    // Toast.makeText(BluetoothChat.this, R.string.not_connected,
    // Toast.LENGTH_SHORT).show();
    // return;
    // }
    //
    // mChatService.write(value);
    //
    // }

    // }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        if(D)
            Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);//��ȥ���⣨Ӧ�õ����ֱ���Ҫд��setContentView֮ǰ����������쳣��
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);

        MySurfaceView myView = new MySurfaceView(this);
        myView.setRockerListener(new myFingerEven());
        LinearLayout layout = findViewById(R.id.ll_controller);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(50, 30, 50, 30);
        layout.addView(myView, lp);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

        // Set up the custom title
        //        mTitle = (TextView) findViewById(R.id.title_left_text);
        //        mTitle.setText(R.string.app_name);
        mTitle = (TextView) findViewById(R.id.title_right_text);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if(mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    public void clickHandler(View view) {
        byte[] value;
        switch(view.getId()) {
            case R.id.btn1:
                value = new byte[1];
                value[0] = (byte) 'J';
                mChatService.write(value);
                break;
            case R.id.btn2:
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                break;
            case R.id.btn4:
                value = new byte[1];
                value[0] = (byte) 'K';
                mChatService.write(value);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(D)
            Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }
        else {
            if(mChatService == null)
                setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D)
            Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity
        // returns.
        if(mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't
            // started already
            if(mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        // mConversationView = (ListView) findViewById(R.id.in);
        // mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        // mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        // mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        // mSendButton = (Button) findViewById(R.id.button_send);
        // button00 = (Button) findViewById(R.id.button1);
        // button01 = (Button) findViewById(R.id.button2);
        // button02 = (Button) findViewById(R.id.button3);
        // ��ӷ��Ͱ���������
        // button00.setOnClickListener(sListener);
        // button01.setOnClickListener(sListener);
        // button02.setOnClickListener(sListener);

        // mSendButton.setOnClickListener(new OnClickListener() {
        // public void onClick(View v) {
        // // Send a message using content of the edit text widget
        // TextView view = (TextView) findViewById(R.id.edit_text_out);
        // String message = view.getText().toString();
        // sendMessage(message);
        // }
        // });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D)
            Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D)
            Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if(mChatService != null)
            mChatService.stop();
        if(D)
            Log.e(TAG, "--- ON DESTROY ---");
    }

    private void ensureDiscoverable() {
        if(D)
            Log.d(TAG, "ensure discoverable");
        if(mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if(mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if(message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            // mOutEditText.setText(mOutStringBuffer);
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    @SuppressLint("HandlerLeak") private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch(msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            mTitle.setText(R.string.title_connected_to);
                            mTitle.append(mConnectedDeviceName);
                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            mTitle.setText(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            mTitle.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D)
            Log.d(TAG, "onActivityResult " + resultCode);
        switch(requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if(resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mChatService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if(resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                }
                else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.scan:
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
        }
        return false;
    }

}