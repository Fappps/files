package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotUpdatedException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;

public interface IHorseService {

    /**
     * @param id of the horse to find.
     * @return the horse with the specified id.
     * @throws ServiceException  will be thrown if something goes wrong during data processing.
     * @throws NotFoundException will be thrown if the horse could not be found in the system.
     */
    Horse findOneById(Integer id) throws ServiceException, NotFoundException;

    Horse createHorse(Horse horse) throws ServiceException, NotUpdatedException, NotFoundException;

    Horse updateHorse(Integer id, Horse horse) throws NotFoundException, NotUpdatedException, ServiceException;

    void deleteHorse(Integer id) throws NotFoundException, ServiceException;

    Horse[] findHorsesFiltered(String name, String breed, Double minSpeed, Double maxSpeed) throws NotFoundException, ServiceException;

    Horse[] getAllHorses() throws NotFoundException, ServiceException;
}
