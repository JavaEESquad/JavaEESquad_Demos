package be.c4j.ee.squad.demo;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.TextMessage;

/**
 *
 */
@Stateless
public class AuditMessageProducer {

    @Resource
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/squadEE")
    private Queue queue;

    public void sendMessage(@Observes AuditEntity.ReadAuditEntity readAuditEntity) {
        try (JMSContext context = connectionFactory.createContext()) {
            TextMessage message = context.createTextMessage(readAuditEntity.getMessage());
            context.createProducer().send(queue, message);
        }
    }

}
