package ie.ul.loginsignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView registerTextView;
    private EditText emailAddress, pw;
    private Button signIn;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        registerTextView = (TextView) findViewById(R.id.register);
        registerTextView.setOnClickListener(this);

        signIn = (Button) findViewById(R.id.signInBtn);
        signIn.setOnClickListener(this);

        emailAddress = (EditText) findViewById(R.id.editTextEmailAddress);
        pw = (EditText) findViewById(R.id.editTextPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.register:
                startActivity(new Intent(this, RegisterUser.class));
                break;

            case R.id.signInBtn:
                userLogin();
                break;
        }
    }

    private void userLogin() {
        String email = emailAddress.getText().toString().trim();
        String password = pw.getText().toString().trim();
        if (validation(email, password) == false){
            email = "";
            password = "";
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                    if (user.isEmailVerified()) {
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    }else {
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Check your email to verify account", Toast.LENGTH_LONG).show();

                    }
                }else {
                    Toast.makeText(MainActivity.this, "Failed to log in. please check your credentionals", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validation(String email, String password) {
        if(email.isEmpty()){
            emailAddress.setError("Email can't be empty");
            emailAddress.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailAddress.setError("Email is not valid");
            emailAddress.requestFocus();
            return false;
        }

        if(password.isEmpty()){
            pw.setError("Password can't be empty");
            pw.requestFocus();
            return false;
        }
        return true;
    }


}