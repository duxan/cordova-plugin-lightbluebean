[BITalino](http://www.bitalino.com) Cordova Android Plugin 
=============================================

This plugin is HTML5 port of [BITalino Java SDK](https://github.com/BITalinoWorld/java-sdk).
Java SDK version is 1.1.-SNAPSHOT and it is provided with the plugin.

If features basic BITalino protocol and utilities for read/write from BITalino devices, for ANDROID only.

Bluetooth connection management is not included.

Plugin has been tested with Cordova 5.0.0., Android 4.4.2.

## Install
- New project: `cordova create hello com.example.hello HelloWorld`
- Add platform: `cordova platform add android`
- Download and install plugin: `cordova plugin add https:\\github.com\duxan\cordova-plugin-bitalino.git`
- Build and run: `cordova build`, `cordova run`

## Usage
- NO need to include JS files in index.html
- Call `BITalinoPlugin.checkBitalino("MAC address", success, failure)` or `BITalinoPlugin.startBitalino("MAC address", success, failure)` from anywhere.
- Catch callbacks with success and failure functions, like this for example: 

``` 
var success = function(message) {
    console.log(message);
}

var failure = function() {
    alert("Error calling Plugin");
}
```

- Callbacks are optional, but `MAC address` you have to enter. It is provided with you BITalino kit. Also, you need to have Bluetooth ON and paired with device (PIN is 1234)
- If you run it via USB debug you can check for logs with logcat: `adb logcat | grep "BITalino"` and in browser console: `chrome://inspect/#devices`
- Basic info will come via Android Toast, too.

## TODO
- stopping methods
- action isBitalinoUP("MAC")
- action getBitalinoFrame(Int skip)