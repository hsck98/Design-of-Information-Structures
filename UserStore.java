/**
* Our UserStore is composed of a SortedArrayList which contains all users in Witter. The reason why I decided to settle with this data structure is because it allows elements to be inserted, modified or deleted from a particular position (by the use of indices) that other data structures may not be able to do. This is a crucial advantage as it allows us to retrieve a user from our array list quickly and in a simple manner without the need for extra methods to re-enter values into an array and output these out. Furthermore, read operations such as get() have a worst case time complexity of O(n) which means that we can essentially retrieve users efficiently. Write operations are usually very efficient with array lists as they have an average time complexity of 0(1) but there may be need of re-allocation and copy if the all slots of the array are full.

addUser() : Best case time complexity: O(1); Average case time complexity: O(1); Worst case time complexity: O(n). Best case  occurs when there is enough space within the array to add the user. Worst case occurs when there is not enough space within the array to add the user so a copy of the array must be made within a new array with a larger capacity. Average time is usually O(1) if you are willing to sacrifice memory space in creating a large enough array to make the worst case less likely to occur.

getUser(): Best case time complexity: O(1); Average case time complexity: O(n); Worst case time complexity: O(n).
Best case occurs when the user we are trying to retrieve is the first element in our array list so the system only has to traverse 1 array position to get the user. The worst case occurs when the user is the last element in our array list so the system has to traverse the entire array list to get the user.

getUsers(): Best case time complexity: O(nlog(n)); Average case time complexity: O(nlog(n)); Worst case time complexity: O(n^2).
Best case, worst case and average case have the same time complexity because this method requires all users to be retrieved from the array list therefore there is no other choice but for the system to traverse the entire array list.

getUsersContaining(): Best case time complexity: O(nlog(n)); Average case time complexity: O(nlog(n)); Worst case time complexity: O(n^2). 
All cases' time complexities depend on the quick sort algorithm.

getUsersJoinedBefore(): Best case time complexity: O(nlog(n)); Average case time complexity: O(nlog(n)); Worst case time complexity: O(n^2).
Then again, best case, worst case and average cases have these time complexities because all users need to be sorted by date joined. I thought that by sorting the users by date joined, the best case time complexity could be improved to O(1), however the fact that we have to sort the users already means that we have to traverse the entire array list therefore the time complexity would be O(nlog(n)). 

Moving on to the sort algorithm used throughout the entirety of Witter: QUICKSORT!
I decided to implement the quick sort algorithms in UserStore, FollowerStore and WeetStore because it is an in-place sorting algorithm and therefore improves the overall cache performance.
Quick sorts' time complexities in each case are as follows:
Best case time complexity: O(nlog(n)); Average case time complexity: O(nlog(n)); Worst case time complexity: O(n^2).
Worst case would occur when the array to sort contains the most unbalanced partitions possible. Best case would occur when the array to sort contains partitions which are evenly balanced therefore also having an odd number of elements (as this would mean that the pivot is in the middle after partitioning). Although the worst case time complexity is O(n^2), it is very very unlikely to occur and as such I believe it is fine to implement it for Witter.
One thing to note from the way I have decided to sort the users is that I sort the array list everytime a user wants to be added (inputted) into it instead of sorting it everytime I want to output the array list. The idea behind this choice is that I have focused on what a real application would look like. If we consider a very large number of users being added into our data structure eg: 1000000, then sorting the array list is not such a bad idea as the time complexity would be greatly reduced when outputting data. If the data had to get sorted everytime a user wants to get all users in the array list, the time taken for this to occur would be incredibily high, whilst by sorting it at every addUser() we can output the array list just as it is when required. Furthermore, the fact that we already know that the output has to be sorted by date means that we can sort it from the very beginning as we are not going to be changing the order of the elements at any points.  
 *
 * @Credit to: Matt Leeke (lecture slides and labwork) and Parinthorn(Kate) Wiwatdirekkul as my lab partner.
 * @Adrian Cho: 1622228
 */

package uk.ac.warwick.java.cs126.services;

import uk.ac.warwick.java.cs126.models.User;

import java.util.Date;


public class UserStore implements IUserStore {

	private SortedArrayList sortedArray;

	public UserStore() {

		this.sortedArray = new SortedArrayList();
	}

	public boolean addUser(User usr) {
		//Checks whether the user already exists in the array list, if he/she exists then return without doing anything, otherwise add the new user
		if (sortedArray.isEqual(usr.getId())) {
			return false;
		} else {
			sortedArray.add(usr);
			return true;
		}
	}

	public User getUser(int id) {
		//returns the user with the specific id provided
		return sortedArray.getId(id);
	}

	public User[] getUsers() {
		//Creates an array of users containing everyone
		User[] tempArray = new User[sortedArray.size()];
		//iterates through the array list, storing each element in a slot in the array of users
		for (int i = 0; i < sortedArray.size(); i++) {
			if (sortedArray.get(i) != null) {
				tempArray[i] = sortedArray.get(i);
			}
		}
		return tempArray;
	}

	public User[] getUsersContaining(String query) {

		int count = 0;
		//Iterates through the array list, incrementing a variable called count everytime it encounters a user whose name contains the specified query
		for (int i = 0; i < sortedArray.size(); i++) {

			if (sortedArray.get(i).getName().toLowerCase().contains(query.toLowerCase())) {
				count++;
			}
		}
		int index = 0;
		//Creates an array of users with a size of "count" (the number of users whose name contains the specified query
		User[] tempArray = new User[count];
		//Iterates through the array list, storing each user whose name contains the specified query in a slot in the array of users
		for (int j = 0; j < sortedArray.size(); j++) {

			if (sortedArray.get(j).getName().toLowerCase().contains(query.toLowerCase())) {
				tempArray[index] = sortedArray.get(j);
				index++;
			}
		}
		return tempArray;
	}

	public User[] getUsersJoinedBefore(Date dateBefore) {
		//Iterates through the array list, incrementing a variable called count everytime it encounters a user that joined before the specified date
		int count = 0;

		for (int i = 0; i < sortedArray.size(); i++) {

			if (sortedArray.get(i).getDateJoined().before(dateBefore)) {
				count++;
			}
		}
		int index = 0;
		//Creates an array of users with a size of "count" (the number of users that joined before the specified date
		User[] tempArray = new User[count];
		//Iterates through the array list, storing each user that joined before the specified date in a slot in the array of users
		for (int j = 0; j < sortedArray.size(); j++) {

			if (sortedArray.get(j).getDateJoined().before(dateBefore)) {
				tempArray[index] = sortedArray.get(j);
				index++;
			}
		}
		return tempArray;
	}

	public void quickSort(User[] unsortedArray, int low, int high) {

		int lo = low;
		int hi = high;
		if (low >= high) return;
		Date mid = unsortedArray[(lo+hi) / 2].getDateJoined();

		while (lo <= hi) {
			//search for the first unordered pair of values within the array
			while (lo <= hi && unsortedArray[lo].getDateJoined().after(mid)) {
				lo++;
			}
			while (lo <= hi && unsortedArray[hi].getDateJoined().before(mid)) {
				hi--;
			}
			//swap the pair of values
			if (lo <= hi) {
				User temp = unsortedArray[lo];
				unsortedArray[lo] = unsortedArray[hi];
				unsortedArray[hi] = temp;
				lo++;
				hi--;
			}
		}

		//increment low and decrement hi to make sure that the while loops are able to run again after swapping
		//call the function recursively until we are left with only 1 value left which should be in the correct place

		if (low < hi) {
		quickSort(unsortedArray, low, hi);
		}
		if (high > lo) {
		quickSort(unsortedArray, lo, high);
		}
	}

    private class SortedArrayList {

		User[] userArray;
		private int capacity;
		private int size;

		public SortedArrayList() {

			this.capacity = 100;
			this.userArray = new User[capacity];
			this.size = 0;
		}

		public int size() {

			return size;
		}

		public boolean add(User usr) {

			if (size < capacity) {
			//Check if there is an available slot for the new user within the array
				userArray[size] = usr;
			//If there is, add the user and increment the size of the array by 1
				size++;
				quickSort(userArray, 0, (size - 1));
				return true;
			} else if (size >= capacity) {
			//Otherwise, transfer all the users from the original array to a temporary array with a bigger capacity and add the user to the last slot
				capacity *= 2;
				User tempArray[] = new User[capacity];
				for(int i = 0; i < size; i++) {
					tempArray[i] = userArray[i];
				}
				tempArray[size] = usr;
				size++;
				userArray = tempArray;
				quickSort(userArray, 0, (size - 1));
				return true;
			} else {
				return false;
			}
		}

		public User getId(int id) {
			//Iterate through the array of users to retrieve the user with the specified ID
			for (int i = 0; i < size; i++) {
				if (userArray[i].getId() == id) {
					return userArray[i];
				}
			}
			return null;
		}

		public User get(int i) {
			//returns the array of users
			return userArray[i];
		}

		public boolean isEqual(int id) {
			//Iterates through the array of users to check whether the user with the specified ID exists
			for (int i = 0; i < size; i++) {
				if (userArray[i].getId() == id) {
					return true;
				}
			}
			return false;
		}
	}
}
