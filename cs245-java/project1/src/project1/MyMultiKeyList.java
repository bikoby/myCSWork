package project1;

import java.util.NoSuchElementException;

public class MyMultiKeyList implements MultiKeyList {

	private final int length;
	private Node[] head;
	private int numOfData;

	public MyMultiKeyList(int length) {
		this.length = length;
		this.head = new Node[length];
		this.numOfData = 0;
	}

	// Adds an object to the list. If the length of keys is not the same
	// as the number of keys in the list, throw an IllegalArgumentException
	@Override
	public void add(Comparable[] keys, Object data)
			throws IllegalArgumentException {
		if (keys.length != this.length) {
			throw new IllegalArgumentException();
		} else {
			Node newNode = new Node(keys, length, data);
			if (head[0] == null) {
				for (int i = 0; i < length; i++) {
					head[i] = newNode;
				}
			} else {
				for (int keyIndex = 0; keyIndex < length; keyIndex++) {
					addByKeyindex(keyIndex, newNode);
				}
			}
			numOfData++;
		}
	}

	private void addByKeyindex(int index, Node newNode) {
		Node temp = head[index];
		while (temp != null) {
			if (temp.getKey(index).compareTo(newNode.getKey(index)) > 0) {
				if (temp.getPrev(index) != null) {
					temp.getPrev(index).setNext(index, newNode);
					newNode.setPrev(index, temp.getPrev(index));
					newNode.setNext(index, temp);
					temp.setPrev(index, newNode);
				} else {
					newNode.setNext(index, temp);
					temp.setPrev(index, newNode);
					head[index] = newNode;
				}
				break;
			} else {
				if (temp.getNext(index) == null) {
					temp.setNext(index, newNode);
					newNode.setPrev(index, temp);
					break;
				}
				temp = temp.getNext(index);
			}
		}

	}

	// Get an iterator to iterate over a particular key. If keyIndex is not
	// within the range of allowed keys, throw an IllegalArgumentException
	@Override
	public MultiKeyListIterator iterator(int keyIndex) {
		if (keyIndex > length - 1) {
			throw new IllegalArgumentException();
		} else {
			return new MyIterator(keyIndex);
		}
	}

	// Get an interface to the element at a particular index of the list. If
	// keyIndex is not
	// within the range of allowed keys, throw an IllegalArgumentException
	@Override
	public ListElem get(int index, int keyIndex) {
		if (keyIndex > length - 1 || index > numOfData) {
			throw new IllegalArgumentException();
		} else {
			Node temp = head[keyIndex];
			for (int i = 0; i < index; i++) {
				temp = temp.getNext(keyIndex);
			}
			return temp;
		}
	}

	private void removeP(Node todel) {
		for (int i = 0; i < length; i++) {
			if (todel.getPrev(i) == null) {
				head[i] = todel.getNext(i);
				todel.getNext(i).setPrev(i, null);
			} else if (todel.getNext(i) == null) {
				todel.getPrev(i).setNext(i, null);
			} else {
				todel.getPrev(i).setNext(i, todel.getNext(i));
				todel.getNext(i).setPrev(i, todel.getPrev(i));
			}
		}
	}

	// Remove the ith element in the list using the given key index.
	@Override
	public void removeIndex(int index, int keyIndex) {
		removeP((Node) get(index, keyIndex));

	}

	// Remove the element matching *all* keys
	@Override
	public void remove(Comparable[] keys) {
		int match = 0;
		int keyIndex = 0;
		MyIterator it = new MyIterator(keyIndex);
		do {
			for (int i = 0; i < length; i++) {
				if (keys[i].compareTo(it.getCurrent().key(i)) != 0) {
					break;
				}
				match++;
			}
			if (match == length) {
				it.next();
				it.remove();
				numOfData--;
				break;
			}
			it.next();
			match = 0;
		} while (it.hasNext());

	}

	// Remove the element matching the key at the given index
	@Override
	public void remove(Comparable key, int keyIndex) {
		MyIterator it = new MyIterator(keyIndex);
		while (it.hasNext()) {
			if (key.compareTo(it.current.getKey(keyIndex)) == 0) {
				it.next();
				it.remove();
				break;
			} else {
				it.next();
			}
		}

	}

	public int size() {
		return this.numOfData;
	}

	public int length() {
		return this.length;
	}

	public class MyIterator implements MultiKeyListIterator {

		private Node current;
		private Node prev;
		private int keyIndex;
		private Node todel;

		public MyIterator(int keyIndex) {
			current = head[keyIndex];
			this.keyIndex = keyIndex;
		}

		@Override
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public ListElem next() throws NoSuchElementException {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			todel = current;
			prev = current;
			current = current.getNext(keyIndex);
			return todel;

		}

		@Override
		public boolean hasPrevious() {
			return prev != null;
		}

		@Override
		public ListElem previous() throws NoSuchElementException {
			if (!hasPrevious()) {
				throw new NoSuchElementException();
			}
			current = prev;
			prev = current.getPrev(keyIndex);
			todel = current;
			return current;
		}

		// Removes the element last returned by next() or previous(). If remove
		// is
		// called before next is called, or of remove is called twice in a row
		// without an intervening call to next or previous, then an
		// IllegalStateExeception is thrown
		@Override
		public void remove() throws IllegalStateException {
			if (todel == null) {
				throw new IllegalStateException();
			}
			removeP(todel);
			numOfData--;
			todel = null;
		}

		public ListElem getCurrent() {
			return this.current;
		}

	}

}
