import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class CustomMark extends Fragment implements OnClickListener {

	private SQLiteLocation currentData;
	
	private EditText textIn;
	private boolean leaveAct = true;
	private TextView tvAddress;
	private Button done;
	private Intent camera;
	private InputMethodManager imm;
	private LocationManager locationManager;
	private Location currentLoc;
	private String provider;
	private ImageButton takePic;
	private List<Address> addresses;
	private Geocoder geocoder;
	private Bitmap btmp;

	public CustomMark() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater
				.inflate(R.layout.fragment_new_location, container, false);
	}

	public void onStart() {
		super.onStart();
		initialize();
	}
	public void onPause() {
		super.onPause();
	}
	public void onResume() {
		super.onResume();
		if(leaveAct)
			textIn.setText(null);
		done.setClickable(true);
	}

	private void initialize() {
		LinearLayout layout = (LinearLayout) getView().findViewById(
				R.id.customFrame);
		LinearLayout content = (LinearLayout) getView().findViewById(
				R.id.contentLay);
		layout.setOnClickListener(this);
		content.setOnClickListener(this);
		TextView welcome = (TextView) getView().findViewById(R.id.welcome);
		welcome.setOnClickListener(this);
		imm = (InputMethodManager) getView().getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		tvAddress = (TextView) getView().findViewById(R.id.tvAddress);
		tvAddress.setOnClickListener(this);
		tvAddress.setText("Cannot find your location");

		textIn = (EditText) getView().findViewById(R.id.etNote);
		textIn.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		textIn.setMaxLines(3);
		textIn.setSingleLine(false);
		textIn.setFocusableInTouchMode(true);

		takePic = (ImageButton) getView().findViewById(R.id.takePic);
		takePic.setOnClickListener(this);

		done = (Button) getView().findViewById(R.id.setLoc);
		done.setOnClickListener(this);

		geocoder = new Geocoder(getView().getContext(), Locale.getDefault());

		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getView().getContext());
		if (status != ConnectionResult.SUCCESS) {
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status,
					getActivity(), requestCode);
			dialog.show();
		} else {
			locationManager = (LocationManager) getView().getContext()
					.getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			provider = locationManager.getBestProvider(criteria, true);
			currentLoc = locationManager.getLastKnownLocation(provider);
			LocationListener locationListener = new LocationListener() {
				public void onLocationChanged(Location location) {
					currentLoc = location;
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
			locationManager.requestLocationUpdates(provider, 10, 10000,
					locationListener);
		}
		try {
			addresses = geocoder.getFromLocation(currentLoc.getLatitude(),
					currentLoc.getLongitude(), 1);
			tvAddress.setText("Your location is roughly "
					+ addresses.get(0).getAddressLine(0) + ", "
					+ addresses.get(0).getAddressLine(1) + ".");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.customFrame:
		case R.id.contentLay:
		case R.id.tvAddress:
		case R.id.welcome:
			imm.hideSoftInputFromWindow(textIn.getWindowToken(), 0);
			break;
		case R.id.takePic:
			leaveAct = false;
			camera = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(camera, 0);
			break;
		case R.id.setLoc:
			imm.hideSoftInputFromWindow(textIn.getWindowToken(), 0);
			final Intent data = new Intent(getView().getContext(), Confirm.class);
			Location myCar = locationManager.getLastKnownLocation(provider);
			String notes = textIn.getText().toString();
			try {
				addresses = geocoder.getFromLocation(myCar.getLatitude(),
						myCar.getLongitude(), 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
			currentData = new SQLiteLocation();
			currentData.setDoubleLat(myCar.getLatitude());
			currentData.setDoubleLng(myCar.getLongitude());
			if (addresses != null)
				currentData.setAddress(addresses.get(0).getAddressLine(0) + ".");
			String notes = textIn.getText().toString();
			currentData.setNote(notes);

			Calendar c = Calendar.getInstance();
			currentData.setDate(c.get(Calendar.MONTH) + 1 + "/"
					+ c.get(Calendar.DAY_OF_MONTH) + "/"
					+ c.get(Calendar.YEAR));
			String am_pm;
			if (c.get(Calendar.AM_PM) == (Calendar.AM))
				am_pm = "a.m.";
			else
				am_pm = "p.m.";
			String hour;
			String tens;
			if (c.get(Calendar.HOUR) == 0)
				hour = "12";
			else
				hour = Integer.toString(c.get(Calendar.HOUR));
			if (c.get(Calendar.MINUTE) < 10)
				tens = "0";
			else
				tens = "";
			currentData.setTime(hour + ":" + tens + c.get(Calendar.MINUTE)
					+ " " + am_pm);

			if (btmp == null) {
				currentData.setPicture(new byte[0]);
			} else {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				btmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] byteArray = stream.toByteArray();
				currentData.setPicture(byteArray);
			}
			currentData.setFavorite(false);
			currentData.setName("");
			
			long dbID = Menu.db.addLocation(currentData);
			data.putExtra("id", dbID);

			leaveAct = true;
			startActivity(data);
			done.setClickable(false);
			break;

		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) { 
			if(btmp == null)
				Toast.makeText(getView().getContext(), "Photo Saved", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(getView().getContext(), "Photo Replaced", Toast.LENGTH_SHORT).show();
			btmp = (Bitmap) data.getExtras().get("data");
		}
	}

}
