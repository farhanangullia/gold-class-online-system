/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CinemaEntity;
import entity.HallEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.HallNotFoundException;

/**
 *
 * @author 
 */
@Local
public interface HallEntityControllerLocal {

    public void updateHallEntity(HallEntity hallEntity);

    public List<HallEntity> retrieveAllHalls(Long cinemaId);

    public HallEntity createHallEntity(HallEntity hallEntity, Long cinemaEntityId);

    public void deleteHall(Long hallId) throws HallNotFoundException;

    public HallEntity retrieveHallByHallId(Long hallId);

    public List<HallEntity> retrieveAllHallsForCinemaStaff();

    public void updateHallEntityWithCinemaEntity(HallEntity hallEntity, CinemaEntity cinemaEntity);

    public void updateHallHandicapSeats(HallEntity hallEntity, List<String> handicapSeats);

    public void updateHallDisabledSeats(HallEntity hallEntity, List<String> disabledSeats);

    public void removeHallDisabledSeats(HallEntity hallEntity, List<String> handicapSeats);

    public void removeHallHandicapSeats(HallEntity hallEntity, List<String> handicapSeats);
    
}
