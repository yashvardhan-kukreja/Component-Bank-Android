package com.ieeevit.componentbank.Adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ieeevit.componentbank.Classes.User;
import com.ieeevit.componentbank.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yash 1300 on 04-12-2017.
 */


public class ListOfUsersAdapter extends ArrayAdapter {

    TextView name, regnum;
    Button call;
    Context context;
    List<User> users;

    public ListOfUsersAdapter(@NonNull Context context, @NonNull List users) {
        super(context, R.layout.adapter_users_list, users);
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.adapter_users_list, parent, false);
        name = v.findViewById(R.id.nameOfIssuer);
        regnum = v.findViewById(R.id.regNumOfIssuer);
        call = v.findViewById(R.id.userCallButton);
        users = new ArrayList<>();

        name.setText(users.get(position).getName());
        regnum.setText(users.get(position).getRegNum());

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + users.get(position).getPhonenum()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "App doesn't have permission to call", Toast.LENGTH_SHORT).show();
                    return;
                }
                context.startActivity(intent);
            }
        });
        return v;
    }
}
