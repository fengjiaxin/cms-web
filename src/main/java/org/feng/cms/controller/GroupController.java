package org.feng.cms.controller;

import javax.inject.Inject;
import javax.validation.Valid;

import org.feng.basic.util.EnumUtils;
import org.feng.cms.model.Group;
import org.feng.cms.service.IGroupService;
import org.feng.cms.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value ="/admin/group")
public class GroupController {
	private IGroupService groupService;
	private IUserService userService;
	public IGroupService getgroupService() {
		return groupService;
	}
	@Inject
	public void setgroupService(IGroupService groupService) {
		this.groupService = groupService;
	}
	public IUserService getUserService() {
		return userService;
	}
	@Inject
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}
	@RequestMapping("/groups")
	public String list(Model model){
		model.addAttribute("groups", groupService.findGroup());
		return "group/list";	
	}
	
	@RequestMapping("/{id}")
	public String show(@PathVariable int id,Model model){
		model.addAttribute(groupService.load(id));
		model.addAttribute("us", userService.listGroupUsers(id));
		return "group/show";
	}
	
	@RequestMapping("/delete/{id}")
	public String delete(@PathVariable int id){
		groupService.delete(id);
		return "redirect:/admin/group/groups";
	}
	
	@RequestMapping("/clearUsers/{id}")
	public String clearUsers(@PathVariable int id){
		groupService.deleteGroupUsers(id);
		return "redirect:/admin/group/groups";
	}
	
	@RequestMapping(value = "/add",method = RequestMethod.GET)
	public String add(Model model){
		model.addAttribute(new Group());
		return "group/add";
		
	}
	@RequestMapping(value ="/add",method = RequestMethod.POST)
	public String add(@Validated Group group,BindingResult br){
		if(br.hasErrors()){
			return "group/add";
		}
		groupService.add(group);
		return "redirect:/admin/group/groups";
	}
	
	@RequestMapping(value = "/update/{id}",method = RequestMethod.GET)
	public String update(@PathVariable int id,Model model){
		model.addAttribute(groupService.load(id));
		return "group/update";
		
	}
	
	@RequestMapping(value = "/update/{id}",method = RequestMethod.POST)
	public String update(@PathVariable int id,Group group,BindingResult br){
		if(br.hasErrors()){
			return "group/update";
		}
		Group og = groupService.load(id);
		og.setName(group.getName());
		og.setDescr(group.getDescr());
		groupService.update(og);
		return "redirect:/admin/group/groups";
	}

	
}
