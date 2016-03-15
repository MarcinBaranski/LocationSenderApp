package com.wat.locationsenderapp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wat.locationsenderapp.DetektorPrzeciazenia.OnShakeListener;

public class MainActivity extends Activity implements SensorEventListener {
	// Logger logger = ;
	
	Sensor accelerometer;
	SensorManager sensorManager;
	
//	private SensorManager mSensorManager;
//	private Sensor mAccelerometer;
//	private DetektorPrzeciazenia mShakeDetector;
//	
	TextView acceleration;
	
	LocationManager lm;
	static double longitude = 0.0;
	static double latitude = 0.0;
	Timer timer = new Timer();
	private static String adresIp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		int i = 2;
		
		//Obs³uga sensora pomiaru przypieszenia
		
		sensorManager= (SensorManager)getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, accelerometer, i);
		
		acceleration = (TextView)findViewById(R.id.acceleration);
		
		
		//Druga wersja
		
//		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//		mAccelerometer = mSensorManager
//				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//		mShakeDetector = new DetektorPrzeciazenia();
//		mShakeDetector.setOnShakeListener(new OnShakeListener() {
// 
//			@Override
//			public void onShake(int count) {
//				/*
//				 * The following method, "handleShakeEvent(count):" is a stub //
//				 * method you would use to setup whatever you want done once the
//				 * device has been shook.
//				 */
//				//handleShakeEvent(count);
//			System.out.println("acceleration:////////////////////");
//				acceleration.setText("Przeciazenie: "+mShakeDetector.gForce);
//			}
//		
//		});
//		mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
//		
		// Przygotowanie filtru adresu IP
		
		InputFilter[] filters = new InputFilter[1];
		filters[0] = new InputFilter() {
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
				if (end > start) {
					String destTxt = dest.toString();
					String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
					adresIp = resultingTxt;
					if (!resultingTxt.matches ("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) { 
						Toast.makeText(getApplicationContext(),"B³êdne dane",Toast.LENGTH_SHORT).show();
						return "";
					} else {
						String[] splits = resultingTxt.split("\\.");
						for (int i=0; i<splits.length; i++) {
							if (Integer.valueOf(splits[i]) > 255) {
								Toast.makeText(getApplicationContext(),"B³êdne dane",Toast.LENGTH_SHORT).show();
								return "";
							}
						}
					}
				}
				return null;
			}
		};
		

		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener ll = new mylocationlistener();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);

		final EditText adrIp = (EditText) findViewById(R.id.ip_address);
		adrIp.setFilters(filters);
//		adrIp.addTextChangedListener(new TextWatcher() {
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//			}
//
//			private String mPreviousText = "";
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				if (PARTIAl_IP_ADDRESS.matcher(s).matches()) {
//					mPreviousText = s.toString();
//					Log.i(getLocalClassName(), "Adres IP: " + mPreviousText);
//					int length = mPreviousText.length();
//					if (length <= 15 || length > 0) {
//						adresIp = mPreviousText;
//					} else {
//						Toast.makeText(getApplicationContext(),
//								"Maksymalny rozmiar adresu IP",
//								Toast.LENGTH_SHORT).show();
//					}
//
//				} else {
//					s.replace(0, s.length(), mPreviousText);
//					Log.i(getLocalClassName(), "Error adres IP zly");
//				}
//			}
//		});

		Button stopBtn = (Button) findViewById(R.id.stopButton);
		Button executionBtn = (Button) findViewById(R.id.execButton);
		executionBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			
				timer.scheduleAtFixedRate(new TimerTask() {
					@Override
					public void run() {
						execute();
					}
				}, 0, 5000);// put here time 1000 milliseconds=1 second

			}
		});
		stopBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				timer.cancel();
				Timer timer = new Timer();

			}
		});

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

	// public void writeJson(){
	//
	// try{
	// json.put("longitude", 21);
	// json.put("latiude", "lati");
	// }
	// catch(JSONException e){
	// System.out.println("Error przy tworzeniu obiektu JSON");
	// }
	//
	// }

	public static void execute() {

		Gson gson = new Gson();
		Location location = new Location();
		String latitudeString = String.valueOf(latitude);
		String longitudeString = String.valueOf(longitude);
	
		location.setLatitude(latitudeString);
		location.setLongitude(longitudeString);
		String jsonResult = gson.toJson(location);
		makeRequest("http://"+adresIp+":8080/locationListener", jsonResult);
		 System.out.println( "Adres IP: " + adresIp);
	}

	public static HttpResponse makeRequest(String uri, String json) {
		try {
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setEntity(new StringEntity(json));
			// httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			return new DefaultHttpClient().execute(httpPost);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Metody walidacji adresu IP

//	public boolean validate(final String ip) {
//		pattern = Pattern.compile(IPADDRESS_PATTERN);
//		matcher = pattern.matcher(ip);
//		return matcher.matches();
//	}

	private class mylocationlistener implements LocationListener {

		@Override
		public void onLocationChanged(android.location.Location location) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();

		}

		
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		acceleration.setText("X: "+ event.values[0] + "Y: "+ event.values[1] +" Z: "+ event.values[2]);
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
}

