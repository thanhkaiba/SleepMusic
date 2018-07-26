package com.example.tienthanh.myapplication;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private ArrayList<Category> categoryList;
    private Context context;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private Listener listener;

    public interface Listener {
        void onClick(int position);
    }

    public CategoryAdapter(Context context, ArrayList<Category> categoryList) {
        this.categoryList = categoryList;
        this.context = context;
    }


    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View cv = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_view, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Category category = categoryList.get(i);
        viewHolder.bind(category, i);

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private ImageView categoryImage;
        private TextView categoryName;
        private TextView categorySongCount;

        public ViewHolder(View v) {
            super(v);
            this.cardView = v.findViewById(R.id.card_view);
            categoryImage = v.findViewById(R.id.category_image);
            categoryName = v.findViewById(R.id.category_name);
            categorySongCount = v.findViewById(R.id.count);
        }

        public void bind(Category category, final int position) {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(position);
                }
            });
            categoryName.setText(category.getCategoryName());
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/BLACK Personal Use.ttf");
            categoryName.setTypeface(tf);
            categorySongCount.setText("" + category.getCountItem() + " songs");
            categoryImage.setImageDrawable(category.getCategoryThumbnail());
        }


    }
}
