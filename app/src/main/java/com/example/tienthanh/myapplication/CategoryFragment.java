package com.example.tienthanh.myapplication;


import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;


public class CategoryFragment extends Fragment implements CategoryAdapter.Listener{

    private ArrayList<Category> categoryList;
    private CategoryAdapter adapter;
    private RecyclerView recyclerView;

    public CategoryFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView = v.findViewById(R.id.recycler_view);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetCategory().execute();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onClick(int position) {
        Category category = categoryList.get(position);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = null;
        if (fm != null) {
            ft = fm.beginTransaction();
            Fragment fragment = new SongListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("CATEGORY_NAME", category.getCategoryName());
            bundle.putString("CATEGORY_CID", category.getCID());
            fragment.setArguments(bundle);
            ft.replace(R.id.container, fragment);
            ft.commit();
        }

    }

    private class GetCategory extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            categoryList = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(MainActivity.CATEGORY_URL);


            if (jsonStr != null) {

                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray categories = jsonObj.getJSONObject("content").getJSONArray("data");

                    for (int i = 0; i < categories.length(); i++) {
                        JSONObject c = categories.getJSONObject(i);

                        String CID = c.getString("CID");
                        String name = c.getString("categoryName");
                        String thumbnail = c.getString("categoryThumbnail");
                        int countItem = c.getInt("countItem");
                        String key = c.getString("categoryKey");
                        boolean active = c.getBoolean("isActive");

                        Category category = new Category(CID, name, thumbnail, key, countItem, active);
                        categoryList.add(category);

                    }

                } catch (JSONException e) {

                }

            } else {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            new StorageUtil(getContext()).storeCategory(categoryList);
            adapter = new CategoryAdapter(getContext(), categoryList);

            adapter.setListener(CategoryFragment.this);
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
            recyclerView.setLayoutManager(staggeredGridLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);

        }
    }



}
