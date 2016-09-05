/*
 * Copyright (C) 2015 Software&System Lab. Kangwon National University.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package MinTFramework.Util.Benchmarks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import jxl.write.Formula;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class MinTExporter extends ExcelExporter{
//    private Collection<BenchAnalize> benchmarks;
    private int period = 0;
    public MinTExporter(String inputFile, int period) {
        super(inputFile);
//        this.benchmarks = benchmarks;
        this.period = period;
    }

    public void makeExcel(Collection<BenchAnalize> benchmarks) {
        try {
            initWorkbook();
            for (BenchAnalize ben : benchmarks) {
                WritableSheet excelSheet = workbook.createSheet(ben.getName(), 0);
                writeData(ben, excelSheet);
            }
            MakeWorkbook();
        } catch (WriteException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void writeData(BenchAnalize ben, WritableSheet excelSheet) throws WriteException {
        createLabel(excelSheet);
        ArrayList<PerformData> datas = ben.getDatas();
        int cnt = 1;
        for(PerformData pd : datas){
            super.addNumber(excelSheet, 0, cnt, cnt);
            super.addNumber(excelSheet, 1, cnt, pd.getNumofPerform());
            super.addNumber(excelSheet, 2, cnt, pd.getTime());
            super.addNumber(excelSheet, 3, cnt, pd.getAvgTime());
            super.addNumber(excelSheet, 4, cnt, pd.getRequest());
            super.addNumber(excelSheet, 5, cnt, pd.getRequestperSec());
            super.addNumber(excelSheet, 6, cnt, pd.getTotalPackets());
            super.addNumber(excelSheet, 7, cnt, pd.getPacketperSec());
            super.addNumber(excelSheet, 8, cnt, pd.getTotalBytes());
            cnt++;
        }
        
        //sum
        addLabel(excelSheet, 0, cnt+1, "Sum");
        for(int i=1;i<9;i++){
            addFormula(excelSheet,i, cnt+1, getCellFucntion(
                    getCellString(i, 1)
                    ,getCellString(i, cnt)
                    ,"SUM"));
        }
        
        //average
        addLabel(excelSheet, 0, cnt+2, "Average");
        for(int i=1;i<9;i++){
            addFormula(excelSheet,i, cnt+2, getCellFucntion(
                    getCellString(i, 1)
                    ,getCellString(i, cnt)
                    ,"AVERAGE"));
        }
    }
    
    private StringBuffer getCellFucntion(String start, String end, String Function){
        StringBuffer buf = new StringBuffer();
        buf.append(Function).append("(").append(start).append(":").append(end).append(")");
        return buf;
    }
    
    private void addFormula(WritableSheet exsheet, int col, int row, StringBuffer data) throws WriteException{
        Formula f = new Formula(col, row, data.toString());
        exsheet.addCell(f);
    }
    
    private void createLabel(WritableSheet excelSheet) {
        try {
            char a = 48;
            addLabel(excelSheet, 0, 0, "time");
            addLabel(excelSheet, 1, 0, "Num of Threads");
            addLabel(excelSheet, 2, 0, "Total Running Time");
            addLabel(excelSheet, 3, 0, "Avg Running Time");
            addLabel(excelSheet, 4, 0, "Total Requests");
            addLabel(excelSheet, 5, 0, "Reqeust/Sec");
            addLabel(excelSheet, 6, 0, "Total Packets");
            addLabel(excelSheet, 7, 0, "Packet/Sec");
            addLabel(excelSheet, 8, 0, "Total bytes");
        } catch (WriteException ex) {
            ex.printStackTrace();
        }
    }
}
