package com.udacity.stage1.popularmovies1.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.udacity.stage1.popularmovies1.fragment.MovieDetailFragment;
import com.udacity.stage1.popularmovies1.R;
import com.udacity.stage1.popularmovies1.adapter.MovieAdapter;
import com.udacity.stage1.popularmovies1.dto.MoviesDbDto;
import com.udacity.stage1.popularmovies1.dto.Results;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MovieActivity extends AppCompatActivity implements MovieDetailFragment.OnFragmentInteractionListener{
    private static final String TAG="MovieActivity";
    private GridView gridView;
    private MoviesDbDto moviesDbDto = new MoviesDbDto();
    private MovieAdapter movieAdapter;
    public static ProgressDialog dialog;
    private AlertDialog alertDialog;
    public static final String FRAGMENT_TAG="movie_fragment";
    private static MyAsync myAsync;
    static String sort_by_param;
    static int page_number;
    public static Boolean isLoading=true;
    static int mLastFirstVisibleItem = 0;
    private static ArrayList<Results> result_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        Toolbar toolbar= (Toolbar) findViewById(R.id.movie_toolbar);
        toolbar.setTitle(getString(R.string.movie_title));
        setSupportActionBar(toolbar);
        gridView= (GridView) findViewById(R.id.gridview);
        dialog=new ProgressDialog(this);
        dialog.setMessage(getString(R.string.progress_bar_msg));
        dialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        result_list=new ArrayList<>();
        Log.i(TAG,"layout used: "+findViewById(R.id.main_view).getTag());
        if(savedInstanceState!=null)
        {
            if(savedInstanceState.getString(getString(R.string.sort_by_key_param))!=null)
            {
                result_list.clear();
                page_number=1;
                sort_by_param= savedInstanceState.getString(getString(R.string.sort_by_key_param));
                callAsyncTask();

            }
            else
            {
                //sort by popularity default option
                result_list.clear();
                page_number=1;
                sort_by_param=getString(R.string.popularity_url);
                callAsyncTask();

            }
        }
        else
        {
            //sort by popularity default option
            result_list.clear();
            page_number=1;
            sort_by_param=getString(R.string.popularity_url);
            callAsyncTask();
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                gridView.setClickable(false);
                Fragment movieFragment = MovieDetailFragment.newInstance(result_list.get(position));
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, movieFragment).addToBackStack(FRAGMENT_TAG).commit();


            }
        });
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(isLoading)
                isLoading=false;

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if((!isLoading) && (page_number<5) && moviesDbDto!=null &&
                        (moviesDbDto.getPage()!=null) &&
                        (page_number!=(Integer.parseInt(moviesDbDto.getPage())+1)))
                {
                     // what is the bottom iten that is visible
                    int lastInScreen = firstVisibleItem + visibleItemCount;
                     //is the bottom item visible & not loading more already ?
                    // Load more !
                    if (lastInScreen >= totalItemCount - 1)
                    {
                        mLastFirstVisibleItem=firstVisibleItem;
                        page_number=page_number+1;
                        callAsyncTask();
                    }

                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie, menu);
        if(sort_by_param!=null)
        {
            if(sort_by_param.equals(getString(R.string.popularity_url)))
            {
                MenuItem menuItem=menu.findItem(R.id.sort_popularity);
                menuItem.setChecked(true);
            }
            else if(sort_by_param.equals(getString(R.string.rating_url)))
            {
                MenuItem menuItem=menu.findItem(R.id.sort_rating);
                menuItem.setChecked(true);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {switch(item.getItemId())
    {

        case R.id.sort_popularity:
            if(!item.isChecked()) {
                item.setChecked(true);
                result_list.clear();
                page_number=1;
                sort_by_param=getString(R.string.popularity_url);
                callAsyncTask();

            }
            return true;
        case R.id.sort_rating:
            if(!item.isChecked()) {
                item.setChecked(true);
                result_list.clear();
                page_number=1;
                sort_by_param=getString(R.string.rating_url);
                callAsyncTask();

            }
            return true;
    }

        return super.onOptionsItemSelected(item);
    }

    private void callAsyncTask() {
        //to prevet out of memory
        Glide.get(this).clearMemory();
        myAsync=new MyAsync();
        myAsync.execute();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myAsync!=null)
            myAsync.cancel(true);
        myAsync = null;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class MyAsync extends AsyncTask<Void, Void, Void>
    {

        public MyAsync() {

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            gridView.setClickable(true);
            movieAdapter=new MovieAdapter(getApplicationContext(),result_list);
            gridView.invalidate();
            gridView.setAdapter(movieAdapter);
            gridView.setSelection(mLastFirstVisibleItem);
            gridView.smoothScrollToPosition(mLastFirstVisibleItem);
            movieAdapter.notifyDataSetChanged();
            dialog.dismiss();

        }

        @Override
        protected Void doInBackground(Void... params) {

            String base_uri=getString(R.string.base_url_tmdb);
            String key=getString(R.string.movie_api_key);
            String sort_by=sort_by_param;
            String page_param=getString(R.string.page_param);
            int page_value=page_number;
            String api_param=getString(R.string.api_key_param);
            String sort_by_param=getString(R.string.sort_by_key_param);
            HttpURLConnection httpURLConnection=null;
            BufferedReader bufferedReader=null;
            StringBuilder responseSB = new StringBuilder();
            try
            {
                Uri uri=Uri.parse(base_uri).buildUpon().appendQueryParameter(sort_by_param, sort_by)
                        .appendQueryParameter(api_param, key).
                                appendQueryParameter(page_param, String.valueOf(page_value)).build();
                URL url=new URL(uri.toString());
                Log.d(TAG,"URL :"+uri.toString());
                httpURLConnection= (HttpURLConnection) url.openConnection();
                bufferedReader=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line;
                while ( (line = bufferedReader.readLine()) != null)
                    responseSB.append(line);
                String jsonResponnse=responseSB.toString();
                Log.d(TAG," JSON is:---------------"+jsonResponnse);
                moviesDbDto=new Gson().fromJson(jsonResponnse,MoviesDbDto.class);
                result_list.addAll(Arrays.asList(moviesDbDto.getResults()));

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                {
                    if(httpURLConnection!=null)
                        httpURLConnection.disconnect();
                    try {
                        if(bufferedReader!=null)
                            bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {


        if(getSupportFragmentManager().getBackStackEntryCount()>0)
        {
            getSupportFragmentManager().popBackStack();
        }
        else {
            if (alertDialog != null && alertDialog.isShowing()) {

            } else {
                alertDialog = new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false).setMessage(getString(R.string.exit_app)).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent a = new Intent(Intent.ACTION_MAIN);
                                a.addCategory(Intent.CATEGORY_HOME);
                                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(a);
                                alertDialog.dismiss();
                                int pid = android.os.Process.myPid();
                                android.os.Process.killProcess(pid);
                                System.exit(0);
                                finish();
                            }
                        }).setNegativeButton(android.R.string.no, null).show();
            }
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

            if(sort_by_param!=null)
            outState.putString(getString(R.string.sort_by_key_param),sort_by_param);

        super.onSaveInstanceState(outState);
    }
}
