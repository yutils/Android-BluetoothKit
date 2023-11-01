package com.inuker.bluetooth.library.search.le;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.os.Build;

import com.inuker.bluetooth.library.search.BluetoothSearcher;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.BluetoothSearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;

/**
 * @author dingjikerbo
 */
public class BluetoothLESearcher extends BluetoothSearcher {

    private BluetoothLESearcher() {
        mBluetoothAdapter = BluetoothUtils.getBluetoothAdapter();
    }

    public static BluetoothLESearcher getInstance() {
        return BluetoothLESearcherHolder.instance;
    }

    private static class BluetoothLESearcherHolder {
        private static BluetoothLESearcher instance = new BluetoothLESearcher();
    }

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressWarnings("deprecation")
    @Override
    public void startScanBluetooth(BluetoothSearchResponse response) {
        super.startScanBluetooth(response);

        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressWarnings("deprecation")
    @Override
    public void stopScanBluetooth() {
        try {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        } catch (Exception e) {
            BluetoothLog.e(e);
        }

        super.stopScanBluetooth();
    }

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressWarnings("deprecation")
    @Override
    protected void cancelScanBluetooth() {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        super.cancelScanBluetooth();
    }

    private final LeScanCallback mLeScanCallback = (device, rssi, scanRecord) -> notifyDeviceFounded(new SearchResult(device, rssi, scanRecord));
}
