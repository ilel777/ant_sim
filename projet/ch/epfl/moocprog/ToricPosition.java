package ch.epfl.moocprog;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.WORLD_HEIGHT;
import static ch.epfl.moocprog.config.Config.WORLD_WIDTH;

import ch.epfl.moocprog.utils.Vec2d;

public final class ToricPosition {

	//declaration des attributs
	private final Vec2d position;
	
	private static Vec2d clampedPosition(double x, double y){
		final int h = getConfig().getInt(WORLD_HEIGHT);
		final int w = getConfig().getInt(WORLD_WIDTH);
		//tant que x est en dehors du monde torique
		//en augmente sa valeur par la largeur de ce dernier ou en la diminue pour l'y remetre 
		while(x < 0) x += w;
		while(x >= w) x -= w;
		
		//en fait de meme pour y
		while(y < 0) y += h;
		while(y >= h) y -= h;
		
		return new Vec2d(x, y);
	}
	//Constructeurs
	public ToricPosition(double x, double y) {
		position = clampedPosition(x, y);
	}
	public ToricPosition(Vec2d coor){
		this(coor.getX(),coor.getY());
	}
	public ToricPosition(){
		this(0.,0.);
	}
	//getters
	public Vec2d toVec2d() {
		return position;
	}
	//methodes
	public ToricPosition add(ToricPosition that){
		
		return new ToricPosition(toVec2d().add(that.toVec2d()));
	}
	public ToricPosition add(Vec2d coor){
		return new ToricPosition(toVec2d().add(coor));
	}
	
	public Vec2d toricVector(ToricPosition that){
		final int h = getConfig().getInt(WORLD_HEIGHT);
		final int w = getConfig().getInt(WORLD_WIDTH);
		Vec2d pos = that.toVec2d();
		
		for(int i=0 ; i<3 ; i++){
			for(int j=0; j<3 ; j++){
				Vec2d coor = that.toVec2d().add(new Vec2d((i-1)*w, (j-1)*h));
				
				if(toVec2d().distance(pos) > toVec2d().distance(coor)){
					pos = coor;
				}
			}
		}
		return new Vec2d(pos.getX()-toVec2d().getX(), pos.getY()-toVec2d().getY());
	}
	public double toricDistance(ToricPosition that){
		return toricVector(that).length();
	}
	
	@Override
	public String toString() {
		return toVec2d().toString();
	}
}
