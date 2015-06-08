import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageViewer extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewer);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		ImageView iv = (ImageView) findViewById(R.id.picture);
		TextView tv = (TextView) findViewById(R.id.title);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int screenHeight = size.y;
		int screenWidth = size.x;
		int imageHeight = screenHeight - getActionBar().getHeight() - tv.getHeight();
		int imageWidth = screenWidth;

		if(getIntent().getBooleanExtra("favorite", false))
			tv.setText(getIntent().getStringExtra("name"));
		else 
			tv.setText(getIntent().getStringExtra("address"));
		
		byte[] byteArray = getIntent().getByteArrayExtra("image");
		Bitmap btmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		btmp = Bitmap.createScaledBitmap(btmp, imageWidth, imageHeight, true);
		iv.setImageBitmap(btmp);
	}
}
