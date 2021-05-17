package com.example.app_babilonia.api.client;

import com.example.app_babilonia.api.bean.CityBean;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface CityService {
    @GET("/cities")
    Call<List<CityBean>> getCities();
    @PUT("/cities")
    Call<List<CityBean>> saveCity(@Body CityBean city);
}