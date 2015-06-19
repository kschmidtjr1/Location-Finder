import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Confirm extends Activity implements OnClickListener, SensorEventListener{

	private SQLiteLocation pastLoc;
	private long dbId;
	private int favPos;
	
	private GoogleMap gMap;
	private LatLng carPos;
	private Location currentLoc;
	private SensorManager sensorManager;
	private Sensor magnetometer;
	private Sensor accelerometer;
	private Float azimut;
	private CameraPosition camPos;
	private MarkerOptions carOptions;
	private Bitmap icon;
	
	private int screenHeight;
	private Button favorite;
	private Button imageTxt;
	private Button noteTxt;
	private Button getMap;
	private Intent i;
	private float disabled;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		intitialize();
		
		// Set and update current Location
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
		if(status!=ConnectionResult.SUCCESS){
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
			dialog.show();
		}
		else{
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			String provider = locationManager.getBestProvider(criteria, true);
			currentLoc = locationManager.getLastKnownLocation(provider);
			LatLng currentPos = new LatLng(currentLoc.getLatitude(),currentLoc.getLongitude());
			camPos = new CameraPosition(currentPos, 16.0f, 0, 0);
			gMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
			
			LocationListener locationListener = new LocationListener(){
				public void onLocationChanged(Location location){
					drawMarker(location);
					
				}
				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
				}
				@Override
				public void onProviderEnabled(String provider) {
				}
				@Override
				public void onProviderDisabled(String provider) {
				}
			};
			if(currentLoc != null){
				drawMarker(currentLoc);
			}
			locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
		}
		
	}
	
	private void intitialize(){
		// Screen scale
		na = (float) .5;
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenHeight = size.y;
		
		// Get Info From Intent
		i = getIntent();
		dbId = i.getIntExtra("id", 0);
		pastLoc = Menu.db.getLocation(dbId);
		// check that ID is being set correctly
		Log.e("TAG", "PastLoc ID: "+pastLoc.getID());
		if (pastLoc == null) {
			pastLoc = new SQLiteLocation();
			Log.e("TAG", "PastLoc is NULL");
		}
		
		// Define layout
		TextView header = (TextView) findViewById(R.id.info_saved);
		if (pastLoc.getAddress() == null || pastLoc.getAddress().equals(""))
			header.setText("Could not find address of your location.");
		else if (pastLoc.isFavorite() && !pastLoc.getName().equals(""))
			header.setText("Your location saved to " + pastLoc.getName() + " ("
					+ pastLoc.getAddress() + ")");
		else
			header.setText("Your location saved to " + pastLoc.getAddress());
		// note button
		noteTxt = (Button) findViewById(R.id.note);
		if(pastLoc.getNote() == null || pastLoc.getNote().equals("")){
			noteTxt.setText("Add Note");
			noteTxt.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.ic_action_new_event, 0, 0);
		}
		else{
			noteTxt.setText("Edit Note");
			noteTxt.setCompoundDrawablesWithIntrinsicBounds(0, 
					R.drawable.ic_action_view_as_list, 0, 0);
		}
		noteTxt.setOnClickListener(this);
		
		//picture button
		imageTxt = (Button) findViewById(R.id.show_image);
		if(pastLoc.getPicture() == null || pastLoc.getPicture().length==0){
			imageTxt.setAlpha(disabled);
			imageTxt.setClickable(false);
		}
		else{
			imageTxt.setAlpha(1);
			imageTxt.setClickable(true);
			imageTxt.setOnClickListener(this);
		}
		
		//favorite button
		favorite = (Button) findViewById(R.id.favorite);
		if(pastLoc.isFavorite()){
			favorite.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_remove, 0, 0);
			favorite.setText("Delete Favorite");
			favPos = Menu.favoriteList.indexOf(pastLoc);
		}
		else{
			favorite.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_important, 0, 0);
			favorite.setText("Favorite");
		}
		favorite.setOnClickListener(this);
		
		// Initialize Maps Variables
		carPos = new LatLng(pastLoc.getLat(),pastLoc.getLng());
		carOptions = new MarkerOptions().position(carPos).title("Car")
				.snippet(pastLoc.getAddress());
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		// Maps
		getMap = (Button) findViewById(R.id.gps_map);
		getMap.setOnClickListener(this);
		
		gMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		gMap.setMyLocationEnabled(true);
		UiSettings settings = gMap.getUiSettings();
		settings.setCompassEnabled(true);
		settings.setRotateGesturesEnabled(true);
		settings.setZoomGesturesEnabled(true);
		settings.setScrollGesturesEnabled(true);
		
		icon = BitmapFactory.decodeResource(getResources(), R.drawable.gps_icon);
		icon = Bitmap.createScaledBitmap(icon, 100, 100, true);
	}
	
	public void onClick(View v){ //play with animations later
		switch(v.getId()){
		case R.id.gps_map:
			TextView infoSaved = (TextView) findViewById(R.id.info_saved);
			RelativeLayout wrapper = (RelativeLayout) findViewById(R.id.map_wrapper);
			LinearLayout buttons = (LinearLayout) findViewById(R.id.buttons);
			int moveDistance = screenHeight - (buttons.getHeight())
					- (2)*(getActionBar().getHeight());
			wrapper.getLayoutParams().height = moveDistance;
			
			// Open Map
			if(infoSaved.getVisibility() == (View.VISIBLE)){
				infoSaved.setVisibility(View.GONE);
				wrapper.setVisibility(View.VISIBLE);
				getMap.setText("Close Map");
			}
			// Close Map
			else{
				wrapper.setVisibility(View.GONE);
				infoSaved.setVisibility(View.VISIBLE);
				getMap.setText("Open Map");
			}
			break;
			
		// button press events
		case R.id.favorite:
			if(!pastLoc.isFavorite()){
				final EditText input = new EditText(this);
				input.setHint("No name");
				
				new AlertDialog.Builder(Confirm.this)
				.setTitle("Favorite")
				.setMessage(
						"Give your new favorite place a name.")
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if(input.getText().toString().equals(""))
									input.setText("Untitled");
								int repeats = 0;
								for(int i=0; i<Menu.favoriteList.size(); i++){
									if(Menu.favoriteList.get(i).getName().equals(input.getText().toString())
											|| Menu.favoriteList.get(i).getName().equals(input.getText().toString()
													+Integer.toString(repeats))){
										repeats++;
									}
								}
								if(repeats == 0)
									pastLoc.setName(input.getText().toString());
								else
									pastLoc.setName(input.getText().toString()+Integer.toString(repeats));
								pastLoc.setFavorite(true);
								Menu.favoriteList.add(pastLoc);
								Menu.db.updateLocation(pastLoc);
								
								Toast.makeText(Confirm.this, "Favorite Saved",
										Toast.LENGTH_SHORT).show();
								favorite.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_remove, 0, 0);
								favorite.setText("Delete Favorite");
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						})
				.setIcon(android.R.drawable.ic_input_add)
				.setView(input)
				.show();
				
			}
			else{
				pastLoc.setFavorite(false);
				Menu.favoriteList.remove(pastLoc);
				Menu.db.updateLocation(pastLoc);
				
				Toast.makeText(Confirm.this, "Favorite Deleted",
						Toast.LENGTH_SHORT).show();
				favorite.setCompoundDrawablesWithIntrinsicBounds(0,
						R.drawable.ic_action_important, 0, 0);
				favorite.setText("Favorite");
			}
			break;
		case R.id.note:
			final EditText etNote = new EditText(this);
			etNote.setText(pastLoc.getNote());
			new AlertDialog.Builder(Confirm.this)
			.setTitle("Note")
			.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							Toast.makeText(Confirm.this, "Note Updated",
									Toast.LENGTH_SHORT).show();
							if(etNote.getText().toString().equals("")){
								noteTxt.setCompoundDrawablesWithIntrinsicBounds(0,
										R.drawable.ic_action_new_event, 0, 0);
								noteTxt.setText("Add Note");
								pastLoc.setNote("");
							}
							else{
								noteTxt.setCompoundDrawablesWithIntrinsicBounds(0,
										R.drawable.ic_action_view_as_list, 0, 0);
								noteTxt.setText("Edit Note");
								pastLoc.setNote(etNote.getText().toString());
							}
							if(pastLoc.isFavorite())
								Menu.favoriteList.set(favPos, pastLoc);
								//replace favorite with updated object
							Menu.db.updateLocation(pastLoc);
						}
					})
			.setNegativeButton("Delete",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							Toast.makeText(Confirm.this, "Note Deleted",
									Toast.LENGTH_SHORT).show();
							noteTxt.setCompoundDrawablesWithIntrinsicBounds(0,
									R.drawable.ic_action_new_event, 0, 0);
							noteTxt.setText("Add Note");
							pastLoc.setNote("");
							if(pastLoc.isFavorite())
								Menu.favoriteList.set(favPos, pastLoc);
							Menu.db.updateLocation(pastLoc);
						}
					})
			.setNeutralButton(android.R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
						}
					}).setView(etNote).show();
			break;
		case R.id.show_image:
			Intent viewPic = new Intent(Confirm.this, ImageViewer.class);
			viewPic.putExtra("image", pastLoc.getPicture());
			if(pastLoc.isFavorite()){
				viewPic.putExtra("favorite", true);
				viewPic.putExtra("name", pastLoc.getName());
			}
			else{
				viewPic.putExtra("favorite", false);
			}
			viewPic.putExtra("address", pastLoc.getAddress());
			startActivity(viewPic);
			imageTxt.setClickable(false);
		break;
		}
		
	}
	
	public void onPause(){
		super.onPause();
		sensorManager.unregisterListener(this);
	}
	
	public void onResume(){
		super.onResume();
		sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		if(pastLoc.getPicture() != null)
			imageTxt.setClickable(true);
	}
	
	public void onStop(){
		super.onStop();
	}
	
	private void drawMarker(Location location) {
		gMap.clear();
		LatLng pos = new LatLng(location.getLatitude(),location.getLongitude());
		if(azimut != null) {
			gMap.addMarker(new MarkerOptions().position(pos)
					.icon(BitmapDescriptorFactory.fromBitmap(icon))
					.flat(true).anchor((float).5, (float).5)
					.rotation((float)Math.toDegrees(azimut)));
		}
		gMap.addMarker(carOptions).showInfoWindow();
	}
	
	float[] mGravity;
	float[] mGeomagnetic;

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			mGravity = event.values;
		if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			mGeomagnetic = event.values;
		if(mGravity != null && mGeomagnetic != null){
			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R,I,mGravity,mGeomagnetic);
			if(success){
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				azimut = orientation[0];
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
