package com.example.asitakipson;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AsiListele extends AppCompatActivity implements CustomAdapter.OnNoteListener {
    List<Asi> asiList = new ArrayList<>();
    List<Asi> asiDelete;
    private RecyclerView recyclerView;
    private CustomAdapter mAdapter;
    DatabaseReference myRef2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asi_listele);

        Bundle extras = getIntent().getExtras();
        String uID = extras.getString("sendUD");

        myRef2 = FirebaseDatabase.getInstance().getReference(uID).child("Asilar");


        recyclerView = findViewById(R.id.recycler_view);

        mAdapter = new CustomAdapter(asiList,this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        asiListeleme();

        asiDelete = new ArrayList<>();

    }

    private void asiListeleme() {
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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
            public void onCancelled(@NonNull DatabaseError databaseError){}

        });
           mAdapter = new CustomAdapter(asiList,this);
            recyclerView.setAdapter(mAdapter);
    }

    private boolean updateAsi(String id, String name, String hastane,String tarih) {

        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Asilar").child(id);

        //updating Asi
        Asi asi = new Asi(id,name,hastane,tarih);
        dR.setValue(asi);
        Toast.makeText(getApplicationContext(), "Aşı Güncellendi.", Toast.LENGTH_LONG).show();
        return true;
    }
    private void showUpdateDeleteDialog(final String asiId, String asiName,String asiHastane,String asiTarih) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.EditTextAsiIdD);
        editTextName.setText(asiName);
        final EditText spinnerGenre =  dialogView.findViewById(R.id.EditTextHastahaneD);
        spinnerGenre.setText(asiHastane);
        final Button buttonUpdate = dialogView.findViewById(R.id.buttonUpdateAsi);
        final Button buttonDelete = dialogView.findViewById(R.id.buttonDeleteAsi);

        dialogBuilder.setTitle("Güncelleme-Silme Ekranı");
        final AlertDialog b = dialogBuilder.create();
        b.show();


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String genre = spinnerGenre.getText().toString().trim();
                String tarih = "29/12/2019";
                if (!TextUtils.isEmpty(name)) {
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

        //getting reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Asilar").child(id);
        dR.removeValue();
       Toast.makeText(getApplicationContext(), "İd="+id, Toast.LENGTH_LONG).show();

        //removing Asi
            dR.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
              //     Toast.makeText(getApplicationContext(), "Aşı Kaldırıldı.", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Hata!.", Toast.LENGTH_LONG).show();
                }
            });

        return true;
    }

    @Override
    public void onNoteClick(int position) {
        Asi asi = asiList.get(position);
        showUpdateDeleteDialog(asi.getAsiId(),asi.getAsiAdi(),asi.getHastahaneAdi(),asi.getAsiTarih());

    }
}
