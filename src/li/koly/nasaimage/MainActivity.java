package li.koly.nasaimage;

import li.koly.xmlpaser.IotdHandler;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new DownLoadNASARss().execute();
		} else {
			System.out.println("The network connection is not ready!");
		}
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private class DownLoadNASARss extends AsyncTask {
		IotdHandler handler = null;

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			handler = new IotdHandler();
			handler.processFeed();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (handler != null)
				resetDisplay(handler.getTitle(), handler.getDate(),
						handler.getImage(), handler.getDescription());
		}

		private void resetDisplay(String title, String date, Bitmap image,
				StringBuffer description) {
			TextView titleView = (TextView) findViewById(R.id.image_title);
			titleView.setText(handler.getTitle());

			TextView dateView = (TextView) findViewById(R.id.image_date);
			dateView.setText(handler.getDate());

			ImageView imageView = (ImageView) findViewById(R.id.image);
			imageView.setImageBitmap(handler.getImage());

			TextView descriptionView = (TextView) findViewById(R.id.image_description);
			descriptionView.setText(handler.getDescription());

		}
	}
}
