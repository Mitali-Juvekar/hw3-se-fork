# hw1- Manual Review

The homework will be based on this project named "Expense Tracker",where users will be able to add/remove daily transaction. 

## Compile

To compile the code from terminal, use the following command:
```
cd src
javac ExpenseTrackerApp.java
java ExpenseTracker
```

You should be able to view the GUI of the project upon successful compilation. 

## Java Version
This code is compiled with ```openjdk 17.0.7 2023-04-18```. Please update your JDK accordingly if you face any incompatibility issue.

# Adding edits to the file 
Added to test file:
1. Add Transaction:
- Steps: Add a transaction with amount 50.00 and category ”food”
- Expected Output: Transaction is added to the table, Total Cost is updated
2. Invalid Input Handling:
- Steps: Attempt to add a transaction with an invalid amount or category
- Expected Output: Error messages are displayed, transactions and Total Cost remain unchanged
3. Filter by Amount:
- Steps: Add multiple transactions with different amounts, apply amount filter
- Expected Output: Only transactions matching the amount are returned (and will be highlighted)
4. Filter by Category:
- Steps: Add multiple transactions with different categories, apply category filter
- Expected Output: Only transactions matching the category are returned (and will be highlighted)
5. Undo Disallowed:
- Steps: Attempt to undo when the transactions list is empty
- Expected Output: Either UI widget is disabled or an error code (exception) is returned (thrown).
CS 520 Fall 2023. Homework 3. Implementation & Testing Page 2 of 3
6. Undo Allowed:
- Steps: Add a transaction, undo the addition
- Expected Output: Transaction is removed from the table, Total Cost is updated

Added undo functionality, when transaction list is empty undo button is dissabled