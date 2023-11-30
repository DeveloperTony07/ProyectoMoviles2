package com.example.proyectomoviles.view.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectomoviles.R;
import com.example.proyectomoviles.controller.Database;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.Map;

public class frm_Appointment_Details extends Fragment {

    EditText txtClintName, txtClientPhone, txtClientID, txtAppointmentType, txtAppointmentTime, txtAppointmentPay;
    TextView txtAppointmentName;
    Button btnEdit, btnDelete, btnSendMessage;
    Database db;

    public frm_Appointment_Details() {
        // Required empty public constructor
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frm_appointment_details, container, false);
        db = new Database();
        btnDelete = view.findViewById(R.id.btnDelate);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnSendMessage = view.findViewById(R.id.btnSendMessage);

        txtAppointmentName = view.findViewById(R.id.textAppointNameDetails);
        txtClintName = view.findViewById(R.id.textClientNameDetails);
        txtClientPhone = view.findViewById(R.id.textClientPhoneDetails);
        txtClientID = view.findViewById(R.id.textClientIDDetails);
        txtAppointmentType = view.findViewById(R.id.textAppointmentTypeDetails);
        txtAppointmentTime = view.findViewById(R.id.textAppointmentTimeDetails);
        txtAppointmentPay = view.findViewById(R.id.textAppointmentPayDetails);

        // Recuperar datos del Bundle
        Bundle args = getArguments();
        String lawyerID = args.getString("lawyerID");
        String clientName = args.getString("clientName");
        String clientID = args.getString("clientID");
        int clientPhone = args.getInt("clientPhone");
        String appointmentName = args.getString("appointmentName");
        String appointmentType = args.getString("appointmentType");
        int time = args.getInt("time");
        int pay = args.getInt("pay");

        txtAppointmentName.setText(appointmentName);
        txtClintName.setText(clientName);
        txtClientID.setText(clientID);
        txtAppointmentType.setText(appointmentType);
        txtClientPhone.setText(String.valueOf(clientPhone));
        txtAppointmentTime.setText(String.valueOf(time));
        txtAppointmentPay.setText(String.valueOf(pay));

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String appointmentNameToDelete = txtAppointmentName.getText().toString();

                db.deleteAppointmentByName(appointmentNameToDelete,
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // La cita se eliminó con éxito, puedes realizar acciones adicionales si es necesario.
                                clean();
                                Toast.makeText(getContext(), "Cita eliminada con éxito", Toast.LENGTH_SHORT).show();
                                // Aquí puedes cerrar el fragmento o realizar cualquier otra acción necesaria.
                            }
                        },
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Ocurrió un error al intentar eliminar la cita.
                                Toast.makeText(getContext(), "Error al eliminar la cita: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldAppointmentName = txtAppointmentName.getText().toString();
                String newClientName = txtClintName.getText().toString();
                int newClientPhone = Integer.parseInt(txtClientPhone.getText().toString());
                String newClientID = txtClientID.getText().toString();
                String newAppointmentType = txtAppointmentType.getText().toString();
                int newTime = Integer.parseInt(txtAppointmentTime.getText().toString());
                int newPay = Integer.parseInt(txtAppointmentPay.getText().toString());

                db.updateAppointment(oldAppointmentName, newClientName,
                        newClientPhone, newClientID, newAppointmentType, newTime, newPay,
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // La cita se actualizó con éxito, puedes realizar acciones adicionales si es necesario.
                                Toast.makeText(getContext(), "Cita actualizada con éxito", Toast.LENGTH_SHORT).show();
                                // Aquí puedes cerrar el fragmento o realizar cualquier otra acción necesaria.
                            }
                        },
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Ocurrió un error al intentar actualizar la cita.
                                Toast.makeText(getContext(), "Error al actualizar la cita: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Mensaje", "Valor de clientPhone: " + clientPhone);
                Bundle bundle = new Bundle();
                bundle.putInt("clientPhoneMessage", clientPhone);

                frm_Message_Client nextFragment = new frm_Message_Client();
                nextFragment.setArguments(bundle);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frmLayoutHome, nextFragment).addToBackStack(null).commit();
            }
        });



        return view;
    }

    private void clean(){
        txtClintName.setText("");
        txtClientID.setText("");
        txtAppointmentType.setText("");
        txtClientPhone.setText(String.valueOf(""));
        txtAppointmentTime.setText(String.valueOf(""));
        txtAppointmentPay.setText(String.valueOf(""));
    }

}