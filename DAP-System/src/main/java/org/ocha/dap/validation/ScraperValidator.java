package org.ocha.dap.validation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.ocha.dap.model.ValidationReport;
import org.ocha.dap.model.ValidationStatus;
import org.ocha.dap.persistence.entity.ckan.CKANDataset;
import org.ocha.dap.tools.IOTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScraperValidator implements DAPValidator {

	private static final Logger log = LoggerFactory.getLogger(ScraperValidator.class);

	@Override
	public ValidationReport evaluateFile(final File file) {
		final ValidationReport report = new ValidationReport(CKANDataset.Type.SCRAPER);
		extractZipContent(file);
		final File parent = file.getParentFile();

		final File datasetFile = new File(parent, "dataset.csv");
		if (datasetFile.exists()) {
			report.addEntry(ValidationStatus.SUCCESS, "dataset.csv does exist");
		} else {
			report.addEntry(ValidationStatus.ERROR, "dataset.csv does not exist");
			return report;
		}

		final File indicatorFile = new File(parent, "indicator.csv");
		if (indicatorFile.exists()) {
			report.addEntry(ValidationStatus.SUCCESS, "indicator.csv does exist");
		} else {
			report.addEntry(ValidationStatus.ERROR, "indicator.csv does not exist");
			return report;
		}

		final File valueFile = new File(parent, "value.csv");
		if (valueFile.exists()) {
			report.addEntry(ValidationStatus.SUCCESS, "value.csv does exist");
		} else {
			report.addEntry(ValidationStatus.ERROR, "value.csv does not exist");
			return report;
		}

		return report;
	}

	private void extractZipContent(final File zipFile) {
		final int BUFFER = 2048;

		ZipFile zip = null;
		try {
			final File parent = zipFile.getParentFile();
			zip = new ZipFile(zipFile);

			final Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();

			BufferedInputStream is = null;
			FileOutputStream fos = null;
			BufferedOutputStream dest = null;
			try {
				// Process each entry
				while (zipFileEntries.hasMoreElements()) {
					// grab a zip file entry
					final ZipEntry entry = zipFileEntries.nextElement();
					final String currentEntry = entry.getName();
					final File destFile = new File(parent, currentEntry);
					// destFile = new File(newPath, destFile.getName());
					final File destinationParent = destFile.getParentFile();

					// create the parent directory structure if needed
					if (destinationParent.mkdirs()) {
						log.debug(String.format("Failed to perform mkdirs for path : %s", destinationParent.getAbsolutePath()));
					}

					if (!entry.isDirectory()) {
						is = new BufferedInputStream(zip.getInputStream(entry));
						int currentByte;
						// establish buffer for writing file
						final byte data[] = new byte[BUFFER];

						// write the current file to disk
						fos = new FileOutputStream(destFile);
						dest = new BufferedOutputStream(fos, BUFFER);

						// read and write until last byte is encountered
						while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
							dest.write(data, 0, currentByte);
						}
						dest.flush();
					}
				}
			} catch (final Exception e) {
				log.debug(e.toString(), e);
			} finally {
				IOTools.closeResource(is);
				IOTools.closeResource(fos);
				IOTools.closeResource(dest);
			}
		} catch (final Exception e) {
			log.debug(e.toString(), e);
		} finally {
			IOTools.closeResource(zip);
		}
	}

}