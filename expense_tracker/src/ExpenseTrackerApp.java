import javax.swing.JOptionPane;
import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import view.ExpenseTrackerView;
import model.Filter.AmountFilter;
import model.Filter.CategoryFilter;

public class ExpenseTrackerApp {

  /**
   * @param args
   */
  public static void main(String[] args) {
    
    // Create MVC components
    ExpenseTrackerModel model = new ExpenseTrackerModel();
    ExpenseTrackerView view = new ExpenseTrackerView();
    ExpenseTrackerController controller = new ExpenseTrackerController(model, view);
    

    // Initialize view
    view.setVisible(true);



    // Handle add transaction button clicks
    view.getAddTransactionBtn().addActionListener(e -> {
      // Get transaction data from view
      double amount = view.getAmountField();
      String category = view.getCategoryField();
      
      // Call controller to add transaction
      boolean added = controller.addTransaction(amount, category);
      
      if (!added) {
        JOptionPane.showMessageDialog(view, "Invalid amount or category entered");
        view.toFront();
      }
    });

      // Add action listener to the "Apply Category Filter" button
    view.addApplyCategoryFilterListener(e -> {
      try{
      String categoryFilterInput = view.getCategoryFilterInput();
      CategoryFilter categoryFilter = new CategoryFilter(categoryFilterInput);
      if (categoryFilterInput != null) {
          // controller.applyCategoryFilter(categoryFilterInput);
          controller.setFilter(categoryFilter);
          controller.applyFilter();
      }
     }catch(IllegalArgumentException exception) {
    JOptionPane.showMessageDialog(view, exception.getMessage());
    view.toFront();
   }});


    // Add action listener to the "Apply Amount Filter" button
    view.addApplyAmountFilterListener(e -> {
      try{
      double amountFilterInput = view.getAmountFilterInput();
      AmountFilter amountFilter = new AmountFilter(amountFilterInput);
      if (amountFilterInput != 0.0) {
          controller.setFilter(amountFilter);
          controller.applyFilter();
      }
    }catch(IllegalArgumentException exception) {
    JOptionPane.showMessageDialog(view,exception.getMessage());
    view.toFront();
   }});

    view.getUndoButton().addActionListener(e -> {
        try{
          int selectedRow = view.getTransactionsTable().getSelectedRow();
          if (selectedRow >= 0) {
            // view.getUndoButton().setEnabled(true);
            Transaction selectedTransaction = model.getTransactions().get(selectedRow);
            controller.undoTransaction(selectedTransaction);
      }
      }catch(IllegalArgumentException exception) {
      JOptionPane.showMessageDialog(view,"Please select transaction to be deleted");
      view.toFront();
    }});

  //  // Add action listener to the "Undo" button
  //   view.getUndoButton().addActionListener(e -> {
  //     int selectedRow = view.getTransactionsTable().getSelectedRow();
  //     if (selectedRow >= 0) {
  //       view.getUndoButton().setEnabled(true);
  //       Transaction selectedTransaction = model.getTransactions().get(selectedRow);
  //       controller.undoTransaction(selectedTransaction);
  //    }
  //    else{
  //     // view.getUndoButton().setEnabled(false);
  //     throw new IllegalArgumentException("Please select transaction to be deleted");
  //    }


  //   });  


  }
}
