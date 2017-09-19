/**
 */
package com.projects.acs;

import org.apache.cordova.CallbackContext;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class AcsSmsPlugin extends CordovaPlugin{
  private static final String TAG = "AcsSmsPlugin";

  public final String ACTION_HAS_PERMISSION = "has_permission";
  
	public final String ACTION_READ_SMS = "read";

	private CallbackContext callbackContext;

  private JSONArray args;
  
  public String smsText;

  private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

  private static final String TAG_1 = "SMSBroadcastReceiver";


  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);

    Log.d(TAG, "Initializing AcsSmsPlugin");
  }

  @Override
	public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
		this.callbackContext = callbackContext;
		this.args = args;
		if (action.equals(ACTION_READ_SMS)) {
			if (hasPermission()) {
				readSMS();
			} else {
				requestPermission();
			}
			return true;
		}
		else if (action.equals(ACTION_HAS_PERMISSION)) {
			callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, hasPermission()));
			return true;
		}
		return false;
	}

  private boolean hasPermission() {
		return cordova.hasPermission(android.Manifest.permission.SEND_SMS);
	}

	private void requestPermission() {
		cordova.requestPermission(this, SEND_SMS_REQ_CODE, android.Manifest.permission.SEND_SMS);
  }
  
  public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
		for (int r : grantResults) {
			if (r == PackageManager.PERMISSION_DENIED) {
				callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "User has denied permission"));
				return;
			}
		}
		readSMS();
  }
  
  private String readSms(){
      if (!checkSupport()) {
          callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "SMS not supported on this platform"));
          smsIntent = new Intent(Intent.SMS_RECEIVED);
          smsText = read(this.cordova.getActivity(), smsIntent);
          callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, smsText));
        }
      }

  private String read(Context context, Intent intent){					

        Log.i(TAG_1, "Intent received: " + intent.getAction());

        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[])bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                }
                if (messages.length > -1) {
                    return messages[0].getMessageBody();
                }
            } else{
              Log.i(TAG_1, "Intent received: " + 'Read FAILED');
            }
        }
  }

  private boolean checkSupport() {
		Activity ctx = this.cordova.getActivity();
		return ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
	}
}
