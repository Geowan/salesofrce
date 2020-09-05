package com.soradius.salesforce.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soradius.salesforce.R;
import com.soradius.salesforce.details.AccountDetailsActivity;
import com.soradius.salesforce.models.AccountListModel;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AccountListRecyclerviewAdapter extends  RecyclerView.Adapter<AccountListRecyclerviewAdapter.ViewHolder> {

    ArrayList<AccountListModel> accountListModels;
    Context context;

    public AccountListRecyclerviewAdapter(ArrayList<AccountListModel> accountListModels, Context context) {
        this.accountListModels = accountListModels;
        this.context = context;
    }

    @NonNull
    @Override
    public AccountListRecyclerviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_list_recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountListRecyclerviewAdapter.ViewHolder holder, int position) {
        final AccountListModel item = accountListModels.get(position);
        holder.accountName.setText(item.getName());
        holder.positionNo.setText(String.valueOf(position+1)+".");
        holder.accountBalance.setText(item.getBalance__c());
        holder. layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AccountDetailsActivity.class);
                intent.putExtra("account_id",item.getAccountId());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return accountListModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView positionNo,accountName,accountBalance;
        LinearLayout layoutItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            accountName = itemView.findViewById(R.id.accountName);
            positionNo = itemView.findViewById(R.id.positionNo);
            accountBalance = itemView.findViewById(R.id.accountBalance);
            layoutItem = itemView.findViewById(R.id.layoutItem);


        }
    }
}
