package com.example.expensecontrol;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.expensecontrol.databinding.ActivityAddExpenseBinding;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.UUID;

public class AddExpenseActivity extends AppCompatActivity {
    ActivityAddExpenseBinding binding;
    private String type;
    private Spinner categorySpinner;
    private ArrayAdapter<String> categoryAdapter;
    private ExpenseModel expenseModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize category spinner and adapter
        categorySpinner = findViewById(R.id.categorySpinner);
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.categories));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        type=getIntent().getStringExtra("type");
        expenseModel=(ExpenseModel) getIntent().getSerializableExtra("model");

        if (type == null) {
            type = expenseModel.getType();
            binding.amount.setText(String.valueOf(expenseModel.getAmount()));
            binding.note.setText(expenseModel.getNote());

            // Set the selected category in the Spinner
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) binding.categorySpinner.getAdapter();
            int position = adapter.getPosition(expenseModel.getCategory());
            binding.categorySpinner.setSelection(position);
        }


        if (type.equals("Income")){
            binding.incomeRadio.setChecked(true);
        }else {
            binding.expenseRadio.setChecked(true);
        }

        binding.incomeRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type="Income";
            }
        });
        binding.expenseRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type="Expense";
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        if (expenseModel==null){
            menuInflater.inflate(R.menu.add_menu,menu);
        }else {
            menuInflater.inflate(R.menu.update_menu,menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id== R.id.saveExpense){
            if (type!=null){
                createExpense();
            }else {
                updateExpense();
            }
            return true;
        }
        if (id==R.id.deleteExpense){
            deleteExpense();
        }
        return false;
    }

    private void deleteExpense() {
        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseModel.getExpenseId())
                .delete();
        finish();
    }

    private void createExpense() {

        String expenseId= UUID.randomUUID().toString();
        String amount=binding.amount.getText().toString();
        String note=binding.note.getText().toString();
        String category = binding.categorySpinner.getSelectedItem().toString();



        boolean incomeChecked=binding.incomeRadio.isChecked();
        if (incomeChecked){
            type="Income";
        }else {
            type="Expense";
        }

        if (amount.trim().length()==0){
            binding.amount.setError("Empty");
            return;
        }
        ExpenseModel expenseModel=new ExpenseModel(expenseId,note,category,type,Long.parseLong(amount), Calendar.getInstance().getTimeInMillis(),
                FirebaseAuth.getInstance().getUid());

        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseId)
                .set(expenseModel);
        finish();

    }
    private void updateExpense() {
        String expenseId = expenseModel.getExpenseId();
        String amount = binding.amount.getText().toString();
        String note = binding.note.getText().toString();
        String category = binding.categorySpinner.getSelectedItem().toString();

        boolean incomeChecked = binding.incomeRadio.isChecked();
        String type = incomeChecked ? "Income" : "Expense";

        if (amount.trim().length() == 0) {
            binding.amount.setError("Empty");
            return;
        }

        ExpenseModel model = new ExpenseModel(expenseId, note, category, type, Long.parseLong(amount), expenseModel.getTime(), FirebaseAuth.getInstance().getUid());

        FirebaseFirestore.getInstance()
                .collection("expenses")
                .document(expenseId)
                .set(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddExpenseActivity.this, "Expense updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddExpenseActivity.this, "Failed to update expense: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}