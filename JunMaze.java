package com.milton;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class JunMaze {

	public static final char WALL_CHAR = '#';
    public static final char FREE_CHAR = ' ';
    public static final char PATH_CHAR = '.';
    public static final char START_CHAR = 'S';
    public static final char FINISH_CHAR = 'F';
    private static final String STRING_NO_SOLUTION = "No solution";


	private Node [][] mMatrix;
	private Dimension startLocation, endLocation, mDimension;
	private boolean solved;


	public static void main(String[] args) throws IOException {
		String mazeFileName = args[0];
		long sTime = System.currentTimeMillis();
		JunMaze jmaze = new JunMaze();
		jmaze.processInputFile(mazeFileName);
		jmaze.BFSSolver();
		jmaze.printMaze();
		// get time
		System.out.println("Added stuff");
		long eTime = System.currentTimeMillis();
		System.out.println("Runtime:  " + (eTime - sTime) + " ms");


	}

	private void processInputFile(String mazeName) throws IOException {
		int matrixLineNumber = 0;
		int lineNumber = 0;
		BufferedReader br = new BufferedReader(new FileReader(mazeName));

		// read line
		String line = br.readLine();
		while(line != null){
			if(lineNumber == 0){
				// get maze dimensions
				setDimensions(line);

			}else if(lineNumber == 1){
				// get start point coordinates
				setStartLocation(line);

			}else if(lineNumber == 2){
				// get end/finish point coordinates
				setFinishLocation(line);

			}else{
				// convert rest of file to matrix
				convertToNode(line, matrixLineNumber);
				matrixLineNumber++;

			}
			line = br.readLine();
			lineNumber++;

		}

		br.close();


	}

	private void setFinishLocation(String finishLine) {
		//System.out.println(finishLine);
		String [] square = finishLine.split("\\s+");
		// Point? maybe
		endLocation = new Dimension(Integer.parseInt(square[1]), Integer.parseInt(square[0]));




	}

	private void setStartLocation(String startLine) {
		//System.out.println(startLine);
		String [] square = startLine.split("\\s+");
		startLocation = new Dimension(Integer.parseInt(square[1]), Integer.parseInt(square[0]));

	}

	private void setDimensions(String line) {
		//System.out.println(line);
		String [] square = line.split("\\s+");
		mDimension = new Dimension(Integer.parseInt(square[1]), Integer.parseInt(square[0]));

		// create maze matrix
		mMatrix = new Node[mDimension.height][mDimension.width];


	}

	private void convertToNode(String line, int matrixLineNumber) {
		if(matrixLineNumber < mDimension.height){
			String [] nLine = line.split("\\s+");

			for(int mColumn = 0; mColumn < nLine.length; mColumn++){
				Node node;
				// get current position
				Dimension currentPosition = new Dimension(mColumn, matrixLineNumber);
				if ( currentPosition.equals(startLocation) ) {
                    node = new Node(NodeType.N_START);
                } else if ( currentPosition.equals(endLocation) ) {
                    node = new Node(NodeType.N_END);
                } else {
                    int matrixValue = Integer.parseInt( nLine[mColumn] );

                    switch (matrixValue) {
                        case 0:
                            node = new Node(NodeType.N_PASSAGE);
                            break;
                        case 1: default:
                            node = new Node(NodeType.N_WALL);
                    }
                }
				node.setHeight(matrixLineNumber);
				node.setWidth(mColumn);
				mMatrix[matrixLineNumber][mColumn] = node;


			}

		}

	}

	// use breadth first search to solve maze
	public void BFSSolver() {
        Queue<Node> queue = new LinkedList<>();
        Node start = mMatrix[startLocation.height][startLocation.width];

        start.distance = 0;
        queue.add(start);

        while ( !queue.isEmpty() ) {
            Node currentNode = queue.remove();

            List<Node> adjacentNodes = getAdjacentNodes(currentNode);

            adjacentNodes.stream().filter(adjacentNode -> adjacentNode.distance < 0).forEach(adjacentNode -> {
                adjacentNode.distance = currentNode.distance + 1;
                adjacentNode.parent = currentNode;
                queue.add(adjacentNode);

                if (adjacentNode.type == NodeType.N_END) {
                    queue.clear();
                    solved = true;
                    setPath();
                }
            });
        }
    }

	private void setPath() {
		Node node = mMatrix[endLocation.height][endLocation.width];

        while (node.parent.type != NodeType.N_START) {
            if (node.parent.type == NodeType.N_PASSAGE) {
                node.parent.type = NodeType.N_PATH;
                node = node.parent;
            }
        }

	}

	private List<Node> getAdjacentNodes(Node node) {
        List<Node> adjacentNodes = new ArrayList<>();

        // North node
        if (node.getHeight() > 0) {
            Node adjacentNodeN = mMatrix[node.getHeight() - 1][node.getWidth()];
            if (adjacentNodeN.type != NodeType.N_WALL) {
                adjacentNodes.add(adjacentNodeN);
            }
        }
        // West node
        if (node.getWidth() < mDimension.width - 1) {
            Node adjacentNodeW = mMatrix[node.getHeight()][node.getWidth() + 1];
            if (adjacentNodeW.type != NodeType.N_WALL) {
                adjacentNodes.add(adjacentNodeW);
            }
        }
        // South node
        if (node.getHeight() < mDimension.height - 1) {
            Node adjacentNodeS = mMatrix[node.getHeight() + 1][node.getWidth()];
            if (adjacentNodeS.type != NodeType.N_WALL) {
                adjacentNodes.add(adjacentNodeS);
            }
        }
        // East Node
        if (node.getWidth() > 0) {
            Node adjacentNodeE = mMatrix[node.getHeight()][node.getWidth() - 1];
            if (adjacentNodeE.type != NodeType.N_WALL) {
                adjacentNodes.add(adjacentNodeE);
            }
        }

        return adjacentNodes;
    }

	private enum NodeType {
        N_START, N_END, N_PASSAGE, N_PATH, N_WALL
    }

	private class Node {
        public int distance;
        public Node parent;
        public JunMaze.NodeType type;
        private Dimension position = new Dimension();

        public Node(NodeType type) {
            this.type = type;

            distance = -1;
            parent = null;
        }

        public void setWidth(int position) {
            this.position.width = position;
        }

        public int getWidth() {
            return this.position.width;
        }

        public void setHeight(int position) {
            this.position.height = position;
        }

        public int getHeight() {
            return this.position.height;
        }
    }

	public void printMaze() {
        if (solved) {
        	System.out.println("Solved Maze: \n");
            for (Node[] nodeLine : mMatrix) {
                String line = "";

                for (Node node : nodeLine) {
                    switch (node.type) {
                        case N_WALL:
                            line += WALL_CHAR;
                            break;
                        case N_START:
                            line += START_CHAR;
                            break;
                        case N_PATH:
                            line += PATH_CHAR;
                            break;
                        case N_PASSAGE:
                            line += FREE_CHAR;
                            break;
                        case N_END:
                            line += FINISH_CHAR;
                            break;
                        default:
                            break;
                    }
                }

                System.out.println(line);
            }
        } else {
            System.out.println(STRING_NO_SOLUTION);
        }
    }

}
