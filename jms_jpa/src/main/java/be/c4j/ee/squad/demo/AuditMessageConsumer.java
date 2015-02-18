package be.c4j.ee.squad.demo;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.persistence.*;

/**
 *
 */
@MessageDriven(mappedName="jms/squadEE", activationConfig =  {
        @ActivationConfigProperty(propertyName = "acknowledgeMode",
                propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType",
                propertyValue = "javax.jms.Queue")
})
public class AuditMessageConsumer implements MessageListener {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void onMessage(Message message) {
        try {
            String logMessage = message.getBody(String.class);

            em.persist(new LogEntity(logMessage));

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Entity
    @Table(name = "audit")
    public static class LogEntity {
        @Id
        private String item;

        public LogEntity() {
        }

        public LogEntity(String item) {
            this.item = item;
        }

        public String getItem() {
            return item;
        }


    }
}
