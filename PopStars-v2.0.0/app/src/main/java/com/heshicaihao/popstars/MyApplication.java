/*
 * Copyright (C) 2015 tyrantgit
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
package com.heshicaihao.popstars;

import android.util.Log;

import android.app.Application;

public class MyApplication extends Application {
	private static final String TAG = "MyApplication";
	// 自定义的变量
	private String param1;
	private MyApplication firstAct;

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public MyApplication getFirstAct() {
		return firstAct;
	}

	public void setFirstAct(MyApplication firstAct) {
		this.firstAct = firstAct;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(TAG, "onCreate");
	}

}