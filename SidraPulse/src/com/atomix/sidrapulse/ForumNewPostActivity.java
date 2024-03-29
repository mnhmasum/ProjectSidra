package com.atomix.sidrapulse;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.atomix.customview.DialogController;
import com.atomix.customview.SidraCustomProgressDialog;
import com.atomix.interfacecallback.OnUploadComplete;
import com.atomix.internetconnection.InternetConnectivity;
import com.atomix.jsonparse.CommunicationLayer;
import com.atomix.multipleimagepicker.CustomGallery;
import com.atomix.sidrainfo.ConstantValues;
import com.atomix.sidrainfo.SidraPulseApp;
import com.atomix.synctask.ImageUploadAsyncTask;
import com.atomix.utils.Utilities;

public class ForumNewPostActivity extends Activity implements OnClickListener,OnFocusChangeListener{
	private Button btnBack;
	private Button btnMenu;
	private TextView txtViewTitle;
	private TextView txtViewCharacterCount;
	private EditText editTxtDescription;
	private Button btnSend;
	private Button btnAdd;
	private TextView txtViewHashtag;
	
	private RelativeLayout relativeMain;
	private LinearLayout linearImageAdd;
	
	private String newPostHashtag = "";
 
	private final int CHOOSE_MULTIPLE = 200;
	private final int CHOOSE_SINGLE = 100;

	private int imageAdded = 0;
	
	public ArrayList<CustomGallery> customGallery;
	public ArrayList<String> takePath;
	private Animation anim;
	
	public static String serverImagePathList = "";
	private Dialog dialogPostingNotice;
	private Dialog dialogConfirmNavigation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forum_new_post);
		initViews();
		setListener();
		loadData();
		initPostNoticeUI();
		initConfirmDialog();
	}

	private void loadData() {
		if( getIntent().getExtras() != null) {
			newPostHashtag = getIntent().getExtras().getString("HASH_TAG");
			editTxtDescription.setText(newPostHashtag);
			editTxtDescription.setSelection(editTxtDescription.getText().toString().length());
			txtViewHashtag.setVisibility(View.VISIBLE);
			txtViewHashtag.setText("New Post about "+ newPostHashtag);
		} else{
			txtViewHashtag.setText("New Forum Post");
			
		}
		
	}

	private void initViews() {
		anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(500); // the blinking time can be adjust with this parameter
		anim.setStartOffset(20);
		anim.setRepeatMode(Animation.REVERSE);
		anim.setRepeatCount(Animation.INFINITE);
		
		imageAdded = 0;
		customGallery = new ArrayList<CustomGallery>();
		takePath = new ArrayList<String>();
		
		btnBack = (Button) findViewById(R.id.btn_back);
		btnMenu = (Button) findViewById(R.id.btn_menu);
		txtViewHashtag = (TextView) findViewById(R.id.txt_view_hash_tag_name);
		
		txtViewTitle = (TextView) findViewById(R.id.txt_view_title);
		txtViewCharacterCount = (TextView) findViewById(R.id.txt_view_character_count);
		
		editTxtDescription = (EditText) findViewById(R.id.edt_txt_description_main);
		editTxtDescription.setImeOptions(EditorInfo.IME_ACTION_DONE);  
		
		btnSend = (Button) findViewById(R.id.btn_send);
		btnSend.setEnabled(false);
    	btnSend.setAlpha(0.3f);
		
		relativeMain = (RelativeLayout) findViewById(R.id.relative_main);
		linearImageAdd = (LinearLayout) findViewById(R.id.linear_img_add);
		btnAdd = (Button) findViewById(R.id.btn_add);
	}
	
	private void setListener() {
		relativeMain.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnMenu.setOnClickListener(this);
		btnSend.setOnClickListener(this);
		btnAdd.setOnClickListener(this);
		
		editTxtDescription.setOnFocusChangeListener(this);
		
		editTxtDescription.addTextChangedListener(new TextWatcher() {
		    @Override
		    public void onTextChanged(CharSequence s, int start, int before, int count) {
		    	
		    }

		    @Override
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		    	
		    }

		    @Override
		    public void afterTextChanged(Editable s) {
		        //if(editTxtDescription.getText().length() >= 0 && editTxtDescription.getText().length() <= 250) {
		    	if (editTxtDescription.getText().toString().trim().length() > 250) {
		    		btnSend.setEnabled(false);
		    		btnSend.setAlpha(0.3f);
		        	int lengthChar = 250 - editTxtDescription.getText().toString().trim().length();
		        	txtViewCharacterCount.setText("" + lengthChar );
		        	txtViewCharacterCount.setTextColor(android.graphics.Color.RED);
		        	return;
		        	
		    	} if(editTxtDescription.getText().toString().trim().length() > 0 && editTxtDescription.getText().toString().trim().length() <= 250) {
		        	btnSend.setEnabled(true);
		    		btnSend.setAlpha(1.0f);
		        	int lengthChar = 250 - editTxtDescription.getText().toString().trim().length();
		        	txtViewCharacterCount.setText("" + lengthChar );
		        	txtViewCharacterCount.setTextColor(0xFF9B9CA0);
		        	
		        	if(editTxtDescription.getText().toString().trim().length() >= 200 && editTxtDescription.getText().toString().trim().length() <= 250) {
		        		txtViewCharacterCount.setTextColor(android.graphics.Color.RED);
		        		//txtViewCharacterCount.startAnimation(anim);
		    		}
		        	
		        	if(editTxtDescription.getText().toString().trim().length() > 245 && editTxtDescription.getText().toString().trim().length() <= 250) {
		        		txtViewCharacterCount.setTextColor(android.graphics.Color.RED);
		        		txtViewCharacterCount.startAnimation(anim);
		    		} else {
		    			txtViewCharacterCount.clearAnimation();
		    		}
		        } else {
		        	btnSend.setEnabled(false);
		    		btnSend.setAlpha(0.3f);
		        	int lengthChar = 250 - editTxtDescription.getText().toString().trim().length();
		        	txtViewCharacterCount.setText("" + lengthChar );
		        	txtViewCharacterCount.setTextColor(0xFF9B9CA0);
		        	//Utilities.showToast(ForumNewPostActivity.this, "Reached to maximum number of character");
		        }
		        
		     /*   if (editTxtDescription.getText().toString().trim().length() == 0) {
		        	btnSend.setEnabled(false);
		        	btnSend.setAlpha(0.3f);
		        	//Utilities.showToast(ForumsDetailsActivity.this, "Reached to maximum number of character");
		        }*/
		    }
		});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			onBackPressed();
			break;

		case R.id.btn_menu:
			if(editTxtDescription.getText().toString().trim().length() > 0 ||  !takePath.isEmpty()) {
				dialogConfirmNavigation.show();
			} else {
				startActivity(new Intent(ForumNewPostActivity.this, MainMenuActivity.class));
				finish();
			}
			
			break;
			
		case R.id.btn_send:
			if (!editTxtDescription.getText().toString().trim().equalsIgnoreCase("")) {
				//dialogPostingNotice.show();
				savePost();
			} else {
				Utilities.showToast(ForumNewPostActivity.this, "Please fill up description field");
				break;
			}
			
			break;
			
		case R.id.relative_main:
			SidraPulseApp.getInstance().hideKeyboard(getApplicationContext(), v);
			break;

		case R.id.btn_add:
			if (imageAdded == 0) {
				selectImage();
			} else{
				Utilities.showToast(ForumNewPostActivity.this, "You have already added an image");
			}

		default:
			break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.edt_txt_description_main:
			break;
		default:
			break;
		}
	}
	
	private void selectImage() {
		Intent i = new Intent("luminous.ACTION_PICK");
		startActivityForResult(i, CHOOSE_SINGLE);
	}
	
	private Bitmap rotateImage(Bitmap bitmap, String filePath)
	{
	    Bitmap resultBitmap = bitmap;
	    resultBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
	    //resultBitmap = Bitmap.createScaledBitmap(resultBitmap, 100, 100, true);
	    //resultBitmap = decodeSampledBitmapFromResource(null, filePath, 0, 100, 100);
	    
	    try
	    {
	        ExifInterface exifInterface = new ExifInterface(filePath);
	        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
	        Log.i("Orientation: ","Value " + orientation);
	        
	        Matrix matrix = new Matrix();

	        if (orientation == ExifInterface.ORIENTATION_ROTATE_90){
	            matrix.postRotate(90);
	            
	        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180){
	            matrix.postRotate(180);
	            
	        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270){
	            matrix.postRotate(270);
	            
	        } else if (orientation == ExifInterface.ORIENTATION_NORMAL) {
	        	return resultBitmap;
	        } 
	        
	        // Rotate the bitmap
	        // resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	        
	        resultBitmap = Bitmap.createBitmap(resultBitmap, 0, 0, resultBitmap.getWidth(), resultBitmap.getHeight(), matrix, true);
	        //
	    }
	    catch (Exception exception)
	    {
	        Log.d("Rotation: ","Could not rotate the image");
	        return resultBitmap;
	    }
	    
	    return resultBitmap;
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			 if (requestCode == CHOOSE_SINGLE) {
				  String single_path = data.getStringExtra("single_path");
				  
				  try {
						LayoutInflater inflater = LayoutInflater.from(ForumNewPostActivity.this);
						final View item = inflater.inflate(R.layout.select_image_row, null);
						ImageView imageView = (ImageView) item.findViewById(R.id.img_view_select);
						Button btnCancel = (Button) item.findViewById(R.id.btn_cancel);
						
						AQuery aq = new AQuery(ForumNewPostActivity.this);
						BitmapAjaxCallback bmCallBack = new BitmapAjaxCallback();
						bmCallBack.url(single_path).targetWidth(300).rotate(true);
						bmCallBack.memCache(true);
						bmCallBack.fileCache(true);
						aq.id(imageView).image(bmCallBack);
						
						//imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.id.useLogo, 100, 100));
						
						btnCancel.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Log.i("CLickImage_pos_camera","" + v.getTag());
								((LinearLayout)item.getParent()).removeView(item);
								imageAdded = 0;
								int removeIndex = (Integer) v.getTag();
			            		takePath.remove((Integer) v.getTag());
			            		takePath.clear();
			            		Log.i("THE SIZE IS ","" + takePath.size());
			            		
			            		if (editTxtDescription.getText().toString().trim().equalsIgnoreCase("")) {
									btnSend.setEnabled(false);
									btnSend.setAlpha(0.3f);
								}
			            		
			            		/** Reset the item id**/
			            		/*for (int itemIndex = 1; itemIndex <= linearImageAdd.getChildCount(); itemIndex++) {
									View v1 = linearImageAdd.getChildAt(itemIndex - 1);
									Button b = (Button) ((RelativeLayout) v1).getChildAt(1);
									b.setTag(itemIndex);
								}
								for (int j = 0; j < takePath.size(); j++) {
									takePath.get(j);
								}*/
							}
						});
							
						imageAdded = 1;
						btnCancel.setTag(imageAdded);
						linearImageAdd.addView(item);
						
						if (imageAdded > 0 && editTxtDescription.getText().toString().trim().length() <=250) {
							btnSend.setEnabled(true);
							btnSend.setAlpha(1.0f);
						}
						
					
						if (InternetConnectivity.isConnectedToInternet(ForumNewPostActivity.this)) {
							ImageUploadAsyncTask imageUploadFromCamera = new ImageUploadAsyncTask(ForumNewPostActivity.this, new OnUploadComplete() {
								
								@Override
								public void onUploadComplete(int responseStatus, String data) {
									if(responseStatus == 1) {
										// load data to adapter
										Log.i("UPLOAD STATUS CAMERA","OK " + data);
										takePath.add(data);
									} else if(responseStatus == 5) {
										SidraPulseApp.getInstance().accessTokenChange(ForumNewPostActivity.this);
										
									} 
								}
								
							}, 1);
							imageUploadFromCamera.execute(ConstantValues.FUNC_ID_THREAD_PHOTO_UPLOAD, single_path);
						
						} else {
							SidraPulseApp.getInstance().openDialogForInternetChecking(ForumNewPostActivity.this);
						}
						
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
						Log.i("OME Error", "===" + e.getMessage());
					}
			}
		}
	}

	private ProgressDialog dlogProg;
	public int response = 0;

	public class postForumThreadAsyncTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			dlogProg = SidraCustomProgressDialog.creator(ForumNewPostActivity.this);
		}

		@Override
		protected Void doInBackground(String... params) {
			String allImagePath = params[0];
			String tagName = params[1];
			try {
				response = CommunicationLayer.getThreadEntryAPI(ConstantValues.FUNC_ID_THREAD_ENTRY_API, Integer.toString(SidraPulseApp.getInstance().getUserInfo().getUserID()), SidraPulseApp.getInstance().getUserInfo().getAccessToken(), allImagePath, editTxtDescription.getText().toString().trim(), tagName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (dlogProg.isShowing()) {
				dlogProg.dismiss();
			}
			
			if (response==1) {
				Utilities.showToast(getApplicationContext(), "Posted Successfully to the Sidra Forum");
				linearImageAdd.removeAllViews();
				editTxtDescription.setText("");
				imageAdded = 0;
				takePath.clear();
				
				Intent intent = new Intent(ForumNewPostActivity.this, ForumsActivity.class);
			    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    startActivity(intent);
			    finish();
				
			} else if(response == 5) {
				SidraPulseApp.getInstance().accessTokenChange(ForumNewPostActivity.this);
				
			} else {
				Utilities.showToast(ForumNewPostActivity.this, "Thread Not Created Successfully to the Sidra Forum");
			}
			
			Log.i("CREATE_THREAD_RESPONSE","" + response);
		
		}
	}
	
	@Override
	protected void onDestroy() {
		takePath.clear();
		serverImagePathList = "";
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
//		if(editTxtDescription.getText().toString().trim().length() > 0 ||  !takePath.isEmpty()) {
//			dialogConfirmNavigation.show();
//		} else {
//			super.onBackPressed();
//		}
	}
	
	
	
	private void initPostNoticeUI() {
		dialogPostingNotice = new DialogController(ForumNewPostActivity.this).PostNoticeDialog();
		final Button btnCancel = (Button) dialogPostingNotice.findViewById(R.id.btnCancel);
		final Button btnClose = (Button) dialogPostingNotice.findViewById(R.id.btn_close);
		
		btnClose.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialogPostingNotice.dismiss();
			}
		});

		final Button btnOk = (Button) dialogPostingNotice.findViewById(R.id.btnOk);

		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialogPostingNotice.dismiss();
			}
		});

		btnOk.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialogPostingNotice.dismiss();
				savePost();
			}
		});
	}
	
	private void initConfirmDialog() {
		DialogController dilog = new DialogController(ForumNewPostActivity.this);
		dialogConfirmNavigation = dilog.DeleteConfirmationDialog();
		final Button btnYes = (Button) dialogConfirmNavigation.findViewById(R.id.btn_yes);
		final Button btnNo = (Button) dialogConfirmNavigation.findViewById(R.id.btn_no);

		btnYes.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialogConfirmNavigation.dismiss();
				finish();
			}
		});
		
		btnNo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialogConfirmNavigation.dismiss();
			}
		});

	}
	
	private void savePost() {
		if( takePath != null) {
			for(int j = 0; j < takePath.size(); j++) {
				takePath.get(j);
				serverImagePathList += takePath.get(j) + ",";
			}
		}
		
		StringBuffer buffer = new StringBuffer();
		Pattern MY_PATTERN = Pattern.compile("#(\\w+)");
		Matcher mat = MY_PATTERN.matcher(editTxtDescription.getText().toString().trim());
		
		while (mat.find()) {
			buffer.append(mat.group(1) +",");
		}
		
		if (buffer.length() > 0) {
			buffer.setLength(buffer.length() - 1);
		}
		
		if (InternetConnectivity.isConnectedToInternet(ForumNewPostActivity.this)) {
			if (serverImagePathList.length() > 1) {
				new postForumThreadAsyncTask().execute(serverImagePathList.substring(0, serverImagePathList.length() - 1), buffer.toString());
			} else {
				new postForumThreadAsyncTask().execute("", buffer.toString());
			}
		} else {
			SidraPulseApp.getInstance().openDialogForInternetChecking(ForumNewPostActivity.this);
		}
	}

}
