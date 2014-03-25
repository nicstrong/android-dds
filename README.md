# Android Debug Data Server

A debug data server for Android. Allow an Android application to publish data for diagnostic purpose and to consume this data via a simple rest API from client applications.

## Getting started

Create a service to host the debug data server. And override ```onBuildServer()``` adding the required data sources.

```java
@Override public void onBuildServer(DebugDataServer.Builder builder) {
    SharedPreferences sharedPrefs
            = PreferenceManager.getDefaultSharedPreferences(this);
    builder.interfaceName(sharedPrefs.getString(PREF_INTERFACE_NAME, "lo"))
            .port(sharedPrefs.getInt(PREF_PORT, 8080))
            .addStaticClass(DebugData.class);
}
```

Then in your main activity start the server with the following code.

```java
DebugDataServer.start(this, AddsService.class, this);
```

This will callback to ```onDebugDataServerStarted(DebugDataServer debugDataServer)``` when the server is ready.

## An Example

To run the sample project (adds-sample) install and run the APK.

    gradle assembleDebug
    adb install -r adds-sample/build/apk/adds-sample-debug-unaligned.apk

Be default it binds to localhost port 8080. Map this to a local port using adb.

    adb forward tcp:8080 tcp:8080

Now the debug data can be queried/updated using simple curl commands.

#### Show the configured data sources

    curl http://localhost:8080/source


```javascript
{
  "dataSources": [
    {
      "name": "DebugData"
    }
  ]
}
```

#### Show the a data source

    curl http://localhost:8080/source/DebugData

```javascript
{
  "BOOLEAN_VALUE": true,
  "INT_VALUE": 1,
  "POJO_VALUE": {
    "d": "Test123",
    "b": true,
    "c": 1.2,
    "a": 1
  },
  "STRING_VALUE": "Hello World!"
}
```

#### Show a property

    curl http://localhost:8080/source/DebugData/STRING_VALUE

```javascript
{
  "name": "STRING_VALUE",
  "propertySourceType": "FieldPropertySource",
  "value": "Hello World!"
}
```

#### Set a property
    curl -X PUT -H "Content-Type: application/json" -d '{"value": "Test 12345!!!"}' http://localhost:8080/source/DebugData/STRING_VALUE

Because the main activity has registered to track changes as you change properties you should see them reflected in the main activity on the application.