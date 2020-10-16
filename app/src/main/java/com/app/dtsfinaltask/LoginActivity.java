package com.app.dtsfinaltask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dtsfinaltask.helper.UserInformesyen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    AppCompatCheckBox showPass;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        auth = FirebaseAuth.getInstance();

        showPass = findViewById(R.id.showpass_login);
        showPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    public void goRegister(View view){
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }

    public void masokIn(View view){

        String email = edtEmail.getText().toString().trim();
        String pass  = edtPassword.getText().toString().trim();

        if(email.isEmpty() && pass.isEmpty()){
            edtEmail.setError("Field email tidak boleh kosong!");
            edtPassword.setError("Field password tidak boleh kosong!");
            return;
        } else if(email.isEmpty()){
            edtEmail.setError("Field email tidak boleh kosong!");
            return;
        } else if(!isValidEmail(email)){
            edtEmail.setError("Email tidak valid!");
            return;
        } else if(pass.isEmpty()){
            edtPassword.setError("Field password tidak boleh kosong!");
            return;
        } else{
            final FirebaseUser user = auth.getCurrentUser();
            final UserInformesyen userInformesyen = new UserInformesyen();
            auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Note: " + task.getException(), Toast.LENGTH_LONG).show();
                            } else {
                                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        }
                    });
        }
    }

    public static boolean isValidEmail(CharSequence email){
        return (Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}