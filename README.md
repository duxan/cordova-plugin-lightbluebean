[Punch Through](https://punchthrough.com/) Cordova Android Plugin 
=============================================

This plugin is HTML5 port of [Bean SDK](https://github.com/PunchThrough/Bean-Android-SDK).
Java SDK version is 1.0.1. (built as dependency via gradle).

## Install
- New project: `cordova create hello com.example.hello HelloWorld`
- Add platform: `cordova platform add android`
- Download and install plugin: `cordova plugin add https:\\github.com\duxan\cordova-plugin-lightbluebean.git`
- Build and run: `cordova build`, `cordova run --nobuild`

## Usage
- NO need to include JS files in index.html
- Call `LightbluebeanPlugin.greet("text", success, failure)` from anywhere.
- Catch callbacks with success and failure functions, like this for example: 

``` 
var success = function(message) {
    console.log(message);
}

var failure = function() {
    alert("Error calling Plugin");
}
```

- Callbacks are optional
- If you run it via USB debug you can check for logs with logcat: `adb logcat | grep "LightBlue"` and in browser console: `chrome://inspect/#devices`