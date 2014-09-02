import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Container;
import java.net.URL;
import java.awt.Color;

public class Othello extends JApplet implements ActionListener{
	static char move = 'b';
	static Node prevbest = null;
	static JFrame frame;
	static JButton [][] buttonBoard = new JButton[8][8];
	ImageIcon whiteChip, blackChip, g1, g2;
	
	static char[][] board ={{'0','1','2','3','4','5','6','7','8'},
				{'1','g','g','g','g','g','g','g','g'},
				{'2','g','g','g','g','g','g','g','g'},
				{'3','g','g','g','g','g','g','g','g'},
				{'4','g','g','g','w','b','g','g','g'},
				{'5','g','g','g','b','w','g','g','g'},
				{'6','g','g','g','g','g','g','g','g'},
				{'7','g','g','g','g','g','g','g','g'},
				{'8','g','g','g','g','g','g','g','g'}};
				
    
    static boolean done = false;

	public static void main(String argv[]){
		//PrintBoard();
		boolean turn = true;
		final Othello othello = new Othello();
		frame = new JFrame("Othello");
		/*
		frame.addWindowListener(new WindowAdapter() {
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		}
		);
		*/
		frame.setSize(400,400);
		JLayeredPane inner = new JLayeredPane();
		frame.getContentPane().add(inner);
		othello.setupBoard(inner);

		frame.setVisible(true);
		
	//	while(!done){
			//othello.PrintBoard();
			othello.play(turn);
		//othello.PrintBoard();
		//othello.play(!turn);
	//	}
		
		
	}
	
	public void init() { 
		Container c = getContentPane();
		JLayeredPane inner = new JLayeredPane();
		c.add(inner);
		setupBoard(inner);
		play(true);
	}
	
	public Othello() {
		super();
	}
    
    private void play(boolean color){
    	if (color== true) move = 'b';
    	else move = 'w';
		//color true for black, false for white
		//if it's computer's move
		if (move =='b'){
			Node root = new Node(-65,null, board, 0,color);
			board = FindBestMove(root,7,color);
			updateButtonBoard();
			PrintBoard();
			play(!color);
		}
		//players move
		else{
			Node root = new Node(-65,null, board, 0,color);
			ComputePlayerMoves(root,color);
		}
    }
    
	private void ComputeValidMoves(Node node, boolean color){
		//try corners first
		tryMove(node,1,1,color,0);
		tryMove(node,1,8,color,0);
		tryMove(node,8,1,color,0);
		tryMove(node,8,8,color,0);
		//try edges next
		for (int j =3; j<=6; j++){
			tryMove(node,1,j, color,0);
			tryMove(node,8,j,color,0);
		}
		for (int j =3; j<=6; j++){
			tryMove(node,j,1, color,0);
			tryMove(node,j,8,color,0);
		}
		// next inners
		for( int i = 2; i<=7;i++){
				for (int j =2; j<=7; j++){
					tryMove(node,i,j, color,0);
				}
		}
		// finally spots that might give up corners
		tryMove(node,1,2,color,0);
		tryMove(node,2,1,color,0);
		tryMove(node,7,8,color,0);
		tryMove(node,8,7,color,0);
		tryMove(node,1,7,color,0);
		tryMove(node,7,1,color,0);
		tryMove(node,2,8,color,0);
		tryMove(node,8,2,color,0);
		/* simple
		for( int i = 1; i<=8;i++){
				for (int j =1; j<=8; j++){
					tryMove(node,i,j, color);
				}
		}
		*/
		
	}
	
	private void ComputePlayerMoves(Node node, boolean color){
		int moves =0;
		for( int i = 1; i<=8;i++){
			for (int j =1; j<=8; j++){
				moves +=tryMove(node,i,j, color,1);
			}
		}
		if(moves==0){
		   if (prevbest ==null){
			   done = true;
			System.out.println("player can't go");
			   updateScore();
			   //System.exit(0);
		   }
		   else{
				System.out.println("player can't go");
				prevbest=null;
				play(!color);
				
		   }
		}else{
			prevbest = node;
		}
	
	}
	
	private char[][] FindBestMove(Node node, int depth,  boolean color){
	// color true for black, false for white

		Node best = AlphaBetaSearch(node,depth);
		if (best == null && prevbest ==null){
			 done = true;
			System.out.println("puter can't go");
			updateScore(); 
			 //System.exit(0);
			 }
			 
		if (best!= null){
			prevbest = best;
			return best.state; 	 
		}
		else{
			System.out.println("puter can't go");
			PrintBoard();
			prevbest = best;
			return board; 
		}
	}
    
	private int EvaluationFunction(char[][] state){
		int sum = 0;
		//advanced
		for( int i = 0; i<=8;i++){
			for (int j =0; j<=8; j++){
				if (state[i][j]=='w'){
					//corners worth more
					if(i==1&&j==1)sum-=20;
					else if(i==1&&j==8)sum-=20;
					else if(i==8&&j==1)sum-=20;
					else if(i==8&&j==8)sum-=20;
					else if(i==1&&(j>2&&j<7))sum-=3;
					else if(i==8&&(j>2&&j<7))sum-=3;
					else if(j==1&&(i>2||i<7))sum-=3;
					else if(j==8&&(i>2||i<7))sum-=3;
					else sum--;
					
				}
				else if (state[i][j]=='b'){
					if(i==1&&j==1)sum+=20;
					else if(i==1&&j==8)sum+=20;
					else if(i==8&&j==1)sum+=20;
					else if(i==8&&j==8)sum+=20;
					else if(i==1&&(j>2&&j<7))sum+=3;
					else if(i==8&&(j>2&&j<7))sum+=3;
					else if(j==1&&(i>2||i<7))sum+=3;
					else if(j==8&&(i>2||i<7))sum+=3;
					
					
					
					 sum++;
				}
				else ;
			}
		}
		
		
		
		/* simple
		for( int i = 0; i<=8;i++){
			for (int j =0; j<=8; j++){
				if (state[i][j]=='w') sum--;
				else if (state[i][j]=='b') sum++;
				else ;
			}
		}
		*/
		if (move=='b')return sum;
		else return -1*sum;
	}
	
	private void PrintBoard(){
		System.out.println();
		System.out.println(board[0]);
		System.out.println(board[1]);
		System.out.println(board[2]);
		System.out.println(board[3]);
		System.out.println(board[4]);
		System.out.println(board[5]);
		System.out.println(board[6]);
		System.out.println(board[7]);
		System.out.println(board[8]);


	}
	
	
	public int tryMove(Node node, int r, int c, boolean col, int func){
		int i,j,spaces;
		int playerMoves =0;                           
													 
		char[][] state = new char[9][9];
		for( int k = 0; k<=8;k++){
			for (int l =0; l<=8; l++){
				state[k][l]=node.state[k][l];
			}
		}
		char color = 'b'; 
		char othercolor = 'w';
		
		if(!col){
			color = 'w';
			othercolor = 'b';
		}
		if (state[r][c] == 'g'){  
			               
			for (int x=-1; x < 2; x++){
				for (int y=-1; y < 2; y++){
					spaces =1;
		   			for(i = r+spaces*x,  j = c+spaces*y;(i > 0) && (i < 9) && (j > 0) && (j < 9) && (state[i][j] == othercolor);){
		   				 spaces++; 
		   				 i=r+spaces*x; 
		   				 j=c+spaces*y;
		   				 }
					
	   				if (( i > 0) && (i < 9) && (j > 0) && (j < 9) &&(spaces > 1) && (state[i][j] == color) ){
	   					
	   					//expand moves for computer
	   					if (func ==0){ 
	   					
		   					node.addChild(new Node(-65,node,state,node.depth+1, !node.color ));
				   			for (int k = 0; k < spaces; k++){
				   				state[r+x*k][c+y*k] = color;
				   
				   			}
	   					}
	   					//test player move
	   					else if (func==1){
	   						
	   						buttonBoard[r-1][c-1].setIcon(g1);
							buttonBoard[r-1][c-1].setEnabled(true);
							playerMoves++;
	   					}
	   					//make player move
	   					else {
							for (int k = 0; k < spaces; k++){
								board[r+x*k][c+y*k] = color;
							}
	   					}
		   			}
		   		}
			}
		 }
		 return playerMoves;
	}

	private Node AlphaBetaSearch(Node node, int depth){
		int v = maxValue(node,-500,500,depth);
		for (Enumeration e = node.children.elements(); e.hasMoreElements() ;) {
			Node n = (Node) e.nextElement();
			if (n.value == v) return n;
		}
		return null;
	}
	
	
	private int maxValue(Node node, int alpha, int beta, int depth){
		if (node.depth == depth){
			node.value = EvaluationFunction(node.state);
			return node.value;
		}
		int v = -500;
		ComputeValidMoves(node, node.color);
		for (Enumeration e = node.children.elements(); e.hasMoreElements() ;) {
			Node n = (Node) e.nextElement();
			v = Math.max(v,minValue(n,alpha,beta,depth));
			if(v>=beta) {
				node.value = v;
				return v;
			} 
			alpha = Math.min(alpha,v);	
		}
		return v;
	}
	
	private int minValue(Node node, int alpha, int beta, int depth){
		if (node.depth == depth){
			node.value = EvaluationFunction(node.state);
			return node.value;
		}
		int v = 500;
		ComputeValidMoves(node, node.color);
		for (Enumeration e = node.children.elements(); e.hasMoreElements() ;) {
			Node n = (Node) e.nextElement();
			v = Math.min(v,maxValue(n,alpha,beta,depth));
			if(v<=beta) {
				node.value = v;
				return v;
			} 
			beta = Math.min(beta,v);	
		}
		return v;
	}	
	
	private void setupBoard(Container c){
		try{
			whiteChip = new ImageIcon(new URL("http://www.eden.rutgers.edu/~nsmolney/gaming/othello/white.gif"));
			blackChip = new ImageIcon(new URL("http://www.eden.rutgers.edu/~nsmolney/gaming/othello/black.gif"));
			ImageIcon back = new ImageIcon(new URL("http://www.eden.rutgers.edu/~nsmolney/gaming/othello/back.JPG"));
			JLabel backLabel = new JLabel(back);
			backLabel.setBounds(0, 0, 400, 400);
			backLabel.setBackground(Color.black);
			c.add(backLabel, new Integer(0));
			g1 = new ImageIcon(new URL("http://www.eden.rutgers.edu/~nsmolney/gaming/othello/g1.JPG"));
			g2 = new ImageIcon(new URL("http://www.eden.rutgers.edu/~nsmolney/gaming/othello/g2.JPG"));
		}
		catch (Exception e){
			System.out.println("couldnt find file");
		}

		
		/*
		JButton label = new JButton();
		label.setBounds(30+20,30+20,20,20);
		label.setBackground(Color.green);
		label.setFocusPainted( false );
		c.add(label, new Integer(12));
		label.setBorderPainted(false);
		*/
		
		for (int i=0; i<8;i++){
			for (int j=0;j<8;j++){
				JButton button = new JButton(g2);
				button.setBorderPainted(false);
				button.setBounds(30+41*i,30+41*j,40,40);
				c.add(button, new Integer(1));
				
				button.setOpaque( false );
				//button.setBackground(new Color(0,102,0));
				button.setRolloverIcon(g1);
				//button.setContentAreaFilled( false );
				button.setVisible(true);
				button.setEnabled(false);
				button.setDisabledIcon(g2);
				button.setPressedIcon(g2);
				
				button.setActionCommand(Integer.toString(i)+Integer.toString(j));
				button.addActionListener(this);
				buttonBoard[i][j] = button;
			}
		}
	
		buttonPlayed(buttonBoard[3][3], whiteChip);
		buttonPlayed(buttonBoard[4][4], whiteChip);
		buttonPlayed(buttonBoard[4][3], blackChip);
		buttonPlayed(buttonBoard[3][4], blackChip);
		
	}
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		int r = command.charAt(0);
		int c = (int) command.charAt(1);
		r-=48;
		c-=48;
		tryMove(new Node(-65,null, board, 0,false),r+1,c+1,false,2);
		//buttonPlayed(buttonBoard[r][c], whiteChip);
		//board[r+1][c+1] = 'w';
		setButtonsDisabled();
		updateButtonBoard();
		PrintBoard();
		play(true);

	}
	
	private void updateButtonBoard(){
		for (int i =1; i<9;i++){
			for (int j=1;j<9;j++){
				if (board[i][j]=='b'){
					buttonPlayed(buttonBoard[i-1][j-1],blackChip);
				}else if (board[i][j]=='w'){
					buttonPlayed(buttonBoard[i-1][j-1],whiteChip);
				}else ;
			}
		}
		
	}
	
	private void buttonPlayed(JButton button, Icon icon){
		button.setBorderPainted(false);
		button.setIcon(icon);
		button.setDisabledIcon(icon);
		button.setRolloverEnabled(false);
		button.setEnabled(false);
		
	}
	
	private void setButtonsDisabled(){
		for (int i=0; i<8;i++){
			for (int j=0;j<8;j++){
				buttonBoard[i][j].setEnabled(false);
			}
		}
	}
	private void updateScore(){
		int black = 0;
		int white = 0;
		//advanced
		for( int i = 0; i<=8;i++){
			for (int j =0; j<=8; j++){
				if (board[i][j]=='w'){
					white++;
					
				}
				else if (board[i][j]=='b'){
					black++;
				}
				else ;
			}
		}
		System.out.println("Black: "+black+" White: "+white);
	}
	
}
