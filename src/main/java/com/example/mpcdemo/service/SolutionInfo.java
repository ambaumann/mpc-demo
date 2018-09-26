package com.example.mpcdemo.service;

import java.io.File;

import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

import com.example.mpcdemo.domain.RockTourSolution;
import com.example.mpcdemo.persistence.RockTourXlsxFileIO;

public class SolutionInfo {
	public static final String SOLVER_CONFIG = "org/optaplanner/examples/rocktour/solver/rockTourSolverConfig.xml";

	public static final String DATA_DIR_NAME = "rocktour";

	// TODO change this.

	/**
	 * The path to the data directory, preferably with unix slashes for portability.
	 * For example: -D{@value #DATA_DIR_SYSTEM_PROPERTY}=sources/data/
	 */
	public static final String DATA_DIR_SYSTEM_PROPERTY = "org.optaplanner.examples.dataDir";

	public static File determineDataDir(String dataDirName) {
		String dataDirPath = System.getProperty(DATA_DIR_SYSTEM_PROPERTY, "data/");
		File dataDir = new File(dataDirPath, dataDirName);
		if (!dataDir.exists()) {
			throw new IllegalStateException("The directory dataDir (" + dataDir.getAbsolutePath()
					+ ") does not exist.\n"
					+ " Either the working directory should be set to the directory that contains the data directory"
					+ " (which is not the data directory itself), or the system property " + DATA_DIR_SYSTEM_PROPERTY
					+ " should be set properly.\n"
					+ " The data directory is different in a git clone (optaplanner/optaplanner-examples/data)"
					+ " and in a release zip (examples/sources/data).\n"
					+ " In an IDE (IntelliJ, Eclipse, NetBeans), open the \"Run configuration\""
					+ " to change \"Working directory\" (or add the system property in \"VM options\").");
		}
		return dataDir;
	}

	public SolutionFileIO<RockTourSolution> createSolutionFileIO() {
		return new RockTourXlsxFileIO();
	}

	public String getSolverConfig() {
		return SOLVER_CONFIG;
	}
}
