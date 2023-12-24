package com.socialnetwork.socialnetwork.service;

import com.socialnetwork.socialnetwork.domain.Message;
import com.socialnetwork.socialnetwork.domain.Utilizator;
import com.socialnetwork.socialnetwork.domain.Prietenie;
import com.socialnetwork.socialnetwork.events.EventType;
import com.socialnetwork.socialnetwork.events.SocialNetworkEvent;
import com.socialnetwork.socialnetwork.events.UserChangeEvent;
import com.socialnetwork.socialnetwork.repository.Page;
import com.socialnetwork.socialnetwork.repository.Pageable;
import com.socialnetwork.socialnetwork.repository.PagingRepository;
import com.socialnetwork.socialnetwork.repository.Repository;
import com.socialnetwork.socialnetwork.utility.observer.Observable;
import com.socialnetwork.socialnetwork.utility.observer.Observer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class ServiceFX implements Observable<SocialNetworkEvent> {
    private final PagingRepository<UUID, Utilizator> utilizatorRepo;
    private final PagingRepository<UUID, Prietenie> prietenieRepo;
    private final List<Observer<SocialNetworkEvent>> observers = new ArrayList<>();
    private final Repository<UUID, Message> messageRepo;

    public void addObserver(Observer<SocialNetworkEvent> o) {
        observers.add(o);
    }

    public ServiceFX(PagingRepository<UUID, Utilizator> utilizatorRepo, PagingRepository<UUID, Prietenie> prietenieRepo, Repository<UUID, Message> messageRepo) {
        this.utilizatorRepo = utilizatorRepo;
        this.prietenieRepo = prietenieRepo;
        this.messageRepo = messageRepo;
    }

    /**
     * User functions
     */
    //Add user
    public void addUser(String nume, String prenume) {
        Utilizator u = new Utilizator(nume, prenume);
        utilizatorRepo.save(u);
        this.notify(new UserChangeEvent(EventType.ADD_USER, u, null));
    }

    //Delete user and his friendships
    public Utilizator deleteUser(String nume, String prenume) {
        Utilizator u = getUserByNumePrenume(nume, prenume);
        ArrayList<Prietenie> listaPrietenie = (ArrayList<Prietenie>) printPrieteniile();
        for (Prietenie prietenie : listaPrietenie) {
            if (prietenie.getUser1().equals(u) || prietenie.getUser2().equals(u)) {
                prietenieRepo.delete(prietenie.getId());
            }
        }
        utilizatorRepo.delete(u.getId());
        this.notify(new UserChangeEvent(EventType.REMOVE_USER, u, u));
        return u;
    }

    //Updating user's name
    public void updateUser(String nume, String prenume, String newF, String newL) {
        Utilizator u = getUserByNumePrenume(nume, prenume);
        utilizatorRepo.update(u, newF, newL);
        this.notify(new UserChangeEvent(EventType.UPDATE_USER, getUserByNumePrenume(newF, newL), null));
    }

    //Retrieves a list of user objects representing friends of the given mainFriend.
    public List<Utilizator> getUserFriendObject(Utilizator mainFriend) {
        ArrayList<Utilizator> userList = new ArrayList<>();
        Iterable<Prietenie> list;
        list = prietenieRepo.findAll();
        for (Prietenie p : list) {
            if (p.getUser1().equals(mainFriend)) {
                userList.add(p.getUser2());
            } else if (p.getUser2().equals(mainFriend))
                userList.add(p.getUser1());
        }
        return userList;
    }

    //List of all users
    public List<Utilizator> printUtilizatorii() {
        ArrayList<Utilizator> lista = new ArrayList<Utilizator>();
        utilizatorRepo.findAll().forEach(lista::add);
        return lista;
    }
    //Retrieving user object by his name
    public Utilizator getUserByNumePrenume(String nume, String prenume) {
        return printUtilizatorii().stream()
                .filter(u -> u.getFirstName().equals(nume) && u.getLastName().equals(prenume))
                .findFirst()
                .orElse(null);
    }

    /**
     * Friendship functions
     */
    //Add friendship
    public void addPrietenie(String n1, String p1, String n2, String p2) {
        Utilizator u1 = getUserByNumePrenume(n1, p1);
        Utilizator u2 = getUserByNumePrenume(n2, p2);

        var prietenie = new Prietenie(u1, u2);
        prietenieRepo.save(prietenie);
        u1.addFriend(u2);
        u2.addFriend(u1);
    }
    //Update friendship in database
    public void updateFriendship(UUID uuid) {
        prietenieRepo.acceptFriendRequest(uuid);
    }
    //List of mainFriend's friendships
    public Iterable<Prietenie> getUsersFriends(Utilizator mainFriend) {
        Iterable<Prietenie> list;
        ArrayList<Prietenie> userList = new ArrayList<>();
        list = prietenieRepo.findAll();
        for (Prietenie p : list) {
            if (p.getUser1().equals(mainFriend) || p.getUser2().equals(mainFriend)) {
                userList.add(p);
            }
        }
        return userList;
    }
    //List of all friendships
    public List<Prietenie> printPrieteniile() {
        ArrayList<Prietenie> lista = new ArrayList<>();
        prietenieRepo.findAll().forEach(lista::add);
        return lista;
    }
    //Deleting friendship by user's name
    public void removePrietenie(String n1, String p1, String n2, String p2) {
        Utilizator u1 = getUserByNumePrenume(n1, p1);
        Utilizator u2 = getUserByNumePrenume(n2, p2);
        Iterable<Prietenie> prt = prietenieRepo.findAll();
        for (Prietenie p : prt) {
            if (p.getUser1().equals(u1) && p.getUser2().equals(u2)) {
                System.out.println("Found");
                prietenieRepo.delete(p.getId());
                break;
            }
        }
    }

    /**
     * Message functions
     */
    //Add message
    public Message addMessage(String n1, String p1, String n2, String p2, String message) {
        Utilizator u1 = getUserByNumePrenume(n1, p1);
        Utilizator u2 = getUserByNumePrenume(n2, p2);
        Message message1 = new Message(u1, u2, message);
        messageRepo.save(message1);
        return message1;
    }
    //Updating message in database
    public void updateMessage(Message message) {
        messageRepo.update(message, "", "");
    }
    //List user's most recent messages
    public List<Message> getDistinctMessagesBetweenUsers(Utilizator user1) {
        List<Message> allMessages = (List<Message>) messageRepo.findAll();

        List<Message> distinctMessages = allMessages.stream()
                .filter(message -> message.getUser1().equals(user1) || message.getUser2().equals(user1))
                .collect(Collectors.groupingBy(message -> message.getOtherUser(user1)))
                .values()
                .stream()
                .map(userMessages -> userMessages.stream().max(Comparator.comparing(Message::getDate)).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return distinctMessages;
    }
    //List of messages between 2 users ordered by date
    public List<Message> getUsersOrderedMessages(Utilizator u1, Utilizator u2) {
        List<Message> allMessages = (List<Message>) messageRepo.findAll();
        List<Message> userMessages = allMessages.stream()
                .filter(message -> message.getUser1().equals(u1) && message.getUser2().equals(u2) || message.getUser2().equals(u1) && message.getUser1().equals(u2))
                .sorted(Comparator.comparing(Message::getDate))
                .toList();
        return userMessages;
    }

    /** Helping functions */
    //DFS
    private List<Utilizator> DFS(Utilizator u, Map<UUID, Boolean> set) {
        List<Utilizator> list = new ArrayList<>();
        list.add(u);
        set.put(u.getId(), true);

        for (Utilizator f : u.getFriends()) {
            if (!set.containsKey(f.getId())) {
                List<Utilizator> l = DFS(f, set);
                list.addAll(l);
            }
        }
        return list;
    }

    //Counting number of communities
    public int numberOfCommunities() {
        Iterable<Utilizator> it = utilizatorRepo.findAll();
        Map<UUID, Boolean> set = new HashMap<>();
        int count = 0;
        for (Utilizator u : it) {
            if (!set.containsKey(u.getId())) {
                ++count;
                DFS(u, set);
            }
        }
        return count;
    }

    //Most sociable community
    public Iterable<Iterable<Utilizator>> mostSociableCommunity() {
        List<Iterable<Utilizator>> list = new ArrayList<>();
        Iterable<Utilizator> it = utilizatorRepo.findAll();
        Map<UUID, Boolean> set = new HashMap<>();

        int max = -1;
        for (Utilizator u : it)
            if (!set.containsKey(u.getId())) {
                List<Utilizator> aux = DFS(u, set);
                int l = longestPath(aux);
                if (l > max) {
                    list = new ArrayList<>();
                    list.add(aux);
                    max = l;
                } else if (l == max)
                    list.add(aux);
            }

        return list;
    }

    private int longestPath(List<Utilizator> nodes) {
        int max = 0;
        for (Utilizator u : nodes) {
            int l = longestPathFromSource(u);
            if (max < l)
                max = l;
        }
        return max;
    }

    private int longestPathFromSource(Utilizator source) {
        Map<UUID, Boolean> set = new HashMap<>();
        return BFS(source, set);
    }

    private int BFS(Utilizator source, Map<UUID, Boolean> set) {
        int max = -1;
        for (Utilizator f : source.getFriends())
            if (!set.containsKey(f.getId())) {
                set.put(f.getId(), true);
                int l = BFS(f, set);
                if (l > max)
                    max = l;
                set.remove(f.getId());
            }

        return max + 1;
    }
    public Prietenie getPrietenieByUser(Utilizator u) {
        return printPrieteniile().stream()
                .filter(p -> p.getUser1().equals(u) || p.getUser2().equals(u))
                .findFirst()
                .orElse(null);
    }
    /**
     * Generating Entities
     */
    public void addEntities() {
        String[] listaNume = {"Smith", "Johnson", "Brown", "Davis", "Miller", "Wilson", "Jones", "Taylor", "Clark", "White", "Harris", "Martin", "Thompson", "Garcia", "Martinez", "Robinson", "Jackson", "Lee", "Walker", "Hall"};
        String[] listaPrenume = {"John", "Jane", "Michael", "Emily", "William", "Olivia", "Ava", "Sophia", "Liam", "Lucas", "Ethan", "Mia"};
        Random rand = new Random();

        int indexNume = rand.nextInt(listaNume.length);
        int indexPrenume = rand.nextInt(listaPrenume.length);
        Utilizator u1 = new Utilizator(listaNume[indexNume], listaPrenume[indexPrenume]);
        utilizatorRepo.save(u1);

        indexNume = rand.nextInt(listaNume.length);
        indexPrenume = rand.nextInt(listaPrenume.length);
        Utilizator u2 = new Utilizator(listaNume[indexNume], listaPrenume[indexPrenume]);
        utilizatorRepo.save(u2);

        indexNume = rand.nextInt(listaNume.length);
        indexPrenume = rand.nextInt(listaPrenume.length);
        Utilizator u3 = new Utilizator(listaNume[indexNume], listaPrenume[indexPrenume]);
        utilizatorRepo.save(u3);

        indexNume = rand.nextInt(listaNume.length);
        indexPrenume = rand.nextInt(listaPrenume.length);
        Utilizator u4 = new Utilizator(listaNume[indexNume], listaPrenume[indexPrenume]);
        utilizatorRepo.save(u4);

        indexNume = rand.nextInt(listaNume.length);
        indexPrenume = rand.nextInt(listaPrenume.length);
        Utilizator u5 = new Utilizator(listaNume[indexNume], listaPrenume[indexPrenume]);
        utilizatorRepo.save(u5);

        indexNume = rand.nextInt(listaNume.length);
        indexPrenume = rand.nextInt(listaPrenume.length);
        Utilizator u6 = new Utilizator(listaNume[indexNume], listaPrenume[indexPrenume]);
        utilizatorRepo.save(u6);

        indexNume = rand.nextInt(listaNume.length);
        indexPrenume = rand.nextInt(listaPrenume.length);
        Utilizator u7 = new Utilizator(listaNume[indexNume], listaPrenume[indexPrenume]);
        utilizatorRepo.save(u7);

        indexNume = rand.nextInt(listaNume.length);
        indexPrenume = rand.nextInt(listaPrenume.length);
        Utilizator u8 = new Utilizator(listaNume[indexNume], listaPrenume[indexPrenume]);
        utilizatorRepo.save(u8);

        indexNume = rand.nextInt(listaNume.length);
        indexPrenume = rand.nextInt(listaPrenume.length);
        Utilizator u9 = new Utilizator(listaNume[indexNume], listaPrenume[indexPrenume]);
        utilizatorRepo.save(u9);

        addPrietenie(u1.getFirstName(), u1.getLastName(), u3.getFirstName(), u3.getLastName());
        Prietenie p = getPrietenieByUser(u1);
        prietenieRepo.acceptFriendRequest(p.getId());
        p.confirmStatus();
        addPrietenie(u5.getFirstName(), u5.getLastName(), u3.getFirstName(), u3.getLastName());
        p = getPrietenieByUser(u5);
        prietenieRepo.acceptFriendRequest(p.getId());
        p.confirmStatus();
        addPrietenie(u1.getFirstName(), u1.getLastName(), u7.getFirstName(), u7.getLastName());
        p = getPrietenieByUser(u7);
        prietenieRepo.acceptFriendRequest(p.getId());
        p.confirmStatus();
        addPrietenie(u8.getFirstName(), u8.getLastName(), u9.getFirstName(), u9.getLastName());
        p = getPrietenieByUser(u9);
        prietenieRepo.acceptFriendRequest(p.getId());
        p.confirmStatus();

    }

    //10.Lista de useri cu cel putin N prieteni
    public List<Utilizator> cerinta(int n) {
        return StreamSupport.stream(utilizatorRepo.findAll().spliterator(), false)
                .filter(i -> i.getNumberOfFriends() >= n)
                .collect(Collectors.toList());

    }

    //11.Prietenii utilizator in luna specificata
    public List<Utilizator> cerinta1(String n, String p, Integer month) {
        ArrayList<Utilizator> users = new ArrayList<>();
        Iterable<Prietenie> prt = prietenieRepo.findAll();
        Utilizator u = getUserByNumePrenume(n, p);
        for (Prietenie prtt : prt) {
            if (prtt.getUser1().equals(u) && prtt.getDate().getMonth().getValue() == month) {
                users.add(prtt.getUser2());
            }
            if (prtt.getUser2().equals(u) && prtt.getDate().getMonth().getValue() == month) {
                users.add(prtt.getUser1());
            }
        }
        return users;
    }

    //12.Utilizator ce contine 'find' in firstName
    public List<Utilizator> cerinta2(String find) {
        ArrayList<Utilizator> users = new ArrayList<>();
        Iterable<Utilizator> usr = utilizatorRepo.findAll();
        for (Utilizator u : usr) {
            if (u.getFirstName().contains(find)) {
                users.add(u);
            }
        }
        return users;
    }

    @Override
    public void removeObserver(Observer<SocialNetworkEvent> observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notify(SocialNetworkEvent event) {
        this.observers.forEach(observers -> observers.update(event));
    }

    public Page<Utilizator> findAll(Pageable pageable) {
        return utilizatorRepo.findAll(pageable);
    }

}
