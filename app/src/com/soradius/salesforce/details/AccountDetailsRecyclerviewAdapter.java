package com.soradius.salesforce.details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.soradius.salesforce.R;

import java.util.ArrayList;

public class AccountDetailsRecyclerviewAdapter extends  RecyclerView.Adapter<AccountDetailsRecyclerviewAdapter.ViewHolder> {

    ArrayList<AccountDetailsModel> items;
    Context context;

    public AccountDetailsRecyclerviewAdapter(ArrayList<AccountDetailsModel> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public AccountDetailsRecyclerviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_details_recyclerview_row, parent, false);
        return new AccountDetailsRecyclerviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountDetailsRecyclerviewAdapter.ViewHolder holder, int position) {
        AccountDetailsModel item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.value.setText(item.getValue());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title,value;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            value = itemView.findViewById(R.id.value);
        }
    }
}
