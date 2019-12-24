package com.example.asitakipson.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asitakipson.Asi;
import com.example.asitakipson.CustomAdapter;
import com.example.asitakipson.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AsiList extends Fragment implements CustomAdapter.OnNoteListener {

    List<Asi> asiList = new ArrayList<>();
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    private RecyclerView recyclerView;
    private CustomAdapter mAdapter;

    DatabaseReference myRef2;
    public static String id2;

    String uID;
    FirebaseAuth mAuth;

    public AsiList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_asi_list, container, false);
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getUid();
        id2 = uID;
        myRef2 = FirebaseDatabase.getInstance().getReference(uID).child("Asilar");


        recyclerView = view.findViewById(R.id.recyclerview);

        mAdapter = new CustomAdapter(asiList,this );

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    private boolean updateAsi(String id, String name, String hastane,String tarih,String asiDurumu) {

        DatabaseReference dR = FirebaseDatabase.getInstance().getReference(id2).child("Asilar").child(id);
        Asi artist = new Asi(id, name, hastane,tarih,asiDurumu);
        dR.setValue(artist);


        Toast.makeText(getContext(), "Aşı Güncellendi.", Toast.LENGTH_LONG).show();
        return true;
    }
    private void showUpdateDeleteDialog( final String asiId, String asiName,String asiHastane,String asiTarih) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.EditTextAsiIdD);
        editTextName.setText(asiName);

        final   EditText spinnerGenre =  dialogView.findViewById(R.id.EditTextHastahaneD);
        spinnerGenre.setText(asiHastane);
        final TextView textViewtarihUpdate = dialogView.findViewById(R.id.textViewTarihUpdate);
        textViewtarihUpdate.setText(asiTarih);
        final Button buttonUpdate = dialogView.findViewById(R.id.buttonUpdateAsi);
        final  Button buttonDelete = dialogView.findViewById(R.id.buttonDeleteAsi);
        final  Button buttonTarih  = dialogView.findViewById(R.id.buttonTarihUpdate);

        buttonTarih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        textViewtarihUpdate.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                    }
                }, day,month,year);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        dialogBuilder.setTitle("Güncelleme-Silme Ekranı");
        final  AlertDialog b = dialogBuilder.create();
        b.show();


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String genre = spinnerGenre.getText().toString().trim();
                String tarih = textViewtarihUpdate.getText().toString().trim();

                String asiDurumu ;
                CheckBox checkBox = dialogView.findViewById(R.id.checkBox);
                if(checkBox.isChecked())  asiDurumu = "1"; else asiDurumu = "0";

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(genre)) {
                    updateAsi(asiId, name, genre,tarih,asiDurumu);
                    b.dismiss();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteAsi(asiId);
                b.dismiss();
            }
        });
    }
    private boolean deleteAsi(String id) {



        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(id2);
        Query applesQuery = ref.child("Asilar").child(id);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
                Toast.makeText(getContext(), "Aşı silindi.", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Hata!.\n"+databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

        return true;
    }
    @Override
    public void onStart() {
        super.onStart();
        //attaching value event listener
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                asiList.clear();
                //  Toast.makeText(AsiListele.this, "OndataChange", Toast.LENGTH_SHORT).show();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Asi asi1 = ds.getValue(Asi.class);
                        if (asi1 != null) {
                            asiList.add(asi1);
                            ds.getKey();
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
        mAdapter = new CustomAdapter(asiList, (CustomAdapter.OnNoteListener) this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onNoteClick(int position) {
        Asi asi = asiList.get(position);
        showUpdateDeleteDialog(asi.getAsiId(),asi.getAsiAdi(),asi.getHastahaneAdi(),asi.getAsiTarih());

    }
}
