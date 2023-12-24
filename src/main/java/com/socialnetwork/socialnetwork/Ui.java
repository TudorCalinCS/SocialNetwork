package com.socialnetwork.socialnetwork;

import com.socialnetwork.socialnetwork.domain.Utilizator;
import com.socialnetwork.socialnetwork.domain.Prietenie;

import com.socialnetwork.socialnetwork.service.ServiceFX;

import java.util.ArrayList;
import java.util.Scanner;

public class Ui {
    private ServiceFX service;

    public Ui(ServiceFX service) {
        this.service = service;
    }

    public void printMenu() {
        System.out.println("Meniu:\n0.Exit\n1.Adauga utilizator\n2.Sterge utilizator\n3.Adauga prietenie\n4.Sterge prietenie\n5.Afisare utiliatori\n6.Afisare prietenii\n7.Numar de comunitati\n8.Comunitatea cea mai sociabila\n9.Generare Utilizatori, Prietenii\n10.Lista de useri cu cel putin N prieteni\n11.Prietenii utilizator in luna specificata\n12.Utilizator ce contine input in firstName\n");
    }

    public void addUser() {
        Scanner myObj = new Scanner(System.in);
        System.out.println("\nNume: ");
        String nume = myObj.nextLine();
        System.out.println("\nPreume: ");
        String prenume = myObj.nextLine();
        service.addUser(nume, prenume);

    }

    public void deleteUser() {
        Scanner myObj = new Scanner(System.in);
        System.out.println("\nNume: ");
        String nume = myObj.nextLine();
        System.out.println("\nPreume: ");
        String prenume = myObj.nextLine();
        service.deleteUser(nume, prenume);
    }

    public void printUsers() {
        Iterable<Utilizator> users = service.printUtilizatorii();
        for (Utilizator user : users) {
            System.out.println(user.toString());
        }
    }

    public void printPrietenii() {
        Iterable<Prietenie> prietenie = service.printPrieteniile();
        for (Prietenie prt : prietenie) {
            System.out.println(prt.toString());
        }
    }

    public void addPrietenie() {
        Scanner myObj = new Scanner(System.in);
        System.out.println("\nPrieten 1");
        System.out.println("\nNume: ");
        String nume1 = myObj.nextLine();
        System.out.println("\nPreume: ");
        String prenume1 = myObj.nextLine();
        System.out.println("\nPrieten 2");
        System.out.println("\nNume: ");
        String nume2 = myObj.nextLine();
        System.out.println("\nPreume: ");
        String prenume2 = myObj.nextLine();
        service.addPrietenie(nume1, prenume1, nume2, prenume2);
    }

    public void removePrietenie() {
        Scanner myObj = new Scanner(System.in);
        System.out.println("\nPrieten 1");
        System.out.println("\nNume: ");
        String nume1 = myObj.nextLine();
        System.out.println("\nPreume: ");
        String prenume1 = myObj.nextLine();
        System.out.println("\nPrieten 2");
        System.out.println("\nNume: ");
        String nume2 = myObj.nextLine();
        System.out.println("\nPreume: ");
        String prenume2 = myObj.nextLine();
        service.removePrietenie(nume1, prenume1, nume2, prenume2);
    }

    public void numarComunitati() {
        System.out.println(service.numberOfCommunities() + "\n");
    }

    public void comunitateaSociabila() {
        System.out.println("Cea mai sociabila comunitate: " + service.mostSociableCommunity());
    }

    public void generare() {
        service.addEntities();
    }

    public void cerinta() {
        Scanner myObj = new Scanner(System.in);
        System.out.println("Numarul N:");
        int n = myObj.nextInt();
        ArrayList<Utilizator> lista = (ArrayList<Utilizator>) service.cerinta(n);
        for (Utilizator i : lista) {
            //System.out.println(i);
            System.out.println(i.toString() + " " + i.getNumberOfFriends() + " friends");
        }

    }

    public void cerinta1() {
        Scanner myObj = new Scanner(System.in);
        System.out.println("\nNume: ");
        String nume = myObj.nextLine();
        System.out.println("\nPreume: ");
        String prenume = myObj.nextLine();
        System.out.println("\nLuna: ");
        int luna = Integer.parseInt(myObj.nextLine());
        ArrayList<Utilizator> list = (ArrayList<Utilizator>) service.cerinta1(nume, prenume, luna);
        for (Utilizator i : list) {
            System.out.println(i);
        }
    }

    public void cerinta2() {
        Scanner myObj = new Scanner(System.in);
        System.out.println("\nString: ");
        String nume = myObj.nextLine();
        ArrayList<Utilizator> list = (ArrayList<Utilizator>) service.cerinta2(nume);
        for (Utilizator i : list) {
            System.out.println(i);
        }
    }

    public void start() {
        Scanner myObj = new Scanner(System.in);
        while (true) {
            printMenu();
            int cmd = myObj.nextInt();
            if (cmd == 0) {
                break;
            }
            if (cmd == 1) {
                addUser();
            }
            if (cmd == 2) {
                deleteUser();
            }
            if (cmd == 3) {
                addPrietenie();
            }
            if (cmd == 4) {
                removePrietenie();
            }
            if (cmd == 5) {
                printUsers();
            }
            if (cmd == 6) {
                printPrietenii();
            }
            if (cmd == 7) {
                numarComunitati();
            }
            if (cmd == 8) {
                comunitateaSociabila();
            }
            if (cmd == 9) {
                generare();
            }
            if (cmd == 10) {
                cerinta();
            }
            if (cmd == 11) {
                cerinta1();
            }
            if (cmd == 12) {
                cerinta2();
            }
        }
    }
}
