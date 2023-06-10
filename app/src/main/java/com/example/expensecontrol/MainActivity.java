package com.example.expensecontrol;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.expensecontrol.databinding.ActivityMainBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnItemsCLick{
    ActivityMainBinding binding;
    private ExpensesAdapter expensesAdapter;
    //    Intent intent;
    private long income=0,expense=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        expensesAdapter=new ExpensesAdapter(this,this);
        binding.recycler.setAdapter(expensesAdapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));


        binding.addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                intent.putExtra("type", "Income");
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please");
        progressDialog.setMessage("Wait");
        progressDialog.setCancelable(false);
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
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        income=0;expense=0;
        getData();
    }

    private void getData() {
        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .whereEqualTo("uid", FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        expensesAdapter.clear();
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();
                        List<PieEntry> pieEntryList = new ArrayList<>();
                        List<Integer> colorsList = new ArrayList<>();

                        for (DocumentSnapshot ds : dsList) {
                            ExpenseModel expenseModel = ds.toObject(ExpenseModel.class);
                            expensesAdapter.add(expenseModel);

                            if (expenseModel.getType().equals("Income")) {
                                income += expenseModel.getAmount();
                            } else {
                                expense += expenseModel.getAmount();
                            }

                            // Assign color based on category
                            int color;
                            switch (expenseModel.getCategory()) {
                                case "Food":
                                    color = getResources().getColor(R.color.colorFood);
                                    break;
                                case "Transportation":
                                    color = getResources().getColor(R.color.colorTransportation);
                                    break;
                                case "Utilities":
                                    color = getResources().getColor(R.color.colorUtilities);
                                    break;
                                case "Entertainment":
                                    color = getResources().getColor(R.color.colorEntertainment);
                                    break;
                                case "Housing":
                                    color = getResources().getColor(R.color.colorHousing);
                                    break;
                                case "Shopping":
                                    color = getResources().getColor(R.color.colorShopping);
                                    break;
                                    case "Education":
                                    color = getResources().getColor(R.color.colorEducation);
                                    break;
                                case "Healthcare":
                                    color = getResources().getColor(R.color.colorHealthcare);
                                    break;
                                case "Travel":
                                    color = getResources().getColor(R.color.colorTravel);
                                    break;
                                case "Personal Care":
                                    color = getResources().getColor(R.color.colorPersonalCare);
                                    break;






                                default:
                                    color = getResources().getColor(R.color.colorDefault);
                                    break;
                            }

                            // Add PieEntry with assigned color
                            pieEntryList.add(new PieEntry(expenseModel.getAmount(), expenseModel.getCategory()));
                            colorsList.add(color);
                        }

                        expensesAdapter.notifyDataSetChanged();
                        setUpGraph(pieEntryList, colorsList);
                    }
                });
    }


    private List<PieEntry> aggregateCategories(List<PieEntry> pieEntryList) {
        Map<String, Float> aggregatedValues = new HashMap<>();

        // Aggregate the values for each category
        for (PieEntry entry : pieEntryList) {
            String category = entry.getLabel();
            float value = entry.getValue();

            if (aggregatedValues.containsKey(category)) {
                // If the category already exists in the map, add the value to the existing total
                float currentTotal = aggregatedValues.get(category);
                aggregatedValues.put(category, currentTotal + value);
            } else {
                // If the category is new, add it to the map with the initial value
                aggregatedValues.put(category, value);
            }
        }

        // Create the aggregated pie entries
        List<PieEntry> aggregatedPieEntries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : aggregatedValues.entrySet()) {
            String category = entry.getKey();
            float totalValue = entry.getValue();
            PieEntry aggregatedEntry = new PieEntry(totalValue, category);
            aggregatedPieEntries.add(aggregatedEntry);
        }

        return aggregatedPieEntries;
    }

    private void setUpGraph(List<PieEntry> pieEntryList, List<Integer> colorsList) {
        // Aggregate the categories
        List<PieEntry> aggregatedEntries = aggregateCategories(pieEntryList);

        // Create the pie data set and customize its appearance
        PieDataSet pieDataSet = new PieDataSet(aggregatedEntries, "Total Expense");
        pieDataSet.setColors(colorsList);
        pieDataSet.setValueTextColor(getResources().getColor(R.color.white));
        pieDataSet.setValueLineColor(getResources().getColor(R.color.black));
        pieDataSet.setValueLinePart1OffsetPercentage(80f);
        pieDataSet.setValueLinePart1Length(0.2f);
        pieDataSet.setValueLinePart2Length(0.4f);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLineVariableLength(true);

        // Create the pie data and set it to the pie chart
        PieData pieData = new PieData(pieDataSet);
        binding.pieChart.setData(pieData);
        binding.pieChart.invalidate();

        // Calculate the total expense
        float totalExpense = 0;
        for (PieEntry entry : aggregatedEntries) {
            totalExpense += entry.getValue();
        }

        // Set the total expense as the center text
        String centerText = "Total\n Rs." + String.valueOf(totalExpense);
        binding.pieChart.setCenterText(centerText);
    }




    @Override
    public void onClick(ExpenseModel expenseModel) {
        Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
        intent.putExtra("model",expenseModel);
        startActivity(intent);
    }
}