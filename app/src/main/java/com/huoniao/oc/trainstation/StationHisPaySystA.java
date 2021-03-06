package com.huoniao.oc.trainstation;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.huoniao.oc.BaseActivity;
import com.huoniao.oc.MyApplication;
import com.huoniao.oc.R;
import com.huoniao.oc.admin.AdminHisPstDetailsA;
import com.huoniao.oc.bean.PaySystemBean;
import com.huoniao.oc.user.LoginA;
import com.huoniao.oc.util.CommonAdapter;
import com.huoniao.oc.util.DateUtils;
import com.huoniao.oc.util.Define;
import com.huoniao.oc.util.SessionJsonObjectRequest;
import com.huoniao.oc.util.ThreadCommonUtils;
import com.huoniao.oc.util.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StationHisPaySystA extends BaseActivity implements OnClickListener{
	private ImageView iv_back, iv_dataChoice;
	private Intent intent;
	private ListView mListView;
	private PullToRefreshListView mPullRefreshListView;
	private List<PaySystemBean> mDatas = new ArrayList<>();
	private TextView tv_data, tv_paysysOnMonth;
	private TextView tv_shouldSum, tv_alreadySum, tv_paymentSize, tv_ticketCountSum;
	private String date;
	private CommonAdapter<PaySystemBean> adapter;
//	private ProgressDialog cpd;
	private TextView tv_title;
	private String paymentSize, ticketCountSum, shouldSum, alreadySum;
	private EditText et_searchContent;
	private String searchContent;
	private TextView tv_select;
	private String type;//汇缴类型：正常or异常
	private int pageNumber = 1;//当前页码
	private boolean refreshClose = true;//标记是否还有数据可加载
	private boolean datePickerDialogFlag = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_historpaysystem);
		initView();
		initData();

	}
	@SuppressWarnings("unchecked")
	private void initData() {
		intent = getIntent();
		type = intent.getStringExtra("type");
		try {
			getOltPaySysForMonth(searchContent, type, 1); //默认第一页
		} catch (Exception e) {
			e.printStackTrace();
		}
//			}
//		});

		/*mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (refreshClose) {  //如果有数据 继续刷新
					try {
						pageNumber++;
						getOltPaySysForMonth(searchContent, type, pageNumber);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					Toast.makeText(StationHisPaySystA.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
					ThreadCommonUtils.runonuiThread(new Runnable() {
						@Override
						public void run() {
							mPullRefreshListView.onRefreshComplete();
						}
					});

				}
			}
		});*/

		/*mDatas = (List<PaySystemBean>) intent.getSerializableExtra("paySysData");
		int type = intent.getIntExtra("type", 0);
		if(type == 2){
			tv_title.setText("异常汇缴");
		}
		adapter = new CommonAdapter<PaySystemBean>(this, mDatas, R.layout.station_historpaysystem_item) {

			@Override
			public void convert(ViewHolder holder, PaySystemBean paySysBean) {
				if (!"".equals(paySysBean) || paySysBean != null) {
					String payDate = DateUtils.getInstance().formatString(DateUtils.getInstance().formatDate(paySysBean.getDate()));
					holder.setText(R.id.tv_date, payDate)
//					  .setText(R.id.tv_oltsAccount, paySysBean.getOfficeCode())
			//		  .setText(R.id.tv_agentName, paySysBean.getAgentName())
					  .setText(R.id.tv_shouldAmount, paySysBean.getShouldAmount())
					  .setText(R.id.tv_alreadyAmount, paySysBean.getAlreadyAmount())
					  .setText(R.id.tv_payWinNumber, paySysBean.getWinNumber())
					  .setText(R.id.tv_outsName, paySysBean.getAgentName());
					
				TextView tv_withholdStatus = holder.getView(R.id.tv_withholdStatus);



					if (Define.CHARG_SUCCESS.equals(paySysBean.getWithholdStatus())) {
						tv_withholdStatus.setText(R.string.charg_success);  //成功
					}else if (Define.CHARG_WAIT.equals(paySysBean.getWithholdStatus())) {
						tv_withholdStatus.setText(R.string.charg_wait);  //待扣款
					}else if(Define.CHARG_FAIL.equals(paySysBean.getWithholdStatus())){
						tv_withholdStatus.setText(R.string.charg_fail); //失败

					}else if(Define.CHARG_RECHARGE.equals(paySysBean.getWithholdStatus())){
						tv_withholdStatus.setText(R.string.charg_recharge); //待充值
					}else if(Define.CHARG_RECHARGE_SUCCESS.equals(paySysBean.getWithholdStatus())){
						tv_withholdStatus.setText(R.string.charg_recharge_success); //补缴成功
					}
				
				
				}
				
			}
		};
		mListView.setAdapter(adapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PaySystemBean pstBean = mDatas.get(position);
				intent = new Intent(StationHisPaySystA.this, AdminHisPstDetailsA.class);
				String payDate = DateUtils.getInstance().formatString(DateUtils.getInstance().formatDate(pstBean.getDate()));
				String createDate = DateUtils.getInstance().formatString(DateUtils.getInstance().formatDate(pstBean.getCreateDate()));
				intent.putExtra("date", payDate);
				intent.putExtra("createDate", createDate);
				intent.putExtra("stationAccount", pstBean.getRailwayStationId());
				intent.putExtra("stationName", pstBean.getRailwayStationName());
//				intent.putExtra("outletsAccount", pstBean.getOfficeCode());
				intent.putExtra("outletsName", pstBean.getAgentName());
				intent.putExtra("shouldAmount", pstBean.getShouldAmount());
				intent.putExtra("alreadyAmount", pstBean.getAlreadyAmount());
				intent.putExtra("withholdStatus", pstBean.getWithholdStatus());
				intent.putExtra("winNumber", pstBean.getWinNumber());
				intent.putExtra("ticketCount", pstBean.getTicketCount());
				intent.putExtra("officeCode", pstBean.getOfficeCode());
				intent.putExtra("trunkName", pstBean.getTrunkName());
				intent.putExtra("branchName", pstBean.getBranchName());
				startActivity(intent);
				overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
			}
		});

		shouldSum = intent.getStringExtra("shouldSum");
		if (shouldSum != null && !shouldSum.isEmpty()){
			tv_shouldSum.setText(shouldSum);
		}else {
			tv_shouldSum.setText("");
		}
		alreadySum = intent.getStringExtra("alreadySum");
		if (alreadySum != null && !alreadySum.isEmpty()){
			tv_alreadySum.setText(alreadySum);
		}else {
			tv_alreadySum.setText("");
		}
		paymentSize = intent.getStringExtra("paymentSize");
		if (paymentSize != null && !paymentSize.isEmpty()){
			tv_paymentSize.setText(paymentSize);
		}else {
			tv_paymentSize.setText("");
		}

		ticketCountSum = intent.getStringExtra("ticketCountSum");
		if (ticketCountSum != null && !ticketCountSum.isEmpty()){
			tv_ticketCountSum.setText(ticketCountSum);
		}else {
			tv_ticketCountSum.setText("");
		}*/
	}
	
	private void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_dataChoice = (ImageView) findViewById(R.id.iv_dataChoice);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.listView);
		mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
		mPullRefreshListView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
		mPullRefreshListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
		mPullRefreshListView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开加载更多");
		mListView = mPullRefreshListView.getRefreshableView();
		tv_shouldSum = (TextView) findViewById(R.id.tv_shouldSum);
		tv_alreadySum = (TextView) findViewById(R.id.tv_alreadySum);
		tv_paymentSize = (TextView) findViewById(R.id.tv_paymentSize);
		tv_ticketCountSum = (TextView) findViewById(R.id.tv_ticketCountSum);
		tv_data = (TextView) findViewById(R.id.tv_data);
		tv_paysysOnMonth = (TextView) findViewById(R.id.tv_paysysOnMonth);
		et_searchContent = (EditText) findViewById(R.id.et_searchContent);
		tv_select = (TextView) findViewById(R.id.tv_select);
//		cpd = new CustomProgressDialog(this, "正在加载中...", R.anim.frame_anim);
//		cpd.setCancelable(false);//设置进度条不可以按退回键取消
//		cpd.setCanceledOnTouchOutside(false);//设置点击进度对话框外的区域对话框不消失
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		tv_data.setText(sdf.format(new java.util.Date()));
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM月");
		tv_paysysOnMonth.setText(sdf2.format(new java.util.Date()));
		iv_back.setOnClickListener(this);
		iv_dataChoice.setOnClickListener(this);
		tv_select.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_dataChoice:
			datePickerDialogFlag = true;
			Calendar cal = Calendar.getInstance();
			
			DatePickerDialog mDialog = new DatePickerDialog(StationHisPaySystA.this, new OnDateSetListener() {
				
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					tv_data.setText(year + "-" + (monthOfYear + 1));
					tv_paysysOnMonth.setText((monthOfYear + 1) + "月");
					try {
						if(mDatas != null){
							mDatas.clear();
						}
						//加这个判断是为了兼容4.1版本以下DatePickerDialog默认点击两次确定的问题
						if(datePickerDialogFlag) {
							getOltPaySysForMonth(searchContent, type, 1);
							datePickerDialogFlag= false;
						}

//						pageNumber = 1;
						refreshClose = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

				mDialog.show();  
				//隐藏不想显示的子控件，这里隐藏日控件
			try {
				DatePicker dp = findDatePicker((ViewGroup) mDialog.getWindow().getDecorView());
				if (dp != null) {
				    ((ViewGroup)((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			/**
				 * 设置可选日期范围
				 * 
				 */
//				dp = mDialog.getDatePicker();
//				//限制显示6个月的
//				dp.setMinDate(new Date().getTime() - (long)150 * 24 * 60 * 60 * 1000);
//
//				dp.setMaxDate(new Date().getTime());
				
			// 直接创建一个DatePickerDialog对话框实例，并将它显示出来
//			new DatePickerDialog(PaySystemA.this,
//					// 绑定监听器
//					new DatePickerDialog.OnDateSetListener() {
//
//						@Override
//						public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//
//							tv_data.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
//						}
//					}
//					// 设置初始日期
//					, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
			break;

			case R.id.tv_select:
				searchContent = et_searchContent.getText().toString();
				try {
					if(mDatas != null){
						mDatas.clear();
					}
					getOltPaySysForMonth(searchContent, type, 1);
//					pageNumber = 1;
					refreshClose = true;
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
		default:
			break;
		}
		
	}
	
	
	/**
	 * 通过遍历方法查找DatePicker里的子控件：年、月、日
	 * @param group
	 * @return
	 */
	private DatePicker findDatePicker(ViewGroup group) {
		if (group != null) {
			for (int i = 0, j = group.getChildCount(); i < j; i++) {
				View child = group.getChildAt(i);
				if (child instanceof DatePicker) {
					return (DatePicker) child;
				} else if (child instanceof ViewGroup) {
					DatePicker result = findDatePicker((ViewGroup) child);
					if (result != null)
						return result;
				}
			}
		}
		return null;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		MyApplication.getHttpQueues().cancelAll("hisSysForMoth");
	}
	
	/**
	 * 按月查询历史汇缴数据
	 * 
	 * @throws Exception
	 */
	private void getOltPaySysForMonth(String searchStr, final String status, final int pageNo) throws Exception {
		cpd.show();
		date = tv_data.getText().toString();
		JSONObject jsonObject = new JSONObject();
		try {
			if("1".equals(status)) {  //1是 历史汇缴
				jsonObject.put("time", date);
				jsonObject.put("winNumber", searchStr);
				jsonObject.put("pageNo", pageNo);
			}
			if("2".equals(status)) { //2是异常汇缴
				jsonObject.put("time", date);
				jsonObject.put("withholdStatus", 2);
				jsonObject.put("winNumber", searchStr);
				jsonObject.put("pageNo", pageNo);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String url = Define.URL + "fb/paymentList";
		
		
//		String url = "http://192.168.0.117:8181/OC/app/fb/paymentList";
		final List<PaySystemBean> paySysData = new ArrayList<PaySystemBean>();
		SessionJsonObjectRequest hisSysForMonthRequest = new SessionJsonObjectRequest(Method.POST,
				url, jsonObject, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.d("debug", "response =" + response.toString());
						try {
							String responseCode = response.getString("code");
							String message = response.getString("msg");
							int nextPage = response.getInt("next");
							setRefreshPager(nextPage);
							if ("0".equals(responseCode)) {
								int num = response.getInt("size");
								JSONArray jsonArray = response.getJSONArray("data");
								num = Math.min(num, jsonArray.length());

//								for (int i = 0; i < jsonArray.length(); i++) {
								for (int i = 0; i < num; i++) {
									PaySystemBean paySysBean = new PaySystemBean();

									JSONObject paySysObj = (JSONObject) jsonArray.get(i);
									String date = paySysObj.optString("date");// 票款时间
									String officeCode = paySysObj.optString("officeCode");// 扣款账号
									String agentName = paySysObj.optString("agentName");// 代售点名称
									String shouldAmount = paySysObj.optString("shouldAmountString");// 应扣金额
									String alreadyAmount = paySysObj.optString("alreadyAmountString");// 已扣金额
									String withholdStatus = paySysObj.optString("withholdStatus");// 扣款状态
									String railwayStationName = paySysObj.optString("railwayStationName");//火车站名称
									String railwayStationId = paySysObj.optString("railwayStationId");//火车站id
									String createDate = paySysObj.optString("createDate");//汇缴时间
									String winNumber = paySysObj.optString("winNumber");//窗口号
									String branchName = paySysObj.optString("branchName");//铁路分局
									String trunkName = paySysObj.optString("trunkName");//铁路总局
									String ticketCount = paySysObj.optString("ticketCount");//实售票数
									

									paySysBean.setDate(date);
									paySysBean.setOfficeCode(officeCode);
									paySysBean.setAgentName(agentName);
									paySysBean.setShouldAmount(shouldAmount);
									paySysBean.setAlreadyAmount(alreadyAmount);
									paySysBean.setWithholdStatus(withholdStatus);
									paySysBean.setRailwayStationName(railwayStationName);
									paySysBean.setRailwayStationId(railwayStationId);
									paySysBean.setCreateDate(createDate);
									paySysBean.setWinNumber(winNumber);
									paySysBean.setBranchName(branchName);
									paySysBean.setTrunkName(trunkName);
									paySysBean.setTicketCount(ticketCount);
//									paySysBean.setShouldSum(shouldSum);
//									paySysBean.setAlreadySum(alreadySum);

//									Log.d("debug", paySysBean.getDate());
									paySysData.add(paySysBean);
								}
								if (pageNo == 1) {
									for (int i = 0; i < jsonArray.length(); i++) {
										JSONObject paySysObj = (JSONObject) jsonArray.get(i);
										alreadySum = paySysObj.optString("alreadySumString");// 已扣总金额
										shouldSum = paySysObj.optString("shouldSumString");// 应扣总金额
										paymentSize = paySysObj.optString("paymentSize");// 汇缴记录
										ticketCountSum = paySysObj.optString("ticketSum");//实售总票数
									}
								}
								if (shouldSum != null && !shouldSum.isEmpty()){
									tv_shouldSum.setText(shouldSum);
								}else {
									tv_shouldSum.setText("");
								}

								if (alreadySum != null && !alreadySum.isEmpty()){
									tv_alreadySum.setText(alreadySum);
								}else {
									tv_alreadySum.setText("");
								}

								if (paymentSize != null && !paymentSize.isEmpty()){
									tv_paymentSize.setText(paymentSize);
								}else {
									tv_paymentSize.setText("");
								}

								if (ticketCountSum != null && !ticketCountSum.isEmpty()){
									tv_ticketCountSum.setText(ticketCountSum);
								}else {
									tv_ticketCountSum.setText("");
								}
								if("2".equals(status)){
									tv_title.setText("异常汇缴");
								}

								if(nextPage == 1){
									mDatas.clear();
								}else if (nextPage == -1){
									refreshClose = false;
								}
								mPullRefreshListView.onRefreshComplete();
//								if (paySysData != null && paySysData.size() > 0) {
								//	mDatas.clear();
								//	mDatas.addAll(capitalFlowData);
			/*mListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();*/
//								int dataSize = mDatas.size();
								mDatas.addAll(paySysData);
								if (adapter == null) {
									adapter = new CommonAdapter<PaySystemBean>(StationHisPaySystA.this, mDatas, R.layout.station_historpaysystem_item) {

										@Override
										public void convert(ViewHolder holder, PaySystemBean paySysBean) {
											if (!"".equals(paySysBean) || paySysBean != null) {
												String payDate = DateUtils.getInstance().formatString(DateUtils.getInstance().formatDate(paySysBean.getCreateDate()));
												holder.setText(R.id.tv_date, payDate)
//					  .setText(R.id.tv_oltsAccount, paySysBean.getOfficeCode())
														//		  .setText(R.id.tv_agentName, paySysBean.getAgentName())
														.setText(R.id.tv_shouldAmount, paySysBean.getShouldAmount())
														.setText(R.id.tv_alreadyAmount, paySysBean.getAlreadyAmount())
														.setText(R.id.tv_payWinNumber, paySysBean.getWinNumber())
														.setText(R.id.tv_outsName, paySysBean.getAgentName());

												TextView tv_withholdStatus = holder.getView(R.id.tv_withholdStatus);


												if (Define.CHARG_SUCCESS.equals(paySysBean.getWithholdStatus())) {
													tv_withholdStatus.setText(R.string.charg_success);  //成功
												} else if (Define.CHARG_WAIT.equals(paySysBean.getWithholdStatus())) {
													tv_withholdStatus.setText(R.string.charg_wait);  //待扣款
												} else if (Define.CHARG_FAIL.equals(paySysBean.getWithholdStatus())) {
													tv_withholdStatus.setText(R.string.charg_fail); //失败

												} else if (Define.CHARG_RECHARGE.equals(paySysBean.getWithholdStatus())) {
													tv_withholdStatus.setText(R.string.charg_recharge); //待充值
												} else if (Define.CHARG_RECHARGE_SUCCESS.equals(paySysBean.getWithholdStatus())) {
													tv_withholdStatus.setText(R.string.charg_recharge_success); //补缴成功
												}


											}

										}
									};
									mListView.setAdapter(adapter);
								}

								if(pageNo == 1){
									if(mDatas.size()>0){
										mListView.setAdapter(adapter);
										mListView.setSelection(1);
									}
								}
								adapter.refreshData(mDatas);

//								mListView.setSelection(dataSize);
								/*if (paySysData.size() < 50){
									refreshClose = false;

								}*/

								cpd.dismiss();

								mListView.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
										PaySystemBean pstBean = mDatas.get(position - 1);
										intent = new Intent(StationHisPaySystA.this, AdminHisPstDetailsA.class);
										String payDate = DateUtils.getInstance().formatString(DateUtils.getInstance().formatDate(pstBean.getDate()));
										String createDate = DateUtils.getInstance().formatString(DateUtils.getInstance().formatDate(pstBean.getCreateDate()));
										intent.putExtra("date", payDate);
										intent.putExtra("createDate", createDate);
										intent.putExtra("stationAccount", pstBean.getRailwayStationId());
										intent.putExtra("stationName", pstBean.getRailwayStationName());
//										intent.putExtra("outletsAccount", pstBean.getOfficeCode());
										intent.putExtra("outletsName", pstBean.getAgentName());
										intent.putExtra("shouldAmount", pstBean.getShouldAmount());
										intent.putExtra("alreadyAmount", pstBean.getAlreadyAmount());
										intent.putExtra("withholdStatus", pstBean.getWithholdStatus());
										intent.putExtra("winNumber", pstBean.getWinNumber());
										intent.putExtra("ticketCount", pstBean.getTicketCount());
										intent.putExtra("officeCode", pstBean.getOfficeCode());
										intent.putExtra("trunkName", pstBean.getTrunkName());
										intent.putExtra("branchName", pstBean.getBranchName());
										startActivity(intent);
										overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
									}
								});
								
							} else if("46000".equals(responseCode)){
								cpd.dismiss();
								Toast.makeText(StationHisPaySystA.this, "登录过期，请重新登录!", Toast.LENGTH_SHORT).show();
								intent = new Intent(StationHisPaySystA.this, LoginA.class);
								startActivity(intent);
								overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
							} else {
								cpd.dismiss();
								Toast.makeText(StationHisPaySystA.this, message, Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							cpd.dismiss();

							Log.d("exciption", "exciption =" + e.toString());
						}

					}
		}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						cpd.dismiss();
						Toast.makeText(StationHisPaySystA.this, R.string.netError, Toast.LENGTH_SHORT).show();
						Log.d("debug", "error = " + error.toString());

					}
				});
		// 解决重复请求后台的问题
		hisSysForMonthRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, // 默认超时时间，应设置一个稍微大点儿的
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // 默认最大尝试次数
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		// 设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
		hisSysForMonthRequest.setTag("hisSysForMoth");
		// 将请求加入全局队列中
		MyApplication.getHttpQueues().add(hisSysForMonthRequest);

	}

	private void setRefreshPager(final int nextPage) {
		mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (refreshClose) {  //如果有数据 继续刷新
					try {
//						pageNumber++;
						getOltPaySysForMonth(searchContent, type, nextPage);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					Toast.makeText(StationHisPaySystA.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
					ThreadCommonUtils.runonuiThread(new Runnable() {
						@Override
						public void run() {
							mPullRefreshListView.onRefreshComplete();
						}
					});

				}
			}
		});
	}
}
