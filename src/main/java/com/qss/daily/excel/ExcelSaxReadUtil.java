package com.qss.daily.excel;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author qiushengsen
 * @dateTime 2018/8/21 下午3:43
 * @descripiton 基于事件驱动模型的excel读取工具，内存友好
 * 调用者自行实现 {@link XSSFSheetXMLHandler.SheetContentsHandler}
 **/
public class ExcelSaxReadUtil {

    private XSSFSheetXMLHandler.SheetContentsHandler sheetContentsHandler;

    private InputStream inputStream;

    private int sheetIndex;

    private ExcelSaxReadUtil(){}

    public static ExcelSaxReadUtil getInstance(InputStream inputStream
            , XSSFSheetXMLHandler.SheetContentsHandler sheetContentsHandler, int sheetIndex) {
        if (inputStream == null || sheetContentsHandler == null) {
            throw new RuntimeException("input stream is null or contentsHandler is null");
        }
        ExcelSaxReadUtil excelSaxReadUtil = new ExcelSaxReadUtil();
        excelSaxReadUtil.inputStream = inputStream;
        excelSaxReadUtil.sheetContentsHandler = sheetContentsHandler;
        excelSaxReadUtil.sheetIndex = sheetIndex;
        return excelSaxReadUtil;
    }


    private void processSheet(
            StylesTable styles,
            ReadOnlySharedStringsTable strings,
            InputStream sheetInputStream) throws IOException, SAXException {
        DataFormatter formatter = new DataFormatter();
        InputSource sheetSource = new InputSource(sheetInputStream);
        try {
            XMLReader sheetParser = SAXHelper.newXMLReader();
            ContentHandler handler = new XSSFSheetXMLHandler(
                    styles, null, strings, sheetContentsHandler, formatter, false);
            sheetParser.setContentHandler(handler);
            sheetParser.parse(sheetSource);
        } catch(ParserConfigurationException e) {
            throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
        }
    }

    public void process() throws IOException, OpenXML4JException, SAXException {
        OPCPackage xlsxPackage = OPCPackage.open(inputStream);
        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(xlsxPackage);
        XSSFReader xssfReader = new XSSFReader(xlsxPackage);
        StylesTable styles = xssfReader.getStylesTable();
        XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        int index = 0;
        while (iter.hasNext()) {
            try (InputStream stream = iter.next()) {
                if (sheetIndex == index) {
                    processSheet(styles, strings, stream);
                }
            }
            index ++;
        }
    }
}
