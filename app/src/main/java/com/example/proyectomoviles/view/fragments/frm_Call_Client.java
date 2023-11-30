package com.example.proyectomoviles.view.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectomoviles.R;
import com.example.proyectomoviles.controller.Database;
import com.example.proyectomoviles.model.Client;
import com.example.proyectomoviles.view.items.item_Adapter_Call_List;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class frm_Call_Client extends Fragment {
    Button btnBack, btnCall;
    ListView contactList;
    //ArrayAdapter<Client> adapter;
    ArrayList<Client> clients;
    TextView txtPhoneNumber, clientName;
    item_Adapter_Call_List adapterP;
    public frm_Call_Client() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frm_call_client, container, false);
        btnBack = view.findViewById(R.id.btnBackClientContact);
        btnCall = view.findViewById(R.id.btnCall);

        contactList = view.findViewById(R.id.listVwClientContact);

        txtPhoneNumber = view.findViewById(R.id.txtPhoneNumber);
        clientName = view.findViewById(R.id.txtClientName);

        String formattedText = getString(R.string.txt_phone_number) + ": ";
        txtPhoneNumber.setText(formattedText);

        Bundle arg = getArguments();
        String lawyerID = arg.getString("lawyerID");
        String lawyerUsername = arg.getString("lawyerUsername");


        // Lista para almacenar los clientes
        clients = new ArrayList<>();

        // Adaptador para el ListView

        //adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, clients);
        //contactList.setAdapter(adapter);
        adapterP = new item_Adapter_Call_List(getContext(),clients);
        contactList.setAdapter(adapterP);

        Database db = new Database();

        db.getAllClients(lawyerID,
                new OnSuccessListener<List<Client>>() {
                    @Override
                    public void onSuccess(List<Client> clientList) {
                        // Limpiar la lista de clientes
                        clients.clear();

                        // Agregar los clientes obtenidos a la lista
                        clients.addAll(clientList);

                        // Notificar al adaptador que los datos han cambiado
                        adapterP.notifyDataSetChanged();
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar el error
                        // Puedes mostrar un mensaje de error o realizar otra acción aquí
                    }
                });

        // Agregar un listener de clics al ListView

        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el cliente seleccionado
                Client selectedClient = clients.get(position);

                // Verificar si el cliente no es nulo
                if (selectedClient != null) {
                    // Obtener el nombre y el teléfono del cliente
                    String name = selectedClient.getName();
                    String phoneNumber = String.valueOf(selectedClient.getPhone());

                    // Mostrar el nombre y el teléfono en los TextView
                    clientName.setText(name);
                    txtPhoneNumber.setText(getString(R.string.txt_phone_number) + ": " + phoneNumber);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("lawyerID", lawyerID);
                bundle.putString("lawyerUsername", lawyerUsername);

                frm_Home nextFragment = new frm_Home();
                nextFragment.setArguments(bundle);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frmLayoutHome, nextFragment).addToBackStack(null).commit();
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el texto del número de teléfono
                String phoneNumberText = txtPhoneNumber.getText().toString();

                // Verificar si hay un número de teléfono disponible
                if (!phoneNumberText.equals(getString(R.string.txt_phone_number) + ": ")) {
                    // Extraer el número de teléfono de la cadena
                    String phoneNumber = phoneNumberText.replace(getString(R.string.txt_phone_number) + ": ", "");

                    // Verificar si el número de teléfono no está vacío
                    if (!phoneNumber.isEmpty()) {
                        // Formatear el número de teléfono
                        String phoneNumberToCall = "tel:" + phoneNumber;

                        // Crear un Intent para realizar la llamada
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumberToCall));

                        // Iniciar la actividad de llamada
                        startActivity(dialIntent);
                    }
                } else {
                    // Mostrar un mensaje indicando que primero debes tocar un cliente
                    // Puedes usar Toast o cualquier otro método para mostrar el mensaje
                    // Por ejemplo:
                     Toast.makeText(requireContext(), "Primero selecciona un cliente", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return  view;
    }
}