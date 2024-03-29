package com.atomix.sidrapulse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
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

import com.atomix.adapter.EventAdapter;
import com.atomix.adapter.SpinnerAdapter;
import com.atomix.customview.SidraCustomProgressDialog;
import com.atomix.internetconnection.InternetConnectivity;
import com.atomix.jsonparse.CommunicationLayer;
import com.atomix.sidrainfo.ConstantValues;
import com.atomix.sidrainfo.SidraPulseApp;
import com.atomix.utils.Utilities;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class EventsActivity extends Activity implements OnItemSelectedListener, OnClickListener, OnItemClickListener, OnCheckedChangeListener {

	private Button btnBack;
	private RadioButton radioBtnMyEvents;
	private RadioButton radioBtnPast;
	private RadioButton radioBtnUpcoming;
	private Spinner spinnerEvents;
	private int tabIndex = 1;
	private PullToRefreshListView lstViewEvents;
	private ProgressDialog progressDialog;
	private int eventsStatus;
	private String eventDateSelected ="";
	private List<String> list;
	private String[] array;
	private EventAdapter eventAdapter;
	private TextView txtViewEventTitle;
	private boolean isPastEvents;
	private int pageNo = 1;
	private int spinnerIndex = 0;
	private TextView txtViewEmptyList;
	private boolean isNotification = false;
	private int notificationEventId ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
		
		initViews();
		setListener();
		loadMonthandYear();
		loadSpinnerData(5 + Calendar.getInstance().get(Calendar.MONTH), 5 + Calendar.getInstance().get(Calendar.MONTH) + 6);
		loadData();
		loadNotificationData();
		
		//getIntent().addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
	}

	private void loadNotificationData() {
		if (getIntent().getExtras() != null) {
			isNotification = getIntent().getBooleanExtra("is_my_event", false);
			if (isNotification) {
				Log.e("I am in here", "----");
				radioBtnMyEvents.performClick();
				spinnerIndex = 0;
				loadSpinnerData(5 + Calendar.getInstance().get(Calendar.MONTH), 5 + Calendar.getInstance().get(Calendar.MONTH) + 6);
//				spinnerIndex = Calendar.getInstance().get(Calendar.MONTH);
//				loadSpinnerData(5 , 17);
				isPastEvents = false;
				tabIndex = 2;
				txtViewEventTitle.setText("Events bookmarked by you");
				radioBtnUpcoming.setButtonDrawable(R.drawable.upcoming_tab_w);
				radioBtnMyEvents.setButtonDrawable(R.drawable.my_event_blue);
				radioBtnPast.setButtonDrawable(R.drawable.past_w);
				
				if(getIntent().getExtras().getBoolean("is_id_found") == true ){
					Log.e("I am in here found", "---");
					notificationEventId = getIntent().getExtras().getInt("EVENT_ID");
					Log.e("I am in here with id", "---" + notificationEventId);
					if(SidraPulseApp.getInstance().getEventsInfoList() != null){
						for(int i = 0; i < SidraPulseApp.getInstance().getEventsInfoList().size(); i++){
							if(notificationEventId == SidraPulseApp.getInstance().getEventsInfoList().get(i).getId()){
								Intent intent = new Intent(EventsActivity.this, EventDetailsActivity.class);
								//intent.putExtra("click_position", i - 1);
								intent.putExtra("click_position", i);
								intent.putExtra("isPast", isPastEvents);
								startActivity(intent);
							}
						}
						
					} else{
						
					}
				}
			}
		}
		
	}

	private void initViews() {
		txtViewEmptyList = (TextView) findViewById(R.id.txt_view_empty_list);
		txtViewEmptyList.setText(getResources().getString(R.string.empty_list));
		txtViewEmptyList.setVisibility(View.INVISIBLE);
		list = new ArrayList<String>();
		btnBack = (Button) findViewById(R.id.btn_back);
		radioBtnMyEvents = (RadioButton) findViewById(R.id.radio_btn_my_event);
		radioBtnPast = (RadioButton) findViewById(R.id.radio_btn_past);
		radioBtnUpcoming = (RadioButton) findViewById(R.id.radio_btn_upcoming);
		spinnerEvents = (Spinner) findViewById(R.id.spinner_events);
		lstViewEvents = (PullToRefreshListView) findViewById(R.id.lst_view_events);
		lstViewEvents.setMode(Mode.BOTH);
		txtViewEventTitle = (TextView) findViewById(R.id.txt_view_event_title);
 	}

	private void setListener() {
		btnBack.setOnClickListener(this);
		spinnerEvents.setOnItemSelectedListener(this);
		lstViewEvents.setOnItemClickListener(this);
		((RadioGroup) findViewById(R.id.radio_group)).setOnCheckedChangeListener(this);
		
		lstViewEvents.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (!InternetConnectivity.isConnectedToInternet(EventsActivity.this)) {
					SidraPulseApp.getInstance().openDialogForInternetChecking(EventsActivity.this);
					lstViewEvents.onRefreshComplete();
					return;
				} 
				
				if (SidraPulseApp.getInstance().getEventsInfoList() == null) {
					lstViewEvents.onRefreshComplete();
					return;
				}
				
				new EventsApiTask(true, Integer.toString(SidraPulseApp.getInstance().getEventsInfoList().get(0).getId()), "0").execute();
				
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (!InternetConnectivity.isConnectedToInternet(EventsActivity.this)) {
					SidraPulseApp.getInstance().openDialogForInternetChecking(EventsActivity.this);
					lstViewEvents.onRefreshComplete();
					return;
				} 
				
				if (SidraPulseApp.getInstance().getEventsInfoList() == null) {
					lstViewEvents.onRefreshComplete();
					return;
				}

				String last_element_id = Integer.toString(SidraPulseApp.getInstance().getEventsInfoList().get(SidraPulseApp.getInstance().getEventsInfoList().size() - 1).getId());
				
				//if (ConstantValues.PullDownActive) {
				new EventsApiTask(true, last_element_id, "1").execute();
				/*} else {
					lstViewEvents.onRefreshComplete();
				}*/
			}

		});
		
	}
	
	private void loadMonthandYear() {
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		
		int lastYear = today.year - 1;
		int nextYear = today.year + 1;
		
	
		list.add("  August "+lastYear);
		list.add("  September "+lastYear);
		list.add("  October "+lastYear);
		list.add("  November "+lastYear);
		list.add("  December "+lastYear);
		
		list.add("  January "+today.year);
		list.add("  February "+today.year);
		list.add("  March "+today.year);
		list.add("  April "+today.year);
		list.add("  May "+today.year);
		list.add("  June "+today.year);
		list.add("  July "+today.year);
		list.add("  August "+today.year);
		list.add("  September "+today.year);
		list.add("  October "+today.year);
		list.add("  November "+today.year);
		list.add("  December "+today.year);
		
		list.add("  January "+nextYear);
		list.add("  February "+nextYear);
		list.add("  March "+nextYear);
		list.add("  April "+nextYear);
		list.add("  May "+nextYear);
	}

	private void loadSpinnerData(int start, int end) {
		array = new String[Math.abs(start - end)];
		list.subList(start, end).toArray(array);
		
		ArrayList<String> listArray = new ArrayList<String>();
		if(array != null && array.length>0){
			for(int i = 0; i<array.length; i++){
				listArray.add(array[i]);
			}
			
			spinnerEvents.setAdapter(new SpinnerAdapter (EventsActivity.this, R.layout.spinner_simple_row, listArray));
			spinnerEvents.setSelection(spinnerIndex);
		}
		else{
			spinnerEvents.setAdapter(null);
		}
	
	}

	private void loadData() {
		txtViewEventTitle.setText("Listing Upcoming Events");
		eventAdapter = new EventAdapter(EventsActivity.this, getApplicationContext(), SidraPulseApp.getInstance().getEventsInfoList(), tabIndex, txtViewEmptyList, lstViewEvents);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			onBackPressed();
			break;
			
		default:
			break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ConstantValues.PullDownActive = true;
		Log.e("event selected date", ""+array[arg2].toString());
		eventDateSelected = Utilities.getDate(array[arg2].toString());
		Log.e("Event Date is selected", "----"+eventDateSelected);
		if (InternetConnectivity.isConnectedToInternet(EventsActivity.this)) {
			if(tabIndex == 3) {
				new EventsApiTask(true, "", "").execute();
			} else {
				new EventsApiTask(false, "", "").execute();
			}
		} else {
			SidraPulseApp.getInstance().openDialogForInternetChecking(EventsActivity.this);
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Intent intent = new Intent(EventsActivity.this, EventDetailsActivity.class);
		intent.putExtra("click_position", position - 1);
		intent.putExtra("isPast", isPastEvents);
		intent.putExtra("tabIndex", tabIndex);
		startActivity(intent);
		
	}
	
	private class EventsApiTask extends AsyncTask<Void, Void, Void> {
		boolean isPast;
		String last_element_id = null;
		String direction = null;
			
		public EventsApiTask(boolean isPast, String last_element_id, String direction) {
			this.isPast = isPast;
			this.last_element_id = last_element_id;
			this.direction = direction;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = SidraCustomProgressDialog.creator(EventsActivity.this);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				
				if (this.direction.equals("0")) {
					eventsStatus = CommunicationLayer.getEventsData(ConstantValues.FUNC_ID_EVENTS, Integer.toString(SidraPulseApp.getInstance().getUserInfo().getUserID()), SidraPulseApp.getInstance().getUserInfo().getAccessToken(), Integer.toString(tabIndex), eventDateSelected.trim(), this.last_element_id, this.direction);
				 
				} else if (ConstantValues.PullDownActive) {
					eventsStatus = CommunicationLayer.getEventsData(ConstantValues.FUNC_ID_EVENTS, Integer.toString(SidraPulseApp.getInstance().getUserInfo().getUserID()), SidraPulseApp.getInstance().getUserInfo().getAccessToken(), Integer.toString(tabIndex), eventDateSelected.trim(), this.last_element_id, this.direction);
					
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
				eventAdapter.notifyDataSetChanged();
				lstViewEvents.onRefreshComplete();
			}
			
			if(eventsStatus == 5) {
				SidraPulseApp.getInstance().accessTokenChange(EventsActivity.this);
				return;
			} 
			
			if (tabIndex == 2) {
				eventAdapter.notifyDataSetChanged();
				lstViewEvents.onRefreshComplete();
			}
			
//		    if (eventsStatus == 1 && pageNo > 1) {
//				return;
//			}
		    
		    if (direction.equals("0") || direction.equals("1")) {
		    	eventAdapter.notifyDataSetChanged();
		    	lstViewEvents.onRefreshComplete();
				return;
			} 

				
			if(eventsStatus == 1) {
				Log.i("EVENT_STATUS", "STATUS " + eventsStatus);
				if(!SidraPulseApp.getInstance().getEventsInfoList().isEmpty()) {
					txtViewEmptyList.setVisibility(View.INVISIBLE);
					lstViewEvents.setVisibility(View.VISIBLE);
					eventAdapter = new EventAdapter(EventsActivity.this, getApplicationContext(), SidraPulseApp.getInstance().getEventsInfoList(), tabIndex, txtViewEmptyList, lstViewEvents);
					lstViewEvents.setAdapter(eventAdapter);
					lstViewEvents.onRefreshComplete();
				} else {
					lstViewEvents.setAdapter(null);
					lstViewEvents.setVisibility(View.INVISIBLE);
					txtViewEmptyList.setVisibility(View.VISIBLE);
					lstViewEvents.onRefreshComplete();
				}

			} else {
				lstViewEvents.setAdapter(null);
				lstViewEvents.setVisibility(View.INVISIBLE);
				txtViewEmptyList.setVisibility(View.VISIBLE);
				lstViewEvents.onRefreshComplete();
			}
			
		}
			
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switchTab();
	}
	
	private void switchTab() {
		int selectedTab = ((RadioGroup) findViewById(R.id.radio_group)).getCheckedRadioButtonId();
		
		switch (selectedTab) {
			case R.id.radio_btn_upcoming:
				spinnerIndex = 0;
				loadSpinnerData(5 + Calendar.getInstance().get(Calendar.MONTH), 5 + Calendar.getInstance().get(Calendar.MONTH) + 6);
				isPastEvents = false;
				tabIndex = 1;
				txtViewEventTitle.setText("Listing Upcoming Events");
				radioBtnUpcoming.setButtonDrawable(R.drawable.upcoming_tab_blue);
				radioBtnMyEvents.setButtonDrawable(R.drawable.my_event);
				radioBtnPast.setButtonDrawable(R.drawable.past_w);

				break;
				
			case R.id.radio_btn_my_event:
				spinnerIndex = 0;
				loadSpinnerData(5 + Calendar.getInstance().get(Calendar.MONTH), 5 + Calendar.getInstance().get(Calendar.MONTH) + 6);
//				spinnerIndex = Calendar.getInstance().get(Calendar.MONTH);
//				loadSpinnerData(5 , 17);
				isPastEvents = false;
				tabIndex = 2;
				txtViewEventTitle.setText("Events Bookmarked By You");
				radioBtnUpcoming.setButtonDrawable(R.drawable.upcoming_tab_w);
				radioBtnMyEvents.setButtonDrawable(R.drawable.my_event_blue);
				radioBtnPast.setButtonDrawable(R.drawable.past_w);
				break;
				
			case R.id.radio_btn_past:
				spinnerIndex = 5;
				loadSpinnerData(5 + Calendar.getInstance().get(Calendar.MONTH) - 5, 5 + Calendar.getInstance().get(Calendar.MONTH) + 1);
				isPastEvents = true;
				tabIndex = 3;
				txtViewEventTitle.setText("Listing Past Events");
				radioBtnUpcoming.setButtonDrawable(R.drawable.upcoming_tab_w);
				radioBtnMyEvents.setButtonDrawable(R.drawable.my_event);
				radioBtnPast.setButtonDrawable(R.drawable.past_blue);
				break;
	
			default:
				break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		eventAdapter.notifyDataSetChanged();
		if(SidraPulseApp.getInstance().getEventsInfoList() != null) {
			if(SidraPulseApp.getInstance().getEventsInfoList().size() == 0) {
				lstViewEvents.setVisibility(View.GONE);
				txtViewEmptyList.setVisibility(View.VISIBLE);
			}
		}
	}

}


