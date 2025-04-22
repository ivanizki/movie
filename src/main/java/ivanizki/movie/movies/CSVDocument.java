package ivanizki.movie.movies;

import java.util.ArrayList;
import java.util.List;

/**
 * CSV document.
 *
 * @author ivanizki
 */
public class CSVDocument {

	private List<String> _header;

	private List<List<String>> _rows;

	/**
	 * Creates a new {@link CSVDocument}.
	 */
	public CSVDocument() {
		_rows = new ArrayList<>();
	}

	/**
	 * @return the header.
	 */
	public List<String> getHeader() {
		return _header;
	}

	/** @see #getHeader() */
	public void setHeader(List<String> header) {
		_header = header;
	}

	/**
	 * @return the rows
	 */
	public List<List<String>> getRows() {
		return _rows;
	}

	/**
	 * Adds the given row.
	 */
	public void addRow(List<String> columns) {
		_rows.add(columns);
	}

}
