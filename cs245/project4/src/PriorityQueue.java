public class PriorityQueue {

	private MinHeap minHeap;

	public PriorityQueue(int length) {
		this.minHeap = new MinHeap(length);
	}

	// Inserts the integer element into the queue, using the priority.
	void insert(int elem, int priority) {
		Key k = new Key(elem, priority);
		this.minHeap.insert(k);
	}

	// Removes the element with the smallest priority from the queue, and
	// returns it.
	int remove_min() {
		Key k = this.minHeap.removemin();
		if (k != null) {
			return k.id;
		} else {
			return -1;
		}
	}

	// Reduce the priority of the element elem in the priority queue to
	// new_priority,rearranging the queue as necessary. To implement this method
	// efficiently, you will need to keep track of where each element is in the
	// queue. The simplest way to do it is to store an array of pointers into
	// the queue.
	void reduce_key(int elem, int new_priority) {
		this.minHeap.reduce(elem, new_priority);
	}

	public class Key {
		private int id;
		private int distance;

		Key(int id, int distance) {
			this.id = id;
			this.distance = distance;
		}
	}

	public class MinHeap {
		private Key[] Heap;
		private int maxsize;
		private int size;
		private int[] pos;

		public MinHeap(int max) {
			maxsize = max;
			Heap = new Key[maxsize];
			size = 0;
			Heap[0] = new Key(-1, Integer.MIN_VALUE);
			pos = new int[max];
		}

		private int leftchild(int pos) {
			return 2 * pos;
		}

		private int rightchild(int pos) {
			return 2 * pos + 1;
		}

		private int parent(int pos) {
			return pos / 2;
		}

		private boolean isleaf(int pos) {
			return ((pos > size / 2) && (pos <= size));
		}

		private void swap(int pos1, int pos2) {
			Key tmp;

			tmp = Heap[pos1];
			Heap[pos1] = Heap[pos2];
			Heap[pos2] = tmp;

			int temPos = pos[Heap[pos1].id];
			pos[Heap[pos1].id] = pos[Heap[pos2].id];
			pos[Heap[pos2].id] = temPos;
		}

		public void insert(Key elem) {
			size++;
			Heap[size] = elem;
			int current = size;
			pos[elem.id] = current;
			while (Heap[current].distance < Heap[parent(current)].distance) {
				swap(current, parent(current));
				current = parent(current);
			}
		}

		public void print() {
			int i;
			for (i = 1; i <= size; i++)
				System.out.print(Heap[i] + " ");
			System.out.println();
		}

		public Key removemin() {
			if (size > 0) {
				swap(1, size);
				size--;
				if (size > 0)
					pushdown(1);
				return Heap[size + 1];
			}
			return null;
		}

		private void pushdown(int position) {
			int smallestchild;
			while (!isleaf(position)) {
				smallestchild = leftchild(position);
				if ((smallestchild < size)
						&& (Heap[smallestchild].distance > Heap[smallestchild + 1].distance))
					smallestchild = smallestchild + 1;
				if (Heap[position].distance <= Heap[smallestchild].distance)
					return;
				swap(position, smallestchild);
				position = smallestchild;
			}
		}

		private void reduce(int elem, int new_priority) {
			int pos = this.pos[elem];
			this.Heap[pos].distance = new_priority;
			pushdown(pos);
		}
	}

}
