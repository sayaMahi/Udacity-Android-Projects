package www.androidcitizen.com.popularmoviesone.data.adapter;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import www.androidcitizen.com.popularmoviesone.R;
import www.androidcitizen.com.popularmoviesone.config.GlobalRef;
import www.androidcitizen.com.popularmoviesone.data.model.MovieDetails;

/**
 * Created by Mahi on 13/06/18.
 * www.androidcitizen.com
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    private List<MovieDetails> movieDetails = null;

    final private MovieClickListener movieClickListener;

    public MovieAdapter(MovieClickListener movieClickListener) {
        this.movieClickListener = movieClickListener;
    }

    public interface MovieClickListener{
        void  onMovieItemClick(int clickedItemIndex, MovieDetails movieDetails);
    }

    @Override
    public int getItemCount() {
        if(null != movieDetails)
            return movieDetails.size();
        else
            return 0;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.grid_item_view,
                                             parent, false);

        return (new MovieViewHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.onBind(position);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        final ImageView imagePosterView;

        private MovieViewHolder(View view) {
            super(view);
            imagePosterView = view.findViewById(R.id.iv_posterImage);
            imagePosterView.setOnClickListener(this);
        }

        void onBind(int index) {

            Picasso.get()
                    .load(movieDetails.get(index).getPosterPath())
                    //.placeholder("http://via.placeholder.com/350x150")
                    //.error(R.drawable.user_placeholder_error)
                    .into(imagePosterView);

        }

        @Override
        public void onClick(View v) {
            int clickedItemIndex = getAdapterPosition();
            movieClickListener.onMovieItemClick(clickedItemIndex, movieDetails.get(clickedItemIndex));
        }
    }

    public void newData(List<MovieDetails> movieList){

        if(null == movieDetails) {

            movieDetails = movieList;
            notifyDataSetChanged();

        } else {

            for (MovieDetails movieDetail : movieList) {
                add(movieDetail);
            }
        }
    }

    public void newCursorData(Cursor cursor) {

        if(null != cursor && 0 != cursor.getCount()) {
            cursor.moveToFirst();
            do {

                MovieDetails movieDetails =
                        new MovieDetails(cursor.getInt(GlobalRef.INDEX_MOVIE_ID),
                                cursor.getString(GlobalRef.INDEX_TITLE),
                                cursor.getString(GlobalRef.INDEX_POSTER_PATH),
                                cursor.getString(GlobalRef.INDEX_BACKDROP_PATH),
                                cursor.getString(GlobalRef.INDEX_RELEASE_DATE),
                                cursor.getString(GlobalRef.INDEX_OVERVIEW),
                                cursor.getFloat(GlobalRef.INDEX_VOTE_AVERAGE));

                add(movieDetails);

            } while (cursor.moveToNext());
        }
    }

    private void add(MovieDetails movieInfo) {
        movieDetails.add(movieInfo);
        notifyItemInserted(movieDetails.size() - 1);
    }

    public void clearAll() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    private void remove(MovieDetails movie) {
        int position = movieDetails.indexOf(movie);
        movieDetails.remove(position);
        notifyItemRemoved(position);

    }

    private MovieDetails getItem(int position) {
        return movieDetails.get(position);
    }


}
