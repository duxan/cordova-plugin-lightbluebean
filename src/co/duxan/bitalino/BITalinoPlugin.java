package co.duxan.bitalino;

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

import com.bitalino.comm.*;
import com.bitalino.util.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * The BITalinoPlugin is where we communicate with Java SDK and establish readings from the bitalino.
 */
public class BITalinoPlugin extends CordovaPlugin {
	private static final String TAG = "BITalino";
	private CallbackContext myCallbackContext;

	/*
	* Constructor.
	*/
	public BITalinoPlugin() {}

	/*
	* Sets the context of the Command. 
	*
	* @param cordova The context of the main Activity.
	* @param webView The CordovaWebView Cordova is running in.
	*/
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		Log.i(TAG, "Initializing BITalinoPlugin");
		this.myCallbackContext = null;
	}

	// TODO: stopping methods
	// TODO: action isBitalinoUP("MAC")
	// TODO: action getBitalinoFrame(Int skip)
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

		} else if (action.equals("showToast")) {

			final int duration = Toast.LENGTH_SHORT;
			cordova.getActivity().runOnUiThread(new Runnable() {
				String msg = args.getString(0);
				public void run() {
					Toast toast = Toast.makeText(cordova.getActivity().getApplicationContext(), msg, duration);
					toast.show();
				};
			});
			return true;

		} else if (action.equals("checkBitalino")) {

			cordova.getThreadPool().execute(new Runnable() { // Thread-safe.
                String mac = args.getString(0);
                public void run() {
                	checkBITalinoAsyncTask task = new checkBITalinoAsyncTask();
                    task.execute(mac);
                }
            });
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult); // initially empty
			return true; 

		} else if (action.equals("startBitalino")) {

			cordova.getThreadPool().execute(new Runnable() { // Thread-safe.
				String mac = args.getString(0);
                public void run() {
					startBITalinoAsyncTask task = new startBITalinoAsyncTask();
                   	task.execute(mac);
                }
            }); 
			return true;

		} else {
		    
		    return false;

		}
	}


	/* 
	 * Based on the code by Paulo Pires, https://github.com/pires/opensignals-android
	 */ 

	public class checkBITalinoAsyncTask extends AsyncTask<String, BITalinoFrame, String> {
		BluetoothDevice dev = null;
		BluetoothSocket sock = null;
		InputStream is = null;
		OutputStream os = null;
		BITalinoDevice bitalino = null;
		String ver = "No connection. Check if Bluetooth ON and BITalino paired.";

		@Override
		protected String doInBackground(String... mac) {
			try {
				cordova.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						int duration = Toast.LENGTH_SHORT;
						String msg = "Connecting ...";
						Toast toast = Toast.makeText(cordova.getActivity().getApplicationContext(), msg, duration);
						toast.show();
					};
				});				

				/*
				 * http://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
				 * #createRfcommSocketToServiceRecord(java.util.UUID)
				 *
				 * "Hint: If you are connecting to a Bluetooth serial board then try using the
				 * well-known SPP UUID 00001101-0000-1000-8000-00805F9B34FB. However if you
				 * are connecting to an Android peer then please generate your own unique
				 * UUID."
				 */
				final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
				final String macAddress = mac[0];				

				// Let's get the remote Bluetooth device
				final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
				dev = btAdapter.getRemoteDevice(macAddress);

				/*
		 		* Establish Bluetooth connection
		 		*
		 		* Because discovery is a heavyweight procedure for the Bluetooth adapter,
		 		* this method should always be called before attempting to connect to a
		 		* remote device with connect(). Discovery is not managed by the Activity,
		 		* but is run as a system service, so an application should always call
		 		* cancel discovery even if it did not directly request a discovery, just to
		 		* be sure. If Bluetooth state is not STATE_ON, this API will return false.
		 		*
		 		* see
		 		* http://developer.android.com/reference/android/bluetooth/BluetoothAdapter
		 		* .html#cancelDiscovery()
		 		*/
				Log.d(TAG, "Stopping Bluetooth discovery.");
				btAdapter.cancelDiscovery();

				sock = dev.createRfcommSocketToServiceRecord(MY_UUID);
				sock.connect();

				BITalinoDevice bitalino = new BITalinoDevice(1000, new int[]{0, 1, 2, 3, 4, 5});
				Log.i(TAG, "Connecting to BITalino [" + macAddress + "] ...");
				bitalino.open(sock.getInputStream(), sock.getOutputStream());
				Log.i(TAG, "Connected.");

				// get BITalino version
				Log.i(TAG, "Version: " + bitalino.version());
				ver = "Connected to BITalino! Version: " + bitalino.version();
				//callbackContext.success(bitalino.version()); //return version

				bitalino.stop();
				sock.close();
				Log.i(TAG, "BITalino is stopped.");

			} catch (Exception e) {
				Log.e(TAG, "There was an error. Be sure that Bluetooth is ON and BITalino paired", e);
			}
			return ver;
		}

		@Override
		protected void onPostExecute(final String result) {
			cordova.getActivity().runOnUiThread(new Runnable() {
				int duration = Toast.LENGTH_LONG;
				String msg = result;
				public void run() {
					Toast toast = Toast.makeText(cordova.getActivity().getApplicationContext(), msg, duration);
					toast.show();
				};
			});
			PluginResult retValue = new PluginResult(PluginResult.Status.OK, result);
            retValue.setKeepCallback(true);
            myCallbackContext.sendPluginResult(retValue);
		}
	}	

	// readBITalino data
	public class startBITalinoAsyncTask extends AsyncTask<String, BITalinoFrame, String> {
		BluetoothDevice dev = null;
		BluetoothSocket sock = null;
		InputStream is = null;
		OutputStream os = null;
		BITalinoDevice bitalino = null;
		String status = "No connection. Check if Bluetooth ON and BITalino paired.";
		int frameCounter = 0;
		
		@Override
		protected String doInBackground(String... mac) {
			try {
				cordova.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						int duration = Toast.LENGTH_SHORT;
						String msg = "Connecting ...";
						Toast toast = Toast.makeText(cordova.getActivity().getApplicationContext(), msg, duration);
						toast.show();
					};
				});

				final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
				final String macAddress = mac[0];				
				final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
				dev = btAdapter.getRemoteDevice(macAddress);

				Log.d(TAG, "Stopping Bluetooth discovery.");
				btAdapter.cancelDiscovery();

				sock = dev.createRfcommSocketToServiceRecord(MY_UUID);
				sock.connect();

				BITalinoDevice bitalino = new BITalinoDevice(10000, new int[]{0, 1, 2, 3, 4, 5});
				Log.i(TAG, "Connecting to BITalino [" + macAddress + "] ...");
				bitalino.open(sock.getInputStream(), sock.getOutputStream());
				Log.i(TAG, "Connected.");

				// start acquisition on predefined analog channels
				bitalino.start();

				// read n samples
				final int numberOfSamplesToRead = 1000;
				Log.i(TAG, "Reading " + numberOfSamplesToRead + " samples..");

				while (!isCancelled()) {
					BITalinoFrame[] frames = bitalino.read(numberOfSamplesToRead);
					publishProgress(frames);
				}

				bitalino.stop();
				sock.close();
				Log.i(TAG, "BITalino is stopped.");
				status = "Collecting 10000 frames done!";

			} catch (Exception e) {
				Log.e(TAG, "There was an error. Be sure that Bluetooth is ON and BITalino paired", e);
			}
			return status;
		}

		@Override
		protected void onPostExecute(final String result) {
			cordova.getActivity().runOnUiThread(new Runnable() {
				int duration = Toast.LENGTH_LONG;
				String msg = result;
				public void run() {
					Toast toast = Toast.makeText(cordova.getActivity().getApplicationContext(), msg, duration);
					toast.show();
				};
				// retValue on end
				PluginResult retValueEnd = new PluginResult(PluginResult.Status.OK, msg.toString());
				myCallbackContext.sendPluginResult(retValueEnd);
			});
		}

		@Override
		protected void onProgressUpdate(BITalinoFrame... frames) {
			for (BITalinoFrame frame : frames) {
				// don't overflow series buffer, but maintain some history NOTE: don-t need this for now
				//if (seriesPort1.size() > 2500)
				//	seriesPort1.removeFirst();

				// read value to log and retValue each 3 frames 
				if (frameCounter % 3 == 0){
					Log.i(TAG, "DATA: " + frame);
					PluginResult retValue = new PluginResult(PluginResult.Status.OK, frame.toString());
					retValue.setKeepCallback(true);
					myCallbackContext.sendPluginResult(retValue);

					// read some values to UI via toast for debbuging // turned off
					if (frameCounter < 10) {
						final String msg = frame.toString();
						cordova.getActivity().runOnUiThread(new Runnable() {
							int duration = Toast.LENGTH_SHORT;
							public void run() {
								Toast toast = Toast.makeText(cordova.getActivity().getApplicationContext(), msg, duration);
								//toast.show();
							};
						});
					}
				}
		
				frameCounter++;
			}
		}
	}
}