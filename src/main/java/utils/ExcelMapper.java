package utils;

import annotations.ColumnDescription;
import annotations.ColumnInclude;
import models.ProjectType;
import models.Model;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.persistence.EnumType;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ExcelMapper {

    public static void main(String[] args) throws IOException, IllegalAccessException, InvocationTargetException {
        List<Model> testList = new ArrayList<>();
//        Model test = new Model();
//        test.setName("Mahmud");
//        test.setSurname("Bodurov");
//        test.setAge("23");
//        test.setProjectType("PROJECT_QR");
//        test.setProjectIntType(1);
//
//        Model test2 = new Model();
//        test2.setName("Mahmud2");
//        test2.setAge("23");
//        test2.setAge2("99");
//        test2.setProjectType("SIMPLE_STORE");
//        test2.setProjectIntType(0);
//        Model test3 = new Model();
//        test3.setSurname("Bodurov3");
//        test3.setAge("23");
//        testList.addAll(List.of(test, test2, test3));
        for (int i = 0; i < 100_000; i++) {
            Model test_ = new Model();
            test_.setName("Mahmud" + i);
            test_.setSurname("Bodurov-" + i);
            test_.setAge(String.valueOf(new Random().nextInt()));
            test_.setAge2(String.valueOf(new Random().nextInt()));
            test_.setProjectType(ProjectType.values()[new Random().nextInt(4)].name());
            test_.setProjectIntType(new Random().nextInt(4));
            testList.add(test_);
        }
        ExcelMapper excelMapper = new ExcelMapper();
        //get headers from class
        List<String> headers = excelMapper.getHeaders(Model.class, false);

        //create table from object list
        List<List<String>> table = excelMapper.getTable(testList);

        //u will only return this if u use web services
        ByteArrayInputStream userTable = excelMapper.createExcel("Users",
                headers,
                table);

        //write to file and save
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";
        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        outputStream.write(userTable.readAllBytes());

        System.out.println(fileLocation);


    }

    public ByteArrayInputStream createExcel(String sheetName, List<String> headers, List<List<String>> table) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet(sheetName);
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);

/**
 *         Headers
 */


        Row header = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        headerStyle.setFillPattern(FillPatternType.NO_FILL);
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);
        for (int i = 0; i < headers.size(); i++) {
            Cell headerCell = header.createCell(i);
            headerCell.setCellValue(headers.get(i));
            headerCell.setCellStyle(headerStyle);
        }

/**
 * Cells
 */


        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        for (int i = 0; i < table.size(); i++) {
            Row row = sheet.createRow(1 + i);
            List<String> strings = table.get(i);
            for (int j = 0; j < strings.size(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(strings.get(j));
                cell.setCellStyle(style);
            }
        }


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public List<String> getHeaders(Class clazz, boolean useFieldName) {

        ArrayList<String> headers = new ArrayList<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        boolean annotationPresent = clazz.isAnnotationPresent(ColumnInclude.class);
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(ColumnDescription.class)) {
                ColumnDescription annotation = declaredField.getAnnotation(ColumnDescription.class);
                if (annotation.ignore()) {
                    continue;
                }
                if (useFieldName) {
                    headers.add(declaredField.getName());
                } else {
                    String name = annotation.name();
                    if (name != null && !name.isEmpty()) {
                        headers.add(name);
                    } else {
                        headers.add(declaredField.getName());
                    }
                }
            } else if (annotationPresent) {
                headers.add(declaredField.getName());
            }
        }
        return headers;
    }

    public String convertValue(Class<?> clazz, String callFunctionName, String value) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Optional<Method> methodOptional = Optional.empty();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        if (!callFunctionName.isEmpty())
            methodOptional = Arrays.stream(declaredMethods).filter(method -> method.getName().equalsIgnoreCase(callFunctionName)).findAny();
        if (methodOptional.isPresent()) {
            Method method = methodOptional.get();
            method.setAccessible(true);
            Object invoke = method.invoke(createInstance(clazz), value);
            return String.valueOf(invoke);// return function value
        }
        return value;
    }

    public Object createInstance(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            Object o = constructor.newInstance(new Object[constructor.getParameterCount()]);
            return o;
        }
        Object o = clazz.getConstructor().newInstance();
        return o;
    }

    public List<List<String>> getTable(List list) throws IllegalAccessException, InvocationTargetException {
        if (Objects.isNull(list)) {
            throw new NullPointerException("The object to excel is null");
        }

        List<List<String>> table = new ArrayList<>();
        for (Object object : list) {
            Class<?> clazz = object.getClass();
            boolean annotationPresent = clazz.isAnnotationPresent(ColumnInclude.class);
            Field[] declaredFields = clazz.getDeclaredFields();
            List<String> row = new ArrayList<>();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                if (declaredField.isAnnotationPresent(ColumnDescription.class)) {
                    ColumnDescription annotation = declaredField.getAnnotation(ColumnDescription.class);
                    if (annotation.ignore()) {
                        continue;
                    }
                    Optional<Object> o = Optional.ofNullable(declaredField.get(object));
                    if (o.isPresent()) {
                        String value = String.valueOf(o.get());
                        //check field is enum
                        if (!value.isEmpty() &&
                                !value.equalsIgnoreCase("null") &&
                                annotation.enumeration() != Object.class &&
                                annotation.enumeration().isEnum()) {
                            String getValue = getEnumByValue(annotation.enumeration(), value, annotation.callFunction(), annotation.enumType());
                            if (getValue.equalsIgnoreCase("null")) {
                                row.add("");
                            } else {
                                row.add(getValue);
                            }
                        } else if (!annotation.callFunction().isEmpty() && !annotation.enumeration().isEnum()) {
                            try {
                                row.add(convertValue(annotation.enumeration(), annotation.callFunction(), value));
                            } catch (NoSuchMethodException | InstantiationException e) {
                                row.add(value);
                            }
                        } else {
                            row.add(value);
                        }
                    } else {
                        row.add("");
                    }
                } else if (annotationPresent) {
                    Optional<Object> o = Optional.ofNullable(declaredField.get(object));
                    if (o.isPresent()) {
                        String value = String.valueOf(o.get());
                        row.add(value);
                    } else {
                        row.add("");
                    }
                }
            }
            table.add(row);
        }
        return table;
    }

    /**
     * A common method for all enums since they can't have another base class
     *
     * @param clazz enum type. All enums must be all caps.
     * @param value case insensitive
     * @return corresponding enum, or null
     */

    public static String getEnumByValue(Class clazz, Object value, String callFunctionName, EnumType enumType) throws IllegalArgumentException {
        if (clazz != null && value != null) {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            if (EnumType.ORDINAL == enumType) {
                int cord = Integer.parseInt(String.valueOf(value));
                Object enumConstant = clazz.getEnumConstants()[cord];
//                return String.valueOf(enumConstant);
            }

            try {
                for (int i = 0; i < clazz.getEnumConstants().length; i++) {
                    Object enumConstant = clazz.getEnumConstants()[i];
                    Optional<Method> methodOptional = Optional.empty();
                    if (!callFunctionName.isEmpty())
                        methodOptional = Arrays.stream(declaredMethods).filter(method -> method.getName().equalsIgnoreCase(callFunctionName)).findAny();

                    if (EnumType.ORDINAL == enumType && String.valueOf(i).equalsIgnoreCase(String.valueOf(value))) {
                        if (!callFunctionName.isEmpty()) {
                            if (methodOptional.isPresent()) {
                                Object invoke = methodOptional.get().invoke(enumConstant);
                                return String.valueOf(invoke);// return function value
                            } else {
                                throw new IllegalArgumentException(String.format(
                                        "There is no value with name '%s' in Enum %s",
                                        value, clazz.getName()
                                ));
                            }
                        }
                        return String.valueOf(enumConstant);
                    } else if (EnumType.STRING == enumType && String.valueOf(value).equalsIgnoreCase(String.valueOf(enumConstant))) {
                        if (!callFunctionName.isEmpty()) {
                            if (methodOptional.isPresent()) {
                                Object invoke = methodOptional.get().invoke(enumConstant);
                                return String.valueOf(invoke);// return function value
                            } else {
                                throw new IllegalArgumentException(String.format(
                                        "There is no value with name '%s' in Enum %s",
                                        value, clazz.getName()
                                ));
                            }
                        }
                        return String.valueOf(i);//return ordinal
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }
        throw new IllegalArgumentException(String.format(
                "There is no value with name '%s' in Enum %s | function : %s | type: %s",
                value, clazz.getName(), callFunctionName, enumType.name()
        ));
    }
}