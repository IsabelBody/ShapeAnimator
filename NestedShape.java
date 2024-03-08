/* ==============================================

   *  NestedShape.java : A subclass of RectangleShape. 
 *  It allows Shape to be a composite class and create shapes within the borders of other shapes. 
 *  YOUR UPI: ibod875
 *  ===============================================================================
 */

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;


public class NestedShape extends RectangleShape {
	private ArrayList<Shape> innerShapes = new ArrayList<Shape>();
	
	// No arg constructor
	NestedShape(){
		super();
		createInnerShape(0,0, width/2, height/2, color, borderColor, PathType.BOUNCING, ShapeType.RECTANGLE);
		setLabel(label);
	}
	
	// Overloaded Constructors
	NestedShape(int x, int y, int width, int height, int panelWidth, int panelHeight, Color fillColor, Color borderColor, PathType pathType){
		super(x, y, width, height, panelWidth, panelHeight, fillColor, borderColor, pathType);
		createInnerShape(0,0, width/2, height/2, color, borderColor, PathType.BOUNCING, ShapeType.RECTANGLE);
	}
	NestedShape(int width, int height) {
		super(Color.black, Color.black, PathType.BOUNCING);
		this.width = width;
		this.height = height;
	}
	
	// Methods
	public Shape createInnerShape(int x, int y, int w, int h, Color c, Color bc, PathType pt, ShapeType st) {
		Shape innerShape;
		if (st == ShapeType.RECTANGLE) {
			innerShape = new RectangleShape(x, y, w, h, width, height, c, bc, pt);
		} else if (st == ShapeType.OVAL) {
			innerShape = new OvalShape(x, y, w, h, width, height, c, bc, pt);
		} else {
			innerShape = new NestedShape(x, y, w, h, width, height, c, bc, pt);
		}
		innerShape.setParent(this);
		innerShapes.add(innerShape);
		return innerShape;
	}
	
	public Shape createInnerShape(PathType pt, ShapeType st) {
		Shape innerShape;
		if (st == ShapeType.RECTANGLE) {
			innerShape = new RectangleShape(0, 0, width/2, height/2, width, height, color, borderColor, pt);
		} else if (st == ShapeType.OVAL) {
			innerShape = new OvalShape(0, 0, width/2, height/2, width, height, color, borderColor, pt);
		} else {
			innerShape = new NestedShape(0, 0, width/2, height/2, width, height, color, borderColor, pt);
		}
		innerShape.setParent(this);
		innerShapes.add(innerShape);
		return innerShape;
	}

	public Shape getInnerShapeAt(int index) {
		return innerShapes.get(index);
	}
	public int getSize() {
		return innerShapes.size();
	}
	public void draw(Graphics g) {
		g.setColor(Color.black);
		g.drawRect(x, y, width, height);
		g.translate(x, y);
		
		for (Shape shape:innerShapes) {
			shape.draw(g);
			if (shape.isSelected()) {
				shape.drawHandles(g);
			}
			shape.setLabel(label);
		}
		g.translate(-x, -y);
	}
	public void move() {
		 super.move();
		 for (Shape shape:innerShapes) {
		     shape.move();
		 }	 
	}

	public int indexOf(Shape s) {
		return innerShapes.indexOf(s);
	}
	
	public void addInnerShape(Shape s) {
		innerShapes.add(s);
		s.setParent(this);
	}
	public void removeInnerShape(Shape s) {
		innerShapes.remove(s);
		s.setParent(null);
	}
	public void removeInnerShapeAt(int index) {
		Shape shape = innerShapes.remove(index);
		shape.setParent(null);
	}
	public ArrayList<Shape> getAllInnerShapes() {
		return innerShapes;
	}
}
