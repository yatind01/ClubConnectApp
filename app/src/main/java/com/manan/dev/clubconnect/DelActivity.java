package com.manan.dev.clubconnect;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manan.dev.clubconnect.Adapters.CoordinatorAdapter;
import com.manan.dev.clubconnect.Models.Coordinator;

import java.util.ArrayList;

public class DelActivity extends AppCompatActivity {
    private DatabaseReference database;
    private FirebaseUser mUser;
    private String clubName;
    private ArrayList<Coordinator> coordinatorsList;
    private String nameList[];
    private AutoCompleteTextView coordinatorAutoCompleteTextView;
    private CoordinatorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_del);
        final LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
        coordinatorsList = new ArrayList<Coordinator>();
        coordinatorAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.coordinator_text_view);
        new FetchDBDetails().execute("");
        nameList = new String[]{"Kushank"};
        coordinatorAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coordinatorAutoCompleteTextView.showDropDown();
            }
        });
       coordinatorAutoCompleteTextView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });
        coordinatorAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = new TextView(DelActivity.this);
                tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                String email = coordinatorAutoCompleteTextView.getText().toString();
                int i;
                for(i=0;i<coordinatorsList.size();i++){
                    if(nameList[i].equals(email))
                        break;
                }
                tv.setText(coordinatorsList.get(i).getName());
                ll.addView(tv);
                coordinatorAutoCompleteTextView.setText("");
            }
        });
    }

    private class FetchDBDetails extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            try {
                clubName = mUser.getEmail().split("@")[0];
                database = FirebaseDatabase.getInstance().getReference("coordinators").child(clubName);
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Coordinator coordinator = snapshot.getValue(Coordinator.class);
                            Toast.makeText(DelActivity.this, coordinator.getName(), Toast.LENGTH_SHORT).show();

                            try {
                                coordinatorsList.add(coordinator);
                                Toast.makeText(DelActivity.this, "" + coordinatorsList.size(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(DelActivity.this, "Error" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        onPost();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }

        private void onPost() {

            int si = coordinatorsList.size();
            Log.d("bhasad", si + "");
            try {
                nameList = new String[si];
                for (int i = 0; i < si; i++) {
                    nameList[i] = coordinatorsList.get(i).getEmail();
                    Log.d("bhasad", nameList[i]);
                }
                adapter = new CoordinatorAdapter(DelActivity.this, R.layout.coordinator_item_view, nameList);
                coordinatorAutoCompleteTextView.setThreshold(1);
                coordinatorAutoCompleteTextView.setAdapter(adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}