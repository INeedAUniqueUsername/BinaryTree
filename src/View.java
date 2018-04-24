

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class View extends JPanel implements KeyListener {
	private static final long serialVersionUID = 1L;
	private final Font FONT;	
	private Node root, selected;
	boolean typing;
	public View(int width, int height) {
		this.addKeyListener(this);
		Font f = null;
		try {
			InputStream input = getClass().getResourceAsStream("/Inconsolata-Regular.ttf");
			f = Font.createFont(Font.TRUETYPE_FONT, input).deriveFont(Font.BOLD, 16);
		} catch (FontFormatException | IOException e) {
			System.out.println("Using default font");
			f = new Font("Consolas", Font.PLAIN, 16);
			e.printStackTrace();
		}
		FONT = f;
		setVisible(true);
		
		selected = root = new Node(null);
		//selected = root = generateFullTree();
		//root.setData("Root");
		organize();
	}

	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.black);
		g.setFont(FONT);
		
		Queue<Node> nodes = new LinkedList<>();
		root.inorder(nodes);
		while(nodes.size() > 0) {
			nodes.remove().paint(g);
		}
		
		g.setColor(typing ? Color.RED : Color.BLUE);
		selected.paint(g);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(typing) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_ENTER:
				typing = false;
				break;
			case KeyEvent.VK_BACK_SPACE:
				selected.setData(selected.getData().substring(0, Math.max(0, selected.getData().length()-1)));
				break;
			default:
				char c = e.getKeyChar();
				if(c != KeyEvent.CHAR_UNDEFINED) {
					selected.setData(selected.getData() + c);
				}
				break;
			}
			repaint();
			return;
		}
		
		switch(e.getKeyCode()) {
		case KeyEvent.VK_UP:
			if(selected.hasParent()) {
				Node parent = selected.getParent();
				//Delete this node if it is empty and has no children
				if(!selected.hasData() && !selected.hasLeft() && !selected.hasRight()) {
					if(selected == parent.getLeft()) {
						parent.setLeft(null);
					} else if(selected == parent.getRight()) {
						parent.setRight(null);
					}
					organize();
				}
				selected = parent;
			}
			break;
		case KeyEvent.VK_RIGHT:
			if(!selected.hasRight()) {
				selected.setRight(new Node(selected));
				organize();
				
				//selected.getRight().setData(selected.getData() + "_R");
			}
			selected = selected.getRight();
			break;
		case KeyEvent.VK_LEFT:
			if(!selected.hasLeft()) {
				selected.setLeft(new Node(selected));
				organize();
				
				//selected.getLeft().setData(selected.getData() + "_L");
			}
			selected = selected.getLeft();
			break;
		case KeyEvent.VK_BACK_SPACE:
			if(selected.hasParent()) {
				Node parent = selected.getParent();
				if(selected == parent.getLeft()) {
					parent.setLeft(null);
				} else if(selected == parent.getRight()) {
					parent.setRight(null);
				}
				selected = parent;
				organize();
			}
			break;
		case KeyEvent.VK_ENTER:
			typing = true;
			break;
		case KeyEvent.VK_I:
			String data = JOptionPane.showInputDialog("Data");
			if(data != null) {
				selected.insert(data);
				organize();	
			}
			break;
		}
		repaint();
	}
	public void keyReleased(KeyEvent e) { }
	public void keyTyped(KeyEvent e) { }
	private void organize() {
		Queue<Node> order = new LinkedList<>();
		root.inorder(order);
		
		int leftMargin = 16;
		int topMargin = 16;
		
		int height = root.getHeight() + 1;
		int intervalX = Math.min(48, (getWidth() - 2 * leftMargin) / order.size());
		int intervalY = Math.min(48, (getHeight() - 2 * topMargin) / height);
		//int intervalX = 24, intervalY = 48;
		int count = order.size();
		for(int i = 0; i < count; i++) {
			Node n = order.remove();
			Point pos = new Point(leftMargin + i * intervalX, topMargin + n.getDepth() * intervalY);
			n.setPos(pos);
			//System.out.println("Node " + n.getData() + ": (" + pos.x + ", " + pos.y + "), Depth: " + n.getDepth() + ", Index: " + i);
		}
		//System.out.println();
	}
	private Node generateFullTree() {
		Node root = new Node(null);
		for(int i = 0; i < 10; i++) {
			Queue<Node> leaves = new LinkedList<>();
			root.getLeaves(leaves);
			leaves.forEach(leaf -> {
				leaf.setLeft(new Node(leaf));
				leaf.setRight(new Node(leaf));
			});
		}
		return root;
	}
}
class Node {
	private Node parent, left, right;
	private String data;
	private Point pos;
	
	private int width = 36;
	private int height = 36;
	
	public boolean hasParent() 			{ return parent != null; }
	public Node getParent()				{ return parent; }
	public void setParent(Node parent)	{ this.parent = parent; }
	public boolean hasLeft()			{ return left != null; }
	public Node getLeft()				{ return left; }
	public void setLeft(Node left)		{ this.left = left; }
	public boolean hasRight()			{ return right != null; }
	public Node getRight()				{ return right; }
	public void setRight(Node right)	{ this.right = right; }
	public boolean hasData()			{ return !data.equals(""); }
	public String getData()				{ return data; }
	public void setData(String data)	{ this.data = data; }
	
	public Node(Node parent) {
		this(parent, "");
	}
	public Node(Node parent, String data) {
		this.parent = parent;
		this.data = data;
		left = right = null;
		pos = new Point();
	}
	
	public void setPos(Point pos) {
		this.pos = pos;
	}
	public void paint(Graphics g) {
		g.drawOval(pos.x, pos.y, width, height);
		drawStringCentered(g, data);
		if(parent != null) {
			double angle = Math.atan2(parent.pos.y - pos.y, parent.pos.x - pos.x);
			double radius = Math.sqrt(Math.pow(width/2, 2) + Math.pow(height/2, 2)) - 6;
			
			Point center = new Point(pos.x + width/2, pos.y + height/2);
			Point center_parent = new Point(parent.pos.x + width/2, parent.pos.y + height/2);
			double length = center.distance(center_parent) - radius;
			g.drawLine(
					(int) (center.x + radius * Math.cos(angle)),
					(int) (center.y + radius * Math.sin(angle)),
					(int) (center.x + length * Math.cos(angle)),
					(int) (center.y + length * Math.sin(angle)));
		}
		
	}
	private void drawStringCentered(Graphics g, String s) {
		g.drawString(s, width/2 + pos.x - (g.getFontMetrics().stringWidth(s) / 2), 3 * height / 4 + pos.y - (g.getFont().getSize() / 2));
	}
	public int getDepth() {
		int result = 0;
		Node n = this.parent;
		while(n != null) {
			result++;
			n = n.parent;
		}
		return result;
	}
	public int getHeight() {
		int heightLeft = 0;
		int heightRight = 0;
		if(left != null) {
			heightLeft = 1 + left.getHeight();
		}
		if(right != null) {
			heightRight = 1 + right.getHeight();
		}
		return Math.max(heightLeft, heightRight);
	}
	public void pathFromRoot(Queue<Node> q) {
		if(parent != null) {
			pathFromRoot(q);
		}
		q.add(this);
	}
	public int getDescendants() {
		int result = 0;
		if(left != null) {
			result += 1 + left.getDescendants();
		}
		if(right != null) {
			result += 1 + right.getDescendants();
		}
		return result;
	}
	public void preorder(Queue<Node> q) {
		q.add(this);
		if(left != null) {
			left.inorder(q);
		}
		if(right != null) {
			right.inorder(q);
		}
	}
	public void inorder(Queue<Node> q) {
		if(left != null) {
			left.inorder(q);
		}
		q.add(this);
		if(right != null) {
			right.inorder(q);
		}
	}
	public void getLeaves(Queue<Node> q) {
		if(left == null && right == null) {
			q.add(this);
			return;
		}
		
		if(left != null) {
			left.getLeaves(q);
		}
		if(right != null) {
			right.getLeaves(q);
		}
	}
	public void insert(String data) {
		if(compare(data, this.data) <= 0) {
			if(left == null) {
				left = new Node(this, data);
			} else {
				left.insert(data);
			}
		} else {
			if(right == null) {
				right = new Node(this, data);
			} else {
				right.insert(data);
			}
		}
	}
	public static int compare(String s1, String s2) {
		try {
			Double d1 = Double.parseDouble(s1);
			Double d2 = Double.parseDouble(s2);
			return d1.compareTo(d2);
		} catch(NumberFormatException e) {
			return s1.compareTo(s2);
		}
	}
}