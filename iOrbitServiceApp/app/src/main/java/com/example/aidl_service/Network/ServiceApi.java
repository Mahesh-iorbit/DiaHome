package com.example.aidl_service.Network;

import static com.example.aidl_service.Network.RetrofitClient.TOKEN;

import com.example.aidl_service.Model.SaveMeasureModel;
import com.example.aidl_service.Model.StatusResponseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface ServiceApi {

    @Headers({"Authorization: Bearer "+ TOKEN})
    @POST("patientId/{patientId}/savemeasurement")
    Call<StatusResponseModel>SaveMeasure(@Path(value="patientId")
                                     String patientId,
                                         @Body SaveMeasureModel measureModel);


}
