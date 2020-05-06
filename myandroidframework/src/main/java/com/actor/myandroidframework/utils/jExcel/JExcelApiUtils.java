package com.actor.myandroidframework.utils.jExcel;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.util.SparseArray;

import com.actor.myandroidframework.utils.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Description: excel导出
 * from: https://github.com/DmrfCoder/AndroidExcelDemo
 *       https://blog.csdn.net/qq_36982160/article/details/82421940
 *
 * 1.如果要使用, 需要添加依赖
 * // https://mvnrepository.com/artifact/net.sourceforge.jexcelapi/jxl
 * implementation 'net.sourceforge.jexcelapi:jxl:2.6.12'
 *
 * 2.一定要申请读写文件的权限，不然可能会导出失败(框架已经添加)
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 *
 * 3.示例使用:
 * String[] row1Titles = {"姓名", "年龄", "男孩", "日期"};
 * JExcelApiUtils.initExcel(filePath, "表格顶部标题", row1Titles);
 * JExcelApiUtils.export2Excel(list, filePath, new JExcelApiUtils.OnExportChangeListener());
 * JExcelApiUtils.shareFile(activity, filePath);//分享
 *
 * Author     : 李大发
 * Date       : 2019/10/31 on 16:41
 * @version 1.0
 * @version 1.0.1 做了一点点修改
 */
public class JExcelApiUtils {

    private static       WritableCellFormat  arial14format;
    private static       WritableCellFormat  arial10format;
    private static       WritableCellFormat  arial12format;
    private static final String              UTF8_ENCODING    = "UTF-8";
    private static final SparseArray<String> fieldSparseArray = new SparseArray<>();

    /**
     * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...
     */
    private static void format() {
        try {
            //每次都new, 否则会报错...
            WritableFont arial14font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            arial14font.setColour(Colour.LIGHT_BLUE);
            arial14format = new WritableCellFormat(arial14font);
            arial14format.setAlignment(jxl.format.Alignment.CENTRE);
            arial14format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial14format.setBackground(Colour.VERY_LIGHT_YELLOW);

            WritableFont arial10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            arial10format = new WritableCellFormat(arial10font);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            arial10format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial10format.setBackground(Colour.GRAY_25);
            //对齐格式
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);

            WritableFont arial12font = new WritableFont(WritableFont.ARIAL, 10);
            arial12format = new WritableCellFormat(arial12font);
            //设置边框
            arial12format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化Excel表格, 高版本必须要写sd卡权限, 否则报错
     *
     * @param filePath  存放excel文件的路径（.../path/demo.xls）
     * @param sheetName Excel表格的表名(QQ浏览器打开后会在第一行ABC...的上面一行显示, 电脑没看见显示)
     * @param row1Titles excel中包含的列名(第一行标题)
     */
    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public static void initExcel(String filePath, String sheetName, String[] row1Titles) {
        format();
        WritableWorkbook workbook = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                return;
            }
            //创建Excel表格
            workbook = Workbook.createWorkbook(file);//OutputStream
            //设置表格的名字, 要插入的索引号
            WritableSheet sheet = workbook.createSheet(sheetName, 0);
            //创建标题栏
            sheet.addCell(new Label(0, 0, filePath, arial14format));
            for (int col = 0; col < row1Titles.length; col++) {
                sheet.addCell(new Label(col, 0, row1Titles[col], arial10format));
            }
            //要格式化的行, 行高
            sheet.setRowView(0, 340);
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void export2Excel(@NonNull List list, String filePath, OnExportChangeListener listener) {
        export2Excel(list, filePath, 0, listener);
    }

    /**
     * 将List写入Excel中, 注意:
     * 1.如果excel名称和上次一样, 会覆写
     * 2.如果excel同名, 且第2次的行数 < 第1次的行数, 第1次已经存储的没有被覆盖到的行数会保留!!!
     *
     * @param list 待写入的list<Object>, 注意: Object里的字段必须添加注解 {@link ExcelField}
     * @param filePath 存放excel文件的路径（.../path/demo.xls）
     * @param listener 导出监听
     */
    public static void export2Excel(@NonNull List list, String filePath, int id, OnExportChangeListener listener) {
        if (list.size() == 0) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                WritableWorkbook writebook = null;
                InputStream in = null;
                try {
                    WorkbookSettings settings = new WorkbookSettings();
                    settings.setEncoding(UTF8_ENCODING);
                    //java.lang.IndexOutOfBoundsException: Invalid index 22, size is 21
//                    settings.setWriteAccess(null);

                    in = new FileInputStream(new File(filePath));
                    Workbook workbook = Workbook.getWorkbook(in);
                    writebook = Workbook.createWorkbook(new File(filePath), workbook, settings);
                    WritableSheet sheet = writebook.getSheet(0);
                    for (int j = 0; j < list.size(); j++) {
                        Object object = list.get(j);
                        if (object != null) {
                            Field[] fields = object.getClass().getDeclaredFields();//获取类中所有字段
                            fieldSparseArray.clear();
                            for (Field field : fields) {
                                ExcelField annotation = field.getAnnotation(ExcelField.class);
                                if (annotation != null) {
                                    int index = annotation.columnIndex();//获取字段index
                                    field.setAccessible(true);
                                    Object o = field.get(object);//获取字段值
                                    if (o instanceof Date) {//这个字段是一个日期Date
                                        String format = annotation.dateFieldFormat();//日期格式
                                        fieldSparseArray.put(index, TimeUtils.date2String((Date) o, getNoNull(format)));
                                    } else {
                                        fieldSparseArray.put(index, getNoNull(o));
                                    }
                                }
                            }
                            if (fieldSparseArray.size() == 0) {
                                throw new RuntimeException(object.getClass().getName()
                                        .concat("里的字段需添加注解: ")
                                        .concat(ExcelField.class.getName()));
                            }
                            for (int i = 0; i < fieldSparseArray.size(); i++) {
                                //column, row, data, cell format
                                sheet.addCell(new Label(i, j + 1, fieldSparseArray.get(i), arial12format));
                                if (fieldSparseArray.get(i).length() <= 4) {
                                    //col要设置宽度的列, width以字符为单位的列的宽度
                                    sheet.setColumnView(i, fieldSparseArray.get(i).length() + 8);//设置列宽
                                } else {
                                    sheet.setColumnView(i, fieldSparseArray.get(i).length() + 5);//设置列宽
                                }
                            }
                            sheet.setRowView(j + 1, 350);//设置行高
                        }
                    }
                    writebook.write();
                    workbook.close();
                    if (listener != null) {
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onExportFinish(filePath, id);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onExportError(filePath, id, e);
                            }
                        });
                    }
                } finally {
                    if (writebook != null) {
                        try {
                            writebook.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    private static String getNoNull(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    /**
     * 导出监听
     */
    public interface OnExportChangeListener {

        /**
         * 导出完成
         */
        void onExportFinish(String filePath, int id);

        /**
         * 导出失败
         */
        void onExportError(String filePath, int id, Exception e);
    }
}
