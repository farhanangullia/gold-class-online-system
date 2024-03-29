/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.HallEntity;
import entity.MovieEntity;
import entity.ScreeningSchedule;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author
 */
@Stateless
public class ScreeningScheduleController implements ScreeningScheduleControllerLocal {

    @EJB
    private HallEntityControllerLocal hallEntityControllerLocal;

    @EJB
    private EJBTimerSessionBeanLocal ejbTimerSessionBeanLocal;

    @PersistenceContext(unitName = "GoldClassOnline-ejbPU")
    private EntityManager em;

    public void persist(Object object) {
        em.persist(object);
    }

    @Override
    public ScreeningSchedule createScreeningSchedule(ScreeningSchedule screeningSchedule, MovieEntity movieEntity, Long hallEntityID) {
        HallEntity hallEntity = hallEntityControllerLocal.retrieveHallByHallId(hallEntityID);
        screeningSchedule.setHallEntity(hallEntity);
        hallEntity.getScreeningSchedules().add(screeningSchedule);
        screeningSchedule.setMovieEntity(movieEntity);
        movieEntity.getScreeningSchedules().add(screeningSchedule);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(screeningSchedule.getScreeningTime());
        calendar.add(Calendar.MINUTE, movieEntity.getRunningTime());
        calendar.add(Calendar.MINUTE, 20); //buffer for cleaning up the hall

        Date endTime = calendar.getTime();

        screeningSchedule.setScreeningEndTime(endTime);

        if (screeningSchedule.getScreeningTime().getDay() == 0 || screeningSchedule.getScreeningTime().getDay() == 6) {
            screeningSchedule.setPrice(new BigDecimal("15"));
        } else {
            screeningSchedule.setPrice(new BigDecimal("10"));
        }

        em.persist(screeningSchedule);
        em.flush();
        em.refresh(screeningSchedule);
        ejbTimerSessionBeanLocal.createTimer(screeningSchedule);

        return screeningSchedule;
    }

    @Override
    public void updateScreeningSchedule(ScreeningSchedule screeningSchedule, MovieEntity movieEntity, Long hallEntityID) {

        ScreeningSchedule ss = em.find(ScreeningSchedule.class, screeningSchedule.getId());
        ss.setScreeningTime(screeningSchedule.getScreeningTime());
        ss.setMovieEntity(movieEntity);
        movieEntity.getScreeningSchedules().add(ss);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ss.getScreeningTime());
        calendar.add(Calendar.MINUTE, movieEntity.getRunningTime());
        calendar.add(Calendar.MINUTE, 20); //buffer for cleaning up the hall
        Date endTime = calendar.getTime();
        ss.setScreeningEndTime(endTime);
        if (screeningSchedule.getScreeningTime().getDay() == 0 || screeningSchedule.getScreeningTime().getDay() == 6) {
            ss.setPrice(new BigDecimal("15"));
        } else {
            ss.setPrice(new BigDecimal("10"));
        }
        ejbTimerSessionBeanLocal.createTimer(ss);

    }

    @Override
    public List<ScreeningSchedule> retrieveAllScreeningSchedules(Long hallId) {
        Query query = em.createQuery("SELECT s FROM ScreeningSchedule s WHERE s.hallEntity.id = :inHallId AND s.enabled=1");
        query.setParameter("inHallId", hallId);

        return query.getResultList();
    }

    @Override
    public ScreeningSchedule retrieveScreeningScheduleById(Long ssId) {
        ScreeningSchedule ssEntity = em.find(ScreeningSchedule.class, ssId);

        return ssEntity;
    }

    @Override
    public void deleteScreeningSchedule(Long hallId) {
        ScreeningSchedule ssEntityToRemove = retrieveScreeningScheduleById(hallId);
        ssEntityToRemove.setEnabled(Boolean.FALSE);
    }

    @Override
    public List<ScreeningSchedule> retrieveAllScreeningSchedulesByMovie(Long movieId) { //how

        Query query = em.createQuery("SELECT ss FROM ScreeningSchedule ss WHERE ss.movieEntity.id=:inMovieId AND ss.enabled=1");
        query.setParameter("inMovieId", movieId);

        return query.getResultList();
    }

    @Override
    public List<ScreeningSchedule> retrieveAllScreeningSchedulesByCinemaAndMovie(Long movieId, Long cinemaId) { //how

        Query query = em.createQuery("SELECT ss FROM ScreeningSchedule ss WHERE ss.movieEntity.id=:inMovieId AND ss.hallEntity.cinemaEntity.id=:inCinemaId AND ss.enabled=1");
        query.setParameter("inMovieId", movieId);
        query.setParameter("inCinemaId", cinemaId);

        return query.getResultList();
    }

}
