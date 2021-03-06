/**
 * 
 */
package com.eyllo.paprika.entity.jobs;

import java.util.List;

import org.apache.gora.util.GoraException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eyllo.paprika.entity.generated.PersistentEntity;
import com.eyllo.paprika.entity.store.DataLayer;
import com.eyllo.paprika.html.parser.ParseUtils;

/**
 * @author renatomarroquin
 *
 */
public class EntityKeeper {

  /**
   * Entity's store name for our data store
   */
  public static final String ENT_STORE_NAME = "entityStore";

  /**
   * Entities List
   */
  private List<PersistentEntity> entities;

  /**
   * Logger to help us write write info/debug/error messages
   */
  private static Logger LOGGER = LoggerFactory.getLogger(EntityKeeper.class);

  /**
   * @param args
   */
  public static void main(String[] args) {
    EntityKeeper entKeeper = new EntityKeeper();
    String dataStoreType = "cassandra";
    String entitiesSource = "vejaRio";
    if (args.length > 2){
        dataStoreType = args[0];
        entitiesSource = args[1];
    }
    /** Getting the entities */
    entKeeper.entities = EntityRetriever.getEntities(entitiesSource);
    /** Updates entities' geolocation */
    entKeeper.entities = EntityRetriever.updateGeoInfo(entKeeper.entities);
    /** Verifies entities' geolocation with geocoders available */
    entKeeper.entities = EntityRetriever.verifyGeoInfo(entKeeper.entities);
    //ParseUtils.printPersistentEntities(entKeeper.entities);
    //entKeeper.entities = EntityRetriever.getGeoValidatedEntities(entKeeper.entities);
    /** Saves entities within its specific store */
    //saveEntities(dataStoreType, entKeeper.entities);
    /** Verifies all entities stored */
    //verifyEntities(entKeeper.entities);
  }

    /**
     * Saves entities within a specific data store
     * @param pDataStoreType
     * @param pEntities
     * @return
     */
    public static boolean saveEntities(String pDataStoreType, List<PersistentEntity> pEntities){
      boolean flgSuccess = true;
      DataLayer<String, PersistentEntity> nKeepr = new DataLayer<String, PersistentEntity>();
      try {
        /** Creating data stores */
        nKeepr.createSpecificDataStore(ENT_STORE_NAME, pDataStoreType, String.class, PersistentEntity.class);
        /** Performing requests PUT requests */
        for (PersistentEntity pEnt : pEntities)
          nKeepr.putRequests(ENT_STORE_NAME, nKeepr.createKey(pEnt.getName().toString()), pEnt);
        //nKeepr.putRequests(ENT_STORE_NAME, nKeepr.createKey("http://www.00cafebistro.com.br"), createEntity());
        //verifyEntity(nKeepr.getRequests(ENT_STORE_NAME, nKeepr.createKey("http://www.00cafebistro.com.br")));
      } catch (GoraException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return flgSuccess;
    }

  public static void verifyEntities(List<PersistentEntity> pEntities){
    boolean flgVerif = true;
    if ( flgVerif )
      LOGGER.info("All entities were persisted and retrieved successfully");
    else
      LOGGER.info("All entities were NOT persisted and retrieved successfully");
  }
    
    public void exportToJson(String pPath){
        if (entities != null && entities.size() > 0)
            ParseUtils.writeJsonFile(entities, pPath);
        else
            LOGGER.info("Entities haven't been retrieved");
    }
}
