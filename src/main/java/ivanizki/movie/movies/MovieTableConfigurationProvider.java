package ivanizki.movie.movies;

import com.top_logic.layout.table.model.ColumnConfiguration;
import com.top_logic.layout.table.model.TableConfiguration;
import com.top_logic.layout.table.model.TableConfigurationProvider;

/**
 * {@link TableConfigurationProvider} for movies.
 *
 * @author ivanizki
 */
public class MovieTableConfigurationProvider implements TableConfigurationProvider {

	@Override
	public void adaptConfigurationTo(TableConfiguration table) {
		table.getDeclaredColumn("duration").setDefaultColumnWidth("50px");
		table.getDeclaredColumn("year").setDefaultColumnWidth("50px");
	}

	@Override
	public void adaptDefaultColumn(ColumnConfiguration defaultColumn) {
		// Do nothing.
	}

}
