package com.ieeevit.componentbank.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ieeevit.componentbank.Classes.Component;
import com.ieeevit.componentbank.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yash 1300 on 12-12-2017.
 */

@SuppressLint("ValidFragment")
public class UserProfileFragment extends Fragment {
    Context context;
    String currentUsername, currentUserEmail, currentUserRegNum, currentUserPhoneNum;
    TextView name, regnum, componentsIssued;
    ListView componentsList;
    List<Component> components;
    public UserProfileFragment(Context context, String currentUsername, String currentUserEmail, String currentUserRegNum, String currentUserPhoneNum) {
        this.context = context;
        this.currentUsername = currentUsername;
        this.currentUserEmail = currentUserEmail;
        this.currentUserRegNum = currentUserRegNum;
        this.currentUserPhoneNum = currentUserPhoneNum;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_profile_page, container, false);
        components = new ArrayList<>();
        name = v.findViewById(R.id.profilePageGreeting);
        regnum = v.findViewById(R.id.profileRegNum);
        componentsIssued = v.findViewById(R.id.profileComponentsIssued);

        name.setText("Hello,\n" + currentUsername);
        regnum.setText("Reg. Number: " + currentUserRegNum);



        return v;
    }
}
