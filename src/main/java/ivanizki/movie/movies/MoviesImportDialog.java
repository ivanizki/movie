package ivanizki.movie.movies;

import com.top_logic.basic.config.ConfigurationException;
import com.top_logic.basic.config.InstantiationContext;
import com.top_logic.basic.io.binary.BinaryData;
import com.top_logic.knowledge.gui.layout.upload.NoNameCheck;
import com.top_logic.layout.form.component.FormComponent;
import com.top_logic.layout.form.model.DataField;
import com.top_logic.layout.form.model.FormContext;
import com.top_logic.layout.form.model.FormFactory;

/**
 * {@link FormComponent} for movies import.
 *
 * @author ivanizki
 */
public class MoviesImportDialog extends FormComponent {

	/** {@link DataField} for the upload file. */
	private static final String UPLOAD_FILE = "uploadFile";

	/**
	 * Creates a new {@link MoviesImportDialog}.
	 */
	public MoviesImportDialog(InstantiationContext context, Config config) throws ConfigurationException {
		super(context, config);
	}

	@Override
	public FormContext createFormContext() {
		FormContext formContext = new FormContext(this);

		DataField dataField = FormFactory.newDataField(UPLOAD_FILE, NoNameCheck.INSTANCE);
		dataField.setAcceptedTypes(".csv");
		formContext.addMember(dataField);

		return formContext;
	}

	/**
	 * @return The uploaded data, or <code>null</code>, if the {@link #UPLOAD_FILE} does not contain
	 *         any data.
	 */
	public BinaryData getData() {
		return ((DataField) getFormContext().getMember(UPLOAD_FILE)).getDataItem();
	}

}
