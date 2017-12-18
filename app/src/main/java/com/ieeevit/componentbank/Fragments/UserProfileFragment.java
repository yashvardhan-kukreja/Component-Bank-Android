package com.ieeevit.componentbank.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Yash 1300 on 12-12-2017.
 */

@SuppressLint("ValidFragment")
public class UserProfileFragment extends Fragment {
    Context context;
    String currentUsername, currentUserEmail, currentUserRegNum, currentUserPhoneNum;

    public UserProfileFragment(Context context, String currentUsername, String currentUserEmail, String currentUserRegNum, String currentUserPhoneNum) {
        this.context = context;
        this.currentUsername = currentUsername;
        this.currentUserEmail = currentUserEmail;
        this.currentUserRegNum = currentUserRegNum;
        this.currentUserPhoneNum = currentUserPhoneNum;
    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }
}
