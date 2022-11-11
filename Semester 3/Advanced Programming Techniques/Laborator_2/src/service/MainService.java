package service;

import domain.Entity;
import domain.Friendship;
import domain.User;
import domain.validators.FriendshipValidator;
import domain.validators.UserValidator;
import domain.validators.Validator;
import exceptions.NetworkException;
import exceptions.RepositoryException;
import exceptions.ValidationException;
import network.Network;
import network.MainNetwork;
import repository.FriendshipFileRepository;
import repository.Repository;
import repository.UserFileRepository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

public class MainService implements Service {

    private final UserValidator userValidator;
    private final FriendshipValidator friendshipValidator;
    private final UserFileRepository userRepository;
    private final FriendshipFileRepository friendshipRepository;
    private final MainNetwork network;

    public MainService(
            Validator<User> validator,
            Validator<Friendship> friendshipValidator,
            Repository<Long, User> userRepository,
            Repository<Long, Friendship> friendshipRepository,
            Network network) {
        this.userValidator = (UserValidator) validator;
        this.friendshipValidator = (FriendshipValidator) friendshipValidator;
        this.userRepository = (UserFileRepository) userRepository;
        this.friendshipRepository = (FriendshipFileRepository) friendshipRepository;
        this.network = (MainNetwork) network;

        Iterable<User> users = userRepository.getAll();
        for (User user : users) {
            network.add(user);
        }

        Iterable<Friendship> friendships = friendshipRepository.getAll();
        for (Friendship friendship : friendships) {
            try {
                network.makeFriends(friendship);
            } catch (NetworkException e) {
                e.printStackTrace();
            }
        }
    }

    private Long getId(Iterable<? extends Entity<Long>> entities) {
        Set<Long> distinct = new TreeSet<>();
        long id = 1L;

        for (Entity<Long> entity : entities) {
            distinct.add(entity.getId());
        }

        while (true) {
            if (!distinct.contains(id)) {
                return id;
            }
            id = id + 1;
        }
    }

    @Override
    public void add(String firstName, String lastName) throws ValidationException, RepositoryException {
        Long id = getId(userRepository.getAll());
        User toAdd = new User(firstName, lastName);
        toAdd.setId(id);
        userValidator.validate(toAdd);
        userRepository.save(toAdd);
        network.add(toAdd);
    }

    @Override
    public void updateUser(long id, String firstName, String lastName) throws RepositoryException, ValidationException {
        User newUser = new User(firstName, lastName);
        newUser.setId(id);
        userValidator.validate(newUser);
        User oldUser = userRepository.findAfterId(id);
        userRepository.update(id, newUser);
        network.remove(oldUser);
        network.add(newUser);
    }

    @Override
    public User remove(long id) throws RepositoryException, NetworkException {
        User toDelete = userRepository.findAfterId(id);
        Vector<Friendship> userFriendships = friendshipRepository.findIdFriendships(id);
        for (Friendship friendship : userFriendships) {
            friendshipRepository.delete(friendship.getId());
            network.removeFriends(friendship);
        }
        network.remove(toDelete);
        return userRepository.delete(id);
    }

    @Override
    public void makeFriends(long id1, long id2) throws NetworkException, ValidationException, RepositoryException {
        userRepository.findAfterId(id1);
        userRepository.findAfterId(id2);
        Long id = getId(friendshipRepository.getAll());
        Friendship friendship = new Friendship(id1, id2, LocalDateTime.now());
        friendship.setId(id);
        friendshipValidator.validate(friendship);
        friendshipRepository.save(friendship);
        network.makeFriends(friendship);
    }

    @Override
    public void updateFriends(long friendshipId, long idUser1, long idUser2) throws ValidationException, RepositoryException, NetworkException {
        Friendship oldFriendship = friendshipRepository.findAfterId(friendshipId);
        Friendship newFriendship = new Friendship(idUser1, idUser2, oldFriendship.getFriendsFrom());
        newFriendship.setId(friendshipId);
        friendshipValidator.validate(newFriendship);
        friendshipRepository.update(friendshipId, newFriendship);
        network.removeFriends(oldFriendship);
        network.makeFriends(newFriendship);
    }

    @Override
    public void removeFriends(long id) throws NetworkException, RepositoryException {
        Friendship friendship = friendshipRepository.findAfterId(id);
        friendshipRepository.delete(friendship.getId());
        network.removeFriends(friendship);
    }

    @Override
    public int numberOfCommunities() {
        return network.getNumberOfCommunities();
    }

    @Override
    public Vector<User> mostPopulatedCommunity() {
        Vector<Long> communityIds = network.getMostPopulatedCommunity();
        Vector<User> community = new Vector<>();
        for (long id : communityIds) {
            try {
                community.add(userRepository.findAfterId(id));
            } catch (RepositoryException e) {
                System.out.println(e.getMessage());
            }
        }
        return community;
    }

    @Override
    public Iterable<User> getAllUsers() {
        return userRepository.getAll();
    }

    @Override
    public Iterable<Friendship> getAllFriendships() {
        return friendshipRepository.getAll();
    }

    @Override
    public int numberOfUsers() {
        return userRepository.size();
    }

    @Override
    public int numberOfFriendships() {
        return friendshipRepository.size();
    }
}
