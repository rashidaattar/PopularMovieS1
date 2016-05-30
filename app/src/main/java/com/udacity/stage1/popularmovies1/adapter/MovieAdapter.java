package com.udacity.stage1.popularmovies1.adapter;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.udacity.stage1.popularmovies1.R;
import com.udacity.stage1.popularmovies1.activity.MovieActivity;
import com.udacity.stage1.popularmovies1.dto.Results;

import java.util.ArrayList;

/**
 * Created by Rashida on 2/3/2016.
 */
public class MovieAdapter extends BaseAdapter{
    Context context;
    ArrayList<Results> resultsArrayList=new ArrayList<>();
    LayoutInflater inflater;
    public MovieAdapter(Context context, ArrayList<Results> resultsArrayList)
    {
        this.context=context;
        this.resultsArrayList=resultsArrayList;
        this.inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        if(resultsArrayList.size()>0 && resultsArrayList!=null)
            return resultsArrayList.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return resultsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=convertView;
        v=inflater.inflate(R.layout.movie_adapter_view,null);
        MoviePlaceholder moviePlaceholder=new MoviePlaceholder();
        moviePlaceholder.imageView= (ImageView) v.findViewById(R.id.image_view);
        String base_url=context.getString(R.string.base_url_img)+ getPixelDensity();
        String final_url=base_url+resultsArrayList.get(position).getPoster_path();
        Glide.with(context).load(final_url).into(moviePlaceholder.imageView);
       if(MovieActivity.dialog.isShowing())
       {
           MovieActivity.dialog.dismiss();
           MovieActivity.isLoading=true;

       }

        return v;
    }
    public class MoviePlaceholder
    {
        public ImageView imageView;
    }

    public String getPixelDensity()
    {
        float density = context.getResources().getDisplayMetrics().density;

        if (density == 0.75f)
        {
            return context.getString(R.string.pixel_img_url_w92);
        }
        else if (density >= 1.0f && density < 1.5f)
        {
            return context.getString(R.string.pixel_img_url_w154);
        }
        else if (density == 1.5f)
        {
            // HDPI
            return context.getString(R.string.pixel_img_url_w185);
        }
        else if (density > 1.5f && density <= 2.0f)
        {
            // XHDPI
            return context.getString(R.string.pixel_img_url_w342);
        }
        else if (density > 2.0f && density <= 3.0f)
        {
            // XXHDPI
            return context.getString(R.string.pixel_img_url_w500);
        }
        else
        {
            // XXXHDPI
            return context.getString(R.string.pixel_img_url_w780);
        }
        //return null;
    }

}
