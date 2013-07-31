package com.makewithmoto.apprunner.api;

import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apidoc.APIAnnotation;
import com.makewithmoto.network.CustomWebsocketServer;

public class JWebApp extends JInterface {

	String TAG = "JWebApp";

	public JWebApp(Activity a) {
        super(a); 
	}

	@JavascriptInterface 
    @APIAnnotation(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public void addWidget(String name, int x, int y, int w, int h) {
		
		JSONObject msg = new JSONObject(); 
		try {
			msg.put("type", "widget");
			msg.put("action", "add");

			JSONObject values = new JSONObject();
			values.put("name", name);
			values.put("type", "plot");
			values.put("x", x);
			values.put("y", y);
			values.put("w", w);
			values.put("h", h);
			
			msg.put("values", values);
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		Log.d(TAG, "added widget ");
		
		try {
			CustomWebsocketServer ws = CustomWebsocketServer.getInstance(a.get());
			ws.send(msg);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	

	}
}
