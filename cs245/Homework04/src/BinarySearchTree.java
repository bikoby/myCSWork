class BinarySearchTree<T extends Comparable<T>> {

	private class BSTNode {
		public T data;
		public BSTNode left;
		public BSTNode right;

		BSTNode(T newdata) {
			data = newdata;
		}
	}

	private BSTNode root;

	public void Insert(T elem) {
		root = Insert(root, elem);
	}

	public boolean Find(T elem) {
		return Find(root, elem);
	}

	public void Delete(T elem) {
		root = Delete(root, elem);
	}

	/*
	 * Returns the ith largest element in the binary search tree. So,
	 * ElementAt(0) would return the smallest element in the tree, ElementAt(1)
	 * would return the second smallest element in the tree, and so forth. This
	 * method does not need to be efficient it only needs to compute the correct
	 * value.
	 */
	public T ElementAt(int i) {
		return ElementAtR(root, i, 0);
	}

	private T ElementAtR(BSTNode tree, int i, int num) {
		if (tree != null) {
			num += numNodes(tree.left);
			if (num > i) {
				return ElementAtR(tree.left, i, 0);
			} else if (num < i) {
				return ElementAtR(tree.right, i, num + 1);
			} else {
				return tree.data;
			}
		}
		return null;
	}

	/*
	 * Returns the number of elements in the tree greater or equal to low and
	 * less than or equal to high. Be sure to make your code as efficient as
	 * possible -- only examine the sections of the tree that you need to in
	 * order to compute the desired information. Note that you need to be able
	 * to get the correct results for BSTs that have duplicate elements
	 */
	public int NumBetween(T low, T high) {
		return NumBetweenR(root, low, high);
	}

	private int NumBetweenR(BSTNode tree, T low, T high) {
		if (tree != null) {
			if (tree.data.compareTo(low) < 0) {
				return NumBetweenR(tree.right, low, high);
			} else if (tree.data.compareTo(high) > 0) {
				return NumBetweenR(tree.left, low, high);
			} else {
				return 1 + NumBetweenR(tree.left, low, high)
						+ NumBetweenR(tree.right, low, high);
			}
		}
		return 0;
	}

	/*
	 * Reorders the tree so that it is balanced -- that is, so the height is as
	 * small as possible. The easiest way to rebalance the tree is to:
	 * 
	 * 1.Create an array that contains all of the elements in the tree in order
	 * (which you can do by using a modified version of print that "prints" to
	 * an array)
	 * 
	 * 2.Create a new tree, by inserting all of the elements from the array into
	 * the tree. In order to make the tree balanced, you are likely going to
	 * want to write a recursive method to do the insertions: First insert the
	 * element in the middle of the array. Then recursively insert the rest of
	 * the elements in the array (you will likely want to make two recursive
	 * calls here)
	 */
	public void Balance() {
		int size = numNodes(root);
		Comparable[] array = new Comparable[size];
		getArray(root, array, 0);
		root = null;
		insertMid(array, 0, size - 1);
		insertRestOdd(array, 0, size - 1);
	}

	private void insertRestOdd(Comparable[] array, int min, int max) {
		int mid = min + (max - min) / 2;
		if (min < max) {
			if (max - min == 1) {
				Insert((T) array[mid + 1]);
				return;
			}
			if ((mid - min - 1) % 2 == 0) {
				insertRestEven(array, min, mid - 1);
			} else {
				insertRestOdd(array, min, mid - 1);
			}
			if ((max - mid - 1) % 2 == 0) {
				insertRestEven(array, mid + 1, max);
			} else {
				insertRestOdd(array, mid + 1, max);
			}

		}

	}

	private void insertRestEven(Comparable[] array, int min, int max) {
		int mid = min + (max - min) / 2;
		if (min < max) {
			if ((mid - min - 1) % 2 == 0) {
				insertRestEven(array, min, mid - 1);
			} else {
				insertRestOdd(array, min, mid - 1);
			}
			if ((max - mid - 1) % 2 == 0) {
				insertRestEven(array, mid + 1, max);
			} else {
				insertRestOdd(array, mid + 1, max);
			}
		} else if (min == max) {
			Insert((T) array[mid]);
		}
	}

	private void insertMid(Comparable[] array, int min, int max) {
		int mid = min + (max - min) / 2;
		if (min < max) {
			Insert((T) array[mid]);
			insertMid(array, min, mid - 1);
			insertMid(array, mid + 1, max);
		}
	}

	private int numNodes(BSTNode tree) {
		if (tree == null)
			return 0;
		return 1 + numNodes(tree.left) + numNodes(tree.right);
	}

	private int getArray(BSTNode tree, Comparable[] array, int index) {
		if (tree != null) {
			index += getArray(tree.left, array, index);
			array[index++] = tree.data;
			getArray(tree.right, array, index);
		}
		return 0;
	}

	public void Print() {
		Print(root);
	}

	public int Height() {
		return Height(root);
	}

	private int Height(BSTNode tree) {
		if (tree == null) {
			return 0;
		}
		return 1 + Math.max(Height(tree.left), Height(tree.right));
	}

	private boolean Find(BSTNode tree, T elem) {
		if (tree == null) {
			return false;
		}
		if (elem.compareTo(tree.data) == 0) {
			return true;
		}
		if (elem.compareTo(tree.data) < 0) {
			return Find(tree.left, elem);
		} else {
			return Find(tree.right, elem);
		}
	}

	private T Minimum(BSTNode tree) {
		if (tree == null) {
			return null;
		}
		if (tree.left == null) {
			return tree.data;
		} else {
			return Minimum(tree.left);
		}
	}

	private void Print(BSTNode tree) {
		if (tree != null) {
			Print(tree.left);
			System.out.println(tree.data);
			Print(tree.right);
		}
	}

	private BSTNode Insert(BSTNode tree, T elem) {
		if (tree == null) {
			return new BSTNode(elem);
		}
		if (elem.compareTo(tree.data) < 0) {
			tree.left = Insert(tree.left, elem);
			return tree;
		} else {
			tree.right = Insert(tree.right, elem);
			return tree;
		}
	}

	private BSTNode Delete(BSTNode tree, T elem) {
		if (tree == null) {
			return null;
		}
		if (tree.data.compareTo(elem) == 0) {
			if (tree.left == null) {
				return tree.right;
			} else if (tree.right == null) {
				return tree.left;
			} else {
				if (tree.right.left == null) {
					tree.data = tree.right.data;
					tree.right = tree.right.right;
					return tree;
				} else {
					tree.data = RemoveSmallest(tree.right);
					return tree;
				}
			}
		} else if (elem.compareTo(tree.data) < 0) {
			tree.left = Delete(tree.left, elem);
			return tree;
		} else {
			tree.right = Delete(tree.right, elem);
			return tree;
		}
	}

	T RemoveSmallest(BSTNode tree) {
		if (tree.left.left == null) {
			T smallest = tree.left.data;
			tree.left = tree.left.right;
			return smallest;
		} else {
			return RemoveSmallest(tree.left);
		}
	}

	public static void main(String args[])

	{
		BinarySearchTree<Integer> t = new BinarySearchTree<Integer>();
		for (int x = 0; x < 31; x++)
			t.Insert(new Integer(x));
		System.out.println(t.ElementAt(new Integer(5)));
		System.out.println(t.NumBetween(new Integer(10), new Integer(15)));
		System.out.println(t.Height());
		t.Balance();
		System.out.println(t.ElementAt(new Integer(5)));
		System.out.println(t.NumBetween(new Integer(10), new Integer(15)));
		System.out.println(t.Height());
	}

}