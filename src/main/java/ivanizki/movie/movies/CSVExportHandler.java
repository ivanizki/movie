package ivanizki.movie.movies;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.top_logic.basic.Settings;
import com.top_logic.basic.StringServices;
import com.top_logic.basic.config.InstantiationContext;
import com.top_logic.basic.i18n.log.I18NLog;
import com.top_logic.basic.io.binary.BinaryData;
import com.top_logic.basic.io.binary.BinaryDataFactory;
import com.top_logic.basic.util.ResKey;
import com.top_logic.element.layout.grid.GridComponent;
import com.top_logic.knowledge.wrap.Wrapper;
import com.top_logic.layout.form.model.FormGroup;
import com.top_logic.layout.form.model.TableField;
import com.top_logic.layout.provider.MetaLabelProvider;
import com.top_logic.layout.table.export.AbstractTableExportHandler;
import com.top_logic.layout.table.model.ColumnConfiguration;
import com.top_logic.mig.html.layout.LayoutComponent;
import com.top_logic.util.Resources;

/**
 * {@link AbstractTableExportHandler} for CSV export.
 *
 * @author ivanizki
 */
public class CSVExportHandler extends AbstractTableExportHandler {

	/**
	 * Creates a new {@link CSVExportHandler}.
	 */
	public CSVExportHandler(InstantiationContext context, Config config) {
		super(context, config);
	}

	@Override
	protected ResKey getDefaultI18NKey() {
		return ResKey.decode("exportMovies");
	}

	@Override
	protected BinaryData createDownloadData(I18NLog log, LayoutComponent component) {
		log.info(com.top_logic.layout.table.export.I18NConstants.STARTING_EXPORT);

//		log.info(com.top_logic.layout.table.export.I18NConstants.EXTRACTING_TABLE_DATA);
		log.info(com.top_logic.layout.table.export.I18NConstants.EXPORTING_DATA);
		CSVDocument document = exportDocument(component);

		log.info(com.top_logic.layout.table.export.I18NConstants.PREPARING_DOWNLOAD);
		return writeToCSV(getFileName(component), document);
	}

	private String getFileName(LayoutComponent component) {
		String title = Resources.getInstance().getString(component.getConfig().getTitleKey());
		return StringServices.isEmpty(title) ? "export-file" : title.toLowerCase();
	}

	/**
	 * Writes the given {@link CSVDocument} to {@link BinaryData}.
	 */
	public static BinaryData writeToCSV(String fileName, CSVDocument document) {
		try {
			File tempFile = writeToCSV(document, createTempFile(fileName, CSVDocument.FILE_EXTENSIONS, Settings.getInstance().getTempDir()));
			return BinaryDataFactory.createBinaryDataWithName(tempFile, fileName + CSVDocument.FILE_EXTENSIONS);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Writes the given {@link CSVDocument} to {@link File}.
	 */
	public static File writeToCSV(CSVDocument document, File file) {
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file))) {
			writer.append(StringServices.join(document.getHeader(), ";"));
			writer.append("\n");
			for (List<String> row : document.getRows()) {
				writer.append(StringServices.join(row, ";"));
				writer.append("\n");
			}
			writer.close();
			return file;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @return A new empty temporary {@link File}.
	 */
	public static File createTempFile(String fileName, String fileExtension, File directory) {
		try {
			return File.createTempFile(fileName, fileExtension, directory);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private CSVDocument exportDocument(LayoutComponent component) {
		CSVDocument document = new CSVDocument();
		GridComponent grid = (GridComponent) component;
		TableField table = (TableField) grid.getFormContext().getMember(GridComponent.FIELD_TABLE);
		List<String> attributeNames = getAttributeNames(table);
		document.setHeader(attributeNames);
		for (Wrapper wrapper : collectWrappers(table)) {
			List<String> row = new ArrayList<>();
			for (String attributeName : attributeNames) {
				row.add(getLabel(wrapper.getValue(attributeName)));
			}
			document.addRow(row);
		}
		return document;
	}

	private String getLabel(Object value) {
		if (value == null) {
			return "";
		} else if (value instanceof Integer) {
			return Integer.toString((Integer) value);
		}
		return MetaLabelProvider.INSTANCE.getLabel(value);
	}

	private List<String> getAttributeNames(TableField table) {
		List<String> attributeNames = new ArrayList<>();
		for (ColumnConfiguration column : table.getTableModel().getTableConfiguration().getElementaryColumns()) {
			attributeNames.add(column.getName());
		}
		attributeNames.remove("_select");
		attributeNames.remove("tType");
		return attributeNames;
	}

	private Set<Wrapper> collectWrappers(TableField table) {
		Set<Wrapper> wrappers = new HashSet<>();
		for (Object row : table.getTableModel().getAllRows()) {
			wrappers.add((Wrapper) GridComponent.getRowObject((FormGroup) row));
		}
		return wrappers;
	}

}
