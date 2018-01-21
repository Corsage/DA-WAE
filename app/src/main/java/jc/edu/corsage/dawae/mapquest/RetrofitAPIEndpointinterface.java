package jc.edu.corsage.dawae.mapquest;

import java.util.ArrayList;

import io.reactivex.Observable;
import jc.edu.corsage.dawae.mapquest.models.Kirito;
import jc.edu.corsage.dawae.mapquest.models.Result;
import jc.edu.corsage.dawae.mapquest.models.Route;
import jc.edu.corsage.dawae.mapquest.models.Search;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * RetroFit API Endpoint Interface for MapQuest.
 * This holds all map-related endpoints.
 * Base URL: http://www.mapquestapi.com/
 */

public interface RetrofitAPIEndpointinterface {
    @GET("directions/v2/route")
    Observable<Kirito> getRoute(@Query("key") String API_KEY, @Query("from") String from, @Query("to") String to);

    @GET("search/v3/prediction")
    Observable<Search> getSearchResults(@Query("key") String API_KEY, @Query("limit") int limit, @Query("collection") String collections, @Query("q") String query);

}
