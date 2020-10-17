package com.app.dtsfinaltask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dtsfinaltask.helper.UserInformesyen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    EditText edtNama, edtEmail, edtPass, edtKonf;
    AppCompatCheckBox showPass;
    private FirebaseAuth auth;
    private FirebaseDatabase fDatabase;
    private DatabaseReference dbReference;
    private FirebaseFirestore fStore;

    String userID;
    String duit;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtNama = findViewById(R.id.edt_nama);
        edtEmail = findViewById(R.id.edt_email_reg);
        edtPass = findViewById(R.id.edt_password_reg);
        edtKonf = findViewById(R.id.edt_password_konfirmasi);

        auth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        fStore = FirebaseFirestore.getInstance();

        showPass = findViewById(R.id.showpass_reg);
        showPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    edtPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    edtKonf.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    edtPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    edtKonf.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

    }

    public void daftarIn(View view){

        String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
        String konf = edtKonf.getText().toString().trim();

        final String nama = edtNama.getText().toString();

        if(email.isEmpty() && pass.isEmpty() && konf.isEmpty()){
            edtEmail.setError("Field email tidak boleh kosong!");
            edtPass.setError("Field password tidak boleh kosong!");
            edtKonf.setError("Field konfirmasi password tidak boleh kosong!");
            return;
        } else if(email.isEmpty()){
            edtEmail.setError("Field email tidak boleh kosong!");
            return;
        } else if(!isValidEmail(email)){
            edtEmail.setError("Email tidak valid!");
            return;
        } else if(pass.isEmpty()){
            edtPass.setError("Field password tidak boleh kosong!");
            return;
        } else if(pass.length() < 6){
            edtPass.setError("Minimal password 6 karakter");
            return;
        } else if(konf.isEmpty()){
            edtKonf.setError("Field konfirmasi password tidak boleh kosong!");
            return;
        } else if(!konf.equals(pass)){
            edtKonf.setError("Konfirmasi password tidak sama!");
            return;
        } else {
            auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Note: " + task.getException(), Toast.LENGTH_LONG).show();
                            } else {

                                userID = auth.getCurrentUser().getUid();

//                                duit = "0";
//                                DocumentReference documentReference = fStore.collection("users")
//                                        .document(userID);
//                                Map<String, Object> user = new HashMap<>();
//                                user.put("name", nama);
//                                user.put("currentFinance", duit);
//                                documentReference.set(user)
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                Log.d(TAG, "onSuccess : user Profile is created for : " + userID);
//                                            }
//                                        });

                                String nama = edtNama.getText().toString();
                                String duitku = "0";
                                dbReference = fDatabase.getReference();
                                UserInformesyen userInformesyen = new UserInformesyen(nama, duitku);
                                dbReference.child(userID).setValue(userInformesyen);
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                                Toast.makeText(getApplicationContext(), "Register berhasil", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public void backLogin(View view){
        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(i);
    }

    public static boolean isValidEmail(CharSequence email){
        return (Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}