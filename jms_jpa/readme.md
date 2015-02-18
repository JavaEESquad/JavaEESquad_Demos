
# Log Entity reads with JMS and JPA into the database.


## Requirements


The example requires a Java EE 7 compliant application server like **GlassFish 4.x** or **WildFly 8**.

The setup described here is for GlassFish but can be adjusted for WildFly or any other compliant server.

## Setup


### Database


You need a relational database, like MySql, PostgreSQL, Oracle or whatever you prefer.

The setup for a dataSource for **MySQL** within GlassFish can be found [here](http://javaeesquad.github.io/tutorials/glassfishDatasource/glassFishDatasource.html)

The dataSource is expected with the jndi name **jdbc/squadEE**.  You can change it in the _persistence.xml_ file if you have defined another name.

The file _src/main/sql/structureWithData.sql_ contains the SQL scripts for the MySql database to create the 2 tables and the contents of the countries table.

### JMS

Java EE 7 servers have by default a connection factory defined.  So you only need to create the queue.  This can be easily done by the command line utility (_asadmin_, but you can do it also from within the web console)

```
create-jms-resource --restype javax.jms.Queue --property Name=squadEE jms/squadEE
```

The jndi name **jms/squadEE** is here important.  If you define another name, you have to change the values also in the classes _AuditMessageProducer_ and _AuditMessageConsumer_.

## Deploy and go

When you deploy the application, you can browse to the country url, and ask for the country name for a code (the 2 and 3 letter codes are supported)

for example
```
http://localhost:8080/jpa_jms/country?code=be
```

## Short explanation

### Country

JPA entity mapped to the country table.  It has the definition for the entity listener.

```java
@Entity
@Table(name="countries")
@EntityListeners(value={AuditEntity.class})
public class Country extends AbstractEntity {
}
```

### AuditEntity

Contains the method annotated with _PostLoad_ which is responsible (the first step in a long chain) for the logging of the read.

Since the injection into an JPA entityListener is not always performed correctly, the mechanism if CDI events are used.  For this purpose, the BeanManager is retrieved from JNDI and the event is fired (firing an event is easier then looking op a bean with the BeanManager. That is the reason why it is so convenient.


```java
    @PostLoad
    public void onRead(Object entity) {
        AbstractEntity abstractEntity = (AbstractEntity) entity;
        String msg = defineMessage(abstractEntity);
        getBeanManager().fireEvent(new ReadAuditEntity(msg));
    }
```

### AuditMessageProducer

Class is responsible for putting the log message on the queue.  It is a Stateless EJB bean where the _ConnectionFactory_ and the _Queue_ are injected.

CDI event observer receives the message to log.

    public void sendMessage(@Observes AuditEntity.ReadAuditEntity readAuditEntity) {
        try (JMSContext context = connectionFactory.createContext()) {
            TextMessage message = context.createTextMessage(readAuditEntity.getMessage());
            context.createProducer().send(queue, message);
        }
    }


### AuditMessageConsumer

This is the Message Driven Bean which takes each message from the queue and write it to the database. Since a MDB is an EJB, we have a JPA transaction available.

    public void onMessage(Message message) {
        try {
            String logMessage = message.getBody(String.class);

            em.persist(new LogEntity(logMessage));

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


