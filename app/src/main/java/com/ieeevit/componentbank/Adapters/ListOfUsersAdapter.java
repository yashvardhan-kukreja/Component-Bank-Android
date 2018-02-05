package com.ieeevit.componentbank.Adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ieeevit.componentbank.Classes.User;
import com.ieeevit.componentbank.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yash 1300 on 04-12-2017.
 */


public class ListOfUsersAdapter extends ArrayAdapter {

    @BindView(R.id.nameOfIssuer) TextView name;
    @BindView(R.id.regNumOfIssuer) TextView regnum;
    @BindView(R.id.userCallButton) Button call;
    @BindView(R.id.issuerLayout) LinearLayout issuer;

    Context context;
    List<User> users;
    List<String> issueDates;
    List<String> quantities;
    Activity activity;

    public ListOfUsersAdapter(@NonNull Context context, Activity activity,@NonNull List users, List issueDates, List quantities) {
        super(context, R.layout.adapter_users_list, users);
        this.context = context;
        this.activity = activity;
        this.users = users;
        this.issueDates = issueDates;
        this.quantities = quantities;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.adapter_users_list, parent, false);
        ButterKnife.bind(this, v);

        name.setText(users.get(position).getName());
        regnum.setText(users.get(position).getRegNum());

        //Checking if the app has the permission to make calls and if not then, ask the user to permit the app to make calls
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 1);

        issuer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View v = View.inflate(context,R.layout.dialog_user_details, null);
                TextView dialogName = v.findViewById(R.id.dialogUserName);
                TextView dialogRegNum = v.findViewById(R.id.dialogRegNum);
                TextView dialogEmail = v.findViewById(R.id.dialogEmailId);
                TextView dialogContactNum = v.findViewById(R.id.dialogContactNum);
                TextView dialogIssueDate = v.findViewById(R.id.dialogIssueDate);
                TextView quantity = v.findViewById(R.id.dialogQuantity);

                dialogName.setText(users.get(position).getName());
                dialogRegNum.setText("Reg. Number: "+users.get(position).getRegNum());
                dialogEmail.setText("E-mail ID: "+users.get(position).getEmail());
                dialogContactNum.setText("Contact Number: "+users.get(position).getPhonenum());
                dialogIssueDate.setText("Issued On: "+issueDates.get(position));
                quantity.setText("Quantity: "+ quantities.get(position));
                builder.setView(v);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

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
