package itstep.learning.android_212.nburate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import itstep.learning.android_212.R;
import itstep.learning.android_212.orm.NbuRate;

public class RateAdapter extends RecyclerView.Adapter<RateAdapter.RateViewHolder> {

    private List<NbuRate> rates;

    public RateAdapter(List<NbuRate> rates) {
        this.rates = rates;
    }

    @NonNull
    @Override
    public RateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rate_item, parent, false);
        return new RateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RateViewHolder holder, int position) {
        NbuRate rate = rates.get(position);
        holder.currencyTextView.setText(rate.getCc());
        holder.rateTextView.setText(String.valueOf(rate.getRate()));
    }

    @Override
    public int getItemCount() {
        return rates.size();
    }

    static class RateViewHolder extends RecyclerView.ViewHolder {
        TextView currencyTextView;
        TextView rateTextView;

        RateViewHolder(@NonNull View itemView) {
            super(itemView);
            currencyTextView = itemView.findViewById(R.id.currencyTextView);
            rateTextView = itemView.findViewById(R.id.rateTextView);
        }
    }
}