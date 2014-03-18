package com.nicstrong.spark.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.collect.Maps;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import timber.log.Timber;


public class MainActivity extends Activity {

    private TextView interfaceName;
    private TextView ipAddress;
    private EditText port;
    private Map<String, NetworkInterface> interfaceMap;
    private int defaultPosition;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());
        interfaceMap = buildInterfaceMap();

        String ifaces[] = new String[interfaceMap.size()];
        interfaceMap.keySet().toArray(ifaces);

        spinner = (Spinner)findViewById(R.id.network_interface);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ifaces);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(defaultPosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String displayName = adapter.getItem(position);
                setSelectedInterface(displayName);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                setSelectedInterface(null);
            }
        });

        port = (EditText)findViewById(R.id.port);
        interfaceName = (TextView)findViewById(R.id.interface_name);
        ipAddress = (TextView)findViewById(R.id.interface_ip_address);

        Button startServer = (Button)findViewById(R.id.start_server);
        startServer.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SparkService.class);
                intent.setAction(SparkService.ACTION_START);
                intent.putExtra(SparkService.EXTRA_PORT, Integer.parseInt(port.getText().toString()));
                intent.putExtra(SparkService.EXTRA_INTERFACE, adapter.getItem(spinner.getSelectedItemPosition()));
                startService(intent);
            }
        });

    }

    private void setSelectedInterface(String displayName) {
        String interfaceName = "N/A";
        String ipAddress = "N/A";

        if (displayName != null) {
            NetworkInterface ni = interfaceMap.get(displayName);
            interfaceName = ni.getName();
            List<InetAddress> addresses = Collections.list(ni.getInetAddresses());
            StringBuilder builder = new StringBuilder();
            for (InetAddress address : addresses) {
                if (address instanceof Inet4Address) {
                    if (builder.length() > 0) {
                        builder.append(", ");
                    }
                    builder.append(address.getHostAddress());
                }
            }
            ipAddress = builder.toString();
        }

        this.interfaceName.setText("Name: " + interfaceName);
        this.ipAddress.setText("IP Addresses: " + ipAddress);
    }



    private Map<String, NetworkInterface> buildInterfaceMap() {
        Map<String, NetworkInterface> interfaceMap = Maps.newLinkedHashMap();

        Enumeration<NetworkInterface> nis = null;
        try {
            nis = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            Timber.d(e, "Failed to get the network interfaces");
            return interfaceMap;
        }

        defaultPosition = Integer.MIN_VALUE;
        int index = 0;
        for (; nis.hasMoreElements(); ) {
            NetworkInterface ni = nis.nextElement();
            String displayName = TextUtils.isEmpty(ni.getDisplayName()) ? ni.getName() : ni.getDisplayName();
            if (defaultPosition == Integer.MIN_VALUE) {
                defaultPosition = index;
            }
            if (ni.getName().equals("lo")) {
                defaultPosition = index;
            }

            interfaceMap.put(displayName, ni);
            index++;
        }

        return interfaceMap;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
