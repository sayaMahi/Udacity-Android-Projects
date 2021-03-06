<<<<<<< HEAD
package www.androidcitizen.com.popularmoviesone.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

import www.androidcitizen.com.popularmoviesone.R;
import www.androidcitizen.com.popularmoviesone.data.adapter.MovieAdapter;
import www.androidcitizen.com.popularmoviesone.data.database.FavContract;
import www.androidcitizen.com.popularmoviesone.databinding.ActivityMainBinding;
import www.androidcitizen.com.popularmoviesone.data.model.Movie;
import www.androidcitizen.com.popularmoviesone.data.model.MovieDetails;
import www.androidcitizen.com.popularmoviesone.pagination.EndlessScrollListener;
import www.androidcitizen.com.popularmoviesone.data.Loader.MovieLoader;

import static www.androidcitizen.com.popularmoviesone.config.Constants.*;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieClickListener,
        LoaderManager.LoaderCallbacks,
        PopupMenu.OnMenuItemClickListener {

    private ActivityMainBinding mainBinding;

    private static MovieAdapter adapter;
    private EndlessScrollListener scrollListener;

    private static int TOTAL_PAGES = 1;

    private static boolean IF_ADAPTER_IS_SET = false;

    private static int MOVIE_FETCH_INDEX = NOW_PLAYING_MOVIES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Stetho.initializeWithDefaults(this);

        IF_ADAPTER_IS_SET = false;

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setupRecycleView();

        if (null == savedInstanceState) {

            getSupportActionBar().setTitle(R.string.now_playing);
            fetchMovies(NOW_PLAYING_MOVIES);

        } else {

            getLoaderManager().destroyLoader(LOADING_ID_MOVIE_SERVER);
            getLoaderManager().destroyLoader(LOADING_ID_MOVIE_DATABASE);

            List<MovieDetails> movieDetails = savedInstanceState.getParcelableArrayList(INSTANCE_STATE_LIST_MOVIES);
            MOVIE_FETCH_INDEX = savedInstanceState.getInt(INSTANCE_STATE_MOVIE_TYPE_INDEX);
            getSupportActionBar().setTitle(savedInstanceState.getString(INSTANCE_STATE_TOOLBAR_MOVIE_TITLE));
            adapter.newData(movieDetails);

            Parcelable state = savedInstanceState.getParcelable(INSTANCE_STATE_LAYOUTMANAGER);
            mainBinding.rvMovieList.getLayoutManager().onRestoreInstanceState(state);
        }
    }

    private void fetchMovies(int iIndex) {

        if(FAVOURITE_MOVIES_INDEX == iIndex) {
            Bundle bundle = new Bundle();
            bundle.putInt(KEY_FAV_MOVIE_ID, FAV_MOVIE_READ);

            getLoaderManager().restartLoader(LOADING_ID_MOVIE_DATABASE, bundle, this);

        } else if ( (NOW_PLAYING_MOVIES == iIndex)      ||
                    (TOPRATED_MOVIES_INDEX == iIndex) ||
                    (POPULAR_MOVIES_INDEX == iIndex)) {

            Bundle bundle = new Bundle();
            bundle.putInt(KEY_MOVIE_SERVICE_LOADING, iIndex);

            getLoaderManager().restartLoader(LOADING_ID_MOVIE_SERVER, bundle, this);

        }
    }

    private void setupRecycleView(){
        int spanCount = getColumnValue(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);

        scrollListener = new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                PAGE_NUM = page;
                if(page < TOTAL_PAGES){
                    fetchMovies(MOVIE_FETCH_INDEX);
                }
            }
        };

        mainBinding.rvMovieList.setHasFixedSize(true);
        mainBinding.rvMovieList.setLayoutManager(layoutManager);
        mainBinding.rvMovieList.setItemAnimator(new DefaultItemAnimator());
        mainBinding.rvMovieList.addOnScrollListener(scrollListener);

        adapter = new MovieAdapter(this);

        mainBinding.rvMovieList.setAdapter(adapter);

        IF_ADAPTER_IS_SET = true;
    }

    private static int getColumnValue(Context context){
        if(context.getResources().getBoolean(R.bool.is_portrait)){
            return 2;
        } else {
            return 4;
        }
    }

    @Override
    public void onMovieItemClick(int clickedItemIndex, MovieDetails movieDetails) {

            Intent movieDetailActivity = new Intent(this, MovieDetailActivity.class);
            movieDetailActivity.putExtra(KEY_MOVIE_DETAILS_DATA, movieDetails);
            startActivity(movieDetailActivity);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.ic_filter:
                View view = findViewById(R.id.ic_filter);
                PopupMenu popupMenu = new PopupMenu(this, view);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.inflate(R.menu.menu_actions);
                popupMenu.getMenu().getItem(MOVIE_FETCH_INDEX).setChecked(true);
                popupMenu.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        enableMenuItemAndFetchMovies(item);

        return true;
    }

    private void enableMenuItemAndFetchMovies(MenuItem item) {
        enableMenuItem(item);
        clearAndFetch();
    }

    private void enableMenuItem(MenuItem item) {

        switch (item.getItemId()){
            case R.id.all:
                getSupportActionBar().setTitle(R.string.now_playing);
                MOVIE_FETCH_INDEX = NOW_PLAYING_MOVIES;
                break;
            case R.id.toprated:
                getSupportActionBar().setTitle(R.string.top_rated);
                MOVIE_FETCH_INDEX = TOPRATED_MOVIES_INDEX;
                break;
            case R.id.popular:
                getSupportActionBar().setTitle(R.string.popular);
                MOVIE_FETCH_INDEX = POPULAR_MOVIES_INDEX;
                break;
            case R.id.favourite:
                getSupportActionBar().setTitle(R.string.favorites);
                MOVIE_FETCH_INDEX = FAVOURITE_MOVIES_INDEX;
                break;
        }
    }

    private void clearAndFetch(){
        clearList();
        PAGE_NUM = 1;
        fetchMovies(MOVIE_FETCH_INDEX);

    }

    private void clearList() {
        adapter.clearAll();
        scrollListener.resetState();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle bundleArgs) {

        switch (id) {

            case LOADING_ID_MOVIE_SERVER:

                return new MovieLoader(this, bundleArgs);

            case LOADING_ID_MOVIE_DATABASE:

                int iDbAction = bundleArgs.getInt(KEY_FAV_MOVIE_ID);

                switch (iDbAction) {

                    case FAV_MOVIE_NULL:
                            return null;

                    case FAV_MOVIE_READ:
                            String sortOrder = FavContract.FavMovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
                            return new CursorLoader(this,
                            FavContract.FavMovieEntry.CONTENT_URI,
                            PROJECTION,
                            null, null, sortOrder);
                }
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

        if(null != data) {
            int id = loader.getId();

            switch (id) {
                case LOADING_ID_MOVIE_SERVER:
                    setupAdapterServerData(data);
                    break;

                case LOADING_ID_MOVIE_DATABASE:
                    setupAdapterDatabaseData(data);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private void setupAdapterServerData(Object object){

            Movie movieObj = (Movie) object;
            PAGE_NUM = movieObj.getPage();
            TOTAL_PAGES = movieObj.getTotalPages();
            TOTAL_ITEMS_COUNT = movieObj.getTotalResults();
            adapter.newData(movieObj.getMovieDetails());
    }

    private void setupAdapterDatabaseData(Object object){

            if (MOVIE_FETCH_INDEX == FAVOURITE_MOVIES_INDEX) {
                //Upon every Fav Set, this condition prevents page refresh.

                adapter.clearAll();

                Cursor cursor = (Cursor) object;
                if (cursor.getCount() > 0) {
                    adapter.newCursorData(cursor);
                }
            }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

            if(IF_ADAPTER_IS_SET) {
                if (0 < adapter.getItemCount()) {
                    ArrayList<MovieDetails> movieDetailsSavedStateList = new ArrayList<>(adapter.getMovies());
                    outState.putParcelableArrayList(INSTANCE_STATE_LIST_MOVIES, movieDetailsSavedStateList);
                    outState.putInt(INSTANCE_STATE_MOVIE_TYPE_INDEX, MOVIE_FETCH_INDEX);
                    outState.putString(INSTANCE_STATE_TOOLBAR_MOVIE_TITLE, getSupportActionBar().getTitle().toString());

                    Parcelable listState = mainBinding.rvMovieList.getLayoutManager().onSaveInstanceState();
                    outState.putParcelable(INSTANCE_STATE_LAYOUTMANAGER, listState);
                }
            }
    }

}
||||||| merged common ancestors
=======
package www.androidcitizen.com.popularmoviesone.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

import www.androidcitizen.com.popularmoviesone.R;
import www.androidcitizen.com.popularmoviesone.config.GlobalRef;
import www.androidcitizen.com.popularmoviesone.data.adapter.MovieAdapter;
import www.androidcitizen.com.popularmoviesone.data.database.FavContract;
import www.androidcitizen.com.popularmoviesone.databinding.ActivityMainBinding;
import www.androidcitizen.com.popularmoviesone.data.model.Movie;
import www.androidcitizen.com.popularmoviesone.data.model.MovieDetails;
import www.androidcitizen.com.popularmoviesone.pagination.EndlessScrollListener;
import www.androidcitizen.com.popularmoviesone.data.Loader.MovieLoader;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieClickListener,
        LoaderManager.LoaderCallbacks,
        PopupMenu.OnMenuItemClickListener {

    private ActivityMainBinding mainBinding;

    private static MovieAdapter adapter;
    private EndlessScrollListener scrollListener;

    private static int TOTAL_PAGES = 1;

    private static boolean IF_ADAPTER_IS_SET = false;

    private static int MOVIE_FETCH_INDEX = GlobalRef.NOW_PLAYING_MOVIES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Stetho.initializeWithDefaults(this);

        IF_ADAPTER_IS_SET = false;

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setupRecycleView();

        if (null == savedInstanceState) {

            getSupportActionBar().setTitle(R.string.now_playing);
            fetchMovies(GlobalRef.NOW_PLAYING_MOVIES);

        } else {

            List<MovieDetails> movieDetails = savedInstanceState.getParcelableArrayList(GlobalRef.INSTANCE_STATE_LIST_MOVIES);
            MOVIE_FETCH_INDEX = savedInstanceState.getInt(GlobalRef.INSTANCE_STATE_MOVIE_TYPE_INDEX);
            getSupportActionBar().setTitle(savedInstanceState.getString(GlobalRef.INSTANCE_STATE_TOOLBAR_MOVIE_TITLE));
            adapter.newData(movieDetails);

            Parcelable state = savedInstanceState.getParcelable(GlobalRef.INSTANCE_STATE_LAYOUTMANAGER);
            mainBinding.rvMovieList.getLayoutManager().onRestoreInstanceState(state);
        }
    }

    private void fetchMovies(int iIndex) {

        if(GlobalRef.FAVOURITE_MOVIES_INDEX == iIndex) {
            Bundle bundle = new Bundle();
            bundle.putInt(GlobalRef.KEY_FAV_MOVIE_ID, GlobalRef.FAV_MOVIE_READ);

            getLoaderManager().restartLoader(GlobalRef.LOADING_ID_MOVIE_DATABASE, bundle, this);

        } else if ( (GlobalRef.NOW_PLAYING_MOVIES == iIndex)      ||
                    (GlobalRef.TOPRATED_MOVIES_INDEX == iIndex) ||
                    (GlobalRef.POPULAR_MOVIES_INDEX == iIndex)) {

            Bundle bundle = new Bundle();
            bundle.putInt(GlobalRef.KEY_MOVIE_SERVICE_LOADING, iIndex);

            getLoaderManager().restartLoader(GlobalRef.LOADING_ID_MOVIE_SERVER, bundle, this);

        }
    }

    private void setupRecycleView(){
        int spanCount = getColumnValue(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);

        scrollListener = new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                GlobalRef.PAGE_NUM = page;
                if(page < TOTAL_PAGES){
                    fetchMovies(MOVIE_FETCH_INDEX);
                }
            }
        };

        mainBinding.rvMovieList.setHasFixedSize(true);
        mainBinding.rvMovieList.setLayoutManager(layoutManager);
        mainBinding.rvMovieList.setItemAnimator(new DefaultItemAnimator());
        mainBinding.rvMovieList.addOnScrollListener(scrollListener);

        adapter = new MovieAdapter(this);

        mainBinding.rvMovieList.setAdapter(adapter);

        IF_ADAPTER_IS_SET = true;
    }

    private static int getColumnValue(Context context){
        if(context.getResources().getBoolean(R.bool.is_portrait)){
            return 2;
        } else {
            return 4;
        }
    }

    @Override
    public void onMovieItemClick(int clickedItemIndex, MovieDetails movieDetails) {

            Intent movieDetailActivity = new Intent(this, MovieDetailActivity.class);
            movieDetailActivity.putExtra(GlobalRef.KEY_MOVIE_DETAILS_DATA, movieDetails);
            startActivity(movieDetailActivity);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.ic_filter:
                View view = findViewById(R.id.ic_filter);
                PopupMenu popupMenu = new PopupMenu(this, view);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.inflate(R.menu.menu_actions);
                popupMenu.getMenu().getItem(MOVIE_FETCH_INDEX).setChecked(true);
                popupMenu.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        enableMenuItemAndFetchMovies(item);

        return true;
    }

    private void enableMenuItemAndFetchMovies(MenuItem item) {
        enableMenuItem(item);
        clearAndFetch();
    }

    private void enableMenuItem(MenuItem item) {

        switch (item.getItemId()){
            case R.id.all:
                getSupportActionBar().setTitle(R.string.now_playing);
                MOVIE_FETCH_INDEX = GlobalRef.NOW_PLAYING_MOVIES;
                break;
            case R.id.toprated:
                getSupportActionBar().setTitle(R.string.top_rated);
                MOVIE_FETCH_INDEX = GlobalRef.TOPRATED_MOVIES_INDEX;
                break;
            case R.id.popular:
                getSupportActionBar().setTitle(R.string.popular);
                MOVIE_FETCH_INDEX = GlobalRef.POPULAR_MOVIES_INDEX;
                break;
            case R.id.favourite:
                getSupportActionBar().setTitle(R.string.favorites);
                MOVIE_FETCH_INDEX = GlobalRef.FAVOURITE_MOVIES_INDEX;
                break;
        }
    }

    private void clearAndFetch(){
        clearList();
        GlobalRef.PAGE_NUM = 1;
        fetchMovies(MOVIE_FETCH_INDEX);

    }

    private void clearList() {
        adapter.clearAll();
        scrollListener.resetState();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle bundleArgs) {

        switch (id) {

            case GlobalRef.LOADING_ID_MOVIE_SERVER:

                return new MovieLoader(this, bundleArgs);

            case GlobalRef.LOADING_ID_MOVIE_DATABASE:

                int iDbAction = bundleArgs.getInt(GlobalRef.KEY_FAV_MOVIE_ID);

                switch (iDbAction) {

                    case GlobalRef.FAV_MOVIE_NULL:
                            return null;

                    case GlobalRef.FAV_MOVIE_READ:
                            String sortOrder = FavContract.FavMovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
                            return new CursorLoader(this,
                            FavContract.FavMovieEntry.CONTENT_URI,
                            GlobalRef.PROJECTION,
                            null, null, sortOrder);
                }
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

        if(null != data) {
            int id = loader.getId();

            switch (id) {
                case GlobalRef.LOADING_ID_MOVIE_SERVER:
                    setupAdapterServerData(data);
                    break;

                case GlobalRef.LOADING_ID_MOVIE_DATABASE:
                    setupAdapterDatabaseData(data);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private void setupAdapterServerData(Object object){

            Movie movieObj = (Movie) object;
            GlobalRef.PAGE_NUM = movieObj.getPage();
            TOTAL_PAGES = movieObj.getTotalPages();
            GlobalRef.TOTAL_ITEMS_COUNT = movieObj.getTotalResults();
            adapter.newData(movieObj.getMovieDetails());
    }

    private void setupAdapterDatabaseData(Object object){

            if (MOVIE_FETCH_INDEX == GlobalRef.FAVOURITE_MOVIES_INDEX) {
                //Upon every Fav Set, this condition prevents page refresh.

                adapter.clearAll();

                Cursor cursor = (Cursor) object;
                if (cursor.getCount() > 0) {
                    adapter.newCursorData(cursor);
                }
            }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

            if(IF_ADAPTER_IS_SET) {
                if (0 < adapter.getItemCount()) {
                    ArrayList<MovieDetails> movieDetailsSavedStateList = new ArrayList<>(adapter.getMovies());
                    outState.putParcelableArrayList(GlobalRef.INSTANCE_STATE_LIST_MOVIES, movieDetailsSavedStateList);
                    outState.putInt(GlobalRef.INSTANCE_STATE_MOVIE_TYPE_INDEX, MOVIE_FETCH_INDEX);
                    outState.putString(GlobalRef.INSTANCE_STATE_TOOLBAR_MOVIE_TITLE, getSupportActionBar().getTitle().toString());

                    Parcelable listState = mainBinding.rvMovieList.getLayoutManager().onSaveInstanceState();
                    outState.putParcelable(GlobalRef.INSTANCE_STATE_LAYOUTMANAGER, listState);
                }
            }
    }

}
>>>>>>> 5bd5f9266ec76f4bbe33414f7c43d01943498a61
