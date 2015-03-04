package com.inveno.piflow.entity.view;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.inveno.piflow.biz.upload.PvBasicStateDataBiz;

public class BaseFragment extends Fragment {

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		PvBasicStateDataBiz.BASEFLAG = false;
	}

}
