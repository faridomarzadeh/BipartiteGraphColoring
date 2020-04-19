import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

public class Coloring {

	static Graph graph = new Graph();

	public static void main(String[] args) {

		//RandomBipartite(graph, 200, 0.2,10);
		//graph.SetSides();
		// System.out.println(graph.check());

		 readGraph("graph.mtx");
		 makeBipartite(graph, "RBPM");
		 System.out.println(graph.check());
		List<Vertex> Input = graph.Order("EveryOther");
		//graph.SetSides();
		// System.out.println(graph.check());
		// graph.Order("Random");
		// System.out.println(graph.check());
		//FirstFit(graph, Input);
		//ResetColor(Input);
		//MyAlgorithm(graph, Input);
		CBIP(graph, Input);

	}

	public static void ResetColor(List<Vertex> Input) {
		for (Vertex vertex : Input) {
			vertex.color = -1;
		}
	}

	public static void readGraph(String filename) {
		try {
			File myObj = new File("graph.mtx");
			Scanner myReader = new Scanner(myObj);
			int i = 0;
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				if (data.contains("%") == false && i == 0) {
					String[] values;
					values = data.split(" ");
					int nodes = Integer.parseInt(values[0]);
					for (int j = 1; j <= nodes; j++) {
						Vertex v = new Vertex();
						v.value = j;
						graph.LNodes.add(v);
					}
					// System.out.println(graph.RightNodes.size());
					i++;
				} else if (i > 0) {
					String[] values;
					values = data.split(" ");
					Edge edge = new Edge();
					edge.v1 = graph.LNodes.get(Integer.parseInt(values[0]) - 1);
					edge.v2 = graph.LNodes.get(Integer.parseInt(values[1]) - 1);
					graph.edges.add(edge);
					// graph.edges.add(edge);
					/*
					 * graph.LNodes.get(Integer.parseInt(values[0]) - 1).neighbours
					 * .add(graph.LNodes.get(Integer.parseInt(values[1]) - 1));
					 * graph.LNodes.get(Integer.parseInt(values[1]) - 1).neighbours
					 * .add(graph.LNodes.get(Integer.parseInt(values[0]) - 1));
					 */
					// System.out.println(values[0]+"
					// "+graph.RightNodes.get(Integer.parseInt(values[0])-1).value);
					// System.out.println(data);
					// System.out.println(i);
					i++;
				}
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}

	private static void makeBipartite(Graph g, String method) {
		// Duplicate Method
		if (method == "DM") {
			List<Vertex> right = new ArrayList<Vertex>();
			int count = 0;
			// deep clone
			while (g.LNodes.size() > count) {
				Vertex v = new Vertex();
				v.value = g.LNodes.get(count).value;
				v.side = "right";
				right.add(v);
				count++;

			}
			g.RNodes = right;
			for (Edge edg : graph.edges) {
				Vertex leftv1 = g.LNodes.stream().filter(v -> v.value == edg.v1.value).findFirst().get();
				leftv1.neighbours.add(g.RNodes.stream().filter(v -> v.value == edg.v2.value).findFirst().get());
				Vertex leftv2 = g.LNodes.stream().filter(v -> v.value == edg.v2.value).findFirst().get();
				leftv2.neighbours.add(g.RNodes.stream().filter(v -> v.value == edg.v1.value).findFirst().get());
				Vertex rightv1 = g.RNodes.stream().filter(v -> v.value == edg.v1.value).findFirst().get();
				rightv1.neighbours.add(g.LNodes.stream().filter(v -> v.value == edg.v2.value).findFirst().get());
				Vertex rightv2 = g.RNodes.stream().filter(v -> v.value == edg.v2.value).findFirst().get();
				rightv2.neighbours.add(g.LNodes.stream().filter(v -> v.value == edg.v1.value).findFirst().get());
			}
		}
		//
		if (method == "RBPM") {
			int half = g.LNodes.size() / 2;
			List<Vertex> left = new ArrayList<Vertex>();
			int count = 0;
			List<Vertex> leftCopy = g.LNodes.subList(0, half);
			while (leftCopy.size() > count) {
				Vertex v = new Vertex();
				v.value = leftCopy.get(count).value;
				//v.neighbours = new ArrayList<Vertex>(leftCopy.get(count).neighbours);
				left.add(v);
				count++;
			}
			count = 0;
			List<Vertex> right = new ArrayList<Vertex>();
			List<Vertex> rightCopy = g.LNodes.subList(half, g.LNodes.size());
			while (rightCopy.size() > count) {
				Vertex v = new Vertex();
				v.value = rightCopy.get(count).value;
				//v.neighbours = new ArrayList<Vertex>(rightCopy.get(count).neighbours);
				right.add(v);
				count++;
			}
			g.LNodes = left;
			g.RNodes = right;
			
			for (Edge edg : graph.edges) {
				Vertex leftv1 = g.LNodes.stream().filter(v -> v.value == edg.v1.value).findFirst().isPresent()?g.LNodes.stream().filter(v -> v.value == edg.v1.value).findFirst().get():null;
				Vertex rightNeighbourv2=g.RNodes.stream().filter(v -> v.value == edg.v2.value).findFirst().isPresent()?g.RNodes.stream().filter(v -> v.value == edg.v2.value).findFirst().get():null;
				if(leftv1!=null && rightNeighbourv2!=null)
				{
				leftv1.neighbours.add(g.RNodes.stream().filter(v -> v.value == edg.v2.value).findFirst().get());
				}
				Vertex leftv2 = g.LNodes.stream().filter(v -> v.value == edg.v2.value).findFirst().isPresent()?g.LNodes.stream().filter(v -> v.value == edg.v2.value).findFirst().get():null;
				Vertex rightNeighbourv1= g.RNodes.stream().filter(v -> v.value == edg.v1.value).findFirst().isPresent()?g.RNodes.stream().filter(v -> v.value == edg.v1.value).findFirst().get():null;
				if(leftv2!=null && rightNeighbourv1!=null)
				{
				leftv2.neighbours.add(g.RNodes.stream().filter(v -> v.value == edg.v1.value).findFirst().get());
				}
				Vertex rightv1 = g.RNodes.stream().filter(v -> v.value == edg.v1.value).findFirst().isPresent()?g.RNodes.stream().filter(v -> v.value == edg.v1.value).findFirst().get():null;
				Vertex leftNeighbourv2=g.LNodes.stream().filter(v -> v.value == edg.v2.value).findFirst().isPresent()?g.LNodes.stream().filter(v -> v.value == edg.v2.value).findFirst().get():null;
				if(rightv1!=null && leftNeighbourv2!=null)
				{
				rightv1.neighbours.add(g.LNodes.stream().filter(v -> v.value == edg.v2.value).findFirst().get());
				}
				Vertex rightv2 = g.RNodes.stream().filter(v -> v.value == edg.v2.value).findFirst().isPresent()?g.RNodes.stream().filter(v -> v.value == edg.v2.value).findFirst().get():null;
				Vertex leftNeighbourv1=g.LNodes.stream().filter(v -> v.value == edg.v1.value).findFirst().isPresent()?g.LNodes.stream().filter(v -> v.value == edg.v1.value).findFirst().get():null;
				if(rightv2!=null && leftNeighbourv1!=null)
				{
				rightv2.neighbours.add(g.LNodes.stream().filter(v -> v.value == edg.v1.value).findFirst().get());
				}
			}
			/*
			for (Vertex vertex : left) {
				List<Vertex> neighbours = new ArrayList<Vertex>();
				for (Vertex v : right) {
					if (ContainsValue(vertex.neighbours, v.value)) {
						neighbours.add(v);
					}
				}
				vertex.neighbours = neighbours;
			}
			for (Vertex vertex : right) {
				List<Vertex> neighbours = new ArrayList<Vertex>();
				for (Vertex v : left) {
					if (ContainsValue(vertex.neighbours, v.value)) {
						neighbours.add(v);
					}

					vertex.neighbours = neighbours;
				}
			}*/
			//g.LNodes = left;
			//g.RNodes = right;
			// System.out.println(g.LNodes.size());
			// System.out.println(g.RNodes.size());
		}

	}

	public static boolean ContainsValue(final List<Vertex> list, final int value) {
		return list.stream().filter(o -> o.value == value).findFirst().isPresent();
	}

	public static void RandomBipartite(Graph g, int n, double p) {
		if (p < 0.0 || p > 1.0)
			throw new IllegalArgumentException("Probability must be between 0 and 1");
		List<Vertex> left = new ArrayList<Vertex>();
		List<Vertex> right = new ArrayList<Vertex>();
		// create nodes
		for (int i = 0; i < n; i++) {
			Vertex vLeft = new Vertex();
			vLeft.value = i;
			left.add(vLeft);
			Vertex vRight = new Vertex();
			vRight.value = i;
			right.add(vRight);
		}
		// add edges
		for (int i = 0; i < left.size(); i++) {
			for (int j = 0; j < right.size(); j++) {
				if (StdRandom.bernoulli(p)) {
					left.get(i).neighbours.add(right.get(j));
					right.get(j).neighbours.add(left.get(i));
				}
			}
		}
		g.LNodes = left;
		g.RNodes = right;

	}
	public static void RandomBipartite(Graph g, int n, double p,long seed) {
		if (p < 0.0 || p > 1.0)
			throw new IllegalArgumentException("Probability must be between 0 and 1");
		List<Vertex> left = new ArrayList<Vertex>();
		List<Vertex> right = new ArrayList<Vertex>();
		// create nodes
		for (int i = 0; i < n; i++) {
			Vertex vLeft = new Vertex();
			vLeft.value = i;
			left.add(vLeft);
			Vertex vRight = new Vertex();
			vRight.value = i;
			right.add(vRight);
		}
		StdRandom.setSeed(seed);
		// add edges
		for (int i = 0; i < left.size(); i++) {
			for (int j = 0; j < right.size(); j++) {
				if (StdRandom.bernoulli(p)) {
					left.get(i).neighbours.add(right.get(j));
					right.get(j).neighbours.add(left.get(i));
				}
			}
		}
		g.LNodes = left;
		g.RNodes = right;

	}

	public static int FirstFit(Graph g, List<Vertex> Input) {
		int max = 0;
		// List<Vertex> Input = g.Order(order);
		// System.out.println(Input.size());
		for (Vertex vertex : Input) {
			if (vertex.color == -1) {
				Set<Integer> colors = new HashSet<Integer>();
				for (Vertex v : vertex.neighbours) {
					colors.add(v.color);
				}
				if (colors.size() > 0) {
					vertex.color = ReturnSmallestColor(colors);
					if (vertex.color > max)
						max = vertex.color;
				} else {
					vertex.color = 1;
				}
			}
		}
		System.out.println(max);
		return 0;
	}

	public static int CBIP(Graph g, List<Vertex> Input) {
		int max = 0;
		int count = 1;
		for (Vertex vertex : Input) {
			updateNeighbourhood(vertex);
			if (vertex.color == -1) {
				String side = vertex.side;
				List<Vertex> connected = connectedComponent(vertex, Input.subList(0, count));
				if (connected.size() > 0) {
					List<Vertex> NSV = connected.stream().filter(o -> o.side.equals(side) == false)
							.collect(Collectors.toList());
					Set<Integer> colors = new HashSet<Integer>();
					for (Vertex v : NSV) {
						colors.add(v.color);
					}
					if (colors.size() > 0) {
						vertex.color = ReturnSmallestColor(colors);
						if (vertex.color > max)
							max = vertex.color;
					} else {
						vertex.color = 1;
					}
				} else {
					vertex.color = 1;
				}
			}
			count++;

		}
		System.out.println(max);
		return max;
	}

	public static int MyAlgorithm(Graph g, List<Vertex> Input) {
		int max = 0;
		int count = 1;
		Random rnd=new Random();
		for (Vertex vertex : Input) {
			updateNeighbourhood(vertex);
			boolean isCBIP = rnd.nextDouble()<0.5;
			if (isCBIP) {
				if (vertex.color == -1) {
					String side = vertex.side;
					List<Vertex> connected = connectedComponent(vertex, Input.subList(0, count));
					if (connected.size() > 0) {
						List<Vertex> NSV = connected.stream().filter(o -> o.side.equals(side) == false)
								.collect(Collectors.toList());
						Set<Integer> colors = new HashSet<Integer>();
						for (Vertex v : NSV) {
							colors.add(v.color);
						}
						if (colors.size() > 0) {
							vertex.color = ReturnSmallestColor(colors);
							if (vertex.color > max)
								max = vertex.color;
						} else {
							vertex.color = 1;
						}
					} else {
						vertex.color = 1;
					}
				}
				count++;
			} else {
				if (vertex.color == -1) {
					Set<Integer> colors = new HashSet<Integer>();
					for (Vertex v : vertex.neighbours) {
						colors.add(v.color);
					}
					if (colors.size() > 0) {
						vertex.color = ReturnSmallestColor(colors);
						if (vertex.color > max)
							max = vertex.color;
					} else {
						vertex.color = 1;
					}
				}
				count++;
			}
		}
		System.out.println(max);
		return max;
	}

	private static void updateNeighbourhood(Vertex vertex) {
		for (Vertex neighbour : vertex.neighbours) {
			if (!neighbour.neighbours.contains(vertex))
				neighbour.neighbours.add(vertex);

		}

	}

	public static List<Vertex> connectedComponent(Vertex vertex, List<Vertex> list) {
		TravelGraph(list, vertex);
		// System.out.println("size: "+list.size());
		// System.out.println("*********************");
		List<Vertex> connected = list.stream().filter(v -> v.visited == true).collect(Collectors.toList());
		vertex.visited = false;
		setVisitedFalse(list);
		return connected;
	}

	public static int ReturnSmallestColor(Set<Integer> colors) {
		for (int i = 1; i <= colors.size(); i++) {
			if (!colors.contains(i)) {
				// System.out.println("smallest"+i);
				return i;
			}
		}

		return Collections.max(colors) + 1;
	}

	public static void setVisitedFalse(List<Vertex> list) {
		for (Vertex vertex : list) {
			vertex.visited = false;
		}
	}

	public static void TravelGraph(List<Vertex> list, Vertex root) {
		// System.out.println(root.value+" "+root.side);
		root.visited = true;
		Iterator<Vertex> iterator = root.neighbours.listIterator();
		while (iterator.hasNext()) {
			Vertex v = iterator.next();
			Vertex newRoot = null;
			// System.out.println("neighbour: "+v.value);
			if (list.contains(v)) {
				newRoot = v;
				// System.out.println("n "+newRoot.value+" "+newRoot.side+" "+newRoot.visited);
				if (newRoot.visited == false) {
					// System.out.println("hello2");
					TravelGraph(list, newRoot);
				}
			}
		}
		/*
		 * graph.getGraphNodes().stream().filter(item ->
		 * item.getName().equalsIgnoreCase(root.getName())).findFirst().get().setVisited
		 * (true); Iterator<String> i = root.getAdjacencyList().listIterator(); while
		 * (i.hasNext()) { String name = i.next(); Node src = null; if
		 * (graph.getGraphNodes().stream().filter(item ->
		 * item.getName().equalsIgnoreCase(name)).findAny().isPresent()) { src =
		 * graph.getGraphNodes().stream().filter(item ->
		 * item.getName().equalsIgnoreCase(name)).findAny().get(); if (src.isVisited()
		 * == false) { DFS(graph, src); } } }
		 */
	}

}

class StdRandom {
	private static Random random = new Random();
	private static long seed; 
	
	 public static void setSeed(long s) {
	        seed   = s;
	        random = new Random(seed);
	    }
	 public static long getSeed() {
	        return seed;
	    }

	public static double uniform() {
		return random.nextDouble();
	}

	public static double uniform(long seed) {
		random.setSeed(seed);
		return random.nextDouble();
	}

	public static boolean bernoulli(double p) {
		if (!(p >= 0.0 && p <= 1.0))
			throw new IllegalArgumentException("probability p must be between 0.0 and 1.0: " + p);
		return uniform() < p;
	}
	public static boolean bernoulli(double p, long seed) {
		if (!(p >= 0.0 && p <= 1.0))
			throw new IllegalArgumentException("probability p must be between 0.0 and 1.0: " + p);
		return uniform(seed) < p;
	}

}

class Vertex {
	int value;
	List<Vertex> neighbours = new ArrayList<Vertex>();
	int color = -1;
	boolean visited = false;
	String side = "";

}

class Edge {
	Vertex v1;
	Vertex v2;
}

class Graph {

	static List<Edge> edges = new ArrayList<Edge>();
	static List<Vertex> LNodes = new ArrayList<Vertex>();
	static List<Vertex> RNodes = new ArrayList<Vertex>();

	public static boolean check() {
		for (Vertex vertex : LNodes) {
			// System.out.println(vertex.value);
			for (Vertex v : vertex.neighbours) {
				if (!v.neighbours.contains(vertex)) {
					// System.out.println("left "+v.value);

					// System.out.println(v.neighbours.stream().filter(i->i.value==vertex.value).findFirst().get()==vertex);
					return false;
				}
				// System.out.println("Correct left "+v.value);
			}
		}
		for (Vertex vertex : RNodes) {
			for (Vertex v : vertex.neighbours) {
				if (!v.neighbours.contains(vertex)) {
					// System.out.println("right "+v.value);
					return false;
				}
			}
		}
		return true;
	}

	public static void SetSides() {
		for (Vertex vertex : LNodes) {
			vertex.side = "left";
		}
		for (Vertex vertex : RNodes) {
			vertex.side = "right";
		}
	}

	/*
	 * generate Input sequence in VAM-PH model
	 */
	public static List<Vertex> Order(String method) {
		List<Vertex> Input = null;
		if (method == "Random") {
			Input = new ArrayList<Vertex>(LNodes);
			Input.addAll(RNodes);
			Collections.shuffle(Input);
			for (Vertex vertex : Input) {
				// System.out.println(vertex.neighbours.size());
				List<Vertex> neighbours = new ArrayList<Vertex>();
				for (Vertex neighbour : vertex.neighbours) {
					if (Input.indexOf(neighbour) > Input.indexOf(vertex))
						neighbours.add(neighbour);
				}
				vertex.neighbours.removeAll(neighbours);
				// System.out.println(neighbours.size());
				// System.out.println(vertex.neighbours.size());
			}

		}
		if (method == "UVOrder") {
			Input = new ArrayList<Vertex>(LNodes);
			Input.addAll(RNodes);
			// Collections.shuffle(Input);
			for (Vertex vertex : Input) {
				// System.out.println(vertex.neighbours.size());
				List<Vertex> neighbours = new ArrayList<Vertex>();
				for (Vertex neighbour : vertex.neighbours) {
					if (Input.indexOf(neighbour) > Input.indexOf(vertex))
						neighbours.add(neighbour);
				}
				vertex.neighbours.removeAll(neighbours);
				// System.out.println(neighbours.size());
				// System.out.println(vertex.neighbours.size());
			}
		}
		if (method == "EveryOther") {
			Input = new ArrayList<Vertex>();
			boolean left = true;
			int size = LNodes.size() + RNodes.size();
			// System.out.println(size);
			int leftIndex = 0;
			int rightIndex = 0;
			for (int i = 0; i < size; i++) {
				if (left) {
					Input.add(LNodes.get(leftIndex));
					leftIndex++;
					left = false;
				} else {
					Input.add(RNodes.get(rightIndex));
					rightIndex++;
					left = true;
				}
			}
			for (Vertex vertex : Input) {
				List<Vertex> neighbours = new ArrayList<Vertex>();
				for (Vertex neighbour : vertex.neighbours) {
					if (Input.indexOf(neighbour) > Input.indexOf(vertex))
						neighbours.add(neighbour);
				}
				vertex.neighbours.removeAll(neighbours);
			}
		}
		if (method == "WorseFirstFit") {
			Input = new ArrayList<Vertex>();
			boolean left = true;
			int size = LNodes.size() + RNodes.size();
			// System.out.println(size);
			int leftIndex = 0;
			int rightIndex = 0;
			for (int i = 0; i < size; i++) {
				if (left) {
					Input.add(LNodes.get(leftIndex));
					leftIndex++;
					left = false;
				} else {
					Input.add(RNodes.get(rightIndex));
					rightIndex++;
					left = true;
				}
			}
			for (Vertex vertex : Input) {
				List<Vertex> neighbours = new ArrayList<Vertex>();
				for (Vertex neighbour : vertex.neighbours) {
					if (Input.indexOf(neighbour) > Input.indexOf(vertex))
						neighbours.add(neighbour);
				}
				vertex.neighbours.removeAll(neighbours);
				if (Coloring.ContainsValue(vertex.neighbours, vertex.value)) {
					Vertex v = vertex.neighbours.stream().filter(o -> o.value == vertex.value).findFirst().get();
					vertex.neighbours.remove(v);
				}
			}
		}
		return Input;
	}
}
