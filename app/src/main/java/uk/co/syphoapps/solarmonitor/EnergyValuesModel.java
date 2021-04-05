package uk.co.syphoapps.solarmonitor;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class EnergyValuesModel
{
	@SerializedName("time")
	private long time;


	@SerializedName("value")
	private int value;


	public EnergyValuesModel(long time, int value)
	{
		Log.d(getClass().getSimpleName(), "time: " + time + " watts: " + value);
		this.time = time;
		this.value = value;
	}

	public long getTime()
	{
		return time;
	}


	public void setTime(long time)
	{
		this.time = time;
	}


	public int getValue()
	{
		return value;
	}


	public void setValue(int value)
	{
		this.value = value;
	}
}
