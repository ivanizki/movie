package ivanizki.movie.movies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.top_logic.basic.config.InstantiationContext;
import com.top_logic.knowledge.service.PersistencyLayer;
import com.top_logic.knowledge.service.Transaction;
import com.top_logic.layout.DisplayContext;
import com.top_logic.mig.html.layout.LayoutComponent;
import com.top_logic.tool.boundsec.AbstractCommandHandler;
import com.top_logic.tool.boundsec.HandlerResult;

/**
 * {@link AbstractCommandHandler} for movie import.
 *
 * @author ivanizki
 */
public class MovieImportHandler extends AbstractCommandHandler {

	/**
	 * Creates a new {@link MovieImportHandler}.
	 */
	public MovieImportHandler(InstantiationContext context, Config config) {
		super(context, config);
	}

	@Override
	public HandlerResult handleCommand(DisplayContext context, LayoutComponent component, Object model,	Map<String, Object> someArguments) {
		CSVDocument document = null;
		try {
			document = readFromFile(((MoviesImportDialog) component).getData().getStream());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		Transaction transaction = PersistencyLayer.getKnowledgeBase().beginTransaction();
		try {
			new MovieImporter().importDocument(document);
			transaction.commit();
		} finally {
			transaction.rollback();
		}
		component.closeDialog();
		return HandlerResult.DEFAULT_RESULT;
	}

	/**
	 * Reads the data from the specified {@link InputStream}.
	 */
	public static CSVDocument readFromFile(InputStream stream) throws IOException {
		CSVDocument document = new CSVDocument();
		int index = 0;
		for (String line : readAllLines(stream)) {
			List<String> columns = splitColumns(line);
			if (index == 0) {
				document.setHeader(columns);
			} else {
				document.addRow(columns);
			}
			index++;
		}
		return document;
	}

	private static List<String> splitColumns(String string) {
		return split(string, ";");
	}

	/**
	 * Splits the given {@link String} around matches of the given {@link String regular
	 * expression}.
	 * 
	 * @return the resulting partial {@link String}s.
	 */
	public static List<String> split(String string, String regex) {
		List<String> parts = new ArrayList<>();
		if (string != null) {
			for (String part : string.split(regex)) {
				parts.add(part);
			}
		}
		return parts;
	}

	/**
	 * @return Whether the given {@link String} is empty or <code>null</code>.
	 */
	public static boolean isEmpty(String string) {
		return string == null || string.isEmpty();
	}

	/**
	 * @return All lines from the specified {@link InputStream}.
	 */
	public static List<String> readAllLines(InputStream stream) throws IOException {
		List<String> lines = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
			String line = reader.readLine();
			while (line != null) {
				lines.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			throw e;
		}
		return lines;
	}
	
}
