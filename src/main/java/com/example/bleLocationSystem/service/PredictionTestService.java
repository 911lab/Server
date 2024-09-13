package com.example.bleLocationSystem.service;

import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.stream.LogOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PredictionTestService {
    // Python 스크립트를 실행하고 결과를 반환하는 메서드
    public String runPythonPrediction(ArrayList<Integer> rssiData) {
        System.out.println(rssiData);
        try {
            // RSSI 값을 공백으로 구분하여 Python 스크립트에 인자로 전달
            StringBuilder rssiValues = new StringBuilder();
            for (int rssi : rssiData) {
                System.out.println(rssi);
                rssiValues.append(rssi).append(" ");
            }
            String path = System.getProperty("user.dir");
            System.out.println("현재 작업 경로: " + path);
            System.out.println(rssiValues);
            // Python 경로
            String pythonScriptPath = ".\\predict_distance.py";

            // Python 명령어
            String command = String.format("python %s %s", pythonScriptPath, rssiValues.toString().trim());
            System.out.println(command);
            // ProcessExecutor를 사용해 Python 스크립트 실행
            ProcessResult result = new ProcessExecutor()
                    .commandSplit(command)
                    .readOutput(true)                // 표준 출력 읽기
                    .redirectErrorStream(true)       // 표준 에러를 표준 출력에 포함
                    .timeout(60, TimeUnit.SECONDS)   // 타임아웃 설정
                    .execute();                     // 실행
//                    .outputUTF8();
            // 프로세스 종료 코드 확인 (0이 정상 종료)
            int exitCode = result.getExitValue();
            String output = result.outputUTF8();   // 실행 결과 출력 (표준 출력과 표준 에러)

            // 스크립트 정상 실행 여부 확인
            if (exitCode == 0) {
                System.out.println("Python script executed successfully.");
            } else {
                System.out.println("Python script failed with exit code: " + exitCode);
            }

            // 스크립트 실행 중 출력된 내용 (표준 출력 + 표준 에러)
            System.out.println("Script output:\n" + output);
            System.out.println("cnnlstm result:  " + result);
//            return result.trim();  // 결과 반환
            return output;  // 결과 반환


        } catch (Exception e) {
            e.printStackTrace();
            return "Error during Python script execution";
        }
    }

}
