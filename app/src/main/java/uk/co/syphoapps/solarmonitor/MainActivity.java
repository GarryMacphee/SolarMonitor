package uk.co.syphoapps.solarmonitor;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Date;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Simple Activity using Retrofit2 to call an API to fetch
 * solar panel energy readings and display on screen. A timer
 * calls the API server every 60 seconds to update wattage values.
 */
public class MainActivity extends AppCompatActivity
{
	private EnergyValuesModel values;
	private CountDownTimer updateTimer;
	public ProgressBar mProgressBar;
	private static final int INTERVAL_TIMER = 60 * 1000;

	private boolean callInProgress = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getPermissions();

		mProgressBar = findViewById(R.id.progressBar);
		mProgressBar.setVisibility(View.VISIBLE);
		mProgressBar.setProgress(0);

		Button fetchButton = findViewById(R.id.fetchButtonId);
		fetchButton.setOnClickListener(view -> makeApiCall());

		makeApiCall();
	}


	private void getPermissions()
	{
		requestPermissions(new String[]{Manifest.permission.INTERNET}, 1);
	}


	/**
	 * Retrofit2 API call with async callback
	 */
	private void makeApiCall()
	{
		if (!callInProgress)
		{
			Retrofit retrofit = new Retrofit.Builder()
					.baseUrl(Api.BASE_URL)
					.addConverterFactory(GsonConverterFactory.create())
					.build();

			Api api = retrofit.create(Api.class);

			Call<EnergyValuesModel> call = api.getEnergyValues();

			call.enqueue(new Callback<EnergyValuesModel>()
			{
				@Override
				public void onResponse(Call<EnergyValuesModel> call, Response<EnergyValuesModel> response)
				{
					values = response.body();
					refreshDisplay();
					startTimer(INTERVAL_TIMER);
				}

				@Override
				public void onFailure(Call<EnergyValuesModel> call, Throwable t)
				{
					Toast.makeText(MainActivity.this, getResources().getString(R.string.error_api_call_failure), Toast.LENGTH_SHORT)
						 .show();
				}
			});
		}
	}


	private void refreshDisplay()
	{
		EditText time = findViewById(R.id.timeValueId);
		EditText watts = findViewById(R.id.wattsValueId);
		mProgressBar.setProgress(0); //reset progress

		if (values.getTime() != 0)
		{
			time.setText(getReadableDateString(getBaseContext(), values.getTime()));
		}
		else
		{
			time.setText(new Date().toString());
		}

		watts.setText(String.valueOf(values.getValue()));

	}


	private static String getReadableDateString(Context context, long timeInMillis)
	{
		final int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_ABBREV_ALL;

		timeInMillis = timeInMillis * 1000;

		return DateUtils.formatDateTime(context, timeInMillis, flags);
	}


	/**
	 * Set a time limit on how long we can wait for
	 * the update to process. If it takes too long
	 * we can back out and go back to the app.
	 */
	private void startTimer(long time)
	{
		if (updateTimer != null)
		{
			updateTimer.cancel();
		}

		updateTimer = new CountDownTimer(time, 1000)
		{
			int i = 0;

			@Override
			public void onTick(long millisUntilFinished)
			{
				i++;
				mProgressBar.setProgress((int) i);
			}

			@Override
			public void onFinish()
			{
				callInProgress = false;
				makeApiCall();
			}
		}.start();
	}

}
