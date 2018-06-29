package acgmaps.com.searoth.geocodingaddresssearch.ui;


import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import acgmaps.com.searoth.geocodingaddresssearch.R;
import acgmaps.com.searoth.geocodingaddresssearch.model.OurResult;




import acgmaps.com.searoth.geocodingaddresssearch.databinding.MyResultItemBinding;


public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    List<? extends OurResult> mResultList;
    @Nullable
    private final LocationClickCallback mProductClickCallback;

    public ResultAdapter(@Nullable LocationClickCallback clickCallback) {
        mProductClickCallback = clickCallback;
    }

    public void setProductList(final List<? extends OurResult> productList) {
        if (productList.size() != 0) {
            mResultList = productList;
            notifyItemRangeInserted(0, productList.size());
        }
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyResultItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.my_result_item,parent, false);
        binding.setCallback(mProductClickCallback);
        return new ResultViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        holder.binding.setResult(mResultList.get(position));
        int pos = position + 1;
        String s = ""+pos;
        holder.number.setText(s);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {

        final MyResultItemBinding binding;
        final TextView number;

        public ResultViewHolder(MyResultItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.number = binding.itemNumber;
        }
    }
}
