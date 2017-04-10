package com.example.hrv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.ScatterChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cqupt.filter.ECGFilter;
import com.cqupt.filter.SmoothFilter;
import com.example.hrv.FirstActivity;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.historyActivity.HistoryActivity;
import com.historyActivity.PanelActivity;
import com.hrv.Constants;
import com.hrv.LocalMaximaFinder;
import com.hrv.RR_Analysis;
import com.hrv.R_Location;
import com.hrv.RecordDetial;
import com.login.ClientUtil;
import com.login.LoadingActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.roundProgressbar.BreathGuideActivity;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.userInfoActivity.UserInfoActivity;


@SuppressLint({"InlinedApi", "NewApi"})
public class FirstActivity extends FragmentActivity implements View.OnClickListener {

    // 低通滤波器
    private ECGFilter ecgFilter;

    private long ecgFilterPtr;
    private long mExitTime;
    private static final String ACTION = "com.hrv.HRV.FINISH";
    private BroadcastReceiver endReceiver;

    // 实时心率计算相关变量
    private int[] R_index_data = new int[512];
    private int Ri = 0, real = 0, Rreal = 0;
    private int resp_real = 0, resp_Rreal = 0;
    private int realHeartRate;
    private int realRespRate;

    // 关于按钮的变量
    private Button startButton;
    private FrameLayout startFrameLayout;
    private boolean isReading = false;
    private Button huxiButton;
    private FrameLayout huxiFrameLayout;
    private boolean isHuxi = false;
    private Button lianjie;
    private Button xinxi;

    // 关于flagment的 变量
    private ResideMenu resideMenu;
    private FirstActivity mContext;
    private ResideMenuItem itemYonghu;
    private ResideMenuItem itemJilu;
    private ResideMenuItem itemShezhi;
    private ResideMenuItem itemHelp;
    private ResideMenuItem itemFankui;
    private ResideMenuItem itemGuanyu;

    // 串口驱动声明
    private D2xxManager ftdid2xx;// 用于连接FTDI设备的管理类
    private FT_Device ftDev = null;// 用于为HOST提供不同APIs的设备类，便于与不同的FT设备进行交流操作
    private FT232 ft232 = new FT232();
    private boolean isopen = true;
    private boolean isre = true;
    private int k = 0, m = 1, l = 200;

    // 存储心电数据
    private ArrayList<Float> EcgData = new ArrayList<Float>();
    private float[] Ynn = new float[150000];

    // 存储呼吸数据
    private ArrayList<Float> respData = new ArrayList<Float>();
    private float[] respYnn = new float[150000];
    // private LocalMaximaFinder extreme;
// 形态学滤波器
// private Morphology morphology;
    private SmoothFilter smoothFilter;
    private int resp_num = 0;
    // private int ecg_num = 0;
    private final int RESP_SUM = 6500;

    // 与SD卡数据存储相关的变量
    private boolean sdcardExit;
    private File dateDir;
    private String origDataAdress;
    private String origDataName;
    private String USER_NAME;// 用户名
    private String date;// 日期
    private String time;// 时间
    private String DATE = "date";
    private String TIME = "time";

    // 用于HRV分析的相关变量
    private String inferData = null;// inferData
    // ="心率：呼吸：焦虑指数：SDNN:NN50：r_MSSD:tpv:vlf:lf:hf"
    private String sandianData = null;// sandianData = "数据1：数据2：数据3：..."
    private String pointStr = null; // 绘制心电图的数据

    // 画图有关的变量
    private LinearLayout mLayout;
    private XYSeries series, Rseries;// XY数据点r
    private XYMultipleSeriesDataset mDataset;// XY轴数据集
    private GraphicalView mViewChart;// 用于显示现行统计图
    private XYMultipleSeriesRenderer mXYRenderer;// 线性统计图主描绘器
    private int X = 1500;// X数据集大小
    private String title = "心电";

    // 实时心率有关变量
    private ImageView SSXLbaiwei;
    private ImageView SSXLshiwei;
    private ImageView SSXLgewei;
    private TextView huxi;
// private int Quzhi = 0;
// private int shimiaojishi = 0;

    // 5分钟定时的有关变量
    private TextView MtextView, FtextView, huadong;
    private int miao = 60;
    private int fen = 4;
    private Timer timer = new Timer();// 定时器
    private TimerTask task; // 定时任务
    private LinearLayout jishixianshi;
    // 分辨率，未用到
    private static int fenbian;

    // 与panel相关的控件
    private TextView[] dateView = new TextView[7];
    private ImageButton[] imageButton = new ImageButton[7];
    private String[] panelText = new String[7];
    private String[] dates;
    private int dateNum = 0;
    private int[] dateViews = {R.id.yihao, R.id.erhao, R.id.sanhao, R.id.sihao
            , R.id.wuhao, R.id.liuhao, R.id.qihao};
    private int[] imageButtons = {R.id.yihaotu, R.id.erhaotu, R.id.sanhaotu, R.id.sihaotu
            , R.id.wuhaotu, R.id.liuhaotu, R.id.qihaotu};

    private HandlerThread handlerThread;
    private Handler realHandler;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置不显示标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_first);


        mContext = this;
        fenbian = getPixe();

        handlerThread = new HandlerThread("handler thread");
        handlerThread.start();
        realHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what == Constants.MESSAGE_CACULATE_HR) {
                    Real_xinlv();
                }

                if (msg.what == Constants.MESSAGE_CACULATE_RESP) {
                    real_respRate();
                }

            }
        };

        // 初始化Flagment控件
        setUpMenu();
        // 获取用户名，作为数据文件的命名
        getUserName();
        // 初始化主界面的相关控件
        initView();

        // 低通滤波
        ecgFilter = new ECGFilter();
        ecgFilterPtr = ecgFilter.init();
        // 形态学滤波
        // morphology = new Morphology();
        // 平滑滤波
        smoothFilter = new SmoothFilter();
        // 呼吸波峰检测
        // extreme = new LocalMaximaFinder();

        // 发送广播，结束登录界面
        Intent finiIntent = new Intent();
        finiIntent.setAction("com.login.LoginActivity.FINISH");
        sendBroadcast(finiIntent);

        // 注册广播，测试结束后结束当前界面
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        endReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 结束界面
                FirstActivity.this.finish();
            }
        };

        registerReceiver(endReceiver, filter);
    }


    /**
     * 初始化界面的相关控件
     */
    private void initView() {

        // Panel控件的初始化
        for (int i = 0; i < dateView.length; i++) {
            dateView[i] = (TextView) findViewById(dateViews[i]);
            dateView[i].setText("--");
        }
        for (int i = 0; i < dateView.length; i++) {
            imageButton[i] = (ImageButton) findViewById(imageButtons[i]);
            imageButton[i].setOnClickListener(this);
        }
        // 向服务器发送查询请求，获得最近一周的历史检测日期
        initPanel();

        // 这里获得xy_chart的布局，下面会把图表画在这个布局里面
        mLayout = (LinearLayout) findViewById(R.id.chart);
        // 这个类用来放置曲线上的所有点，是一个点的集合，根据这些点画出曲线
        series = new XYSeries(title);
        // 此程序未用到
        Rseries = new XYSeries("R波定位");
        // 创建一个数据集的实例，这个数据集将被用来创建图表
        mDataset = new XYMultipleSeriesDataset();
        // 将点集添加到这个数据集中
        mDataset.addSeries(series);
        mDataset.addSeries(0, Rseries);
        // 设置颜色
        // int color = Color.rgb(192, 255, 140);
        int color = Color.GREEN;
        // 设置点的风格
        PointStyle style = PointStyle.POINT;
        mXYRenderer = buildRenderer(color, style, true);
        // mXYRenderer.setBackgroundColor(Color.parseColor("#f3f3f3"));
        mXYRenderer.setApplyBackgroundColor(true);
        // 图表与屏幕四边的间距颜色
        // mXYRenderer.setMarginsColor(Color.argb(0, 0xF3, 0xF3, 0xF3));
        // mXYRenderer.setChartTitleTextSize(30);
        // mXYRenderer.setAxisTitleTextSize(25);
        // 显示表格
        mXYRenderer.setShowGrid(true);
        // 设置背景表格颜色
        mXYRenderer.setGridColor(Color.GRAY);
        // 设置X轴标签
        mXYRenderer.setXLabels(15);
        // 设置Y轴标签
        mXYRenderer.setYLabels(10);
        // 右对齐
        mXYRenderer.setYLabelsAlign(Align.RIGHT);
        // 不显示图例
        mXYRenderer.setShowLegend(false);
        mXYRenderer.setZoomEnabled(false);
        mXYRenderer.setPanEnabled(true, false);
        mXYRenderer.setClickEnabled(false);
        mXYRenderer.setPointSize(5f);

        XYSeriesRenderer R_Renderer = new XYSeriesRenderer();
        R_Renderer.setColor(Color.RED);
        R_Renderer.setPointStyle(PointStyle.X);
        R_Renderer.setFillPoints(true);
        mXYRenderer.addSeriesRenderer(0, R_Renderer);
        setChartSettings(mXYRenderer, " ", null, "幅度/mV", 0, X, -20000, 35000,
                Color.WHITE, Color.WHITE);// 这个是采用官方APIdemo提供给的方法
        // 设置好图表的样式
        String[] types = new String[]{ScatterChart.TYPE, LineChart.TYPE};
        // 通过ChartFactory生成图表
        mViewChart = ChartFactory.getCombinedXYChartView(mContext, mDataset,
                mXYRenderer, types);
        // 将图表添加到布局中去
        mLayout.addView(mViewChart, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        // “开始”按钮的初始化
        startFrameLayout = (FrameLayout) findViewById(R.id.frame);
        startButton = (Button) findViewById(R.id.start);
        startButton.setOnClickListener(this);
        // “呼吸引导”按钮的初始化
        huxiFrameLayout = (FrameLayout) findViewById(R.id.huxi_layout);
        huxiButton = (Button) findViewById(R.id.huxi_button);
        huxiButton.setOnClickListener(this);
        // “硬件连接”与“信息显示”按钮的初始化（功能还未添加）
        lianjie = (Button) findViewById(R.id.lianjie);
        lianjie.setOnClickListener(this);
        xinxi = (Button) findViewById(R.id.xinxi);
        xinxi.setOnClickListener(this);
        // 实时心率显示
        SSXLbaiwei = (ImageView) findViewById(R.id.icon_1);
        SSXLshiwei = (ImageView) findViewById(R.id.icon_2);
        SSXLgewei = (ImageView) findViewById(R.id.icon_3);
        // 实时呼吸率显示
        huxi = (TextView) findViewById(R.id.jihuxi);
        // 5分钟计时显示
        MtextView = (TextView) findViewById(R.id.miaotext);
        FtextView = (TextView) findViewById(R.id.fentext);
        huadong = (TextView) findViewById(R.id.huadong);
        jishixianshi = (LinearLayout) findViewById(R.id.jishi);
    }

    /**
     * 画图有关的控件
     *
     * @param color
     * @param style
     * @param fill
     * @return
     */
    protected XYMultipleSeriesRenderer buildRenderer(int color,
                                                     PointStyle style, boolean fill) {// 设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(color);
        r.setPointStyle(style);
        r.setFillPoints(fill);
        r.setLineWidth(3);
        renderer.addSeriesRenderer(r);

        return renderer;
    }

    /**
     * 画图有关的控件
     */
    protected void setChartSettings(XYMultipleSeriesRenderer renderer,
                                    String title, String xTitle, String yTitle, double xMin,
                                    double xMax, double yMin, double yMax, int axesColor,
                                    int labelsColor) {// 设置主描绘器的各项属性，详情可阅读官方API文档
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
    }

    /**
     * 用于通知USB设备接入
     */
    @Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            ft232.notifyUSBDeviceAttach();
        }
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                ft232.notifyUSBDeviceDetach();
            }

        }
    };

    // 与Activity生命周期相关的函数
    @Override
    public void onResume() {
        super.onResume();
        ft232.openDev();
        uiInit();
    }

    // 与Activity生命周期相关的函数
    @Override
    protected void onPause() {
        super.onPause();
        // HRV.this.finish();
    }

    // 与Activity生命周期相关的函数
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ft232 != null)
            ft232.disconnectFunction();
        if (endReceiver != null)
            unregisterReceiver(endReceiver);
        if (handlerThread != null) {
            if (handlerThread.isAlive()) {
                handlerThread.quit();
            }
        }
        // unregisterReceiver(mUsbReceiver);
    }

    /**
     * 以文件形式存储数据至手机SD卡
     */
    public void writeDataToSD(float[] ecgData, float[] respData) {

        String SUFFIX = ".txt";
        // 判断sd Card是否插入
        sdcardExit = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        origDataName = time + SUFFIX;
        origDataAdress = Environment.getExternalStorageDirectory().getPath()
                + "/" + USER_NAME + "/" + "/" + date + "/";
        if (sdcardExit) {

            dateDir = new File(origDataAdress);
            if (!dateDir.exists()) {
                // mkdirs()可以创建多级目录
                dateDir.mkdirs();
            }
        }

        File ecgFile = new File(dateDir.getAbsolutePath(), "ecg_"
                + origDataName);
        if (!ecgFile.exists()) {
            try {
                /* 流的对象 */
                RandomAccessFile raf = new RandomAccessFile(ecgFile, "rw");
                // 将文件记录指针移动到最后
                raf.seek(ecgFile.length());
                for (int i = 0; i < resp_num; i++) {
                    // 开始写入数据
                    raf.writeFloat(ecgData[i]);
                }
                // 关闭流的使用
                raf.close();
            } catch (Exception e) {
                e.printStackTrace();
				Toast.makeText(mContext, "文件写入失败", Toast.LENGTH_LONG).show();
            }
        }
        File respFile = new File(dateDir.getAbsolutePath(), "resp_"
                + origDataName);
        if (!respFile.exists()) {
            try {
                RandomAccessFile rand = new RandomAccessFile(respFile, "rw");
                rand.seek(respFile.length());
                for (int j = 0; j < resp_num; j++) {
                    // 开始写入数据
                    rand.writeFloat(respData[j]);
                }
                rand.close();
            } catch (Exception e) {
                e.printStackTrace();
				Toast.makeText(mContext, "文件写入失败", Toast.LENGTH_SHORT).show();
            }

        }

    }

    /**
     * 从SD卡中读取心电数据
     */
    public float[] readDataFromSD() {
        float data[] = new float[150000];
        File parent = Environment.getExternalStorageDirectory();
        File file = new File(parent, "xind.txt");
        if (file.exists()) {

            try {
				/* 流的对象 */
                RandomAccessFile raf = new RandomAccessFile(file, "r");

                for (int i = 0; i < data.length; i++) {
                    data[i] = raf.readFloat();
                }

                // 关闭流的使用
                raf.close();

            } catch (Exception ex) {
                Toast.makeText(mContext, "文件读取失败", Toast.LENGTH_SHORT).show();
            }
        }
        return data;

    }

    // 实时心率计算(取6s的数据)
    private void Real_xinlv() {

        float[] realYnn = new float[3000];
        float Realmean = 0.0f;
        float[] dif = new float[3000];
        int fs = 500;
        int dif_max_index = 0, R_index = 0, RR = 0;

        int dif_min_index = 0, Mn = 0, Mnt1 = 0, wf = 0;
        float max = 0.0f, maxt1 = 0.0f;

        R_Location Real_Rlocation = new R_Location();
        RR_Analysis rAnalysis = new RR_Analysis();

        for (int i = real; i < real + 1500; i++) {
            realYnn[Rreal] = Ynn[i];
            Rreal = Rreal + 1;
        }
        real = real + 1500;
        Rreal = 0;
        // 去除基线漂移
        // realYnn = morphology.Morphology_Fep_baseLine(realYnn);

        while (true) {

            if (Ri == 0) {

                max = Real_Rlocation.compare_max_in_data(realYnn, Mn);
                Mnt1 = Mn + (int) (0.1 * fs);
                maxt1 = Real_Rlocation.compare_max_in_data(realYnn, Mnt1);
                if (max > 1.8 * maxt1) {
                    Mnt1 = Mn + (int) (0.2 * fs);
                    dif = Real_Rlocation.R_dif(realYnn, Mnt1);
                    dif_max_index = Real_Rlocation.compare_R_dif_maxdata(dif,
                            Mnt1); // 计算差分值中的最大值，并返回最大值的点的横坐标
                    dif_min_index = Real_Rlocation.compare_R_dif_mindata(dif,
                            Mnt1); // 计算差分值中的最小值，并返回最大值的点的横坐标
                } else {
                    dif = Real_Rlocation.R_dif(realYnn, Mn);
                    dif_max_index = Real_Rlocation.compare_R_dif_maxdata(dif,
                            Mn); // 计算差分值中的最大值，并返回最大值的点的横坐标
                    dif_min_index = Real_Rlocation.compare_R_dif_mindata(dif,
                            Mn); // 计算差分值中的最小值，并返回最大值的点的横坐标
                }

                R_index = Real_Rlocation.R_adr_data(realYnn, dif_min_index,
                        dif_max_index); // 计算R波的最大值点，并返回最大值点的横坐标
                R_index_data[Ri] = R_index;
                Ri++;
                Mn = Mn + 350;
            }

            if (Ri == 1) {
                Mn = R_index + (int) (0.35 * fs);
                dif = Real_Rlocation.R_dif(realYnn, Mn);
                dif_max_index = Real_Rlocation.compare_R_dif_maxdata(dif, Mn); // 计算差分值中的最大值，并返回最大值的点的横坐标
                dif_min_index = Real_Rlocation.compare_R_dif_mindata(dif, Mn); // 计算差分值中的最小值，并返回最大值的点的横坐标
                R_index = Real_Rlocation.R_adr_data(realYnn, dif_min_index,
                        dif_max_index); // 计算R波的最大值点，并返回最大值点的横坐标
                R_index_data[Ri] = R_index;
                Ri++;
                wf = fs;

            }
            if (Ri > 1) {
                RR = R_index_data[Ri - 1] - R_index_data[Ri - 2];
                wf = (wf + RR) / 2;
                dif = Real_Rlocation.R_dif(realYnn, R_index, wf);
                dif_max_index = Real_Rlocation.compare_R_dif_maxdata(dif,
                        R_index, wf);
                dif_min_index = Real_Rlocation.compare_R_dif_mindata(dif,
                        R_index, wf); // 计算差分值中的最小值，并返回最大值的点的横坐标
                R_index = Real_Rlocation.R_adr_data(realYnn, dif_min_index,
                        dif_max_index); // 计算R波的最大值点，并返回最大值点的横坐标
                if (dif_max_index == dif_min_index) {

                    break;
                } else {
                    R_index_data[Ri] = R_index;
                    Ri++;
                }
            }
        }
        // RR平均间期
        Realmean = rAnalysis.RS_RR(R_index_data, Ri);
        // Log.i("info", "实时检测R波个数为："+Ri);
        // Toast.makeText(mContext, "R波个数为："+Ri, Toast.LENGTH_LONG).show();
        // 平均心率
        realHeartRate = (int) (60 / Realmean * 1000);
        Ri = 0;
        // Message msg = new Message();
        // msg.what = Constants.MESSAGE_REALHEARTRATE;
        myHandler.sendEmptyMessage(Constants.MESSAGE_REALHEARTRATE);
        // showRealHR(realHeartRate);
    }

    // 实时呼吸率计算
    private void real_respRate() {
        // 用来计算呼吸率的数据
        float[] resp = new float[RESP_SUM];
        // 平均波峰间期，单位为ms
        float ave_top = 0;
        // 呼吸率
        // float realRespRate;
        int count = 0;
        int sum = 0;
        int[] val_extrems = new int[100];
        float ms = 2.0f;
        // 准备数据
        for (int i = resp_real; i < resp_real + RESP_SUM; i++) {
            resp[resp_Rreal] = respYnn[i];
            resp_Rreal++;
        }
        resp_real = resp_real + RESP_SUM;
        resp_Rreal = 0;
        // 第一次平滑滤波
        resp = smoothFilter.smoothFilter(resp, 15);
        // 第二次平滑滤波
        resp = smoothFilter.smoothFilter(resp, 35);
        // 呼吸波波峰检测，返回波峰的位置
        int[] extrIndex = LocalMaximaFinder.findLocalMaxima(resp);

        for (int i = 0; i < extrIndex.length - 1; i++) {
            if (extrIndex[i + 1] - extrIndex[i] > 0) {
                val_extrems[count] = extrIndex[i + 1] - extrIndex[i];
                // 波峰的个数
                count++;

            }
        }
        Log.i("info", "波峰的个数：" + count);
        if (count != 0) {
            for (int i = 0; i < count; i++) {
                sum += val_extrems[i];
            }
            ave_top = sum / count * ms;
            realRespRate = (int) (60 / ave_top * 1000);
            // Message msg = new Message();
            // msg.what = Constants.MESSAGE_REALRESPRATE;
            myHandler.sendEmptyMessage(Constants.MESSAGE_REALRESPRATE);
        }

        // if(realRespRate != 0){
        // huxi.setText("" + realRespRate);
        // }

    }

    /**
     * 显示实时心率
     *
     * @param data 实时心率
     */
    public void showRealHR(int data) {
        int baiwei = 0;
        int shiwei = 0;
        int gewei = 0;
        baiwei = (int) (data / 100.0);
        shiwei = (int) ((data - 100 * baiwei) / 10.0);
        gewei = data - 100 * baiwei - shiwei * 10;

        int[] icon_da = {R.drawable.icon_da_0, R.drawable.icon_da_1,
                R.drawable.icon_da_2, R.drawable.icon_da_3,
                R.drawable.icon_da_4, R.drawable.icon_da_5,
                R.drawable.icon_da_6, R.drawable.icon_da_7,
                R.drawable.icon_da_8, R.drawable.icon_da_9,};
        if (baiwei == 0) {
            SSXLbaiwei.setVisibility(View.GONE);
        } else {
            SSXLbaiwei.setVisibility(View.VISIBLE);
            SSXLbaiwei.setBackgroundResource(icon_da[baiwei]);
        }

        SSXLshiwei.setBackgroundResource(icon_da[shiwei]);
        SSXLgewei.setBackgroundResource(icon_da[gewei]);

    }

    /**
     * 与flagment有关的函数
     */
    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
//		resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
        // valid scale factor is between 0.0f and 1.0f. leftmenu'width is
        // 150dip.
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        itemYonghu = new ResideMenuItem(this, R.drawable.yonghu, "用户");
        itemJilu = new ResideMenuItem(this, R.drawable.btn_rili, "记录");
        itemShezhi = new ResideMenuItem(this, R.drawable.set, "设置");
        itemHelp = new ResideMenuItem(this, R.drawable.bangzhu2, "帮助");
        itemFankui = new ResideMenuItem(this, R.drawable.fankui, "反馈");
        itemGuanyu = new ResideMenuItem(this, R.drawable.guanyu, "关于");

        itemYonghu.setOnClickListener(this);
        itemJilu.setOnClickListener(this);
		/*
		 * itemProfile.setOnClickListener(this);
		 * itemCalendar.setOnClickListener(this);
		 * itemSettings.setOnClickListener(this);
		 */

        resideMenu.addMenuItem(itemYonghu, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemJilu, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemShezhi, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemHelp, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemFankui, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemGuanyu, ResideMenu.DIRECTION_LEFT);

        // You can disable a direction by setting ->
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        // resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);

        findViewById(R.id.caidan_1).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                    }
                });

    }

    /*
     * 与flagment有关的函数
     */
    @Override
    public void onClick(View view) {
        // flag的窗口
        if (view == itemYonghu) {
            // 跳转至用户信息界面
            Intent intent = new Intent();
            intent.setClass(FirstActivity.this, UserInfoActivity.class);
            startActivity(intent);
        }

        if (view == itemJilu) {

            // 启动等待对话框
            Intent intent = new Intent(FirstActivity.this, LoadingActivity.class);
            intent.putExtra("loadMsg", "正在查询...");
            startActivity(intent);
            // 访问服务器,并将历史日期传递至记录界面
            queryDate();

        }
        resideMenu.closeMenu();

        // 接收的按钮
        if (view == startButton) {

            if (!isReading) {
                isReading = true;

                // 定时器
                task = new TimerTask() {
                    @Override
                    public void run() {

                        myHandler.sendEmptyMessage(Constants.MESSAGE_TIMER);
                    }
                };
                if (isre) {
                    isre = false;
                    if (isopen) {
                        ft232.DeviceUARTReData();
                    } else {
                        ft232.readData();
                    }
                }
                timer.schedule(task, 1000, 1000);// 运行时间和间隔都是1000ms

            } else {
                isReading = false;
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) startButton
                        .getLayoutParams();
                lp.leftMargin = 0;

                startButton.setBackgroundResource(R.drawable.btn_kaishi_1);
                startFrameLayout
                        .setBackgroundResource(R.drawable.bg_zhuangtai_1);
                startButton.setText("开始");
                startButton.setLayoutParams(lp);
                if (task != null) {
                    task.cancel();
                }
                jishixianshi.setVisibility(View.GONE);
                huadong.setVisibility(View.VISIBLE);
                fen = 4;
                miao = 60;
                FtextView.setText("05");
                MtextView.setText("00");
                ft232.stopRead();
            }

        }

        // 呼吸的按钮
        if (view == huxiButton) {

            isHuxi = true;
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) huxiButton
                    .getLayoutParams();
            lp.leftMargin = huxiFrameLayout.getWidth() - huxiButton.getWidth();
            huxiButton.setLayoutParams(lp);
            // 跳转至呼吸引导界面
            Intent intent = new Intent(FirstActivity.this, BreathGuideActivity.class);
            startActivity(intent);

        }

        // 硬件连接按钮
        if (view == lianjie) {

        }

        // 详细信息显示按钮
        if (view == xinxi) {

        }

        // Panel的按钮及TextView的设置
        if (view == imageButton[0]) {
            imageButton[0].setBackgroundResource(R.drawable.btn_lishidian_2);
            imageButton[1].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[2].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[3].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[4].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[5].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[6].setBackgroundResource(R.drawable.btn_lishidian_1);
            if (dateNum != -1 && 0 < dateNum) {
                queryTime(panelText[0]);
            }

        }
        if (view == imageButton[1]) {
            imageButton[0].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[1].setBackgroundResource(R.drawable.btn_lishidian_2);
            imageButton[2].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[3].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[4].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[5].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[6].setBackgroundResource(R.drawable.btn_lishidian_1);

            if (dateNum != -1 && 1 < dateNum) {
                queryTime(panelText[1]);
            }
        }
        if (view == imageButton[2]) {
            imageButton[0].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[1].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[2].setBackgroundResource(R.drawable.btn_lishidian_2);
            imageButton[3].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[4].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[5].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[6].setBackgroundResource(R.drawable.btn_lishidian_1);

            if (dateNum != -1 && 2 < dateNum) {
                queryTime(panelText[2]);
            }
        }
        if (view == imageButton[3]) {
            imageButton[0].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[1].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[2].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[3].setBackgroundResource(R.drawable.btn_lishidian_2);
            imageButton[4].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[5].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[6].setBackgroundResource(R.drawable.btn_lishidian_1);
            if (dateNum != -1 && 3 < dateNum) {
                queryTime(panelText[3]);
            }
        }
        if (view == imageButton[4]) {
            imageButton[0].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[1].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[2].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[3].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[4].setBackgroundResource(R.drawable.btn_lishidian_2);
            imageButton[5].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[6].setBackgroundResource(R.drawable.btn_lishidian_1);
            if (dateNum != -1 && 4 < dateNum) {
                queryTime(panelText[4]);
            }
        }
        if (view == imageButton[5]) {
            imageButton[0].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[1].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[2].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[3].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[4].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[5].setBackgroundResource(R.drawable.btn_lishidian_2);
            imageButton[6].setBackgroundResource(R.drawable.btn_lishidian_1);
            if (dateNum != -1 && 5 < dateNum) {
                queryTime(panelText[5]);
            }
        }
        if (view == imageButton[6]) {
            imageButton[0].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[1].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[2].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[3].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[4].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[5].setBackgroundResource(R.drawable.btn_lishidian_1);
            imageButton[6].setBackgroundResource(R.drawable.btn_lishidian_2);
            if (dateNum != -1 && 6 < dateNum) {
                queryTime(panelText[6]);
            }
        }
    }


    /**
     * 根据日期向服务器发送查询请求，返回该日期下所有历史检测时间
     *
     * @param date --检测日期
     */
    private void queryTime(final String date) {

        String url = "QueryTimeServlet";
        RequestParams params = new RequestParams();
        params.put("date", date);
        // 启动等待对话框
        Intent intent = new Intent(FirstActivity.this, LoadingActivity.class);
        intent.putExtra("loadMsg", "正在查询...");
        startActivity(intent);

        ClientUtil.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                if (statusCode == 200) {
                    String timeStr = new String(responseBody);
                    ArrayList<String> timeList = new ArrayList<String>();
                    if (timeStr != null && timeStr.length() <= 5) {
                        timeList.add(timeStr);
                        // 更新适配器数据
                        // Log.i("info", "时间为：" + timeStr);
                    } else {
                        String[] times;
                        times = timeStr.split(":");

                        for (int i = 0; i < times.length; i++) {
                            timeList.add(times[i]);
                            // Log.i("info", "时间为：" + times[i]);
                        }
                    }
                    Intent myIntent = new Intent(FirstActivity.this, PanelActivity.class);
                    myIntent.putExtra("datedata", date);
                    myIntent.putStringArrayListExtra("timeList", timeList);
                    startActivity(myIntent);

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                error.printStackTrace();
            }
        });
    }

    /**
     * 查询最近一周的历史检测日期，并在Panel控件中显示
     */
    private void initPanel() {
        String url = "QueryDateServlet";
        ClientUtil.post(url, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                if (statusCode == 200) {

                    String dateStr = new String(responseBody);
                    Log.i("info", "日期：" + dateStr);
                    if (dateStr.equals("登录超时，请重新登录！")
                            || dateStr.equals("没有历史记录")) {
                        dateNum = -1;
                        for (int i = 0; i < panelText.length; i++) {

                            dateView[i].setText("--");
                        }
                    }

                    // 历史检测日期只有一个(***年**月**日)
                    if (dateStr.length() == 11
                            && !dateStr.equals("登录超时，请重新登录！")) {
                        dateNum = 1;
                        dateView[0].setText(dateStr.substring(5, 7) + "."
                                + dateStr.substring(8, 10));
                        panelText[0] = dateStr;
                    }

                    // 历史检测日期超过一个
                    if (dateStr.length() > 12) {

                        dates = dateStr.split(":");
                        dateNum = dates.length;
                        if (dateNum > 7) {
                            for (int i = 0; i < 7; i++) {
                                panelText[i] = dates[dateNum - i - 1];
                                dateView[i].setText(panelText[i]
                                        .substring(5, 7)
                                        + "."
                                        + panelText[i].substring(8, 10));

                            }
                        } else {
                            for (int i = 0; i < dateNum; i++) {
                                panelText[i] = dates[dateNum - i - 1];
                                dateView[i].setText(panelText[i]
                                        .substring(5, 7)
                                        + "."
                                        + panelText[i].substring(8, 10));
                            }
                        }

                    }

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                error.printStackTrace();
            }
        });
    }


    /**
     * 与flagment有关的函数
     */
    public ResideMenu getResideMenu() {
        return resideMenu;
    }

    /**
     * 与flagment有关的函数
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    /**
     * 获取用户名，作为数据文件的命名
     */
    private void getUserName() {
        Intent intent = getIntent();
        USER_NAME = intent.getStringExtra("user_name");
        if (USER_NAME == null) {
            USER_NAME = "Ecg";
        }

    }

    /**
     * 访问服务器，查询历史检测所有日期
     */
    private void queryDate() {

        String url = "QueryDateServlet";

        ClientUtil.post(url, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                if (statusCode == 200) {

                    // 获取服务器响应--历史检测所有日期，如："2016年03月20日:2016年03月25日:..."
                    String dateStr = new String(responseBody);
                    Log.i("info", "日期：" + dateStr);

                    if (dateStr.equals("登录超时，请重新登录！")) {
                        Toast.makeText(mContext, "查询失败，请重试！", Toast.LENGTH_LONG)
                                .show();
                        // 发送广播，关闭等待框
                        Intent mIntent = new Intent();
                        mIntent.setAction("com.hrv.load.FINISH");
                        sendBroadcast(mIntent);
                        return;
                    } else {
                        // 发送广播，关闭等待框
                        Intent intent = new Intent();
                        intent.setAction("com.hrv.load.FINISH");
                        sendBroadcast(intent);
                        // 跳转至历史查询界面
                        Intent intent2 = new Intent(FirstActivity.this,
                                HistoryActivity.class);
                        intent2.putExtra("dateStr", dateStr);
                        startActivity(intent2);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                error.printStackTrace();
            }
        });
    }

    /**
     * 获取系统日期与时间
     */
    private void getDateTime() {
        Calendar c = Calendar.getInstance();
        date = c.get(Calendar.YEAR) + "年" + (c.get(Calendar.MONTH) + 1) + "月"
                + c.get(Calendar.DAY_OF_MONTH) + "日";
        int min = c.get(Calendar.MINUTE);
        if (min < 10) {
            time = c.get(Calendar.HOUR_OF_DAY) + "时" + "0"
                    + c.get(Calendar.MINUTE) + "分";
        } else {
            time = c.get(Calendar.HOUR_OF_DAY) + "时" + c.get(Calendar.MINUTE)
                    + "分";
        }
    }

    /**
     * 5分钟定时
     */
    private void dingshi() {
        miao--;
        FtextView.setText("0" + fen);
        if (miao < 0) {
            fen = fen - 1;
            miao = 59;
            FtextView.setText("0" + fen);
            MtextView.setText("" + miao);

        } else {
            if (miao < 10 && miao >= 0) {
                MtextView.setText("0" + miao);
            } else {
                MtextView.setText("" + miao);
            }
        }
        if (miao == 0 && fen == 0) {
            // 5分钟计时结束
            task.cancel();
            ft232.stopRead();

            if (EcgData.size() > 140000) {
                // 上传数据文件至服务器，并获取分析报告响应
                requestFromSever();
            }
        } else {
            if (miao % 10 == 0) {
                // 每10秒计算一次心率
//				Real_xinlv();
                realHandler.sendEmptyMessage(Constants.MESSAGE_CACULATE_HR);

            }

            if (miao % 20 == 0) {
                // 每20秒计算一次呼吸率
//				real_respRate();
                realHandler.sendEmptyMessage(Constants.MESSAGE_CACULATE_RESP);
            }
        }
    }

    // 将呼吸与心电数据存储至SD卡
    private void requestFromSever() {
        // 开启数据存储线程
        new Thread() {
            public void run() {
                // 打开等待框
                Intent intent = new Intent(FirstActivity.this, LoadingActivity.class);
                intent.putExtra("loadMsg", "数据正在处理...");
                startActivity(intent);
                // 获取时间和日期
                getDateTime();
                // 存储数据至SD卡
                writeDataToSD(Ynn, respYnn);
                // Message msg = new Message();
                // msg.what = Constants.MESSAGE_WRITE_FINISHED;
                myHandler.sendEmptyMessage(Constants.MESSAGE_WRITE_FINISHED);
            }
        }.start();
    }

    /**
     * 上传心电数据文件至服务器
     */
    private void uploadFile() {

        String url = "UploadServlet";
        RequestParams params = new RequestParams();
        File ecgFile = new File(dateDir.getAbsolutePath(), "ecg_"
                + origDataName);
        File respFile = new File(dateDir.getAbsolutePath(), "resp_"
                + origDataName);
        try {
            // 设置文件参数
            // file1为心电数据
            params.put("file1", ecgFile);
            // file2为呼吸数据
            params.put("file2", respFile);
            // 设置响应超时时间为40s
            ClientUtil.setDataTimeout();
            // 向服务器发送请求
            ClientUtil.post(url, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers,
                                      byte[] responseBody) {

                    // 得到服务器响应——responseBody字节数组，将其转换为字符串
                    // 得到的字符串为："Ecg|inferData|sandianData|pointStr"
                    // inferData ="心率：呼吸：焦虑指数：SDNN:NN50：r_MSSD:tpv:vlf:lf:hf"
                    // sandianData = "数据1：数据2：数据3：..."
                    // pointStr为3000个心电数据，用来绘制部分心电图
                    String data = new String(responseBody);

                    if (statusCode == 200) {

                        // Log.i("info", data);
                        String[] splitStr = new String[5];
                        int i = 0;
                        // 对字符串进行分割，分割标志位“|”
                        while (true) {

                            int j = data.indexOf("|");// 找分隔符的位置
                            if (j == -1) {// 没有分隔符存在
                                splitStr[i] = data;
                                break;
                            }
                            splitStr[i] = data.substring(0, j);// 找到分隔符，截取子字符串
                            i++;
                            data = data.substring(j + 1); // 剩下需要处理的字符串
                        }
                        inferData = splitStr[1];
                        // Log.i("info", inferData);
                        sandianData = splitStr[2];
                        // Log.i("info", sandianData);
                        pointStr = splitStr[3];
                        // Log.i("info", pointStr);

                        Intent intent = new Intent(FirstActivity.this, RecordDetial.class);
                        intent.putExtra("isDown", "down");
                        intent.putExtra(DATE, date);// 日期
                        intent.putExtra(TIME, time);// 时间
                        // HRV分析的相关参数
                        intent.putExtra("inferData", inferData);
                        intent.putExtra("sandianData", sandianData);
                        intent.putExtra("pointStr", pointStr);
                        // 发送广播，结束等待框
                        Intent mintent = new Intent();
                        mintent.setAction("com.hrv.load.FINISH");
                        sendBroadcast(mintent);

                        startActivity(intent);
                        // Message msg = new Message();
                        // msg.what = Constants.MESSAGE_MONITOR_END;
                        myHandler
                                .sendEmptyMessage(Constants.MESSAGE_MONITOR_END);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      byte[] responseBody, Throwable error) {
                    error.printStackTrace();
                    // Message msg = new Message();
                    // msg.what = Constants.MESSAGE_REQUEST_FALURE;
                    myHandler
                            .sendEmptyMessage(Constants.MESSAGE_REQUEST_FALURE);
                }
            });
        } catch (FileNotFoundException e) {
            Toast.makeText(mContext, "上传文件不存在", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取屏幕分辨率
     */
    public int getPixe() {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        int w = mDisplayMetrics.widthPixels;
        int h = mDisplayMetrics.heightPixels;
        return w * h;
    }

    public static int getfenbian() {

        return fenbian;

    }

    /**
     * OTG串口驱动管理类
     */
    public class FT232 {
        // 打开设备的引脚声明
        private int DevCount = -1;
        // int openIndex = -1;//设备标号
        // 读取数据相关的参数
        private int baudRate = 115200;// 波特率
        // private int readLength = 50;// 每次读取数据的长度
        private int iavailable = 0;
        // 接收相关参数
        private int NUM = 0;
        private String ecgStr_temp = null;
        private String respStr_temp = null;
        private String N1 = "c0";
        private String N2 = "00";

        private boolean bReadThreadGoing = false;
        private FirstActivity.FT232.readThread read_thread;

        // 设备连接相关函数
        public void DeviceUARTReData() {

            // Log.i(TAG, "=======DeviceUARTReData()======");
            if (DevCount <= 0) {
                createDevice();
                isopen = false;
            }
            connectFunction();
            readData();
        }

        // 设备连接相关函数
        public void createDevice() {

            // Log.i(TAG, "=======createDevice()======");
            try {
                ftdid2xx = D2xxManager.getInstance(getApplicationContext());
            } catch (D2xxManager.D2xxException ex) {
                ex.printStackTrace();
            }
            int tempDevCount = ftdid2xx
                    .createDeviceInfoList(getApplicationContext());
            if (tempDevCount > 0) {
                if (DevCount != tempDevCount) {
                    DevCount = tempDevCount;
                }
            } else {
                DevCount = -1;

                return;
            }
        }

        // 设备连接相关函数
        public void openDev() {
            // Log.i(TAG, "=======openDev()======");

            if (DevCount <= 0) {
                createDevice();
            } else {
                connectFunction();
            }
        }

        // 设备连接相关函数
        public void connectFunction() {

            // Log.i(TAG, "=======connectFunction()======");
            if (ftDev == null && DevCount > 0) {
                ftDev = ftdid2xx.openByIndex(getApplicationContext(), 0);
            }
            if (ftDev != null && ftDev.isOpen()) {
                ftDev.setBaudRate(baudRate);
            } else {
                Toast.makeText(getApplicationContext(), "未找到设备，请检查设备连接是否正常！",
                        Toast.LENGTH_LONG).show();
                return;
            }
        }

        // 断开连接相关函数
        public void disconnectFunction() {

            // Log.i(TAG, "=======disconnectFunction()======");
            DevCount = -1;

            bReadThreadGoing = false;

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (ftDev != null) {
                synchronized (ftDev) {
                    if (true == ftDev.isOpen()) {
                        ftDev.close();
                    }
                }
            }
        }

        // OTG线连接后调用
        public void notifyUSBDeviceAttach() {
            // Log.i(TAG, "=======notifyUSBDeviceAttach()======");
            createDevice();
        }

        // OTG线拔出后调用
        public void notifyUSBDeviceDetach() {
            // Log.i(TAG, "=======notifyUSBDeviceDetach()======");
            disconnectFunction();
        }

        // 开始读取数据
        public void readData() {
            // Log.i(TAG, "=======readData()======");
            if (DevCount < 0 || ftDev == null) {
                Toast.makeText(getApplicationContext(),
                        "Device not open yet...", Toast.LENGTH_SHORT).show();
            } else {
                ftDev.purge((byte) (D2xxManager.FT_PURGE_RX));
                ftDev.restartInTask();

                if (false == bReadThreadGoing) {
                    bReadThreadGoing = true;
                    read_thread = new FirstActivity.FT232.readThread();
                    read_thread.start();
                    // Log.i(TAG, "=======read_thread.start()======");
                }
            }
        }

        // 停止读取串口数据
        public void stopRead() {
            // Log.i(TAG, "=======stopRead()======");
            if (bReadThreadGoing) {
                ftDev.stopInTask();
                bReadThreadGoing = false;
            }
        }

        /**
         * 读取串口数据的线程
         */
        public class readThread extends Thread {
            // Handler mHandler;

            public readThread() {
                // mHandler = h;
                this.setPriority(Thread.MIN_PRIORITY);
            }

            @Override
            public void run() {
                while (bReadThreadGoing) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (ftDev) {
                        try {
                            // 获得串口中字节的个数
                            iavailable = ftDev.getQueueStatus();
                            // if (iavailable != 0) {
                            // Log.i(TAG, "读取到的串口字节数为：" + iavailable);
                            // }
                            byte[] readData = new byte[iavailable];
                            // 以字节为单位每次从串口中读取iavailable个字节。
                            ftDev.read(readData, iavailable);
                            // 将字节转换为十六进制字符串，并判断分离，提取心电数据
                            getHexString(readData);
                            // 向主线程中是mHandler发送消息，处理波形更新、实时心率计算与显示等
                            // Message msg = myHandler.obtainMessage();
                            myHandler.sendEmptyMessage(Constants.MESSAGE_UPDATE_CHART);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }

        // /**
        // * 主要用来处理心电波形的实时跟新、心率实时计算与显示等
        // */
        // Handler handler = new Handler() {
        // @Override
        // public void handleMessage(Message msg) {
        //
        // // // 实时心率的计算与显示
        // // shimiaojishi++;
        // // if (shimiaojishi > 50) {
        // // if (2000 * (Quzhi + 1) < EcgData.size() - 5000) {
        // // float[] data = new float[2000];
        // // int k = 0;
        // // for (int i = EcgData.size() - 2100; i < EcgData.size() - 100;
        // // i++) {
        // // data[k] = EcgData.get(i);
        // // k++;
        // // }
        // // // 实时心率计算
        // // realHeartRate(data);
        // // Quzhi++;
        // // }
        // //
        // // shimiaojishi = 0;
        // // }
        //
        // // 心电波形的实时显示
        // if (EcgData.size() > 300) {
        // for (int i = 0; i < readLength; i++) {
        // float sumYnn = 0;
        // float meanYnn = 0;
        //
        // for (int j = l - 100; j < l; j++) {
        // sumYnn = sumYnn + EcgData.get(j);
        // }
        // meanYnn = (float) ((sumYnn / 100.0));
        //
        // if (l < EcgData.size()) {
        // Ynn[k] = EcgData.get(l - 50);
        // // respYnn[k] = respData.get(l - 50);
        // // ecg_num++;
        // series.add(k, (Ynn[k] - meanYnn));
        // l++;
        // k++;
        // } else {
        // return;
        // }
        //
        // if (k == m * 1500) { // 屏幕自动向右滑动
        // mXYRenderer.setXAxisMin(X * m);
        // mXYRenderer.setXAxisMax(X * (m + 1));
        // m++;
        // }
        // }
        // }
        //
        // // if (EcgData.size() > 320000) {
        // // stopRead();
        // // Toast.makeText(getApplicationContext(), "结束",
        // // Toast.LENGTH_LONG).show();
        // //
        // // } else {
        //
        // mViewChart.invalidate();// 视图更新
        //
        // // }
        //
        // }
        // };

        /**
         * 将字节数组中的字节装换为十六进制字符
         *
         * @param b 从串口读取到的字节
         * @throws Exception
         */
        public void getHexString(byte[] b) throws Exception {
            String[] dataStr = new String[b.length];
            for (int i = 0; i < b.length; i++) {
                dataStr[i] = Integer.toString((b[i] & 0xff) + 0x100, 16)
                        .substring(1);
                // deviceDrop(result[i]);
                parseData(dataStr[i]);
            }
        }

        /**
         * 判断分离:找到帧头为"C0 00 00"的数据帧(9个字节，其中前三个字节为帧头，中间三个字节为呼吸数据，最后3个为心电数据)
         * 提取最后三个字节即为心电数据
         *
         * @param r 十六进制的字符数据
         */
        public void parseData(String r) {

            if (r.equals(N1) && NUM == 0) {
                NUM = 1;
                return;
            }
            if (NUM == 1) {
                if (r.equals(N2)) {
                    NUM = 2;

                } else {
                    NUM = 0;
                }
                return;
            }

            if (NUM == 2) {
                if (r.equals(N2)) {
                    NUM = 3;

                } else {
                    NUM = 0;
                }
                return;
            }
            if (NUM == 3) {
                respStr_temp = r;
                NUM = 4;
                return;
            }
            if (NUM == 4) {
                respStr_temp += r;
                NUM = 5;
                return;
            }
            if (NUM == 5) {
                respStr_temp += r;
                // Log.i("info", "呼吸："+respStr_temp);
                // 存储呼吸数据至respData集合中
                respData.add((float) Integer.parseInt(respStr_temp, 16));
                respYnn[resp_num] = (float) Integer.parseInt(respStr_temp, 16);
                resp_num++;

                NUM = 6;
                return;
            }
            if (NUM == 6) {
                ecgStr_temp = r;
                NUM = 7;
                return;
            }
            if (NUM == 7) {
                ecgStr_temp += r;
                NUM = 8;
                return;
            }
            if (NUM == 8) {
                ecgStr_temp += r;
                // Log.i(TAG, "心电数据：" + ecgStr_temp);
                // 将心电数据FIR低通滤波后，存储至EcgData集合中
                EcgData.add(ecgFilter.filterInt(ecgFilterPtr,
                        Integer.parseInt(ecgStr_temp, 16)));
                NUM = 0;

            }

        }

    }

    Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {

            super.handleMessage(msg);

            switch (msg.what) {

                // 心电波形更新
                case Constants.MESSAGE_UPDATE_CHART:

                    updateChart();
                    break;

                // 存储数据至SD卡结束
                case Constants.MESSAGE_WRITE_FINISHED:
                    // writeFinished = true;
                    uploadFile();
                    break;

                // 上传数据文件至服务器
                case Constants.MESSAGE_UPLOAD_FILE:
                    // if(writeFinished){
                    // uploadFile();
                    // }else{
                    // Toast.makeText(mContext, "数据未准备好，上传失败！",
                    // Toast.LENGTH_SHORT).show();
                    // }
                    break;

                // 定时器
                case Constants.MESSAGE_TIMER:
                    dingshi();
                    break;

                // 获取服务器响应失败
                case Constants.MESSAGE_REQUEST_FALURE:
                    Intent mintent = new Intent();
                    mintent.setAction("com.hrv.load.FINISH");
                    sendBroadcast(mintent);
                    Toast.makeText(mContext, "获取服务器响应超时！", Toast.LENGTH_LONG)
                            .show();
                    break;

                // 成功获取分析报告，监测结束
                case Constants.MESSAGE_MONITOR_END:

                    break;

                // 计算实时心率
                case Constants.MESSAGE_REALHEARTRATE:
                    showRealHR(realHeartRate);
                    break;

                // 计算实时呼吸率
                case Constants.MESSAGE_REALRESPRATE:
                    huxi.setText("" + realRespRate);
                    break;
                default:
                    break;
            }
        }
    };

    // 心电波形的实时显示
    public void updateChart() {

        if (EcgData.size() > 300) {
            for (int i = 0; i < 50; i++) {
                float sumYnn = 0;
                float meanYnn = 0;

                for (int j = l - 100; j < l; j++) {
                    sumYnn = sumYnn + EcgData.get(j);
                }
                meanYnn = (float) (sumYnn / 100.0);

                if (l < EcgData.size()) {
                    Ynn[k] = EcgData.get(l - 50);
                    // respYnn[k] = respData.get(l - 50);
                    // ecg_num++;
                    series.add(k, (Ynn[k] - meanYnn));
                    l++;
                    k++;
                } else {
                    return;
                }

                if (k == m * 1500) { // 屏幕自动向右滑动
                    mXYRenderer.setXAxisMin(X * m);
                    mXYRenderer.setXAxisMax(X * (m + 1));
                    m++;
                }
            }
        }
        // 图表更新
        mViewChart.invalidate();
    }

    /**
     * 呼吸按钮复位
     */
    private void uiInit() {

        if (isHuxi) {
            LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) huxiButton
                    .getLayoutParams();
            lp2.leftMargin = 0;
            huxiButton.setLayoutParams(lp2);
        }
    }

    // /**
    // * 5分钟测试结束后调用，变量清零，初始化
    // */
    // private void hasFinished() {
    //
    // // 开始按钮复位
    // LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) startButton
    // .getLayoutParams();
    // lp.leftMargin = 0;
    // startButton.setBackgroundResource(R.drawable.btn_kaishi_1);
    // startFrameLayout.setBackgroundResource(R.drawable.bg_zhuangtai_1);
    // startButton.setText("开始");
    // startButton.setLayoutParams(lp);
    // jishixianshi.setVisibility(View.GONE);
    // huadong.setVisibility(View.VISIBLE);
    //
    // // 变量初始化
    // EcgData.clear();
    // series.clear();
    // mViewChart.invalidate();// 视图更新
    // isReading = false;
    // isopen = true;
    // isre = true;
    // fen = 4;
    // miao = 60;
    // inferData = null;
    // sandianData = null;
    // pointStr = null;
    //
    // ft232.bReadThreadGoing = false;
    // ft232.DevCount = -1;
    // ft232.NUM = 0;
    // // ft232.k = 0;
    // // ft232.m = 1;
    // // ft232.l = 200;
    // ft232.ecgStr_temp = null;
    // ft232.respStr_temp = null;
    //
    // }


}
