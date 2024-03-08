/*
 * ==========================================================================================
 * AnimationViewer.java : Moves shapes around on the screen according to different paths.
 * It is the main drawing area where shapes are added and manipulated.
 * YOUR UPI: ibod875
 * ==========================================================================================
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractListModel;
import javax.swing.JComponent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

class AnimationViewer extends JComponent implements Runnable {
	private Thread animationThread = null; // the thread for animation
	private static int DELAY = 120; // the current animation speed
	private ShapeType currentShapeType = Shape.DEFAULT_SHAPETYPE; // the current shape type,
	private PathType currentPathType = Shape.DEFAULT_PATHTYPE; // the current path type
	private Color currentColor = Shape.DEFAULT_COLOR; // the current fill colour of a shape
	private Color currentBorderColor = Shape.DEFAULT_BORDER_COLOR;
	private int currentPanelWidth = Shape.DEFAULT_PANEL_WIDTH, currentPanelHeight = Shape.DEFAULT_PANEL_HEIGHT,currentWidth = Shape.DEFAULT_WIDTH, currentHeight = Shape.DEFAULT_HEIGHT;
	private String currentLabel = Shape.DEFAULT_LABEL;
	protected NestedShape root = new NestedShape(currentPanelWidth, currentPanelHeight);
	protected MyModel model = new MyModel();

	public AnimationViewer() {
		start();
		addMouseListener(new MyMouseAdapter());
	}
	public void setCurrentLabel(String text) {
		currentLabel = text;
		for (Shape currentShape : root.getAllInnerShapes())
			if (currentShape.isSelected())
				currentShape.setLabel(currentLabel);
	}
	public void setCurrentColor(Color bc) {
		currentColor = bc;
		for (Shape currentShape: root.getAllInnerShapes())
			if ( currentShape.isSelected())
				currentShape.setColor(currentColor);
	}
	public void setCurrentBorderColor(Color bc) {
		currentBorderColor = bc;
		for (Shape currentShape: root.getAllInnerShapes())
			if ( currentShape.isSelected())
				currentShape.setBorderColor(currentBorderColor);
	}
	public void setCurrentHeight(int h) {
		currentHeight = h;
		for (Shape currentShape: root.getAllInnerShapes())
			if ( currentShape.isSelected())
				currentShape.setHeight(currentHeight);
	}
	public void setCurrentWidth(int w) {
		currentWidth = w;
		for (Shape currentShape: root.getAllInnerShapes())
			if ( currentShape.isSelected())
				currentShape.setWidth(currentWidth);
	}

	// when the user clicks on the panel.
	class MyMouseAdapter extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
		
			boolean found = false;
			// checking if new coords map over with any existing shape. 
			for (Shape currentShape : root.getAllInnerShapes()) {
				// if the mousepoint is within a shape, then set the shape to selected.
				if (currentShape.contains(e.getPoint())) { 
					// after this we should have options to delete or add. 
					// it needs to become the getLastSelectedPathComponent
					currentShape.setSelected(!currentShape.isSelected());
					
					found = true;
					
				}
				model.insertNodeInto(currentShape, root); // added by me
			}
			// else the spot is empty, add a new shape there.
			if (found == false) {
				root.createInnerShape(e.getX(), e.getY(), currentWidth, currentHeight, currentColor, currentBorderColor, currentPathType, currentShapeType);
				// firemethod. (added by me)
				int[] childIndices = {root.getSize()-1};
				Object[] children = {root.getInnerShapeAt(root.getSize()-1)};
				model.fireTreeNodesInserted(this, root.getPath(), childIndices, children);
			}

		}
	}
	public final void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (Shape currentShape : root.getAllInnerShapes()) {
			currentShape.move();
			currentShape.draw(g);
			currentShape.drawHandles(g);
			currentShape.drawString(g);
		}
	}
	public void resetMarginSize() {
		currentPanelWidth = getWidth();
		currentPanelHeight = getHeight();
		for (Shape currentShape : root.getAllInnerShapes())
			currentShape.resetPanelSize(currentPanelWidth, currentPanelHeight);
	}

	// you don't need to make any changes after this line ______________
	public String getCurrentLabel() {return currentLabel;}
	public int getCurrentHeight() { return currentHeight; }
	public int getCurrentWidth() { return currentWidth; }
	public Color getCurrentColor() { return currentColor; }
	public Color getCurrentBorderColor() { return currentBorderColor; }
	public void setCurrentShapeType(ShapeType value) {currentShapeType = value;}
	public void setCurrentPathType(PathType value) {currentPathType = value;}
	public ShapeType getCurrentShapeType() {return currentShapeType;}
	public PathType getCurrentPathType() {return currentPathType;}
	public void update(Graphics g) {
		paint(g);
	}
	public void start() {
		animationThread = new Thread(this);
		animationThread.start();
	}
	public void stop() {
		if (animationThread != null) {
			animationThread = null;
		}
	}
	public void run() {
		Thread myThread = Thread.currentThread();
		while (animationThread == myThread) {
			repaint();
			pause(DELAY);
		}
	}
	private void pause(int milliseconds) {
		try {
			Thread.sleep((long) milliseconds);
		} catch (InterruptedException ie) {}
	}

	// inner class
	class MyModel extends AbstractListModel<Shape> implements TreeModel {
		private ArrayList<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();
		private ArrayList<Shape> selectedShapes = new ArrayList<Shape>();
		MyModel(){
			selectedShapes = root.getAllInnerShapes();
		}
		public int getSize() {
			return selectedShapes.size();
		}
		public Shape getElementAt(int index) {
			return selectedShapes.get(index);
		}
		public void reload(NestedShape selected) {
			selectedShapes = selected.getAllInnerShapes();
			fireContentsChanged(this, 0, selected.getSize());
		}

		public Shape getRoot() {
			return root;
		}
		public boolean isLeaf(Object node) {
			return !(node instanceof NestedShape);
		}
		public boolean isRoot(Shape selectedNode) {
			return (selectedNode.equals(root));
		}
		public Object getChild(Object parent, int index) {
			try {
				NestedShape nestParent = (NestedShape)parent;
				return nestParent.getAllInnerShapes().get(index);
			} catch (Exception e) {
				return null;
			}			
		}
		public int getChildCount(Object parent) {
			try {
				NestedShape nestParent = (NestedShape)parent;
				return nestParent.getAllInnerShapes().size();
			} catch (Exception e) {
				return 0;
			}
		}
		public int getIndexOfChild(Object parent, Object child) {
			try {
				NestedShape nestParent = (NestedShape)parent;
				return nestParent.getAllInnerShapes().indexOf(child);
			} catch (Exception e) {
				return -1;
			}
		}
		public void addTreeModelListener(final TreeModelListener tml) {
			treeModelListeners.add(tml);

		}
		public void removeTreeModelListener(final TreeModelListener tml) {
			treeModelListeners.remove(tml);
		}
		public void valueForPathChanged(TreePath path, Object newValue) {}
		public void fireTreeNodesInserted(Object source, Object[] path,int[] childIndices,Object[] children) {
			TreeModelEvent object = new TreeModelEvent(source, path, childIndices, children);
			for (TreeModelListener tml:treeModelListeners) {
				tml.treeNodesInserted(object);
			}
			System.out.printf("Called fireTreeNodesInserted: path=%s, childIndices=%s, children=%s\n", Arrays.toString(path), Arrays.toString(childIndices), Arrays.toString(children));
		}
		public void insertNodeInto(Shape newChild, NestedShape parent) {
			int[] childIndices = {parent.indexOf(newChild)};
			Object[] children = {newChild};
			fireTreeNodesInserted(this, parent.getPath(), childIndices, children);
		}
		public void addShapeNode(NestedShape selectedNode) {
			if (selectedNode.equals(root)) {
				selectedNode.createInnerShape(0, 0, currentWidth, currentHeight, currentColor, currentBorderColor, currentPathType, currentShapeType);
			} else {
				selectedNode.createInnerShape(currentPathType, currentShapeType);	
			}
			insertNodeInto(selectedNode.getInnerShapeAt(selectedNode.getAllInnerShapes().size()-1), selectedNode);
			model.reload(selectedNode); // reload when node is added.

		}
		public void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices,Object[] children) {
			TreeModelEvent object = new TreeModelEvent(source, path, childIndices, children);
			for (TreeModelListener tml:treeModelListeners) tml.treeNodesRemoved(object);
			System.out.printf("Called fireTreeNodesRemoved: path=%s, childIndices=%s, children=%s\n", Arrays.toString(path), Arrays.toString(childIndices), Arrays.toString(children));

		}
		public void removeNodeFromParent(Shape selectedNode) {
			NestedShape parent = selectedNode.getParent();
			int[] indices = {parent.indexOf(selectedNode)};
			parent.removeInnerShape(selectedNode);
			Object[] children = {selectedNode};
			fireTreeNodesRemoved(this, parent.getPath(), indices, children);

			model.reload(parent); // reload the parent when node is removed.
		}
	}
}
