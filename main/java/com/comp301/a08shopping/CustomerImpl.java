package com.comp301.a08shopping;

import com.comp301.a08shopping.events.*;

import java.util.ArrayList;
import java.util.List;

public class CustomerImpl implements Customer {
  private final String name;
  private double budget;
  private final List<ReceiptItem> receiptItems;

  public CustomerImpl(String name, double budget) {
    if (name == null || budget < 0)
      throw new IllegalArgumentException(
          "name must not be empty and budget should be greater than zero");
    this.name = name;
    this.budget = budget;
    this.receiptItems = new ArrayList<>();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public double getBudget() {
    return budget;
  }

  @Override
  public void purchaseProduct(Product product, Store store) {
    if (product == null || store == null)
      throw new IllegalArgumentException("product or store cannot be null");
    if (product.getBasePrice() > budget)
      throw new IllegalStateException("product price is beyond budgetde");
    budget -= store.getSalePrice(product);
    receiptItems.add(store.purchaseProduct(product));
  }

  @Override
  public List<ReceiptItem> getPurchaseHistory() {
    return receiptItems;
  }

  @Override
  public void update(StoreEvent event) {
    String product = event.getProduct().getName();
    String store = event.getStore().getName();
    if (event instanceof BackInStockEvent)
      System.out.println(product + " is back in stock at " + store);
    if (event instanceof OutOfStockEvent)
      System.out.println(product + " is now out of stock at " + store);
    if (event instanceof PurchaseEvent)
      System.out.println("Someone purchased " + product + " at " + store);
    if (event instanceof SaleStartEvent)
      System.out.println("New sale for " + product + " at " + store + "!");
    if (event instanceof SaleEndEvent)
      System.out.println("The sale for " + product + " at " + store + " has ended");
  }
}
