package com.example.puzzle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.puzzle.utils.RankDatabaseHelper;

public class MainActivity extends AppCompatActivity {
    ImageButton sp00,sp01,sp02,sp10,sp11,sp12,sp20,sp21,sp22;
    Button restartBtn;
    TextView timeTv;
    ImageButton help,rank;

    private RankDatabaseHelper dbHelper;

//    每行和每列的图片个数
    private int imageX = 3;
    private int imageY = 3;
//    图片总个数
    private int imageCount = imageX*imageY;
//    空白区域的位置
    private int blankSwap = imageCount - 1;
//    初始化空白区域按钮id
    private int blankImageId = R.id.pt_id_02x02;


//    定义计数时间的变量
    int time = 0;
//    存放碎片的数组，便于进行统一管理
    private int[]image = {
    R.mipmap.split00x00,R.mipmap.split00x01,R.mipmap.split00x02,
    R.mipmap.split01x00,R.mipmap.split01x01,R.mipmap.split01x02,
    R.mipmap.split02x00,R.mipmap.split02x01,R.mipmap.split02x02};
//    声明一个图片数组的下标数组，随机排列这个数组
    private int[]imageIndex = new int[image.length];


//    使用handler消息发送机制改变时间
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                time++;
                timeTv.setText("时间："+time+" 秒");
                handler.sendEmptyMessageDelayed(1,1000);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化数据库帮助类
        dbHelper = new RankDatabaseHelper(this);
        initView();
//        打乱拼图的函数
        disruptRandom();
        handler.sendEmptyMessageDelayed(1,1000);

        LinearLayout background = findViewById(R.id.activityBackground);
        ImageView imageView1 = findViewById(R.id.first_image);
        ImageView imageView2 = findViewById(R.id.second_image);
        ImageView imageView3 = findViewById(R.id.third_image);

        // 创建一个GradientDrawable对象，并设置渐变色
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] { Color.parseColor("#eae9f4"), Color.parseColor("#4c3428") }
        );
        background.setBackground(gradientDrawable);

        // 创建一个GradientDrawable对象，并设置渐变色
        GradientDrawable gradientDrawable2 = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] { Color.parseColor("#eae9f4"), Color.parseColor("#403054") }
        );

        // 创建一个GradientDrawable对象，并设置渐变色
        GradientDrawable gradientDrawable3 = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] { Color.parseColor("#eae9f4"), Color.parseColor("#6aa650") }
        );
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image[0] = R.mipmap.split00x00;
                image[1] = R.mipmap.split00x01;
                image[2] = R.mipmap.split00x02;
                image[3] = R.mipmap.split01x00;
                image[4] = R.mipmap.split01x01;
                image[5] = R.mipmap.split01x02;
                image[6] = R.mipmap.split02x00;
                image[7] = R.mipmap.split02x01;
                image[8] = R.mipmap.split02x02;
                restart(imageView1);
                imageView2.setAlpha(0.2f);
                imageView3.setAlpha(0.2f);
                imageView1.setAlpha(1f);

                // 设置GradientDrawable为背景的背景
                background.setBackground(gradientDrawable);
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image[0] = R.mipmap.two00x00;
                image[1] = R.mipmap.two00x01;
                image[2] = R.mipmap.two00x02;
                image[3] = R.mipmap.two01x00;
                image[4] = R.mipmap.two01x01;
                image[5] = R.mipmap.two01x02;
                image[6] = R.mipmap.two02x00;
                image[7] = R.mipmap.two02x01;
                image[8] = R.mipmap.two02x02;
                restart(imageView2);
                imageView1.setAlpha(0.2f);
                imageView3.setAlpha(0.2f);
                imageView2.setAlpha(1f);

                background.setBackground(gradientDrawable2);
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image[0] = R.mipmap.three00x00;
                image[1] = R.mipmap.three00x01;
                image[2] = R.mipmap.three00x02;
                image[3] = R.mipmap.three01x00;
                image[4] = R.mipmap.three01x01;
                image[5] = R.mipmap.three01x02;
                image[6] = R.mipmap.three02x00;
                image[7] = R.mipmap.three02x01;
                image[8] = R.mipmap.three02x02;
                restart(imageView3);
                imageView1.setAlpha(0.2f);
                imageView2.setAlpha(0.2f);
                imageView3.setAlpha(1f);

                background.setBackground(gradientDrawable3);
            }
        });
    }

//    随机打乱数组中的元素
    private void disruptRandom() {
        for(int i=0; i<imageIndex.length; i++){
            imageIndex[i] = i;
        }
//     规定二十次，随机选择两个角标对应的值进行交换
        int rand1,rand2;
        for (int j=0; j<20; j++){
            rand1 = (int) (Math.random()*(imageIndex.length-1));
            do {
                rand2 = (int) (Math.random()*(imageIndex.length-1));
                if (rand1!=rand2){
                    break;
                }
            }while (true);
            swap(rand1,rand2);
        }
//        随机排列到指定的控件上
        sp00.setImageResource(image[imageIndex[0]]);
        sp01.setImageResource(image[imageIndex[1]]);
        sp02.setImageResource(image[imageIndex[2]]);
        sp10.setImageResource(image[imageIndex[3]]);
        sp11.setImageResource(image[imageIndex[4]]);
        sp12.setImageResource(image[imageIndex[5]]);
        sp20.setImageResource(image[imageIndex[6]]);
        sp21.setImageResource(image[imageIndex[7]]);
        sp22.setImageResource(image[imageIndex[8]]);
    }

    private void swap(int rand1, int rand2) {
        int temp = imageIndex[rand1];
        imageIndex[rand1] = imageIndex[rand2];
        imageIndex[rand2] = temp;
    }

    private void initView() {
        sp00 = findViewById(R.id.pt_id_00x00);
        sp01 = findViewById(R.id.pt_id_00x01);
        sp02 = findViewById(R.id.pt_id_00x02);
        sp10 = findViewById(R.id.pt_id_01x00);
        sp11 = findViewById(R.id.pt_id_01x01);
        sp12 = findViewById(R.id.pt_id_01x02);
        sp20 = findViewById(R.id.pt_id_02x00);
        sp21 = findViewById(R.id.pt_id_02x01);
        sp22 = findViewById(R.id.pt_id_02x02);
        timeTv = findViewById(R.id.pt_tv_time);
        restartBtn = findViewById(R.id.pt_btn_restart);
        help = findViewById(R.id.help);
        rank = findViewById(R.id.rank);
    }

    public void onClick(View view) {
        int id = view.getId();
//        按钮逻辑：九个按钮执行逻辑相同，如果周围有空格，就交换和空格的位置，否则点击事件不响应
        switch (id) {
            case R.id.pt_id_00x00:
            move(R.id.pt_id_00x00,0);
                break;
            case R.id.pt_id_00x01:
                move(R.id.pt_id_00x01,1);
                break;
            case R.id.pt_id_00x02:
                move(R.id.pt_id_00x02,2);
                break;
            case R.id.pt_id_01x00:
                move(R.id.pt_id_01x00,3);
                break;
            case R.id.pt_id_01x01:
                move(R.id.pt_id_01x01,4);
                break;
            case R.id.pt_id_01x02:
                move(R.id.pt_id_01x02,5);
                break;
            case R.id.pt_id_02x00:
                move(R.id.pt_id_02x00,6);
                break;
            case R.id.pt_id_02x01:
                move(R.id.pt_id_02x01,7);
                break;
            case R.id.pt_id_02x02:
                move(R.id.pt_id_02x02,8);
                break;
        }
    }

//    将图片与空白位置交换
    private void move(int imageButtonId, int site) {
//      判断选中图片在第几行第几列
        int sitex = site/imageX;
        int siteY = site%imageY;
//        获取空白区域的坐标
        int blankx = blankSwap/imageX;
        int blanky = blankSwap%imageY;
//        可以移动的条件有两个：
//        1.在同一行，列数相减绝对值为1
//        2.在同一列，行数相减绝对值为1
        int x = Math.abs(sitex-blankx);
        int y = Math.abs(siteY-blanky);
        if((x==0&&y==1)||(x==1&&y==0)){
            ImageButton clickButton = findViewById(imageButtonId);
            clickButton.setVisibility(View.INVISIBLE);
//            找到空白区域的按钮
            ImageButton blankButton = findViewById(blankImageId);
//            将空白区域按钮设置为图片
            blankButton.setImageResource(image[imageIndex[site]]);
//            移动之前是不可见的，移动之后将控件设置为可见
            blankButton.setVisibility(View.VISIBLE);
//            将改变角标的过程记录在储存图片位置的数组中
            swap(site,blankSwap);
//            新的空白区域位置更新
            blankSwap = site;
            blankImageId = imageButtonId;
        }

//       判断本次移动完成后，是否完成了拼图游戏
        judgeGameOver();

    }

    private void judgeGameOver() {
        boolean loop = true;//定义标志位
        for(int i = 0; i < imageIndex.length; i++){
            if (imageIndex[i]!=i) {
                loop = false;
                break;
            }
        }
        if (loop){
//            拼图成功
//            停止计时
            handler.removeMessages(1);
//            拼图成功后，禁止玩家继续移动按钮
            sp00.setClickable(false);
            sp01.setClickable(false);
            sp02.setClickable(false);
            sp10.setClickable(false);
            sp11.setClickable(false);
            sp12.setClickable(false);
            sp20.setClickable(false);
            sp21.setClickable(false);
            sp22.setClickable(false);
            sp22.setImageResource(image[8]);
            sp22.setVisibility(View.VISIBLE);
// 弹出对话框并使用特定的XML布局
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
// 加载自定义布局
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_custom, null);
            TextView tvTimeUsed = dialogView.findViewById(R.id.tv_time_used);
            tvTimeUsed.setText(time + "秒"); // 假设time是字符串类型的秒数
// 设置自定义视图
            builder.setView(dialogView)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 从dialogView中查找EditText
                            EditText editText = dialogView.findViewById(R.id.et_player_name);
                            // 确保EditText不为空且获取到了文本
                            if (editText != null) {
                                String playerName = editText.getText().toString().trim();
                                // 处理确认按钮点击事件，只有当输入非空时才添加记录
                                if (!playerName.isEmpty()) {
                                    dbHelper.addNewRecord(playerName, time);
                                }
                            }
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void restart(View view) {
//        先还原，再打乱
        restore();

//        将拼图重新打乱
        disruptRandom();

        handler.removeMessages(1);
//        将时间重置为0，并重新计时
        time = 0;
        timeTv.setText("时间："+time+" 秒");
        handler.sendEmptyMessageDelayed(1,1000);

    }

    private void restore() {
        sp00.setClickable(true);
        sp01.setClickable(true);
        sp02.setClickable(true);
        sp10.setClickable(true);
        sp11.setClickable(true);
        sp12.setClickable(true);
        sp20.setClickable(true);
        sp21.setClickable(true);
        sp22.setClickable(true);
//        还原被点击按钮变成初始化模样
        ImageButton clickBtn = findViewById(blankImageId);
        clickBtn.setVisibility(View.VISIBLE);
//        隐藏第九张图片
        ImageButton blankBtn = findViewById(R.id.pt_id_02x02);
        blankBtn.setVisibility(View.INVISIBLE);
        blankImageId = R.id.pt_id_02x02;//初始化空白区域按钮id
        blankSwap = imageCount - 1;
    }

    public void help_onclick(View view) {


// 加载自定义的布局作为PopupWindow的内容
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.popup_window_layout, null);

// 创建PopupWindow实例
        PopupWindow popupWindow = new PopupWindow(contentView, 750, 1500, true);

        String hexColor = "#eae9f4";
// 使用Color.parseColor()将十六进制颜色字符串转换为颜色的整数表示
        int color = Color.parseColor(hexColor);
// 用这个颜色创建一个ColorDrawable并设置为PopupWindow的背景
        popupWindow.setBackgroundDrawable(new ColorDrawable(color));

// 获取并设置确认按钮的点击事件
        Button confirmButton = contentView.findViewById(R.id.btn_confirm);
        confirmButton.setOnClickListener(v -> {
            // 点击确认按钮时关闭PopupWindow
            popupWindow.dismiss();
        });

// 显示PopupWindow，以屏幕中心为基准点
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }

    public void rank_onclick(View view) {
        Intent intent = new Intent(MainActivity.this, RankingActivity.class);
        startActivity(intent);
    }
}