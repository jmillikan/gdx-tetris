package com.highestqualitygames.gdx_tetris;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class GameScreen implements Screen {
	// Graphics constants
	final int BLOCK_SIZE = 20;
	
	// Game constants
	final int FASTER_COUNT = 1;
	final float FASTER_RATIO = 0.98f;

	final int GRID_HEIGHT = 22;
	final int GRID_WIDTH = 10;
	
	// Switches on a mode with a huge 4-line block for 0manual testing...
	final boolean EASY = false;

	// LibGDX graphics & input machinery
	SpriteBatch batch = new SpriteBatch();
	Vector3 touchPoint = new Vector3();

	Game game;
	OrthographicCamera cam;
	Sprite mm_sprite, block_sprite, over_sprite, pause_sprite;
	BitmapFont font = new BitmapFont();

	// Game variables
	float drop_timeout = 1;
	int rows = 0;
	int score = 0;
	
	// State handles pause, animations, game over, etc.
	// Most of the game is in "PieceState", the state when the player is controlling a piece
	interface GameState {
		void draw();
		void update(float delta);
	}

	GameState state;

	// The main variance in the game modes - how the next piece comes about.
	// Might be renamed GameMode later and come to encapsulate way more of the behavior.
	interface PieceFactory {
		boolean[][] nextPiece();
	}

	PieceFactory pieceFactory;
	
	public enum GameType {
		Classic, EasyFives, ThreesAndFives, Test
	}
	
	GameType gameType;

	// row-major grid, 0 at the bottom, 21/20 hidden at the top
	boolean[][] grid = new boolean[GRID_HEIGHT][GRID_WIDTH];
	
	public GameScreen(Game game, GameType type) {
		this.game = game;
		this.gameType = type;
		
		switch(gameType){
		case Test:
			pieceFactory = new EasyFactory();
			break;
		case Classic:
			pieceFactory = new PieceBagFactory(classicPieces);
			break;
		case EasyFives:
			pieceFactory = new PieceBagFactory(niceFives);
			break;
		case ThreesAndFives:
			pieceFactory = new AlternatingFactories(
					new AlternatingFactories(
							new PieceBagFactory(niceFives),
							new PieceBagFactory(modFives)),
							new PieceBagFactory(threePieces));
			break;
		}
						
		// PieceState is closed over pieceFactory.
		state = new PieceState();
		
		cam = new OrthographicCamera();
		cam.setToOrtho(false, 800, 480);
		
		mm_sprite = new Sprite(Assets.gameScreen);
		mm_sprite.setPosition(0, 0);
		
		over_sprite = new Sprite(Assets.overScreen);
		over_sprite.setPosition(0, 0);
		
		pause_sprite = new Sprite(Assets.pauseScreen);
		pause_sprite.setPosition(0, 0);
		
		block_sprite = new Sprite(Assets.block);
		block_sprite.setPosition(0, 0);
	}

	// At some point this will pause, allow user to see score, give info, etc.
	class GameOverState implements GameState {
		GameState losingState;
		
		public GameOverState(GameState losing){
			losingState = losing;
		}
		
		public void draw(){
			losingState.draw();

			batch.draw(over_sprite, 0, 0);
		}
		
		public void update(float delta){
			if(touched(Assets.overMenu)){
				game.setScreen(new MenuScreen(game));
			}
			
			if(touched(Assets.overRestart)){
				game.setScreen(new GameScreen(game, gameType));
			}
		}
	}
	
	class PausedState implements GameState {
		GameState returnState;
		public PausedState(GameState returnState){
			this.returnState = returnState;
		}
		
		public void draw(){
			returnState.draw();
				
			batch.draw(pause_sprite, 0, 0);
		}
		
		public void update(float delta){
			if(touched(Assets.pauseResume)){
				state = returnState;
			}
			
			if(touched(Assets.pauseMenu)){
				game.setScreen(new MenuScreen(game));
			}
		}
	}
	
	class ClearingState implements GameState {
		float left = 0.4f;
		boolean cleared = false;
		
		public void draw(){
			drawGame();
		}
		
		public void update(float delta){
			if(touched(Assets.gameScreenPause1) || touched(Assets.gameScreenPause2)){
				state = new PausedState(this);
			}

			left -= delta;
			
			if(left <= 0.2f && !cleared){
				clearRows();
				cleared = true;
			}
			
			if(left <= 0){
				state = new PieceState();
			}
		}
	}

	class PieceState implements GameState {
		// A square array of length 2/3/4
		boolean[][] piece = pieceFactory.nextPiece();
		
		// These are interpreted as grid position of the top left corner
		// of the piece (which give the odd-looking "- i" bits) -
		// should be changed to bottom left corner at some point
		int piece_x = GRID_WIDTH / 2 - piece[0].length / 2; 
		int piece_y = 21;
		
		float next_drop = drop_timeout;

		boolean checkEnd = true;
		
		public void draw(){
			drawGame();
			
			drawBlocks(piece, piece_x, piece_y, true);
		}
		
		// copy piece onto grid
		void attachPiece(){
			int size = piece.length;
			for(int i = 0; i < size; i++){
				for(int j = 0; j < size; j++){
					if(piece[i][j]){
						grid[piece_y - i][piece_x + j] = true;
					}
				}
			}
		}
		
		public void update(float delta){
			// If this check is done in the constructor, it's possible for other state to be overwritten
			// (e.g. by my own sloppiness)
			if(checkEnd){
				if(collidesWithGridOrWall(piece, piece_x, piece_y)){
					state = new GameOverState(this);
					
					return;
				}
				checkEnd = false;
			}
			
			if(touched(Assets.gameScreenPause1) || touched(Assets.gameScreenPause2)){
				state = new PausedState(PieceState.this);		
			}
			
			next_drop -= delta;
			
			if (next_drop <= 0) {
				dropPiece(false);
			}
			
			handlePieceKeys();
		}
		
		void handlePieceKeys() {
			if (touched(Assets.gameScreenDrop)){
					dropPiece(true);
			}
				
			if (touched(Assets.gameScreenLeft)){
				if(!collidesWithGridOrWall(piece, piece_x - 1, piece_y)){
					piece_x -= 1;
				}
			}

			if (touched(Assets.gameScreenRight)){
				if(!collidesWithGridOrWall(piece, piece_x + 1, piece_y)){
					piece_x += 1;
				}
			}	

			if (touched(Assets.gameScreenRotLeft)){
				boolean[][] new_piece = GameScreen.this.rotate(false, piece);
				if(!collidesWithGridOrWall(new_piece, piece_x, piece_y)){
					piece = new_piece;
				}
			}

			if (touched(Assets.gameScreenRotRight)){ 
				boolean[][] new_piece = GameScreen.this.rotate(true, piece);
				if(!collidesWithGridOrWall(new_piece, piece_x, piece_y)){
					piece = new_piece;
				}
			}
		}
		
		void dropPiece(boolean byUser){
			if(!collidesWithGridOrWall(piece, piece_x, piece_y - 1)){
				piece_y = piece_y - 1;
				
				next_drop = drop_timeout - (byUser ? 0 : next_drop);
			}
			else {
				attachPiece();

				state = new ClearingState();
			}
		}
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(cam.combined);

		batch.begin();
		
		batch.setColor(Assets.color1);
		
		state.draw();
		state.update(delta);
		
		batch.end();
	}
	
	boolean touched(com.badlogic.gdx.math.Rectangle r){
		if (!Gdx.input.justTouched())
			return false;
		
		// If this could possibly be slow, I could move it t...
		// It won't be slow
		cam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
		
		return r.contains(touchPoint.x, touchPoint.y);
	}
	
	void clearRows(){
		int rowsCleared = 0;
		
		for(int i = 0; i < GRID_HEIGHT;){
			boolean full = true;
			
			for(int j = 0; j < GRID_WIDTH; j++) {
				if(!grid[i][j]) 
					full = false;
			}
			
			if(full){
				// Move rows down...
				rows++;
				rowsCleared++;
				
				if(rows >= FASTER_COUNT){
					drop_timeout = drop_timeout * FASTER_RATIO;
					rows = 0;
				}

				for(int k = i; k < GRID_HEIGHT - 1; k++){
					grid[k] = grid[k + 1];
				}
				
				grid[GRID_HEIGHT - 1] = new boolean[GRID_WIDTH];
			}
			else {
				i++;
			}
			
		}

		score += rowsCleared * rowsCleared * 10;
	}

	boolean collidesWithGridOrWall(boolean[][] p, int p_x, int p_y){
		// i is p row, j is p column
		for(int i = 0; i < p.length; i++){
			for(int j = 0; j < p.length; j++){
				if(p[i][j]){
					int b_x = p_x + j;
					int b_y = p_y - i;
					
					if(b_y < 0 || b_y >= GRID_HEIGHT ||
							b_x < 0 || b_x >= GRID_WIDTH ||
							grid[b_y][b_x])
						return true;
				}
			}
		}
		
		return false;
	}
	
	void drawGame(){
		batch.setColor(Assets.color2);
		batch.draw(mm_sprite,  0,  0);

		// calls setColor. Need to disentangle this eventually.
		drawBlocks(grid, 0, 0, false);

		// GWT doesn't support normal string formatting. Rather than get into a mess, don't use string formatting...
		int s = score;
		for(int i = 0; i < 6; i++){
			int d = s % 10;
			Assets.font.draw(batch, Integer.toString(d), 750f, (260.0f + i * 40.0f));
			s = s / 10;
		}
	}
	
	// flip_y because the grid is stored upside down from the piece.
	// This is not really a good thing.
	void drawBlocks(boolean[][] blocks, int x, int y, boolean flip_y){
		float grid_x = Assets.gameScreenGrid.x;
		float grid_y = Assets.gameScreenGrid.y;
		
		batch.setColor(Assets.color1);
		
		for(int i = 0; i < blocks.length; i++){
			for(int j = 0; j < blocks[i].length; j++){
				if(blocks[i][j] && (y + (flip_y ? -i : i)) < GRID_HEIGHT - 2){
					batch.draw(
							block_sprite, 
							grid_x + BLOCK_SIZE * (x + j),
							grid_y + BLOCK_SIZE * (y + (flip_y ? -i : i)));
				}
			}
		}
	}

	boolean[][] rotate(boolean right, boolean[][] piece){
		// Assume that piece is a square (not ragged or rectangular)
		boolean[][] new_piece = new boolean[piece.length][piece[0].length];
		
		for(int i = 0; i < piece.length; i++){
			for(int j = 0; j < piece.length; j++){
				new_piece[right ? j : piece.length - 1 - j][right ? piece.length - 1 - i : i] = piece[i][j]; 
			}
		}
		
		return new_piece;
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	final static boolean X = true;
	final static boolean O = false;

	// PIECES
	// Pieces are square arrays of booleans, true for "block here".
	// To keep things 'simple', never write over pieces. Instead, copy when necessary.
	
	class EasyFactory implements PieceFactory {
		public boolean[][] nextPiece(){
			boolean easy[][] = new boolean[10][10];
			for(int i = 0; i < 10;i++){
				easy[0][i] = true;
				easy[1][i] = true;
				easy[2][i] = true;
				easy[3][i] = true;
			}

			return easy;
		}
	}

	// Evenly, randomly from a predetermined list
	class PieceBagFactory implements PieceFactory {
		// An array of pieces
		boolean[][][] pieces;
		
		PieceBagFactory(boolean[][][] pieces) {
			this.pieces = pieces;
		}
		
		public boolean[][] nextPiece() {
			return pieces[(int) Math.floor(Math.random() * pieces.length)];			
		}
	}
	
	class AlternatingFactories implements PieceFactory {
		PieceFactory fa, fb;
		boolean a = false;
		
		AlternatingFactories(PieceFactory fa, PieceFactory fb){
			this.fa = fa;
			this.fb = fb;
		}
		
		public boolean[][] nextPiece(){
			a = !a;
			
			return a ? fa.nextPiece() : fb.nextPiece();
		}
	}
	
	static boolean[][][] threePieces = new boolean[][][]{
		{{O,X,O},
			{O,X,O},
			{O,X,O}},
			{{X,O},{X,X}}
	};
	
	static boolean[][][] niceFives = new boolean[][][]{
		{{X,X,X},
			{X,X,O},
			{O,O,O}
		},
		{{X,X,X},
			{O,X,X},
			{O,O,O}
		},
		{{X,X,X},
			{X,X,O},
			{O,O,O}
		},
		{{X,X,X},
			{O,X,X},
			{O,O,O}
		},
		{{O,O,O,O},
			{X,X,O,O},
			{O,X,X,X},
			{O,O,O,O}
		},
		{{O,O,O,O},
			{O,X,X,X},
			{X,X,O,O},
			{O,O,O,O}
		},
		{{X,O,O},
			{X,X,X},
			{O,X,O}
		},
		{{O,O,X},
			{X,X,X},
			{O,X,O}
		},
		{{X,X,X},
			{X,O,O},
			{X,O,O}
		},
		{{X,X,O},
			{O,X,X},
			{O,O,X}
		},
		{{O,O,O,O,O},
			{X,X,X,X,X},
			{O,O,O,O,O},
			{O,O,O,O,O},
			{O,O,O,O,O}
		}
	};
	
	static boolean[][][] modFives = new boolean[][][]{
		{{X,O,X},{X,X,X},{O,O,O}},
		{{O,O,O,O},
			{X,X,X,X},
			{O,O,O,X},
			{O,O,O,O}
		},
		{{O,O,O,O},
			{O,O,O,X},
			{X,X,X,X},
			{O,O,O,O}
		},
		{{X,X,X},
			{O,X,O},
			{O,X,O}
		},
		{{O,X,O},
			{X,X,X},
			{O,X,O}
		},
		{{O,O,O,O},
			{X,X,X,X},
			{O,O,X,O},
			{O,O,O,O}
				
		},
		{{O,O,O,O},
			{O,O,X,O},
			{X,X,X,X},
			{O,O,O,O}
		},
		{{O,X,O},
			{X,X,X},
			{O,X,O}
		}
	};
	
	static boolean[][][] classicPieces = new boolean[][][]{
		{{O,X,O,O},
		 {O,X,O,O},
		 {O,X,O,O},
		 {O,X,O,O}},
		   {{X,X},
			{X,X}},
		   {{O,O,O},
			{X,X,X},
			{O,X,O}},
		   {{O,X,O},
			{O,X,O},
			{O,X,X}},
		   {{O,X,O},
			{O,X,O},
			{X,X,O}},
		   {{O,X,O},
			{X,X,O},
			{X,O,O}},
		   {{O,X,O},
			{O,X,X},
			{O,O,X}}
		};
}