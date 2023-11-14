// package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTextField;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import model.Filter.AmountFilter;
import model.Filter.CategoryFilter;
import view.ExpenseTrackerView;


public class TestExample {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  private ExpenseTrackerController controller;

  @Before
  public void setup() {
    model = new ExpenseTrackerModel();
    view = new ExpenseTrackerView();
    controller = new ExpenseTrackerController(model, view);
  }

    public double getTotalCost() {
        double totalCost = 0.0;
        List<Transaction> allTransactions = model.getTransactions(); // Using the model's getTransactions method
        for (Transaction transaction : allTransactions) {
            totalCost += transaction.getAmount();
        }
        return totalCost;
    }
    

    public void checkTransaction(double amount, String category, Transaction transaction) {
	assertEquals(amount, transaction.getAmount(), 0.01);
        assertEquals(category, transaction.getCategory());
        String transactionDateString = transaction.getTimestamp();
        Date transactionDate = null;
        try {
            transactionDate = Transaction.dateFormatter.parse(transactionDateString);
        }
        catch (ParseException pe) {
            pe.printStackTrace();
            transactionDate = null;
        }
        Date nowDate = new Date();
        assertNotNull(transactionDate);
        assertNotNull(nowDate);
        // They may differ by 60 ms
        assertTrue(nowDate.getTime() - transactionDate.getTime() < 60000);
    }


    @Test
    public void testAddTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add a transaction
	double amount = 50.0;
	String category = "food";
        assertTrue(controller.addTransaction(amount, category));
    
        // Post-condition: List of transactions contains only
	//                 the added transaction	
        assertEquals(1, model.getTransactions().size());
    
        // Check the contents of the list
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);
	
	// Check the total amount
        assertEquals(amount, getTotalCost(), 0.01);
    }

    @Test
    public void testAddTransactionAndUpdateTotalCost() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
        
        // Perform the action: Add a transaction
    double amount = 50.0;
    String category = "food";
        assertTrue(controller.addTransaction(amount, category));
        
        // Post-condition: List of transactions contains only the added transaction
        // assertEquals(1, model.getTransactions().size());
        assertEquals(2, view.getTableModel().getRowCount());
        
        // Check if the transaction is added to the table
    Transaction addedTransaction = model.getTransactions().get(0);
    int rowCountBefore = view.getTableModel().getRowCount();
    view.refreshTable(model.getTransactions());
    int rowCountAfter = view.getTableModel().getRowCount();
        assertEquals(rowCountBefore , rowCountAfter); // One row should be added

        /***
         * Validation
         */

        // Check the contents of the table
        assertEquals(1, view.getTransactionsTable().getValueAt(0, 0)); // Serial
        assertEquals(50.0, view.getTransactionsTable().getValueAt(0, 1)); // Amount
        assertEquals("food", view.getTransactionsTable().getValueAt(0, 2)); // Category
        
        // Check if the total cost is updated
        double expectedTotalCost = amount;
        assertEquals(expectedTotalCost, view.getTransactionsTable().getValueAt(1,3)); // Total Cost
    }


    @Test
    public void testInvalidTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        // Attempt to add a transaction with an invalid amount
        double invalidAmount = -100.0;
        String validCategory = "groceries";
        try {
            Transaction invalidTransaction = new Transaction(invalidAmount, validCategory);
            fail("Expected IllegalArgumentException for invalid amount, but no exception was thrown.");
        } catch (IllegalArgumentException e) {
            // Expected behavior
            assertEquals(0, model.getTransactions().size()); // Transactions remain unchanged
            assertEquals(0.00, getTotalCost(), 0.01); // Total Cost remains unchanged
        }

        // Attempt to add a transaction with an invalid category
        double validAmount = 20.0;
        String invalidCategory = "invalid_category123";
        try {
            Transaction invalidTransaction = new Transaction(validAmount, invalidCategory);
            fail("Expected IllegalArgumentException for invalid category, but no exception was thrown.");
        } catch (IllegalArgumentException e) {
            // Expected behavior
            assertEquals(0, model.getTransactions().size()); // Transactions remain unchanged
            assertEquals(0.00, getTotalCost(), 0.01); // Total Cost remains unchanged
        }

        // Attempt to add a transaction with an invalid amount and category
        double invalidAmount1 = -20.0;
        String invalidCategory1 = "invalid_category123";
        try {
            Transaction invalidTransaction = new Transaction(invalidAmount1, invalidCategory1);
            fail("Expected IllegalArgumentException for invalid category, but no exception was thrown.");
        } catch (IllegalArgumentException e) {
            // Expected behavior
            assertEquals(0, model.getTransactions().size()); // Transactions remain unchanged
            assertEquals(0.00, getTotalCost(), 0.01); // Total Cost remains unchanged
        }
}


    @Test
        public void testUndoAllowed() {
            // Pre-condition: List of transactions is empty
            assertEquals(0, view.getTableModel().getRowCount());

            // Perform the action: Add a transaction
            double amount = 50.0;
            String category = "food";
            assertTrue(controller.addTransaction(amount, category));

            // Post-condition: List of transactions contains only the added transaction
            assertEquals(2, view.getTableModel().getRowCount());


            // Check if the total cost is updated
            double expectedTotalCost = amount;
            assertEquals(expectedTotalCost, view.getTransactionsTable().getValueAt(1, 3));

            // Perform the action: Undo the addition
            int rowCountBeforeUndo = view.getTableModel().getRowCount(); //2

            Transaction addedTransaction = model.getTransactions().get(0); //?????????????????????
	        // checkTransaction(amount, category, addedTransaction);
        
            controller.undoTransaction(addedTransaction);
            /***
         * Validation
         */

            // Post-condition: List of transactions is empty
            assertEquals(1, view.getTableModel().getRowCount());

            // Check if the transaction is removed from the table
            int rowCountAfterUndo = view.getTableModel().getRowCount();
            assertEquals(rowCountBeforeUndo-1, rowCountAfterUndo); // Table should be back to its original state

            // Check if the total cost is updated after undo
            double totalCostAfterUndo = Double.parseDouble(view.getTransactionsTable().getValueAt(0, 3).toString());
            assertEquals(0.00, totalCostAfterUndo, 0.01);

            // double totalCost = getTotalCost();
            // assertEquals(0.00, totalCost, 0.01);
           
        }



    @Test
    public void testRemoveTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add and remove a transaction
	double amount = 50.0;
	String category = "food";
        Transaction addedTransaction = new Transaction(amount, category);
        model.addTransaction(addedTransaction);
    
        // Pre-condition: List of transactions contains only
	//                the added transaction
        assertEquals(1, model.getTransactions().size());
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);

	assertEquals(amount, getTotalCost(), 0.01);
	
	// Perform the action: Remove the transaction
        model.removeTransaction(addedTransaction);

         /***
         * Validation
         */
    
        // Post-condition: List of transactions is empty
        List<Transaction> transactions = model.getTransactions();
        assertEquals(0, transactions.size());
    
        // Check the total cost after removing the transaction
        double totalCost = getTotalCost();
        assertEquals(0.00, totalCost, 0.01);
    }

    @Test
    public void testUndoDisallowed() {
        // Pre condition: Initial rows in the table should be zero
        assertEquals(0, model.getTransactions().size());
        assertEquals(0.00, getTotalCost(), 0.01);

        JButton undoButton = view.getUndoButton();
        assertFalse("Undo button should be disabled when transactions list is empty", undoButton.isEnabled());

        double amount = 50.0;
	    String category = "food";
        Transaction addedTransaction = new Transaction(amount, category);
        model.addTransaction(addedTransaction);
    
        // Pre-condition: List of transactions contains only
	//                the added transaction
        assertEquals(1, model.getTransactions().size());
	    Transaction t = model.getTransactions().get(0);

        //Performing Undo expectation is to catch the exception
        // Transaction t = model.getTransactions().get(0);
        model.removeTransaction(t);

         /***
         * Validation
         */
    
        //Number of transactions should be 0
        assertEquals(0, model.getTransactions().size());

	    //Total amount should be 0
        // assertEquals(0.00, getTotalCost(), 0.01);
        JButton undoButton1 = view.getUndoButton();
        assertFalse("Undo button should be disabled when transactions list is empty", undoButton1.isEnabled());
    }



    @Test
    public void filterByAmount() {
        // Pre condition: Initial rows in the table should be zero
        assertEquals(0, model.getTransactions().size());

        //Adding multiple items to the table
        double[] amount = {10, 200, 10, 30};
        String[] category = {"food", "travel", "bills", "food"};

        for(int i=0; i<4;i++)
            assertTrue(controller.addTransaction(amount[i], category[i]));

        //Filtering by amount=10.0 We should see only 2 rows with amount as 10
        AmountFilter amountFilter = new AmountFilter(10.0);
        List<Transaction> filteredList = amountFilter.filter(model.getTransactions());
        controller.setFilter(amountFilter);
        controller.applyFilter();

        /***
         * Validation
         */

        //Number of Transactions in the filtered list should only be 2
        assertEquals(2,filteredList.size());

        //Amount should be 10.00 for both the transactions
        for(int i=0;i<2;i++){
            Transaction t = filteredList.get(i);
            assertEquals(10.00, t.getAmount(), 0.01);
        }
    }

    @Test
    public void filterByCategory() {
        // Pre condition: Initial rows in the table should be zero
        assertEquals(0, model.getTransactions().size());

        //Adding multiple items to the table
        double[] amount = {10, 100, 10, 50};
        String[] category = {"food", "food", "travel", "bills"};

        for(int i=0; i<4;i++)
            assertTrue(controller.addTransaction(amount[i], category[i]));

        //Filtering by category=food We should see only 2 rows with category as food
        CategoryFilter categoryFilter = new CategoryFilter("food");
        List<Transaction> filteredList = categoryFilter.filter(model.getTransactions());
        controller.setFilter(categoryFilter);
        controller.applyFilter();

        /***
         * Validation
         */

        //Number of Transactions in the filtered list should only be 2
        assertEquals(2,filteredList.size());

        //Category should be "food" for both the transactionss
        for(int i=0;i<2;i++){
            Transaction t = filteredList.get(i);
            assertEquals("food", t.getCategory());
        }
    }

    
    
}
