package co.duxan.lightbluebean;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter; 
import android.bluetooth.BluetoothDevice; 
import android.bluetooth.BluetoothSocket; 
import android.os.AsyncTask; 

// import com.bitalino.comm.*;
// import com.bitalino.util.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * The LightbluebeanPlugin is where we communicate with Java SDK and establish readings from the LightBlueBean.
 */
public class LightbluebeanPlugin extends CordovaPlugin {
	private static final String TAG = "LightBlue";
	private CallbackContext myCallbackContext;

	/*
	* Constructor.
	*/
	public LightbluebeanPlugin() {}

	/*
	* Sets the context of the Command. 
	*
	* @param cordova The context of the main Activity.
	* @param webView The CordovaWebView Cordova is running in.
	*/
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		Log.i(TAG, "Initializing LightbluebeanPlugin");
		this.myCallbackContext = null;
	}

	public boolean execute(String action, final JSONArray args, CallbackContext callbackContext) throws JSONException {
		Log.i(TAG, "BITalinoPlugin received action: "+ action + "; Args:  " + args);
		this.myCallbackContext = callbackContext;

		// create an empty result, because the asynchronous call can be long
		PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);

		if (action.equals("greet")) {

		    String name = args.getString(0);
		    String message = "Hello, " + name;
		    callbackContext.success(message);
		    return true;

		} else {
		    
		    return false;

		}
	}
}