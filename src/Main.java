
import java.awt.Component;

import javax.swing.JFrame;

public class Main extends JFrame{

	private static final long serialVersionUID = 1L;
	private static int WIDTH = 1920;//1920;  //1920
	private static int HEIGHT = 1080;//1080;  //960
	
	public Main() {
		super("Binary Tree");
		setSize(WIDTH, HEIGHT);
		View theGame = new View(WIDTH-32, HEIGHT-64);
		((Component) theGame).setFocusable(true);
		getContentPane().add(theGame);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new Main();
	}

}
