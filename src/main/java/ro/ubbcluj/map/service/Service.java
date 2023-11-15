package ro.ubbcluj.map.service;

import ro.ubbcluj.map.domain.Prietenie;
import ro.ubbcluj.map.domain.Utilizator;
import ro.ubbcluj.map.repository.InMemoryRepository;
import ro.ubbcluj.map.repository.Repository;
import ro.ubbcluj.map.domain.Entity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class Service {
    //private InMemoryRepository<UUID, Utilizator> utilizatorRepo;
    private Repository<UUID, Utilizator> utilizatorRepo;
    //private InMemoryRepository<UUID, Prietenie> prietenieRepo;
    private Repository<UUID, Prietenie> prietenieRepo;


    //Constructor
    // public Service(InMemoryRepository<UUID, Utilizator> utilizatorRepo, InMemoryRepository<UUID, Prietenie> prietenieRepo) {
    public Service(Repository<UUID, Utilizator> utilizatorRepo, Repository<UUID, Prietenie> prietenieRepo) {
        this.utilizatorRepo = utilizatorRepo;
        this.prietenieRepo = prietenieRepo;
    }

    //Adauga user
    public void addUser(String nume, String prenume) {
        Utilizator u = new Utilizator(nume, prenume);
        utilizatorRepo.save(u);
    }

    //Sterge user si prieteniile acestuia
    public void deleteUser(String nume, String prenume) {
        Utilizator u = getUserByNumePrenume(nume, prenume);
        if (u.getFriends().stream().anyMatch(f -> f != null)) {
            Prietenie p = getPrietenieByUser(u);
            prietenieRepo.delete(p.getId());
        }
        utilizatorRepo.delete(u.getId());
    }

    public Iterable<Utilizator> printUtilizatori() {
        return utilizatorRepo.findAll();
    }

    public List<Utilizator> printUtilizatorii() {
        ArrayList<Utilizator> lista = new ArrayList<Utilizator>();
        utilizatorRepo.findAll().forEach(lista::add);
        return lista;
    }

    public Iterable<Prietenie> printPrietenii() {
        return prietenieRepo.findAll();
    }

    public List<Prietenie> printPrieteniile() {
        ArrayList<Prietenie> lista = new ArrayList<>();
        prietenieRepo.findAll().forEach(lista::add);
        return lista;
    }

    //Adauga prietenie
    public void addPrietenie(String n1, String p1, String n2, String p2) {
        Utilizator u1 = getUserByNumePrenume(n1, p1);
        Utilizator u2 = getUserByNumePrenume(n2, p2);

        var prietenie = new Prietenie(u1, u2);
        prietenieRepo.save(prietenie);
        u1.addFriend(u2);
        u2.addFriend(u1);
    }

    public Prietenie getPrietenieByUser(Utilizator u) {
        return printPrieteniile().stream()
                .filter(p -> p.getUser1().equals(u) || p.getUser2().equals(u))
                .findFirst()
                .orElse(null);
    }

    public Utilizator getUserByNumePrenume(String nume, String prenume) {
        return printUtilizatorii().stream()
                .filter(u -> u.getFirstName().equals(nume) && u.getLastName().equals(prenume))
                .findFirst()
                .orElse(null);
    }

    //Sterge prietenie dupa nume prenume useri
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

    //Numara cate comunitati exista
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

    //Cea mai sociabila comunitate
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
        addPrietenie(u5.getFirstName(), u5.getLastName(), u3.getFirstName(), u3.getLastName());
        addPrietenie(u1.getFirstName(), u1.getLastName(), u7.getFirstName(), u7.getLastName());
        addPrietenie(u8.getFirstName(), u8.getLastName(), u9.getFirstName(), u9.getLastName());

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
}
