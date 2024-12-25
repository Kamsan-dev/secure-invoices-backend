package io.kamsan.secureinvoices.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;

import io.kamsan.secureinvoices.entities.Customer;
import io.kamsan.secureinvoices.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerReport {
	
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	private List<Customer> customers;
	private static String[] HEADERS = {"ID","Name", "Email", "Type", "Status", "Address", "Phone", "Created At"};
	List<Function<Customer, Object>> columns = Arrays.asList(
		    Customer::getCustomerId,
		    Customer::getName,
		    Customer::getEmail,
		    Customer::getType,
		    Customer::getStatus,
		    Customer::getAddress,
		    Customer::getPhone,
		    Customer::getCreatedAt
		);
	DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	public CustomerReport(List<Customer> customers) {
		this.customers = customers;
		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet("Customers");
		this.setHeaders();
	}
	
	public InputStreamResource export() {
		return this.generateReport();
	}
	
	private void setHeaders() {
		XSSFRow headerRow = sheet.createRow(0);
		XSSFCellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(14);
		style.setFont(font);
		IntStream.range(0, HEADERS.length).forEach(index -> {
		    XSSFCell cell = headerRow.createCell(index);
		    cell.setCellValue(HEADERS[index]);
		    cell.setCellStyle(style);
		});
	}
	
	// InputStreamResource for returning as a response in a web application
	private InputStreamResource generateReport() {
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			
		    // 1. Create styles and fonts for the workbook
			XSSFCellStyle style = workbook.createCellStyle();
			XSSFFont font = workbook.createFont();
			font.setFontHeight(10);
			style.setFont(font);
			
			// 2. Write customer data into rows and columns
			int rowIndex = 1;
			for(Customer customer : customers) {
				XSSFRow row = sheet.createRow(rowIndex++);
				for (int i = 0; i < this.columns.size(); i++){
					Object value = columns.get(i).apply(customer);
					if (value instanceof LocalDateTime) {
						row.createCell(i).setCellValue(((LocalDateTime) value).format(dateTimeFormatter));
					} else row.createCell(i).setCellValue(value !=null ? value.toString() : "");
				}
			}
			// 3. Write the workbook contents into the ByteArrayOutputStream
			workbook.write(out);
			return new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));
		} catch (Exception ex) {
			log.error(ex.getMessage());
			throw new ApiException("Unable to export report file");
		}
	}
}
