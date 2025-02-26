
package com.xubop961.niamniamapp.api;

import com.xubop961.niamniamapp.api.RegisterRequest;
import com.xubop961.niamniamapp.api.RegisterUser;  // O crea una clase User de respuesta si la tienes definida

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface  RegisterApi {


    @POST("api/users/create")
    Call<RegisterUser> createUser(@Body RegisterRequest registerRequest);
}
