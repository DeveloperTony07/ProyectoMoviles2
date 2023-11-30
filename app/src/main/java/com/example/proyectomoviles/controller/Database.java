package com.example.proyectomoviles.controller;

import com.example.proyectomoviles.model.Client;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {

    private FirebaseFirestore mFirestore;

    public Database() {
        mFirestore = FirebaseFirestore.getInstance();
    }


    //Add a lawyer
    public void postLawyer(String id, String username, String phone, String email, String password,
                           OnSuccessListener<DocumentReference> onSuccessListener,
                           OnFailureListener onFailureListener) {
        // Realiza una consulta para verificar si el ID ya existe
        mFirestore.collection("lawyer")
                .whereEqualTo("id", id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // No hay documentos con el mismo ID, procede con la inserción
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", id);
                        map.put("username", username);
                        map.put("phone", phone);
                        map.put("email", email);
                        map.put("password", password);

                        mFirestore.collection("lawyer").add(map)
                                .addOnSuccessListener(onSuccessListener)
                                .addOnFailureListener(onFailureListener);
                    } else {
                        // Ya existe un documento con el mismo ID
                        onFailureListener.onFailure(new Exception("El ID ya existe"));
                    }
                })
                .addOnFailureListener(onFailureListener);
    }//End postLawyer

    public void postAppointment(String lawyerId, String clientName, String clientID, int clientPhone, String appointmentName,
                                String appointmentType, int time, int pay,
                                OnSuccessListener<DocumentReference> onSuccessListener,
                                OnFailureListener onFailureListener) {

        // Verificar si ya existe una cita con el mismo nombre
        mFirestore.collection("appointments")
                .whereEqualTo("appointmentName", appointmentName)
                .limit(1)  // Limitar a uno, ya que debería ser único
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // No hay citas con el mismo nombre, procede con la inserción

                        // Crear un mapa con los datos de la cita
                        Map<String, Object> appointmentData = new HashMap<>();
                        appointmentData.put("lawyerId", lawyerId);
                        appointmentData.put("clientName", clientName);
                        appointmentData.put("clientID", clientID);
                        appointmentData.put("clientPhone", clientPhone);
                        appointmentData.put("appointmentName", appointmentName);
                        appointmentData.put("appointmentType", appointmentType);
                        appointmentData.put("time", time);
                        appointmentData.put("pay", pay);

                        // Utilizar el nombre de la cita como ID
                        mFirestore.collection("appointments")
                                .add(appointmentData)
                                .addOnSuccessListener(documentReference -> {
                                    // Obtener el DocumentReference del documento recién creado
                                    onSuccessListener.onSuccess(documentReference);
                                })
                                .addOnFailureListener(onFailureListener);
                    } else {
                        // Ya existe una cita con el mismo nombre
                        onFailureListener.onFailure(new Exception("Ya existe una cita con el mismo nombre"));
                    }
                })
                .addOnFailureListener(onFailureListener);
    }


    //user login
    public void loginUser(String id, String password,
                          OnSuccessListener<DocumentSnapshot> onSuccessListener,
                          OnFailureListener onFailureListener) {
        mFirestore.collection("lawyer")
                .whereEqualTo("id", id)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Usuario autenticado correctamente
                        onSuccessListener.onSuccess(queryDocumentSnapshots.getDocuments().get(0));
                    } else {
                        // No se encontró un usuario con las credenciales proporcionadas
                        onFailureListener.onFailure(new Exception("Credenciales inválidas"));
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

    public void getAllLawyers(OnSuccessListener<QuerySnapshot> onSuccessListener,
                              OnFailureListener onFailureListener) {
        mFirestore.collection("lawyer")
                .get()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void getAppointmentsByLawyer(String lawyerId,
                                        OnSuccessListener<QuerySnapshot> onSuccessListener,
                                        OnFailureListener onFailureListener) {
        mFirestore.collection("appointments")
                .whereEqualTo("lawyerId", lawyerId)
                .get()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }


    public void getAllClients(String lawyerId,
                              OnSuccessListener<List<Client>> onSuccessListener,
                              OnFailureListener onFailureListener) {
        mFirestore.collection("appointments")
                .whereEqualTo("lawyerId", lawyerId)  // Filtrar por el ID del abogado
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Client> clients = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("clientName");
                        long phone = document.getLong("clientPhone");

                        if (name != null && phone != 0) {
                            clients.add(new Client(name, (int) phone));
                        }
                    }
                    onSuccessListener.onSuccess(clients);
                })
                .addOnFailureListener(onFailureListener);
    }

    public void deleteAppointmentByName(String appointmentName,
                                        OnSuccessListener<Void> onSuccessListener,
                                        OnFailureListener onFailureListener) {
        mFirestore.collection("appointments")
                .whereEqualTo("appointmentName", appointmentName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Se encontró al menos una cita con el mismo nombre, proceder a eliminar
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String documentId = document.getId();
                            mFirestore.collection("appointments")
                                    .document(documentId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> onSuccessListener.onSuccess(null))
                                    .addOnFailureListener(onFailureListener);
                        }
                    } else {
                        // No se encontraron citas con el nombre proporcionado
                        onFailureListener.onFailure(new Exception("No se encontró ninguna cita con ese nombre"));
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

    public void updateAppointment(String oldAppointmentName, String newClientName,
                                  int newClientPhone, String newClientID,
                                  String newAppointmentType, int newTime, int newPay,
                                  OnSuccessListener<Void> onSuccessListener,
                                  OnFailureListener onFailureListener) {

        mFirestore.collection("appointments")
                .whereEqualTo("appointmentName", oldAppointmentName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Se encontró al menos una cita con el mismo nombre, proceder a actualizar
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String documentId = document.getId();
                            Map<String, Object> updatedData = new HashMap<>();
                            //updatedData.put("appointmentName", newAppointmentName);
                            updatedData.put("clientName", newClientName);
                            updatedData.put("clientPhone", newClientPhone);
                            updatedData.put("clientID", newClientID);
                            updatedData.put("appointmentType", newAppointmentType);
                            updatedData.put("time", newTime);
                            updatedData.put("pay", newPay);

                            mFirestore.collection("appointments")
                                    .document(documentId)
                                    .update(updatedData)
                                    .addOnSuccessListener(aVoid -> onSuccessListener.onSuccess(null))
                                    .addOnFailureListener(onFailureListener);
                        }
                    } else {
                        // No se encontraron citas con el nombre proporcionado
                        onFailureListener.onFailure(new Exception("No se encontró ninguna cita con ese nombre"));
                    }
                })
                .addOnFailureListener(onFailureListener);
    }


}


