package com.example.puzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.puzzle.utils.RankDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class RankingActivity extends AppCompatActivity {

    private RankDatabaseHelper dbHelper;
    private ListView listViewRankings;
    private List<RankModel> rankingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        // 初始化数据库帮助类
        dbHelper = new RankDatabaseHelper(this);

        // 获取ListView和初始化列表数据
        listViewRankings = findViewById(R.id.listViewRankings);
        rankingsList = new ArrayList<>();

        // 查询数据库获取排名信息
        loadRankingsFromDB();

        // 创建并设置适配器
        RankingsAdapter adapter = new RankingsAdapter(this, rankingsList);
        listViewRankings.setAdapter(adapter);
    }

    private void loadRankingsFromDB() {
        // 查询数据库
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + RankDatabaseHelper.TABLE_NAME + " ORDER BY " + RankDatabaseHelper.COL_RANK + " ASC", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int rank = cursor.getInt(cursor.getColumnIndex(RankDatabaseHelper.COL_RANK));
                @SuppressLint("Range") String playerName = cursor.getString(cursor.getColumnIndex(RankDatabaseHelper.COL_PLAYER_NAME));
                @SuppressLint("Range") int timeTaken = cursor.getInt(cursor.getColumnIndex(RankDatabaseHelper.COL_TIME_TAKEN));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(RankDatabaseHelper.COL_DATE));

                rankingsList.add(new RankModel(rank, playerName, timeTaken, date));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    // 简单的RankModel类用于存放数据
    private static class RankModel {
        int rank;
        String playerName;
        int timeTaken;
        String date;

        RankModel(int rank, String playerName, int timeTaken, String date) {
            this.rank = rank;
            this.playerName = playerName;
            this.timeTaken = timeTaken;
            this.date = date;
        }
    }

    // RankingsAdapter类用于将RankModel数据绑定到ListView
    private class RankingsAdapter extends ArrayAdapter<RankModel> {
        private Context context;
        private List<RankModel> data;

        public RankingsAdapter(Context context, List<RankModel> data) {
            super(context, R.layout.row_ranking, data);
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.row_ranking, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tvRank = convertView.findViewById(R.id.tv_rank);
                viewHolder.tvName = convertView.findViewById(R.id.tv_name);
                viewHolder.tvDate = convertView.findViewById(R.id.tv_date);
                viewHolder.tvTakenTime = convertView.findViewById(R.id.tv_takenTime);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            RankModel item = data.get(position);
            viewHolder.tvRank.setText(String.valueOf(item.rank));
            viewHolder.tvName.setText(item.playerName);
            viewHolder.tvDate.setText(item.date);
            // 将时间转换为"X秒"格式显示
            viewHolder.tvTakenTime.setText(item.timeTaken + "秒");

            return convertView;
        }


        private class ViewHolder {
            TextView tvRank;
            TextView tvName;
            TextView tvDate;
            TextView tvTakenTime;
        }
    }
}