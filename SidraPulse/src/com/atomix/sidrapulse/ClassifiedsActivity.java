package com.atomix.sidrapulse;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.atomix.adapter.ClassifiedAdapter;
import com.atomix.adapter.SpinnerAdapter;
import com.atomix.customview.SidraCustomProgressDialog;
import com.atomix.interfacecallback.UnReadRequest;
import com.atomix.internetconnection.InternetConnectivity;
import com.atomix.jsonparse.CommunicationLayer;
import com.atomix.sidrainfo.ConstantValues;
import com.atomix.sidrainfo.SidraPulseApp;
import com.atomix.synctask.UnReadTask;
import com.atomix.utils.Utilities;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class ClassifiedsActivity extends Activity implements OnCheckedChangeListener, OnClickListener, OnItemClickListener, OnItemSelectedListener {

	private Button btnBack;
	private RadioButton radioBtnAllClassifieds;
	private RadioButton radioBtnPostedByMe;
	private Spinner spinnerAllCategories;
	private Button btnPost;
	private ProgressDialog progressDialog;
	private int categoryStatus;
	private int classifiedStatus;
	private int selectedCategoryId = 0;
	private int type = 1;
	private PullToRefreshListView lstViewClassified;
	private ClassifiedAdapter adapter;
	private ArrayList<String> categoryList;
	private int pageNo = 1;
	private TextView txtViewSubTitle;
	private TextView txtViewEmptyList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_classifieds);
		
		initViews();
		setListener();
		loadSpinnerData();
		loadData();
	}

	private void loadSpinnerData() {
		if (InternetConnectivity.isConnectedToInternet(ClassifiedsActivity.this)) {
			new ClassifiedCategoryListApiTask().execute();
		} else {
			SidraPulseApp.getInstance().openDialogForInternetChecking(ClassifiedsActivity.this);
		}
	}

	private void initViews() {
		txtViewEmptyList = (TextView) findViewById(R.id.txt_view_empty_list);
		txtViewEmptyList.setText(getResources().getString(R.string.empty_list));
		txtViewEmptyList.setVisibility(View.INVISIBLE);
		txtViewSubTitle = (TextView) findViewById(R.id.txt_view_subTitle);
		btnBack = (Button) findViewById(R.id.btn_back);
		radioBtnAllClassifieds = (RadioButton) findViewById(R.id.radio_btn_all_classifieds);
		radioBtnPostedByMe = (RadioButton) findViewById(R.id.radio_btn_posted_by_me);
		btnPost = (Button) findViewById(R.id.btn_post);
		lstViewClassified = (PullToRefreshListView) findViewById(R.id.lst_view_classified);
		lstViewClassified.setMode(Mode.BOTH);
		spinnerAllCategories = (Spinner) findViewById(R.id.spinner_classified_categories);
	
	}

	private void setListener() {
		btnBack.setOnClickListener(this);
		btnPost.setOnClickListener(this);
		((RadioGroup) findViewById(R.id.radio_group)).setOnCheckedChangeListener(this);
		lstViewClassified.setOnItemClickListener(this);
		spinnerAllCategories.setOnItemSelectedListener(this);
		lstViewClassified.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (!InternetConnectivity.isConnectedToInternet(ClassifiedsActivity.this)) {
					SidraPulseApp.getInstance().openDialogForInternetChecking(ClassifiedsActivity.this);
					adapter.notifyDataSetChanged();
					return;
				} 
				
				if (SidraPulseApp.getInstance().getClassifiedInfoList() == null) {
					adapter.notifyDataSetChanged();
					lstViewClassified.onRefreshComplete();
					return;
				}
				
				String first_element_id = Integer.toString(SidraPulseApp.getInstance().getClassifiedInfoList().get(0).getId());
				new ClassifiedsApiOnPullRefreshTask(first_element_id, "0").execute();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				
				if (!InternetConnectivity.isConnectedToInternet(ClassifiedsActivity.this)) {
					SidraPulseApp.getInstance().openDialogForInternetChecking(ClassifiedsActivity.this);
					adapter.notifyDataSetChanged();
					return;
				} 
				
				if (SidraPulseApp.getInstance().getClassifiedInfoList() == null) {
					adapter.notifyDataSetChanged();
					lstViewClassified.onRefreshComplete();
					return;
				}
				
				String last_element_id = Integer.toString(SidraPulseApp.getInstance().getClassifiedInfoList().get(SidraPulseApp.getInstance().getClassifiedInfoList().size() - 1).getId());
				new ClassifiedsApiOnPullRefreshTask(last_element_id, "1").execute();
				
				
			}

		});
	
	}

	private void loadData() {
		ConstantValues.PullDownActive = true;
		txtViewSubTitle.setText("Listing All Classifieds");
		adapter = new ClassifiedAdapter(ClassifiedsActivity.this, ClassifiedsActivity.this, SidraPulseApp.getInstance().getClassifiedInfoList(), type);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			onBackPressed();
			break;
			
		case R.id.btn_post:
			Intent intent = new Intent(ClassifiedsActivity.this, ClassifiedsCreateNewPostActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switchTab();
	}
	
	private void switchTab() {
		ConstantValues.PullDownActive = true;
		int selectedTab = ((RadioGroup) findViewById(R.id.radio_group)).getCheckedRadioButtonId();
		
		switch (selectedTab) {
			case R.id.radio_btn_all_classifieds:
				txtViewSubTitle.setText("Listings All Classifieds");
				spinnerAllCategories.setVisibility(View.VISIBLE);
				radioBtnAllClassifieds.setButtonDrawable(R.drawable.all_classifieds_btn_blue);
				radioBtnPostedByMe.setButtonDrawable(R.drawable.posted_btn_white);
				type = 1;
				if (InternetConnectivity.isConnectedToInternet(ClassifiedsActivity.this)) {
					new ClassifiedsApiTask().execute();
				} else {
					SidraPulseApp.getInstance().openDialogForInternetChecking(ClassifiedsActivity.this);
				}
				
				break;
				
			case R.id.radio_btn_posted_by_me:
				txtViewSubTitle.setText("Listings Posted By Me");
				spinnerAllCategories.setVisibility(View.GONE);
				selectedCategoryId = 0;
				radioBtnAllClassifieds.setButtonDrawable(R.drawable.all_classifieds_btn_white);
				radioBtnPostedByMe.setButtonDrawable(R.drawable.posted_btn_blue);
				type = 0;
				if (InternetConnectivity.isConnectedToInternet(ClassifiedsActivity.this)) {
					new ClassifiedsApiTask().execute();
				} else {
					SidraPulseApp.getInstance().openDialogForInternetChecking(ClassifiedsActivity.this);
				}
				break;
	
			default:
				break;
		}
	}
	
	private class ClassifiedsApiTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			progressDialog = SidraCustomProgressDialog.creator(ClassifiedsActivity.this);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				if(selectedCategoryId == 0) {
					classifiedStatus = CommunicationLayer.getShowClassified(ConstantValues.FUNC_ID_CLASSIFIED, Integer.toString(SidraPulseApp.getInstance().getUserInfo().getUserID()), SidraPulseApp.getInstance().getUserInfo().getAccessToken(), Integer.toString(type), "0", "", "");
				} else {
					classifiedStatus = CommunicationLayer.getShowClassified(ConstantValues.FUNC_ID_CLASSIFIED, Integer.toString(SidraPulseApp.getInstance().getUserInfo().getUserID()), SidraPulseApp.getInstance().getUserInfo().getAccessToken(), Integer.toString(type), Integer.toString(SidraPulseApp.getInstance().getClassifiedCategoryInfoList().get(selectedCategoryId - 1).getId()), "", "");
				}
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			
			if(classifiedStatus == 5) {
				SidraPulseApp.getInstance().accessTokenChange(ClassifiedsActivity.this);
				return;
			} 
		
			Log.i("STATUS_CLASSIFIED","***" + classifiedStatus);
			
			if(classifiedStatus == 1) { 
				if(SidraPulseApp.getInstance().getClassifiedInfoList() != null) {
					lstViewClassified.setVisibility(View.VISIBLE);
					txtViewEmptyList.setVisibility(View.INVISIBLE);
					adapter = new ClassifiedAdapter(ClassifiedsActivity.this, ClassifiedsActivity.this, SidraPulseApp.getInstance().getClassifiedInfoList(), type);
					lstViewClassified.setAdapter(adapter);
					
				} else {	
					lstViewClassified.setVisibility(View.INVISIBLE);
					txtViewEmptyList.setVisibility(View.VISIBLE);
					lstViewClassified.setAdapter(null);
				}
				
			} else {
				lstViewClassified.setAdapter(null);
				lstViewClassified.setVisibility(View.INVISIBLE);
				txtViewEmptyList.setVisibility(View.VISIBLE);
			
			}
			
			lstViewClassified.onRefreshComplete();
		}
	}
	
	private class ClassifiedsApiOnPullRefreshTask extends AsyncTask<Void, Void, Void> {
		 private String last_element_id = null;
		 private String direction = null;
		
		public ClassifiedsApiOnPullRefreshTask(String last_element_id, String direction) {
			this.last_element_id = last_element_id;
			this.direction = direction;
		}
		
		@Override
		protected void onPreExecute() {
			progressDialog = SidraCustomProgressDialog.creator(ClassifiedsActivity.this);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				
				if (this.direction.equals("0")) {
					if(selectedCategoryId == 0) {
						classifiedStatus = CommunicationLayer.getShowClassified(ConstantValues.FUNC_ID_CLASSIFIED, Integer.toString(SidraPulseApp.getInstance().getUserInfo().getUserID()), SidraPulseApp.getInstance().getUserInfo().getAccessToken(), Integer.toString(type), "0", this.last_element_id, this.direction);
					
					} else {
						classifiedStatus = CommunicationLayer.getShowClassified(ConstantValues.FUNC_ID_CLASSIFIED, Integer.toString(SidraPulseApp.getInstance().getUserInfo().getUserID()), SidraPulseApp.getInstance().getUserInfo().getAccessToken(), Integer.toString(type), Integer.toString(SidraPulseApp.getInstance().getClassifiedCategoryInfoList().get(selectedCategoryId -1).getId()), this.last_element_id, this.direction);
					
					}
					
				} else if (ConstantValues.PullDownActive) {
					if(selectedCategoryId == 0) {
						classifiedStatus = CommunicationLayer.getShowClassified(ConstantValues.FUNC_ID_CLASSIFIED, Integer.toString(SidraPulseApp.getInstance().getUserInfo().getUserID()), SidraPulseApp.getInstance().getUserInfo().getAccessToken(), Integer.toString(type), "0", this.last_element_id, this.direction);
					
					} else {
						classifiedStatus = CommunicationLayer.getShowClassified(ConstantValues.FUNC_ID_CLASSIFIED, Integer.toString(SidraPulseApp.getInstance().getUserInfo().getUserID()), SidraPulseApp.getInstance().getUserInfo().getAccessToken(), Integer.toString(type), Integer.toString(SidraPulseApp.getInstance().getClassifiedCategoryInfoList().get(selectedCategoryId -1).getId()), this.last_element_id, this.direction);
					
					}
				}
				
				
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {

				if(progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				
				if(classifiedStatus == 5) {
					SidraPulseApp.getInstance().accessTokenChange(ClassifiedsActivity.this);
					return;
				} 
				
				Log.i("STATUS_CLASSIFIED_UPDATE","***" + classifiedStatus);
				
				if (this.direction.equals("0") || this.direction.equals("1")) {
					adapter.notifyDataSetChanged();
					lstViewClassified.onRefreshComplete();
					return;
				}
				
				lstViewClassified.onRefreshComplete();
				
			} catch (Exception e) {
				Log.i("Exception Pull Classified","***" + e.getMessage());
			}
			
		}
	}
	
	private class ClassifiedCategoryListApiTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			progressDialog = SidraCustomProgressDialog.creator(ClassifiedsActivity.this);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				categoryStatus = CommunicationLayer.getClassifiedCategories(ConstantValues.FUNC_ID_CLASSIFIED_CATEGORY, Integer.toString(SidraPulseApp.getInstance().getUserInfo().getUserID()), SidraPulseApp.getInstance().getUserInfo().getAccessToken());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			
			if(categoryStatus == 1) {
				if(SidraPulseApp.getInstance().getClassifiedCategoryInfoList() != null) {
					categoryList = new ArrayList<String>();
					categoryList.add("All categories");
					for(int i = 0; i < SidraPulseApp.getInstance().getClassifiedCategoryInfoList().size(); i++) {
						categoryList.add(SidraPulseApp.getInstance().getClassifiedCategoryInfoList().get(i).getCategoryName());
					}	
     
			     spinnerAllCategories.setAdapter(new SpinnerAdapter(ClassifiedsActivity.this, R.layout.spinner_simple_row, categoryList));
					
					
					
				} else {
					spinnerAllCategories.setAdapter(null);
				}
			} else if(categoryStatus == 5) {
				SidraPulseApp.getInstance().accessTokenChange(ClassifiedsActivity.this);
				
			}  else {
				spinnerAllCategories.setAdapter(null);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (0 == SidraPulseApp.getInstance().getClassifiedInfoList().get(arg2 - 1).getIsDraft()) {
			Intent intent = new Intent(ClassifiedsActivity.this, ClassifiedDetailsActivity.class);
			intent.putExtra("click_position", arg2 - 1);
			startActivity(intent);
		}
		else if (1 == SidraPulseApp.getInstance().getClassifiedInfoList().get(arg2 - 1).getIsDraft()) {
			Intent intent = new Intent(ClassifiedsActivity.this, ClassifiedsCreateNewPostActivity.class);
			intent.putExtra("click_position", arg2 - 1);
			startActivity(intent);
		}
		
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		selectedCategoryId = arg2;
		if (selectedCategoryId == 0) {
			txtViewSubTitle.setText("Listing All Classifieds");
		} else {
			txtViewSubTitle.setText("Listing under "+ categoryList.get(selectedCategoryId));
		}
		new ClassifiedsApiTask().execute();
	
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
		if(SidraPulseApp.getInstance().getClassifiedInfoList()!=null) {
				if(SidraPulseApp.getInstance().getClassifiedInfoList().size() == 0) {
					lstViewClassified.setVisibility(View.INVISIBLE);
					txtViewEmptyList.setVisibility(View.VISIBLE);
					lstViewClassified.setAdapter(null);
				}
		}
		if (InternetConnectivity.isConnectedToInternet(ClassifiedsActivity.this)) {
			new UnReadTask(ClassifiedsActivity.this, new UnReadRequest() {
				@Override
				public void onTaskCompleted(int status) {
					if(status == 1){
						SidraPulseApp.getInstance().getNotificationInfo().setClassified(0);
					} else if(status == 5) {
						SidraPulseApp.getInstance().accessTokenChange(ClassifiedsActivity.this);
					} else {
						Utilities.showToast(ClassifiedsActivity.this, ConstantValues.failureMessage);
					}
				}
			}, 2, 0, true).execute();
		} else {
			SidraPulseApp.getInstance().openDialogForInternetChecking(ClassifiedsActivity.this);
		}
	}
	
//	@Override
//	protected void onRestart() {
//		super.onRestart();
//		finish();
//		startActivity(getIntent());
//	}

}
