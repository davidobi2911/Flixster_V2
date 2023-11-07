import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Headers
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codepath.asynchttpclient.RequestParams
import com.example.flixsterv2.R
import com.example.flixsterv2.TopPlayingMovie
import com.example.flixsterv2.TopPlayingMovieRecyclerViewAdapter

private const val API_KEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed"

class TopPlayingMovieFragment(private val context: Context) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movie_item_list, container, false)
        val progressBar = view.findViewById<View>(R.id.progressBar) as ContentLoadingProgressBar
        val recyclerView = view.findViewById<View>(R.id.movieList) as RecyclerView
        val context = view.context
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        updateAdapter(progressBar, recyclerView)
        return view
    }

    private fun updateAdapter(progressBar: ContentLoadingProgressBar, recyclerView: RecyclerView) {
        progressBar.show()

        val client = AsyncHttpClient()
        val params = RequestParams()
        params["api_key"] = API_KEY

        // "now_playing" endpoint (changed from "upcoming")
        client[
            "https://api.themoviedb.org/3/movie/popular", // API endpoint
            params,
            object : JsonHttpResponseHandler() {
                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    errorResponse: String,
                    throwable: Throwable?
                ) {
                    progressBar.hide()
                    throwable?.message?.let {
                        Log.e("TopPlayingMovieFragment", errorResponse)
                    }
                }

                override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                    progressBar.hide()

                    val resultsJson: String = json.jsonObject.get("results").toString()
                    val gson = Gson()
                    val arrayMovieType = object : TypeToken<List<TopPlayingMovie>>() {}.type
                    val models: List<TopPlayingMovie> = gson.fromJson(resultsJson, arrayMovieType)
                    recyclerView.adapter = TopPlayingMovieRecyclerViewAdapter(models, context)

                    Log.d("TopPlayingMovieFragment", "Response successful")
                }
            }
        ]
    }
}
