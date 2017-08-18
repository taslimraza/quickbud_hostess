package com.shaddyhollow.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.dinendashhostess.requests.LoginHostessRequest;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.FlurryEvents;
import com.shaddyhollow.quicktable.models.LocationEntry;
import com.shaddyhollow.robospice.BaseListener;
import com.shaddyhollow.robospice.BaseRoboSpiceActivity;
import com.shaddyhollow.util.FileOperations;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends BaseRoboSpiceActivity {
	
	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
	public static final String EXTRA_PASSWORD = "com.example.android.authenticatordemo.extra.PASSWORD";
	public static final String EXTRA_OFFLINE = "com.example.android.authenticatordemo.extra.OFFLINE";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
	private boolean mForceOffline;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private CheckBox mOfflineView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private String serialId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		getActionBar().setTitle("QuickBud");
		getActionBar().setIcon(R.drawable.quickbud_image);
		
		FileOperations.createLogDirectory();

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});
		mPassword = getIntent().getStringExtra(EXTRA_PASSWORD);
		mPasswordView.setText(mPassword);
		
		mForceOffline = getIntent().getBooleanExtra(EXTRA_OFFLINE, false);
		mOfflineView = (CheckBox) findViewById(R.id.offline);
		mOfflineView.setChecked(mForceOffline);
		
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		
		TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		serialId = manager.getDeviceId();

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//TODO implement password reminder
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mForceOffline = mOfflineView.isChecked();

//		if(!Config.isOnline() && !mForceOffline) {
//			Toast.makeText(this, "No network is available.  Please select Offline option'", Toast.LENGTH_SHORT).show();
//			return;
//		}
		
		Config.forceOffline(mForceOffline);

		boolean cancel = false;
		View focusView = null;

		if(!mForceOffline) {
			// Check for a valid password.
			if (TextUtils.isEmpty(mPassword)) {
				mPasswordView.setError(getString(R.string.error_field_required));
				focusView = mPasswordView;
				cancel = true;
			} else if (mPassword.length() < 4) {
				mPasswordView.setError(getString(R.string.error_invalid_password));
				focusView = mPasswordView;
				cancel = true;
			}
	
			// Check for a valid email address.
			if (TextUtils.isEmpty(mEmail)) {
				mEmailView.setError(getString(R.string.error_field_required));
				focusView = mEmailView;
				cancel = true;
			} else if (!mEmail.contains("@")) {
				mEmailView.setError(getString(R.string.error_invalid_email));
				focusView = mEmailView;
				cancel = true;
			}
		}
		
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			
			mEmail = Config.validateUsername(mEmail);
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			
	        LoginHostessRequest request = new LoginHostessRequest(mEmail, mPassword, mForceOffline, serialId );
	        request.execute(contentManager, new LoginHostessRequestListener() );
		}
	}

	private class LoginHostessRequestListener extends BaseListener<LocationEntry> {

		@Override
		public void onFailure(SpiceException e) {
	        Toast.makeText( getApplicationContext(), "Error during Hostess Login: " + e.getMessage(), Toast.LENGTH_LONG ).show();
	    	 FlurryAgent.logEvent(FlurryEvents.LOGIN_FAIL.name());
			showProgress(false);
		}

		@Override
		public void onRequestSuccess(LocationEntry location) {
			showProgress(false);
			
			if (location == null) {
		        Toast.makeText( getApplicationContext(), "Error during Hostess Login", Toast.LENGTH_LONG ).show();
				showProgress(false);
		    	FlurryAgent.logEvent(FlurryEvents.LOGIN_FAIL.name());
				return;
			}

			if (location.getTenantId() != 0) {
				Intent returnIntent = new Intent();
				returnIntent.putExtra("location", location.toString());
				returnIntent.putExtra("email", mEmail);
				setResult(RESULT_OK, returnIntent);
				FlurryAgent.logEvent(FlurryEvents.LOGIN_SUCCESS.name());
				finish();
			} else {
		    	FlurryAgent.logEvent(FlurryEvents.LOGIN_FAIL.name());
				mPasswordView.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
}
