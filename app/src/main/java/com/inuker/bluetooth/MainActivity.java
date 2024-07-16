package com.inuker.bluetooth;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.view.PullRefreshListView;
import com.inuker.bluetooth.view.PullToRefreshFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private PullToRefreshFrameLayout mRefreshLayout;
    private PullRefreshListView mListView;
    private DeviceListAdapter mAdapter;
    private TextView mTvTitle;
    private List<SearchResult> mDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDevices = new ArrayList<>();
        mTvTitle = findViewById(R.id.title);
        mRefreshLayout = findViewById(R.id.pulllayout);
        mListView = mRefreshLayout.getPullToRefreshListView();
        mAdapter = new DeviceListAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnRefreshListener(this::searchDevice);
        searchDevice();
        ClientManager.getClient().registerBluetoothStateListener(new BluetoothStateListener() {
            @Override
            public void onBluetoothStateChanged(boolean openOrClosed) {
                BluetoothLog.v(String.format("onBluetoothStateChanged %b", openOrClosed));
            }
        });
        //关闭日志输出
        // BluetoothLog.isShowLog = false;

        //打开APP后立即扫描一次
        searchDevice();
    }

    private void searchDevice() {
        //获取权限
        ActivityResultLauncher<String> arl = getActivityResultRegistry().register("123", new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    //获取权限后，立即进行扫描一次
                    SearchRequest request = new SearchRequest.Builder().searchBluetoothLeDevice(5000, 2).build();
                    ClientManager.getClient().search(request, mSearchResponse);
                } else {
                    Toast.makeText(MainActivity.this, "必须开启位置信息权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
        arl.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            BluetoothLog.w("MainActivity.onSearchStarted");
            mListView.onRefreshComplete(true);
            mRefreshLayout.showState(AppConstants.LIST);
            mTvTitle.setText(R.string.string_refreshing);
            mDevices.clear();
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            if (!mDevices.contains(device)) {
                mDevices.add(device);
                mAdapter.setDataList(mDevices);
//                Beacon beacon = new Beacon(device.scanRecord);
//                BluetoothLog.v(String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));
//                BeaconItem beaconItem = null;
//                BeaconParser beaconParser = new BeaconParser(beaconItem);
//                int firstByte = beaconParser.readByte(); // 读取第1个字节
//                int secondByte = beaconParser.readByte(); // 读取第2个字节
//                int productId = beaconParser.readShort(); // 读取第3,4个字节
//                boolean bit1 = beaconParser.getBit(firstByte, 0); // 获取第1字节的第1bit
//                boolean bit2 = beaconParser.getBit(firstByte, 1); // 获取第1字节的第2bit
//                beaconParser.setPosition(0); // 将读取起点设置到第1字节处
            }
            if (!mDevices.isEmpty()) {
                mRefreshLayout.showState(AppConstants.LIST);
            }
        }

        @Override
        public void onSearchStopped() {
            BluetoothLog.w("MainActivity.onSearchStopped");
            mListView.onRefreshComplete(true);
            mRefreshLayout.showState(AppConstants.LIST);
            mTvTitle.setText(R.string.devices);
        }

        @Override
        public void onSearchCanceled() {
            BluetoothLog.w("MainActivity.onSearchCanceled");
            mListView.onRefreshComplete(true);
            mRefreshLayout.showState(AppConstants.LIST);
            mTvTitle.setText(R.string.devices);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        ClientManager.getClient().stopSearch();
    }
}
