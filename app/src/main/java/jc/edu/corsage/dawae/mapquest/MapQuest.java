package jc.edu.corsage.dawae.mapquest;

import java.util.ArrayList;

import io.reactivex.Observable;
import jc.edu.corsage.dawae.mapquest.models.Kirito;
import jc.edu.corsage.dawae.mapquest.models.Result;
import jc.edu.corsage.dawae.mapquest.models.Route;
import jc.edu.corsage.dawae.mapquest.models.Search;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by j3chowdh on 1/20/2018.
 */

public class MapQuest {
    private String TAG ="MapQuest";

    private String BASE_URL = "http://www.mapquestapi.com/";

    private String API_KEY = "hVFeNPqxGPaxUWVMBVFyTzIkyOXsRGDK";

    private RetrofitAPIEndpointinterface mapQuestAPI;

    public MapQuest() {
        Retrofit retrofitMapQuest = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mapQuestAPI = retrofitMapQuest.create(RetrofitAPIEndpointinterface.class);
    }

    /* Public API */

    public Observable<Kirito> getRoute(String from, String to) {
        return mapQuestAPI.getRoute(API_KEY, from, to);
    }

    public Observable<Search> getSearchResults(String query) {
        return mapQuestAPI.getSearchResults(API_KEY, 5, "adminArea,poi,address,category,franchise,airport", query);
    }

}
