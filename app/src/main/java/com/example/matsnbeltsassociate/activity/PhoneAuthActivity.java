package com.example.matsnbeltsassociate.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.matsnbeltsassociate.R;
import com.example.matsnbeltsassociate.utils.CloudStoreHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    private boolean doubleBackToExitPressedOnce = false;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    //private ViewGroup mPhoneNumberViews;
    private ViewGroup mSignedInViews;
    private View mProgressView;
    private View mLoginFormView;
    //private TextView mStatusText;
    private TextView mDetailText;

    private EditText mPhoneNumberField;
    private EditText mVerificationCodeField;

    private Button mStartButton;
    private Button mVerifyButton;
    private Button mResendButton;
    private LinearLayout mVerifyLayout;
    private TextView mFieldAutoMsgText;
    private String countryCode;

    public static boolean isNetworkAvailable(Context con) {
        try {
            ConnectivityManager cm = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        if (!isNetworkAvailable(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), NetworkConnectivity.class);
            Log.i("PhoneAuthActivity:::", "No network connection ");
            intent.putExtra(LauncherActivity.EXTRA_MESSAGE, NetworkConnectivity.phoneAuth);
            startActivity(intent);
            finish();
        }
        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        final Toolbar mToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        // Assign views
        //mPhoneNumberViews = findViewById(R.id.phoneAuthFields);
        mSignedInViews = findViewById(R.id.signedInButtons);

        //mStatusText = findViewById(R.id.status);
        //mDetailText = findViewById(R.id.detail);
        countryCode = ((TextView) findViewById(R.id.country_code)).getText().toString();
        mPhoneNumberField = findViewById(R.id.mobileno);
        mVerificationCodeField = findViewById(R.id.fieldVerificationCode);
        mVerificationCodeField.setVisibility(View.INVISIBLE);

        mVerifyLayout = findViewById(R.id.verifyLayout);
        mVerifyLayout.setVisibility(View.INVISIBLE);
        mStartButton = findViewById(R.id.buttonStartVerification);
        mVerifyButton = findViewById(R.id.buttonVerifyPhone);
        mResendButton = findViewById(R.id.buttonResend);
        Button mSignOutButton = findViewById(R.id.signOutButton);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mFieldAutoMsgText = findViewById(R.id.fieldPhoneNumber);
        mFieldAutoMsgText.setVisibility(View.INVISIBLE);
        // Assign click listeners
        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    mPhoneNumberField.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };
        // [END phone_auth_callbacks]
    }
    // [END on_start_check_user]

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    public void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
         // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            if(task.getResult() == null) {
                                updateUI(STATE_SIGNIN_FAILED);
                            }
                            else {
                                FirebaseUser user = task.getResult().getUser();
                                // [START_EXCLUDE]
                                updateUI(STATE_SIGNIN_SUCCESS, user);
                                // [END_EXCLUDE]
                            }
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                mVerificationCodeField.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                            updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }
    // [END sign_in_with_phone]

    private void signOut() {
        mAuth.signOut();
        updateUI(STATE_INITIALIZED);
    }

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        Context context = getApplicationContext();
        if (!PhoneAuthActivity.isNetworkAvailable(context)) {
            Intent network_intent = new Intent(context, NetworkConnectivity.class);
            Log.i("PhoneAuthActivity:::", "No network connection ");
            network_intent.putExtra(LauncherActivity.EXTRA_MESSAGE, NetworkConnectivity.phoneAuth);
            startActivity(network_intent);
            finish();
        }
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        // [START_EXCLUDE]
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(countryCode + mPhoneNumberField.getText().toString());
        }
        // [END_EXCLUDE]
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
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        }

        return true;
    }

    private void showViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.VISIBLE);
        }
    }

    private void hideViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.INVISIBLE);
        }
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }


    private void writeUserNametoLocalFile(String mobile) {
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(LauncherActivity.fileName, Context.MODE_PRIVATE);
            Log.i("WriteFileLogin", "Writing in : " + LauncherActivity.fileName);
            outputStream.write((mobile).getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button
                enableViews(mStartButton, mPhoneNumberField);
                mVerifyLayout.setVisibility(View.VISIBLE);
                mFieldAutoMsgText.setVisibility(View.VISIBLE);
                hideViews(mVerifyLayout, mFieldAutoMsgText, mVerificationCodeField);
                //disableViews(mVerifyButton, mResendButton, mVerificationCodeField);
                //mDetailText.setText(null);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                showViews(mVerifyLayout, mFieldAutoMsgText, mVerificationCodeField);
                enableViews(mVerifyButton, mResendButton, mPhoneNumberField, mVerificationCodeField);
                disableViews(mStartButton);
                //mDetailText.setText(R.string.status_code_sent);
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.main_layout), R.string.status_code_sent, Snackbar.LENGTH_LONG);
                snackbar.show();
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                enableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                        mVerificationCodeField);
                //mDetailText.setText(R.string.status_verification_failed);
                snackbar = Snackbar
                        .make(findViewById(R.id.main_layout), R.string.status_verification_failed, Snackbar.LENGTH_LONG);
                snackbar.show();
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                disableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                        mVerificationCodeField);
                //mDetailText.setText(R.string.status_verification_succeeded);
                snackbar = Snackbar
                        .make(findViewById(R.id.main_layout), R.string.status_verification_succeeded, Snackbar.LENGTH_LONG);
                snackbar.show();

                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        mVerificationCodeField.setText(cred.getSmsCode());
                    } else {
                        mVerificationCodeField.setText(R.string.instant_validation);
                    }
                }

                break;
            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check
                snackbar = Snackbar
                        .make(findViewById(R.id.main_layout), R.string.status_sign_in_failed, Snackbar.LENGTH_LONG);
                snackbar.show();
                break;
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                break;
        }

        if (user == null) {
            // Signed out
           // mPhoneNumberViews.setVisibility(View.VISIBLE);
            mSignedInViews.setVisibility(View.GONE);

            //mStatusText.setText(R.string.signed_out);
        } else {
            // Signed in
            //mPhoneNumberViews.setVisibility(View.GONE);
            //mSignedInViews.setVisibility(View.VISIBLE);



            //mStatusText.setText(R.string.signed_in);
            //mDetailText.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            showProgress(true);
            final String mobile = getResources().getString(R.string.mobile_country_code) + mPhoneNumberField.getText().toString();
            writeUserNametoLocalFile(mobile);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(LauncherActivity.EXTRA_MESSAGE, mobile);
            Log.i("PhoneAuthActivity:::", "Starting with " + mobile);
            startActivity(intent);
            finish();
        }
    }

    public EditText getmPhoneNumberField() {
        return mPhoneNumberField;
    }

    @Override
    public void onClick(View view) {
        final String phoneNumber = getResources().getString(R.string.mobile_country_code) + mPhoneNumberField.getText().toString();
        switch (view.getId()) {
            case R.id.buttonStartVerification:
                Log.i("PhoneAuthActivity:::: ", " : " + phoneNumber);
                if (!PhoneAuthActivity.isNetworkAvailable(getApplicationContext())) {
                    Log.d(TAG, "No internet connection on verify mobile number in db");
                    mPhoneNumberField.setError("Check Internet Connection");
                    mPhoneNumberField.requestFocus();
                } else {
                    CloudStoreHelper.getInstance().doesAssociateExist(phoneNumber, this);
                }
                break;
            case R.id.buttonVerifyPhone:
                String code = mVerificationCodeField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationCodeField.setError("Cannot be empty.");
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.buttonResend:
                resendVerificationCode(mPhoneNumberField.getText().toString(), mResendToken);
                break;
            case R.id.signOutButton:
                signOut();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent intent = new Intent(this, CloseAppActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
