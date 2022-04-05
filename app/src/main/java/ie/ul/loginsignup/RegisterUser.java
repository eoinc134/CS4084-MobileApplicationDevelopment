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
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText editTextFullName, editTextEmail, editTextPassword, getEditTextPasswordConfirm;
    private TextView logIn;
    private Button register;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        logIn = (TextView) findViewById(R.id.logIn);
        logIn.setOnClickListener(this);

        register = (Button) findViewById(R.id.signInBtn);
        register.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.editTextEmailAddress);
        editTextFullName = (EditText) findViewById(R.id.editTextPassword);
        editTextPassword = (EditText) findViewById(R.id.editTextTextPassword);
        getEditTextPasswordConfirm = (EditText) findViewById(R.id.editTextTextConfirmPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logIn:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.signInBtn:
                registerUser();
                break;
        }

    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String name = editTextFullName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = getEditTextPasswordConfirm.getText().toString().trim();
        if (validation(email, name, password, confirmPassword) == false){
            email = "";
            name= "";
            password = "";
            confirmPassword = "";
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        String finalName = name;
        String finalEmail = email;
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        User user = new User(finalName, finalEmail);

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>(){
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                        if(task.isSuccessful()){
                                            Toast.makeText(RegisterUser.this, "User has been successfully registered!", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.VISIBLE);
                                        } else {
                                            Toast.makeText(RegisterUser.this, ", try again", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }

                        });
                    }else {
                        Toast.makeText(RegisterUser.this, "Failed to create account, try again", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }

                }
        });



    }

    private boolean validation(String email, String name, String password, String confirmPassword) {
        if(email.isEmpty()){
            editTextEmail.setError("Email can't be empty");
            editTextEmail.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Email is not valid");
            editTextEmail.requestFocus();
            return false;
        }
        if(name.isEmpty()){
            editTextFullName.setError("Name can't be empty");
            editTextFullName.requestFocus();
            return false;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Password can't be empty");
            editTextPassword.requestFocus();
            return false;
        }
        if(password.length() < 6){
            editTextPassword.setError("Password must be longer than 6 characters");
            editTextPassword.requestFocus();
            return false;
        }
        if(password.equals(confirmPassword) == false) {
            editTextPassword.setError("Passwords do not match");
            editTextPassword.requestFocus();
            return false;
        }
        return true;
    }


}