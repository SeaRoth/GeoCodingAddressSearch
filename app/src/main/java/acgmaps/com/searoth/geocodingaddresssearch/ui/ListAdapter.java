package acgmaps.com.searoth.geocodingaddresssearch.ui;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import acgmaps.com.searoth.geocodingaddresssearch.R;
import acgmaps.com.searoth.geocodingaddresssearch.databinding.MyListItemBinding;
import acgmaps.com.searoth.geocodingaddresssearch.model.LocationModel;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ResultViewHolder> {

    List<? extends LocationModel> mLocations;
    @Nullable
    private final LocationClickCallback callback;

    ListAdapter(@Nullable LocationClickCallback clickCallback) {
        callback = clickCallback;
    }

    public void setProductList(final List<? extends LocationModel> locations) {
        if (locations != null ) {
            mLocations = locations;
            notifyItemRangeInserted(0, locations.size());
        }
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyListItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.my_list_item,parent, false);
        binding.setCallback(callback);
        return new ResultViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        holder.binding.setResult(mLocations.get(position));
        int pos = position + 1;
        String s = ""+pos;
        holder.number.setText(s);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mLocations.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {

        final MyListItemBinding binding;
        final TextView number;

        ResultViewHolder(MyListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.number = binding.itemNumber;
        }
    }
}
