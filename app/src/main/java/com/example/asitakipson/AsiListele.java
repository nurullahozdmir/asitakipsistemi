package com.example.asitakipson;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AsiListele extends AppCompatActivity implements CustomAdapter.OnNoteListener {
    List<Asi> asiList = new ArrayList<>();
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    private RecyclerView recyclerView;
    private CustomAdapter mAdapter;
    DatabaseReference myRef2;
   // DatabaseReference dR;
    public static String id2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asi_listele);

        Bundle extras = getIntent().getExtras();
        String uID = extras.getString("sendUD");
        id2 = uID;

        myRef2 = FirebaseDatabase.getInstance().getReference(uID).child("Asilar");


        recyclerView = findViewById(R.id.recyclerview);

        mAdapter = new CustomAdapter(asiList,this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }
     private boolean updateAsi(String id, String name, String hastane,String tarih) {

        DatabaseReference dR = FirebaseDatabase.getInstance().getReference(id2).child("Asilar").child(id);
         Asi artist = new Asi(id, name, hastane,tarih);
         dR.setValue(artist);


        Toast.makeText(getApplicationContext(), "Aşı Güncellendi.", Toast.LENGTH_LONG).show();
        return true;
    }
    private void showUpdateDeleteDialog( final String asiId, String asiName,String asiHastane,String asiTarih) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final   EditText editTextName = dialogView.findViewById(R.id.EditTextAsiIdD);
        //editTextName.setHint(asiName);

       final   EditText spinnerGenre =  dialogView.findViewById(R.id.EditTextHastahaneD);
        //spinnerGenre.setHint(asiHastane);
        final TextView textViewtarihUpdate = dialogView.findViewById(R.id.textViewTarihUpdate);

       final  Button buttonUpdate = dialogView.findViewById(R.id.buttonUpdateAsi);
       final  Button buttonDelete = dialogView.findViewById(R.id.buttonDeleteAsi);
       final  Button buttonTarih  = dialogView.findViewById(R.id.buttonTarihUpdate);

        buttonTarih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(AsiListele.this, new DatePickerDialog.OnDateSetListener() {
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
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(genre)) {
                    updateAsi(asiId, name, genre,tarih);
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
                Toast.makeText(getApplicationContext(), "Aşı silindi.", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Hata!.\n"+databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

        return true;
    }

    @Override
    public void onNoteClick(int position) {
        Asi asi = asiList.get(position);
        showUpdateDeleteDialog(asi.getAsiId(),asi.getAsiAdi(),asi.getHastahaneAdi(),asi.getAsiTarih());

    }
    @Override
    protected void onStart() {
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
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
        mAdapter = new CustomAdapter(asiList, this);
        recyclerView.setAdapter(mAdapter);
    }
}
