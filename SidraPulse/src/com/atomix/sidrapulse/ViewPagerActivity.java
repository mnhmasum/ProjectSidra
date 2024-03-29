package com.atomix.sidrapulse;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.atomix.adapter.ViewPagerAdapter;
import com.atomix.sidrainfo.SidraPulseApp;

public class ViewPagerActivity extends Activity implements OnClickListener, OnPageChangeListener {

	private Button btnBack;
	private Button btnMenu;
	private ViewPager viewPager;
	private TextView title;
	private boolean isPathLocal = true;
	private int typeIs;
	ArrayList<HashMap<String, String>> mediaList;
	ViewPagerAdapter viewPagerAdapter;
	boolean isFirstTime = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_pager);
		
		initViews();
		setListener();
		loadData();
	}
	
	private void initViews() {
		title = (TextView) findViewById(R.id.txt_view_title);
		btnBack = (Button) findViewById(R.id.btn_back);
		btnMenu = (Button) findViewById(R.id.btn_menu);
		viewPager = (ViewPager) findViewById(R.id.view_pager);
	}
	
	private void setListener() {
		btnBack.setOnClickListener(this);
		btnMenu.setOnClickListener(this);
		viewPager.setOnPageChangeListener(this);
	}
	
	private void loadData() {
		mediaList = new ArrayList<HashMap<String, String>>();
		
		for(int count = 0; count < SidraPulseApp.getInstance().getGalleryDetailsInfoList().size(); count++) {
			HashMap<String, String> map1 = new HashMap<String, String>();
			//if(SidraPulseApp.getInstance().getGalleryDetailsInfoList().get(count).getMediaType() != 2) {
				map1.put("media_type", Integer.toString(SidraPulseApp.getInstance().getGalleryDetailsInfoList().get(count).getMediaType()));
				map1.put("url", SidraPulseApp.getInstance().getGalleryDetailsInfoList().get(count).getImageOrVideoUrl());
				mediaList.add(map1);
			//}
		} 
		
		if(getIntent().getExtras() != null) {
			int position = getIntent().getExtras().getInt("click_position");
			typeIs = getIntent().getExtras().getInt("type_is");
			if(typeIs == 1){
				title.setText("Sidra Forums");
			}else if (typeIs == 2){
				title.setText("Classifieds");
			} else if (typeIs == 3){
				title.setText("Offers");
			} else{
				title.setText("Gallery");
			}
			
			
			viewPagerAdapter = new ViewPagerAdapter(ViewPagerActivity.this, mediaList, isPathLocal, false, typeIs );
			
			viewPager.setAdapter(viewPagerAdapter);
			viewPager.setCurrentItem(position);
		}
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			onBackPressed();
			break;
			
		case R.id.btn_menu:
			startActivity(new Intent(ViewPagerActivity.this, MainMenuActivity.class));
			finish();
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		Log.e("onPageScrollStateChanged","****" + arg0);
		
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		Log.e("onPageScrolled","****" + arg0 + "==" + arg2);
	}
	
	@Override
	public void onPageSelected(int arg0) {
		Log.e("onPageSelected","****" + arg0);
		
	}
	

}
