package com.nicstrong.android.dds.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

import com.nicstrong.android.dds.DebugDataServer;
import com.nicstrong.android.dds.OnDebugDataServerStartedListener;
import com.nicstrong.android.dds.datasource.OnPropertyChangedListener;
import com.nicstrong.android.dds.datasource.Property;

import timber.log.Timber;


public class MainActivity extends Activity implements OnDebugDataServerStartedListener, OnPropertyChangedListener {

    private EditText stringValue;
    private CheckBox booleanValue;
    private EditText integerValue;
    private DebugDataServer debugDataServer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Timber.plant(new Timber.DebugTree());

        stringValue = (EditText) findViewById(R.id.string_value);
        booleanValue = (CheckBox) findViewById(R.id.boolean_value);
        integerValue = (EditText) findViewById(R.id.integer_value);
        stringValue.setText(DebugData.STRING_VALUE);
        booleanValue.setChecked(DebugData.BOOLEAN_VALUE);
        integerValue.setText(Integer.toString(DebugData.INT_VALUE));

        Handler handler = new Handler(getMainLooper());

        DebugDataServer.start(this, AddsService.class, this);
    }

    @Override public void onPropertyChanged(final Property property) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                if (property.getName().equals("STRING_VALUE")) {
                    stringValue.setText(DebugData.STRING_VALUE);
                }
                if (property.getName().equals("BOOLEAN_VALUE")) {
                    booleanValue.setChecked(DebugData.BOOLEAN_VALUE);
                }
                if (property.getName().equals("INT_VALUE")) {
                    integerValue.setText(Integer.toString(DebugData.INT_VALUE));
                }
            }
        });
    }


    @Override public void onDebugDataServerStarted(DebugDataServer debugDataServer) {
        this.debugDataServer = debugDataServer;
        debugDataServer.getDataSourceRegistry().get("DebugData").registerPropertyChangedListener(this);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (debugDataServer != null) {
            debugDataServer.getDataSourceRegistry().get("DebugData").unregisterPropertyChangedListener(this);
        }
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
