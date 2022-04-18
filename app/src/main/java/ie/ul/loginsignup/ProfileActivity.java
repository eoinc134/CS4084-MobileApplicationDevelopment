package ie.ul.loginsignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private FirebaseUser user;
    private DatabaseReference reference;
    private Button logout, applyChanges;
    private String userID;
    private long pressedTime;
    private Spinner spinner;
    private RadioButton radioMale, radioFemale;
    private String fullName, email, yearOfStudy, gender;



    @Override
    /**
     *
     *
     */
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_male:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radio_female:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        logout = (Button) findViewById(R.id.SignOutButton);
        logout.setOnClickListener(this);

        applyChanges = (Button) findViewById(R.id.applyChangesBtn);
        applyChanges.setOnClickListener(this);


        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://safeaccomodation-58b6c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        userID = user.getUid();

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        radioMale = (RadioButton)  findViewById(R.id.radio_male);
        radioFemale = (RadioButton)  findViewById(R.id.radio_female);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.year_of_study_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
//            }
//        });

        final TextView name = (TextView) findViewById(R.id.emptyFullNameTextView);
        final TextView emailTextView = (TextView) findViewById(R.id.emptyEmailTextView);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null){
                    fullName = userProfile.fullName;
                    email = userProfile.email;
                    yearOfStudy = userProfile.yearOfStudy;
                    gender = userProfile.gender;
                    name.setText(fullName);
                    emailTextView.setText(email);

                    // GENDER CHECKED
                    if (gender.equals("Male")) {
                        radioMale.setChecked(true);
                    } else if (gender.equals("Female")) {
                        radioFemale.setChecked(true);
                    }
                    // Spinner year selected.
                    // Not ideal way but with so few options hardcoding works better than spending the resources figuring out a solution
                    if (yearOfStudy.equals("First year")) {
                        spinner.setSelection(0,true);
                    } else if(yearOfStudy.equals("Second year")) {
                        spinner.setSelection(1,true);
                    } else if (yearOfStudy.equals("Third year")){
                        spinner.setSelection(2,true);
                    } else if (yearOfStudy.equals("Fourth year")){
                        spinner.setSelection(3,true);
                    } else if (yearOfStudy.equals("Post graduate")){
                        spinner.setSelection(4,true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.applyChangesBtn:
                //send to database
                //reference.child(userID).updateChildren(user);
                spinner = (Spinner) findViewById(R.id.spinner);
                yearOfStudy = String.valueOf(spinner.getSelectedItem());

                radioMale = (RadioButton)  findViewById(R.id.radio_male);
                radioFemale = (RadioButton)  findViewById(R.id.radio_female);
                gender = "";
                if (radioFemale.isChecked()) {
                    gender = "Female";
                } else if (radioMale.isChecked()) {
                    gender = "Male";
                }

                Log.d("Apply Changes", " Reached here successfully " + yearOfStudy);

                User user = new User(fullName, email, gender, yearOfStudy);

                FirebaseDatabase.getInstance("https://safeaccomodation-58b6c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ProfileActivity.this, "User successfully updated!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Error, try again", Toast.LENGTH_LONG).show();
                        }
                    }

                });


                break;
            case R.id.SignOutButton:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                break;
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        String selected = (String) parent.getItemAtPosition(pos);
        Log.d("spinner", "item selected is: " + selected);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}
