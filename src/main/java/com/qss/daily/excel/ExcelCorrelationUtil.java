package com.qss.daily.excel;

import com.google.common.collect.Lists;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

/**
 * Created by admin360buyad on 2017-05-19.
 */
public class ExcelCorrelationUtil {
    private String excelHideSheetName;

    private int MAX_ROW = 2000;

    private int rowCount;

    private Workbook workbook;

    private String sheetName;

    private ExcelCorrelationUtil(){
        rowCount = 1;
        excelHideSheetName = "hide" + System.nanoTime();
    }

    public ExcelCorrelationUtil setMaxRow(int maxRow) {
        this.MAX_ROW = maxRow;
        return this;
    }

    public static ExcelCorrelationUtil getInstance(){
        return new ExcelCorrelationUtil();
    }

    public void generateCorrelation(Workbook workbook, int sheetIndex, LevelNode root){
        generateCorrelation(workbook, workbook.getSheetName(sheetIndex), root);
    }

    /**
     * 生成多级联动注入到workbook
     * @param targetWorkbook
     * @param sheetName 需要多级联动的表名
     * @param root 多级联动数据源
     */
    public void generateCorrelation(Workbook targetWorkbook, String sheetName, LevelNode root){
        if(root.isRoot() && (root.getSons() == null || root.getSons().size() == 0)) return;
        this.workbook = targetWorkbook;
        this.sheetName = sheetName;
        Sheet sheet = workbook.getSheet(this.sheetName);
        Sheet hideSheet = workbook.createSheet(excelHideSheetName);
        workbook.setSheetHidden(workbook.getSheetIndex(excelHideSheetName), true);
        generate(sheet, hideSheet, root);

    }

    /**
     * 生成一列的数据
     * @param sheet
     * @param hideSheet 用于保存数据源的表
     * @param node
     */
    private void generate(Sheet sheet, Sheet hideSheet, LevelNode node){
        if(node.getSons() == null || node.getSons().size() == 0) return;
        Row currentRow = hideSheet.createRow(rowCount - 1);
        creatRow(currentRow, retriveValueFromNodes(node.getSons()));
        creatExcelNameList(node.getValue(), rowCount, node.getSons().size());
        setCorrelation(node, sheet);
        for(LevelNode next : node.getSons()){
            if(next.getSons() != null && next.getSons().size() > 0){
                rowCount++;
            }
            generate(sheet, hideSheet, next);
        }

    }

    /**
     * 将被关联数据与联动表的指定列进行关联
     * @param node
     * @param sheet
     */
    private void setCorrelation(LevelNode node, Sheet sheet){
        String formula = node.getValue();
        for(int i = 0; i < MAX_ROW; i ++){
            if(!node.isRoot()){
                formula = "INDIRECT($" + generateColumnDesc(node.getFather().getColumnStart()) + "$"
                        + (node.getFather().getRowStart() + 1 + i) + ")";
            }
            DataValidation validation = getDataValidationByFormula(formula,node.getRowStart() + 1 + i,
                    node.getColumnStart() + 1);
            sheet.addValidationData(validation);
        }
    }

    /**
     * 从数据源取出数据
     * @param nodes
     * @return
     */
    private List<String> retriveValueFromNodes(List<LevelNode> nodes){
        List<String> values = Lists.newArrayListWithCapacity(nodes.size());
        for(LevelNode node : nodes){
            values.add(node.getValue());
        }
        return values;
    }

    public void creatRow(Row currentRow, List<String> textList) {
        if (textList != null && textList.size() > 0) {
            int i = 0;
            for (String cellValue : textList) {
                Cell userNameLableCell = currentRow.createCell(i++);
                userNameLableCell.setCellValue(cellValue);
            }
        }
    }

    /**
     * 生成excel的列描述，从0开始
     * @param column
     * @return
     */
    private String generateColumnDesc(int column){
        StringBuilder sb = new StringBuilder();
        while(true){
            if(column < 26){
                sb.append((char)('A' + column));
                break;
            }
            sb.append((char)('A' + column % 26));
            column = column / 26 - 1;
        }
        return sb.reverse().toString();
    }

    /**
     * 创建数据源的名，联动表根据名的name关联数据源
     * @param nameCode
     * @param order
     * @param size
     */
    private void creatExcelNameList(String nameCode, int order, int size) {
        if (workbook.getName(nameCode) != null) {
            return;
        }
        Name name;
        name = workbook.createName();
        name.setNameName(nameCode);
        String formula = excelHideSheetName + "!" + creatExcelNameList(order, size);
        name.setRefersToFormula(formula);
    }

    /**
     * 生成某一行数据源的位置
     * @param order
     * @param size
     * @return
     */
    private String creatExcelNameList(int order, int size) {
        char start = 'A';
        String end = generateColumnDesc(size - 1);
        return "$" + start + "$" + order + ":$" + end + "$" + order;
    }

    /**
     * 生成关联到某一个单元格
     * @param formulaString
     * @param naturalRowIndex
     * @param naturalColumnIndex
     * @return
     */
    private DataValidation getDataValidationByFormula(String formulaString, int naturalRowIndex, int naturalColumnIndex) {
        //设置数据有效性加载在哪个单元格上。
        //四个参数分别是：起始行、终止行、起始列、终止列
        int firstRow = naturalRowIndex - 1;
        int lastRow = naturalRowIndex - 1;
        int firstCol = naturalColumnIndex - 1;
        int lastCol = naturalColumnIndex - 1;
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        //数据有效性对象
        DataValidation data_validation_list = null;
        if(this.workbook instanceof HSSFWorkbook){
            //加载下拉列表内容
            DVConstraint constraint = DVConstraint.createFormulaListConstraint(formulaString);
            data_validation_list = new HSSFDataValidation(regions, constraint);
            data_validation_list.setSuppressDropDownArrow(false);
        } else {//xssf or sxssf
            XSSFDataValidationConstraint constraint = new XSSFDataValidationConstraint(
                    DataValidationConstraint.ValidationType.LIST, formulaString);
            XSSFSheet sheet = null;
            if(workbook instanceof XSSFWorkbook){
                sheet  = (XSSFSheet) workbook.getSheet(sheetName);
            } else if (workbook instanceof SXSSFWorkbook){
                sheet = ((SXSSFWorkbook) workbook).getXSSFWorkbook().getSheet(sheetName);
            }
            DataValidationHelper helper = new XSSFDataValidationHelper(sheet);
            data_validation_list = helper.createValidation(constraint, regions);
            data_validation_list.setSuppressDropDownArrow(true);
            data_validation_list.setShowErrorBox(true);
        }
        data_validation_list.setEmptyCellAllowed(false);

       /* //设置输入信息提示信息
        data_validation_list.createPromptBox("下拉选择提示", "请使用下拉方式选择合适的值！");*/
        //设置输入错误提示信息
        data_validation_list.createErrorBox("choose error", "the list does not contains the value that you fill in");
        return data_validation_list;
    }
}
