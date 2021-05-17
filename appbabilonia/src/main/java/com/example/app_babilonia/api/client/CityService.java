package com.example.app_babilonia.api.client;

import com.example.app_babilonia.api.bean.CityBean;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
public interface CityService {
    @GET("/cities")
    Call<List<CityBean>> getCities();
}