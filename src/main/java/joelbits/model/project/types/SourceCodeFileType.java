package joelbits.model.project.types;

import joelbits.model.utils.FileNameUtils;

import java.util.Arrays;

public enum SourceCodeFileType {
    BINARY, JAVA, GO, TEXT, XML, JSON , OTHER;

    public static boolean exist(String file) {
        String fileType = FileNameUtils.getExtension(file).toLowerCase();
        return Arrays.stream(SourceCodeFileType.values())
                .map(type -> type.name().toLowerCase())
                .anyMatch(type -> type.equals(fileType));
    }
}
