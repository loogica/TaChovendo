package com.example.tachovendo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Iterator;

public class MainActivity extends FragmentActivity {

	private GoogleMap googleMap;

	@SuppressLint("NewApi")
	public String getStringContent(String uri) throws Exception {
		try {

			Thread.currentThread().setContextClassLoader(
					this.getClass().getClassLoader());

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(uri));
			HttpResponse response = client.execute(request);
			InputStream ips = response.getEntity().getContent();
			BufferedReader buf = new BufferedReader(new InputStreamReader(ips,
					"UTF-8"));

			StringBuilder sb = new StringBuilder();
			String s;
			while (true) {
				s = buf.readLine();
				if (s == null || s.length() == 0)
					break;
				sb.append(s);

			}
			buf.close();
			ips.close();
			return sb.toString();

		} finally {
			// any cleanup code...
		}
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_map);
		GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());

		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);

		googleMap = fm.getMap();

		String data;
		try {
			data = this
					.getStringContent("http://www0.rio.rj.gov.br/alertario/upload/Mapa/mc.json");

			JSONObject jObject = new JSONObject(data);

			Iterator<String> it = jObject.keys();
			while (it.hasNext()) {
				String key = it.next();
				JSONObject pluv = jObject.getJSONObject(key);
				try {
					googleMap.addMarker(new MarkerOptions().position(
							new LatLng(pluv.getDouble("lat"), pluv
									.getDouble("long"))).title(
							pluv.getString("name")));
				} catch (Exception e) {

				}

			}

			new AlertDialog.Builder(this)
					.setTitle("Delete entry")
					.setMessage(data)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// continue with delete
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// do nothing
								}
							}).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}