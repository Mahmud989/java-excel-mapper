package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum ProjectType {
    SIMPLE_STORE(0, "Simple file storage"),
    PROJECT_QR(1, "Project QR storage"),
    PROJECT_MEMORIES(2, "Project MEMORIES storage"),
    PROJECT_NOTES(3, "PROJECT NOTES storage");


    private final Integer value;
    private final String desc;
    private static final Map<Integer, ProjectType> map = new HashMap();


    static {
        for (ProjectType projectType : ProjectType.values()) {
            map.put(projectType.value, projectType);
        }
    }

    ProjectType(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonCreator
    public static ProjectType valueOf(Integer value) {
        return map.get(value);
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public String getValueAndDesc() {
        return value + " - " + desc;
    }
}
