package com.example.proyectomoviles.view.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.proyectomoviles.R;

public class frm_Message_Client extends Fragment {

    TextView clientPhone;
    EditText message;
    Button btnSend;

    public frm_Message_Client() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frm_message_client, container, false);

        clientPhone = view.findViewById(R.id.txtClientPhoneMessage);
        btnSend = view.findViewById(R.id.btnSend);
        message = view.findViewById(R.id.textMessage);

        String txt = clientPhone.getText().toString();

        Bundle arg = getArguments();
        int txtClientPhone = arg.getInt("clientPhoneMessage");

        txt = txt + ": " + txtClientPhone;

        clientPhone.setText(txt);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                // Formatea la URI de WhatsApp correctamente
                sendIntent.putExtra(Intent.EXTRA_TEXT, message.getText().toString());
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");
                startActivity(sendIntent);
            }
        });


        return view;
    }
}