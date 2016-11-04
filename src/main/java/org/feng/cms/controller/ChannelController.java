package org.feng.cms.controller;

import java.util.List;

import javax.inject.Inject;

import org.feng.basic.util.EnumUtils;
import org.feng.basic.util.JsonUtil;
import org.feng.cms.dto.AjaxObj;
import org.feng.cms.model.Channel;
import org.feng.cms.model.ChannelTree;
import org.feng.cms.model.ChannelType;
import org.feng.cms.service.IChannelService;
import org.jboss.logging.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/channel")
public class ChannelController {
	private IChannelService channelService;
	
	
	public IChannelService getChannelService() {
		return channelService;
	}

	@Inject
	public void setChannelService(IChannelService channelService) {
		this.channelService = channelService;
	}


	@RequestMapping("/channels")
	public String list(Model model){
//		model.addAttribute("treeDatas", JsonUtil.getInstance().obj2json(channelService.generateTree()));
		return "channel/list";
	}
	@RequestMapping("/treeAll")
	public @ResponseBody List<ChannelTree> tree(){
		return channelService.generateTree();
	}
	
	@RequestMapping("/channels/{pid}")
	public String listChild(@PathVariable Integer pid,@Param Integer refresh,Model model) {
		Channel pc = null;
		if(refresh==null) {
			model.addAttribute("refresh",0);
		} else {
			model.addAttribute("refresh",1);
		}
		if(pid==null||pid<=0) {
			pc = new Channel();
			pc.setName(Channel.ROOT_NAME);
			pc.setId(Channel.ROOT_ID);
		} else
			pc = channelService.load(pid);
		model.addAttribute("pc", pc);
		model.addAttribute("channels",channelService.listByParent(pid));
		return "channel/list_child";
	}
	
	private void initAdd(Model model, Integer pid) {
		if(pid==null) pid = 0;
		Channel pc  = null;
		if(pid ==0){
			pc = new Channel();
			pc.setId(Channel.ROOT_ID);
			pc.setName(Channel.ROOT_NAME);
		}else{
			pc =channelService.load(pid);
		}
		model.addAttribute("pc", pc);
		model.addAttribute("types", EnumUtils.enumProp2NameMap(ChannelType.class, "name"));
	}
	
	@RequestMapping(value="/add/{pid}",method = RequestMethod.GET)
	public String add(@PathVariable Integer pid,Model model){
		initAdd(model,pid);
		model.addAttribute(new Channel());
		return "channel/add";
	}
	
	
	@RequestMapping(value = "/add/{pid}",method =RequestMethod.POST)
	public String add(@PathVariable Integer pid,Channel channel,BindingResult br,Model model){
		if(br.hasErrors()){
			initAdd(model, pid);
			return "channel/add";
		}
		channelService.add(channel, pid);
		return "redirect:/admin/channel/channels/"+pid+"?refresh=1";
	}
	
	@RequestMapping("/delete/{pid}/{id}")
	public String delete(@PathVariable Integer pid,@PathVariable Integer id,Model model){
		channelService.delete(id);
		return "redirect:/admin/channel/channels/"+pid+"?refresh=1";
		
	}
	
	@RequestMapping(value = "/update/{id}",method = RequestMethod.GET)
	public String update(@PathVariable Integer id,Model model){
		Channel c = channelService.load(id);
		model.addAttribute("channel", c);
		Channel pc = null;
		if(c.getParent()==null){
			pc = new Channel();
			pc.setId(Channel.ROOT_ID);
			pc.setName(Channel.ROOT_NAME);
		}else{
			pc = c.getParent();
		}
		model.addAttribute("pc", pc);
		model.addAttribute("types", EnumUtils.enumProp2NameMap(ChannelType.class, "name"));
		return "channel/update";
	}
	
	@RequestMapping(value = "/update/{id}",method = RequestMethod.POST)
	public String update(@PathVariable Integer id,Channel channel,BindingResult br,Model model){
		if(br.hasErrors()){
			model.addAttribute("types", EnumUtils.enumProp2NameMap(ChannelType.class, "name"));
			return "channel/update";
		}
		Channel oc = channelService.load(id);
		int pid = 0;
		if(oc.getParent()!=null)
			pid = oc.getParent().getId();
		oc.setCustomLink(channel.getCustomLink());
		oc.setCustomLinkUrl(channel.getCustomLinkUrl());
		oc.setIsIndex(channel.getIsIndex());
		oc.setIsTopNav(channel.getIsTopNav());
		oc.setName(channel.getName());
		oc.setOrders(channel.getOrders());
		oc.setRecommend(channel.getRecommend());
		oc.setStatus(channel.getStatus());
		oc.setType(channel.getType());
		channelService.update(oc);
		return "redirect:/admin/channel/channels/"+pid+"?refresh=1";
	}
	
	@RequestMapping("/channels/updateSort")
	public @ResponseBody AjaxObj updateSort(@Param Integer[] ids){
		try {
			channelService.updateSort(ids);
		} catch (Exception e) {
			return new AjaxObj(0,e.getMessage());
		}
		return new AjaxObj(1);
	}

}
