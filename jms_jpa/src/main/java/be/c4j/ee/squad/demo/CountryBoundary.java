package be.c4j.ee.squad.demo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 *
 */
public class CountryBoundary {

    @PersistenceContext
    private EntityManager em;

    public Country findCountry(String code) {
        TypedQuery<Country> query = em.createQuery("SELECT c FROM Country c WHERE c.isoCode = :code OR c.longCode = :code", Country.class);
        query.setParameter("code", code.toUpperCase());
        List<Country> resultList = query.getResultList();

        Country result = null;
        if (!resultList.isEmpty()) {
            result = resultList.get(0);
        }
        return result;
    }


}
