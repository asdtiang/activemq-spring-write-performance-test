package com.zhaiyz.activemq;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * 消息生产者
 * 
 * @author zhaiyz
 */
public class Producer {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Producer.class);

	private JmsTemplate jmsTemplate;

	private Destination requestDestination;

	private static ConcurrentMap<String, AtomicLong> concurrentMap = new ConcurrentHashMap<String, AtomicLong>();

	private int sendThreads = 10;

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-jms.xml");
		try {
			Thread.sleep(Long.MIN_VALUE);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void init() {
		for (int i = 0; i < sendThreads; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					final AtomicLong count = new AtomicLong();
					concurrentMap.put(UUID.randomUUID().toString(),count);
					for (int i = 0; i < Integer.MAX_VALUE; i++) {
						jmsTemplate.send(requestDestination,
								new MessageCreator() {
									@Override
									public Message createMessage(Session session)
											throws JMSException {
										Message msg = session
												.createTextMessage("messageBody1111111111111");
										msg.setLongProperty("couny",
												count.incrementAndGet());
										return msg;
									}
								});
					}
				}
			}).start();
		}
		while (true) {
			try {
				Thread.sleep(1000);
				long count  = 0;
				for(AtomicLong single:concurrentMap.values()){
					count = single.get() +count;
					single.set(0);
				}
				LOGGER.info(count + "/s");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return the jmsTemplate
	 */
	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	/**
	 * @param jmsTemplate
	 *            the jmsTemplate to set
	 */
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	/**
	 * @return the requestDestination
	 */
	public Destination getRequestDestination() {
		return requestDestination;
	}

	/**
	 * @param requestDestination
	 *            the requestDestination to set
	 */
	public void setRequestDestination(Destination requestDestination) {
		this.requestDestination = requestDestination;
	}

	public int getSendThreads() {
		return sendThreads;
	}

	public void setSendThreads(int sendThreads) {
		this.sendThreads = sendThreads;
	}

}
