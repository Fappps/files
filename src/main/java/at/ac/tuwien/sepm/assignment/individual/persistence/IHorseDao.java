package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotUpdatedException;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;

public interface IHorseDao {

    /**
     * @param id of the horse to find.
     * @return the horse with the specified id.
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     * @throws NotFoundException    will be thrown if the horse could not be found in the database.
     */
    Horse findOneById(Integer id) throws PersistenceException, NotFoundException;

    Horse createHorse(Horse horse) throws PersistenceException, NotUpdatedException, NotFoundException;

    Horse updateHorse(Integer id, Horse horse) throws NotFoundException, PersistenceException, NotUpdatedException;

    void deleteHorse(Integer id) throws PersistenceException, NotFoundException;

    Horse[] findHorsesFiltered(String name, String breed, Double minSpeed, Double maxSpeed) throws PersistenceException, NotFoundException;

    Horse[] getAllHorses() throws PersistenceException, NotFoundException;
}
