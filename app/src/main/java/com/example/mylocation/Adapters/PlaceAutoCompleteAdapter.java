package com.example.mylocation.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.mylocation.API.APIClient;
import com.example.mylocation.API.GoogleMapsAPI;
import com.example.mylocation.Entities.PlacePrediction;
import com.example.mylocation.Entities.PlacePredictions;
import com.example.mylocation.R;

import java.util.ArrayList;
import java.util.List;


public class PlaceAutoCompleteAdapter extends ArrayAdapter<PlacePrediction> {
    private static final String TAG = "PlaceAutoCompAdapter";
    private Context mContext;
    private List<PlacePrediction> placePredictions;

    public PlaceAutoCompleteAdapter(Context context) {
        super(context, R.layout.prediction_place);
        this.mContext = context;
        this.placePredictions = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return placePredictions.size();
    }

    @Nullable
    @Override
    public PlacePrediction getItem(int position) {
        return placePredictions.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.prediction_place, parent, false);

        if (placePredictions != null && placePredictions.size() > 0) {
            PlacePrediction placePrediction = placePredictions.get(position);
            TextView predictionTV = convertView.findViewById(R.id.predictionTV);
            predictionTV.setText(placePrediction.getDescription());
        }
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new PlaceAutoCompleteFilter(this, mContext);
    }

    private class PlaceAutoCompleteFilter extends Filter {

        private PlaceAutoCompleteAdapter placeAutoCompleteAdapter;
        private Context mContext;

        private PlaceAutoCompleteFilter(PlaceAutoCompleteAdapter placeAutoCompleteAdapter, Context context) {
            super();
            this.placeAutoCompleteAdapter = placeAutoCompleteAdapter;
            this.mContext = context;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            try {
                placeAutoCompleteAdapter.placePredictions.clear();

                FilterResults filterResults = new FilterResults();
                if ((charSequence == null) || (charSequence.length() == 0)) {
                    filterResults.values = new ArrayList<PlacePrediction>();
                    filterResults.count = 0;
                }
                else {
                    GoogleMapsAPI googleMapsAPI = APIClient.getClient().create(GoogleMapsAPI.class);

                    PlacePredictions placePredictions = googleMapsAPI.getPlaceAutoComplete(charSequence.toString(),
                            "geocode", "en", mContext.getString(R.string.google_maps_key)).execute().body();

                    if (placePredictions != null) {
                        filterResults.values = placePredictions.getPlacePredictions();
                        filterResults.count = placePredictions.getPlacePredictions().size();
                    }
                }
                return filterResults;
            } catch (Exception e) {
                return null;
            }
        }

        @SuppressWarnings("unchecked")   //Used to suppress the unchecked cast warning
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if ((filterResults != null) && (filterResults.count > 0)) {
                placeAutoCompleteAdapter.placePredictions.clear();
                placeAutoCompleteAdapter.placePredictions.addAll((List<PlacePrediction>) filterResults.values);

                notifyDataSetChanged();
            }
            else
                notifyDataSetInvalidated();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            PlacePrediction placePrediction = (PlacePrediction) resultValue;
            return placePrediction.getDescription();
        }
    }
}