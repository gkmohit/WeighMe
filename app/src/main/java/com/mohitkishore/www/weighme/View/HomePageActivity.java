package com.mohitkishore.www.weighme.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohitkishore.www.weighme.Model.Weight;
import com.mohitkishore.www.weighme.R;
import com.mohitkishore.www.weighme.Utils.FirebaseConstants;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomePageActivity extends AppCompatActivity {

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @BindView(R.id.weight_lists_view)
    ListView mWeightLists;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.fab)
    public void logWeight(View view) {
        LayoutInflater inflater = LayoutInflater.from(HomePageActivity.this);
        View dialogView = inflater.inflate(R.layout.dialog_log_weight, null);
        AlertDialog.Builder alertDialogBuilder = createAlertDialog(dialogView);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        setListenersForAlertDialog(dialogView, alertDialog);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {

        } else if (item.getItemId() == R.id.action_sign_out) {
            signOut();
        }
        return true;
    }

    @NonNull
    private AlertDialog.Builder createAlertDialog(final View dialogView) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomePageActivity.this);

        alertDialogBuilder.setTitle(getString(R.string.log_weight));
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.setPositiveButton(getString(R.string.log), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText weight = (EditText) dialogView.findViewById(R.id.logged_weight_text);
                String loggedWeight = weight.getEditableText().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference weightUserReference = database.getReference(
                        FirebaseConstants.USERS + "/" + mAuth.getCurrentUser().getUid());

                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                String year = calendar.get(Calendar.YEAR) + "";
                String month = calendar.get(Calendar.MONTH) + "";
                String date = calendar.get(Calendar.DATE) + "";
                String time = calendar.getTime().toString();
                Weight w = new Weight(date, month, year, time, loggedWeight);
                String key = weightUserReference.getKey();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(key, w.toMap());
                weightUserReference.push().setValue(childUpdates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {

                        }
                    }
                });
            }
        });
        return alertDialogBuilder;
    }

    private void setListenersForAlertDialog(View dialogView, final AlertDialog alertDialog) {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        EditText loggedWeight = (EditText) dialogView.findViewById(R.id.logged_weight_text);
        loggedWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        startActivity(new Intent(HomePageActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }
}
