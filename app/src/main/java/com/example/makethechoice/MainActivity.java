package com.example.makethechoice;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bravin.btoast.BToast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {

    private Button confirmButton;
    private Button nextButton;
    private Button switchButton;
    private TextView numCurrentText;
    private TextView numTotalText;
    private TextView questionText;
    private TextView typeText;
    private TextView rightChoiceText;
    private ListView choicesListView;
    private List<String> fileNameList = new ArrayList<>();
    private List<HashMap<String, String>> questionBank = new ArrayList<>();
    private List<ChoiceItem> choiceItemList = new ArrayList<>();
    private boolean isMultiple;
    private int lastChosen = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewByIds();
        showQuestionBankDialog();
        generateQuestionBank("广东试题（L1）导入.xls");
        initChoiceItems();
    }

    private void findViewByIds() {
        confirmButton = findViewById(R.id.head_confirm);
        confirmButton.setOnClickListener(this);
        nextButton = findViewById(R.id.head_next);
        nextButton.setOnClickListener(this);
        switchButton = findViewById(R.id.question_switch);
        switchButton.setOnClickListener(this);
        numCurrentText = findViewById(R.id.num_current);
        numTotalText = findViewById(R.id.num_total);
        typeText = findViewById(R.id.type_s_m);
        choicesListView = findViewById(R.id.main_choices);
        questionText = findViewById(R.id.main_questionText);
        rightChoiceText = findViewById(R.id.right_choices);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_confirm:
                confirmButton.setVisibility(View.INVISIBLE);
                judgeAnswer();
                break;
            case R.id.head_next:
                nextButton.setVisibility(View.INVISIBLE);
                initChoiceItems();
                break;
            case R.id.question_switch:
                showQuestionBankDialog();
                break;
        }
    }

    private void readQuestionBankFiles() {
        fileNameList = new ArrayList<>();
        String dirPath = getApplicationContext().getFilesDir().getPath();
        File file = new File(dirPath);
        File[] files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (!files[i].isDirectory()) {
                    String tempFileName = files[i].getName();
                    if (files[i].getName().contains("xls")) {
                        fileNameList.add(tempFileName);
                    }
                }
            }
        }
    }

    // 显示题库选择框
    private void showQuestionBankDialog() {
        readQuestionBankFiles();
        final Dialog qbDialog = new Dialog(MainActivity.this);
        qbDialog.setCancelable(false);
        qbDialog.setCanceledOnTouchOutside(false);
        Window window = qbDialog.getWindow();
        View view = View.inflate(MainActivity.this, R.layout.dialog_questionbank, null);
        ListView fileList = view.findViewById(R.id.dialog_list);
        final String fileNameListArray[] = new String[fileNameList.size()];
        fileNameList.toArray(fileNameListArray);
        ArrayAdapter<String> fileNameListAdapter = new ArrayAdapter<>(this,
                R.layout.dialog_listviewitem, fileNameListArray);
        fileList.setAdapter(fileNameListAdapter);
        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("name", fileNameListArray[position]);
                generateQuestionBank(fileNameListArray[position]);
                initChoiceItems();
                qbDialog.dismiss();
            }
        });
        Button cancelButton = view.findViewById(R.id.dialog_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qbDialog.dismiss();
            }
        });
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setContentView(view);
        qbDialog.show();
    }

    // /data/data/com.example.makethechoice/files/L1试题1.xls
    // 生成题库列表
    private void generateQuestionBank(String fileName) {
        String filePath = getApplicationContext().getFilesDir().getPath();
        filePath += "/" + fileName;
        questionBank = new ArrayList<>();
        InputStream inputStream = null;
        Workbook workbook = null;
        try {
            inputStream = new FileInputStream(filePath);
            workbook = Workbook.getWorkbook(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Sheet sheet = workbook.getSheet(0);
        for (int row = 2; row < sheet.getRows(); row++) {
            HashMap<String, String> tempRow = new HashMap<>();
            if (sheet.getCell(2, row).getContents().equals("")) {
                continue;
            }
            tempRow.put("qType", sheet.getCell(1, row).getContents());
            tempRow.put("qText", sheet.getCell(2, row).getContents());
            for (int col = 4; col < 9; col++) {
                if (!sheet.getCell(col, row).getContents().equals("")) {
                    String qLabel = "qChoice" + ((char) (65+col-4));
                    tempRow.put(qLabel, sheet.getCell(col, row).getContents());
                }
                else {
                    break;
                }
            }
            tempRow.put("qRight", sheet.getCell(3, row).getContents());
            questionBank.add(tempRow);
        }
        if (workbook != null) {
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 生成选项列表
    private void initChoiceItems() {
        choiceItemList = new ArrayList<>();
        int qNum = (int) (Math.random() * questionBank.size());
        HashMap<String, String> qCurrent = questionBank.get(qNum);
        for (int qLabelInt = 65; qLabelInt < 70; qLabelInt++) {
            String qLetter = String.valueOf((char) (qLabelInt));
            String qLabel = "qChoice" + qLetter;
            if (!(qCurrent.get(qLabel) == null)) {
                choiceItemList.add(new ChoiceItem(qLetter+"."+qCurrent.get(qLabel), false, false));
                if (qCurrent.get("qRight").contains(qLetter)) {
                    choiceItemList.get(choiceItemList.size()-1).setRight(true);
                }
            }
            else {
                break;
            }
        }
        isMultiple = !qCurrent.get("qType").equals("单选");
        typeText.setText(!isMultiple?getResources().getText(R.string.typeSingle):
                getResources().getText(R.string.typeMultiple));
        questionText.setText(qCurrent.get("qText"));
        // 刷新题目序号
        numCurrentText.setText(String.valueOf(qNum));
        numTotalText.setText("/"+questionBank.size());
        // 刷新选项列表
        refreshView();
        // 生成正确答案，但是在点击确定前需要隐藏
        rightChoiceText.setText(qCurrent.get("qRight"));
        rightChoiceText.setVisibility(View.INVISIBLE);
    }

    // 根据选项属性显示列表
    private void refreshView() {
        // 根据是否已经选择答案来判断是否显示"确定"按钮，未选择答案时按钮不显示
        boolean isConfirmable = false;
        for (int item = 0; item < choiceItemList.size(); item++) {
            if (choiceItemList.get(item).getChoosen()) {
                isConfirmable = true;
            }
        }
        // 一旦有选项被选择，"确定"按钮就会显现
        confirmButton.setVisibility(isConfirmable?View.VISIBLE:View.INVISIBLE);
        // 刷新选项列表
        final choicesListViewAdapter choicesAdapter = new choicesListViewAdapter(
                getApplicationContext(), R.layout.choiceitem, choiceItemList, onClickListener
        );
        choicesListView.setAdapter(choicesAdapter);
        Utility.setListViewHeightBasedOnChildren(choicesListView);
    }

    // 列表中的选项对应的点击事件
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(@NotNull View v) {
            int position = v.getTag().toString().charAt(0) - 'A';
            choiceItemList.get(position).setChoosen(!choiceItemList.get(position).getChoosen());
            if (!isMultiple && lastChosen != position) {
                choiceItemList.get(lastChosen).setChoosen(false);
                lastChosen = position;
            }
            refreshView();
        }
    };

    // 判断答案正确与否，并给正确/错误选项打上标记，同时显示正确答案
    private void judgeAnswer() {
        lastChosen = 0;
        boolean isAnswerRight = true;
        for (int item = 0; item < choiceItemList.size(); item++) {
            if (choiceItemList.get(item).getRight()) {
                choiceItemList.get(item).setShowingRight(true);
                if (!choiceItemList.get(item).getChoosen()) {
                    isAnswerRight = false;
                }
            }
            else if (choiceItemList.get(item).getChoosen()) {
                if (choiceItemList.get(item).getChoosen()) {
                    isAnswerRight = false;
                    choiceItemList.get(item).setShowingWrong(true);
                }
            }
        }
        refreshView();
        rightChoiceText.setVisibility(View.VISIBLE);
        // 回答正确，间隔一段时间后自动跳转到下一题
        if (isAnswerRight) {
            BToast.success(getApplicationContext()).animate(true).text("答案正确！自动跳转下一题...").show();
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(1000);
                        Message message = new Message();
                        message.what = 1;
                        switchHandler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        // 回答错误，显示"下一题"按钮，需要手动跳转，同时需要显示正确答案
        else {
            BToast.error(getApplicationContext()).animate(true).text("答案错误！").show();
            wrongAnswerSwithcer();
        }
    }

    Handler switchHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                initChoiceItems();
            }
        }
    };

    // 答案错误，显示"下一题"按钮，同时提示正确答案
    private void wrongAnswerSwithcer() {
        nextButton.setVisibility(View.VISIBLE);
    }

}
