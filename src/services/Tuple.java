package services;

import java.util.Objects;

/**
 * Class that represents a cell number in the sudoku field and the gene
 * sequential number i.e. how far it is from the start Gene on the first
 * position has sequential number 0;
 * 
 * @author panva
 */
public class Tuple implements Comparable<Tuple> {

	private int cellNumber;
	private int geneNumber;

	public Tuple(int cellNumber, int geneNumber) {
		this.cellNumber = cellNumber;
		this.geneNumber = geneNumber;
	}

	public int getCellNumber() {
		return cellNumber;
	}

	public void setCellNumber(int cellNumber) {
		this.cellNumber = cellNumber;
	}

	public int getGeneNumber() {
		return geneNumber;
	}

	public void setGeneNumber(int geneNumber) {
		this.geneNumber = geneNumber;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cellNumber, geneNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple other = (Tuple) obj;
		return cellNumber == other.cellNumber && geneNumber == other.geneNumber;
	}

	@Override
	public String toString() {
		return "(cell:gene)->(" + cellNumber + ":" + geneNumber + ")";
	}

	@Override
	public int compareTo(Tuple o) {
		return Integer.compare(this.geneNumber, o.getGeneNumber());
	}

}
