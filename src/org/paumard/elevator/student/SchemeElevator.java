package org.paumard.elevator.student;

import org.paumard.elevator.Elevator;
import org.paumard.elevator.model.Person;

import java.time.LocalTime;
import java.util.List;
import java.util.Random;

public class SchemeElevator implements Elevator {

    private int[] scheme = {1, 2, 3, 4, 3, 2};
    private int schemeIndex = 0;

    private boolean lastPersonArrived = false;

    private int peopleInElevator = 0;
    private int peopleWaitingAtFloors = 0;

    public SchemeElevator(int elevatorCapacity) {
    }

    @Override
    public void startsAtFloor(LocalTime time, int initialFloor) {
    }

    @Override
    public void peopleWaiting(List<List<Person>> peopleByFloor) {
        this.peopleWaitingAtFloors =
                peopleByFloor.stream()
                        .mapToInt(List::size)
                        .sum();
    }

    @Override
    public int chooseNextFloor() {
        if (lastPersonArrived &&
                this.peopleInElevator == 0 &&
                this.peopleWaitingAtFloors == 0) {
            return 1;
        } else {
            this.schemeIndex = (this.schemeIndex + 1) % this.scheme.length;
            int nextFloor = this.scheme[schemeIndex];
            return nextFloor;
        }
    }

    @Override
    public void arriveAtFloor(int floor) {
    }

    @Override
    public void loadPerson(Person person) {
        this.peopleInElevator++;
        this.peopleWaitingAtFloors--;
    }

    @Override
    public void unloadPerson(Person person) {
        this.peopleInElevator--;
    }

    @Override
    public void newPersonWaitingAtFloor(int floor, Person person) {
        this.peopleWaitingAtFloors++;
    }

    @Override
    public void lastPersonArrived() {
        this.lastPersonArrived = true;
    }
}