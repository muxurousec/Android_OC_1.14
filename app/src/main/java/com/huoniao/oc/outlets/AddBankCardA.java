package com.huoniao.oc.outlets;

import com.huoniao.oc.BaseActivity;
import com.huoniao.oc.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class AddBankCardA extends BaseActivity implements OnClickListener{
	private ImageView iv_back, iv_prompt;
	private EditText et_bankCardNumber;
	private Button bt_next;
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addbankcard);
		initView();
	}
	private void initView() {
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_prompt = (ImageView) findViewById(R.id.iv_prompt);
		et_bankCardNumber = (EditText) findViewById(R.id.et_bankCardNumber);
		bt_next = (Button) findViewById(R.id.bt_next);
		iv_back.setOnClickListener(this);
		iv_prompt.setOnClickListener(this);
		bt_next.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
			
		case R.id.iv_prompt:
			//TODO
			
			break;
			
		case R.id.bt_next:
			intent = new Intent(AddBankCardA.this, BankCardInfoA.class);
			startActivity(intent);
			overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
			
			break;	
		default:
			break;
		}
		
	}		
	
		
		
	

}
