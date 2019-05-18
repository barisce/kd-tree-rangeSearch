import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import kdtree.KDNode;
import kdtree.KDTree;

@SuppressWarnings("serial")
public class KDTreeVisualization extends JFrame {

	public int numpoints = 10000;
	public boolean drawPointsOutsideRadius = true;
	public KDTree kdt;
	public double[] player = {500, 500};
	Color bgColor = Color.BLACK;
	private double radius = 40;
	private JTextField textField;
	private JCheckBox drawUnnecessaryCheckBox;

	public KDTreeVisualization () {
		System.out.println("For " + numpoints + " points;");
		long start = System.currentTimeMillis();
		kdt = new KDTree(numpoints);
		fillPoints(kdt, numpoints);
		long end = System.currentTimeMillis();
		float sec = (end - start) / 1000F;
		System.out.printf("Creating kdTree took %.5f seconds\n", sec);

		start = System.currentTimeMillis();
		KDNode kdn = kdt.findNearest(player);
		end = System.currentTimeMillis();
		sec = (end - start) / 1000F;
		System.out.printf("Finding nearest neighbour took %.5f seconds\n", sec);
		System.out.println("The nearest neighbor is: (" + kdn.coordinates[0] + " , " + kdn.coordinates[1] + ")");

		JFrame f = new JFrame("KDTree implementation");
		f.setSize(800, 800);
//		f.setLocation(300, 300);
		f.setResizable(false);
		f.setBackground(bgColor);
		JPanel p = new JPanel() {
			{
				addMouseListener(new MouseAdapter() {
					public void mousePressed (MouseEvent e) {
						player[0] = e.getX();
						player[1] = e.getY();
						repaint();
						KDNode kdn = kdt.findNearest(player);
						System.out.println("The nearest neighbor is: (" + kdn.coordinates[0] + " , " + kdn.coordinates[1] + ")");
					}

					public void mouseReleased (MouseEvent e) {
					}
				});
				addMouseMotionListener(new MouseMotionAdapter() {
					public void mouseMoved (MouseEvent e) {
//						player[0] = e.getX();
//						player[1] = e.getY();
//						KDNode kdn = kdt.findNearest(player);
//						System.out.println("The nearest neighbor is: (" + kdn.coordinates[0] + " , " + kdn.coordinates[1] + ")");
//						repaint();
					}

					public void mouseDragged (MouseEvent e) {
						player[0] = e.getX();
						player[1] = e.getY();
						KDNode kdn = kdt.findNearest(player);
						System.out.println("The nearest neighbor is: (" + kdn.coordinates[0] + " , " + kdn.coordinates[1] + ")");
						repaint();
					}
				});
			}

			public void paint (Graphics g) {
				super.paint(g);
				for (KDNode point : kdt.allNodes) {
					if (point != null) {
						if (Math.sqrt(point.distanceSquared(point.coordinates, player)) < radius / 2) {
//							System.out.println("Radius: " + Math.sqrt(point.distanceSquared(point.coordinates, player, 2)));
							drawPoint(g, point.coordinates[0], point.coordinates[1], Color.RED);
						} else if (drawPointsOutsideRadius) {
							drawPoint(g, point.coordinates[0], point.coordinates[1], Color.WHITE);
						}
					}
				}
				drawPoint(g, player[0], player[1], Color.CYAN);
				g.drawOval((int) (player[0] - (radius / 2)), (int) (player[1] - (radius / 2)), (int) radius, (int) radius);
				g.setColor(Color.GREEN);
				g.drawString("(" + (int) player[0] + "," + (int) player[1] + ")", (int) player[0] + 5, (int) player[1] + 5);
			}
		};
		p.setBackground(bgColor);
		p.setSize(new Dimension(700, 700));
		f.add(p);

		setUI(f);
		f.setVisible(true);
	}

	public static void main (String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run () {
				new KDTreeVisualization();
			}
		});
	}

	private void setUI (JFrame f) {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Radius: ");//Make a label
		label.setSize(new Dimension(40, 40));
		panel.add(label);
		textField = new JTextField(20);
		textField.setSize(new Dimension(40, 40));
		textField.setText("40");
		panel.add(textField);
		JButton setRadiusButton = new JButton("Set Radius");
		setRadiusButton.setSize(new Dimension(40, 40));
		panel.add(setRadiusButton);
		drawUnnecessaryCheckBox = new JCheckBox("drawPointsOutsideRadius");
		drawUnnecessaryCheckBox.setSize(new Dimension(40, 40));
		drawUnnecessaryCheckBox.setSelected(true);
		panel.add(drawUnnecessaryCheckBox);
		f.add(panel, BorderLayout.SOUTH);
		setRadiusButton.addActionListener(new TextListener());
		drawUnnecessaryCheckBox.addItemListener(new CheckBoxListener());
	}

	private void fillPoints (KDTree kdt, int numpoints) {
		Random r = new Random();
		double[] point = new double[2];
		for (int i = 0; i < numpoints; i++) {
			point[0] = (r.nextInt(7700) + 100) / 10.0;
			point[1] = (r.nextInt(7100) + 100) / 10.0;
			kdt.add(point);
		}
	}

	private void drawPoint (Graphics g, double x, double y, Color color) {
		g.setColor(color);
		g.fillOval((int) x - 2, (int) y - 2, 4, 4);
		//g.drawString("(" + coordinates + "," + y + ")", (int) coordinates + 5, (int) y + 5);
	}

	class TextListener implements ActionListener {
		public void actionPerformed (ActionEvent event) {
			String radiusText = textField.getText();
			radius = Double.parseDouble(radiusText);
			repaint();
		}
	}

	class CheckBoxListener implements ItemListener {
		public void itemStateChanged (ItemEvent e) {
			Object source = e.getItemSelectable();
			if (source == drawUnnecessaryCheckBox) {
				drawPointsOutsideRadius = !drawPointsOutsideRadius;
			}
		}
	}
}