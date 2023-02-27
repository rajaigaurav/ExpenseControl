package com.example.expensecontrol;

public class ExpenseModel {

    private String expenseId;
    private String note;
    private String category;
    private long amount;
    private long time;
    private String type;
    private String uid;


    public ExpenseModel() {


    }

    public ExpenseModel(String expenseId, String note, String category, long amount, long time, String type, String uid) {
        this.expenseId = expenseId;
        this.note = note;
        this.category = category;
        this.amount = amount;
        this.time = time;
        this.type = type;
        this.uid = uid;
    }


    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}