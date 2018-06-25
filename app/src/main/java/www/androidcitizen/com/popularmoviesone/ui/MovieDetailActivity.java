package www.androidcitizen.com.popularmoviesone.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import www.androidcitizen.com.popularmoviesone.R;
import www.androidcitizen.com.popularmoviesone.model.MovieDetails;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageView ivPoster;
        ImageView ivBackground;
        TextView tvTitle;
        RatingBar rvRatingbar;
        TextView tvReleaseDate;
        TextView tvOverview;

        setContentView(R.layout.activity_movie_detail);

        ivPoster        = findViewById(R.id.iv_posterImage);
        ivBackground    = findViewById(R.id.iv_backgroundImage);
        tvTitle         = findViewById(R.id.tv_title);
        rvRatingbar     = findViewById(R.id.ratingBar);
        tvReleaseDate   = findViewById(R.id.tv_releaseDate);
        tvOverview      = findViewById(R.id.tv_overView);

        MovieDetails movieDetails = getIntent().getParcelableExtra("movieDetailsdata");

        Picasso.get()
                .load(movieDetails.getBackdropPath())
                //.placeholder("http://via.placeholder.com/350x150")
                //.error(R.drawable.user_placeholder_error)
                .into(ivBackground);

        Picasso.get()
                .load(movieDetails.getPosterPath())
                //.placeholder("http://via.placeholder.com/350x150")
                //.error(R.drawable.user_placeholder_error)
                .into(ivPoster);

            tvTitle.setText(movieDetails.getTitle());
            rvRatingbar.setRating(movieDetails.getVoteAverage()/2);
            tvReleaseDate.setText(movieDetails.getReleaseDate());
            tvOverview.setText(movieDetails.getOverview());

    }

}
