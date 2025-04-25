package ivanizki.movie.movies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.top_logic.basic.CollectionUtil;
import com.top_logic.basic.StringServices;
import com.top_logic.basic.col.MapUtil;
import com.top_logic.element.meta.MetaElementUtil;
import com.top_logic.element.model.DynamicModelService;
import com.top_logic.knowledge.wrap.ValueProvider;
import com.top_logic.knowledge.wrap.Wrapper;
import com.top_logic.model.TLClass;
import com.top_logic.model.util.TLModelUtil;

/**
 * Importer for movies.
 *
 * @author ivanizki
 */
public class MovieImporter {

	private static final List<String> MOVIE_ATTRIBUTES =
		CollectionUtil.list("year", "title", "directors", "genres", "duration", "actors");

	private static final TLClass MOVIE_TYPE = (TLClass) TLModelUtil.findType("movie", "Movie");

	private static final TLClass HUMAN_TYPE = (TLClass) TLModelUtil.findType("movie", "Human");

	private static final TLClass GENRE_TYPE = (TLClass) TLModelUtil.findType("movie", "Genre");

	private Map<String, Wrapper> _movies;

	private Map<String, Wrapper> _humans;

	private Map<String, Wrapper> _genres;

	/**
	 * Creates a new {@link MovieImporter}.
	 */
	public MovieImporter() {
		_movies = MapUtil.createValueMap(getAllMovies(), movie -> getMovieKey(movie));
		_humans = MapUtil.createValueMap(getAllHumans(), human -> getHumanKey(human));
		_genres = MapUtil.createValueMap(getAllGenres(), human -> getGenreKey(human));
	}

	private List<Wrapper> getAllMovies() {
		return MetaElementUtil.getAllInstancesOf(MOVIE_TYPE, Wrapper.class);
	}

	private List<Wrapper> getAllHumans() {
		return MetaElementUtil.getAllInstancesOf(HUMAN_TYPE, Wrapper.class);
	}

	private List<Wrapper> getAllGenres() {
		return MetaElementUtil.getAllInstancesOf(GENRE_TYPE, Wrapper.class);
	}

	/**
	 * Imports the given {@link CSVDocument}.
	 */
	public void importDocument(CSVDocument document) {
		List<Integer> indexList = getIndexList(document);
		for (List<String> row : document.getRows()) {
			ImportObject importObject = new ImportObject();
			for (int i = 0; i < row.size(); i++) {
				Integer index = indexList.get(i);
				if (index < row.size()) {
					importObject.setValue(MOVIE_ATTRIBUTES.get(index), row.get(i));
				}
			}
			importMovie(importObject);
		}
	}

	private void importMovie(ImportObject importMovie) {
		String key = getMovieKey(importMovie);
		Wrapper movie = _movies.get(key);
		if (movie == null) {
			movie = (Wrapper) DynamicModelService.getInstance().createObject(MOVIE_TYPE);
			movie.setValue("year", parseInteger((String) importMovie.getValue("year")));
			movie.setValue("title", getTitle(importMovie));
			movie.setValue("duration", parseInteger((String) importMovie.getValue("duration")));
			movie.setValue("directors", parseHumans((String) importMovie.getValue("directors")));
			movie.setValue("actors", parseHumans((String) importMovie.getValue("actors")));
			movie.setValue("genres", parseGenres((String) importMovie.getValue("genres")));
		}
	}

	private Integer parseInteger(String value) {
		return StringServices.isEmpty(value) ? null : Integer.parseInt(value);
	}

	private List<ValueProvider> parseHumans(String names) {
		List<ValueProvider> humans = new ArrayList<>();
		for (String name : MovieImportHandler.split(names, ",")) {
			ImportObject importObject = new ImportObject();
			importObject.setValue("name", name);
			humans.add(importHuman(importObject));
		}
		return humans;
	}

	private ValueProvider importHuman(ImportObject importHuman) {
		String key = getHumanKey(importHuman);
		Wrapper human = _humans.get(key);
		if (human == null) {
			human = (Wrapper) DynamicModelService.getInstance().createObject(HUMAN_TYPE);
			human.setValue("name", getName(importHuman));
			_humans.put(key, human);
		}
		return human;
	}

	private List<ValueProvider> parseGenres(String names) {
		List<ValueProvider> genres = new ArrayList<>();
		for (String name : MovieImportHandler.split(names, ",")) {
			ImportObject importObject = new ImportObject();
			importObject.setValue("name", name);
			genres.add(importGenre(importObject));
		}
		return genres;
	}

	private ValueProvider importGenre(ImportObject importGenre) {
		String key = getGenreKey(importGenre);
		Wrapper genre = _genres.get(key);
		if (genre == null) {
			genre = (Wrapper) DynamicModelService.getInstance().createObject(GENRE_TYPE);
			genre.setValue("name", getName(importGenre));
			_genres.put(key, genre);
		}
		return genre;
	}

	private List<Integer> getIndexList(CSVDocument document) {
		List<Integer> indexList = new ArrayList<>();
		List<String> header = document.getHeader();
		for (int i = 0; i < header.size(); i++) {
			String attributeName = header.get(i).toLowerCase();
			int index = MOVIE_ATTRIBUTES.indexOf(attributeName);
			if (index > -1) {
				indexList.add(index);
			}
		}
		return indexList;
	}

	private String getMovieKey(ValueProvider wrapper) {
		StringBuilder key = new StringBuilder()
			.append(getYearKey(wrapper)).append("_")
			.append(getKey(getTitle(wrapper))).append("_")
			.append(getDirectorsKey(wrapper));
		return key.toString();
	}

	private String getHumanKey(ValueProvider wrapper) {
		return getKey(getName(wrapper));
	}

	private String getGenreKey(ValueProvider wrapper) {
		return getKey(getName(wrapper));
	}

	private String getKey(Integer integer) {
		String string = integer == null ? "" : Integer.toString(integer);
		return getKey(string);
	}

	private String getDirectorsKey(ValueProvider wrapper) {
		Object directors = wrapper.getValue("directors");
		if (directors instanceof List<?>) {
			return getHumansKey(CollectionUtil.dynamicCastView(ValueProvider.class, (List<?>) directors));
		}
		return getKey((String) directors);
	}

	private String getYearKey(ValueProvider wrapper) {
		Object year = wrapper.getValue("year");
		if (year instanceof Integer) {
			return getKey((Integer) year);
		}
		return getKey((String) year);
	}

	private String getHumansKey(List<ValueProvider> humans) {
		StringBuilder key = new StringBuilder();
		for (ValueProvider human : humans) {
			key.append(getHumanKey(human));
		}
		return key.toString();
	}

	private String getKey(String string) {
		return nonNull(string).toLowerCase();
	}

	private String nonNull(String string) {
		return string == null ? "" : string;
	}

	private String getTitle(ValueProvider wrapper) {
		return (String) wrapper.getValue("title");
	}

	private String getName(ValueProvider wrapper) {
		return (String) wrapper.getValue("name");
	}

}
