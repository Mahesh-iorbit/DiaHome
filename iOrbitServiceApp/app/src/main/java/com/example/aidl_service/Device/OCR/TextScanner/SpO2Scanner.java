package com.example.aidl_service.Device.OCR.TextScanner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.ContextThemeWrapper;

import com.example.aidl_service.Device.CommonDataArea;
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


public class SpO2Scanner extends TextCameraScanActivity {


    private int spo2;
    private int pulse;
    private AppDialogConfig config;
    private Handler handler = new Handler();
    private boolean measurementSaved = false;
    private ProgressDialog progressDialog;
    private boolean oxygenLevelRecognized = false;
    private boolean isScanning = false;
    ImageView gif;
    protected ViewfinderView viewfinderView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler.postDelayed(stopScanRunnable, 30000); // 15 seconds
        isScanning = true;
    }

    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            if (oxygenLevelRecognized) {
                return;
            }
            if (!measurementSaved) {


                showSuccess("Make sure the background is clear", "Timeout-Measure Not Found");

            }
            isScanning = false;
        }
    };

    @Override
    public void onScanResultCallback(AnalyzeResult<Text> result) {
        if (!isScanning) {
            return;
        }
        if (oxygenLevelRecognized) {
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
        //Pattern pattern = Pattern.compile("PULSE OXIMETER\\s+(\\d+)\\s+(\\d+)");
        //Pattern pattern = Pattern.compile("PULSE OXIMETER\\s+([1-9]\\d*)\\s+([1-9]\\d*)");
        //Pattern pattern = Pattern.compile("PULSE OXIMETER\\s+([1-9]\\d*)\\s+([1-9]\\d+)");
        //Pattern pattern = Pattern.compile("PULSE OXIMETER\\s+([1-9]|[1-9][0-9])\\s+([1-9]\\d+)");
        Pattern pattern = Pattern.compile(CommonDataArea.SUPPORTED_PATTERN_PULSEOXIMETER);


        Matcher matcher = pattern.matcher(recognizedText);

        if (matcher.find()) {
            String oxygenValueString = matcher.group(1);
            String pulseValueString = matcher.group(2);

            try {
                spo2 = Integer.parseInt(oxygenValueString);
                pulse = Integer.parseInt(pulseValueString);
            } catch (NumberFormatException e) {
                // Handle the case where the string is not a valid integer
                Log.e("TAG", "Invalid oxygenValueString : " + oxygenValueString + "pulseValueString :" + pulseValueString);
                return;
            }
            //  glucoseUnit = "mg/dl";
            Log.d("TAG", "values: spo2: " + spo2 + "\npulse- " + pulse);
            showGlucoseMeasurementResult(btnRescan);
            oxygenLevelRecognized = false;
            getCameraScan().setAnalyzeImage(false);
        }
    }

    private void showResultDialog() {
        config = new AppDialogConfig(SpO2Scanner.this, R.layout.text_result_dialog);
        config.setOnClickCancel(v -> {
            AppDialog.INSTANCE.dismissDialog();
            handler.removeCallbacksAndMessages(null);
            SpO2Scanner.this.finish();
            finish();
        });
        config.setOnClickConfirm(v -> {
            saveMeasurement(spo2,pulse);
            measurementSaved = true;
        });
        AppDialog.INSTANCE.showDialog(config, false);
    }


    private void updateDialogContent(String text) {
        if (config == null) {
            return;
        }
        viewfinderView = config.getView(R.id.viewfinderView);
        TextView tvDialogContent = config.getView(R.id.tvDialogContent);
        tvDialogContent.setText(text);
        // tvDialogContent.setVisibility(View.INVISIBLE);
        tvDialogContent.setMovementMethod(ScrollingMovementMethod.getInstance());
    }


    private void showGlucoseMeasurementResult(Button btnRescan) {
        TextView tvDialogTitle = config.getView(R.id.tvDialogTitle);
        tvDialogTitle.setText("Pulse Oximeter Measurement");
        TextView tvDialogContent = config.getView(R.id.tvDialogContent);
        // tvDialogContent.setVisibility(View.VISIBLE);
        tvDialogContent.setText("Oxygen Level: " + spo2 + "% " + " Pulse: " + pulse + " bpm");
        btnRescan.setVisibility(View.VISIBLE);
        Button btnConfirm = config.getView(R.id.btnDialogConfirm);
        btnConfirm.setVisibility(View.VISIBLE);
        Button btnCancel = config.getView(R.id.btnDialogCancel);
        btnCancel.setVisibility(View.INVISIBLE);
        viewfinderView.setVisibility(View.GONE);
        // gif.setVisibility(View.GONE);


        btnRescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oxygenLevelRecognized = false;
                getCameraScan().setAnalyzeImage(true);
                TextView tvDialogTitle = config.getView(R.id.tvDialogTitle);
                tvDialogTitle.setText("Rescanning for Measurement");
                btnRescan.setVisibility(View.GONE);
                Button btnConfirm = config.getView(R.id.btnDialogConfirm);
                btnConfirm.setVisibility(View.GONE);
                Button btnCancel = config.getView(R.id.btnDialogCancel);
                btnCancel.setVisibility(View.VISIBLE);
                viewfinderView.setVisibility(View.VISIBLE);
                // gif.setVisibility(View.VISIBLE);
            }
        });

        handler.removeCallbacks(stopScanRunnable);
        handler.postDelayed(stopScanRunnable, 30000);
    }
//

    @Override
    public Analyzer<Text> createAnalyzer() {
        return new TextRecognitionAnalyzer(new TextRecognizerOptions.Builder().build());
    }


    public void saveMeasurement(int spo2, int pulse) {
        //  progressDialog.setTitle("Please Wait...");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        int[] intVal = {spo2, pulse};
        String[] BpName = {"oxygen_level", "BPM"};
        RetrofitClient retrofit = new RetrofitClient();
        Retrofit retrofitClient = retrofit.getRetrofitInstance(this);
        if (retrofitClient == null) {
            return;
        }

        for (int i = 0; i < intVal.length; i++) {
            SaveMeasureModel measure = new SaveMeasureModel();
            measure.setParamName(BpName[i]);
            measure.setParamFraction("");
            measure.setDevmodelId("2ab90e73-99c5-11eb-853f-e9af88721123");
            measure.setDevId("852a2034-c8dd-11eb-a396-755a8569ff4d");
            measure.setIntVal(String.valueOf(intVal[i]));
            measure.setPatientId("1aa0001");
            Call<StatusResponseModel> call = retrofitClient.create(ServiceApi.class).SaveMeasure("1aa0001", measure);
            call.enqueue(new Callback<StatusResponseModel>() {
                @Override
                public void onResponse(Call<StatusResponseModel> call, Response<StatusResponseModel> response) {
                    if (response.isSuccessful()) {
                        StatusResponseModel saveMeasureModel = new StatusResponseModel();
                        saveMeasureModel = response.body();
                        if (saveMeasureModel.getStatus().getMessage().equalsIgnoreCase("Success")) {
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

        }
    }


    private void showSuccess(String msg, String Title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(SpO2Scanner.this,R.style.Widget_AppCompat_ButtonBar_AlertDialog));
        builder.setTitle(Title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                AppDialog.INSTANCE.dismissDialog();


            }
        });
        builder.show();

    }
}