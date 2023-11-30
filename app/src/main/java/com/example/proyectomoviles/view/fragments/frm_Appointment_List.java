package com.example.proyectomoviles.view.fragments;

import android.annotation.SuppressLint;
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
import com.example.proyectomoviles.model.Appointment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class frm_Appointment_List extends Fragment {

    ListView appointmentList;
    ArrayAdapter<Appointment> adapter;
    ArrayList<Appointment> appointments;
    Button btnBack;
    Database db;
    public frm_Appointment_List() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frm_appointment_list, container, false);

        db = new Database();

        appointmentList = view.findViewById(R.id.listVwAppointmentList);
        btnBack = view.findViewById(R.id.btnBackAppointmentList);

        Bundle arg = getArguments();
        String lawyerID = arg.getString("lawyerID");
        String lawyerUsername = arg.getString("lawyerUsername");

        // Lista para almacenar las citas
        appointments = new ArrayList<>();

        // Adaptador personalizado para el ListView
        adapter = new ArrayAdapter<Appointment>(requireContext(), android.R.layout.simple_list_item_1, appointments) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                }

                Appointment currentAppointment = getItem(position);

                if (currentAppointment != null) {
                    String appointmentInfo =
                            "LawyerID: " + currentAppointment.getLawyerId() +
                                    " \nClient name: " + currentAppointment.getClientName() +
                                    " \nClient ID: " + currentAppointment.getClientID() +
                                    " \nClient Phone: " + currentAppointment.getClientPhone() +
                                    " \nAppointment Name: " + currentAppointment.getAppointmentName() +
                                    " \nAppointment Type: " + currentAppointment.getAppointmentType() +
                                    " \nTime: " + currentAppointment.getTime() +
                                    " \nPay: " + currentAppointment.getPay();

                    ((TextView) convertView.findViewById(android.R.id.text1)).setText(appointmentInfo);
                }

                return convertView;
            }
        };

        appointmentList.setAdapter(adapter);

        Database db = new Database();
        db.getAppointmentsByLawyer(lawyerID,
                new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        appointments.clear();

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String lawyerId = document.getString("lawyerId");
                            String clientName = document.getString("clientName");
                            String clientId = document.getString("clientID");
                            int clientPhone = document.getLong("clientPhone").intValue();
                            String appointmentName = document.getString("appointmentName");
                            String appointmentType = document.getString("appointmentType");
                            long time = document.getLong("time");
                            int pay = document.getLong("pay").intValue();

                            Appointment appointment = new Appointment(
                                    lawyerId, clientName, clientId, clientPhone,
                                    appointmentName, appointmentType, (int) time, pay
                            );

                            appointments.add(appointment);
                        }

                        adapter.notifyDataSetChanged();
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (isAdded()) {
                            String errorMessage = "Error al obtener las citas: " + e.getMessage();
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        appointmentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Appointment selectedAppointment = appointments.get(position);
                frm_Appointment_Details appointmentDetailsFragment = new frm_Appointment_Details();

                Bundle args = new Bundle();
                args.putString("lawyerID", selectedAppointment.getLawyerId());
                args.putString("clientName", selectedAppointment.getClientName());
                args.putString("clientID", selectedAppointment.getClientID());
                args.putInt("clientPhone", selectedAppointment.getClientPhone());
                args.putString("appointmentName", selectedAppointment.getAppointmentName());
                args.putString("appointmentType", selectedAppointment.getAppointmentType());
                args.putInt("time", selectedAppointment.getTime());
                args.putInt("pay", selectedAppointment.getPay());

                appointmentDetailsFragment.setArguments(args);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frmLayoutHome, appointmentDetailsFragment)
                        .addToBackStack(null)
                        .commit();
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

        return view;
    }
}
