package com.comp301.a08shopping;

import com.comp301.a08shopping.events.*;
import com.comp301.a08shopping.exceptions.OutOfStockException;
import com.comp301.a08shopping.exceptions.ProductNotFoundException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class StoreImpl implements Store {
  private final String storeName;
  private final List<StoreObserver> storeObservers;
  private final List<ProductImpl> products;

  public StoreImpl(String name) {
    if (name == null) throw new IllegalArgumentException("name cannot be empty");
    this.storeName = name;
    this.storeObservers = new ArrayList<>();
    this.products = new ArrayList<>();
  }

  @Override
  public String getName() {
    return storeName;
  }

  @Override
  public void addObserver(StoreObserver observer) {
    if (observer == null) throw new IllegalArgumentException("observer cannot be null");
    storeObservers.add(observer);
  }

  @Override
  public void removeObserver(StoreObserver observer) {
    if (observer == null) throw new IllegalArgumentException("observer cannot be null");
    if (storeObservers.contains(observer)) storeObservers.remove(observer);
  }

  @Override
  public List<Product> getProducts() {
    return new ArrayList<>(products);
  }

  @Override
  public Product createProduct(String name, double basePrice, int inventory) { // diff
    if (name == null || basePrice <= 0 || inventory < 0)
      throw new IllegalArgumentException("name cannot be empty");
    Product product = new ProductImpl(name, basePrice, inventory);
    products.add((ProductImpl) product);
    return product;
  }

  @Override
  public ReceiptItem purchaseProduct(Product product) {
    if (product == null) throw new IllegalArgumentException("product cannot be null");
    if (!products.contains(product)) throw new ProductNotFoundException("product cannot be found");
    int index = products.indexOf(product);
    if (products.get(index).getStock() == 0)
      throw new OutOfStockException("product is out of stock");
    double basePrice = getIsOnSale(product) ? getSalePrice(product) : product.getBasePrice();
    products.get(index).removeStock(1);
    ReceiptItem recipientItem = new ReceiptItemImpl(product.getName(), basePrice, storeName);
    if (products.get(index).getStock() <= 0) {
      OutOfStockEvent outOfStockEvent = new OutOfStockEvent(product, this);
      for (StoreObserver storeObserver : storeObservers) storeObserver.update(outOfStockEvent);
    } else {
      PurchaseEvent purchaseEvent = new PurchaseEvent(product, this);
      for (StoreObserver storeObserver : storeObservers) storeObserver.update(purchaseEvent);
    }
    return recipientItem;
  }

  @Override
  public void restockProduct(Product product, int numItems) {
    if (product == null || numItems < 0)
      throw new IllegalArgumentException("product cannot be null and items must be at leat 1");
    if (!products.contains(product)) throw new ProductNotFoundException("product cannot be found");
    int index = products.indexOf(product);
    if (products.get(index).getStock() == 0) {
      BackInStockEvent backInStockEvent = new BackInStockEvent(product, this);
      for (StoreObserver storeObserver : storeObservers) storeObserver.update(backInStockEvent);
    }
    products.get(index).addStock(numItems);
  }

  @Override
  public void startSale(Product product, double percentOff) {
    if (product == null || percentOff < 0 || percentOff > 1.0)
      throw new IllegalArgumentException("invalid product or discount");
    if (!products.contains(product)) throw new ProductNotFoundException("product cannot be found");
    products.get(products.indexOf(product)).addDiscount(percentOff);
    SaleStartEvent saleStartEvent = new SaleStartEvent(product, this);
    for (StoreObserver storeObserver : storeObservers) storeObserver.update(saleStartEvent);
  }

  @Override
  public void endSale(Product product) {
    if (product == null) throw new IllegalArgumentException("product cannot be null");
    if (!products.contains(product)) throw new ProductNotFoundException("product cannot be found");
    SaleEndEvent saleEndEvent = new SaleEndEvent(product, this);
    products.get(products.indexOf(product)).addDiscount(0);
    for (StoreObserver storeObserver : storeObservers) storeObserver.update(saleEndEvent);
  }

  @Override
  public int getProductInventory(Product product) {
    if (product == null) throw new IllegalArgumentException("product cannot be null");
    if (!products.contains(product)) throw new ProductNotFoundException("product cannot be found");
    return products.get(products.indexOf(product)).getStock();
  }

  @Override
  public boolean getIsInStock(Product product) {
    if (product == null) throw new IllegalArgumentException("product cannot be null");
    if (!products.contains(product)) throw new ProductNotFoundException("product not found");
    int index = products.indexOf(product);
    return products.get(index).getStock() > 0;
  }

  @Override
  public double getSalePrice(Product product) {
    if (product == null) throw new IllegalArgumentException("product cannot be null");
    if (!products.contains(product)) throw new ProductNotFoundException("product cannot be found");
    int index = products.indexOf(product);
    double discount = products.get(index).getDiscount();
    double price = products.get(index).getBasePrice();
    return Math.round((price * (1.0 - discount)) * 100.0) / 100.0;
  }

  @Override
  public boolean getIsOnSale(Product product) {
    if (product == null) throw new IllegalArgumentException("product cannot be null");
    if (!products.contains(product)) throw new ProductNotFoundException("product cannot be found");
    return products.get(products.indexOf(product)).getDiscount() > 0;
  }
}
