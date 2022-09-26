package com.kartik.homework.recyclerview.completed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kartik.homework.R;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<com.kartik.homework.recyclerview.completed.Adapter.RecyclerViewHolder> {

    private final com.kartik.homework.recyclerview.completed.Interface recyclerViewInterface;
    Context context;
    ArrayList<Completed> completedHomeworks = new ArrayList<>();
    int layout;

    public Adapter(com.kartik.homework.recyclerview.completed.Interface recyclerViewInterface, Context context, ArrayList<Completed> completedHomeworks, int layout) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.context = context;
        this.completedHomeworks = completedHomeworks;
        this.layout = layout;
    }

    @NonNull
    @Override
    public com.kartik.homework.recyclerview.completed.Adapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout, parent, false);
        return new com.kartik.homework.recyclerview.completed.Adapter.RecyclerViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull com.kartik.homework.recyclerview.completed.Adapter.RecyclerViewHolder holder, int position) {
        holder.title.setText(completedHomeworks.get(position).getTitle());
        holder.author.setText(completedHomeworks.get(position).getAuthor());
        holder.time.setText(completedHomeworks.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return completedHomeworks.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

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
                            recyclerViewInterface.onCompletedClick(position);
                        }
                    }
                }
            });
        }
    }
}

