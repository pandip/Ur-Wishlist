package com.app.dtsfinaltask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingActivity extends AppCompatActivity {

    EditText edtPassbaru, edtKonfpass;
    AppCompatCheckBox showPass;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        edtPassbaru = findViewById(R.id.edt_password_baru);
        edtKonfpass = findViewById(R.id.edt_konf_passbaru);

        firebaseAuth = FirebaseAuth.getInstance();

        showPass = findViewById(R.id.showpass_sett);
        showPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    edtKonfpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    edtPassbaru.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    edtPassbaru.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    edtKonfpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    public void gantiIn(View view){
        FirebaseUser user = firebaseAuth.getCurrentUser();

        String passbaru = edtPassbaru.getText().toString().trim();
        String konfpass = edtKonfpass.getText().toString().trim();

        if(passbaru.isEmpty() && konfpass.isEmpty()){
            edtPassbaru.setError("Field harus diisi!");
            edtKonfpass.setError("Field harus diisi!");
            return;
        } else if (passbaru.isEmpty()){
            edtPassbaru.setError("Field harus diisi!");
            return;
        } else if(passbaru.length() < 6){
            edtPassbaru.setError("Password minimal 6 karakter!");
            return;
        } else if(!konfpass.equals(passbaru)){
            edtKonfpass.setError("Password tidak sesuai!");
        } else {
            user.updatePassword(passbaru)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SettingActivity.this, "Password diubah", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                                finish();
                            } else {
                                Toast.makeText(SettingActivity.this, "" + task.getException(), Toast.LENGTH_LONG).show();
                                edtKonfpass.setText("");
                                edtPassbaru.setText("");
                            }
                        }
                    });
        }
    }

    public void hapusIn(View view){
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        AlertDialog.Builder alDi = new AlertDialog.Builder(SettingActivity.this);
        alDi.setTitle("Hapus Akun");
        alDi
                .setMessage("Yakin ingin menghapus akun?")
                .setCancelable(true)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(), "Akun berhasil dihapus", Toast.LENGTH_LONG)
                                                    .show();
                                            Intent i = new Intent(getApplicationContext(), Bridge.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Note: " + task.getException(), Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alDi.create();
        alDi.show();
    }
}