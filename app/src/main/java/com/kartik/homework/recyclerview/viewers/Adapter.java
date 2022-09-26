package com.kartik.homework.recyclerview.viewers;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kartik.homework.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.RecyclerViewHolder> {

    Context context;
    ArrayList<Viewer> viewers;
    private final Interface recyclerViewInterface;

    public Adapter(Context context, Interface recyclerViewInterface, ArrayList<Viewer> viewers) {
        this.context = context;
        this.viewers = viewers;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row_viewer, parent, false);
        return new Adapter.RecyclerViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        String name = viewers.get(position).getName();
        holder.name.setText(name);
        holder.email.setText(viewers.get(position).getEmailId());

        boolean notified = viewers.get(position).isNotified();
        boolean uploaded = viewers.get(position).isFileUploaded();
        String link = viewers.get(position).getHomeworkLink();

        if (uploaded) {
            if (link != null) {
                SharedPreferences sf = context.getSharedPreferences("LINKS", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sf.edit();
                editor.putString("HomeworkLink" + position, link);
                editor.apply();
                System.out.println(link);
                holder.attachment.setClickable(true);
                holder.attachment.setImageResource(R.drawable.ic_attachment);
            }
        }
        if (notified) {
            holder.seen.setImageResource(R.drawable.ic_double_check_seen);
        } else {
            holder.seen.setImageResource(R.drawable.ic_double_check_not_received);
        }

        holder.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.anim_viewer));
        Picasso.get().load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT_EMdQsFpPIZnHbnfNnuDSjWW3BGMrNHQCiBLXqICLEg&s").into(holder.profile);
    }

    @Override
    public int getItemCount() {
        return viewers.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView name, email;
        ImageView seen, profile;
        ImageButton attachment;
        CardView cardView;

        public RecyclerViewHolder(@NonNull View itemView, Interface recyclerViewInterface) {
            super(itemView);

            name = itemView.findViewById(R.id.missed_title);
            email = itemView.findViewById(R.id.missed_date);
            seen = itemView.findViewById(R.id.imageView2);
            attachment = itemView.findViewById(R.id.imageButton2);
            profile = itemView.findViewById(R.id.profileViewer);
            cardView = itemView.findViewById(R.id.cardView);

            attachment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onAttachmentClick(position);
                        }
                    }
                }
            });
        }
    }
}
