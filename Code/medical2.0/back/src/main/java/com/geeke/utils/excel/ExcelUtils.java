package com.geeke.utils.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.poi.ss.usermodel.CellType.*;

/**
 * 
 * @author skydu
 *
 */
public class ExcelUtils {
	//
	private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);
	//
	private static final Class<?>[] supportFieldClasses = { int.class, Integer.class, long.class, Long.class,
			short.class, Short.class, byte.class, Byte.class, boolean.class, Boolean.class, float.class, Float.class,
			double.class, Double.class, Date.class, String.class };

	private static ExcelCellProcessor excelCellProcessor = new ExcelCellProcessor();

	//
	public static class Coordinate {
		public int x;
		public int y;

		//
		public Coordinate() {

		}

		//
		public Coordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * 
	 * @param wb
	 * @param sheetName
	 * @param list
	 * @throws Exception
	 */
	public static <T> void writeExcel(XSSFWorkbook wb, String sheetName, List<T> list) throws Exception {
		writeExcel0(wb, sheetName, list);
	}

	private static <T> void writeExcel0(XSSFWorkbook wb, String sheetName, List<T> list) {
		if (list == null || list.size() == 0) {
			return;
		}
		try {
			T test = list.get(0);
			Map<String, Field> fieldMap = new HashMap<>();
			List<String> titles = new ArrayList<>();
			Field[] fields = test.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(ExcelCell.class)) {
					checkFieldClass(field.getType());
					ExcelCell excelCell = field.getAnnotation(ExcelCell.class);
					fieldMap.put(excelCell.name(), field);
					titles.add(excelCell.name());
				}
			}
			if (fieldMap.size() != titles.size()) {
				throw new IllegalArgumentException("@ExcelCell name cannot same");
			}
			//
			Sheet sheet = null;
			if (sheetName == null) {
				sheet = wb.createSheet();
			} else {
				sheet = wb.createSheet(sheetName);
			}
			// set header
			Row titleRow = sheet.createRow(0);
			for (int i = 0; i < titles.size(); i++) {
				Cell cell = titleRow.createCell(i);
				cell.setCellValue(titles.get(i));
			}
			//
			int totalRowCount = list.size();
			for (int r = 0; r < totalRowCount; r++) {
				T bean = list.get(r);
				Row row = sheet.createRow(r + 1);
				for (int c = 0; c < titles.size(); c++) {
					Cell cell = row.createCell(c);
					String title = titles.get(c);
					Field field = fieldMap.get(title);
					field.setAccessible(true);
					Object val = field.get(bean);
					if (val == null) {
						continue;
					}
					ExcelCell excelCell = field.getAnnotation(ExcelCell.class);
					if (StringUtils.isNotBlank(excelCell.convertKey())) {
						val = excelCellProcessor.convert(excelCell.convertKey(),val);
					}
					if (excelCell.hyperLink() == true) {
						cell.setCellFormula("HYPERLINK(\"" + val.toString() + "\",\"" + val.toString() + "\")");
					} else if (field.getType() == Date.class) {
						cell.setCellValue(new SimpleDateFormat(excelCell.dateFormat()).format((Date) val));
					} else {
						cell.setCellValue(val.toString());
					}
				}
			}
		} catch (Exception e) {
			//throw new POIXMLException(e.getMessage(), e);
			throw new POIXMLException(e.getMessage(), e);
		}

	}

	/**
	 * 
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public static <T> ByteArrayOutputStream writeExcel(List<T> list) throws Exception {
		return writeExcel(null, list);
	}

	/**
	 * 
	 * @param list
	 * @param file
	 * @throws Exception
	 */
	public static <T> void writeExcelToFile(List<T> list, File file) throws Exception {
		writeExcelToFile(null, list, file);
	}

	/**
	 * 
	 * @param list
	 * @param file
	 * @throws Exception
	 */
	public static <T> void writeExcelToFile(String sheetName, List<T> list, File file) throws Exception {
		ByteArrayOutputStream bos = writeExcel(sheetName, list);
		if (bos == null) {
			throw new RuntimeException("no data to write");
		}
		try (OutputStream outputStream = new FileOutputStream(file)) {
			bos.writeTo(outputStream);
		}
	}



	private static void checkFieldClass(Class<?> clazz) {
		for (Class<?> supprtClass : supportFieldClasses) {
			if (supprtClass == clazz) {
				return;
			}
		}
		throw new RuntimeException("Unsupport Field Class " + clazz.getSimpleName());
	}

	/**
	 * 
	 * @param sheetName
	 * @param list
	 * @param file
	 * @param startRow
	 * @param startCol
	 * @throws Exception
	 */
	public static <T> void writeMoreSheetExcelToFile(String sheetName, List<T> list, File file, int startRow,
			int startCol) throws Exception {
		if (list == null || list.size() == 0) {
			return;
		}
		Workbook wb = null;
		if (!file.exists()) {
			wb = new XSSFWorkbook();
		} else {
			wb = new XSSFWorkbook(new FileInputStream(file));
		}
		T test = list.get(0);
		Map<String, Field> fieldMap = new HashMap<>();
		List<String> titles = new ArrayList<>();
		Field[] fields = test.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(ExcelCell.class)) {
				checkFieldClass(field.getType());
				ExcelCell excelCell = field.getAnnotation(ExcelCell.class);
				fieldMap.put(excelCell.name(), field);
				titles.add(excelCell.name());
			}
		}
		if (fieldMap.size() != titles.size()) {
			wb.close();
			throw new IllegalArgumentException("@ExcelCell name cannot same");
		}
		//
		Sheet sheet = wb.getSheet(sheetName);
		if (sheet == null) {
			sheet = wb.createSheet(sheetName);
		}
		// set header
		Row titleRow = sheet.createRow(0 + startRow);
		for (int i = 0; i < titles.size(); i++) {
			Cell cell = titleRow.createCell(i + startCol);
			cell.setCellValue(titles.get(i));
		}
		//
		int totalRowCount = list.size();
		for (int r = 0; r < totalRowCount; r++) {
			T bean = list.get(r);
			Row row = sheet.createRow(r + 1 + startRow);
			for (int c = 0; c < titles.size(); c++) {
				Cell cell = row.createCell(c + startCol);
				String title = titles.get(c);
				Field field = fieldMap.get(title);
				field.setAccessible(true);
				Object val = field.get(bean);
				if (val == null) {
					continue;
				}
				ExcelCell excelCell = field.getAnnotation(ExcelCell.class);
				if (field.getType() == Date.class) {
					cell.setCellValue(new SimpleDateFormat(excelCell.dateFormat()).format((Date) val));
				} else {
					cell.setCellValue(val.toString());
				}
			}
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		wb.write(bos);
		bos.close();
		try (OutputStream outputStream = new FileOutputStream(file)) {
			bos.writeTo(outputStream);
			outputStream.close();
		}
		if (wb != null) {
			wb.close();
		}
	}

	/**
	 * 
	 * @param sheetName
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public static <T> ByteArrayOutputStream writeExcel(String sheetName, List<T> list) throws Exception {
		if (list == null || list.size() == 0) {
			return null;
		}
		try (XSSFWorkbook wb = new XSSFWorkbook()) {
			writeExcel0(wb, sheetName, list);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			wb.write(bos);
			bos.close();
			return bos;
		}
	}

	public static ByteArrayOutputStream writeExcel(TableData data) throws IOException {
		try (XSSFWorkbook wb = new XSSFWorkbook()) {
			XSSFSheet sheet = wb.createSheet(data.sheetName);
			XSSFRow row = sheet.createRow(0);
			List<Integer> urllist = new ArrayList<>();
			for (int i = 0; i < data.headers.size(); i++) {
				XSSFCell cell = row.createCell(i);
				cell.setCellValue(data.headers.get(i));
				if (data.headers.get(i).indexOf("链接") != -1) {
					urllist.add(i);
				}
			}
			int idx = 1;
			for (List<String> ss : data.contents) {
				XSSFRow r = sheet.createRow(idx++);
				for (int i = 0; i < ss.size(); i++) {
					XSSFCell cell = r.createCell(i);
					if (urllist.contains(i)) {
						cell.setCellFormula("HYPERLINK(\"" + ss.get(i) + "\",\"" + ss.get(i) + "\")");
					} else {
						cell.setCellValue(ss.get(i));
					}
				}
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			wb.write(bos);
			bos.close();
			return bos;
		}
	}

	public static void writeResponse(ByteArrayOutputStream bos, HttpServletResponse response, String sheetName) throws IOException {
		response.setContentType("application/xlsx;charset=UTF-8");
		//response.setContentType("application/octet-stream;charset=UTF-8");
		response.setContentLength(bos.size());
		response.setHeader("Content-Disposition", "attachment; filename=" + new String(sheetName.getBytes("utf-8"),"ISO-8859-1"));
		ServletOutputStream outputStream = response.getOutputStream();
		outputStream.write(bos.toByteArray());
	}

	/**
	 * 
	 * @param xlsxFile
	 * @param imageData
	 * @param col1
	 * @param row1
	 * @param col2
	 * @param row2
	 * @throws IOException
	 */
	public static void addPicture(File xlsxFile, byte[] imageData, int col1, int row1, int col2, int row2)
			throws IOException {
		try (XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(xlsxFile))) {
			XSSFSheet sheet = wb.getSheetAt(0);
			XSSFDrawing drawing = sheet.createDrawingPatriarch();
			XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, col1, row1, col2, row2);
			drawing.createPicture(anchor, wb.addPicture(imageData, XSSFWorkbook.PICTURE_TYPE_JPEG));
			FileOutputStream fileOut = new FileOutputStream(xlsxFile);
			wb.write(fileOut);
			fileOut.close();
		}
	}

	/**
	 * 
	 * @param clazz
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> readExcel(Class<T> clazz, InputStream in) throws Exception {
		try (XSSFWorkbook wb = new XSSFWorkbook(in)) {
			if (wb.getNumberOfSheets() <= 0) {
				return new ArrayList<>();
			}
			XSSFSheet sheet = wb.getSheetAt(0);
			//
			List<T> result = new ArrayList<T>(sheet.getLastRowNum() - 1);
			Row row = sheet.getRow(sheet.getFirstRowNum());
			//
			Map<String, Field> fieldMap = new HashMap<String, Field>();
			Map<String, String> titleMap = new HashMap<String, String>();
			//
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(ExcelCell.class)) {
					ExcelCell mapperCell = field.getAnnotation(ExcelCell.class);
					fieldMap.put(mapperCell.name(), field);
				}
			}

			for (Cell title : row) {
				CellReference cellRef = new CellReference(title);
				titleMap.put(cellRef.getCellRefParts()[2], title.getRichStringCellValue().getString());
			}

			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
				T t = clazz.newInstance();
				Row dataRow = sheet.getRow(i);
				for (Cell data : dataRow) {
					CellReference cellRef = new CellReference(data);
					String cellTag = cellRef.getCellRefParts()[2];
					String name = titleMap.get(cellTag);
					Field field = fieldMap.get(name);
					if (null != field) {
						field.setAccessible(true);
						setFiedlValue(data, t, field);
					}
				}
				result.add(t);
			}
			//
			return result;
		}
	}
	//
	public static TableData readExcel(InputStream in) throws Exception {
		return readExcel(in, 0);
	}
	//
	/*public static TableData readExcel(InputStream in,int sheetIndex) throws Exception {
		TableData data = new TableData();
		try (XSSFWorkbook wb = new XSSFWorkbook(in)) {
			if (wb.getNumberOfSheets() <= sheetIndex) {
				return data;
			}
			XSSFSheet sheet = wb.getSheetAt(sheetIndex);
			data.sheetName = sheet.getSheetName();
			if (sheet.getLastRowNum() <= 0) {
				return data;
			}
			XSSFRow row = sheet.getRow(sheet.getFirstRowNum());
			for (int i = 0; i <= row.getLastCellNum(); i++) {
				if(i<row.getFirstCellNum()) {
					data.headers.add("");
				}else {
					XSSFCell cell = row.getCell(i);
					data.headers.add(getCellContent(cell));
				}
			}
			//
			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
				List<String> cc = new ArrayList<>();
				data.contents.add(cc);
				XSSFRow rr = sheet.getRow(i);
				for (int j = 0; j <= rr.getLastCellNum(); j++) {
					if(j<0) {
						continue;
					}
					if(j<rr.getFirstCellNum()) {
						cc.add("");
					}else {
						XSSFCell cell = rr.getCell(j);
						cc.add(getCellContent(cell));
					}
				}
			}
		}
		return data;
	}*/

	public static TableData readExcel(InputStream in,int sheetIndex) throws Exception {
		TableData data = new TableData();
		//Workbook workbook = WorkbookFactory.create(in);
		try (Workbook wb = WorkbookFactory.create(in)) {
			if (wb.getNumberOfSheets() <= sheetIndex) {
				return data;
			}
			Sheet sheet = wb.getSheetAt(sheetIndex);
			data.sheetName = sheet.getSheetName();
			if (sheet.getLastRowNum() <= 0) {
				return data;
			}
			Row row = sheet.getRow(sheet.getFirstRowNum());
			//for (int i = 0; i <= row.getLastCellNum(); i++) {
			for (int i = 0; i < row.getLastCellNum(); i++) {
				if(i<row.getFirstCellNum()) {
					data.headers.add("");
				}else {
					Cell cell = row.getCell(i);
					data.headers.add(getCellContent(cell));
				}
			}
			//
			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
			//for (int i = sheet.getFirstRowNum() + 1; i < sheet.getLastRowNum(); i++) {
				List<String> cc = new ArrayList<>();
				data.contents.add(cc);
				Row rr = sheet.getRow(i);
				//for (int j = 0; j <= rr.getLastCellNum(); j++) {
				for (int j = 0; j < rr.getLastCellNum(); j++) {
					if(j<0) {
						continue;
					}
					if(j<rr.getFirstCellNum()) {
						cc.add("");
					}else {
						Cell cell = rr.getCell(j);
						cc.add(getCellContent(cell));
					}
				}
			}
		}
		return data;
	}

	//
	private static String getCellContent(Cell cell) {
		if (cell != null) {
			//if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
			if (cell.getCellType() == BOOLEAN) {
				return (cell.getBooleanCellValue() + "");
			}
			//if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
			if (cell.getCellType() == NUMERIC) {
				if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
					SimpleDateFormat sdf = null;
					if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
						sdf = new SimpleDateFormat("HH:mm");
					} else {// 日期
						sdf = new SimpleDateFormat("yyyy-MM-dd");
					}
					Date date = cell.getDateCellValue();
					return sdf.format(date);
				} else if (cell.getCellStyle().getDataFormat() == 58) {
					// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					double value = cell.getNumericCellValue();
					Date date = DateUtil.getJavaDate(value);
					return sdf.format(date);
				}
				DecimalFormat df = new DecimalFormat("0");
				return (df.format(cell.getNumericCellValue()));
			}
			if (cell.getCellType() == STRING) {
				return (cell.getStringCellValue() + "");
			}
			if (cell.getCellType() == FORMULA) {
				try {
					return String.valueOf(cell.getNumericCellValue());
				}catch (IllegalStateException e){
					return String.valueOf(cell.getRichStringCellValue());
				}
			}
		}
		return "";
	}

	//
	private static void setFiedlValue(Cell cell, Object o, Field field) throws IllegalAccessException, ParseException {
		String content = getCellContent(cell);
		if (content == null || content.length() == 0) {
			return;
		}
		if (logger.isInfoEnabled()) {
			logger.info("cell:{}, field:{}, type:{} content:{}", cell.getCellType(), field.getName(),
					field.getType().getName(), content);
		}
		//
		Class<?> filedClass = field.getType();
		if (filedClass == int.class || filedClass == Integer.class) {
			field.set(o, Integer.valueOf(content));
		} else if (filedClass == short.class || filedClass == Short.class) {
			field.set(o, Short.valueOf(content));
		} else if (filedClass == float.class || filedClass == Float.class) {
			field.set(o, Float.valueOf(content));
		} else if (filedClass == double.class || filedClass == Double.class) {
			field.set(o, Double.valueOf(content));
		} else if (filedClass == byte.class || filedClass == Byte.class) {
			field.set(o, Byte.valueOf(content));
		} else if (filedClass == boolean.class || filedClass == Boolean.class) {
			field.set(o, Boolean.valueOf(content));
		} else if (filedClass == Date.class) {
			ExcelCell excelCell = field.getAnnotation(ExcelCell.class);
			field.set(o, new SimpleDateFormat(excelCell.dateFormat()).parse(content));
		} else if (filedClass == String.class) {
			field.set(o, content);
		}
	}
}
