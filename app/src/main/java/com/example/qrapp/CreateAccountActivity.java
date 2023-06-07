package com.example.qrapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
//import com.google.firebase.ktx.Firebase;

public class CreateAccountActivity extends AppCompatActivity {
    EditText emailEditTest,passwordEditText,confirmPasswordEditText;
    Button createAccountButton;
    ProgressBar progressBar;
    TextView loginBtnTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        emailEditTest=findViewById(R.id.email_edit_text);
        passwordEditText=findViewById(R.id.password_edit_text);
        confirmPasswordEditText=findViewById(R.id.confirm_password_edit_text);
        createAccountButton=findViewById(R.id.creat_account_btn);
        progressBar=findViewById(R.id.progress_bar);
        loginBtnTextView=findViewById(R.id.login_text_view_btn);
        createAccountButton.setOnClickListener(v->createAccount());
        loginBtnTextView.setOnClickListener(v->finish());

    }
    void createAccount (){
        String email=emailEditTest.getText().toString();
        String password=passwordEditText.getText().toString();
        String confirmPassword=confirmPasswordEditText.getText().toString();

        boolean isValidated= validateData(email,password,confirmPassword);
        if(!isValidated){
            return;
        }
        createAccountInFirebase(email,password);
    }
    void  createAccountInFirebase(String email,String password){
        changeInProgress(true);
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if(task.isSuccessful()){
                           // Utility.showToast(CreatAccountActivity.this,"Successfully Created,Check Email to verify");
                            Toast.makeText(getApplicationContext(),"Successfully Created,Check Email to verify",Toast.LENGTH_SHORT).show();
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }else{
                           // Utility.showToast(CreatAccountActivity.this,task.getException().getLocalizedMessage());
                            Toast.makeText(getApplicationContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );



    }
    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccountButton.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            createAccountButton.setVisibility(View.VISIBLE);
        }
    }
    boolean validateData(String email,String password , String confirmPassword ){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditTest.setError("Email is Invalid");
            return false;
        }
        if(password.length()<6){
            passwordEditText.setError("Password must be greater than 6");
            return  false;
        }
        if(!password.equals(confirmPassword)){
            confirmPasswordEditText.setError("Passwords dont match");
            return false;
        }
        return true;
    }
}