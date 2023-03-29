/*
 * Copyright (C) Jenly, MLKit Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.aidl_service.Device.OCR.camera;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;

import com.example.aidl_service.Device.OCR.camera.analyze.Analyzer;
import com.example.aidl_service.Device.OCR.camera.config.CameraConfig;


/**
 * 相机扫描基类定义；内置的默认实现见：{@link BaseCameraScan}
 * <p>
 * 快速实现扫描识别主要有以下几种方式：
 * <p>
 * 1、通过继承 {@link BaseCameraScanActivity}或者{@link BaseCameraScanFragment}或其子类，可快速实现扫描识别。
 * （适用于大多数场景，自定义布局时需覆写getLayoutId方法）
 * <p>
 * 2、在你项目的Activity或者Fragment中实例化一个{@link BaseCameraScan}。（适用于想在扫码界面写交互逻辑，又因为项目
 * 架构或其它原因，无法直接或间接继承{@link BaseCameraScanActivity}或{@link BaseCameraScanFragment}时使用）
 * <p>
 * 3、继承{@link CameraScan}自己实现一个，可参照默认实现类{@link BaseCameraScan}，其他步骤同方式2。（高级用法，谨慎使用）
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public abstract class CameraScan<T> implements ICamera, ICameraControl {

    /**
     * 扫描返回结果的key；解析方式可参见：{@link #parseScanResult(Intent)}
     */
    public static String SCAN_RESULT = "SCAN_RESULT";

    /**
     * A camera on the device facing the same direction as the device's screen.
     */
    public static int LENS_FACING_FRONT = CameraSelector.LENS_FACING_FRONT;
    /**
     * A camera on the device facing the opposite direction as the device's screen.
     */
    public static int LENS_FACING_BACK = CameraSelector.LENS_FACING_BACK;

    /**
     * 是否需要支持触摸缩放
     */
    private boolean isNeedTouchZoom = true;

    /**
     * 是否需要支持触摸缩放
     *
     * @return
     */
    protected boolean isNeedTouchZoom() {
        return isNeedTouchZoom;
    }

    /**
     * 设置是否需要支持触摸缩放
     *
     * @param needTouchZoom
     * @return
     */
    public CameraScan setNeedTouchZoom(boolean needTouchZoom) {
        isNeedTouchZoom = needTouchZoom;
        return this;
    }

    /**
     * 设置相机配置，请在{@link #startCamera()}之前调用
     *
     * @param cameraConfig
     */
    public abstract CameraScan setCameraConfig(CameraConfig cameraConfig);

    /**
     * Set whether to analyze the image. This method can dynamically control whether to analyze the image. It is often used to interrupt the scanning code recognition. For example: during continuous scanning, scan to the result, and then stop analyzing the image
     * <p>
     * 1. Because the analysis image is true by default, if you want to support continuous scanning, you can intercept it by returning true in {@link OnScanResultCallback#onScanResultCallback(AnalyzeResult)}.
     * When the processing logic of continuous scanning is more complicated, please stop analyzing the image by calling setAnalyzeImage(false) before processing the logic,
     * After the logic is processed, call getCameraScan().setAnalyzeImage(true) to continue analyzing the image.
     * <p>
     * 2. If you just want to intercept the scanning result and call back your own processing logic, but don't want to continue to analyze the image (that is, you don't want to scan continuously), you can pass
     * Call getCameraScan().setAnalyzeImage(false) to stop analyzing the image.
     *
     * @param analyze
     */
    public abstract CameraScan setAnalyzeImage(boolean analyze);

    /**
     * 设置分析器，如果内置的一些分析器不满足您的需求，你也可以自定义{@link Analyzer}，
     * 自定义时，切记需在{@link #startCamera()}之前调用才有效。
     *
     * @param analyzer
     */
    public abstract CameraScan setAnalyzer(Analyzer<T> analyzer);

    /**
     * 设置是否震动
     *
     * @param vibrate
     */
    public abstract CameraScan setVibrate(boolean vibrate);

    /**
     * 设置是否播放提示音
     *
     * @param playBeep
     */
    public abstract CameraScan setPlayBeep(boolean playBeep);

    /**
     * 设置扫码结果回调
     *
     * @param callback
     */
    public abstract CameraScan setOnScanResultCallback(OnScanResultCallback<T> callback);

    /**
     * 绑定手电筒，绑定后可根据光线传感器，动态显示或隐藏手电筒
     *
     * @param v
     */
    public abstract CameraScan bindFlashlightView(@Nullable View v);

    /**
     * 设置光线足够暗的阈值（单位：lux），需要通过{@link #bindFlashlightView(View)}绑定手电筒才有效
     *
     * @param lightLux
     */
    public abstract CameraScan setDarkLightLux(float lightLux);

    /**
     * 设置光线足够明亮的阈值（单位：lux），需要通过{@link #bindFlashlightView(View)}绑定手电筒才有效
     *
     * @param lightLux
     */
    public abstract CameraScan setBrightLightLux(float lightLux);

    /**
     * 扫码结果回调
     *
     * @param <T>
     */
    public interface OnScanResultCallback<T> {
        /**
         * 扫码结果回调
         *
         * @param result
         */
        void onScanResultCallback(@NonNull AnalyzeResult<T> result);

        /**
         * 扫码结果识别失败时触发此回调方法
         */
        default void onScanResultFailure() {

        }

    }

    /**
     * 解析扫码结果
     *
     * @param data
     * @return
     */
    @Nullable
    public static String parseScanResult(Intent data) {
        if (data != null) {
            return data.getStringExtra(SCAN_RESULT);
        }
        return null;
    }

}
