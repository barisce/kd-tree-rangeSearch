package kdtree;

public class KDTree {

	public int kdId;
	public KDNode root;
	public KDNode nearestNeighbour;
	public KDNode[] allNodes;
	public int allNodesCount;
	public int checkedNodesIndex;
	public KDNode[] checkedNodes;
	public double dMin;
	public double[] xMin;
	public double[] xMax;
	public boolean[] maxBoundary;
	public boolean[] minBoundary;
	public int nBoundary;

	public KDTree (int i) {
		root = null;
		kdId = 1;
		allNodesCount = 0;
		allNodes = new KDNode[i];
		checkedNodes = new KDNode[i];
		maxBoundary = new boolean[2];
		minBoundary = new boolean[2];
		xMin = new double[2];
		xMax = new double[2];
	}

	public boolean add (double[] x) {
		if (root == null) {
			root = new KDNode(x, 0);
			root.id = kdId++;
			allNodes[allNodesCount++] = root;
		} else {
			KDNode pNode;
			if ((pNode = root.insert(x)) != null) {
				pNode.id = kdId++;
				allNodes[allNodesCount++] = pNode;
			}
		}
		return true;
	}

	public KDNode findNearest (double[] point) {
		if (root == null) return null;
		checkedNodesIndex = 0;

		KDNode parent = root.findParent(point);
		nearestNeighbour = parent;
		dMin = root.distanceSquared(point, parent.coordinates);

		if (parent.equal(point, parent.coordinates)) {
			return nearestNeighbour;
		}

		searchParent(parent, point);
		uncheck();
		return nearestNeighbour;
	}

	// Checks the subtree and reports back if inside bounding box
	public void checkSubtree (KDNode node, double[] x) {
		if ((node == null) || node.checked) return;
		checkedNodes[checkedNodesIndex++] = node;
		node.checked = true;

		setBoundingBox(node, x);

		int dimension = node.alignment;
		double distance = node.coordinates[dimension] - x[dimension];

		if (distance * distance > dMin) {
			if (node.coordinates[dimension] > x[dimension]) checkSubtree(node.left, x);
			else checkSubtree(node.right, x);
		} else {
			checkSubtree(node.left, x);
			checkSubtree(node.right, x);
		}
	}

	// Finds the bounding box of a given point and finds nearest neighbour if it is one
	public void setBoundingBox (KDNode node, double[] point) {
		if (node == null) return;
		int totalDistance = 0;
		double distanceBetweenNodeAndPoint;
		for (int i = 0; i < 2; i++) {
			distanceBetweenNodeAndPoint = node.coordinates[i] - point[i];
			if (distanceBetweenNodeAndPoint > 0) {
				distanceBetweenNodeAndPoint *= distanceBetweenNodeAndPoint;
				if (!maxBoundary[i]) {
					if (distanceBetweenNodeAndPoint > xMax[i]) {
						xMax[i] = distanceBetweenNodeAndPoint;
					}
					if (xMax[i] > dMin) {
						maxBoundary[i] = true;
						nBoundary++;
					}
				}
			} else {
				distanceBetweenNodeAndPoint *= distanceBetweenNodeAndPoint;
				if (!minBoundary[i]) {
					if (distanceBetweenNodeAndPoint > xMin[i]) {
						xMin[i] = distanceBetweenNodeAndPoint;
					}
					if (xMin[i] > dMin) {
						minBoundary[i] = true;
						nBoundary++;
					}
				}
			}
			totalDistance += distanceBetweenNodeAndPoint;
			if (totalDistance > dMin) return;
		}
		if (totalDistance < dMin) {
			dMin = totalDistance;
			nearestNeighbour = node;
		}
	}

	// Traverses back to parent node and checks its subtrees
	public KDNode searchParent (KDNode parent, double[] point) {
		for (int i = 0; i < 2; i++) {
			xMin[i] = 0;
			xMax[i] = 0;
			maxBoundary[i] = false;
			minBoundary[i] = false;
		}
		nBoundary = 0;
		KDNode searchRoot = parent;

		while (parent != null && (nBoundary != 2 * 2)) {
			checkSubtree(parent, point);
			searchRoot = parent;
			parent = parent.parent;
		}
		return searchRoot;
	}

	// Clears the checked flag of all nodes
	public void uncheck () {
		for (int n = 0; n < checkedNodesIndex; n++)
			checkedNodes[n].checked = false;
	}
}
