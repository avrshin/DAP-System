package org.ocha.hdx.persistence.entity.dictionary;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.gson.JsonObject;
import org.hibernate.annotations.ForeignKey;
import org.ocha.hdx.persistence.entity.configs.ResourceConfiguration;
import org.ocha.hdx.persistence.entity.curateddata.Source;

@Entity
@Table(name = "source_dictionary")
public class SourceDictionary extends AbstractDictionary {

	@ManyToOne
	@ForeignKey(name = "fk_source_dictionary_to_source")
	@JoinColumn(name = "source_id", nullable = false)
	private final Source source;

	public Source getSource() {
		return source;
	}

	public SourceDictionary(final String unnormalizedName, final String importer, final Source source, final ResourceConfiguration configuration) {
		super(unnormalizedName, importer, configuration);
		this.source = source;
	}

	public SourceDictionary() {
		super();
		this.source = null;

	}

    @Override
    public JsonObject toJSON() {
        JsonObject element = super.toJSON();
        element.addProperty("sourceName", getSource().getName().getDefaultValue());
        return element;
    }

}
