package com.sda.project.service;

import java.util.List;

import com.sda.project.model.Item;


public interface ItemService {


	
	void saveItem(Item item);
	
	void updateItem(Item item);

	void deleteItemById(int id);
	
	Item findItemById(int id);
	
	public void setEntityState(int itemId, String state);

	List<Item> findAllItems(); 
	
	boolean isItemIdUnique(int id);
}