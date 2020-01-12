package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotUpdatedException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IHorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


@Repository
public class HorseDao implements IHorseDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(HorseDao.class);
    private final DBConnectionManager dbConnectionManager;

    @Autowired
    public HorseDao(DBConnectionManager dbConnectionManager) {
        this.dbConnectionManager = dbConnectionManager;
    }

    private static Horse dbResultToHorseDto(ResultSet result) throws SQLException {
        return new Horse(
            result.getInt("id"),
            result.getString("name"),
            result.getString("breed"),
            result.getDouble("min_speed"),
            result.getDouble("max_speed"),
            result.getTimestamp("created").toLocalDateTime(),
            result.getTimestamp("updated").toLocalDateTime());
    }


    @Override
    public Horse findOneById(Integer id) throws PersistenceException, NotFoundException {
        LOGGER.info("Get horse with id " + id);
        String sql = "SELECT * FROM Horse WHERE id=?";
        Horse horse = null;
        try {
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                horse = dbResultToHorseDto(result);
            }
        } catch (SQLException e) {
            LOGGER.error("Problem while executing SQL select statement for reading horse with id " + id, e);
            throw new PersistenceException("Could not read horses with id " + id, e);
        }
        if (horse != null) {
            return horse;
        } else {
            throw new NotFoundException("Could not find horse with id " + id);
        }
    }

    @Override
    public Horse createHorse(Horse horse) throws PersistenceException, NotUpdatedException, NotFoundException {
        LOGGER.info("Create horse " + horse);
        String sql = "INSERT INTO Horse (name,breed,min_speed,max_speed,created,updated) VALUES (?,?,?,?,?,?)";
        int result;
        int key = 0;
        try {
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, horse.getName());
            statement.setString(2, horse.getBreed());
            statement.setDouble(3, horse.getMinSpeed());
            statement.setDouble(4, horse.getMaxSpeed());
            statement.setTimestamp(5, getCurrentTimeStamp());
            statement.setTimestamp(6, getCurrentTimeStamp());
            result = statement.executeUpdate();
            ResultSet generatedKeys =statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                key = generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.error("Problem while executing SQL insert statement for creating horse " + horse, e);
            throw new PersistenceException("Could not create horses " + horse, e);
        }
        if (result == 0){
            LOGGER.error("Creating didn't work " + horse);
            throw new NotUpdatedException("Horse wasn't created" + horse);
        }else {
            return findOneById(key);
        }
    }

    @Override
    public Horse updateHorse(Integer id, Horse horse) throws NotFoundException, PersistenceException, NotUpdatedException {
        LOGGER.info("Create horse " + horse);
        Horse oldHorse = findOneById(id);
        String sql = "UPDATE Horse SET name = ?,breed = ?,min_speed = ?,max_speed = ?,updated = ? WHERE id=?";
        int result;
        int key = 0;
        try {
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            if(horse.getName()!=null){
                statement.setString(1, horse.getName());
            }else {
                statement.setString(1, oldHorse.getName());
            }
            if(horse.getBreed()!=null){
                statement.setString(2, horse.getBreed());
            }else {
                statement.setString(2, oldHorse.getBreed());
            }
            if(horse.getMinSpeed()!=null){
                statement.setObject(3, horse.getMinSpeed());
            }else {
                statement.setObject(3, oldHorse.getMinSpeed());
            }
            if(horse.getMaxSpeed()!=null){
                statement.setObject(4, horse.getMaxSpeed());
            }else {
                statement.setObject(4, oldHorse.getMaxSpeed());
            }
            statement.setTimestamp(5, getCurrentTimeStamp());
            statement.setInt(6, id);
            result = statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Problem while executing SQL insert statement for creating horse " + horse, e);
            throw new PersistenceException("Could not create horses " + horse, e);
        }
        if (result == 0){
            LOGGER.error("Creating didn't work " + horse);
            throw new NotUpdatedException("Horse wasn't created" + horse);
        }else {
            return findOneById(id);
        }
    }

    @Override
    public void deleteHorse(Integer id) throws PersistenceException, NotFoundException {
        LOGGER.info("Delete horse with id " + id);
        String sql = "DELETE FROM Horse WHERE id=?";
        int result;
        try {
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setInt(1, id);
            result = statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Problem while executing SQL select statement for reading horse with id " + id, e);
            throw new PersistenceException("Could not read horses with id " + id, e);
        }
        if (result==0) {
            throw new NotFoundException("Could not find horse with id " + id);
        }
    }

    @Override
    public Horse[] findHorsesFiltered(String name, String breed, Double minSpeed, Double maxSpeed) throws PersistenceException, NotFoundException {
        LOGGER.info("Find Horse with " + name + ", " + breed + ", " + minSpeed + ", " + maxSpeed);
        String sql = "SELECT * FROM Horse WHERE";
        if (name != null) sql+=" name LIKE ? AND";
        if (breed != null) sql+=" breed LIKE ? AND";
        if (minSpeed != null) sql+=" min_speed <= ? AND";
        if (maxSpeed != null) sql+=" max_speed <= ? AND";
        if (sql.substring(sql.length()-3,sql.length()).equals("AND"))sql= sql.substring(0,sql.length()-3);
        ArrayList<Horse> horseArrayList = new ArrayList<>();
        try {
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            int i = 1;

            if (name!=null){
                name = "%" + name +"%";
                statement.setString(i, name);
                i++;
            }
            if (breed!=null) {
                breed = "%" + breed +"%";
                statement.setString(i, breed);
                i++;
            }
            if (minSpeed!=null) {
                statement.setDouble(i, minSpeed);
                i++;
            }
            if (maxSpeed!=null){
                statement.setDouble(i, maxSpeed);
                i++;
            }
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                horseArrayList.add(dbResultToHorseDto(result));
            }
        } catch (SQLException e) {
            LOGGER.error("Problem while executing SQL select statement for reading horses", e);
            throw new PersistenceException("Could not read horses", e);
        }
        Horse[]horseArray = horseArrayList.toArray(new Horse[horseArrayList.size()]);
        if (horseArray.length==0) {
            throw new NotFoundException("Could not find horses");
        }
        return horseArray;
    }

    @Override
    public Horse[] getAllHorses() throws PersistenceException, NotFoundException {
        LOGGER.info("Get all horses");
        String sql = "SELECT * FROM Horse";
        ArrayList<Horse>horseArrayList = new ArrayList<>();
        try {
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                horseArrayList.add(dbResultToHorseDto(result));
            }
        } catch (SQLException e) {
            LOGGER.error("Problem while executing SQL select statement for reading horses", e);
            throw new PersistenceException("Could not read horses", e);
        }
        Horse[]horseArray = horseArrayList.toArray(new Horse[horseArrayList.size()]);
        if (horseArray.length==0) {
            throw new NotFoundException("Could not find horses");
        }
        return horseArray;
    }


    private static java.sql.Timestamp getCurrentTimeStamp() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());

    }
}
