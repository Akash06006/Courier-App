package com.example.courier.maps

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.google.android.gms.common.data.DataBufferUtils
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import java.util.ArrayList
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class PlaceAutocompleteAdapter
/**
 * Initializes with a resource for text rows and autocomplete query bounds.
 *
 * @see ArrayAdapter.ArrayAdapter
 */
internal constructor(
    context : Context,
    /**
     * Handles autocomplete requests.
     */
    private val mGeoDataClient : GeoDataClient,
    /**
     * The bounds used for Places Geo Data autocomplete API requests.
     */
    private var mBounds : LatLngBounds?,
    /**
     * The autocomplete filter used to restrict queries to a specific set of place types.
     */
    private val mPlaceFilter : AutocompleteFilter
) : ArrayAdapter<AutocompletePrediction>(
    context,
    android.R.layout.simple_list_item_activated_2,
    android.R.id.text1
),
    Filterable {
    /**
     * Current results returned by this adapter.
     */
    private var mResultList : ArrayList<AutocompletePrediction>? = null

    /**
     * Sets the bounds for all subsequent queries.
     */
    fun setBounds(bounds : LatLngBounds) {
        mBounds = bounds
    }

    /**
     * Returns the number of results received in the last autocomplete query.
     */
    override fun getCount() : Int {
        return mResultList!!.size
    }

    /**
     * Returns an item from the last autocomplete query.
     */
    override fun getItem(position : Int) : AutocompletePrediction? {
        return mResultList!![position]
    }

    override fun getView(position : Int, convertView : View?, parent : ViewGroup) : View {
        val row = super.getView(position, convertView, parent)
        // Sets the primary and secondary text for a row.
        // Note that getPrimaryText() and getSecondaryText() return a CharSequence that may contain
        // styling based on the given CharacterStyle.
        val item = getItem(position)
        val textView1 = row.findViewById<TextView>(android.R.id.text1)
        val textView2 = row.findViewById<TextView>(android.R.id.text2)
        assert(item != null)
        textView1.text = item!!.getPrimaryText(STYLE_BOLD)
        textView2.text = item.getSecondaryText(STYLE_NORMAL)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            textView1.setTextAppearance(
                parent.context,
                android.R.style.TextAppearance_DeviceDefault_Medium
            )
            textView2.setTextAppearance(
                parent.context,
                android.R.style.TextAppearance_DeviceDefault_Small
            )
        } else {
            textView1.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Medium)
            textView2.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Small)
        }

        return row
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    override fun getFilter() : Filter {
        return object : Filter() {
            override fun performFiltering(constraint : CharSequence?) : Filter.FilterResults {
                val results = Filter.FilterResults()
                // We need a separate latLongList to store the results, since
                // this is run asynchronously.
                var filterData : ArrayList<AutocompletePrediction>? = ArrayList()
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    filterData = getAutocomplete(constraint)
                }

                results.values = filterData
                if (filterData != null) {
                    results.count = filterData.size
                } else {
                    results.count = 0
                }

                return results
            }

            override fun publishResults(
                constraint : CharSequence,
                results : Filter.FilterResults?
            ) {
                if (results != null && results.count > 0) {
                    //  HomeActivity.search_icon.setVisibility(View.GONE);
                    // HomeActivity.cross_icon.setVisibility(View.VISIBLE);
                    // The API returned at least one result, update the data.
                    mResultList = results.values as ArrayList<AutocompletePrediction>
                    notifyDataSetChanged()
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated()
                }
            }

            override fun convertResultToString(resultValue : Any) : CharSequence {
                // Override this method to display a readable result in the AutocompleteTextView
                // when clicked.
                return if (resultValue is AutocompletePrediction) {
                    resultValue.getFullText(null)
                } else {
                    super.convertResultToString(resultValue)
                }
            }
        }
    }

    /**
     * Submits an autocomplete query to the Places Geo Data Autocomplete API.
     * Results are returned as frozen AutocompletePrediction objects, ready to be cached.
     * Returns an empty latLongList if no results were found.
     * Returns null if the API client is not available or the query did not complete
     * successfully.
     * This method MUST be called off the main UI thread, as it will block until data is returned
     * from the API, which may include a network request.
     *
     * @param constraint Autocomplete query string
     * @return Results from the autocomplete API or null if the query was not successful.
     * @see GeoDataClient.getAutocompletePredictions
     * @see AutocompletePrediction.freeze
     */
    private fun getAutocomplete(constraint : CharSequence) : ArrayList<AutocompletePrediction>? {
        // Submit the query to the autocomplete API and retrieve a PendingResult that will
        // contain the results when the query completes.
        val results = mGeoDataClient.getAutocompletePredictions(
            constraint.toString(), mBounds,
            mPlaceFilter
        )
        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            Tasks.await(results, 60, TimeUnit.SECONDS)
        } catch (e : ExecutionException) {
            e.printStackTrace()
        } catch (e : InterruptedException) {
            e.printStackTrace()
        } catch (e : TimeoutException) {
            e.printStackTrace()
        }

        try {
            val autocompletePredictions = results.result
            // Freeze the results immutable representation that can be stored safely.
            return DataBufferUtils.freezeAndClose(autocompletePredictions!!)
        } catch (e : RuntimeExecutionException) {
            // If the query did not complete successfully return null
            //   Toast.makeText(getContext(), "Error contacting API: " + e.toString(), Toast.LENGTH_SHORT).show();
            return null
        }

    }

    companion object {
        private val STYLE_BOLD = StyleSpan(Typeface.BOLD)
        private val STYLE_NORMAL = StyleSpan(Typeface.NORMAL)
    }
}
