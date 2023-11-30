package com.example.proyectomoviles.view.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.proyectomoviles.R;
import com.example.proyectomoviles.model.Client;

import java.util.ArrayList;

public class item_Adapter_Call_List extends BaseAdapter {

    Context context;
    ArrayList<Client> list;

    public item_Adapter_Call_List(Context context, ArrayList<Client> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_adapter_call_list, null);
        }

        TextView txtClientName = view.findViewById(R.id.txtNameClientAdapter);
        TextView txtClientPhone = view.findViewById(R.id.txtClientNumberAdapter);

        txtClientName.setText(list.get(i).getName());
        txtClientPhone.setText(String.valueOf(list.get(i).getPhone()));


        return view;
    }
}
