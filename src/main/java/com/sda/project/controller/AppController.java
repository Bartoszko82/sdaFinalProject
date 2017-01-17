package com.sda.project.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.omg.CORBA.DomainManagerOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sda.project.model.Item;
import com.sda.project.model.User;
import com.sda.project.service.ItemService;
//import com.sda.project.model.EmployeeService;
import com.sda.project.service.UserService;

@Controller
@RequestMapping("/")
public class AppController {

	@Autowired
	UserService userService;
	
	@Autowired
	ItemService itemService;
	
	@Autowired
	MessageSource messageSource;

	/*
	 * This method will show main page
	 */
	@RequestMapping(value = { "/", "/main"}, method = RequestMethod.GET)
	public String showMain(ModelMap model) {
		List<Item> readyItems = new ArrayList<Item>();
		List<Item> assignedItems = new ArrayList<Item>();
		List<Item> doneItems = new ArrayList<Item>();
		List<Item> items = itemService.findAllItems();
			
		for(Item i : items) {
			if("ASSIGNED".equals(i.getState())){
				assignedItems.add(i);
			} else if ("DONE".equals(i.getState())) {
				doneItems.add(i);
			} else {
				readyItems.add(i);
			}
			
		}
		model.addAttribute("assigned", assignedItems);
		model.addAttribute("done", doneItems);
		model.addAttribute("ready", readyItems);
//		model.addAttribute("items", items);
		return "main";
	}
	
	@RequestMapping(value = { "/newItem" }, method = RequestMethod.GET)
	public String newItem(ModelMap model) {
		Item item = new Item();
		
		model.addAttribute("item", item);
		model.addAttribute("edit", false);
		return "itemRegistration";
	}
	
	@RequestMapping(value = { "/newItem" }, method = RequestMethod.POST)
	public String saveItem(Item item, ModelMap model) {

		item.setState("ASSIGNED");
		itemService.saveItem(item);

		model.addAttribute("success", "Item registered successfully");
		return "success";
	}
	
	@RequestMapping(value = { "/set-{itemId}-AsReady" }, method = RequestMethod.GET)
	public String setItemToNew(ModelMap model, @PathVariable int itemId) {
		itemService.setEntityState(itemId, "READY");
		return "redirect:/main";
	}
	
	@RequestMapping(value = { "/set-{itemId}-AsAssigned" }, method = RequestMethod.GET)
	public String setItemToAssigned(ModelMap model, @PathVariable int itemId) {
		itemService.setEntityState(itemId, "ASSIGNED");
		return "redirect:/main";
	}
	
	@RequestMapping(value = { "/set-{itemId}-AsDone" }, method = RequestMethod.GET)
	public String setItemToDone(ModelMap model, @PathVariable int itemId) {
		itemService.setEntityState(itemId, "DONE");
		return "redirect:/main";
	}
	
	@RequestMapping(value = { "/edit-{itemId}-item" }, method = RequestMethod.GET)
	public String editItem(@PathVariable int itemId, ModelMap model) {
		Item item = itemService.findItemById(itemId);
		model.addAttribute("item", item);
		model.addAttribute("edit", true);
		return "itemRegistration";
	}
	
	@RequestMapping(value = { "/edit-{itemId}-item" }, method = RequestMethod.POST)
	public String updateItem(@Valid Item item, BindingResult result,
			ModelMap model, @PathVariable String itemId) {
		
		if (result.hasErrors()) {
			return "itemRegistration";
		}

//		if(!userService.isUserIdUnique(user.getUserId())){
//			FieldError idError =new FieldError("user","userId",messageSource.getMessage("non.unique.id", new Integer[]{user.getUserId()}, Locale.getDefault()));
//		    result.addError(idError);
//			return "userRegistration";
//		}

		itemService.updateItem(item);

		model.addAttribute("success", "Item " + item.getTitle()	+ " updated successfully");
		return "success";
	}
	
	@RequestMapping(value = { "/delete-{itemId}-item" }, method = RequestMethod.GET)
	public String deleteItem(@PathVariable int itemId) {
		itemService.deleteItemById(itemId);
		return "redirect:/main";
	}
	
	/*
	 * This method will list all existing users.
	 */
	@RequestMapping(value = { "/usersList" }, method = RequestMethod.GET)
	public String showUsers(ModelMap model) {
		List<User> users = userService.findAllUsers();
		model.addAttribute("users", users);
		return "usersList";
	}

	/*
	 * This method will show user details.
	 */
	@RequestMapping(value = { "/user-{userId}-info" }, method = RequestMethod.GET)
	public String showOneUser(ModelMap model, @PathVariable int userId) {
		User user = userService.findUserById(userId);
		model.addAttribute("user", user);
		return "userInfo";
	}

	/*
	 * This method will provide the medium to add a new user.
	 */
	@RequestMapping(value = { "/newUser" }, method = RequestMethod.GET)
	public String newUser(ModelMap model) {
		User user = new User();
		model.addAttribute("user", user);
		model.addAttribute("edit", false);
		return "userRegistration";
	}	
	
	/*
	 * This method will be called on form submission, handling POST request for
	 * saving employee in database. It also validates the user input
	 */
	@RequestMapping(value = { "/newUser" }, method = RequestMethod.POST)
	public String saveUser(@Valid User user, BindingResult result,
			ModelMap model) {

		if (result.hasErrors()) {
			return "userRegistration";
		}

		
		if(!userService.isUserIdUnique(user.getUserId())){
			FieldError idError = new FieldError("user","userId",messageSource.getMessage("non.unique.id", new Integer[]{user.getUserId()}, Locale.getDefault()));
		    result.addError(idError);
			return "userRegistration";
		}
		
		userService.saveUser(user);

		model.addAttribute("success", "User " + user.getLogin() + " registered successfully");
		return "success";
	}


	
	/*
	 * This method will provide the medium to update an existing employee.
	 */
	@RequestMapping(value = { "/edit-{userId}-user" }, method = RequestMethod.GET)
	public String editUser(@PathVariable int userId, ModelMap model) {
		User user = userService.findUserById(userId);
		model.addAttribute("user", user);
		model.addAttribute("edit", true);
		return "userRegistration";
	}
	
	/*
	 * This method will be called on form submission, handling POST request for
	 * updating employee in database. It also validates the user input
	 */
	@RequestMapping(value = { "/edit-{userId}-user" }, method = RequestMethod.POST)
	public String updateUser(@Valid User user, BindingResult result,
			ModelMap model, @PathVariable String userId) {
		
		if (result.hasErrors()) {
			return "userRegistration";
		}

//		if(!userService.isUserIdUnique(user.getUserId())){
//			FieldError idError =new FieldError("user","userId",messageSource.getMessage("non.unique.id", new Integer[]{user.getUserId()}, Locale.getDefault()));
//		    result.addError(idError);
//			return "userRegistration";
//		}

		userService.updateUser(user);

		model.addAttribute("success", "User " + user.getLogin()	+ " updated successfully");
		return "success";
	}
	
	/*
	 * This method will delete an employee by it's SSN value.
	 */
	@RequestMapping(value = { "/delete-{userId}-user" }, method = RequestMethod.GET)
	public String deleteUser(@PathVariable int userId) {
		userService.deleteUserById(userId);
		return "redirect:/usersList";
	}

}
