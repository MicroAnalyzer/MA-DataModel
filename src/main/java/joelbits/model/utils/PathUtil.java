package joelbits.model.utils;

import java.io.File;

public final class PathUtil {
    private static final String PATH = System.getProperty("user.dir") + File.separator;
    private static final String REPOSITORIES = PATH + "repositories" + File.separator;
    private static final String CHANGED_FILES = PATH + "files" + File.separator;
    private static final String PROJECTS = PATH + "projects" + File.separator;
    private static final String SOURCE_MAPPINGS = PATH + "source.mappings";

    public static String jarPath() {
        return PATH;
    }

    public static String projectSequenceFile() {
        return PROJECTS;
    }

    public static String changedFilesMapFile() {
        return CHANGED_FILES;
    }

    public static String clonedRepositoriesFolder() { return REPOSITORIES; }

    public static String sourceMappingsFile() { return SOURCE_MAPPINGS; }
}
