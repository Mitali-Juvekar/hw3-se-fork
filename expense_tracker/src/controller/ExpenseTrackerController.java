package controller;

import view.ExpenseTrackerView;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import model.ExpenseTrackerModel;
import model.Transaction;
import model.Filter.TransactionFilter;

public class ExpenseTrackerController {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  private List<Transaction> removedTransactions = new ArrayList<>();
  /** 
   * The Controller is applying the Strategy design pattern.
   * This is the has-a relationship with the Strategy class 
   * being used in the applyFilter method.
   */
  private TransactionFilter filter;

  public ExpenseTrackerController(ExpenseTrackerModel model, ExpenseTrackerView view) {
    this.model = model;
    this.view = view;
  }

  public void setFilter(TransactionFilter filter) {
    // Sets the Strategy class being used in the applyFilter method.
    this.filter = filter;
  }

  public void refresh() {
    List<Transaction> transactions = model.getTransactions();
    view.refreshTable(transactions);
  }

  public boolean addTransaction(double amount, String category) {
    if (!InputValidation.isValidAmount(amount)) {
      return false;
    }
    if (!InputValidation.isValidCategory(category)) {
      return false;
    }
    
    Transaction t = new Transaction(amount, category);
    model.addTransaction(t);
    removedTransactions.add(t);
    view.getTableModel().addRow(new Object[]{t.getAmount(), t.getCategory(), t.getTimestamp()});
    refresh();
    return true;
  }

  public void applyFilter() {
    //null check for filter
    if(filter!=null){
      // Use the Strategy class to perform the desired filtering
      List<Transaction> transactions = model.getTransactions();
      List<Transaction> filteredTransactions = filter.filter(transactions);
      List<Integer> rowIndexes = new ArrayList<>();
      for (Transaction t : filteredTransactions) {
        int rowIndex = transactions.indexOf(t);
        if (rowIndex != -1) {
          rowIndexes.add(rowIndex);
        }
      }
      view.highlightRows(rowIndexes);
    }
    else{
      JOptionPane.showMessageDialog(view, "No filter applied");
      view.toFront();}

  }
  //new function to get removedTransaction function
 public void removeTransaction(Transaction t) {
  model.removeTransaction(t);
  int rowIndex = removedTransactions.indexOf(t);
  if (rowIndex != -1) {
    view.getTableModel().removeRow(removedTransactions.indexOf(t));
    removedTransactions.remove(t);
    refresh(); }
}


  public void undoTransaction(Transaction t) {
  // if (removedTransactions.contains(t)) {
    model.removeTransaction(t);
    view.getTableModel().addRow(new Object[]{t.getAmount(), t.getCategory(), t.getTimestamp()});
    removedTransactions.remove(t);
    refresh();
  // }
// Check if both transaction and removed transaction lists are empty
  if (model.getTransactions().isEmpty() && removedTransactions.isEmpty()) {
    view.getUndoButton().setEnabled(false);}

}
}
