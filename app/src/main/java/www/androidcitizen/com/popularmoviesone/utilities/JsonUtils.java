package www.androidcitizen.com.popularmoviesone.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import www.androidcitizen.com.popularmoviesone.model.Movie;

/**
 * Created by Mahi on 13/06/18.
 * www.androidcitizen.com
 */

public class JsonUtils {

    private final static String KEY_RESULTS      = "results";
    private final static String KEY_MOVIE_ID     = "id";
    private final static String KEY_VOTE_AVERAGE = "vote_average";
    private final static String KEY_TITLE        = "title";
    private final static String KEY_POSTER_PATH  = "poster_path";

    public static List<Movie> processJsonData(String jsonResponse)
            throws JSONException {

        List<Movie> movies = new ArrayList<>();

        JSONObject rootObject = new JSONObject(jsonResponse);

        JSONArray resultsArray = rootObject.optJSONArray(KEY_RESULTS);

        for(int index = 0; index < resultsArray.length(); index++) {
            JSONObject resultObject = resultsArray.optJSONObject(index);

            String t = resultObject.optString(KEY_TITLE);
            int i = resultObject.optInt(KEY_MOVIE_ID);
            String p = resultObject.optString(KEY_POSTER_PATH);
            String v = resultObject.optString(KEY_VOTE_AVERAGE);
            float f = Float.parseFloat(v);

            movies.add(new Movie(resultObject.optString(KEY_TITLE),
                                 resultObject.optInt(KEY_MOVIE_ID),
                                 resultObject.optString(KEY_POSTER_PATH),
                                 Float.parseFloat(resultObject.optString(KEY_VOTE_AVERAGE)) )
            );

        }

        return movies;

    }

}
