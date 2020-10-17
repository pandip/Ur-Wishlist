package com.app.dtsfinaltask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dtsfinaltask.helper.UserInformesyen;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference databaseReference;
    private TextView namaUser, duitSekarang;
    private ImageView profPic;

    String duitKu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        profPic = findViewById(R.id.profilepic_home);
        namaUser = findViewById(R.id.namauser_home);
        duitSekarang = findViewById(R.id.duit_sekarang);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

//        firebaseFirestore.collection("users")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
//                                String nama = documentSnapshot.getString("nama");
//                                String duit = documentSnapshot.getString("currentFinance");
//
//                                Locale localeID = new Locale("in", "ID");
//                                NumberFormat formatIDR = NumberFormat.getCurrencyInstance(localeID);
//                                duitKu = duit;
//                                namaUser.setText(nama);
//                                duitSekarang.setText(formatIDR.format((double)Double.parseDouble(duitKu)));
//                            }
//                        }
//                    }
//                });

        DatabaseReference databaseReference = firebaseDatabase.getReference(auth.getUid());
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(auth.getUid()).child("Images").child("Profile Picture").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).fit().centerInside().into(profPic);
                    }
                });

        if (auth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        final FirebaseUser user = auth.getCurrentUser();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserInformesyen userInformesyen = snapshot.getValue(UserInformesyen.class);
                Locale localeID = new Locale("in", "ID");
                NumberFormat formatIDR = NumberFormat.getCurrencyInstance(localeID);
                duitKu = userInformesyen.getCurrentFinance();
                namaUser.setText(userInformesyen.getNama());
                duitSekarang.setText(formatIDR.format((double)Double.parseDouble(duitKu)));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getCode(), Toast.LENGTH_LONG).show();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigasi);

        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.funct_logout :
                        AlertDialog.Builder alDi = new AlertDialog.Builder(HomeActivity.this);
                        alDi.setTitle("Logout");
                        alDi
                                .setMessage("Yakin ingin logout?")
                                .setCancelable(true)
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        auth.signOut();
                                        Intent i = new Intent(getApplicationContext(), Bridge.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
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

                    case R.id.nav_home :
                        return true;

                    case R.id.nav_profile :
                        Intent iSet = new Intent(HomeActivity.this, SettingActivity.class);
                        startActivity(iSet);
                        return true;
                }
                return true;
            }
        });
    }

    public void gantiDuit(View view){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_edit_duit, null);
        final EditText gantiDuit = alertLayout.findViewById(R.id.gantiduit);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Uang yang Dimiliki Sekarang");
        alert.setView(alertLayout);
        alert.setCancelable(true);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String duit = gantiDuit.getText().toString().trim();
                String nama = namaUser.getText().toString();
                UserInformesyen userInformesyen = new UserInformesyen(duit, nama);
                FirebaseUser user = auth.getCurrentUser();
                databaseReference.child(user.getUid()).setValue(userInformesyen);
                databaseReference.child(user.getUid()).setValue(userInformesyen);
                gantiDuit.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

//    public void gantiNama(View view){
//        LayoutInflater inflater = getLayoutInflater();
//        View alertLayout = inflater.inflate(R.layout.layout_edit_nama, null);
//        final EditText gantiNama = alertLayout.findViewById(R.id.gantinama);
//        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        alert.setTitle("Ubah Nama");
//        alert.setView(alertLayout);
//        alert.setCancelable(true);
//        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//
//        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
////                String duit = String.valueOf(duitSekarang.getText().toString().trim());
//                String nama = gantiNama.getText().toString();
//                UserInformesyen userInformesyen = new UserInformesyen(nama);
//                FirebaseUser user = auth.getCurrentUser();
//                databaseReference.child(user.getUid()).setValue(userInformesyen);
//                databaseReference.child(user.getUid()).setValue(userInformesyen);
//                gantiNama.onEditorAction(EditorInfo.IME_ACTION_DONE);
//            }
//        });
//        AlertDialog dialog = alert.create();
//        dialog.show();
//    }
}