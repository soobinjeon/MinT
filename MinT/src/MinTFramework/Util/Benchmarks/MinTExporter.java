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
        int cnt = 4;
        addTime(excelSheet, 0, cnt, datas);
        
        addIntegerDatas(excelSheet, 1, cnt, ben.getNumberofPerform());
        addLongDatas(excelSheet, 2, cnt, ben.getTotalTime());
        addDoubleDatas(excelSheet, 3, cnt, ben.getAverageTime());
        addLongDatas(excelSheet, 4, cnt, ben.getTotalRequest());
        addDoubleDatas(excelSheet, 5, cnt, ben.getRequestperSeconds());
        addLongDatas(excelSheet, 6, cnt, ben.getTotalPackets());
        addDoubleDatas(excelSheet, 7, cnt, ben.getPacketperSeconds());
        addLongDatas(excelSheet, 8, cnt, ben.getTotalbytes());
    }
    
    private void addIntegerDatas(WritableSheet excelSheet, int col, int cnt, ArrayList<Integer> datas) throws WriteException {
        int total = 0;
        int avgcnt = 0;
        for(int i=0;i<datas.size();i++){
            int data = datas.get(i);
            total += data;
            if(data > 0)
                avgcnt ++;
            super.addNumber(excelSheet, col, i+cnt, data);
        }
        int loc = 1;
        //sum
        super.addNumber(excelSheet, col, loc, total);
        //avg
        double average = avgcnt == 0 ? 0 : (double)(total/(avgcnt));
        super.addNumber(excelSheet, col, loc+1, average);
    }
    
    private void addLongDatas(WritableSheet excelSheet, int col, int cnt, ArrayList<Long> datas) throws WriteException {
        long total = 0;
        int avgcnt = 0;
        for(int i=0;i<datas.size();i++){
            long data = datas.get(i);
            total += data;
            if(data > 0)
                avgcnt ++;
            super.addNumber(excelSheet, col, i+cnt, datas.get(i));
        }
        int loc = 1;
        //sum
        super.addNumber(excelSheet, col, loc, total);
        //avg
        double average = avgcnt == 0 ? 0 : (double)(total/(avgcnt));
        super.addNumber(excelSheet, col, loc+1, average);
    }
    
    private void addDoubleDatas(WritableSheet excelSheet, int col, int cnt, ArrayList<Double> datas) throws WriteException {
        double total = 0;
        int avgcnt = 0;
        for(int i=0;i<datas.size();i++){
            double data = datas.get(i);
            total += data;
            if(data > 0)
                avgcnt ++;
            super.addNumber(excelSheet, col, i+cnt, datas.get(i));
        }
        int loc = 1;
        //sum
        super.addNumber(excelSheet, col, loc, total);
        //avg
        double average = avgcnt == 0 ? 0 : (double)(total/(avgcnt));
        super.addNumber(excelSheet, col, loc+1, average);
    }
    
    private void addTime(WritableSheet excelSheet, int col, int startrow, ArrayList<PerformData> datas) throws WriteException {
        for(int i=0;i<datas.size();i++){
            super.addNumber(excelSheet, col, i+startrow, i+1);
        }
    }
    
    private StringBuffer getCellFucntion(String start, String end, String Function){
        StringBuffer buf = new StringBuffer();
        buf.append(Function).append("(").append(start).append(":").append(end).append(")");
        return buf;
    }
    
    private StringBuffer getAverageIf(String start, String end, String condition){
        StringBuffer buf = new StringBuffer();
        buf.append("AVERAGEIF").append("(")
                .append(start).append(":").append(end)
                .append(",")
                .append("\"").append(condition).append("\"")
                .append(",")
                .append(start).append(":").append(end)
                .append(")");
        System.out.println("print- "+buf.toString());
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
            
            //sum, avg
            addLabel(excelSheet, 0, 1, "Summary");
            addLabel(excelSheet, 0, 2, "Average");
        } catch (WriteException ex) {
            ex.printStackTrace();
        }
    }


}
