package project3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class FlightList {
	private FlightNode tail;
	private FlightNode head;
	private FlightList upperList;

	public FlightList() {
		head = new FlightNode(null, null);
		tail = new FlightNode(null, null);
		head.setNext(tail);
		tail.setPre(head);
	}

	public FlightList(String file) {
		head = new FlightNode(null, null);
		tail = new FlightNode(null, null);
		head.setNext(tail);
		tail.setPre(head);
		FlightKey tempK;
		FlightData tempD;
		try {
			Scanner scan = new Scanner(new File(file));
			while (scan.hasNextLine()) {
				String[] line = scan.nextLine().split(" ");
				tempK = new FlightKey(line[0], line[1], line[2], line[3]);
				tempD = new FlightData(line[4], Integer.parseInt(line[5]));
				insert(tempK, tempD);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Cannot read file: " + file);
		}
	}

	private void link(FlightList list, FlightList newList) {
		list.head.setUp(newList.head);
		newList.head.setDown(list.head);
		list.tail.setUp(newList.tail);
		newList.tail.setDown(list.tail);
	}

	public boolean insert(FlightKey key, FlightData data) {
		FlightNode newNode = new FlightNode(key, data);
		insert(this, newNode);
		return true;
	}

	private void insert(FlightList flightList, FlightNode newNode) {
		if (flightList.upperList == null) {
			flightList.upperList = new FlightList();
			link(flightList, flightList.upperList);
		}
		FlightNode temp = flightList.head;
		Random random = new Random();
		int ran = random.nextInt(2);
		if (temp.getNext() == flightList.tail) {
			temp.setNext(newNode);
			newNode.setNext(flightList.tail);
			flightList.tail.setPre(newNode);
			newNode.setPre(temp);
			if (ran == 1) {
				createNewList(flightList, newNode);
			}
			return;
		}
		while (temp.getNext() != flightList.tail) {
			temp = temp.getNext();
			if (temp.getKey().compareTo(newNode.getKey()) > 0) {
				insert(temp, newNode);
				if (ran == 1) {
					createNewList(flightList, newNode);
				}
				return;
			}
		}
		if (temp.getNext() == tail) {
			temp.setNext(newNode);
			newNode.setPre(temp);
			flightList.tail.setPre(newNode);
			newNode.setNext(flightList.tail);
		}

	}

	private void createNewList(FlightList flightList, FlightNode newNode) {
		FlightNode upperNode = new FlightNode(newNode.getKey(),
				newNode.getData());
		newNode.setUp(upperNode);
		upperNode.setDown(newNode);
		insert(flightList.upperList, upperNode);
	}

	private void insert(FlightNode temp, FlightNode newNode) {
		newNode.setNext(temp);
		newNode.setPre(temp.getPre());
		temp.getPre().setNext(newNode);
		temp.setPre(newNode);

	}

	public boolean find(FlightKey key) {
		FlightNode temp = head;
		FlightNode tempTail = tail;
		while (temp.getUp().getUp() != null) {
			temp = temp.getUp();
			tempTail = tempTail.getUp();
		}
		return find(temp.getNext(), key, tempTail);
	}

	private boolean find(FlightNode begin, FlightKey key, FlightNode tempTail) {
		FlightNode temp;
		if (begin.getData() == null) {
			temp = begin.getNext();
		} else {
			temp = begin;
		}
		if (temp.getKey().compareTo(key) == 0) {
			return true;
		}
		while (temp != tempTail) {
			if (temp.getKey().compareTo(key) == 0) {
				return true;
			}
			// key1>key2
			if (temp.getKey().compareTo(key) > 0) {
				if (temp.getDown() != null) {
					return find(temp.getPre().getDown(), key,
							tempTail.getDown());
				}
			}
			if (temp.getNext() == tempTail && temp.getDown() != null) {
				return find(temp.getDown(), key, tempTail.getDown());
			}
			temp = temp.getNext();
		}

		return true;
	}

	private FlightNode findPlace(FlightNode begin, FlightKey key,
			FlightNode tempTail) {
		FlightNode temp;
		if (begin.getData() == null) {
			temp = begin.getNext();
		} else {
			temp = begin;
		}
		if (temp.getKey().compareTo(key) == 0) {
			while (temp.getDown() != null) {
				temp = temp.getDown();
			}
			return temp;
		}
		while (temp != tempTail) {
			if (temp.getKey().compareTo(key) == 0) {
				while (temp.getDown() != null) {
					temp = temp.getDown();
				}
				return temp;
			}
			// key1>key2
			if (temp.getKey().compareTo(key) > 0) {
				if (temp.getDown() != null) {
					return findPlace(temp.getPre().getDown(), key,
							tempTail.getDown());
				} else {
					return temp;
				}
			}
			if (temp.getNext() == tempTail && temp.getDown() != null) {
				return findPlace(temp.getDown(), key, tempTail.getDown());
			}
			temp = temp.getNext();
		}

		return null;
	}

	public ArrayList<FlightNode> successors(FlightKey key) {
		FlightNode temp = head;
		FlightNode tempTail = tail;
		while (temp.getUp().getUp() != null) {
			temp = temp.getUp();
			tempTail = tempTail.getUp();
		}
		return successors(temp.getNext(), key, tempTail);

	}

	private ArrayList<FlightNode> successors(FlightNode begin, FlightKey key,
			FlightNode tempTail) {
		FlightKey newKey = new FlightKey(key.getDep(), key.getTem(),
				key.getDate(), key.getTime());
		FlightNode place = findPlace(begin, newKey, tempTail);
		ArrayList<FlightNode> suc = new ArrayList<>();
		if (place.getKey().compare2(key) == 0) {
			if (place.getKey().getTime().compareTo(key.getTime()) >= 0) {
				suc.add(place);
			}
		}
		place = place.getNext();
		while (place.getNext() != tail && place.getKey().compare2(key) == 0) {
			suc.add(place);
			place = place.getNext();
		}
		return suc;
	}

	public ArrayList<FlightNode> predecessors(FlightKey key) {
		FlightNode temp = head;
		FlightNode tempTail = tail;
		while (temp.getUp().getUp() != null) {
			temp = temp.getUp();
			tempTail = tempTail.getUp();
		}
		return predecessors(temp.getNext(), key, tempTail);

	}

	private ArrayList<FlightNode> predecessors(FlightNode begin, FlightKey key,
			FlightNode tempTail) {
		FlightKey newKey = new FlightKey(key.getDep(), key.getTem(),
				key.getDate(), key.getTime());
		FlightNode place = findPlace(begin, newKey, tempTail);
		ArrayList<FlightNode> suc = new ArrayList<>();
		if (place.getKey().compare2(key) == 0) {
			if (place.getKey().getTime().compareTo(key.getTime()) <= 0) {
				suc.add(place);
			}
		}
		place = place.getPre();
		while (place.getPre() != head && place.getKey().compare2(key) == 0) {
			suc.add(place);
			place = place.getPre();
		}
		return suc;
	}

	public ArrayList<FlightNode> findFlights(FlightKey key, int timeDifference) {
		FlightNode temp = head;
		FlightNode tempTail = tail;
		while (temp.getUp().getUp() != null) {
			temp = temp.getUp();
			tempTail = tempTail.getUp();
		}
		return findFlights(temp.getNext(), key, tempTail, timeDifference);
	}

	private ArrayList<FlightNode> findFlights(FlightNode begin, FlightKey key,
			FlightNode tempTail, int timeDifference) {

		FlightNode place = findPlace(begin, key, tempTail);
		ArrayList<FlightNode> suc = new ArrayList<>();

		FlightNode backward = place.getPre();
		while (backward != head && backward.getKey().compare2(key) == 0
				&& backward.getKey().timeDifference(key) <= timeDifference) {
			suc.add(backward);
			backward = backward.getPre();
		}
		if (place.getKey().compare2(key) == 0) {
			if (place.getKey().timeDifference(key) <= timeDifference) {
				suc.add(place);
			}
		}
		FlightNode forward = place.getNext();
		while (forward != tail && forward.getKey().compare2(key) == 0
				&& forward.getKey().timeDifference(key) <= timeDifference) {
			suc.add(forward);
			forward = forward.getNext();
		}
		return suc;
	}

	public void print() {
		FlightNode temp = head;
		FlightNode tempTail = tail;
		while (temp.getUp().getUp() != null) {
			temp = temp.getUp();
			tempTail = tempTail.getUp();
		}
		FlightNode tempHead;
		while (temp != null) {
			tempHead = temp.getNext();
			while (tempHead != tempTail) {
				System.out.print(tempHead.getKey() + " ");
				tempHead = tempHead.getNext();
			}
			System.out.println();
			temp = temp.getDown();
			tempTail = tempTail.getDown();
		}
	}

	// extra credits
	public void remove(FlightKey key) {
		FlightNode tempHead = head;
		FlightNode tempTail = tail;
		while (tempHead.getUp().getUp() != null) {
			tempHead = tempHead.getUp();
			tempTail = tempTail.getUp();
		}
		FlightNode remove = findPlace(tempHead, key, tempTail);
		if (remove.getKey().compareTo(key) == 0) {
			FlightNode temp = remove;
			boolean bre = false;
			do {
				if (temp.getPre().getKey() == null
						&& temp.getNext().getKey() == null) {
					temp.getPre().setUp(null);
					temp.getNext().setUp(null);
					bre = true;
				}
				temp.getPre().setNext(temp.getNext());
				temp.getNext().setPre(temp.getPre());
				temp = temp.getUp();
				if (bre)
					break;
			} while (temp != null);
		}
	}

	public ArrayList<FlightNode> suggestFlights(FlightKey key) {
		FlightKey newKey = new FlightKey(key.getDep(), key.getTem(),
				String.valueOf(Integer.parseInt((String) key.getDate()) - 3),
				key.getTime());
		FlightNode tempHead = head;
		FlightNode tempTail = tail;
		while (tempHead.getUp().getUp() != null) {
			tempHead = tempHead.getUp();
			tempTail = tempTail.getUp();
		}
		FlightNode place = findPlace(tempHead, newKey, tempTail);
		ArrayList<FlightNode> sug = new ArrayList<>();
		if (place.getKey().compareTo(key) != 0) {
			if (place.getKey().compare3(key) == 0) {
				if (place.getKey().dayDifference(key) <= 3) {
					sug.add(place);
				}
			}
			place = place.getNext();
			while (place.getNext() != tail && place.getKey().compare3(key) == 0
					&& place.getKey().dayDifference(key) <= 3) {
				sug.add(place);
				place = place.getNext();
			}
		}

		return sug;
	}

	public void readFlights(String file) {
		head = new FlightNode(null, null);
		tail = new FlightNode(null, null);
		head.setNext(tail);
		tail.setPre(head);
		FlightKey tempK;
		FlightData tempD;
		try {
			Scanner scan = new Scanner(new File(file));
			while (scan.hasNextLine()) {
				String[] line = scan.nextLine().split(" ");
				tempK = new FlightKey(line[0], line[1], line[2], line[3]);
				tempD = new FlightData(line[4], Integer.parseInt(line[5]));
				insert(tempK, tempD);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Cannot read file: " + file);
		}

	}
}
