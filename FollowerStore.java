/**
 * Our FollowerStore is structured as following:
HashMap 				----> 	Buckets
Buckets 				----> 	KeyValuePairLinkedList
KeyValuePairLinkedList 	----> 	KeyValuePairs
KeyValuePairs 			----> 	Key = User_id; Value = Users (created data type)
Users 					----> 	Key = User_id; 2 IdDatePairLinkedLists (worshipper and worshipping)
IdDatePairLinkedList 	----> 	IdDatePair
IdDatePair 				----> 	User_id, DateFollowed

First of all, the reason why I have chosen HashMap as my data structure is because of the high performance speed it provides when retrieving specific data. Since a hashmap contains buckets that contains only elements with the same hashcode of the key (User_id in this case), we do not need to iterate over all the data, but only over the elements that have the same hashcode. One problem with the hashmap is that I have chosen the primer number 151 as its size which is efficient enough for low numbers of users such as 144 (number of users for Witter runner) as the mod of any number from 1-144 will return a unique key, however, if the number of users exceeds over 151, the hashmap will become less and less efficient as more and more KeyValuePairs will be stored in the same bucket. Therefore, one improvement I could make would be to make the size and the mod value of the hash() function scalable depending on the number of users eg: find the nearest prime number above the number of users. 

addFollower(): Best case time complexity: O(1); Average case time complexity: O(n); Worst case time complexity: O(n).
Best case occurs when the follower user1 is the first element in the worshipper IdDatePairLinkedList of user2, and the followed user2 is the first element in the worshipping IdDatePairLinkedList of user1, therefore the system only has to traverse 1 element into the linkedlist. Worst case occurs when user1 and user2 are the last elements in the worshipper and worshipping IdDatePairLinkedList respectively, therefore the system has to iterate through the entire IdDatePairLinkedList to check whether or not they follow each other already.

getFollowers(): Best case time complexity: O(nlog(n)); Average case time complexity: O(nlog(n)); Worst case time complexity: O(n^2).
All cases have the same time complexity because the method requires the followers to be ordered by dateFollowed and since we used the quick sort algorithm.

getFollows(): Best case time complexity: O(nlog(n)); Average case time complexity: O(nlog(n)); Worst case time complexity: O(nlog(n)).
This method is exactly the same as for the getFollowers() method except that we are retrieving the follows that the specified user has instead of the followers.

isAFollower(): Best case time complexity: O(1) ; Average case time complexity: O(n) ; Worst case time complexity: O(n).
The reasoning behind the addFollower() explanation of time complexities revolves around this function. The best case is when the follower and the followed are the first elements in the linkedlists of worshippers and worshipping, and worst case when they are the last elements.

getNumFollowers(): Best case time complexity: O(nlog(n); Average case time complexity: O(nlog(n)); Worst case time complexity: O(nlog(n)).
This method calls the method getFollowers() therefore the time complexity of it is dependant on that method.

getMutualFollowers(): Best case time complexity: O(nlog(n)); Average case time complexity: O(nlog(n)); Worst case time complexity: O(n^2).
The way I have implemented this method is in such a way that it will avoid having an average case time complexity of O(n^2). I sorted two arrays of followers, one from each user, by ID (lowest first).  I compared two followers, one from each array of followers from each user. I incremented the index of the array containing the mathematically lower value ID and stored the value when both followers had the same ID. However, since we are required to output an array of integers containing the user ID's that matched in a date sorted manner, our time complexity ends up being O(nlog(n)) for best and average case time complexities and O(n^2) for worst case time complexity.

getMutualFollows(): Best case time complexity: O(nlog(n)); Average case time complexity: O(nlog(n)); Worst case time complexity: O(n^2).
Exactly the same as getMutualFollowers() method except that we are searching for the ID's of matching follows rather than followers.

getTopUsers(): Best case time complexity: O(nlog(n)); Average case time complexity: O(nlog(n)); Worst case time complexity: O(n^2).
Best case cannot surpass O(nlog(n)) as we have to quick sort the output by frequency of the trending topic. Worst case will always be time complexity O(n^2) as long as we use quick sort but as we mentioned in UserStore, there are advantages and disadvantages of using quick sort.

N.B: For the hash() method, I made sure that the key was not a negative value by changing it into an absolute value.
 *
 * @Credit to: Matt Leeke (lecture slides and labwork) and Parinthorn(Kate) Wiwatdirekkul as my lab partner.
 * @Adrian Cho: 1622228
 */

package uk.ac.warwick.java.cs126.services;

import java.io.PrintStream;
import java.util.Date;

public class FollowerStore implements IFollowerStore {

	private HashMap followermap;

	public FollowerStore() {

		followermap = new HashMap(151);
	}

	public boolean addFollower(int uid1, int uid2, Date followDate) {
		//Makes sure that no one can follow themselves
		if (uid1 == uid2) return false;
    //Check if the follower exists within our HashMap, if not create and add the user
		if (!followermap.contains(uid1)) {
			followermap.add(uid1);
		}
    //Check if the user being followed exists within our HashMap, if not add the user
		if (!followermap.contains(uid2)) {
			followermap.add(uid2);
		}
		//Check if the follower is already following the other, if not add the follower uid1 to the list of followers of user uid2, and add the person being followed uid2 to the follows list of user uid1
		if (isAFollower(uid1, uid2)) {
			return false;
		} else {
			followermap.get(uid2).addWorshipper(uid1, followDate);
			followermap.get(uid1).addWorshipping(uid2, followDate);
		}
		return true;
	}

	public int[] getFollowers(int uid) {
    //Return an array of user ID's which have been sorted by date (latest follower first)
		return toArray(quickSortDate(followermap.get(uid).getWorshipperArray(), 0, followermap.get(uid).getWorshipperArray().length -1));
	}

	public int[] getFollows(int uid) {
    //Return an array of user ID's which have been sorted by date (latest follow first)
		return toArray(quickSortDate(followermap.get(uid).getWorshippingArray(), 0, followermap.get(uid).getWorshippingArray().length -1));
	}

	public boolean isAFollower(int uid1, int uid2) {
    //Checks if the users uid1 and uid2 exist in the first place
		if ((followermap.get(uid1) == null) || (followermap.get(uid2) == null)) {
		return false;
  } //If users exist, check the follower list of uid2 and look for uid1 and also check the follow list of uid1 and look for uid2
		if ((followermap.get(uid1).getWorshipping().containsUid(uid2)) && (followermap.get(uid2).getWorshipper().containsUid(uid1))) {
			return true;
		} else return false;
	}

	public int getNumFollowers(int uid) {
    //returns the number of followers of the specified user
		return getFollowers(uid).length;
	}

	public int[] getMutualFollowers(int uid1, int uid2) {
    //Create 2 arrays of followers containing the followers of uid1 and uid2 sorted by ID
		IdDatePair[] arrayWorshipper1 = quickSortId(followermap.get(uid1).getWorshipperArray(), 0, followermap.get(uid1).getWorshipperArray().length-1);
		IdDatePair[] arrayWorshipper2 = quickSortId(followermap.get(uid2).getWorshipperArray(), 0, followermap.get(uid2).getWorshipperArray().length-1);

		int i = 0;
		int j = 0;
		int k = 0;
		//Compare 2 followers, 1 from each array of followers and increment a variable called "k" everytime the user ID's of the followers are the same
		while ((i != arrayWorshipper1.length) && (j != arrayWorshipper2.length)) {
			if (arrayWorshipper1[i].getuid() == arrayWorshipper2[j].getuid()) {
				i++;
				j++;
				k++;
			}
      //Increase the index of the follower array which contains the numerically lower ID
			else if (arrayWorshipper1[i].getuid() < arrayWorshipper2[j].getuid()) {
				i++;
			}
			else if (arrayWorshipper1[i].getuid() > arrayWorshipper2[j].getuid()) {
				j++;
			}
		}
    //Create an array of mutual followers of size "k", the number of mutual followers of uid1 and uid2
		IdDatePair[] arrayMutualWorshipper = new IdDatePair[k];
    //Check that there are at least some mutual followers, otherwise just return an empty array
    if (k == 0) {
			return new int[0];
		}
		//Reset variables
		i = 0;
		j = 0;
		k = 0;
    //Compare 2 followers, 1 from each array of followers and increment the index of the mutual followers array called "k" everytime the user ID's of the followers are the same
		while ((i != arrayWorshipper1.length) && (j != arrayWorshipper2.length)) {
			if (arrayWorshipper1[i].getuid() == arrayWorshipper2[j].getuid()) {
        //Whilst comparing the 2 followers, make sure to store the follower that followed later
				if (followermap.get(uid1).getWorshipper().get(arrayWorshipper1[i].getuid()).getdateFollowed().before(followermap.get(uid2).getWorshipper().get(arrayWorshipper2[j].getuid()).getdateFollowed())) {
					arrayMutualWorshipper[k] = arrayWorshipper2[j];
				} else {
					arrayMutualWorshipper[k] = arrayWorshipper1[i];
				}
				i++;
				j++;
				k++;
			}
			else if (arrayWorshipper1[i].getuid() < arrayWorshipper2[j].getuid()) {
				i++;
			}
			else if (arrayWorshipper1[i].getuid() > arrayWorshipper2[j].getuid()) {
				j++;
			}
		}
    //When outputting the array of mutual followers, sort the users by date (latest follower first)
		return toArray(quickSortDate(arrayMutualWorshipper, 0, arrayMutualWorshipper.length-1));
	}

	public int[] getMutualFollows(int uid1, int uid2) {
    //Exact same concept is applied as in getMutualFollowers
		IdDatePair[] arrayWorshipping1 = quickSortId(followermap.get(uid1).getWorshippingArray(), 0, followermap.get(uid1).getWorshippingArray().length-1);
		IdDatePair[] arrayWorshipping2 = quickSortId(followermap.get(uid2).getWorshippingArray(), 0, followermap.get(uid2).getWorshippingArray().length-1);

		int i = 0;
		int j = 0;
		int k = 0;

		while ((i != arrayWorshipping1.length) && (j != arrayWorshipping2.length)) {
			if (arrayWorshipping1[i].getuid() == arrayWorshipping2[j].getuid()) {
				i++;
				j++;
				k++;
			}
			else if (arrayWorshipping1[i].getuid() < arrayWorshipping2[j].getuid()) {
				i++;
			}
			else if (arrayWorshipping1[i].getuid() > arrayWorshipping2[j].getuid()) {
				j++;
			}
		}
		IdDatePair[] arrayMutualWorshipping = new IdDatePair[k];

		if (k == 0) {
			return new int[0];
		}

		i = 0;
		j = 0;
		k = 0;

		while ((i != arrayWorshipping1.length) && (j != arrayWorshipping2.length)) {
			if (arrayWorshipping1[i].getuid() == arrayWorshipping2[j].getuid()) {
				if (followermap.get(uid1).getWorshipping().get(arrayWorshipping1[i].getuid()).getdateFollowed().before(followermap.get(uid2).getWorshipping().get(arrayWorshipping2[j].getuid()).getdateFollowed())) {
					arrayMutualWorshipping[k] = arrayWorshipping2[j];
				} else {
					arrayMutualWorshipping[k] = arrayWorshipping1[i];
				}
				i++;
				j++;
				k++;
			}
			else if (arrayWorshipping1[i].getuid() < arrayWorshipping2[j].getuid()) {
				i++;
			}
			else if (arrayWorshipping1[i].getuid() > arrayWorshipping2[j].getuid()) {
				j++;
			}
		}
		return toArray(quickSortDate(arrayMutualWorshipping, 0, arrayMutualWorshipping.length-1));
	}

	public int[] getTopUsers() {
    //Count the total number of users in our HashMap
		int count = 0;
		for (int i = 0; i < followermap.table.length; i++) {
			try {
				if (followermap.table[i] != null) {
					try {
						for (ListElement<KeyValuePair> ptr = followermap.table[i].head; ptr != null; ptr= ptr.getNext()) {
							count++;
						}
					} catch (Exception x) {}
				}
			} catch (Exception x) {}
		}
		//Check if the number of users is 0, if it is return an empty array
		if (count == 0) {
			return new int[0];
		}
		//Create an array of keyvaluepairs of size "count", the total number of users
		KeyValuePair[] topUsers = new KeyValuePair[count];
		count = 0;

		for (int i = 0; i < followermap.table.length; i++) {
			try {
				if (followermap.table[i] != null) {
					try {
						for (ListElement<KeyValuePair> ptr = followermap.table[i].head; ptr != null; ptr= ptr.getNext()) {
							try {
								if (ptr.getValue() != null) {
                  //Store each element of the KeyValuePairLinkedList into a slot in the array
									topUsers[count] = ptr.getValue();
									count++;
								}
							} catch (Exception x) {}
						}
					} catch (Exception x) {}
				}
			} catch (Exception x) {}
		}
		//Sort the users by the number of followers (most followers first)
		topUsers = quickSortFollowers(topUsers, 0, topUsers.length-1);
		//Create an array of user ID's of size count, the total number of users
		int[] topUsersArray = new int[count];
		for (int i = 0; i < topUsers.length; i++) {
			topUsersArray[i] = topUsers[i].getKey();
		}
		return topUsersArray;
	}

	public KeyValuePair[] quickSortFollowers(KeyValuePair[] unsortedArray, int low, int high) {
    //quick sort by number of followers
		int lo = low;
		int hi = high;
		if (low >= high) return unsortedArray;
		int mid = unsortedArray[((lo + hi) / 2)].getValue().getWorshipper().size();


		while (lo <= hi) {
			while ((lo <= hi) && (unsortedArray[lo].getValue().getWorshipper().size() > mid)) {
				lo++;
			}
			while ((lo <= hi) && (unsortedArray[hi].getValue().getWorshipper().size() < mid)) {
				hi--;
			}
			if (lo <= hi) {
				KeyValuePair temp = unsortedArray[lo];
				unsortedArray[lo] = unsortedArray[hi];
				unsortedArray[hi] = temp;
				lo++;
				hi--;
			}
		}

		if (low < hi) {
		quickSortFollowers(unsortedArray, low, hi);
		}
		if (high > lo) {
		quickSortFollowers(unsortedArray, lo, high);
		}
		return unsortedArray;
	}

	public IdDatePair[] quickSortDate(IdDatePair[] unsortedArray, int low, int high) {
	    //quick sort by date
			int lo = low;
			int hi = high;
			if (low >= high) return unsortedArray;
			Date mid = unsortedArray[(lo+hi) / 2].getdateFollowed();

			while (lo <= hi) {
				while (lo <= hi && unsortedArray[lo].getdateFollowed().after(mid)) {
					lo++;
				}
				while (lo <= hi && unsortedArray[hi].getdateFollowed().before(mid)) {
					hi--;
				}
				if (lo <= hi) {
					int temp = unsortedArray[lo].getuid();
					unsortedArray[lo].setuid(unsortedArray[hi].getuid());
					unsortedArray[hi].setuid(temp);
					lo++;
					hi--;
				}
			}

			if (low < hi) {
			quickSortDate(unsortedArray, low, hi);
			}
			if (high > lo) {
			quickSortDate(unsortedArray, lo, high);
			}
			return unsortedArray;
		}

	public IdDatePair[] quickSortId(IdDatePair[] unsortedArray, int low, int high) {
		//quick sort by ID (numerically)
		int lo = low;
		int hi = high;
		if (low >= high) return unsortedArray;;
		int mid = unsortedArray[(lo+hi) / 2].getuid();

		while (lo <= hi) {
			while (lo <= hi && unsortedArray[lo].getuid() > mid) {
				lo++;
			}
			while (lo <= hi && unsortedArray[hi].getuid() < mid) {
				hi--;
			}
			if (lo <= hi) {
				int temp = unsortedArray[lo].getuid();
				unsortedArray[lo].setuid(unsortedArray[hi].getuid());
				unsortedArray[hi].setuid(temp);
				lo++;
				hi--;
			}
		}

		if (low < hi) {
		quickSortId(unsortedArray, low, hi);
		}
		if (high > lo) {
		quickSortId(unsortedArray, lo, high);
		}
		return unsortedArray;
		}

	public int[] toArray(IdDatePair[] arrayIdDate) {
    //create an array of users to only store the ID's of the users
		int[] sortedArray = new int[arrayIdDate.length];
		for (int i = 0; i < arrayIdDate.length; i++) {
			sortedArray[i] = arrayIdDate[i].getuid();
		}
		return sortedArray;
	}

	public class HashMap {

		private KeyValuePairLinkedList[] table;

		public HashMap() {
			/* for very simple hashing, primes reduce collisions */
			this(151);
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
      //use the user ID as the key and mod it by 151 to give a hash value
			int code = Math.abs(uid) % 151;
			return code;
		}

		public void add(int uid) {
      //Use the hash function to get a hash value and access a specific bucket within our HashMap to add the user ID
			int hash_code = hash(uid);
			int location = hash_code;

			table[location].add(uid);
		}

		public Users get(int uid) {
      //access the required bucket using the hash value of the user ID and return the user along with his followers and follows linked lists
			int hash_code = hash(uid);
			int location = hash_code;

			ListElement<KeyValuePair> ptr = table[location].head;
			if (table[location].get(uid) == null) {
				return null;
			} else {
				return (Users)table[location].get(uid).getValue();
			}
		}

		public boolean contains(int uid) {
      //access the required bucket using the hash value of the user ID and check if the user is in the bucket
			int hash_code = hash(uid);
			int location = hash_code;

			if (table[location].get(uid) == null) {
				return false;
			}
			if (table[location].get(uid).getKey() == uid) {
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
      //Creates a new element and set the head as the next element in the list. The new element is assigned to be the new head
			ListElement<KeyValuePair> new_element = new ListElement(kvp);
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
				if(temp.getValue().getKey() == key) {
					return temp.getValue();
				}

				temp = temp.getNext();
			}

			return null;
		}

		// public int find(int key) {
		//
		// 	int index = 0;
		// 	ListElement<KeyValuePair> ptr = head;
		//
		// 	while(ptr != null) {
		// 		if(ptr.getValue().getKey() == key) {
		// 			return index;
		// 		}
		//
		// 		ptr = ptr.getNext();
		// 		index++;
		// 	}
		//
		// 	return -1;
		// }
	}

	public class KeyValuePair {

		private int key;
		private Users value;

		public KeyValuePair(int k) {
      //The constructor for the KeyValuePair contains the key(id of user) and a data type called "Users" (contains 2 linked list: followers and follows)
			key = k;
			value = new Users(k);
		}

		public int getKey() {

			return key;
		}

		public Users getValue() {

			return value;
		}
	}

    public class Users {

  		private int key;
  		private IdDatePairLinkedList worshipper;
  		private IdDatePairLinkedList worshipping;

  		public Users(int uid) {
        //Constructor for Users contains the user ID and 2 linked lists: followers and follows
  			key = uid;
  			worshipper = new IdDatePairLinkedList();
  			worshipping = new IdDatePairLinkedList();
  		}

  		public boolean addWorshipper(int uid, Date dateFollowed) {
        //Add a follower to the linked list by taking in parameters user ID and the date the user got followed
  			worshipper.add(uid, dateFollowed);
  			return true;
  		}

  		public boolean addWorshipping(int uid, Date dateFollowed) {
        //Add a follow to the linked list by taking in parameters user ID and the date the user followed
  			worshipping.add(uid, dateFollowed);
  			return true;
  		}

  		public int getuid() {

  			return key;
  		}

  		public IdDatePairLinkedList getWorshipper() {

  			return worshipper;
  		}

  		public IdDatePairLinkedList getWorshipping() {

  			return worshipping;
  		}

  		public IdDatePair[] getWorshipperArray() {

  			return worshipper.toArray();
  		}

  		public IdDatePair[] getWorshippingArray() {

  			return worshipping.toArray();
  		}
	}

	public class IdDatePairLinkedList {
    //Methods for this class are practically the same as for the methods in the class KeyValuePairLinkedList, except for the extra method containsUid() and the type of data stored in the linked list: changes from KeyValuePair to IdDatePair
		private ListElement<IdDatePair> head;
		private int size;

		public IdDatePairLinkedList() {

			head = null;
			size = 0;
		}

		public void add(int uid, Date dateFollowed) {

			this.add(new IdDatePair(uid, dateFollowed));
		}

		public void add(IdDatePair idp) {

			ListElement<IdDatePair> new_element = new ListElement(idp);
			new_element.setNext(head);
			head = new_element;
			size++;
		}

		public int size() {

			return size;
		}

		public ListElement<IdDatePair> getHead() {
			return head;
		}

		public IdDatePair get(int uid) {

			ListElement<IdDatePair> temp = head;

			while(temp != null) {
				if(temp.getValue().getuid() == uid) {
					return temp.getValue();
				}

				temp = temp.getNext();
			}

			return null;
		}

		// public int find(int uid) {
		//
		// 	int index = 0;
		// 	ListElement<IdDatePair> ptr = head;
		//
		// 	while(ptr != null) {
		// 		if(ptr.getValue().getuid() == uid) {
		// 			return index;
		// 		}
		//
		// 		ptr = ptr.getNext();
		// 		index++;
		// 	}
		//
		// 	return -1;
		// }

		public boolean containsUid(int uid) {
    //Iterates through the linked list by checking if the element at the pointer has the specified user ID
			ListElement<IdDatePair> ptr = head;
			while (ptr != null) {
				if (ptr.getValue().getuid() == uid) {
					return true;
				}
				ptr = ptr.getNext();
			}
			return false;
		}

		public IdDatePair[] toArray() {

			ListElement<IdDatePair> ptr = head;
			IdDatePair[] arrayPair = new IdDatePair[size()];
			int i = 0;
			while (ptr != null) {
				arrayPair[i] = ptr.getValue();
				i++;
				ptr = ptr.getNext();
			}

			return arrayPair;
		}
	}

  public class IdDatePair {
    //Methods for this class are practically the same as for the methods in the class KeyValuePair, except for the extra setuid() method which was made mainly for good coding practices and the type of data stored as an IdDatePair
		private int uid;
		private Date dateFollowed;

		public IdDatePair(int uid, Date dateFollowed) {
      //Stores the user ID and the date the user followed (or got followed)
			this.uid = uid;
			this.dateFollowed = dateFollowed;
		}

		public int getuid() {

			return uid;
		}

		public Date getdateFollowed() {

			return dateFollowed;
		}

		public void setuid(int uid) {
      //Sets the user ID to the specified user ID
			this.uid = uid;
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
