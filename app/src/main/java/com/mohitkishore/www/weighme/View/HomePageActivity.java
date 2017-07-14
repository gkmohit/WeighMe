package com.mohitkishore.www.weighme.View;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohitkishore.www.weighme.Adaptors.WeightListAdaptor;
import com.mohitkishore.www.weighme.Model.Weight;
import com.mohitkishore.www.weighme.R;
import com.mohitkishore.www.weighme.Utils.FirebaseConstants;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomePageActivity extends AppCompatActivity {

    private final String TAG = HomePageActivity.class.getSimpleName();

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @BindView(R.id.weight_lists_view)
    ListView mWeightLists;

    @BindView(R.id.line_chart)
    LineChart mLineChart;

    FirebaseListAdapter<Weight> mAdaptor;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference(FirebaseConstants.USERS + "/" + mAuth.getCurrentUser().getUid() + "/");
        mAdaptor = new FirebaseListAdapter<Weight>(HomePageActivity.this,
                Weight.class, R.layout.weight_list_item, databaseReference) {
            @Override
            protected void populateView(View v, Weight model, int position) {
                ((TextView) v.findViewById(R.id.weight)).setText(model.getWeight());
                ((TextView) v.findViewById(R.id.date)).setText(model.getDate());
                ((TextView) v.findViewById(R.id.month)).setText(model.getMonth());
            }
        };
        mWeightLists.setAdapter(mAdaptor);

        List<Entry> entries  = populateGraph();
        Log.d(TAG, "Size" + entries.size());
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

    private List<Entry> populateGraph() {
        final ProgressDialog progressDialog = ProgressDialog.show(HomePageActivity.this, null,
                getString(R.string.dialog_one_moment));
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference(FirebaseConstants.USERS + "/" + mAuth.getCurrentUser().getUid() + "/");
        final List<Entry> entries = new ArrayList<Entry>();
        final List<Float> xaxis = new ArrayList<Float>();
        final List<Float> yaxis = new ArrayList<Float>();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    HashMap<String, HashMap<String, Object>> data = (HashMap<String, HashMap<String, Object>>) dataSnapshot.getValue();
                    for (String key : data.keySet()) {
                        Weight w = new Weight(data.get(key));
                        xaxis.add(Float.parseFloat(w.getWeight()));
                        yaxis.add(Float.parseFloat(w.getWeight()));
                    }

                    Collections.sort(xaxis);
                    Collections.sort(yaxis);
                    for (int i = 0; i < xaxis.size(); i++){
                        entries.add(new Entry(xaxis.get(i), yaxis.get(i)));
                    }
                    LineDataSet dataSet = new LineDataSet(entries, "Label");
                    LineData lineData = new LineData(dataSet);
                    mLineChart.setData(lineData);
                    mLineChart.invalidate();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        };
        databaseReference.addListenerForSingleValueEvent(valueEventListener);

        return entries;
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
        alertDialogBuilder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final ProgressDialog progressDialog = ProgressDialog.show(HomePageActivity.this,
                        null,
                        getString(R.string.dialog_one_moment));
                progressDialog.show();

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

                weightUserReference.push().setValue(w.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {

                        }
                        progressDialog.dismiss();
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
