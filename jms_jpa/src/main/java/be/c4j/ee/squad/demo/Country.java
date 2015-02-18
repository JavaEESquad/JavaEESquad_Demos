package be.c4j.ee.squad.demo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Is only read from DB. So no GeneratedValue and Equals/Hashcode based on the PK field.
 */
@Entity
@Table(name="countries")
@EntityListeners(value={AuditEntity.class})
public class Country extends AbstractEntity {

    @Id
    private String isoCode;
    @Column
    private String longCode;
    @Column
    private String description;

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getLongCode() {
        return longCode;
    }

    public void setLongCode(String longCode) {
        this.longCode = longCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Serializable getId() {
        return isoCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Country)) {
            return false;
        }

        Country country = (Country) o;

        if (!isoCode.equals(country.isoCode)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return isoCode.hashCode();
    }
}
