package com.inuker.bluetooth;

import static com.inuker.bluetooth.library.Constants.GATT_DEF_BLE_MTU_SIZE;
import static com.inuker.bluetooth.library.Constants.GATT_MAX_MTU_SIZE;
import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.response.BleMtuResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.ByteUtils;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/9/6.
 */
public class CharacterActivity extends AppCompatActivity implements View.OnClickListener {

    private String mMac;
    private UUID mService;
    private UUID mCharacter;

    private TextView mTvTitle;

    private Button mBtnRead;

    private Button mBtnWrite;
    private EditText mEtInput;

    private Button mBtnNotify;
    private Button mBtnUnnotify;
    private EditText mEtInputMtu;
    private Button mBtnRequestMtu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.character_activity);

        Intent intent = getIntent();
        mMac = intent.getStringExtra("mac");
        mService = (UUID) intent.getSerializableExtra("service");
        mCharacter = (UUID) intent.getSerializableExtra("character");

        mTvTitle = findViewById(R.id.title);
        mTvTitle.setText(String.format("%s", mMac));

        mBtnRead = findViewById(R.id.read);

        mBtnWrite = findViewById(R.id.write);
        mEtInput = findViewById(R.id.input);

        mBtnNotify = findViewById(R.id.notify);
        mBtnUnnotify = findViewById(R.id.unnotify);

        mEtInputMtu = findViewById(R.id.et_input_mtu);
        mBtnRequestMtu = findViewById(R.id.btn_request_mtu);

        mBtnRead.setOnClickListener(this);
        mBtnWrite.setOnClickListener(this);

        mBtnNotify.setOnClickListener(this);
        mBtnNotify.setEnabled(true);

        mBtnUnnotify.setOnClickListener(this);
        mBtnUnnotify.setEnabled(false);

        mBtnRequestMtu.setOnClickListener(this);
    }

    private final BleReadResponse mReadRsp = new BleReadResponse() {
        @Override
        public void onResponse(int code, byte[] data) {
            if (code == REQUEST_SUCCESS) {
                mBtnRead.setText(String.format("read: %s", ByteUtils.byteToString(data)));
                CommonUtils.toast("success");
            } else {
                CommonUtils.toast("failed");
                mBtnRead.setText("read");
            }
        }
    };

    private final BleWriteResponse mWriteRsp = code -> {
        if (code == REQUEST_SUCCESS) {
            CommonUtils.toast("success");
        } else {
            CommonUtils.toast("failed");
        }
    };

    private final BleNotifyResponse mNotifyRsp = new BleNotifyResponse() {
        @Override
        public void onNotify(UUID service, UUID character, byte[] value) {
            if (service.equals(mService) && character.equals(mCharacter)) {
                mBtnNotify.setText(String.format("%s", ByteUtils.byteToString(value)));
            }
        }

        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                mBtnNotify.setEnabled(false);
                mBtnUnnotify.setEnabled(true);
                CommonUtils.toast("success");
            } else {
                CommonUtils.toast("failed");
            }
        }
    };

    private final BleUnnotifyResponse mUnnotifyRsp = new BleUnnotifyResponse() {
        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                CommonUtils.toast("success");
                mBtnNotify.setEnabled(true);
                mBtnUnnotify.setEnabled(false);
            } else {
                CommonUtils.toast("failed");
            }
        }
    };

    private final BleMtuResponse mMtuResponse = (code, data) -> {
        if (code == REQUEST_SUCCESS) {
            CommonUtils.toast("request mtu success,mtu = " + data);
        } else {
            CommonUtils.toast("request mtu failed");
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.read) {
            ClientManager.getClient().read(mMac, mService, mCharacter, mReadRsp);
        } else if (id == R.id.write) {
            ClientManager.getClient().write(mMac, mService, mCharacter,
                    ByteUtils.stringToBytes(mEtInput.getText().toString()), mWriteRsp);
        } else if (id == R.id.notify) {
            ClientManager.getClient().notify(mMac, mService, mCharacter, mNotifyRsp);
        } else if (id == R.id.unnotify) {
            ClientManager.getClient().unnotify(mMac, mService, mCharacter, mUnnotifyRsp);
        } else if (id == R.id.btn_request_mtu) {
            String mtuStr = mEtInputMtu.getText().toString();
            if (TextUtils.isEmpty(mtuStr)) {
                CommonUtils.toast("MTU不能为空");
                return;
            }
            int mtu = Integer.parseInt(mtuStr);
            if (mtu < GATT_DEF_BLE_MTU_SIZE || mtu > GATT_MAX_MTU_SIZE) {
                CommonUtils.toast("MTU不不在范围内");
                return;
            }
            ClientManager.getClient().requestMtu(mMac, mtu, mMtuResponse);
        }
    }

    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            BluetoothLog.v(String.format("CharacterActivity.onConnectStatusChanged status = %d", status));

            if (status == STATUS_DISCONNECTED) {
                CommonUtils.toast("disconnected");
                mBtnRead.setEnabled(false);
                mBtnWrite.setEnabled(false);
                mBtnNotify.setEnabled(false);
                mBtnUnnotify.setEnabled(false);

                mTvTitle.postDelayed(() -> finish(), 300);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        ClientManager.getClient().registerConnectStatusListener(mMac, mConnectStatusListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ClientManager.getClient().unregisterConnectStatusListener(mMac, mConnectStatusListener);
    }
}
