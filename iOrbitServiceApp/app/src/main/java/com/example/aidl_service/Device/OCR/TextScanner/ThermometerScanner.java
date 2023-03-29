package com.example.aidl_service.Device.OCR.TextScanner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.ContextThemeWrapper;

import com.example.aidl_service.Device.OCR.camera.AnalyzeResult;
import com.example.aidl_service.Device.OCR.camera.analyze.Analyzer;
import com.example.aidl_service.Device.OCR.text.TextCameraScanActivity;
import com.example.aidl_service.Device.OCR.text.ViewfinderView;
import com.example.aidl_service.Device.OCR.text.analyze.TextRecognitionAnalyzer;
import com.example.aidl_service.Model.SaveMeasureModel;
import com.example.aidl_service.Model.StatusResponseModel;
import com.example.aidl_service.Network.RetrofitClient;
import com.example.aidl_service.Network.ServiceApi;
import com.example.aidl_service.R;
import com.example.aidl_service.Utils.Utils;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.king.app.dialog.AppDialog;
import com.king.app.dialog.AppDialogConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ThermometerScanner  extends TextCameraScanActivity {

    private Float tempValue;
    private String tempUnit;
    private boolean measurementSaved = false;
    private boolean glucoseLevelRecognized = false;
    private final Handler handler = new Handler();
    private AppDialogConfig config = null;
    private ProgressDialog progressDialog;
    private boolean isScanning = false;
    private final boolean stopScanRunnableCanceled = false;
    protected ViewfinderView viewfinderView;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler.postDelayed(stopScanRunnable, 30000); // 15 seconds
        isScanning = true;


    }

    private final Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            if (glucoseLevelRecognized) {
                return;
            }
            if (!measurementSaved) {


                showSuccess("Tips:Make sure the background is clear","Timeout-Measure Not Found");

            }
            isScanning = false;
        }
    };



    @Override
    public void onBackPressed() {
        // Check if the dialog is showing


        finish();
        AppDialog.INSTANCE.dismissDialog();

        // Call the default implementation of onBackPressed
        super.onBackPressed();
    }

    @Override
    public void onScanResultCallback(AnalyzeResult<Text> result) {
        if (!isScanning) {
            return;
        }
        if (glucoseLevelRecognized) {
            return;
        }

        if (result.getResult().getText().isEmpty()) {
            return;
        }

        if (config == null && !measurementSaved) {
            showResultDialog();
        }
        updateDialogContent(result.getResult().getText());

        if (measurementSaved) {
            return;
        }

        Button btnRescan = config.getView(R.id.btnRescan);
        String recognizedText = result.getResult().getText();
        Log.d("TAG", "onScanResultCallback: "+recognizedText);
        //Pattern pattern = Pattern.compile("^(?!\\d$)(\\d{3})(\\d{1})$");
        //Pattern pattern = Pattern.compile("^(?!\\d$)(\\d{2,3})(\\d{1})$|^(?!\\d$)(\\d{4})(\\d{1})$");
      //  Pattern pattern = Pattern.compile("^(?!0*\\.?0+$)([5-9]\\d{2}|[1-9]\\d{3})(\\.\\d)?$");
        Pattern pattern = Pattern.compile("^(?!0\\d)(?!\\d$)(\\d{2,3})(\\d{1})$|^(?!0\\d)(?!\\d$)(\\d{4})(\\d{1})$");


        //Pattern pattern = Pattern.compile(CommonDataArea.SUPPORTED_PATTERN_THERMOGUN);


        Matcher matcher = pattern.matcher(recognizedText);

        if (matcher.find()) {
            String tempValueString = matcher.group(1) + "." + matcher.group(2);
            try {
                tempValue = Float.parseFloat(tempValueString);
            } catch (NumberFormatException e) {
                // Handle the case where the string is not a valid float
                Log.e("TAG", "Invalid temp value: " + tempValueString);
                return;
            }
            tempUnit = "°F";
            Log.d("TAG", "Temp measurement: " + tempValue + " " + tempUnit);
            showTempMeasurementResult(btnRescan);
            glucoseLevelRecognized = false;
            getCameraScan().setAnalyzeImage(false);
        }
    }


    private void showResultDialog() {
        config = new AppDialogConfig(ThermometerScanner.this, R.layout.text_result_dialog);
        config.setOnClickCancel(v -> {
            AppDialog.INSTANCE.dismissDialog();
            handler.removeCallbacksAndMessages(null);
            ThermometerScanner.this.finish();

        });
        config.setOnClickConfirm(v -> {

            saveMeasurement(tempValue);
            measurementSaved = true;
        });
        AppDialog.INSTANCE.showDialog(config, false);


    }

    private void updateDialogContent(String text) {
        if (config == null) {
            return;
        }
        viewfinderView=config.getView(R.id.viewfinderView);
        //  gif = config.getView(R.id.loading_indicator);
        // Glide.with(getApplicationContext()).load(R.drawable.scanner).into(gif);
        TextView tvDialogContent = config.getView(R.id.tvDialogContent);
        tvDialogContent.setText(text);
        tvDialogContent.setVisibility(View.INVISIBLE);
        tvDialogContent.setMovementMethod(ScrollingMovementMethod.getInstance());

    }

    private void showTempMeasurementResult(Button btnRescan) {
        TextView tvDialogTitle = config.getView(R.id.tvDialogTitle);
        tvDialogTitle.setText("Temperature Measurement");
        TextView tvDialogContent = config.getView(R.id.tvDialogContent);
        tvDialogContent.setVisibility(View.VISIBLE);
        tvDialogContent.setText("Temperature: " + tempValue + " " + tempUnit);
        btnRescan.setVisibility(View.VISIBLE);
        Button btnConfirm=  config.getView(R.id.btnDialogConfirm);
        btnConfirm.setVisibility(View.VISIBLE);
        Button btnCancel=  config.getView(R.id.btnDialogCancel);
        btnCancel.setVisibility(View.INVISIBLE);
        // gif.setVisibility(View.GONE);
        viewfinderView.setVisibility(View.GONE);

        btnRescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                glucoseLevelRecognized = false;
                getCameraScan().setAnalyzeImage(true);
                TextView tvDialogTitle = config.getView(R.id.tvDialogTitle);
                tvDialogTitle.setText("Rescanning for Measurement");
                btnRescan.setVisibility(View.GONE);
                Button btnConfirm=  config.getView(R.id.btnDialogConfirm);
                btnConfirm.setVisibility(View.GONE);
                Button btnCancel=  config.getView(R.id.btnDialogCancel);
                btnCancel.setVisibility(View.VISIBLE);
                // gif.setVisibility(View.VISIBLE);
                viewfinderView.setVisibility(View.VISIBLE);

            }
        });

        handler.removeCallbacks(stopScanRunnable);
        handler.postDelayed(stopScanRunnable, 30000);
    }

    @Override
    public Analyzer<Text> createAnalyzer() {
        return new TextRecognitionAnalyzer(new TextRecognizerOptions.Builder().build());
    }

    public void saveMeasurement(Float tempValue) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        float intVal = tempValue;
        String BpName = "temperature";
        if(Utils.isConnected(this)){
            RetrofitClient retrofit = new RetrofitClient();
            Retrofit retrofitClient = retrofit.getRetrofitInstance(this);
            if (retrofitClient == null) {
                return;
            }

            SaveMeasureModel measure = new SaveMeasureModel();
            measure.setParamName(BpName);
            measure.setParamFraction("");
            measure.setDevmodelId("2ab90e73-99c5-11eb-853f-e9af88721123");
            measure.setDevId("852a2034-c8dd-11eb-a396-755a8569ff4d");
            measure.setIntVal(String.valueOf(intVal));
            measure.setPatientId("1aa0001");
            Call<StatusResponseModel> call = retrofitClient.create(ServiceApi.class).SaveMeasure("1aa0001", measure);
            call.enqueue(new Callback<StatusResponseModel>() {
                @Override
                public void onResponse(Call<StatusResponseModel> call, Response<StatusResponseModel> response) {
                    if (response.isSuccessful()) {
                        StatusResponseModel saveMeasureModel = new StatusResponseModel();
                        saveMeasureModel = response.body();
                        if (saveMeasureModel.getStatus().getMessage().equalsIgnoreCase("Success")) {
                            progressDialog.dismiss();
                            showSuccess("Measurement has been saved successfully","Measure Saved");
                        } else {
                            Toast.makeText(getApplicationContext(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<StatusResponseModel> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            showSuccess("Check Your Internet Connection","NO INTERNET");
        }

    }


    private void showSuccess(String msg, String title){
//Base_ThemeOverlay_AppCompat_Dialog_Alert {-White Color}
        //Base_Theme_MaterialComponents_Dialog_Alert {-Black}
        try{
            AlertDialog.Builder builder =new AlertDialog.Builder(new ContextThemeWrapper(ThermometerScanner.this, R.style.Widget_AppCompat_ButtonBar_AlertDialog));
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", (dialogInterface, i) ->
                    finish());
            AppDialog.INSTANCE.dismissDialog();
            builder.show();
        } catch (
                WindowManager.BadTokenException e) {
            e.printStackTrace();
        }

    }


}
