package com.comp301.a08shopping;

public class ProductImpl implements Product {
  private final double basePrice;
  private final String name;
  private int inventory;
  private double discount;

  public ProductImpl(String name, double basePrice, int inventory) {
    if (basePrice == 0) throw new IllegalArgumentException("base price must be greater than zero.");
    this.basePrice = basePrice;
    this.name = name;
    this.inventory = inventory;
    this.discount = 0;
  }

  public ProductImpl(String name, double basePrice) {
    if (basePrice == 0) throw new IllegalArgumentException("base price must be greater than zero.");
    this.basePrice = basePrice;
    this.name = name;
    this.inventory = 0;
    this.discount = 0;
  }

  public int getStock() {
    return inventory;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public double getBasePrice() {
    return basePrice;
  }

  public double getDiscount() {
    return discount;
  }

  public void removeStock(int amount) {
    if (amount < 0) throw new IllegalArgumentException("amount cannot be less than 0");
    inventory -= amount;
  }

  public void addStock(int amount) {
    if (amount < 0) throw new IllegalArgumentException("amount cannot be less than 0");
    inventory += amount;
  }

  public void addDiscount(double amount) {
    discount = amount;
  }
}
