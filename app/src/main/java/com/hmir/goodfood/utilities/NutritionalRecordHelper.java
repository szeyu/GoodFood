package com.hmir.goodfood.utilities;

import android.util.Log;

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
      Function 1 : Fetch
                    - fetchNutritionalRecord()
                    - fetchSomeNutritionalRecords()
                    - fetchAllNutritionalRecords()

      Function 2 : Add
                    - addNutritionalRecord()

      Function 3 : Update
                    - updateNutritionalRecord()

       Function 4 : Delete
                    - deleteNutritionalRecord()
     */
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public NutritionalRecordHelper() {}

    // Function 1 : Fetch

    // Helper Method
    // Fetch specific nutritional record and return a Task of it
    private Task<NutritionalRecord> fetchNutritionalRecordTask(String record_id) {
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

    // Return NutritionalRecord object
    public NutritionalRecord fetchNutritionalRecord(String record_id) throws Exception {
        if (record_id == null || record_id.isEmpty()) {
            throw new IllegalArgumentException("record_id is null or empty");
        }

        try {
            Task<NutritionalRecord> task = fetchNutritionalRecordTask(record_id);
            return Tasks.await(task); // This blocks until the task completes
        } catch (ExecutionException | InterruptedException e) {
            throw new Exception("Error fetching nutritional record information", e);
        }
    }

    // Helper Method
    // Fetch selected nutritional records and return a Task of a List of them
    private Task<List<NutritionalRecord>> fetchSomeNutritionalRecordsTask(List<String> record_id) {
        if (record_id == null || record_id.isEmpty()) {
            return Tasks.forException(new Exception("Record IDs list is null or empty"));
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

    // Return selected NutritionalRecord objects List
    public List<NutritionalRecord> fetchSomeNutritionalRecords(List<String> record_id) throws Exception {
        if (record_id == null || record_id.isEmpty()) {
            throw new Exception("Record IDs list is null or empty");
        }

        try {
            Task<List<NutritionalRecord>> task = fetchSomeNutritionalRecordsTask(record_id);
            return Tasks.await(task);
        } catch (ExecutionException | InterruptedException e) {
            throw new Exception("Error fetching selected nutritional records", e);
        }
    }

    // Helper Method
    // Fetch all nutritional records with no restrictions and return a Task of a List of them
    private Task<List<NutritionalRecord>> fetchAllNutritionalRecordsTask() {
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

    // Return all NutritionalRecord objects List
    public List<NutritionalRecord> fetchAllNutritionalRecords() throws Exception {
        try {
            Task<List<NutritionalRecord>> task = fetchAllNutritionalRecordsTask();
            return Tasks.await(task);
        } catch (ExecutionException | InterruptedException e) {
            throw new Exception("Error fetching all nutritional records", e);
        }
    }

    // Function 2 : Add

    // Add new nutritional record
    public String addNutritionalRecord(Map<String, Object> record) throws Exception {
        Map<String, Object> defaultRecord = new HashMap<>();
        defaultRecord.put("calcium", null);
        defaultRecord.put("calories", null);
        defaultRecord.put("carbs", null);
        defaultRecord.put("magnesium", null);
        defaultRecord.put("cholesterol", null);
        defaultRecord.put("date_time", null);
        defaultRecord.put("fat", null);
        defaultRecord.put("image", null);
        defaultRecord.put("ingredients", null);
        defaultRecord.put("iron", null);
        defaultRecord.put("potassium", null);
        defaultRecord.put("protein", null);
        defaultRecord.put("sodium", null);
        defaultRecord.putAll(record);

        try {
            // Fetch the latest record ID
            Task<QuerySnapshot> queryTask = db.collection("nutritional_records")
                    .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
                    .limit(1)
                    .get();

            QuerySnapshot querySnapshot = Tasks.await(queryTask);

            // Generate the new record ID
            String newRecordId = "record-1";
            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot latestRecord = querySnapshot.getDocuments().get(0);
                String latestRecordId = latestRecord.getId();
                if (latestRecordId.startsWith("record-")) {
                    String[] parts = latestRecordId.split("-");
                    try {
                        int currentId = Integer.parseInt(parts[1]);
                        newRecordId = "record-" + (currentId + 1);
                    } catch (NumberFormatException e) {
                        Log.e("Firestore", "Failed to parse record ID: " + latestRecordId, e);
                    }
                }
            }

            // Add the new record to Firestore
            Task<Void> addTask = db.collection("nutritional_records")
                    .document(newRecordId)
                    .set(defaultRecord);

            Tasks.await(addTask);

            // Return the new record ID
            return newRecordId;

        } catch (ExecutionException | InterruptedException e) {
            throw new Exception("Error adding nutritional record", e);
        }
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
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Record info updated"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating record info", e));
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
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Nutritional record deleted"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting nutritional record", e));
    }
}