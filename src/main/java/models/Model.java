package models;

import annotations.ColumnDescription;
import annotations.ColumnInclude;

import javax.persistence.EnumType;

@ColumnInclude
public class Model {
    @ColumnDescription(name = "Adı")
    private String name;
    @ColumnDescription(name = "Soyadı")
    private String surname;
    @ColumnDescription(name = "Yaşı")
    private String age;
    @ColumnDescription(name = "Proyekt versiya", enumeration = ProjectType.class, callFunction = "getDesc", enumType = EnumType.STRING)
    private String projectType;
    @ColumnDescription(name = "Proyekt versiya-2 ", enumeration = ProjectType.class, callFunction = "getValueAndDesc", enumType = EnumType.ORDINAL)
    private Integer projectIntType;
    @ColumnDescription(ignore = true)
    private String age2;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAge2() {
        return age2;
    }

    public void setAge2(String age2) {
        this.age2 = age2;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public int getProjectIntType() {
        return projectIntType;
    }

    public void setProjectIntType(int projectIntType) {
        this.projectIntType = projectIntType;
    }
}
