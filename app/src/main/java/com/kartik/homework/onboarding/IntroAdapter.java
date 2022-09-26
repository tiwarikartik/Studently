package com.kartik.homework.onboarding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.kartik.homework.R;

public class IntroAdapter extends PagerAdapter {

    Context context;

    int[] images = new int[]{
            R.drawable.bestapp,
            R.drawable.economical,
            R.drawable.notifications,
            R.drawable.lovedbystudents
    };

    int[] titles = new int[]{
            R.string.intro_title_one,
            R.string.intro_title_two,
            R.string.intro_title_three,
            R.string.intro_title_four
    };

    int[] descriptions = new int[]{
            R.string.intro_desc_one,
            R.string.intro_desc_two,
            R.string.intro_desc_three,
            R.string.intro_desc_four
    };

    public IntroAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.viewpager_item, container, false);

        ImageView image = view.findViewById(R.id.image);
        TextView title = view.findViewById(R.id.title);
        TextView description = view.findViewById(R.id.description);

        image.setImageResource(images[position]);
        title.setText(titles[position]);
        description.setText(descriptions[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((LinearLayout) object);
    }
}
