package at.ac.tuwien.sepm.assignment.individual.util.mapper;

import at.ac.tuwien.sepm.assignment.individual.rest.dto.HorseDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import org.springframework.stereotype.Component;

@Component
public class HorseMapper {

    public HorseDto entityToDto(Horse horse) {
        return new HorseDto(horse.getId(), horse.getName(), horse.getBreed(), horse.getMinSpeed(), horse.getMaxSpeed(), horse.getCreated(), horse.getUpdated());
    }

    public HorseDto[] entityArrayToDtoArray(Horse[] allHorses) {
        HorseDto[] horseDtos = new HorseDto[allHorses.length];
        for (int i = 0;i<allHorses.length;i++){
            horseDtos[i]=entityToDto(allHorses[i]);
        }
        return horseDtos;
    }

}
