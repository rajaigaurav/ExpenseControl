package com.example.expensecontrol;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.MyViewHolder> {
    protected Context context;
    protected List<ExpenseModel> list;
    private OnItemsCLick onItemsClick;


    public ExpensesAdapter(Context context,OnItemsCLick onItemsClick ) {
        this.context = context;
        list = new ArrayList<>();
        this.onItemsClick = onItemsClick;
    }

    @NonNull
    @Override
    public ExpensesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.expense_row,parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpensesAdapter.MyViewHolder holder, int position) {
        ExpenseModel user = list.get(position);

        holder.noteToDisplay.setText(user.getNote());
        holder.amountToDisplay.setText(String.valueOf(user.getAmount()));
        holder.categoryToDisplay.setText(user.getCategory());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemsClick.onClick(user);
            }
        });

    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void add(ExpenseModel expenseModel) {
        list.add(expenseModel);
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView noteToDisplay, amountToDisplay, dateToDisplay, categoryToDisplay;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            noteToDisplay = itemView.findViewById(R.id.note);
            amountToDisplay = itemView.findViewById(R.id.amountToDisplay);
            dateToDisplay = itemView.findViewById(R.id.date);
            categoryToDisplay = itemView.findViewById(R.id.categoryToDisplay);
        }
    }
}

