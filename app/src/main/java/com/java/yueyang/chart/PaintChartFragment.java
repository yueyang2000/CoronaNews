package com.java.yueyang.chart;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.java.yueyang.R;
import com.java.yueyang.data.Epidemic;
import com.java.yueyang.data.Manager;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;


/**
 * 新闻主页面
 * Created by equation on 9/8/17.
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */

public class PaintChartFragment extends Fragment {
    private Spinner mProvinceSpinner;
    private Spinner mCountrySpinner;
    private Spinner mTypeSpinner;
    private ProgressBar mProgrossBar;
    private View content;
    private Button mButton;
    private String country = "";
    private String province = "";
    private int type = -1;
    private View view;
    private LineChart lineChart;
    private TextView graphTextView;




    public PaintChartFragment() {
        // Required empty public constructor
    }


    public static PaintChartFragment newInstance() {
        PaintChartFragment fragment = new PaintChartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TypedValue colorPrimary = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, colorPrimary, true);

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.data_chart, container, false);


        mCountrySpinner = view.findViewById(R.id.data_country);
        mProvinceSpinner = view.findViewById(R.id.data_province);
        mTypeSpinner = view.findViewById(R.id.data_type);
        mProgrossBar = view.findViewById(R.id.progress_bar);
        lineChart = view.findViewById(R.id.lineChart);
        content = view.findViewById(R.id.content);
        mProgrossBar.setVisibility(View.VISIBLE);
        content.setVisibility(View.INVISIBLE);



        loadData();
        return view;
    }
    private void loadData(){
        if(!Epidemic.loaded){
            Single<JSONObject> single = null;
            single = Manager.I.fetchData();
            single.subscribe(new Consumer<JSONObject>() {
                @Override
                public void accept(JSONObject simpleNewses) throws Exception {
                    Epidemic.loaded = true;
                    Epidemic.data = simpleNewses;
                    System.out.println("download epidemic data");
                    for (Iterator<String> it = simpleNewses.keys(); it.hasNext(); ) {
                        String key = it.next();
                        String[] places = key.split("\\|");

                        String country = places[0];
                        String province = (places.length<2) ? "all": places[1];
                        Epidemic.countries.add(country);
                        TreeSet<String> map = Epidemic.place_map.get(country);
                        if(map == null) map = new TreeSet<>();
                        map.add(province);
                        Epidemic.place_map.put(country, map);
                    }
                    loadFinish();
                }
            });
        }
        else{
            loadFinish();
        }

    }
    private void loadFinish(){
        mProgrossBar.setVisibility(View.INVISIBLE);
        content.setVisibility(View.VISIBLE);
        ArrayAdapter<String> countryApatper = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1,  Epidemic.countries.toArray(new String[Epidemic.countries.size()]));
        mCountrySpinner.setAdapter(countryApatper);
        mProvinceSpinner = view.findViewById(R.id.data_province);
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1);
        mProvinceSpinner.setAdapter(provinceAdapter);

        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String c = adapterView.getItemAtPosition(position).toString();//获得国家
                country = c;
                ArrayList<String> provinces = new ArrayList<>(Epidemic.place_map.get(country));
                provinces.remove("all");
                provinces.add(0, "all");
                String[] allProvinces =  provinces.toArray(new String[provinces.size()]);
                provinceAdapter.clear();
                provinceAdapter.addAll(allProvinces);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mProvinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                province = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1, Epidemic.types);
        mTypeSpinner.setAdapter(typeAdapter);
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mButton = view.findViewById(R.id.draw_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(country!="" && province!= "" && type!=-1){
                    drawChart();
                }
            }
        });
    }
    private void drawChart() {
        ArrayList<String> dataList = Epidemic.getData(country, province, type);


        lineChart.setVisibility(View.VISIBLE);
        String begin = Epidemic.getDate(country, province, type);
        SimpleDateFormat sdf = sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try{
            date = sdf.parse(begin);
        }
        catch(Exception e){
            return;
        }


        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
                String tmp = dataList.get(i);
                if(tmp != "null") {
                    entries.add(new Entry(i, Integer.parseInt(tmp)));
                }
        }
        if (entries.size() == 0) {
            Toast.makeText(this.getActivity(), "无数据", Toast.LENGTH_SHORT).show();
            lineChart.setVisibility(View.GONE);
            return;
        }
        else if(entries.size() == 1){
            entries.add(entries.get(0));
            entries.add(entries.get(0));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(Color.parseColor("#AABCC6"));//线条颜色
//        dataSet.setCircleColor(Color.parseColor("#7d7d7d"));//圆点颜色
        dataSet.setDrawValues(false);                     // 设置是否显示数据点的值
        dataSet.setDrawCircleHole(false);                 // 设置数据点是空心还是实心，默认空心
        dataSet.setCircleColor(Color.parseColor("#AABCC6"));              // 设置数据点的颜色
        dataSet.setCircleSize(1);                         // 设置数据点的大小
        dataSet.setHighLightColor(Color.parseColor("#AABCC6"));            // 设置点击时高亮的点的颜色
        dataSet.setLineWidth(2f);//线条宽度

        //设置样式
        YAxis rightAxis = lineChart.getAxisRight();
        //设置图表右边的y轴禁用
        rightAxis.setEnabled(false);
        YAxis leftAxis = lineChart.getAxisLeft();
        //设置图表左边的y轴禁用
        leftAxis.setEnabled(true);
        leftAxis.setTextColor(Color.parseColor("#333333"));

        //设置x轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextColor(Color.parseColor("#333333"));
        xAxis.setTextSize(11f);
        xAxis.setAxisMinimum(0f);
        xAxis.setDrawAxisLine(false);//是否绘制轴线
        xAxis.setDrawGridLines(false);//设置x轴上每个点对应的线
        xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        xAxis.setGranularity(1f);//禁止放大后x轴标签重绘

        Calendar calendar = new GregorianCalendar();

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int val = (int) value;
                if (val < 0) {
                    val = 0;
                }
                calendar.setTime(date);
                calendar.add(Calendar.DATE, val);
                return simpleDateFormat.format(calendar.getTime());
            }
        });

        //透明化图例
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.NONE);
        legend.setTextColor(Color.WHITE);

        //隐藏x轴描述
        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);


        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); // refresh

    }
}
