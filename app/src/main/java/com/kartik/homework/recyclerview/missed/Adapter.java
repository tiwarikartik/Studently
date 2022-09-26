package com.kartik.homework.recyclerview.missed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kartik.homework.R;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.RecyclerViewHolder> {

    Context context;
    ArrayList<Missed> missedHomeworks = new ArrayList<>();
    int layout;

    public Adapter(Context context, ArrayList<Missed> missedHomeworks, int layout) {
        this.context = context;
        this.missedHomeworks = missedHomeworks;
        this.layout = layout;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout, parent, false);
        return new com.kartik.homework.recyclerview.missed.Adapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.title.setText(missedHomeworks.get(position).getTitle());
        holder.author.setText(missedHomeworks.get(position).getAuthor());
        holder.time.setText(missedHomeworks.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return missedHomeworks.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView title, author, time;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.missed_title);
            author = itemView.findViewById(R.id.missed_author);
            time = itemView.findViewById(R.id.missed_date);
        }
    }
}
