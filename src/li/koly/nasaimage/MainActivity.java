package li.koly.nasaimage;

import li.koly.xmlpaser.IotdHandler;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	IotdHandler handler = null;
	Handler androidHandler = null;

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
		androidHandler = new Handler();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private class DownLoadNASARss extends AsyncTask {

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			if (handler == null)
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

	public void refreshFromFeed(View view) {
		final Dialog dialog = ProgressDialog.show(this, "Loading",
				"Loading the image of the day");
		Thread th = new Thread() {
			@Override
			public void run() {
				if (handler == null)
					handler = new IotdHandler();
				handler.processFeed();
				androidHandler.post(new Runnable() {
					@Override
					public void run() {
						resetDisplay(handler.getTitle(), handler.getDate(),
								handler.getImage(), handler.getDescription());
						dialog.dismiss();
					}
				});
			}
		};
		th.start();
	}
}
