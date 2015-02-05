package cn.bc.remoting.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bc.remoting.msoffice.PowerPointSaveFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import cn.bc.remoting.msoffice.ExcelSaveFormat;
import cn.bc.remoting.msoffice.WordSaveFormat;
import cn.bc.remoting.service.WordService;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;

/**
 * 文档转换服务接口的实现
 *
 * @author dragon
 */
public class WordServiceImpl implements WordService {
    private static Logger logger = LoggerFactory.getLogger(WordServiceImpl.class);
    private static String TEMP_DIR = "/data_rmi/temp";
    private static String FROM_ROOT_DIR = "/data_rmi/source";
    private static String TO_ROOT_DIR = "/data_rmi/convert";
    private boolean compatible = false;
    private DateFormat df4fileName = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
    private DateFormat df4yearMonth = new SimpleDateFormat("yyyyMM");

    public void setTempDir(String tempDir) {
        TEMP_DIR = tempDir;
    }

    public void setFromRootDir(String fromRootDir) {
        FROM_ROOT_DIR = fromRootDir;
    }

    public void setToRootDir(String toRootDir) {
        TO_ROOT_DIR = toRootDir;
    }

    public void setCompatible(boolean compatible) {
        this.compatible = compatible;
    }

    public boolean test(String token) throws RemoteException {
        Assert.hasText(token, "token could not be null.");
        logger.debug("test:token={}", token);
        return true;
    }

    public boolean convertFormat(String token, String fromFile, String toFile) throws RemoteException {
        // 参数验证
        Assert.notNull(token, "token could not be null.");
        Assert.hasText(fromFile, "fromFile could not be null.");
        Assert.hasText(toFile, "toFile could not be null.");

        // 根据文件名获取文件格式
        String fromFormat = StringUtils.getFilenameExtension(fromFile).toLowerCase();
        String toFormat = StringUtils.getFilenameExtension(toFile).toLowerCase();

        // 检测来源文件必须存在，否则抛出异常
        fromFile = FROM_ROOT_DIR + "/" + fromFile;
        toFile = TO_ROOT_DIR + "/" + toFile;

        // 转换路径中的/为\避免Windows打开保存文件的路径错误
        fromFile = fromFile.replaceAll("/", "\\\\");
        toFile = toFile.replaceAll("/", "\\\\");
        if (logger.isDebugEnabled()) {
            logger.debug("convertFormat:token={}", token);
            logger.debug("convertFormat:fromFile={}", fromFile);
            logger.debug("convertFormat:toFile={}", toFile);
        }

        File from = new File(fromFile);
        if (!from.exists()) {
            throw new RemoteException("FileNotFound:fromFile=" + fromFile);
        }

        // 创建目标文件所在的目录
        File f = new File(toFile);
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }

        // 根据来源文件格式调用相应的方法进行文档格式转换
        if (isWordFormat(fromFormat)) {// Word文档格式转换
            convertByWord(fromFile, toFile, WordSaveFormat.get(toFormat).getValue());
        } else if (isExcelFormat(fromFormat)) {// Excel文档格式转换
            convertByExcel(fromFile, toFile, ExcelSaveFormat.get(toFormat).getValue());
        } else if (isPowerPointFormat(fromFormat)) {// PowerPoint 文档格式转换
            convertByPowerPoint(fromFile, toFile, PowerPointSaveFormat.get(toFormat).getValue());
        } else {
            throw new RemoteException("unsupport file type: fromFormat=" + fromFormat);
        }

        // 记录一条转换日志
        logger.warn("convert:token={}, fromFile={}, toFile={}", token, fromFile, toFile);
        return true;
    }

    /**
     * 判断是否是 Excel 可以打开的文档格式
     *
     * @param fromFormat
     * @return
     */
    private boolean isExcelFormat(String fromFormat) {
        return "xls".equals(fromFormat) || "xlsx".equals(fromFormat)
                || "xlsm".equals(fromFormat) || "csv".equals(fromFormat);
    }

    /**
     * 判断是否是 Word 可以打开的文档格式
     *
     * @param fromFormat
     * @return
     */
    private boolean isWordFormat(String fromFormat) {
        return "doc".equals(fromFormat) || "docx".equals(fromFormat) || "docm".equals(fromFormat)
                || "txt".equals(fromFormat) || "rtf".equals(fromFormat);
    }

    /**
     * 判断是否是 PowerPoint 可以打开的文档格式
     *
     * @param fromFormat
     * @return
     */
    private boolean isPowerPointFormat(String fromFormat) {
        return "ppt".equalsIgnoreCase(fromFormat) || "pptx".equalsIgnoreCase(fromFormat)
                || "pptm".equalsIgnoreCase(fromFormat);
    }

    public byte[] convertFormat(String token, byte[] source, String fromFormat, String toFormat) throws RemoteException {
        Assert.notNull(source, "source could not be null.");
        InputStream t = convert(token, new ByteArrayInputStream(source), fromFormat, toFormat);
        try {
            return FileCopyUtils.copyToByteArray(t);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new RemoteException(e.getMessage(), e);
        }
    }

    /**
     * 转换文件流
     *
     * @param token
     * @param source
     * @param fromFormat
     * @param toFormat
     * @return
     * @throws IOException
     */
    private InputStream convert(String token, InputStream source, String fromFormat, String toFormat) throws RemoteException {
        try {
            Assert.hasText(token, "token could not be null.");
            Assert.notNull(source, "source could not be null.");
            Assert.notNull(fromFormat, "fromFormat could not be null.");
            Assert.notNull(toFormat, "toFormat could not be null.");
            if (logger.isDebugEnabled()) {
                logger.debug("convert:token=" + token);
                logger.debug("convert:fromFormat=" + fromFormat);
                logger.debug("convert:toFormat=" + toFormat);
            }
            Date now = new Date();

            String dateDir = df4yearMonth.format(now);
            String fileName = df4fileName.format(now);
            // 将原始文件流保存为临时文件
            String tempDir = TEMP_DIR + "/" + dateDir + "/";
            File dir = new File(tempDir);
            if (!dir.exists())
                dir.mkdirs();
            String fromName = fileName + "." + fromFormat;
            String fromFile = tempDir + fromName;
            fromFile = fromFile.replaceAll("/", "\\\\");// 转换路径中的/为\避免Windows打开保存文件的路径错误
            logger.debug("convert:fromFile={}", fromFile);
            FileCopyUtils.copy(source, new FileOutputStream(fromFile));

            // 转换后的文件保存到的路径
            String toName = fileName + ".to." + toFormat;
            String toFile = tempDir + toName;
            toFile = toFile.replaceAll("/", "\\\\");// 转换路径中的/为\避免Windows打开保存文件的路径错误
            logger.debug("convert:toFile={}", toFile);

            if (isWordFormat(fromFormat)) {// Word 文档格式转换
                convertByWord(fromFile, toFile, WordSaveFormat.get(toFormat).getValue());
            } else if (isExcelFormat(fromFormat)) {// Excel 文档格式转换
                convertByExcel(fromFile, toFile, ExcelSaveFormat.get(toFormat).getValue());
            } else if (isPowerPointFormat(fromFormat)) {// PowerPoint 文档格式转换
                convertByPowerPoint(fromFile, toFile, PowerPointSaveFormat.get(toFormat).getValue());
            } else {
                throw new RemoteException("unsupport file type: fromFormat=" + fromFormat);
            }

            // 检测转换后的文件是否存在
            File target = new File(toFile);
            if (!target.exists()) {
                throw new RemoteException("convert failed: fromFormat=" + fromFormat + ",toFile=" + toFile);
            }

            // 返回文件流
            return new FileInputStream(target);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new RemoteException(e.getMessage(), e);
        }
    }

    /**
     * 使用 jacob 调用 Office 服务执行 Word 文档格式转换
     *
     * @param fromFile 来源文件的绝对路径
     * @param toFile   转换后的文件要保存到的绝对路径
     * @param toFormat 转换后的文件格式
     */
    private void convertByWord(String fromFile, String toFile, int toFormat) {
        ActiveXComponent wordApp = null;
        try {
            logger.info("----Word: Start----");

            // 打开 Word 应用程序
            wordApp = new ActiveXComponent("Word.Application");

            // 设置 Word 不可见
            wordApp.setProperty("Visible", false);

            // 获取 Application 对象的 Documents 属性
            Dispatch wordDocuments = wordApp.getProperty("Documents").toDispatch();

            // 调用 Documents 对象中 Open 方法打开文档，并返回打开的文档对象 Document
            // see: https://msdn.microsoft.com/en-us/library/office/ff835182(v=office.15).aspx
            Dispatch doc = Dispatch.call(wordDocuments, "Open"  // 调用Documents对象的Open方法
                    , fromFile      // 文件全路径名
                    , false         // ConfirmConversions: 设置为false表示不显示转换框
                    , true          // ReadOnly
                    , false         // AddToRecentFiles
                    , "bc"          // PasswordDocument: The password for opening the document.
                    , "bc"          // PasswordTemplate: The password for opening the template.
                    , true          // Revert: 如果已打开同名文件，强制关闭重新打开
                    , "bc"          // WritePasswordDocument: The password for saving changes to the document.
                    , "bc"          // WritePasswordTemplate: The password for saving changes to the template.
            ).toDispatch();

            // 检测目标文件是否存在，存在就先删除
            File f = new File(toFile);
            if (f.exists()) {
                logger.warn("file exists, delete it first: file={}", toFile);
                f.delete();
            }

            if (this.compatible) {
                // Office 2007+ 调用ExportAsFixedFormat:目标文件不能存在，否则报错
                // (2007需要按装插件才能支持此方法：http://www.microsoft.com/zh-cn/download/details.aspx?id=7)
                logger.debug("----Word: ExportAsFixedFormat----");
                Dispatch.call(doc, "ExportAsFixedFormat", toFile, toFormat);
            } else {
                // Office 2010 调用Document对象的SaveAs2方法:目标文件不能存在，否则报错
                // see: https://msdn.microsoft.com/en-us/library/office/ff836084(v=office.15).aspx
                logger.debug("----Word: SaveAs2----");
                Dispatch.call(doc, "SaveAs2", toFile, toFormat);
            }

            // 关闭文档 https://msdn.microsoft.com/en-us/library/microsoft.office.tools.word.document.close.aspx
            Dispatch.call(doc, "Close", false);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (wordApp != null) {
                try {
                    // https://msdn.microsoft.com/en-us/library/office/ff844895(v=office.15).aspx
                    logger.info("----Word: Quit----");
                    wordApp.invoke("Quit", 0);
                } catch (Exception e) {
                    logger.debug(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 使用 jacob 调用 Office 服务执行 Excel 文档格式转换
     *
     * @param fromFile 来源文件的绝对路径
     * @param toFile   转换后的文件要保存到的绝对路径
     * @param toFormat 转换后的文件格式
     */
    private void convertByExcel(String fromFile, String toFile, int toFormat) {
        ActiveXComponent excelApp = null;
        try {
            if (logger.isInfoEnabled()) {
                logger.info("----Excel: Start----");
                logger.info("fromFile={}", fromFile);
                logger.info("toFile={}", toFile);
                logger.info("toFormat={}", toFormat);
            }

            // 打开 Excel 应用程序
            excelApp = new ActiveXComponent("Excel.Application");

            // 设置 Excel 不可见
            excelApp.setProperty("Visible", false);

            // 获取 Application 对象的 Workbooks 属性
            Dispatch workbooks = excelApp.getProperty("Workbooks").toDispatch();

            // 调用 Workbooks 对象的 Open 方法打开文档，并返回打开的文档对象 Workbook
            // see: https://msdn.microsoft.com/en-us/library/office/ff194819(v=office.15).aspx
            Dispatch workbook = Dispatch.call(workbooks, "Open",
                    fromFile,   // Excel 文件全路径名
                    false,      // UpdateLinks
                    true,       // ReadOnly
                    5,          // Format: Nothing
                    "bc",       // Password: 打开文件的密码，若有密码则进行匹配，无会直接打开
                    "bc",         // WriteResPassword: 编辑文件的密码
                    true        // IgnoreReadOnlyRecommended: 对只读文件是否不显示只读提示框
            ).toDispatch();

            // 检测目标文件是否存在，存在就先删除
            File f = new File(toFile);
            if (f.exists()) {
                logger.warn("file exists, delete it first: file={}", toFile);
                f.delete();
            }

            if (this.compatible || toFormat == ExcelSaveFormat.PDF.getValue()) {
                // Office 2007+ 调用ExportAsFixedFormat:目标文件不能存在，否则报错
                // (2007需要按装插件才能支持此方法：http://www.microsoft.com/zh-cn/download/details.aspx?id=7)
                logger.debug("----Excel: ExportAsFixedFormat----");
                Dispatch.call(workbook, "ExportAsFixedFormat", toFormat, toFile);
            } else {
                // Office 2010 调用Document对象的SaveAs方法:目标文件不能存在，否则报错
                // see: https://msdn.microsoft.com/en-us/library/office/ff841185(v=office.15).aspx
                logger.debug("----Excel: SaveAs----");
                Dispatch.call(workbook, "SaveAs", toFile, toFormat);
            }

            // 关闭文档
            Dispatch.call(workbook, "Close", false);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (excelApp != null) {
                try {
                    logger.info("----Excel: Quit----");
                    excelApp.invoke("Quit");
                } catch (Exception e) {
                    logger.debug(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 使用 jacob 调用 Office 服务执行 PowerPoint 文档格式转换
     *
     * @param fromFile 来源文件的绝对路径
     * @param toFile   转换后的文件要保存到的绝对路径
     * @param toFormat 转换后的文件格式
     */
    private void convertByPowerPoint(String fromFile, String toFile, int toFormat) {
        ActiveXComponent ppApp = null;
        try {
            logger.info("----PowerPoint: Start----");

            // 打开 PowerPoint 应用程序 https://msdn.microsoft.com/en-us/library/microsoft.office.interop.powerpoint.application.aspx
            ppApp = new ActiveXComponent("PowerPoint.Application");

            // 设置 PowerPoint 不可见：不支持
            // ppApp.setProperty("Visible", false);

            // 获取 Application 对象的 Presentations 属性
            Dispatch presentations = ppApp.getProperty("Presentations").toDispatch();

            Dispatch presentation;
            try {
                // 调用 Presentations 对象中 Open 方法打开文档，并返回打开的文档对象 Document
                // see: https://msdn.microsoft.com/en-us/library/office/ff746171(v=office.15).aspx
                presentation = Dispatch.call(presentations, "Open"  // 调用Documents对象的Open方法
                        , fromFile      // 文件全路径名
                        , true          // ReadOnly
                        , true         // Untitled: 指定文件是否有标题
                        , false          // WithWindow: 指定文件是否可见
                ).toDispatch();
            } catch (Exception e){// 如果文件有密码就会有异常，进入这里，尝试使用其他方法打开
                logger.warn("Open PowerPoint File with special way ...fromFile={}", fromFile);
                // 获取 Application 对象的 ProtectedViewWindows 属性
                // https://msdn.microsoft.com/en-us/library/microsoft.office.interop.powerpoint.protectedviewwindows.open(v=office.14).aspx
                Dispatch protectedViewWindows = ppApp.getProperty("ProtectedViewWindows").toDispatch();

                // 调用 ProtectedViewWindows 对象中 Open 方法打开密码文档，并返回 ProtectedViewWindow
                // https://msdn.microsoft.com/en-us/library/microsoft.office.interop.powerpoint.protectedviewwindow(v=office.14).aspx
                Dispatch protectedViewWindow = Dispatch.call(protectedViewWindows, "Open"  // 调用Documents对象的Open方法
                        , fromFile      // 文件全路径名
                        , "bc"          // ReadPassword: 密码
                        , false         // OpenAndRepair
                ).toDispatch();

                // 激活 Presentation
                presentation = Dispatch.call(protectedViewWindow, "Edit", "").toDispatch();
            }

            // 检测目标文件是否存在，存在就先删除
            File f = new File(toFile);
            if (f.exists()) {
                logger.warn("file exists, delete it first: file={}", toFile);
                f.delete();
            }

            if (this.compatible) {
                // Office 2007+ 调用ExportAsFixedFormat:目标文件不能存在，否则报错
                // (2007需要按装插件才能支持此方法：http://www.microsoft.com/zh-cn/download/details.aspx?id=7)
                logger.debug("----PowerPoint: ExportAsFixedFormat----");
                Dispatch.call(presentation, "ExportAsFixedFormat", toFile, toFormat);
            } else {
                // Office 2010 调用 PowerPoint 对象的 SaveAs 方法:目标文件不能存在，否则报错
                // see: https://msdn.microsoft.com/en-us/library/office/ff841185(v=office.15).aspx
                logger.debug("----PowerPoint: SaveAs----");
                Dispatch.call(presentation, "SaveAs", toFile, toFormat);
            }

            // 关闭文档 https://msdn.microsoft.com/en-us/library/office/ff743857(v=office.15).aspx
            Dispatch.call(presentation, "Close");
            //Dispatch.call(protectedViewWindow, "Close");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (ppApp != null) {
                try {
                    // https://msdn.microsoft.com/en-us/library/office/ff746388(v=office.15).aspx
                    logger.info("----PowerPoint: Quit----");
                    ppApp.invoke("Quit");
                } catch (Exception e) {
                    logger.debug(e.getMessage(), e);
                }
            }
        }
    }
}