package com.example.expensecontrol;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.example.expensecontrol.databinding.ActivityMainBinding;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ExpensesAdapter expensesAdapter;
    RecyclerView recyclerView;
    List<ExpenseModel> expenseModelList;
    FirebaseFirestore db;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        expensesAdapter = new ExpensesAdapter(MainActivity.this, expenseModelList);


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Fetching Data...");
        recyclerView = findViewById(R.id.recyclerMain);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(expensesAdapter);

        db = FirebaseFirestore.getInstance();
        expenseModelList = new ArrayList<>();
        expensesAdapter = new ExpensesAdapter(MainActivity.this, expenseModelList);




        binding.recyclerMain.setLayoutManager(new LinearLayoutManager(this ));
        binding.recyclerMain.setAdapter(expensesAdapter);



        Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);


        binding.addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("type", "Income");
                startActivity(intent);
            }
        });

        binding.addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("type", "Expense ");
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();


        if (FirebaseAuth.getInstance().getCurrentUser()==null){
            progressDialog.show();
            FirebaseAuth.getInstance()
                    .signInAnonymously()
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            progressDialog.cancel();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.cancel();
                            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });
                    }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
    //New things added
    private void getData() {
        FirebaseFirestore
                .getInstance()
                .collection("expense")
                .whereEqualTo("uid", FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        expensesAdapter.clear();
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot ds:dsList) {
                            ExpenseModel expenseModel = ds.toObject(ExpenseModel.class);
                            expensesAdapter.add(expenseModel);
                        }
                    }
                });
    }
}
