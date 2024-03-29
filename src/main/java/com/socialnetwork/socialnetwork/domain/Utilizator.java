package com.socialnetwork.socialnetwork.domain;


import java.util.*;

public class Utilizator extends Entity<UUID> {
    private String firstName;
    private String lastName;
    private Map<UUID, Utilizator> friends;
    private String password;

    public Utilizator(String firstName, String lastName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.friends = new HashMap<>();
        this.setId(UUID.randomUUID());
    }
    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password=this.getPassword();
        this.friends = new HashMap<>();
        this.setId(UUID.randomUUID());
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getNumberOfFriends() {
        return this.friends.size();
    }

    public List<Utilizator> getFriends() {
        return new ArrayList<>(this.friends.values());
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Utilizator{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }

    public void addFriend(Utilizator u) {
        this.friends.put(u.getId(), u);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}