package com.ieeevit.componentbank.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ieeevit.componentbank.R;

import java.util.List;

/**
 * Created by Yash 1300 on 10-01-2018.
 */

public class ListOfUnauthUsersAdapter extends ArrayAdapter {
    TextView name, regnum;
    Context context;
    List<String> names;
    List<String> regnums;

    public ListOfUnauthUsersAdapter(@NonNull Context context, List<String> names, List<String> regnums) {
        super(context, R.layout.adapter_unauth_users, names);
        this.context = context;
        this.names = names;
        this.regnums = regnums;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = LayoutInflater.from(context).inflate(R.layout.adapter_unauth_users, parent, false);
        name = v.findViewById(R.id.unauthUsername);
        regnum = v.findViewById(R.id.unauthUserRegnum);

        name.setText(names.get(position));
        regnum.setText(regnums.get(position));

        return v;
    }
}
