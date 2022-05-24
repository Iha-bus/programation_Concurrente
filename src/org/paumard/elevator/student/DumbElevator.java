package org.paumard.elevator.student;

import org.paumard.elevator.Building;
import org.paumard.elevator.Elevator;
import org.paumard.elevator.model.Person;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DumbElevator implements Elevator {

	//Building.MAX_FLOOR       alt shift R       == false on peu le remplacer par !
	private int elevatorCapacity;			// currentFloorindex == currentFloor-1
	private int currentFloor;
	private int prvFloor=1;
	private int tmp;
	private List<Person> listPeopleInElevator = new ArrayList<Person>();
	private List<List<Person>> peopleByFloor;

	private boolean lastPersonArrived = false;
	private int peopleWaitingAtFloors = 0;
	
	
	static <K, V> K getKey(Map<K, V> map, V value) {
		for (Entry<K, V> entry : map.entrySet()) {
		if (entry.getValue().equals(value)) {
		return entry.getKey();
		}
		}
		return null;
		}
	
    public DumbElevator(int elevatorCapacity) {
        this.elevatorCapacity = elevatorCapacity;
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
		if (lastPersonArrived && this.listPeopleInElevator.isEmpty() && this.peopleWaitingAtFloors == 0) {
			return 1;
		}

		else if ((this.listPeopleInElevator.size() == this.elevatorCapacity)) { // si l'Elevator est rempli faut mieux
																				// vider le plus du monde possible;
			return choseNextFloorWhenElevIsNonEmpty();
		}

		else if (this.peopleWaitingAtFloors != 0 && prvFloor == tmp) {// on choisit selon le nombre max dans les étage
																		// sinon selon le nombre max dans
																		// l'étage courant
			Map<Integer, Integer> myMap = new HashMap<>();
			for (int FloorIndex = 0; FloorIndex < Building.MAX_FLOOR; FloorIndex++) {
				myMap.put(FloorIndex + 1, this.peopleByFloor.get(FloorIndex).size());
			}
			int max = myMap.entrySet().stream().map(m -> m.getValue()).max(Long::compare).orElseThrow(); //le maximum de perssone 
			int max1 = getKey(myMap, max);     //max1 contient L'etage ou il yas plus de perssonne en file d'attente
			
			Map<Integer, Long> collect = peopleByFloor.get(currentFloor - 1).stream().map(p -> p.getDestinationFloor())
					.collect(Collectors.groupingBy(Function.identity(),	// histogramme des etages par personne (densité)
							Collectors.counting()));
    		Long max2 = collect.entrySet().stream().map(m->m.getValue()).max(Long::compare).orElseThrow();
    		int max3= getKey(collect,max2); //max2 contient L'etage de distination ou il a plus de perssonne 
    		
    		if (peopleByFloor.get(currentFloor-1).size()!=0) {
    			System.out.println("****2..1****");
    			System.out.println(this.listPeopleInElevator.size());
    			tmp =prvFloor;
    			prvFloor=max3;
    			return max3;
    			}
    			else {
    			tmp =prvFloor;
    			prvFloor=max1;
    			return max1;}
    			
    	}
    	else { // fonctionnement par défaut {less dump}
    		switch (currentFloor ) {
    		  case 1: {
    			tmp =prvFloor;
    			prvFloor=1;
    			return 2;
    		} case 2: {
    			if (prvFloor==1) { //monter
    			tmp =prvFloor;
    			prvFloor=2;
    			return 3;
    			}else if (prvFloor==3){//descendre
    			tmp =prvFloor;
    			prvFloor=2;
    			return 1;
    			}
    		} case 3: {
    			if (prvFloor==2) {
    			tmp =prvFloor;
    			prvFloor=3;
    			return 4;
    			}else if (prvFloor==4){
    			tmp =prvFloor;
    			prvFloor=3;
    			return 2;
    			}
    		}case 4: {
    			tmp =prvFloor;
    			prvFloor=4;
    			return 3;
    		}
    	}
    	
    	}
    	
    	return 1;
    	}

	private int choseNextFloorWhenElevIsNonEmpty() {
		Map<Integer, Long> collectElevator = this.listPeopleInElevator.stream().map(p-> p.getDestinationFloor())
    	.collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));
    	Long max =collectElevator.entrySet().stream().map(m->m.getValue()).max(Long::compare).orElseThrow();
    	int max2= getKey(collectElevator,max);
    	tmp =prvFloor;
    	prvFloor =max2;
    	return max2;
	}

    @Override
    public void arriveAtFloor(int floor) {
    	this.currentFloor = floor;
    }

    @Override
    public void loadPerson(Person person) {
    	
    		this.listPeopleInElevator.add(person);
    		this.peopleWaitingAtFloors--;
    }

    @Override
    public void unloadPerson(Person person) {
    	this.listPeopleInElevator.remove(person);

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
