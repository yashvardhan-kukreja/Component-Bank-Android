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

/**
 * Created by Yash 1300 on 04-01-2018.
 */

public class ListOfIssuersAdapter extends ArrayAdapter {
    TextView compName, memName, memRegno, date, quantity;
    Context context;
    LinearLayout linearLayout;
    List<User> usersList;
    List<String> dates;
    List<String> quantities;
    List<String> compNames;
    List<String> transIds;
    String transId;
    String token;
    int choice; // choice is 0 if the adapter is used for issuers, choice is 1 if the adapter is used for requests
    public ListOfIssuersAdapter(@NonNull Context context, List<User> usersList, List<String> dates, List<String> quantities, List<String> compNames,int choice) {
        super(context, R.layout.adapter_issuers_list, usersList);
        this.context = context;
        this.usersList = usersList;
        this.dates = dates;
        this.quantities = quantities;
        this.compNames = compNames;
        //this.transIds = transIds;
        this.choice = choice;
        //this.token = token;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View v = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.adapter_issuers_list, null, false);

        compName = v.findViewById(R.id.compNameIssuersListAdmin);
        memName = v.findViewById(R.id.memNameIssuersListAdmin);
        memRegno = v.findViewById(R.id.memRegNumIssuersListAdmin);
        date = v.findViewById(R.id.dateIssuersListAdmin);
        quantity = v.findViewById(R.id.quantityIssuersListAdmin);
        linearLayout = v.findViewById(R.id.issuerAdminLayout);

        compName.setText(compNames.get(position));
        memName.setText(usersList.get(position).getName());
        memRegno.setText(usersList.get(position).getRegNum());
        date.setText(dates.get(position));
        quantity.setText("Quantity\n"+quantities.get(position));

        return v;
    }
}
