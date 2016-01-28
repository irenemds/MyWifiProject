package com.mdes.mywifi.generic;
/**
 * Define los puntos que componen las l�neas representadas
 * en las graficas de la aplicaci�n.
 *
 */
public class Point {
	/**
	 * Coordenada x del punto
	 */
	private int x;
	/**
	 * Coordenada y del punto
	 */
	private int y;
	
	public Point( int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
}
