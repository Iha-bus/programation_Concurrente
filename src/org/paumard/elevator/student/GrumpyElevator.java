package org.paumard.elevator.student;

import org.paumard.elevator.Elevator;
import org.paumard.elevator.model.Person;

import java.time.LocalTime;
import java.util.*;

public class GrumpyElevator implements Elevator {

	private Random random = new Random(314L);

    private int elevatorCapacity;
	private int currentFloor;
	private List<List<Person>> peopleByFloor;

	private boolean lastPersonArrived = false;

	private List<Person> peopleInElevator = new ArrayList<>();
	private int numberOfPeopleWaitingAtFloors = 0;

    public GrumpyElevator(int elevatorCapacity) {
        this.elevatorCapacity = elevatorCapacity;
    }

    @Override
    public void startsAtFloor(LocalTime time, int initialFloor) {
		this.currentFloor = initialFloor;
    }

    @Override
    public void peopleWaiting(List<List<Person>> peopleByFloor) {
		this.peopleByFloor = peopleByFloor;
		this.numberOfPeopleWaitingAtFloors =
				peopleByFloor.stream()
							.mapToInt(List::size)
							.sum();
    }

    @Override
    public int chooseNextFloor() {
    	if (lastPersonArrived && 
    			this.peopleInElevator.size() == 0 &&
    			this.numberOfPeopleWaitingAtFloors == 0) {
            return 1;
        } else if (lastPersonArrived &&
    			this.peopleInElevator.size() == 0) {
    	    if (noOneIsWaitingAtCurrentFloor()) {
                return findFloorWithTheLeastNumberOfPeople();
            } else {
    	        return chooseDestinationFromCurrentFloor();
            }
        } else if (lastPersonArrived) {
    	    return this.peopleInElevator.get(0).getDestinationFloor();
    	} else {
            if (numberOfPeopleWaitingAtFloors == 0) {
                return randomFloorDifferentFromCurrent();
            }
            List<Integer> emptyFloors = findEmptyFloorsDifferentFromCurrent();
            if (emptyFloors.isEmpty()) {
                return findFloorWithTheLeastNumberOfPeople();
            } else {
                return pickRandomFloorFrom(emptyFloors);
            }
    	}
    }

    private int chooseDestinationFromCurrentFloor() {
        return this.peopleByFloor.get(this.currentFloor - 1).get(0).getDestinationFloor();
    }

    private boolean noOneIsWaitingAtCurrentFloor() {
        return this.peopleByFloor.get(this.currentFloor - 1).isEmpty();
    }

    private int findFloorWithTheLeastNumberOfPeople() {
        int floorWithTheLeastNumberOfPeople = 0;
        int leastNumberOfPeople = 50;
        for (int floorIndex = 0 ; floorIndex < this.peopleByFloor.size() ; floorIndex++) {
            int numberOFPeopleWaiting = this.peopleByFloor.get(floorIndex).size();
            if (numberOFPeopleWaiting > 0 &&
                numberOFPeopleWaiting < leastNumberOfPeople) {
                leastNumberOfPeople = numberOFPeopleWaiting;
                floorWithTheLeastNumberOfPeople = floorIndex + 1;
            }
        }
        return floorWithTheLeastNumberOfPeople;
    }

    private Integer pickRandomFloorFrom(List<Integer> emptyFloors) {
        return emptyFloors.get(random.nextInt(emptyFloors.size()));
    }

    private List<Integer> findEmptyFloorsDifferentFromCurrent() {

        List<Integer> emptyFloorsDifferentFromCurrent = new ArrayList<>();
        for (int floorIndex = 0 ; floorIndex < this.peopleByFloor.size() ; floorIndex++) {
            if (floorIndex != (this.currentFloor - 1) && this.peopleByFloor.get(floorIndex).isEmpty()) {
                emptyFloorsDifferentFromCurrent.add(floorIndex + 1);
            }
        }
        return emptyFloorsDifferentFromCurrent;
    }

    private int randomFloorDifferentFromCurrent() {
        int nextFloor = random.nextInt(this.peopleByFloor.size()) + 1;
        while (nextFloor == this.currentFloor) {
            nextFloor = random.nextInt(this.peopleByFloor.size()) + 1;
        }
        return nextFloor;
    }

    @Override
    public void arriveAtFloor(int floor) {
    	this.currentFloor = floor;
    }

    @Override
    public void loadPerson(Person person) {
    	this.numberOfPeopleWaitingAtFloors--;
    	this.peopleByFloor.get(this.currentFloor - 1).remove(person);
    	this.peopleInElevator.add(person);
    }

    @Override
    public void unloadPerson(Person person) {
        this.peopleInElevator.remove(person);
        System.out.println("People in elevator = " + this.peopleInElevator.size());
        if (lastPersonArrived) {
            System.out.println("Last person arrived");
        }
    }

    @Override
    public void newPersonWaitingAtFloor(int floor, Person person) {
    	this.numberOfPeopleWaitingAtFloors++;
        this.peopleByFloor.get(floor - 1).add(person);
    }

    @Override
    public void lastPersonArrived() {
    	this.lastPersonArrived  = true;
    }
}