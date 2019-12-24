package com.example.asitakipson;


import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
        private List<Asi> asiList;
        private String[] mDataset;

        private OnNoteListener mOnNoteListener;

        public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public  TextView  asiName,hastaneAdı,asiTarih,asiDurumu;


            OnNoteListener onNoteListener;

            public MyViewHolder(View v, OnNoteListener onNoteListener) {
                super(v);

                this.onNoteListener = onNoteListener;

                 asiName = v.findViewById(R.id.asiName);
                hastaneAdı = v.findViewById(R.id.hastaneAdi);
                asiTarih = v.findViewById(R.id.asiTarih);
                asiDurumu = v.findViewById(R.id.durum);

                v.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                onNoteListener.onNoteClick(getAdapterPosition());
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public CustomAdapter(List<Asi> asiList, OnNoteListener onNoteListener) {
           this.asiList = asiList;
           this.mOnNoteListener = onNoteListener;
        }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_item_layout, parent, false);

        return new MyViewHolder(itemView,mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Asi asi = asiList.get(position);
        holder. asiName.setText("Aşı adı:"+asi.getAsiAdi());
        holder.hastaneAdı.setText("Hastane:"+asi.getHastahaneAdi());
        holder.asiTarih.setText("Tarih:"+asi.getAsiTarih());
        holder.asiDurumu.setText(asi.getAsiDurumu());


        if (asi.getAsiDurumu().equals("1")){
            holder.asiDurumu.setText("Aşı Yapıldı");
            holder.asiDurumu.setTextColor(Color.parseColor("#008000"));
        }
        else{
            holder.asiDurumu.setText("Aşı Yapılmadı");
            holder.asiDurumu.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    @Override
    public int getItemCount() {
        return asiList.size();
    }

    public interface OnNoteListener{
            void onNoteClick(int position);
    }
}