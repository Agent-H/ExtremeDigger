package com.agenth.engine.util;

/**
 * Basic class representing a 2D vector of floats coordinates.
 */
public class VectF {
	
	public float x;
	public float y;
	
	/**
	 * Instantiate a new vector with specified values
	 * @param X value on x axis
	 * @param Y calue on y axis
	 */
	public VectF(float X, float Y){ x = X; y = Y; }
	
	public VectF(){}
	
	/**
	 * Calculates the length of this vector given by pythagore's theorem
	 * @return length of the vector
	 */
	public float length(){
		return (float) Math.sqrt(x*x+y*y);
	}
	
	/**
	 * Sets x and y coordinates
	 * @param X
	 * @param Y
	 */
	public void set(float X, float Y){
		x = X;
		y = Y;
	}
	
	/**
	 * Adds a vector with this one. Returns the result, do not modify this
	 * @param other vector to add
	 * @return resulting vector
	 */
	public VectF add(VectF other){
		return new VectF(x+other.x, y+other.y);
	}
	
	/**
	 * Subtracts a vector to this one. Returns the result, do not modify this
	 * @param other vector to subtract
	 * @return resulting vector
	 */
	public VectF sub(VectF other){
		return new VectF(x-other.x, y-other.y);
	}
	
	/**
	 * returns the multiplication between this vector and a scalar
	 */
	public VectF mult(float val){
		return new VectF(x*val, y*val);
	}
}
