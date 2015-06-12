/*
 * Fill in the missing code
 */
public class LinkedList {

	private LinkElem head = null;
	private LinkElem tail = null;

	/*
	 * Adds an element to the linked list
	 */
	public void add(int elem) {
		LinkElem newNode = new LinkElem(elem);
		if (head == null) {
			head = newNode;
			tail = head;
		} else {
			if (tail != null) {
				tail.setNext(newNode);
				newNode.setNext(null);
				tail = newNode;
			}
		}
	}

	/*
	 * Prints all the elements of the list
	 */
	public void printList() {
		LinkElem curr = head;
		while (curr != null) {
			System.out.print(curr.elem() + " ");
			curr = curr.next();
		}
		System.out.println();
	}

	public static void printList(LinkElem beg) {
		LinkElem curr = beg;
		while (curr != null) {
			System.out.print(curr.elem() + " ");
			curr = curr.next();
		}
		System.out.println();
	}

	/*
	 * Finds the middle element in the linked list that starts with link
	 */
	private static LinkElem findMiddle(LinkElem link) {

		LinkElem slow, fast;
		slow = link;
		fast = link.next();

		while ((fast != null) && fast.next() != null) {
			slow = slow.next();
			fast = fast.next().next();

		}
		return slow;
	}

	/*
	 * Merges two linked lists: one with head1 and the other with head2 Returns
	 * the head of the merged list
	 */
	public static LinkElem mergeLists(LinkElem head1, LinkElem head2) {
		LinkElem link1 = head1;
		LinkElem link2 = head2;
		LinkElem tempHead;
		LinkElem temp;
		if (link1.elem() > link2.elem()) {
			tempHead = link2;
			link2 = link2.next();
		} else {
			tempHead = link1;
			link1 = link1.next();
		}
		temp = tempHead;
		while (link1 != null && link2 != null) {
			if (link1.elem() > link2.elem()) {
				temp.setNext(link2);
				link2 = link2.next();
			} else {
				temp.setNext(link1);
				link1 = link1.next();
			}
			temp = temp.next();
		}
		if (link1 != null) {
			temp.setNext(link1);
		} else if (link2 != null) {
			temp.setNext(link2);
		}
		return tempHead;
	}

	/*
	 * Recursively divides the LinkedList into two sublists: one that goes from
	 * the element "begLink" to the middle element of the list; and another one
	 * that goes from the element after the middle element and till the
	 * "endLink". Merges the lists using mergeLists method and returns the new
	 * head.
	 */
	public static LinkElem divideList(LinkElem begLink, LinkElem endLink) {
		LinkElem middle = findMiddle(begLink);
		LinkElem middleN = middle.next();
		middle.setNext(null);
		if (endLink == begLink) {
			begLink.setNext(null);
			return begLink;
		} else if (middle == begLink) {
			begLink.setNext(null);
			endLink.setNext(null);
			return mergeLists(begLink, endLink);
		} else {
			LinkElem head1 = divideList(begLink, middle);
			LinkElem head2 = divideList(middleN, endLink);

			return mergeLists(head1, head2);
		}

	}

	/*
	 * Sort this linked list using merge sort This method should call divideList
	 */
	public void mergeSort() {
		head = divideList(head, tail);

	}

	public static void main(String[] args) {

		LinkedList list = new LinkedList();
		int[] test = { 15, 24, 66, 18, 14, 55, 9, 1, 2012, 78, 22, 37 };
		for (int i = 0; i < test.length; i++) {
			list.add(test[i]);
		}
		list.printList();

		list.mergeSort();

		list.printList();

	}

}