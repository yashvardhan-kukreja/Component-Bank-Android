package com.ieeevit.componentbank.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ieeevit.componentbank.Classes.User;
import com.ieeevit.componentbank.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yash 1300 on 04-01-2018.
 */

public class ListOfIssuersAdapter extends ArrayAdapter {

    @BindView(R.id.compNameIssuersListAdmin) TextView compName;
    @BindView(R.id.memNameIssuersListAdmin) TextView memName;
    @BindView(R.id.memRegNumIssuersListAdmin) TextView memRegno;
    @BindView(R.id.dateIssuersListAdmin) TextView date;
    @BindView(R.id.quantityIssuersListAdmin) TextView quantity;

    Context context;
    List<User> usersList;
    List<String> dates;
    List<String> quantities;
    List<String> compNames;
    int choice; // choice is 0 if the adapter is used for issuers, choice is 1 if the adapter is used for requests

    public ListOfIssuersAdapter(@NonNull Context context, List<User> usersList, List<String> dates, List<String> quantities, List<String> compNames,int choice) {
        super(context, R.layout.adapter_issuers_list, usersList);
        this.context = context;
        this.usersList = usersList;
        this.dates = dates;
        this.quantities = quantities;
        this.compNames = compNames;
        this.choice = choice;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View v = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.adapter_issuers_list, null, false);
        ButterKnife.bind(this, v);

        compName.setText(compNames.get(position));
        memName.setText(usersList.get(position).getName());
        memRegno.setText(usersList.get(position).getRegNum());
        date.setText(dates.get(position));
        quantity.setText("Quantity\n"+quantities.get(position));

        return v;
    }
}
