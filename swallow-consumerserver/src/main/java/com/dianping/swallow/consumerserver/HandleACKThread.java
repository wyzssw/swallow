package com.dianping.swallow.consumerserver;

import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;

import com.dianping.swallow.consumerserver.impl.ConsumerServiceImpl;

public class HandleACKThread implements Runnable{
	
	private ArrayBlockingQueue<Runnable> getAckWorker;
	
	private String consumerId;
	
	private ConsumerServiceImpl cService;
	
	private Boolean isLive = true;

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	public void setcService(ConsumerServiceImpl cService) {
		this.cService = cService;
	}

	public void setGetAckWorker(ArrayBlockingQueue<Runnable> getAckWorker) {
		this.getAckWorker = getAckWorker;
	}

	@Override
	public void run() {
		while(isLive){
			Runnable worker = null;
			try {
				worker = getAckWorker.poll(cService.getConfigManager().getFreeChannelBlockQueueOutTime(),TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			worker.run();
			synchronized(cService.getConsumerTypes()){
				HashSet<Channel> channels = cService.getChannelWorkStatue().get(consumerId);
				if(channels.isEmpty()){
					cService.getConsumerTypes().remove(consumerId);
					isLive = false;
				}
			}
		}
		
	}

}