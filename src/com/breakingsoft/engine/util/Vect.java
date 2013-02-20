package com.breakingsoft.engine.util;

/**
 * Basic class representing a 2D vector of floats coordinates.
 */
public class Vect {
	
	public int x;
	public int y;
	
	/**
	 * Instantiate a new vector with specified values
	 * @param X value on x axis
	 * @param Y calue on y axis
	 */
	public Vect(int X, int Y){ x = X; y = Y; }
	
	public Vect(){}
	
	/**
	 * Calculates the length of this vector given by pythagore's theorem
	 * @return length of the vector
	 */
	public int length(){
		return (int) Math.sqrt(x*x+y*y);
	}
	
	/**
	 * Sets x and y coordinates
	 * @param X
	 * @param Y
	 */
	public void set(int X, int Y){
		x = X;
		y = Y;
	}
	
	/**
	 * Adds a vector with this one. Returns the result, do not modify this
	 * @param other vector to add
	 * @return resulting vector
	 */
	public Vect add(Vect other){
		return new Vect(x+other.x, y+other.y);
	}
	
	/**
	 * Subtracts a vector to this one. Returns the result, do not modify this
	 * @param other vector to subtract
	 * @return resulting vector
	 */
	public Vect sub(Vect other){
		return new Vect(x-other.x, y-other.y);
	}
	
	/**
	 * returns the multiplication between this vector and a scalar
	 */
	public Vect mult(float val){
		return new Vect((int)(x*val), (int)(y*val));
	}
}