package com.example.asitakipson.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asitakipson.Asi;
import com.example.asitakipson.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AsiAdd extends Fragment {

    DatabaseReference db;
    EditText editTextAsiAdı;
    EditText editTextHastahaneAdi;
    TextView textViewtarih;

    Button button, buttonTarih;
    Calendar calendar;
    DatePickerDialog datePickerDialog;

    String uID;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_asi_add, container, false);

        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getUid();

        editTextAsiAdı = view.findViewById(R.id.EditTextAsiName);
        editTextHastahaneAdi = view.findViewById(R.id.EditTextHastahane);
        textViewtarih = view.findViewById(R.id.TextViewAsiTarihi);

        button = view.findViewById(R.id.ButtonAsiKaydet);
        buttonTarih = view.findViewById(R.id.buttonTarih);

        buttonTarih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        textViewtarih.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, day, month, year);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });


        if (uID != null) {
            db = FirebaseDatabase.getInstance().getReference(uID).child("Asilar");

        } else {
            System.out.println("uıd boş olamaz");
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnAsiKaydet();
            }
        });
        return view;
    }

    private void btnAsiKaydet() {

        if (!editTextAsiAdı.getText().toString().isEmpty() && !editTextHastahaneAdi.getText().toString().isEmpty() ) {
            String ad = editTextAsiAdı.getText().toString().trim();
            String hastane = editTextHastahaneAdi.getText().toString().trim();

            String tarih = textViewtarih.getText().toString();

            String id = db.push().getKey();
            String asiDurumu = "0";
            Asi asi = new Asi(id, ad, hastane,tarih,asiDurumu);

            db.child(id).setValue(asi).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setTitle("Aşı Eklendi");

                    alertDialog
                            .setMessage("Aşı ekleme başarılı.")
                            .setCancelable(false)
                            .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    editTextAsiAdı.setText("");
                                    editTextHastahaneAdi.setText("");
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
                            Toast.makeText(getActivity(), "HATA! Aşı ekleme başarısız", Toast.LENGTH_SHORT).show();
                        }
                    });


        } else {
            Toast.makeText(getContext(), "Lütfen alanları doldurunuz.", Toast.LENGTH_SHORT).show();
        }
    }


}
