package kdtree;

public class KDNode {

	public double[] coordinates;
	public int alignment;
	public int id;
	public boolean checked;
	public boolean orientation;
	public KDNode parent;
	public KDNode left;
	public KDNode right;

	public KDNode (double[] point, int alignment) {
		coordinates = new double[2];
		this.alignment = alignment;
		for (int i = 0; i < 2; i++) {
			coordinates[i] = point[i];
		}
		left = null;
		right = null;
		parent = null;
		checked = false;
		id = 0;
	}

	public KDNode findParent (double[] point) {
		KDNode parent = null;
		KDNode next = this;
		int split;
		while (next != null) {
			split = next.alignment;
			parent = next;
			if (point[split] > next.coordinates[split]) next = next.right;
			else next = next.left;
		}
		return parent;
	}

	public KDNode insert (double[] point) {
		KDNode parent = findParent(point);
		if (equal(point, parent.coordinates)) return null;
		KDNode newNode = new KDNode(point, parent.alignment + 1 < 2 ? parent.alignment + 1 : 0);
		newNode.parent = parent;
		if (point[parent.alignment] > parent.coordinates[parent.alignment]) {
			parent.right = newNode;
			newNode.orientation = true;
		} else {
			parent.left = newNode;
			newNode.orientation = false;
		}
		return newNode;
	}

	// This method checks if two points are the same
	boolean equal (double[] sourcePoint, double[] destinationPoint) {
		for (int i = 0; i < 2; i++) {
			if (sourcePoint[i] != destinationPoint[i]) {
				return false;
			}
		}
		return true;
	}

	// This method returns the euclidian distance as squared
	public double distanceSquared (double[] sourcePoint, double[] destinationPoint) {
		double s = 0;
		// x^2 + y^2
		for (int i = 0; i < 2; i++) {
			s += (sourcePoint[i] - destinationPoint[i]) * (sourcePoint[i] - destinationPoint[i]);
		}
		return s;
	}
}
