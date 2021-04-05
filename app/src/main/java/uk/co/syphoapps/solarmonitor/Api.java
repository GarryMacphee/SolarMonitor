package uk.co.syphoapps.solarmonitor;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Api
    {
        String BASE_URL = "http://url/to/your/server:8080/emoncms/";

        @GET("feed/timevalue.json?id=YOUR_API_KEY_HERE")
        Call<EnergyValuesModel> getEnergyValues();
    }
