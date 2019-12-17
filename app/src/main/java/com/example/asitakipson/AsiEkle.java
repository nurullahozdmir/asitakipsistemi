package com.example.asitakipson;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AsiEkle extends AppCompatActivity {


    DatabaseReference db;
    EditText editTextAsiAdı;
    EditText editTextHastahaneAdi;
    // EditText editTextAsiTarihi;
    CalendarView editTextAsiTarihi;
    final Context context = this;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asi_ekle);

        Bundle extras = getIntent().getExtras();
        final String uID = extras.getString("sendUD");
        //  Toast.makeText(this, uID, Toast.LENGTH_SHORT).show();
        System.out.println("başlangıç:" + uID);

        editTextAsiAdı = findViewById(R.id.EditTextAsiName);
        editTextHastahaneAdi = findViewById(R.id.EditTextHastahane);
        editTextAsiTarihi = findViewById(R.id.calendarView);
        // editTextAsiTarihi=findViewById(R.id.EditTextAsiTarihi);

        button = findViewById(R.id.ButtonAsiKaydet);

        if (uID != null) {
            db = FirebaseDatabase.getInstance().getReference(uID).child("Asilar");

        } else {
              Toast.makeText(this, "UID Boş Olamaz", Toast.LENGTH_SHORT).show();
            System.out.println("uıd boş olamaz");
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnAsiKaydet();
            }
        });

    }

    private void btnAsiKaydet() {

        if (!editTextAsiAdı.getText().toString().isEmpty() && !editTextHastahaneAdi.getText().toString().isEmpty() ) {

            Asi asi = new Asi();

            String id = db.push().getKey();
            System.out.println("başlangıç:" + id);

            asi.setAsiId(id);

            asi.setAsiAdi(editTextAsiAdı.getText().toString());
            asi.setHastahaneAdi(editTextHastahaneAdi.getText().toString());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            asi.setAsiTarih(sdf.format(new Date(editTextAsiTarihi.getDate())));
            db.push().setValue(asi).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Aşı Eklendi");

                    alertDialog
                            .setMessage("Geri dönmek istiyor musunuz?")
                            .setCancelable(false)
                            .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    AsiEkle.this.finish();
                                }
                            })
                            .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = alertDialog.create();

                    alert.show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "HATA! Aşı ekleme başarısız", Toast.LENGTH_SHORT).show();
                        }
                    });


        } else {
            Toast.makeText(getApplicationContext(), "Lütfen alanları doldurunuz.", Toast.LENGTH_SHORT).show();
        }

    }
}


