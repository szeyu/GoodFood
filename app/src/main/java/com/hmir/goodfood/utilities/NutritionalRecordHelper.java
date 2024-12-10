package com.hmir.goodfood.utilities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class NutritionalRecordHelper {
    /*
      In this class, there will be multiple methods relating to nutritional record(s):

      * Note :  The following Functions 1 - 4 are mostly covered in UserHelper, so these usually are not being used / called directly from
                NutritionalRecordHelper.

      Function 1 : Fetch
                    - fetchNutritionalRecord()
                    - fetchSomeNutritionalRecords()
                    - fetchAllNutritionalRecords()

                    * method calling example
                    nutritionalRecordHelper.fetchAllNutritionalRecords()
                        .addOnSuccessListener(nutritionalRecords -> {
                            for (NutritionalRecord record : nutritionalRecords) {
                                Log.d("MainActivity", "Record: " + record.toString());
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("MainActivity", "Error fetching nutritional records: " + e.getMessage());
                        });

      Function 2 : Add
                    - addNutritionalRecord()

      Function 3 : Update
                    - updateNutritionalRecord()

      Function 4 : Delete
                    - deleteNutritionalRecord()
     */
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public NutritionalRecordHelper() {
    }

    // Function 1 : Fetch

    // Fetch specific nutritional record and return a Task of it
    public Task<NutritionalRecord> fetchNutritionalRecord(@NonNull String record_id) {
        return db.collection("nutritional_records")
                .document(record_id)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            NutritionalRecord record = documentSnapshot.toObject(NutritionalRecord.class);
                            if (record != null) {
                                // Set the record_id field to the document ID
                                record.setRecord_id(documentSnapshot.getId());
                            }
                            return record;
                        } else {
                            throw new Exception("Nutritional record not found");
                        }
                    } else {
                        throw task.getException() != null ? task.getException() :
                                new Exception("Failed to fetch nutritional record");
                    }
                });
    }

    // Fetch selected nutritional records and return a Task of a List of them
    public Task<List<NutritionalRecord>> fetchSomeNutritionalRecords(List<String> record_id) {
        if (record_id == null || record_id.isEmpty()) {
            return Tasks.forException(new Exception("record_id(s) list is null or empty"));
        }

        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (String id : record_id) {
            Task<DocumentSnapshot> task = db.collection("nutritional_records").document(id).get();
            tasks.add(task);
        }

        return Tasks.whenAllSuccess(tasks).continueWith(task -> {
            List<NutritionalRecord> records = new ArrayList<>();
            for (Object result : task.getResult()) {
                DocumentSnapshot documentSnapshot = (DocumentSnapshot) result;
                if (documentSnapshot.exists()) {
                    NutritionalRecord record = documentSnapshot.toObject(NutritionalRecord.class);
                    if (record != null) {
                        record.setRecord_id(documentSnapshot.getId());
                        records.add(record);
                    }
                }
            }
            return records;
        });
    }

    // Fetch all nutritional records with no restrictions and return a Task of a List of them
    public Task<List<NutritionalRecord>> fetchAllNutritionalRecords() {
        return db.collection("nutritional_records")
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<NutritionalRecord> records = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            if (documentSnapshot.exists()) {
                                NutritionalRecord record = documentSnapshot.toObject(NutritionalRecord.class);
                                if (record != null) {
                                    record.setRecord_id(documentSnapshot.getId());
                                    records.add(record);
                                }
                            }
                        }
                        return records;
                    } else {
                        throw task.getException() != null ? task.getException() :
                                new Exception("Failed to fetch all nutritional records");
                    }
                });
    }

    // Function 2 : Add

    // Add new nutritional record
    public void addNutritionalRecord(Map<String, Object> record, OnRecordAddedCallback callback) {
        Map<String, Object> defaultRecord = new HashMap<>();
        defaultRecord.put("calcium", 0);
        defaultRecord.put("calories", 0);
        defaultRecord.put("carbs", 0);
        defaultRecord.put("magnesium", 0);
        defaultRecord.put("cholesterol", 0);
        defaultRecord.put("date_time", null);
        defaultRecord.put("fat", 0);
        defaultRecord.put("image", null);
        defaultRecord.put("ingredients", null);
        defaultRecord.put("iron", 0);
        defaultRecord.put("potassium", 0);
        defaultRecord.put("protein", 0);
        defaultRecord.put("sodium", 0);
        defaultRecord.putAll(record);

        // Use Firestore to generate a random document ID
        db.collection("nutritional_records")
                .add(defaultRecord)
                .addOnSuccessListener(documentReference -> {
                    String newRecordId = documentReference.getId();
                    callback.onRecordAdded(newRecordId);
                })
                .addOnFailureListener(e -> {
                    callback.onError(new Exception("Error adding nutritional record", e));
                });
    }


    // Function 3 : Update

    // Update Nutritional Record
    public void updateNutritionalRecord(double calcium, double calories, double carbs, double cholesterol,
                                        double magnesium, Timestamp date_time, double fat,
                                        DocumentReference image, String ingredients, double iron,
                                        double potassium, double protein, double sodium, String record_id) {
        if (record_id == null || record_id.isEmpty()) {
            throw new IllegalArgumentException("record_id is null or empty");
        }

        Map<String, Object> recordUpdates = Map.ofEntries(
                Map.entry("calcium", calcium),
                Map.entry("calories", calories),
                Map.entry("carbs", carbs),
                Map.entry("cholesterol", cholesterol),
                Map.entry("magnesium", magnesium),
                Map.entry("date_time", date_time),
                Map.entry("fat", fat),
                Map.entry("image", image),
                Map.entry("ingredients", ingredients),
                Map.entry("iron", iron),
                Map.entry("potassium", potassium),
                Map.entry("protein", protein),
                Map.entry("sodium", sodium)
        );
        db.collection("nutritional_records")
                .document(record_id)
                .update(recordUpdates)
                .addOnSuccessListener(aVoid -> Log.d("NutritionalRecordHelper", "Record info updated"))
                .addOnFailureListener(e -> Log.e("NutritionalRecordHelper", "Error updating record info", e));
    }

    // Function 4 : Delete

    // Delete Nutritional Record
    public void deleteNutritionalRecord(String record_id) {
        if (record_id == null || record_id.isEmpty()) {
            throw new IllegalArgumentException("record_id is null or empty");
        }

        db.collection("nutritional_records")
                .document(record_id)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("NutritionalRecordHelper", "Nutritional record deleted"))
                .addOnFailureListener(e -> Log.e("NutritionalRecordHelper", "Error deleting nutritional record", e));
    }

    public interface OnRecordAddedCallback {
        void onRecordAdded(String recordId);

        void onError(Exception e);
    }
}