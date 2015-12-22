package com.github.mjeanroy.dbunit.dataset;

import com.github.mjeanroy.dbunit.exception.DbUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;

public class DataSetFactory {

	/**
	 * Class Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(DataSetFactory.class);

	public static IDataSet createDataSet(String path) {
		File file = new File(path);
		return createDataSet(file);
	}

	public static IDataSet createDataSet(File file) {
		String name = file.getName().toLowerCase();

		try {
			if (file.isDirectory()) {
				return new DirectoryDataSetBuilder(file).build();
			}
			else if (name.endsWith(".json")) {
				// TODO
			}
			else if (name.endsWith(".xml")) {
				return new FlatXmlDataSetBuilder()
					.setColumnSensing(true)
					.build(file);
			}
		}
		catch (DataSetException ex) {
			log.error(ex.getMessage(), ex);
			throw new DbUnitException(ex);
		}
		catch (MalformedURLException ex) {
			log.error(ex.getMessage(), ex);
			throw new DbUnitException(ex);
		}

		return null;
	}


}
