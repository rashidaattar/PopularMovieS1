package com.udacity.stage1.popularmovies1.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.udacity.stage1.popularmovies1.R;
import com.udacity.stage1.popularmovies1.activity.MovieActivity;
import com.udacity.stage1.popularmovies1.dto.Results;


public class MovieDetailFragment extends Fragment {

    private static final String ARG_PARAM1 = "result";
    private Results results;

    private OnFragmentInteractionListener mListener;
    TextView overview_text,movie_title,votes_text,movie_date;
    RatingBar voter_rating;
    ImageView poster_img;
    Toolbar toolbar;

    public static MovieDetailFragment newInstance(Results results) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, results);
        fragment.setArguments(args);
        return fragment;
    }

    public MovieDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            results = getArguments().getParcelable(ARG_PARAM1);
        }
        setRetainInstance(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v= inflater.inflate(R.layout.fragment_movie_detail, container, false);
        String base_url=getString(R.string.base_url_img)+ getPixelDensity();
        toolbar= (Toolbar) v.findViewById(R.id.fragment_toolbar);
        if(getActivity().findViewById(R.id.main_view).getTag().equals(getString(R.string.normal_tag)))
        {
            toolbar.setTitle("Movie Details");
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getActivity().getSupportFragmentManager().getBackStackEntryCount()>0)
                    {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }
            });
        }
        else
        {
            toolbar.setVisibility(View.GONE);
        }
        movie_title= (TextView) v.findViewById(R.id.movie_title);
        overview_text= (TextView) v.findViewById(R.id.overview_text);
        voter_rating= (RatingBar) v.findViewById(R.id.ratingBar);
        poster_img= (ImageView) v.findViewById(R.id.poster_img);
        votes_text= (TextView) v.findViewById(R.id.votes_text);
        movie_date= (TextView) v.findViewById(R.id.movie_date);

        String final_url=base_url+results.getPoster_path();
        Glide.with(getActivity()).load(final_url).into(poster_img);
        //to handle the delay while loading the image
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                movie_title.setText(results.getTitle());
                overview_text.setText(results.getOverview());
                if(Integer.parseInt(results.getVote_count())==1)
                {
                    votes_text.setText("( " + results.getVote_count() + "  " + "vote" + " ) ");
                }
                else
                {
                    votes_text.setText("( " + results.getVote_count() + "  " + "votes" + " ) ");
                }
                voter_rating.setRating(Float.parseFloat(results.getVote_average()));
                movie_date.setText((CharSequence) results.getRelease_date());
                MovieActivity.isLoading=false;
            }
        }, 2000);
        return v;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    public String getPixelDensity()
    {
        float density = getResources().getDisplayMetrics().density;

        if (density == 0.75f)
        {
           return getString(R.string.pixel_img_url_w92);
        }
        else if (density >= 1.0f && density < 1.5f)
        {
            return getString(R.string.pixel_img_url_w92);
        }
        else if (density == 1.5f)
        {
            // HDPI
            return getString(R.string.pixel_img_url_w92);
        }
        else if (density > 1.5f && density <= 2.0f)
        {
            // XHDPI
            return getString(R.string.pixel_img_url_w185);
        }
        else if (density > 2.0f && density <= 3.0f)
        {
            // XXHDPI
            return getString(R.string.pixel_img_url_w185);
        }
        else
        {
            // XXXHDPI
            return getString(R.string.pixel_img_url_w500);
        }
        //return null;
    }
}
