/**
* Our WeetStore is based on a hybrid structure between the FollowerStore data structure and the UserStore data structure. WeetStore revolves around creating a HashMap with its corresponding KeyValuePairLinkedLists and WeetLinkedList and also creating two separate array lists (one for the array of weets and the other for an array of strings containing the most frequent topics). The advantages of each individual data structure are explained separately in the UserStore and FollowerStore files. The disadvantage however of using two data structures is that the memory space will be doubled. On the other hand, we gain greater speed and efficiency as we are able to access specific indices that we otherwise would not be able to if we were just using a HashMap.

addWeet(): Best case time complexity: O(1); Average case time complexity: O(n); Worst case time complexity: O(n).
As I am adding a user to both a HashMap and an array list, the best case can be achieved as O(1) as the HashMap will generate a unique ID through hashing the user ID to get a key, and the array list will have enough space to not have to re-allocate and copy the array list to add another user. Worst case on the other hand would be O(n) as if the array list was full, we would have to re-allocate and copy over all the elements in the list to a new array list with a greater capacity.

getWeet(): Best case time complexity: O(n); Average case time complexity: O(n); Worst case time complexity: O(n).
We have to iterate through the array list containing the weets to store them in an array of weets and then once finished iterate through that array of weets to look for the weet with the specified weet ID. The improved version would just simply take the weet ID directly from our array list of weets and compared it with the specified weet ID.
 
Now before we move on,  the rest of the methods' time complexities all depend on the quick sort algorithm. The specification for WeetStore asked for data sorted by recency (most recent first) for all methods, except for getTrending() method which was by frequency (most frequent first) but since I also used quick sort for this method the time complexities will be the same.
 
getWeets(): Best case time complexity: O(nlog(n)); Average case time complexity: O(nlog(n)); Worst case time complexity: O(n^2).

getWeetsByUser(): Best case time complexity: O(nlog(n)); Average case time complexity: O(nlog(n)); Worst case time complexity: O(n^2).

getWeetsContaining(): Best case time complexity: O(nlog(n)); Average case time complexity: O(nlog(n)); Worst case time complexity: O(n^2).

getWeetsOn(): Best case time complexity: O(nlog(n)); Average case time complexity: O(nlog(n)); Worst case time complexity: O(n^2).

getWeetsBefore(): Best case time complexity: O(nlog(n)); Average case time complexity: O(nlog(n)); Worst case time complexity: O(n^2).

getTrending(): Best case time complexity: O(nlog(n)); Average case time complexity: O(nlog(n); Worst case time complexity: O(n^2).
 
 * @Credit to: Matt Leeke (lecture slides and labwork) and Parinthorn(Kate) Wiwatdirekkul as my lab partner.
 * @Adrian Cho: 1622228
 */

package uk.ac.warwick.java.cs126.services;

import uk.ac.warwick.java.cs126.models.User;
import uk.ac.warwick.java.cs126.models.Weet;

import java.io.BufferedReader;
import java.util.Date;
import java.io.FileReader;
import java.text.ParseException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class WeetStore implements IWeetStore {

	private HashMap Weetmap;
	private SortedArrayList arrayWeet;
	private StringArrayList arrayTrending;

	public WeetStore() {

		Weetmap = new HashMap(387031);
		arrayWeet = new SortedArrayList();
		arrayTrending = new StringArrayList();
	}

	public boolean addWeet(Weet weet) {
		//Checks if our HashMap contains the user that wrote the weet, if he/she does not exist add him/her to the HashMap
		if (!Weetmap.contains(weet.getUserId())) {
			Weetmap.add(weet.getUserId());
		}
		//Checks if the user that wrote the weet already contains a weet with the same weet ID, if not then add the weet to his weet list
		if (!Weetmap.get(weet.getUserId()).contains(weet.getId())) {
			Weetmap.get(weet.getUserId()).add(weet);
			arrayWeet.add(weet);
			//Finds all trends within a string and outputs it to our stringArrayList
			Pattern trend = Pattern.compile("#[A-Za-z0-9-_]+");
			Matcher match = trend.matcher(weet.getMessage().toLowerCase());

			while (match.find()) {
				String topic = match.group();

				CountStringPair x = new CountStringPair(topic);
				arrayTrending.add(x);
			}
			return true;
		} else return false;
	}

	public Weet getWeet(int wid) {
		//Iterates through the array LIST of weets and store each element in a NORMAL array of weets
		try {
			Weet[] weetArray = new Weet[arrayWeet.size()];

			for (int i = 0; i < arrayWeet.size(); i++) {
				weetArray[i] = arrayWeet.get(i);
			}
			//Checks if any of the weets stored in our array of weets contains the same ID as the specified ID, if there is, return it; otherwise return null
			for (int i = 0; i < weetArray.length; i++) {
				if (weetArray[i].getId() == wid) {
					return weetArray[i];
				}
			}
		} catch (Exception E) {
			return null;
		}
		return null;
	}

	public Weet[] getWeets() {
		//Stores all weets within our data structure into the slots of our array of weets
		Weet[] tempArray = new Weet[arrayWeet.size()];

		for (int i = 0; i < arrayWeet.size(); i++) {
			if (arrayWeet.get(i) != null) {
				tempArray[i] = arrayWeet.get(i);
			}
		}
		//Sort the whole array of weets by the frequency of each one (most frequent first)
		return quickSort(tempArray, 0, tempArray.length -1);
	}

	public Weet[] getWeetsByUser(User usr) {

		int count = 0;
		//Checks if size of our array LIST of weets is empty, if it is then return and empty array of weets
		if (arrayWeet.size() == 0) {
			Weet[] empty = new Weet[0];
			return empty;
		}
		//Iterates through our array LIST of weets and increments a variable called "count" everytime a weet with the same specified weet ID is encountered
		for (int i = 0; i < arrayWeet.size(); i++) {
			if (arrayWeet.get(i).getUserId() == usr.getId()) {
				count++;
			}
		}

		int index = 0;
		//Creates an array of weets of size "count", the number of weets with the same weet ID as the specifed weet ID
		Weet[] tempArray = new Weet[count];
		//Iterates through our array LIST of weets and store each weet with the same weet ID as the specified weet ID into our array of weets
		for (int j = 0; j < arrayWeet.size(); j++) {
			if (arrayWeet.get(j).getUserId() == usr.getId()) {
				tempArray[index] = arrayWeet.get(j);
				index++;
			}
		}
		//Sort the array of weets by the frequency of the each one (most frequent first)
		return quickSort(tempArray, 0, tempArray.length -1);
	}

	public Weet[] getWeetsContaining(String query) {
		//Same concept as getWeetsByUser() method however, store the weets that contain the specified query rather than the ones with a specified weet ID
		int count = 0;

		for (int i = 0; i < arrayWeet.size(); i++) {
			if (arrayWeet.get(i).getMessage().toLowerCase().contains(query.toLowerCase())) {
				count++;
			}
		}
		int index = 0;

		Weet[] tempArray = new Weet[count];

		for (int j = 0; j < arrayWeet.size(); j++) {

			if (arrayWeet.get(j).getMessage().toLowerCase().contains(query.toLowerCase())) {
				tempArray[index] = arrayWeet.get(j);
				index++;
			}
		}
		return quickSort(tempArray, 0, tempArray.length -1);
	}

	public Weet[] getWeetsOn(Date dateOn) {
		//Same concept as getWeetsContaining() method however, store the weets that were created on the specified date rather than the ones containing a specified query
		int count = 0;

		for (int i = 0; i < arrayWeet.size(); i++) {
			Date shortcut = arrayWeet.get(i).getDateWeeted();
			//In order to check whether or not a weet was created on the specified date, we must compare the year, month and day of the weet to the ones provided by the specified date
			if (shortcut.getYear() == dateOn.getYear() && shortcut.getMonth() == dateOn.getMonth() && shortcut.getDay() == dateOn.getDay()) {
				count++;
			}
		}
		int index = 0;

		Weet[] tempArray = new Weet[count];

		for (int j = 0; j < arrayWeet.size(); j++) {
			Date shortcut = arrayWeet.get(j).getDateWeeted();
			if (shortcut.getYear() == dateOn.getYear() && shortcut.getMonth() == dateOn.getMonth() && shortcut.getDay() == dateOn.getDay()) {
				tempArray[index] = arrayWeet.get(j);
				index++;
			}
		}
		return tempArray;
	}

	public Weet[] getWeetsBefore(Date dateBefore) {
		//Same concept as getWeetsContaining() method however, store the weets that were created before the specified date rather than the ones created on a specified date
		int count = 0;

		for (int i = 0; i < arrayWeet.size(); i++) {

			if (arrayWeet.get(i).getDateWeeted().before(dateBefore)) {
				count++;
			}
		}
		int index = 0;

		Weet[] tempArray = new Weet[count];

		for (int j = 0; j < arrayWeet.size(); j++) {

			if (arrayWeet.get(j).getDateWeeted().before(dateBefore)) {
				tempArray[index] = arrayWeet.get(j);
				index++;
			}
		}
		return tempArray;
	}

	public String[] getTrending() {
		//Create an array of strings of size 10 as the specification only asks for the top 10 most frequent strings starting with a #
		String[] array = new String[10];
		//Create an array of CountStringPair to iterate through it
		CountStringPair[] array2 = arrayTrending.getArray();
		//Iterates through the array of CountStringPair (that has been sorted by frequency by the getArray() function) and stores the first 10 strings
		for (int i = 0; i < 10; i++) {
			if (array2[i] != null) {
				array[i] = array2[i].getString();
			}
		}
		return array;
	}

	private class StringArrayList {

		CountStringPair[] countStringArray;
		private int capacity;
		private int size;

		public StringArrayList() {

			this.capacity = 100;
			this.countStringArray = new CountStringPair[capacity];
			this.size = 0;
		}

		public int size() {

			return size;
		}

		public boolean add(CountStringPair topic) {
		//iterates through our array of countStringPair to check whether or not the string stored in each element is equal to the specified string. If it is, increment the count of the weet by 1 to keep track of the frequency
			for (int i = 0; i < size; i++) {
				if (topic.getString().equals(countStringArray[i].getString())) {
					countStringArray[i].addCount();
					return true;
				}
			}

			if (size < capacity) {
			//Check if there is an available slot for the new topic within the array
			//If there is, add the topic and increment the size of the array by 1
				countStringArray[size] = topic;
				countStringArray[size].addCount();
				size++;
				return true;
			} else if (size >= capacity) {
			//Otherwise, transfer all the topics from the original array to a temporary array with a bigger capacity and add the topic to the last slot
				capacity *= 2;
				CountStringPair[] tempArray = new CountStringPair[capacity];
				for(int i = 0; i < size; i++) {
					tempArray[i] = countStringArray[i];
				}
				tempArray[size] = topic;
				tempArray[size].addCount();
				size++;
				countStringArray = tempArray;
				return true;
			} else {
				return false;
			}
		}

		public CountStringPair getCountString(CountStringPair string) {

			for (int i = 0; i < size; i++) {
				if (countStringArray[i] == string) {
					return countStringArray[i];
				}
			}
			return null;
		}

		public CountStringPair[] getArray() {

			return quickSortTrending(countStringArray, 0, size -1);
		}

		public String getString(int i) {

			return countStringArray[i].getString();
		}

		public boolean isEqual(String string) {

			for (int i = 0; i < size; i++) {
				if (countStringArray[i].getString() == string) {
					return true;
				}
			}
			return false;
		}

		public CountStringPair[] quickSortTrending(CountStringPair[] unsortedArray, int low, int high) {
			//Sorts the CountStringPair array by frequency (most frequent first)
			if (unsortedArray.length == 0) {
				return new CountStringPair[0];
			}

			int lo = low;
			int hi = high;
			if (low >= high) return unsortedArray;
			int mid = unsortedArray[((lo + hi) / 2)].getCount();


			while (lo <= hi) {
				while ((lo <= hi) && (unsortedArray[lo].getCount() > mid)) {
					lo++;
				}
				while ((lo <= hi) && (unsortedArray[hi].getCount() < mid)) {
					hi--;
				}
				if (lo <= hi) {
					CountStringPair temp = unsortedArray[lo];
					unsortedArray[lo] = unsortedArray[hi];
					unsortedArray[hi] = temp;
					lo++;
					hi--;
				}
			}

			if (low < hi) {
			quickSortTrending(unsortedArray, low, hi);
			}
			if (high > lo) {
			quickSortTrending(unsortedArray, lo, high);
			}
			return unsortedArray;
		}
	}

	public class CountStringPair {

		private int count;
		private String string;

		public CountStringPair(String string) {
			//Constructor of a CountStringPair will contain a count (for frequency) and the actual string of the weet
			count = 0;
			this.string = string;
		}

		public int getCount() {

			return count;
		}

		public String getString() {

			return string;
		}

		public void addCount() {
			//Increments property count
			count++;
		}
	}

	public Weet[] quickSort(Weet[] unsortedArray, int low, int high) {
	//Sort by date (latest weet first)
		int lo = low;
		int hi = high;
		if (low >= high) return unsortedArray;
		Date mid = unsortedArray[(lo+hi) / 2].getDateWeeted();

		while (lo <= hi) {
			while (lo <= hi && unsortedArray[lo].getDateWeeted().after(mid)) {
				lo++;
			}
			while (lo <= hi && unsortedArray[hi].getDateWeeted().before(mid)) {
				hi--;
			}
			if (lo <= hi) {
				Weet temp = unsortedArray[lo];
				unsortedArray[lo] = unsortedArray[hi];
				unsortedArray[hi] = temp;
				lo++;
				hi--;
			}
		}
		
		if (low < hi) {
		quickSort(unsortedArray, low, hi);
		}
		if (high > lo) {
		quickSort(unsortedArray, lo, high);
		}
		return unsortedArray;
	}

	public class HashMap {

		private KeyValuePairLinkedList[] table;

		public HashMap() {

			this(387031);
		}

		public HashMap(int size) {

			table = new KeyValuePairLinkedList[size];
			initTable();
		}

		// public int find(int uid) {
		// 	//returns the number of comparisons required to find element using Linear Search.
		// 	int count = 0;
		// 	int hash_code = hash(uid);
		// 	int location = hash_code;
		//
		// 	return table[location].find(uid);
		//
		// }

		private void initTable() {

			for(int i = 0; i < table.length; i++) {
				table[i] = new KeyValuePairLinkedList();
			}
		}

		private int hash(int uid) {

			int code = Math.abs(uid) % 387031;
			return code;
		}

		public void add(int uid) {

			int hash_code = hash(uid);
			int location = hash_code;

			table[location].add(uid);
		}

		public WeetLinkedList get(int uid) {

			int hash_code = hash(uid);
			int location = hash_code;

			ListElement<KeyValuePair> ptr = table[location].head;

			return table[location].get(uid).getValue();
		}

		public boolean contains(int uid) {

			int hash_code = hash(uid);
			int location = hash_code;

			if (table[location].get(uid) == null) {
				return false;
			}
			if (table[location].get(uid).getuid() == uid) {
				return true;
			} return false;
		}
	}

	public class KeyValuePairLinkedList {

		private ListElement<KeyValuePair> head;
		private int size;

		public KeyValuePairLinkedList() {

			head = null;
			size = 0;
		}

		public void add(int key) {

			this.add(new KeyValuePair(key));
		}

		public void add(KeyValuePair kvp) {

			ListElement<KeyValuePair> new_element = new ListElement<>(kvp);
			new_element.setNext(head);
			head = new_element;
			size++;
		}

		public int size() {

			return size;
		}

		public ListElement<KeyValuePair> getHead() {

			return head;
		}

		public KeyValuePair get(int key) {

			ListElement<KeyValuePair> temp = head;

			while(temp != null) {
				if(temp.getValue().getuid() == key) {
					return temp.getValue();
				}

				temp = temp.getNext();
			}

			return null;
		}

		public int find(int key) {

			int index = 0;
			ListElement<KeyValuePair> ptr = head;

			while(ptr != null) {
				if(ptr.getValue().getuid() == key) {
					return index;
				}

				ptr = ptr.getNext();
				index++;
			}

			return -1;
		}
	}

	public class KeyValuePair {

		private int uid;
		private WeetLinkedList weetList;

		public KeyValuePair(int uid) {
		//Constructor for the KeyValuePair consists of the userId and the data type I created WeetLinkedList
			this.uid = uid;
			weetList = new WeetLinkedList();
		}

		public int getuid() {

			return uid;
		}

		public WeetLinkedList getValue() {

			return weetList;
		}
	}

	public class WeetLinkedList {

		private ListElement<Weet> head;
		private int size;

		public WeetLinkedList() {

			head = null;
			size = 0;
		}

		public boolean add(Weet weet) {

			if(contains(weet.getId())) {
				return false;
			}

			ListElement<Weet> new_element = new ListElement<>(weet);
			new_element.setNext(head);
			head = new_element;
			size++;
			return true;
		}

		public int size() {

			return size;
		}

		public ListElement<Weet> getHead() {

			return head;
		}

		public Weet get(int wid) {

			ListElement<Weet> temp = head;

			while(temp != null) {
				if(temp.getValue().getId() == wid) {
					return temp.getValue();
				}

				temp = temp.getNext();
			}

			return null;
		}

		public boolean contains(int wid) {

			ListElement<Weet> temp = head;

			while(temp != null)	{
				if(temp.getValue().getId() == wid) {
					return true;
				}

				temp = temp.getNext();
			}
			return false;
		}
	}


	public class SortedArrayList {

		Weet[] weetArray;
		private int capacity;
		private int size;

		public SortedArrayList() {

			this.capacity = 100;
			this.weetArray = new Weet[capacity];
			this.size = 0;
		}

		public int size() {

			return size;
		}

		public boolean add(Weet weet) {

			if (size < capacity) {
				weetArray[size] = weet;
				size++;
				return true;
			} else if (size >= capacity) {
				capacity *= 2;
				Weet tempArray[] = new Weet[capacity];
				for(int i = 0; i < size; i++) {
					tempArray[i] = weetArray[i];
				}
				tempArray[size] = weet;
				size++;
				weetArray = tempArray;
				return true;
			} else {
				return false;
			}
		}

		public Weet getId(int id) {

			for (int i = 0; i < size; i++) {
				if (weetArray[i].getId() == id) {
					return weetArray[i];
				}
			}
			return null;
		}

		public Weet get(int i) {

			return weetArray[i];
		}

		public boolean isEqual(int id) {

			for (int i = 0; i < size; i++) {
				if (weetArray[i].getId() == id) {
					return true;
				}
			}
			return false;
		}
	}

	public class ListElement<E> {

		private final E value;
		private ListElement<E> next;
		private ListElement<E> prev;

		public ListElement(E value) {

			this.value = value;
		}

		public E getValue() {

			return this.value;
		}

		public ListElement<E> getNext() {

			return this.next;
		}

		public ListElement<E> getPrev() {

			return this.prev;
		}

		public void setNext(ListElement<E> e) {

			this.next = e;
		}

		public void setPrev(ListElement<E> e) {

			this.prev = e;
		}
	}
}
