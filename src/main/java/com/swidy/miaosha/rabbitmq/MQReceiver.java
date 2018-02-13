package com.swidy.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

	private static Logger logger = LoggerFactory.getLogger(MQReceiver.class);
	
	@RabbitListener(queues=MQConfig.QUEUE)
	public void receive(String message){
		logger.info("receive message:" + message);
	}
	
	@RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
	public void receiveTopic1(String message){
		logger.info("receive queue1 message:" + message);
	}
	
	@RabbitListener(queues=MQConfig.TOPIC_QUEUE2)
	public void receiveTopic2(String message){
		logger.info("receive queue2 message:" + message);
	}
	
	@RabbitListener(queues=MQConfig.HEADER_QUEUE)
	public void receiveHeader(byte[] message){
		logger.info("receive header message:" + new String(message));
	}
	
}
