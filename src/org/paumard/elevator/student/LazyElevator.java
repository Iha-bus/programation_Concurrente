package org.paumard.elevator.student;

import org.paumard.elevator.Elevator;
import org.paumard.elevator.model.Person;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class LazyElevator implements Elevator {

    private static final String UP = "UP";
    private static final String DOWN = "down";
    private Random random = new Random(314L);

    private int elevatorCapacity;
	private int currentFloor;
	private List<List<Person>> peopleByFloor;

	private boolean lastPersonArrived = false;

	private int peopleInElevator = 0;
	private int peopleWaitingAtFloors = 0;
    private String direction;

    public LazyElevator(int elevatorCapacity) {
        this.elevatorCapacity = elevatorCapacity;
        this.direction = UP;
    }

    @Override
    public void startsAtFloor(LocalTime time, int initialFloor) {
		this.currentFloor = initialFloor;
    }

    @Override
    public void peopleWaiting(List<List<Person>> peopleByFloor) {
		this.peopleByFloor = peopleByFloor;
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
    	    if (Objects.equals(this.direction, UP) && this.currentFloor == 4) {
    	        this.direction = DOWN;
            }
            if (Objects.equals(this.direction, DOWN) && this.currentFloor == 1) {
                this.direction = UP;
            }
    	    if (Objects.equals(this.direction, UP)) {
                return this.currentFloor + 1;
            } else {
    	        return this.currentFloor - 1;
            }
    	}
    }

    @Override
    public void arriveAtFloor(int floor) {
    	this.currentFloor = floor;
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
    	this.lastPersonArrived  = true;
    }
}