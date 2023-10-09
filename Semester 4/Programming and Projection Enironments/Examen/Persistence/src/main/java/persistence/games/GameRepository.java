package persistence.games;

import model.Game;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Component
public class GameRepository implements IGameRepository {
    private static SessionFactory sessionFactory;
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    public GameRepository() {
        logger.traceEntry("Initializing Game Repository");
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();

        try {
            sessionFactory = new org.hibernate.cfg.Configuration().configure().buildSessionFactory();
        } catch (Exception e) {
            logger.error(e);
            StandardServiceRegistryBuilder.destroy(registry);
        }

        logger.traceExit();
    }

    @Override
    public Game startGame(String player, List<String> positions) {
        logger.traceEntry("Starting game");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
//                Game game = new Game(player, 0, 0, positions, new ArrayList<>());
                Game game = new Game(player, 0, 0, positions, new HashSet<>());
                session.save(game);
                transaction.commit();
                logger.traceExit("Successfully started game");
                return game;
            } catch (RuntimeException ex) {
                if (transaction != null)
                    transaction.rollback();
                logger.error("Error while starting game: " + ex);
            }
        }
        logger.traceExit("Error");
        return null;
    }

    @Override
    public void updateGame(Game game) {
        logger.traceEntry("Updating game");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.update(game);
                transaction.commit();
                logger.traceExit("Successfully updated game");
                return;
            } catch (RuntimeException ex) {
                if (transaction != null)
                    transaction.rollback();
                logger.error("Error while updating game: " + ex);
            }
        }
        logger.traceExit("Error");
    }

    @Override
    public Collection<Game> getAll() {
        logger.traceEntry("Retrieving games");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                Collection<Game> games = session.createQuery("from Game", Game.class).list();
                transaction.commit();
                logger.traceExit("Successfully retrieved games");
                return games;
            } catch (RuntimeException ex) {
                if (transaction != null)
                    transaction.rollback();
                logger.error("Error while retrieving games: " + ex);
            }
        }
        logger.traceExit("Error");
        return null;
    }

    @Override
    public Collection<Game> getByUsername(String username) {
        logger.traceEntry("Retrieving games by username");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                Collection<Game> games = session.createQuery("from Game where player = :username", Game.class)
                        .setParameter("username", username).list();
                transaction.commit();
                logger.traceExit("Successfully retrieved games by username");
                return games;
            } catch (RuntimeException ex) {
                if (transaction != null)
                    transaction.rollback();
                logger.error("Error while retrieving games by username: " + ex);
            }
        }
        logger.traceExit("Error");
        return null;
    }

    @Override
    public Game findById(int id) {
        logger.traceEntry("Finding entity with id {} ", id);

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                Game game = session.createQuery("from Game where id = :id", Game.class)
                        .setParameter("id", id)
                        .setMaxResults(1)
                        .uniqueResult();
                transaction.commit();
                logger.traceExit("Successfully found entity");
                return game;
            } catch (RuntimeException ex) {
                if (transaction != null)
                    transaction.rollback();
                logger.error("Error while finding entity: " + ex);
            }
        }

        logger.traceExit("Error");
        return null;
    }
}
