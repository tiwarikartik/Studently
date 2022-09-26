package com.kartik.homework.recyclerview.teacher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.kartik.homework.R;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.RecyclerViewHolder> {

    private final Interface recyclerViewInterface;

    Context context;
    ArrayList<Homework> homeworks;

    public Adapter(Interface recyclerViewInterface, Context context, ArrayList<Homework> homeworks) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.context = context;
        this.homeworks = homeworks;
    }

    @NonNull
    @Override
    public Adapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row_homework, parent, false);
        return new Adapter.RecyclerViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.title.setText(homeworks.get(position).getTitle());
        holder.author.setText(homeworks.get(position).getAuthor());
        holder.time.setText(homeworks.get(position).getTime());
        holder.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.anim_homework));
    }

    @Override
    public int getItemCount() {
        return homeworks.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        // Grabbing the views from our recycler view row layout file kinda like onCreate Method
        TextView title, author, time;
        CardView cardView;

        public RecyclerViewHolder(@NonNull View itemView, Interface recyclerViewInterface) {
            super(itemView);

            title = itemView.findViewById(R.id.missed_title);
            author = itemView.findViewById(R.id.missed_author);
            time = itemView.findViewById(R.id.missed_date);
            cardView = itemView.findViewById(R.id.cardView);

            cardView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onPreviewClick(position);
                        }
                    }
                }
            });
        }
    }
}
