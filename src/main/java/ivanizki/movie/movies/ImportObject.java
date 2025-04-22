package ivanizki.movie.movies;

import java.util.HashMap;
import java.util.Map;

import com.top_logic.knowledge.wrap.ValueProvider;

/**
 * Import object.
 *
 * @author ivanizki
 */
public class ImportObject implements ValueProvider {

	private Map<String, String> _valueMap;

	/**
	 * Creates a new {@link ImportObject}.
	 */
	public ImportObject() {
		_valueMap = new HashMap<>();
	}

	@Override
	public Object getValue(String attributeName) {
		return _valueMap.get(attributeName);
	}

	@Override
	public void setValue(String attributeName, Object attributeValue) {
		_valueMap.put(attributeName, (String) attributeValue);
	}

}
