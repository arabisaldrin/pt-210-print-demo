package com.example.bluetooth_print;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.S)
public class MainActivity extends Activity implements View.OnClickListener {


    private static final int PERMISSION_BLUETOOTH = 0;
    private static final int PERMISSION_BLUETOOTH_ADMIN = 3;
    private static final int PERMISSION_BLUETOOTH_CONNECT = 2;
    private static final int PERMISSION_BLUETOOTH_SCAN = 1;

    private final ArrayList<CartModel> items = new ArrayList<>();
    BluetoothDevice device = null;

    @Override
    public void onCreate(Bundle mSavedInstanceState) {
        super.onCreate(mSavedInstanceState);

        setContentView(R.layout.activity_main);

        initData();

        Button btn = findViewById(R.id.mPrint);
        btn.setOnClickListener(this);

        //  Request for bluetooth permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, MainActivity.PERMISSION_BLUETOOTH);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, MainActivity.PERMISSION_BLUETOOTH_ADMIN);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, MainActivity.PERMISSION_BLUETOOTH_CONNECT);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, MainActivity.PERMISSION_BLUETOOTH_SCAN);
        } else {
            // make sure device is already paired
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> mBtDevices = btAdapter.getBondedDevices();// Get first paired device

            for (BluetoothDevice bluetoothDevice : mBtDevices) {
                if (bluetoothDevice.getName().equals("MTP-2")) {
                    device = bluetoothDevice;
                }
            }
        }

    }

    /*
     * Create Sample data
     * */
    private void initData() {
        items.add(new CartModel("Item 1", 25, 1));
        items.add(new CartModel("Item 2", 35, 2));
        items.add(new CartModel("Item 3", 45, 2));
    }

    private void testPrint() {
        // receipt divider ========================
        String divider = String.format("%" + 32 + "s", "").replace(' ', '-');
        if (device != null) {
            final BluetoothPrinter mPrinter = new BluetoothPrinter(device);
            mPrinter.connectPrinter(new BluetoothPrinter.PrinterConnectListener() {
                @Override
                public void onConnected() {

                    mPrinter.setAlign(BluetoothPrinter.ALIGN_LEFT);
                    // table format: 32 character width per line
                    String format = "%-15s%5s%6s%6s";

                    mPrinter.printText("Order ID: #00001\n");
                    mPrinter.printText("Customer Name: Mary Jane\n");
                    mPrinter.printText(divider);
                    // order item headers
                    mPrinter.printText(String.format(format, "Items", "Prc", "Qty", "Total"));
                    mPrinter.printText(divider);
                    mPrinter.addNewLine();
                    for (CartModel item : items) {
                        mPrinter.printText(item.forReceipt());
                        mPrinter.addNewLine();
                    }

                    // get grand total
                    double sum = items.stream().map(CartModel::getTotal)
                            .reduce(0.0, Double::sum);

                    mPrinter.printText(divider);
                    mPrinter.setAlign(BluetoothPrinter.ALIGN_RIGHT);
                    mPrinter.printText(String.valueOf(sum));
                    mPrinter.addNewLine();
                    mPrinter.printText(divider);
                    mPrinter.setAlign(BluetoothPrinter.ALIGN_LEFT);
                    // print address
                    mPrinter.printText("Delivery Address:\nUnit 3rd Ground Floor, One Global Place, 5th Avenue corner 25th Street");

                    // feed for empty lines for cutting
                    mPrinter.feedPaper();
                    mPrinter.finish();
                }

                @Override
                public void onFailed() {
                }
            });
        }

    }


    @Override
    public void onClick(View v) {

        testPrint();
    }
}