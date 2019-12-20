package com.example.asitakipson;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AsiEkle extends AppCompatActivity {


    DatabaseReference db;
    EditText editTextAsiAdı;
    EditText editTextHastahaneAdi;
    TextView textViewtarih;
    // EditText editTextAsiTarihi;
    CalendarView editTextAsiTarihi;
    final Context context = this;
    Button button,buttonTarih;
    Calendar calendar;
    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asi_ekle);

        Bundle extras = getIntent().getExtras();
        final String uID = extras.getString("sendUD");
        //  Toast.makeText(this, uID, Toast.LENGTH_SHORT).show();
       // System.out.println("başlangıç:" + uID);

        editTextAsiAdı = findViewById(R.id.EditTextAsiName);
        editTextHastahaneAdi = findViewById(R.id.EditTextHastahane);
        textViewtarih = findViewById(R.id.TextViewAsiTarihi);

   //     editTextAsiTarihi = findViewById(R.id.calendarView);

        button = findViewById(R.id.ButtonAsiKaydet);
        buttonTarih = findViewById(R.id.buttonTarih);

        buttonTarih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(AsiEkle.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        textViewtarih.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                    }
                }, day,month,year);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });




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
            String ad = editTextAsiAdı.getText().toString().trim();
            String hastane = editTextHastahaneAdi.getText().toString().trim();

            String tarih = textViewtarih.getText().toString();

            String id = db.push().getKey();

            Asi asi = new Asi(id, ad, hastane,tarih);

            db.child(id).setValue(asi).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                    editTextAsiAdı.setText("");
                                    editTextHastahaneAdi.setText("");
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


