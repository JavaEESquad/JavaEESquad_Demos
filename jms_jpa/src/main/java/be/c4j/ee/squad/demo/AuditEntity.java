package be.c4j.ee.squad.demo;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.PostLoad;
import java.util.Date;

/**
 *
 */
public class AuditEntity {

    @PostLoad
    public void onRead(Object entity) {
        AbstractEntity abstractEntity = (AbstractEntity) entity;
        String msg = defineMessage(abstractEntity);
        getBeanManager().fireEvent(new ReadAuditEntity(msg));
    }

    private String defineMessage(AbstractEntity entity) {
        return "Entity  = " + entity.getClass().getSimpleName() + ", id = " + entity.getId() + ", on = " + new Date();
    }

    public static BeanManager getBeanManager() {
        try {
            InitialContext initialContext = new InitialContext();
            return (BeanManager) initialContext.lookup("java:comp/BeanManager");
        } catch (NamingException e) {
            e.printStackTrace();  // Not in production code, we are doing a demo
            throw new IllegalStateException(e);
        }
    }

    public static class ReadAuditEntity {
        private String message;

        public ReadAuditEntity(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
