package com.ieeevit.componentbank.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ieeevit.componentbank.Classes.Component;
import com.ieeevit.componentbank.R;

import java.util.List;

/**
 * Created by Yash 1300 on 12-12-2017.
 */

public class ComponentsListAdapter extends ArrayAdapter {
Context context;
List<Component> components;

TextView name, code, available;

    public ComponentsListAdapter(@NonNull Context context, List<Component> components) {
        super(context, R.layout.adapter_components_list, components);
        this.context = context;
        this.components = components;
    }
    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.adapter_components_list, parent, false);
        name = v.findViewById(R.id.componentsListComponentName);
        code = v.findViewById(R.id.componentsListComponentId);
        available = v.findViewById(R.id.componentsListComponentAvailability);

        name.setText(components.get(position).getName()); // The name of the component
        code.setText(components.get(position).getCode()); // The code of the component
        available.setText("Available\n"+components.get(position).getQuantity()); // The availability of that component
        if (components.get(position).getQuantity().equals("0")){
            available.setTextColor(Color.parseColor("#aa0000"));
        } else {
            available.setTextColor(Color.parseColor("#00aa00"));
        }

        return v;
    }
}
