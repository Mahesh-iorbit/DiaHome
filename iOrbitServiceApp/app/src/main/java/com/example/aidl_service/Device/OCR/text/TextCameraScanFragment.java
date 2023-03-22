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
package com.example.aidl_service.Device.OCR.text;

import androidx.annotation.Nullable;

import com.example.aidl_service.Device.OCR.camera.BaseCameraScanFragment;
import com.example.aidl_service.Device.OCR.camera.analyze.Analyzer;
import com.example.aidl_service.Device.OCR.text.analyze.TextRecognitionAnalyzer;
import com.google.mlkit.vision.text.Text;



/**
 * 文字识别 - 相机扫描基类
 * <p>
 * 通过继承 {@link TextCameraScanActivity}或{@link TextCameraScanFragment}可快速实现文字识别
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public abstract class TextCameraScanFragment extends BaseCameraScanFragment<Text> {

    @Nullable
    @Override
    public Analyzer<Text> createAnalyzer() {
        return new TextRecognitionAnalyzer();
    }

}
