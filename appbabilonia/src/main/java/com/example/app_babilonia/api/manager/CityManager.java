package com.example.app_babilonia.api.manager;

import com.example.app_babilonia.api.bean.CityBean;
import com.example.app_babilonia.api.client.CityService;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class CityManager {

    private static CityManager cityManager;

    private CityManager(){}

    public static CityManager getInstance(){
        return cityManager == null ? new CityManager() : cityManager;
    }

    public List<CityBean> save(CityBean cityBean){
        final List<CityBean> cities = new ArrayList<>();
        CityService service = this.getCallBack();
        service.saveCity(cityBean).enqueue(new Callback<List<CityBean>>() {
            @Override
            public void onResponse(Call<List<CityBean>> call, Response<List<CityBean>> response) {
                Logger.getLogger("log").info("Estado: "+response.message());
                if(response.isSuccessful()){
                    System.out.println(response.body());
                    cities.addAll(response.body());
                    Logger.getLogger("log").info("respuesta"+response.body().toString());
                }
            }
            @Override
            public void onFailure(Call<List<CityBean>> call, Throwable t) {
                Logger.getLogger("log").info(t.getMessage());
            }
        });
        return cities;
    }

    public List<CityBean> getCities(){
        final List<CityBean> cities = new ArrayList<>();
        CityService service = this.getCallBack();
        service.getCities().enqueue(new Callback<List<CityBean>>() {
            @Override
            public void onResponse(Call<List<CityBean>> call, Response<List<CityBean>> response) {
                Logger.getLogger("log").info("Estado: "+response.message());
                if(response.isSuccessful()){
                    System.out.println(response.body());
                    cities.addAll(response.body());
                    Logger.getLogger("log").info("respuesta"+response.body().toString());
                }
            }
            @Override
            public void onFailure(Call<List<CityBean>> call, Throwable t) {
                Logger.getLogger("log").info(t.getMessage());
            }
        });
        return cities;
    }

    public CityService getCallBack(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit.create(CityService.class);
    }

}
