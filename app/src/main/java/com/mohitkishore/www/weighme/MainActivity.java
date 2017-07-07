package com.mohitkishore.www.weighme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.lamudi.phonefield.PhoneEditText;

import java.util.concurrent.TimeUnit;

import at.markushi.ui.CircleButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = MainActivity.class.getSimpleName();


    private PhoneEditText mPhoneEditText;
    private CircleButton mSubmitButton;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initListeners();
        mPhoneEditText.setHint(R.string.phone_number_hint);
        mPhoneEditText.setDefaultCountry("CA");

        mCallbacks = getCallbacks();

    }

    @NonNull
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks getCallbacks() {
        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

//                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Toast.makeText(MainActivity.this, getString(R.string.error_string_too_many_requests), Toast.LENGTH_LONG).show();
                }

                // Show a message and update the UI
                // ...
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

                // ...
            }
        };
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.submitButton) {
            submitPhoneNumber();
        }
    }

    private void initViews() {
        mPhoneEditText = (PhoneEditText) findViewById(R.id.phone_number);
        mSubmitButton = (CircleButton) findViewById(R.id.submitButton);
    }

    private void initListeners() {
        mSubmitButton.setOnClickListener(this);
    }

    private void submitPhoneNumber() {
        boolean valid = validateNumber();
        if (valid) {
            String phoneNumber = mPhoneEditText.getPhoneNumber();

            firebaseNumberAuth(phoneNumber);
        }
    }


    private void firebaseNumberAuth(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }


    private boolean validateNumber() {
        boolean valid = true;

        if (mPhoneEditText.isValid()) {
            mPhoneEditText.setError(null);
        } else {
            mPhoneEditText.setError(getString(R.string.invalid_phone_number));
            valid = false;
        }

        if (valid) {
            Toast.makeText(MainActivity.this, R.string.valid_phone_number, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, R.string.invalid_phone_number, Toast.LENGTH_LONG).show();
        }

        return valid;
    }
}
