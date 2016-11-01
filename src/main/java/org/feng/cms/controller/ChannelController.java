package org.feng.cms.controller;

import javax.inject.Inject;

import org.feng.cms.service.IChannelService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
	public String list(){
		m
		return "channel/list";
	}

}
