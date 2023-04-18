package com.example.weather;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;


public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.MyViewHolder> {
    Context context;
    List<HourlyForeModel> HourlyList;
    int selectedPos = RecyclerView.NO_POSITION;


    public HourlyAdapter(Context context, List<HourlyForeModel> hourlyList) {
        this.context = context;
        HourlyList = hourlyList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.rec_hourly_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HourlyForeModel hourModel = HourlyList.get(position);
        holder.recTime.setText(hourModel.getmTime());
        holder.recTemp.setText(hourModel.getmTemp()+" C");

        Glide.with(context).load(hourModel.getImg()).into(holder.recImg);
        holder.itemView.setSelected(selectedPos==position);








    }

    @Override
    public int getItemCount() {
        return 24;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView recImg;
        TextView recTime, recTemp;
        CardView cardLay;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recImg=itemView.findViewById(R.id.recImg);
            recTime=itemView.findViewById(R.id.recTime);
            recTemp=itemView.findViewById(R.id.recTemp);
            cardLay=itemView.findViewById(R.id.cardLay);


        }
    }
}
